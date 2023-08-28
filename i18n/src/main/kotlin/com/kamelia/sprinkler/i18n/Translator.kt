package com.kamelia.sprinkler.i18n

import com.fasterxml.jackson.databind.ObjectMapper
import com.kamelia.sprinkler.util.illegalArgument
import com.kamelia.sprinkler.util.unsafeCast
import com.zwendo.restrikt.annotation.PackagePrivate
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.util.*

/**
 * Interface representing an object that can be used to translate strings. Through [translate] method (and its
 * shorthand [t]), a key can be translated using a [Locale].
 *
 * Keys are represented as strings, and must respect the following syntax:
 * - A key is composed of one or more key **identifiers** separated by a **dot** ;
 * - **Identifiers** must be **at least one character long**, **start and end** with an **alphanumeric character**, and can contain
 * **alphanumeric characters**, **dashes** and **underscores** ;
 *
 * For example, the following keys are valid:
 * - `foo`
 * - `foo.bar`
 * - `foo-bar`
 * - `foo_bar`
 *
 * Any other key is invalid and will throw an [IllegalArgumentException] when used to translate a value.
 *
 * If a valid key is passed to the [translate] method, the [Translator] will try to find a translation for the given
 * key and the given locale (depending on the chosen overload it can be the [currentLocale] or the provided locale
 * parameter). If no translation is found for the given locale, the [defaultLocale] will be used as a fallback. If no
 * translation is found for the [defaultLocale], an [IllegalArgumentException] will be thrown.
 *
 * @see TranslatorBuilder
 */
interface Translator {

    /**
     * The prefix prepended to all keys used to translate values. If null, the [Translator] is a root [Translator].
     */
    val prefix: String?

    /**
     * Whether this [Translator] is a root [Translator], meaning that no prefix is prepended to the keys used to
     * translate values.
     */
    val isRoot: Boolean
        get() = prefix == null

    /**
     * The default locale that is used as a fallback when a translation is not found for the current locale.
     */
    val defaultLocale: Locale

    /**
     * The current locale used to translate keys.
     */
    val currentLocale: Locale

    /**
     * Translates the given key to the given [locale]. If the key is not found for the given [locale] the key will be
     * translated to the [defaultLocale]. If the key is not found for the [defaultLocale], an [IllegalArgumentException]
     * will be thrown.
     *
     * **NOTE**: If [locale] is the [defaultLocale], the fallback step will be skipped in case the key is not found.
     *
     * @param key the key to translate
     * @param locale the locale to translate the key to
     * @return the translated key
     * @throws IllegalArgumentException if the key is not [valid][Translator], or if it does not exist for the [locale]
     * and the [defaultLocale]
     */
    fun translate(key: String, locale: Locale): String

    /**
     * Translates the given key to the [currentLocale]. If the key is not found for the [currentLocale] the key will be
     * translated to the [defaultLocale]. If the key is not found for the [defaultLocale], an [IllegalArgumentException]
     * will be thrown.
     *
     * **NOTE**: If the [currentLocale] is the [defaultLocale], the fallback step will be skipped in case the key is not
     * found.
     *
     * @param key the key to translate
     * @return the translated key
     * @throws IllegalArgumentException if the key is not [valid][Translator], or if it does not exist for the
     * [currentLocale] and the [defaultLocale]
     */
    fun translate(key: String): String = translate(key, currentLocale)

    /**
     * Returns a new [Translator] with the given [key] as root key. The [key] will be prepended to all keys used to
     * translate values.
     *
     * **NOTE**: This method does not check if the key actually exists in the translations.
     *
     * @param key the root key
     * @return a new [Translator] with the given [key] as root key
     * @throws IllegalArgumentException if the key is not [valid][Translator]
     */
    fun section(key: String): Translator

    /**
     * Translates the given key to the given [locale]. If the key is not found for the given [locale] the key will be
     * translated to the [defaultLocale]. If the key is not found for the [defaultLocale], an [IllegalArgumentException]
     * will be thrown.
     *
     * **NOTE**: If [locale] is the [defaultLocale], the fallback step will be skipped in case the key is not found.
     *
     * @param key the key to translate
     * @param locale the locale to translate the key to
     * @return the translated key
     * @throws IllegalArgumentException if the key is not [valid][Translator], or if it does not exist for the [locale]
     * and the [defaultLocale]
     */
    fun t(key: String, locale: Locale): String = translate(key, locale)

    /**
     * Translates the given key to the [currentLocale]. If the key is not found for the [currentLocale] the key will be
     * translated to the [defaultLocale]. If the key is not found for the [defaultLocale], an [IllegalArgumentException]
     * will be thrown.
     *
     * **NOTE**: If the [currentLocale] is the [defaultLocale], the fallback step will be skipped in case the key is not
     * found.
     *
     * @param key the key to translate
     * @return the translated key
     * @throws IllegalArgumentException if the key is not [valid][Translator], or if it does not exist for the
     * [currentLocale] and the [defaultLocale]
     */
    fun t(key: String): String = translate(key)

    /**
     * Returns a map containing all translations for all locales.
     *
     * **NOTE**: If this [Translator] is a section, only the translations for the keys under the section will be
     * returned.
     *
     * @return a map containing all translations for all locales
     */
    fun toMap(): Map<Locale, Map<String, String>>

    /**
     * Returns a pretty-printed string representation of this [Translator] for the given [locale].
     *
     * @param locale the locale to use to translate the keys
     * @return a pretty-printed string representation of this [Translator]
     */
    fun prettyDisplay(locale: Locale): String

    /**
     * Returns a new [Translator] with the given [locale] as current locale. This operation is lightweight (it simply
     * translation map is shared between all instances), meaning that it can be used frequently without any performance
     * impact.
     *
     * **NOTE**: This method does not check if the [locale] is actually supported by this [Translator].
     *
     * @param locale the new current locale
     * @return a new [Translator] with the given [locale] as current locale
     */
    fun withNewCurrentLocale(locale: Locale): Translator

    companion object {

        /**
         * Returns a new [TranslatorBuilder] with the given [defaultLocale]. The [defaultLocale] will be used as the
         * default locale for all [Translator]s built by the returned [TranslatorBuilder].
         *
         * @param defaultLocale the default locale
         * @return a new [TranslatorBuilder] with the given [defaultLocale]
         */
        @JvmStatic
        fun builder(defaultLocale: Locale): TranslatorBuilder = TranslatorBuilder(defaultLocale)

    }

}

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


    private fun innerTranslate(key: String, locale: Locale): String {
        val localeMap = translations[locale] ?: return tryFallback(key, locale)
        return localeMap[key] ?: tryFallback(key, locale)
    }

    private fun tryFallback(key: String, locale: Locale): String {
        if (defaultLocale == locale) throw NotFoundException
        return innerTranslate(key, defaultLocale)
    }

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
