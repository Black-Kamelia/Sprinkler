package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.util.VariableDelimiter
import com.kamelia.sprinkler.util.VariableResolver
import com.kamelia.sprinkler.util.illegalArgument
import com.kamelia.sprinkler.util.interpolate
import com.zwendo.restrikt.annotation.PackagePrivate
import java.util.*

@PackagePrivate
internal object OptionProcessor {

    fun translate(
        data: TranslatorData,
        key: String,
        options: Map<String, Any>,
        locale: Locale,
    ): String? {
        // first, get the translations for the given locale, or return null if they don't exist
        val translations = data.translations[locale] ?: return null

        val config = data.translatorConfiguration
        val optionMap = options.safeType<Map<String, Any>>(Options.OPTIONS)
            ?: emptyMap()

        // build the actual key with the options
        val actualKey = buildKey(key, optionMap, locale, config.pluralMapper)

        // get the value for the actual key or return null if it doesn't exist
        val value = translations[actualKey] ?: return null

        return interpolate(value, locale, options, optionMap, config.interpolationDelimiter, config.formats)
    }

    fun buildKey(
        key: String,
        optionMap: Map<String, Any>,
        locale: Locale,
        pluralMapper: Plural.Mapper,
    ): String {
        if (optionMap.isEmpty()) return key

        val context = optionMap.safeType<String>(Options.CONTEXT)
        val ordinal = optionMap.safeType<Boolean>(Options.ORDINAL) ?: false
        val count = optionMap.safeType<Int>(Options.COUNT)?.let { count ->
            if (ordinal) {
                pluralMapper.mapOrdinal(locale, count)
            } else {
                pluralMapper.mapPlural(locale, count)
            }.representation
        }

        val builder = StringBuilder(key)

        if (context != null) {
            builder.append("_")
                .append(context)
        }

        if (count != null) {
            if (ordinal) {
                builder.append("_ordinal")
            }
            builder.append("_")
                .append(count)
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
        if (interpolationDelimiter.variableStart !in value) return value

        val customResolver = VariableResolver { key, _ ->
            // '!!' is ok, because values are validated on translator creation
            val (_, variableName, formatName, params) = generalSplit.matchEntire(key)!!.groupValues

            require(Options.OPTIONS != variableName) {
                "The '${Options.OPTIONS}' variable name is reserved for the options map, use another name."
            }

            val result = options[variableName]
                ?: optionMap[variableName]
                ?: illegalArgument("unknown variable name '$variableName'")

            if (formatName.isNotEmpty()) { // there is a format
                // try to get the format from its name, or throw an exception if it doesn't exist
                val format = formats[formatName] ?: error("Unknown format '$formatName' ($key)")

                val paramList = if (params.isNotEmpty()) { // there are format parameters
                    params.split(paramsSplit)
                        .asSequence()
                        .map(keyValueSplit::split)
                        .map { it[0] to it[1] } // also safe because values are validated on translator creation
                        .toList()
                } else {
                    emptyList()
                }

                format.format(result, locale, paramList)
            } else { // otherwise, just return the result
                result.toString()
            }
        }

        return value.interpolate(customResolver, interpolationDelimiter)
    }

    private inline fun <reified T> Map<String, Any>.safeType(key: String): T? {
        val value = get(key) ?: return null
        require(value is T) {
            "Cannot cast $value (${value.javaClass}) to ${T::class.java}"
        }
        return value
    }

    private fun checkValue(value: String, limiter: VariableDelimiter): Boolean {
        if (value.isEmpty()) return true

        var escaping = false
        var lastChar = value[0]
        var found = false
        for (index in 1 until value.length) {
            val element = value[index]
            if (escaping) {
                escaping = false
                continue
            }
            when (element) {
                '\\' -> escaping = true
                limiter.variableStart -> found = true
                limiter.variableEnd -> {
                    if (lastChar == limiter.variableStart) {
                        throw IllegalStateException("Empty variable name in '$value'")
                    }
                }
            }
            lastChar = element
        }

        return found
    }

    private val generalSplit = """($IDENTIFIER)\s*(?:,\s*($IDENTIFIER)\s*(?:\((.+(?<!\\))\))?)?""".toRegex()

    private val paramsSplit = """(?<!\\),""".toRegex()

    private val keyValueSplit = """(?<!\\):""".toRegex()

}
