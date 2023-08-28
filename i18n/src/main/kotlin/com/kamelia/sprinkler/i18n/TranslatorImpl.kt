package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.util.illegalArgument
import com.kamelia.sprinkler.util.unsafeCast
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

    override fun translate(key: String, locale: Locale): String {
        require(FULL_KEY_REGEX.matches(key)) {
            "Invalid key '$key'. For more details about key syntax, see Translator interface documentation."
        }
        val actualKey = prefix?.let { "$it.$key" } ?: key

        val value = translations[locale]?.get(actualKey)
        if (value != null) return value

        if (defaultLocale != currentLocale) {
            val fallback = translations[defaultLocale]?.get(actualKey)
            if (fallback != null) return fallback
        }

        illegalArgument("Key '$actualKey' not found for locale '$locale'.")
    }

    override fun section(key: String): Translator {
        require(FULL_KEY_REGEX.matches(key)) {
            "Invalid key '$key'. For more details about key syntax, see Translator interface documentation."
        }
        val newRootKey = prefix?.let { "$it.$key" } ?: key
        return TranslatorImpl(newRootKey, currentLocale, defaultLocale, translations)
    }

    override fun toMap(): Map<Locale, Map<String, String>> {
        if (isRoot) {
            return buildMap {
                translations.forEach { (locale, map) ->
                    put(locale, map.toMap())
                }
            }
        }
        val root = prefix!!
        return buildMap {
            translations.forEach { (locale, map) ->
                put(locale, map.filter { (key, _) -> key.startsWith(root) })
            }
        }
    }

    override fun prettyDisplay(locale: Locale): String {
        val map = translations[locale] ?: return "{}"
        val dumpMap = HashMap<String, Any>()
        val root = prefix ?: ""
        val rootLength = root.split('.').size - 1
        map.asSequence()
            .filter { it.key.startsWith(root) }
            .forEach { (key, value) ->
                val split = key.split('.')
                var currentMap = dumpMap
                split.forEachIndexed { index, it ->
                    if (index < rootLength) return@forEachIndexed
                    if (index == split.size - 1) {
                        currentMap[it] = value
                        return@forEachIndexed
                    }
                    currentMap = currentMap.computeIfAbsent(it) { HashMap<String, Any>() }.unsafeCast()
                }
            }

        return if (dumpMap.isEmpty()) {
            "{}"
        } else {
            dumpMap.prettyPrint()
        }
    }

    override fun withNewCurrentLocale(locale: Locale): Translator =
        TranslatorImpl(prefix, defaultLocale, locale, translations)

    override fun toString(): String {
        val actualTranslations = if (isRoot) {
            translations
        } else {
            val root = prefix!!
            translations.mapValues { (_, map) ->
                map.filter { (key, _) -> key.startsWith(root) }
            }
        }
        return "Translator(prefix=$prefix, defaultLocale=$defaultLocale, currentLocale=$currentLocale, translations=$actualTranslations)"
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Translator) return false
        if (prefix != other.prefix || defaultLocale == other.defaultLocale || currentLocale == other.currentLocale) {
            return false
        }
        return if (other is TranslatorImpl) {
            translations == other.translations
        } else {
            toMap() == other.toMap()
        }
    }

    override fun hashCode(): Int = Objects.hash(prefix, defaultLocale, currentLocale, translations)

}
