package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.util.VariableResolver
import com.kamelia.sprinkler.util.interpolate
import com.kamelia.sprinkler.util.unsafeCast
import com.zwendo.restrikt.annotation.PackagePrivate
import java.util.*
import org.intellij.lang.annotations.Language

@PackagePrivate
internal class OptionProcessor(
    private val data: TranslatorData,
) {

    fun translate(
        key: String,
        options: Map<TranslationOption, Any>,
        locale: Locale,
    ): String? {
        // first, get the translations for the given locale, or return null if they don't exist
        val translations = data.translations[locale] ?: return null

        val config = data.optionConfiguration

        // build the actual key with the options
        val actualKey = buildKey(key, options, locale, config)

        // get the value for the actual key or return null if it doesn't exist
        val value = translations[actualKey] ?: return null

        return interpolate(value, locale, options, config)
    }

    private fun buildKey(key: String, options: Map<String, Any>, locale: Locale, config: OptionConfiguration): String {
        if (options.isEmpty()) return key

        val context = options.safeType<String>(Options.CONTEXT)
        val count = options.safeType<Int>(Options.COUNT)?.let { count ->
            options.safeType<(Locale, Int) -> Options.Plurals>(Options.COUNT_MAPPER)?.let {
                it(locale, count)
            } ?: config.pluralMapper(locale, count).representation
        }

        return when {
            count == null && context == null -> key
            count == null -> "${key}_$context"
            context == null -> "${key}_$count"
            else -> "${key}_${context}_$count"
        }
    }

    private inline fun <reified T> Map<TranslationOption, Any>.safeType(key: String): T? {
        val value = get(key) ?: return null
        require(value is T) {
            "Expected ${T::class.simpleName}, got ${value::class.simpleName}, if you want to use a reserved name for interpolation, use the '${Options.INTERPOLATION}' option."
        }
        return value.unsafeCast()
    }

    private fun interpolate(
        value: String,
        locale: Locale,
        options: Map<TranslationOption, Any>,
        config: OptionConfiguration,
    ): String {
        if (options.isEmpty() && config.interpolationDelimiter.variableStart !in value) {
            return value
        }

        val customResolver = VariableResolver { name, delimiter ->
            val tokens = VARIABLE_REGEX.matchEntire(name) ?: TODO("Throw name $name")

            val values = if (tokens.groupValues.last().isEmpty()) {
                tokens.groupValues.subList(0, tokens.groupValues.size - 1)
            } else {
                tokens.groupValues
            }
            val variableName = values[1]

            val result = options[variableName]
                ?: throw VariableResolver.ResolutionException("unknown variable name '$name'")

            if (values.size > 2) { // there is a format
                val formatName = values[2]
                val format = config.formats[formatName] ?: TODO("Throw formatName $formatName")

                val params = if (values.size > 3) { // there are format parameters
                    values[3].split(FORMAT_PARAM_SPLIT_REGEX)
                } else {
                    emptyList()
                }

                format.format(result, locale, params)
            } else { // otherwise, just return the result
                result.toString()
            }
        }

        return value.interpolate(customResolver, config.interpolationDelimiter)
    }

}

// (\w+)((?:,\s*\w+(?:\(\w+(?:,\s*\w+)*\))?)*)

@Language("RegExp")
private val FORMAT_REGEX = """,\s*(\w+)(?:\(([\w:]+(?:,\s*[\w:]+)*)\))?"""

private val VARIABLE_REGEX = """(\w+)(?:$FORMAT_REGEX)?""".toRegex()

private val FORMAT_PARAM_SPLIT_REGEX = """,\s*""".toRegex()

fun main() {
    val translator = Translator.builder(Locale.US)
        .addMap(
            Locale.FRANCE,
            mapOf(
                "sandwich" to "Des sandwichs ? J'en ai {qty, number(minIntDigits:3)}.",
                "date" to "aujourd'hui, c'est le {date, date(short)}."
            )
        )
        .addMap(
            Locale.US,
            mapOf(
                "sandwich" to "this sandwich costs {price, currency}.",
                "date" to "today is {date, datetime}."
            )
        )
        .build()

    println(translator.t("sandwich", mapOf("qty" to 4.53), Locale.FRANCE))

//    val t = translator.t("sandwich", mapOf("price" to 4.53), Locale.FRANCE)
//    println(t)
//    val t2 = translator.t("sandwich", mapOf("price" to 4))
//    println(t)
//    println(t2)
//
//    val now = LocalDate.now()
//    val t3 = translator.t("date", mapOf("date" to now), Locale.FRANCE)
//    val t4 = translator.t("date", mapOf("date" to now))
//    println(t3)
//    println(t4)
}










