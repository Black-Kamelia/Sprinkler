package com.kamelia.sprinkler.i18n

import com.zwendo.restrikt.annotation.PackagePrivate
import java.util.*

@PackagePrivate
internal class TranslatorImpl private constructor(
    override val prefix: String?,
    override val defaultLocale: Locale,
    override val currentLocale: Locale,
    private val translations: Map<Locale, Map<String, String>>,
) : Translator {

    constructor(defaultLocale: Locale, currentLocale: Locale, children: Map<Locale, Map<String, String>>) : this(
        null,
        defaultLocale,
        currentLocale,
        children
    )

    override fun translateOrNull(key: String, locale: Locale): String? {
        require(FULL_KEY_REGEX.matches(key)) {
            "Invalid key '$key'. For more details about key syntax, see Translator interface documentation."
        }
        val actualKey = prefix?.let { "$it.$key" } ?: key

        val value = translations[locale]?.get(actualKey)
        if (value != null) return value

        if (defaultLocale != locale) { // to avoid a second lookup with the same key
            val fallback = translations[defaultLocale]?.get(actualKey)
            if (fallback != null) return fallback
        }

        return null
    }

    override fun section(key: String): Translator {
        require(FULL_KEY_REGEX.matches(key)) {
            "Invalid key '$key'. For more details about key syntax, see Translator interface documentation."
        }
        val newRootKey = prefix?.let { "$it.$key" } ?: key
        return TranslatorImpl(newRootKey, currentLocale, defaultLocale, translations)
    }

    override fun toMap(): Map<Locale, Map<String, String>> {
        val root = prefix
        return if (root == null) {
            translations.mapValues { (_ , map) -> // simple deep copy
                map.toMap()
            }
        } else {
            translations.mapValues { (_, map) -> // deep copy with filtering and key prefix removal
                map.asSequence()
                    .filter { (key, _) -> key.startsWith(root) && key != root }
                    .map { (key, value) -> key.substring(root.length) to value }
                    .toMap()
            }
        }
    }

    override fun withNewCurrentLocale(locale: Locale): Translator =
        TranslatorImpl(prefix, defaultLocale, locale, translations)

    override fun toString(): String =
        "Translator(prefix=$prefix, defaultLocale=$defaultLocale, currentLocale=$currentLocale, translations=${toMap()})"

}
