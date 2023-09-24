package com.kamelia.sprinkler.i18n

import com.zwendo.restrikt.annotation.PackagePrivate
import java.util.*

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
            throw IllegalArgumentException(e.message)
        }

        return null
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
                    .filter { (key, _) -> '.' == key.getOrNull(root.length) && key.startsWith(root) }
                    .map { (key, value) -> key.substring(root.length + 1) to value } // + 1 to remove the dot
                    .toMap()
            }
        }
    }

    override fun withNewCurrentLocale(locale: Locale): Translator = TranslatorImpl(prefix, locale, data)

    override fun asRoot(): Translator = if (isRoot) {
        this
    } else {
        TranslatorImpl(null, currentLocale, data)
    }

    override fun toString(): String =
        "Translator(prefix=$prefix, defaultLocale=$defaultLocale, currentLocale=$currentLocale, translations=${toMap()})"

    private fun innerTranslate(
        key: String,
        locale: Locale,
        options: Map<String, Any>,
        fallbacks: Array<out String>,
    ): String? {
        data.optionProcessor.translate(key, prefix, options, locale)?.let { return it }

        fallbacks.forEach { fallback ->
            data.optionProcessor.translate(fallback, prefix, options, locale)?.let { return it }
        }

        return null
    }

}

