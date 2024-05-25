package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.util.VariableResolver
import com.kamelia.sprinkler.util.illegalArgument
import com.kamelia.sprinkler.util.interpolate
import com.kamelia.sprinkler.util.unsafeCast
import com.zwendo.restrikt.annotation.PackagePrivate
import java.util.Locale
import org.intellij.lang.annotations.Language

@PackagePrivate
internal object TranslationProcessor {

    fun translate(data: TranslatorData, key: String, extraArgs: Map<String, Any>, locale: Locale): String? {
        val optionArg = extraArgs[Options.OPTIONS] ?: emptyMap<String, Any>()
        require(optionArg is Map<*, *>) {
            "The '${Options.OPTIONS}' argument is reserved and must be a map, but was ${optionArg.javaClass}"
        }

        val optionMap = optionArg.unsafeCast<Map<String, Any>>()

        // first, get the translations for the given locale, or return null if they don't exist
        val translations = data.translations[locale] ?: return null

        // build the actual key with the options
        val actualKey = buildKey(key, locale, optionMap, data.configuration.pluralMapper)

        // get the value for the actual key or return null if it doesn't exist
        val value = translations[actualKey] ?: return null

        val context = ProcessingContext(data.configuration.formatters, locale, extraArgs, optionMap)
        return value.interpolate(context, data.configuration.interpolationDelimiter, customResolver)
    }


    fun buildKey(key: String, locale: Locale, optionMap: Map<String, Any>, pluralMapper: Plural.Mapper): String {
        if (optionMap.isEmpty()) return key

        val interpolationContext = optionMap.safeType<String>(Options.CONTEXT)
        val ordinal = optionMap.safeType<Boolean>(Options.ORDINAL) ?: false
        val pluralValue = optionMap.safeType<Int>(Options.COUNT)?.let { count ->
            if (ordinal) {
                pluralMapper.mapOrdinal(locale, count)
            } else {
                pluralMapper.mapPlural(locale, count)
            }.name.lowercase()
        }

        val builder = StringBuilder(key)

        if (interpolationContext != null) {
            builder.append("_")
                .append(interpolationContext)
        }

        if (pluralValue != null) {
            if (ordinal) {
                builder.append("_ordinal")
            }
            builder.append("_")
                .append(pluralValue)
        }

        return builder.toString()
    }


    private inline fun <reified T> Map<String, Any>.safeType(key: String): T? {
        val value = get(key) ?: return null
        require(value is T) { "Cannot cast $value (${value.javaClass}) to ${T::class.java}" }
        return value
    }

    /**
     * This regex is globally the same as the translation value format check regex except that this regex actually
     * captures the information in groups, whereas the other one only checks if the format is valid.
     */
    private val generalSplit: Regex

    private val paramsSplit = """(?<!\\),""".toRegex()

    private val keyValueSplit = """(?<!\\):""".toRegex()

    @PackagePrivate
    internal class ProcessingContext(
        val formatters: Map<String, VariableFormatter>,
        val locale: Locale,
        val interpolationValues: Map<String, Any>,
        val optionMap: Map<String, Any>,
    )

    init {
        // capture all the params in a single group
        // any char and ending with a non-escaped ')', no need further validation as the value has already been validated
        // on translator creation
        @Language("RegExp")
        val formatParams = """\((.+(?<!\\))\)"""

        // capture the format name
        @Language("RegExp")
        val format = """\s*,\s*($IDENTIFIER)\s*(?:$formatParams)?"""

        // capture the variable name
        generalSplit = """\s*(${Regex.escape(NESTED_KEY_CHAR.toString())}?$IDENTIFIER)(?:$format)?\s*""".toRegex()
    }

    private val customResolver = object : VariableResolver<ProcessingContext> {

        override fun resolveTo(builder: Appendable, name: String, context: ProcessingContext) {
            // '!!' is ok, because values are validated on translator creation
            val (_, variableName, formatName, formatParams) = generalSplit.matchEntire(name)!!.groupValues

            val variableValue = context.interpolationValues[variableName]
                ?: context.optionMap[variableName]
                ?: illegalArgument("variable '$variableName' not found")

            var formatPassedParams = emptyMap<String, Any>()
            var actualValue: Any = variableValue
            if (variableValue is FormattedValue) {
                actualValue = variableValue.value
                formatPassedParams = variableValue.formatParams
            }

            if (formatName.isEmpty()) { // if there is no format, just append the value
                builder.append(actualValue.toString())
                return
            }

            // otherwise, there is a format
            val paramMap = if (formatParams.isEmpty() && formatPassedParams.isEmpty()) {
                emptyMap()
            } else {
                HashMap<String, Any>(formatParams.length).apply {
                    if (formatParams.isNotEmpty()) { // there are format parameters
                        formatParams.split(paramsSplit).forEach {
                            val (k, v) = keyValueSplit.split(it, 2)
                            put(k, v)
                        }
                    }
                    putAll(formatPassedParams) // add the formats passed after to override the defaults
                }
            }
            // same as above, '!!' is ok due to pre-validation
            context.formatters[formatName]!!.format(builder, actualValue, context.locale, paramMap)
        }

        override fun resolve(name: String, context: ProcessingContext): String =
            throw AssertionError("This method should never be called")

    }

}
