package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.util.VariableResolver
import com.kamelia.sprinkler.util.illegalArgument
import com.kamelia.sprinkler.util.interpolate
import com.kamelia.sprinkler.util.unsafeCast
import com.zwendo.restrikt.annotation.PackagePrivate
import java.util.*

@PackagePrivate
internal object OptionProcessor {

    fun translate(
        translator: TranslatorImpl,
        key: String,
        prefix: String?,
        options: Map<TranslationOption, Any>,
        locale: Locale,
    ): String? {
        // first, we get the translations for the given locale
        val translations = translator.translations[locale] ?: return null

        // if there is no options, we can return the value directly
        if (key.isEmpty()) return translations[key]

        val config = translator.optionConfiguration

        // we build the actual key with the options
        val actualKey = buildKey(key, options, locale, config)

        // we get the value for the actual key or return null if it doesn't exist
        val value = translations[actualKey] ?: return null

        // we interpolate the value with the options
        var interpolated = value.interpolate(VariableResolver.fromMap(options), config.interpolationDelimiter)

        // if the nesting option is true, we try to interpolate the value with the nestingVariableResolver
        if (config.alwaysEnableNestedParsing || options.safeType<Boolean>(Options.NESTING) == true) {
            interpolated = interpolated.interpolate(
                nestingVariableResolver(translations, prefix),
                config.nestingVariableDelimiter
            )
        }

        return interpolated
    }

    private fun buildKey(key: String, options: Map<String, Any>, locale: Locale, config: OptionConfiguration): String {
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
            else -> "${key}_${context}_$count" // maybe use a StringBuilder?
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


private fun nestingVariableResolver(
    map: Map<TranslationKey, String>,
    prefix: TranslationKey?,
): VariableResolver {
    val inner = VariableResolver { name, _ -> getValue(map, name, prefix) }

    return VariableResolver { name, delimitation ->
        var lastResult = getValue(map, name, prefix)
        var current = lastResult.interpolate(inner, delimitation)
        while (current != lastResult) {
            lastResult = current
            current = lastResult.interpolate(inner, delimitation)
        }
        current
    }
}

private fun getValue(map: Map<TranslationKey, String>, key: TranslationKey, prefix: TranslationKey?): String {
    val actualKey = prefix?.let { "$it.$prefix" } ?: key
    return map[actualKey] ?: illegalArgument("Invalid key '$key'.")
}
