package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.util.VariableResolver
import com.kamelia.sprinkler.util.interpolate
import com.kamelia.sprinkler.util.unsafeCast
import com.zwendo.restrikt.annotation.PackagePrivate
import java.util.*

@PackagePrivate
internal class OptionProcessor(
    private val data: TranslatorData
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

        return if (options.isEmpty()) {
            value
        } else { // interpolate the value with the options
            value.interpolate(VariableResolver.fromMap(options), config.interpolationDelimiter)
        }
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

}


fun main() {
    val translator = Translator.builder(Locale.ENGLISH)
        .addMap(
            Locale.FRANCE,
            mapOf(
                "child_male_one" to "voici mon fils",
                "child_male_other" to "voici mes {count} fils",
                "child_female_one" to "voici ma fille",
                "child_female_other" to "voici mes {count} filles",
                "child_zero" to "je n'ai pas d'enfant"
            )
        )
        .build()

    val t = translator.t("child", mapOf("count" to 4, "context" to "male"), Locale.FRANCE)
    println(t)
}
