package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.util.illegalArgument
import com.zwendo.restrikt.annotation.PackagePrivate
import java.util.Locale

@PackagePrivate
internal class TranslatorImpl private constructor(
    override val prefix: String?,
    override val currentLocale: Locale,
    private val data: TranslatorData,
) : Translator {

    constructor(currentLocale: Locale, data: TranslatorData) : this(null, currentLocale, data)

    override val defaultLocale: Locale
        get() = data.defaultLocale

    override fun tn(
        key: String,
        extraArgs: Map<String, Any>,
        locale: Locale,
        fallbackLocale: Locale?,
        vararg fallbacks: String,
    ): String? {
        require(KEY_REGEX.matches(key)) {
            "Invalid key '$key'. For more details about key syntax, see Translator interface documentation."
        }
        val actualKey = prefix?.let { "$it.$key" } ?: key

        innerTranslate(actualKey, locale, extraArgs, fallbacks)?.let { return it }

        if (fallbackLocale != null && locale != fallbackLocale) { // to avoid a second lookup with the same key
            innerTranslate(actualKey, fallbackLocale, extraArgs, fallbacks)?.let { return it }
        }

        return null
    }

    override fun t(
        key: TranslationKey,
        extraArgs: Map<String, Any>,
        locale: Locale,
        fallbackLocale: Locale?,
        vararg fallbacks: String,
    ): String = tn(key, extraArgs, locale, fallbackLocale, *fallbacks)
        ?: when (data.configuration.missingKeyPolicy) {
            TranslatorConfiguration.MissingKeyPolicy.THROW_EXCEPTION -> keyNotFound(
                key,
                extraArgs,
                locale,
                fallbackLocale,
                fallbacks
            )
            TranslatorConfiguration.MissingKeyPolicy.RETURN_KEY -> key
        }

    override fun section(key: String): Translator {
        require(KEY_REGEX.matches(key)) {
            "Invalid key '$key'. For more details about key syntax, see Translator interface documentation."
        }
        val newRootKey = prefix?.let { "$it.$key" } ?: key
        return TranslatorImpl(newRootKey, currentLocale, data)
    }

    override fun toMap(): Map<Locale, Map<String, String>> {
        val root = prefix
        return if (root == null) {
            data.translations.mapValues { (_, map) -> // simple deep copy
                map.toMap()
            }
        } else {
            data.translations.mapValues { (_, map) -> // deep copy with filtering and key prefix removal
                map.asSequence()
                    // we must check that the char at root.length is a dot to avoid removing keys that start with the
                    // same prefix but are not direct children of the root e.g. prefix='a' and key='ab'
                    // NOTE: we first check the dot instead of the startWith because it is cheaper
                    .filter { (key, _) -> key.length > root.length && '.' == key[root.length] && key.startsWith(root) }
                    .map { (key, value) -> key.substring(root.length + 1) to value } // + 1 to remove the dot
                    .toMap()
            }
        }
    }

    override fun withNewCurrentLocale(locale: Locale): Translator = if (currentLocale == locale) {
        this
    } else {
        TranslatorImpl(prefix, locale, data)
    }

    override fun asRoot(): Translator = if (isRoot) {
        this
    } else {
        TranslatorImpl(null, currentLocale, data)
    }

    override fun toString(): String =
        "Translator(prefix=$prefix, defaultLocale=$defaultLocale, currentLocale=$currentLocale, configuration=${data.configuration}, translations=${toMap()})"

    private fun innerTranslate(
        key: String,
        locale: Locale,
        options: Map<String, Any>,
        fallbacks: Array<out String>,
    ): String? {
        OptionProcessor.translate(data, key, options, locale)?.let { return it }

        fallbacks.forEach { fallback ->
            OptionProcessor.translate(data, fallback, options, locale)?.let { return it }
        }

        return null
    }

    private fun keyNotFound(
        key: TranslationKey,
        options: Map<String, Any>,
        locale: Locale,
        fallbackLocale: Locale?,
        fallbacks: Array<out String>,
    ): Nothing {
        val builder = StringBuilder()
        builder.append("No translation found for parameters: key='")
            .append(key)
            .append("', locale='")
            .append(locale)
            .append("', fallbackLocale='")
            .append(fallbackLocale)
            .append("', fallbacks='")

        fallbacks.joinTo(builder, ", ", "[", "]")

        builder.append("', extraArgs='")
            .append(options)
            .append("'. ")

        illegalArgument(builder.toString())
    }

}
