package com.kamelia.sprinkler.i18n

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

    override fun translateOrNull(key: String, locale: Locale): String? {
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

internal fun Map<*, *>.prettyPrint(): String = buildString {
    append("{\n")
    val queue = ArrayDeque<Triple<Int, String, Map<*, *>>>()
    queue.addFirst(Triple(0, "", this@prettyPrint))
    while (queue.isNotEmpty()) {
        val (indentLevel, name, map) = queue.removeFirst()
        prettyPrint(map, queue, indentLevel, name)
    }
    append("\n}")
}

private fun StringBuilder.prettyPrint(
    map: Map<*, *>,
    queue: ArrayDeque<Triple<Int, String, Map<*, *>>>,
    indentLevel: Int,
    keyName: String,
) {
    val notEmptyKey = keyName.isNotEmpty()
    if (notEmptyKey) {
        repeat(indentLevel) { append("    ") }
        append("\"")
        append(keyName)
        append("\": {\n")
    }

    map.entries.forEachIndexed { index, (key, value) ->
        if (value is Map<*, *>) {
            queue.addFirst(Triple(indentLevel + 1, key.toString(), value.unsafeCast()))
            return@forEachIndexed
        }
        repeat(indentLevel + 1) { append("    ") }
        append("\"")
        append(key)
        append("\": \"")
        append(value)
        append("\"")
        if (index < map.size - 1 || queue.peekFirst()?.first == indentLevel + 1) {
            append(",")
        }
        append("\n")
    }

    if (notEmptyKey) {
        val nextIndentation = queue.peekFirst()?.first ?: 1
        val difference = indentLevel - (nextIndentation - 1)
        val notLast = queue.isNotEmpty()
        for (i in 0 until difference) {
            repeat(indentLevel - i) { append("    ") }
            append("}")
            if (i == difference - 1 && queue.isNotEmpty()) {
                append(",")
            }
            if (notLast || i < difference - 1) {
                append("\n")
            }
        }
    }
}

