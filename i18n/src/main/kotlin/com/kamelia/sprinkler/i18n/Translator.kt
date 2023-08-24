package com.kamelia.sprinkler.i18n

import com.fasterxml.jackson.databind.ObjectMapper
import com.kamelia.sprinkler.util.castOrNull
import com.kamelia.sprinkler.util.illegalArgument
import com.kamelia.sprinkler.util.interpolateIndexed
import com.kamelia.sprinkler.util.unsafeCast
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.util.*

interface Translator {

    val defaultLocale: Locale

    fun translate(key: String, locale: Locale): String

    fun translate(key: String): String = translate(key, defaultLocale)

    fun section(key: String): Translator

    fun t(key: String, locale: Locale): String = translate(key, locale)

    fun t(key: String): String = translate(key)

    companion object {

        fun builder(defaultLocale: Locale): TranslatorBuilder = TranslatorBuilder(defaultLocale)

    }

}

internal class TranslatorImpl private constructor(
    private val rootKey: String?,
    override val defaultLocale: Locale,
    private val children: Map<Locale, Map<String, Any>>,
) : Translator {

    constructor(defaultLocale: Locale, children: Map<Locale, Map<String, Any>>) : this(null, defaultLocale, children)

    override fun section(key: String): Translator = TranslatorImpl(key, defaultLocale, children)

    override fun translate(key: String, locale: Locale): String {
        val actualKey = rootKey?.let { "$it.$key" } ?: key
        return try {
            innerTranslate(actualKey, locale)
        } catch (e: IllegalArgumentException) {
            illegalArgument("Key '$actualKey' not found for locale '$locale'")
        }
    }

    private fun innerTranslate(
        key: String,
        locale: Locale,
    ): String {
        val localeMap = children[locale] ?: return tryFallback(key, locale)
        val fastResult = localeMap[key]
        if (fastResult != null) {
            return fastResult.castOrNull<String>() ?: return tryFallback(key, locale)
        }

        val keyParts = key.split('.').iterator()

        var current = localeMap[keyParts.next()] ?: return tryFallback(key, locale)
        while (keyParts.hasNext()) {
            current = current.castOrNull<Map<String, Any>>()
                ?.get(keyParts.next())
                ?: return tryFallback(key, locale)
        }
        return current.castOrNull<String>() ?: tryFallback(key, locale)
    }

    private fun tryFallback(key: String, locale: Locale): String {
        require(defaultLocale != locale)
        return innerTranslate(key, defaultLocale)
    }

    override fun toString(): String {
        return "TranslatorImpl(rootKey=$rootKey, defaultLocale=$defaultLocale, children=$children)"
    }


}


fun jsonParser(): I18nFileParser = I18nFileParser.from { content ->
    ObjectMapper().readValue(content, HashMap::class.java).unsafeCast()
}

fun yamlParser(): I18nFileParser = I18nFileParser.from { Yaml().load(it) }

fun main() {
    val translator = Translator.builder(Locale.FRANCE)
        .addFile(File("translations"), yamlParser())
        .addMap(Locale.CHINESE, mapOf("color" to "bingchilling"))
        .addMap(Locale.CHINESE, mapOf("color" to "bingchilling2"))
        .duplicateKeyResolutionPolicy(TranslatorBuilder.DuplicateKeyResolutionPolicy.KEEP_FIRST)
        .build()
    println(translator)
    println(translator.t("player1.line", Locale.ENGLISH).interpolateIndexed("John"))
}