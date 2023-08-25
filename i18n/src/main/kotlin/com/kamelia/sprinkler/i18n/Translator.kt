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

    fun translate(key: String, locale: Locale): String

    fun section(key: String): Translator

    fun t(key: String, locale: Locale): String = translate(key, locale)

    fun toMap(): Map<Locale, Map<String, String>>

    companion object {

        @JvmStatic
        fun builder(defaultLocale: Locale): TranslatorBuilder = TranslatorBuilder(defaultLocale)

    }

}

internal class TranslatorImpl private constructor(
    override val rootKey: String?,
    override val defaultLocale: Locale,
    private val translations: Map<Locale, Map<String, String>>,
) : Translator {

    constructor(defaultLocale: Locale, children: Map<Locale, Map<String, String>>) : this(null, defaultLocale, children)

    override fun section(key: String): Translator {
        require(FULL_KEY_REGEX.matches(key)) {
            "Invalid key '$key'. For more details about key syntax, see Translator interface documentation."
        }
        val newRootKey = rootKey?.let { "$it.$key" } ?: key
        return TranslatorImpl(newRootKey, defaultLocale, translations)
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

fun main() {
    val translator = Translator.builder(Locale.FRANCE)
        .addFile(File("translations"), yamlParser())
        .addMap(Locale.CHINESE, mapOf("color" to "bingchilling"))
        .addMap(Locale.CHINESE, mapOf("color" to "bingchilling2"))
        .duplicateKeyResolutionPolicy(TranslatorBuilder.DuplicateKeyResolution.KEEP_FIRST)
        .build()
}
