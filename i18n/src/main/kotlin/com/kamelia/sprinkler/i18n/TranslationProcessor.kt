package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.util.VariableDelimiter
import com.kamelia.sprinkler.util.VariableResolver
import com.kamelia.sprinkler.util.illegalArgument
import com.kamelia.sprinkler.util.interpolateTo
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
        val context = ProcessingContext(data, locale, extraArgs, optionMap)

        val found = try {
            innerTranslate(key, context)
        } catch (e: NestingDepthExceededException) {
            val errorMessageBuilder = StringBuilder()
            errorMessageBuilder.append("Nesting depth exceeded while translating key '$key' with locale '$locale': ")
            for (i in e.pairs().lastIndex downTo 0) {
                errorMessageBuilder.append(e.pairs()[i])
                if (i > 0) errorMessageBuilder.append(" -> ")
            }
            throw IllegalArgumentException(errorMessageBuilder.toString())
        }
        return if (found) context.builder.toString() else null
    }

    fun buildKey(key: String, context: ProcessingContext): String {
        val optionMap = context.optionMap
        if (optionMap.isEmpty()) return key

        val interpolationContext = optionMap.safeType<String>(Options.CONTEXT)
        val ordinal = optionMap.safeType<Boolean>(Options.ORDINAL) ?: false
        val pluralValue = optionMap.safeType<Int>(Options.COUNT)?.let { count ->
            if (ordinal) {
                context.pluralMapper.mapOrdinal(context.locale, count)
            } else {
                context.pluralMapper.mapPlural(context.locale, count)
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

    fun innerTranslate(key: String, context: ProcessingContext): Boolean {
        if (context.depth > context.maxDepth) {
            throw NestingDepthExceededException()
        }
        // first, get the translations for the given locale, or return null if they don't exist
        val translations = context.translations[context.locale] ?: return false

        // build the actual key with the options
        val actualKey = buildKey(key, context)

        // get the value for the actual key or return null if it doesn't exist
        val value = translations[actualKey] ?: return false

        try {
            value.interpolateTo(context.builder, context, customResolver, context.variableDelimiter)
        } catch (e: NestingDepthExceededException) {
            e.addPair(actualKey)
            throw e
        }

        return true
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
        private val data: TranslatorData,
        val locale: Locale,
        val interpolationValues: Map<String, Any>,
        val optionMap: Map<String, Any>,
    ) {

        var depth: Int = 0
            private set

        val builder: StringBuilder = StringBuilder()

        val translations: Map<Locale, Map<String, String>>
            get() = data.translations

        val pluralMapper: Plural.Mapper
            get() = data.configuration.pluralMapper

        val formatters: Map<String, VariableFormatter>
            get() = data.configuration.formatters

        val variableDelimiter: VariableDelimiter
            get() = data.configuration.interpolationDelimiter

        val maxDepth: Int
            get() = data.configuration.maxNestingDepth

        fun nestedTranslate(key: String) {
            depth++
            innerTranslate(key, this)
            depth--
        }

    }

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

            if (variableName[0] == NESTED_KEY_CHAR) { // if it's a nested key
                context.nestedTranslate(variableName.substring(1))
                return
            }

            val variableValue = context.interpolationValues[variableName]
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
            context.formatters[formatName]!!.format(builder, actualValue, context.locale, paramMap)
        }

        override fun resolve(name: String, context: ProcessingContext): String =
            throw AssertionError("This method should never be called")

    }

}

private class NestingDepthExceededException : RuntimeException("", null, false, false) {

    private val translationKeys = ArrayList<String>()

    fun addPair(key: String) {
        translationKeys.add(key)
    }

    fun pairs(): List<String> = translationKeys

}
