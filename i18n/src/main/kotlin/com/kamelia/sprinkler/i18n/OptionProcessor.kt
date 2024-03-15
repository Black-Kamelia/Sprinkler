package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.util.VariableDelimiter
import com.kamelia.sprinkler.util.VariableResolver
import com.kamelia.sprinkler.util.illegalArgument
import com.kamelia.sprinkler.util.interpolate
import com.kamelia.sprinkler.util.unsafeCast
import com.zwendo.restrikt.annotation.PackagePrivate
import java.util.Locale
import org.intellij.lang.annotations.Language

@PackagePrivate
internal object OptionProcessor {

    fun translate(data: TranslatorData, key: String, extraArgs: Map<String, Any>, locale: Locale): String? {
        // first, get the translations for the given locale, or return null if they don't exist
        val translations = data.translations[locale] ?: return null

        val config = data.configuration
        val optionArg = extraArgs[Options.OPTIONS] ?: emptyMap<String, Any>()

        require(optionArg is Map<*, *>) {
            "The '${Options.OPTIONS}' argument is reserved and must be a map, but was ${optionArg.javaClass}"
        }
        val optionMap = optionArg.unsafeCast<Map<String, Any>>()

        // build the actual key with the options
        val actualKey = buildKey(key, optionMap, locale, config.pluralMapper)

        // get the value for the actual key or return null if it doesn't exist
        val value = translations[actualKey] ?: return null

        return interpolate(value, locale, extraArgs, optionMap, config.interpolationDelimiter, config.formatters)
    }

    fun buildKey(key: String, optionMap: Map<String, Any>, locale: Locale, pluralMapper: Plural.Mapper): String {
        if (optionMap.isEmpty()) return key

        val context = optionMap.safeType<String>(Options.CONTEXT)
        val ordinal = optionMap.safeType<Boolean>(Options.ORDINAL) ?: false
        val pluralValue = optionMap.safeType<Int>(Options.COUNT)?.let { count ->
            if (ordinal) {
                pluralMapper.mapOrdinal(locale, count)
            } else {
                pluralMapper.mapPlural(locale, count)
            }.name.lowercase()
        }

        val builder = StringBuilder(key)

        if (context != null) {
            builder.append("_")
                .append(context)
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

    fun interpolate(
        value: String,
        locale: Locale,
        options: Map<String, Any>,
        optionMap: Map<String, Any>,
        interpolationDelimiter: VariableDelimiter,
        formats: Map<String, VariableFormatter>,
    ): String {
        val context = InterpolationContext(locale, options, optionMap, formats)
        return value.interpolate(context, interpolationDelimiter, customResolver)
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

    private class InterpolationContext(
        val locale: Locale,
        val options: Map<String, Any>,
        val optionMap: Map<String, Any>,
        val formats: Map<String, VariableFormatter>,
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
        generalSplit = """\s*($IDENTIFIER)(?:$format)?\s*""".toRegex()
    }

    private val customResolver = object : VariableResolver<InterpolationContext> {

        override fun resolveTo(builder: Appendable, name: String, context: InterpolationContext) {
            // '!!' is ok, because values are validated on translator creation
            val (_, variableName, formatName, formatParams) = generalSplit.matchEntire(name)!!.groupValues

            val variableValue = context.options[variableName]
                ?: context.optionMap[variableName]
                ?: illegalArgument("variable '$variableName' not found")

            var formatPassedParams = emptyMap<String, String>()
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
                HashMap<String, String>(formatParams.length).apply {
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
            context.formats[formatName]!!.format(builder, actualValue, context.locale, paramMap)
        }

        override fun resolve(name: String, context: InterpolationContext): String =
            throw AssertionError("This method should never be called")

    }

}
