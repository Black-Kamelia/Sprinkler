package com.kamelia.sprinkler.i18n

import com.zwendo.restrikt.annotation.PackagePrivate
import java.util.*

@PackagePrivate
internal class TranslatorImpl private constructor(
    override val prefix: String?,
    override val defaultLocale: Locale,
    override val currentLocale: Locale,
    @PackagePrivate
    internal val translations: Map<Locale, Map<String, String>>,
    @PackagePrivate
    internal val optionConfiguration: OptionConfiguration,
) : Translator {

    constructor(
        defaultLocale: Locale,
        currentLocale: Locale,
        children: Map<Locale, Map<String, String>>,
        optionConfiguration: OptionConfiguration,
    ) : this(
        null,
        defaultLocale,
        currentLocale,
        children,
        optionConfiguration,
    )

    override fun tn(
        key: String,
        options: Map<String, Any>,
        locale: Locale,
        fallbackLocale: Locale?,
        vararg fallbacks: String,
    ): String? {
        require(KEY_REGEX.matches(key)) {
            "Invalid key '$key'. For more details about key syntax, see Translator interface documentation."
        }
        val actualKey = prefix?.let { "$it.$key" } ?: key

        try {
            innerTranslate(actualKey, locale, options, fallbacks)?.let { return it }

            if (fallbackLocale != null && locale != fallbackLocale) { // to avoid a second lookup with the same key
                innerTranslate(actualKey, fallbackLocale, options, fallbacks)?.let { return it }
            }
        } catch (e: I18nException) {
            TODO()
            //throw IllegalArgumentException("")
        }

        return null
    }

    override fun section(key: String): Translator {
        require(KEY_REGEX.matches(key)) {
            "Invalid key '$key'. For more details about key syntax, see Translator interface documentation."
        }
        val newRootKey = prefix?.let { "$it.$key" } ?: key
        return TranslatorImpl(newRootKey, currentLocale, defaultLocale, translations, optionConfiguration)
    }

    override fun toMap(): Map<Locale, Map<String, String>> {
        val root = prefix
        return if (root == null) {
            translations.mapValues { (_, map) -> // simple deep copy
                map.toMap()
            }
        } else {
            translations.mapValues { (_, map) -> // deep copy with filtering and key prefix removal
                map.asSequence()
                    // we must check that the char at root.length is a dot to avoid removing keys that start with the
                    // same prefix but are not direct children of the root e.g. prefix='a' and key='ab'
                    // NOTE: we first check the dot instead of the startWith because it is cheaper
                    .filter { (key, _) -> '.' == key.getOrNull(root.length) && key.startsWith(root) }
                    .map { (key, value) -> key.substring(root.length + 1) to value } // + 1 to remove the dot
                    .toMap()
            }
        }
    }

    override fun withNewCurrentLocale(locale: Locale): Translator =
        TranslatorImpl(prefix, defaultLocale, locale, translations, optionConfiguration)

    override fun toString(): String =
        "Translator(prefix=$prefix, defaultLocale=$defaultLocale, currentLocale=$currentLocale, translations=${toMap()})"

    private fun innerTranslate(
        key: String,
        locale: Locale,
        options: Map<String, Any>,
        fallbacks: Array<out String>,
    ): String? {
        OptionProcessor.translate(this, key, prefix, options, locale)?.let { return it }

        fallbacks.forEach { fallback ->
            OptionProcessor.translate(this, fallback, prefix, options, locale)?.let { return it }
        }

        return null
    }

}
