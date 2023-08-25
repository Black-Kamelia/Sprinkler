package com.kamelia.sprinkler.i18n

import com.fasterxml.jackson.databind.ObjectMapper
import com.kamelia.sprinkler.util.illegalArgument
import com.kamelia.sprinkler.util.unsafeCast
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.util.*

/**
 * - key syntax
 * - accepted value types (string, map, list)
 * - flattening
 *
 */
interface Translator {

    val rootKey: String?

    val isRoot: Boolean
        get() = rootKey == null

    val defaultLocale: Locale

    val currentLocale: Locale

    fun translate(key: String, locale: Locale): String

    fun translate(key: String): String = translate(key, currentLocale)

    fun section(key: String): Translator

    fun t(key: String, locale: Locale): String = translate(key, locale)

    fun t(key: String): String = translate(key)

    fun toMap(): Map<Locale, Map<String, String>>

    fun prettyDisplay(locale: Locale): String

    fun withNewCurrentLocale(locale: Locale): Translator

    companion object {

        @JvmStatic
        fun builder(defaultLocale: Locale): TranslatorBuilder = TranslatorBuilder(defaultLocale)

    }

}

internal class TranslatorImpl private constructor(
    override val rootKey: String?,
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
        val actualKey = rootKey?.let { "$it.$key" } ?: key
        return try {
            innerTranslate(actualKey, locale)
        } catch (e: NotFoundException) {
            illegalArgument("Key '$actualKey' not found for locale '$locale'.")
        }
    }

    override fun section(key: String): Translator {
        require(FULL_KEY_REGEX.matches(key)) {
            "Invalid key '$key'. For more details about key syntax, see Translator interface documentation."
        }
        val newRootKey = rootKey?.let { "$it.$key" } ?: key
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
        val root = rootKey!!
        return buildMap {
            translations.forEach { (locale, map) ->
                put(locale, map.filter { (key, _) -> key.startsWith(root) })
            }
        }
    }

    override fun prettyDisplay(locale: Locale): String {
        val map = translations[locale] ?: return "{}"
        val dumpMap = HashMap<String, Any>()
        val root = rootKey ?: ""
        val rootLength = root.split('.').size - 1
        map.asSequence()
            .filter { it.key.startsWith(root) }
            .sortedBy { it.key }
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
        TranslatorImpl(rootKey, defaultLocale, locale, translations)


    private fun innerTranslate(key: String, locale: Locale): String {
        val localeMap = translations[locale] ?: return tryFallback(key, locale)
        return localeMap[key] ?: tryFallback(key, locale)
    }

    private fun tryFallback(key: String, locale: Locale): String {
        if (defaultLocale == locale) throw NotFoundException
        return innerTranslate(key, defaultLocale)
    }

    override fun toString(): String =
        "Translator(rootKey=$rootKey, defaultLocale=$defaultLocale, translations=$translations)"


}

private object NotFoundException : RuntimeException(null, null, false, false)

fun jsonParser(): I18nFileParser = I18nFileParser.from { content ->
    ObjectMapper().readValue(content, HashMap::class.java).unsafeCast()
}

fun yamlParser(): I18nFileParser = I18nFileParser.from { Yaml().load(it) }

fun Map<*, *>.prettyPrint(): String = buildString {
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

fun main() {
    val translator = Translator.builder(Locale.FRANCE)
        .addFile(File("translations"), yamlParser())
        .build()
    val translator2 = TranslatorBuilder(Locale.FRANCE)
        .addFile(File("foo.json"), jsonParser()) { Locale.ENGLISH }
        .build()
    val str = translator.section("pages.login").prettyDisplay(Locale.ENGLISH)
    val str2 = translator2.prettyDisplay(Locale.ENGLISH)
    println(translator.toMap() == translator2.toMap())
    println(str2)
}
