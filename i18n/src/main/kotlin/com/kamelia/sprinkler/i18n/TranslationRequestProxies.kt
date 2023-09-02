package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.util.interpolate
import com.kamelia.sprinkler.util.interpolateIndexed
import java.util.*

object TranslationRequestProxies {

    @JvmStatic
    inline fun mapKey(crossinline mapper: (String) -> String): TranslationRequestProxy =
        TranslationRequestProxy { _, key, _ ->
            mapper(key)
        }

    @JvmStatic
    inline fun mapValue(crossinline mapper: (String) -> String): TranslationRequestProxy =
        TranslationRequestProxy { translator, key, locale ->
            mapper(translator.translate(key, locale))
        }

    @JvmStatic
    fun default(value: String): TranslationRequestProxy =
        TranslationRequestProxy { translator, key, locale ->
        translator.translateOrNull(key, locale) ?: value
    }

    @JvmStatic
    @JvmOverloads
    fun fallback(fallbackOnDefault: Boolean = true, vararg fallbackKeys: String): TranslationRequestProxy =
        object : TranslationRequestProxy { // we can't use the lambda syntax here because we need to access 'this'
            override fun translate(translator: Translator, key: String, locale: Locale): String? {

                var result = translator.translateOrNull(key, locale, fallbackOnDefault)
                var index = 0
                while (result == null && index < fallbackKeys.size) {
                    result = translator.translateOrNull(fallbackKeys[index], locale, fallbackOnDefault)
                    index++
                }
                return if (result == null && fallbackOnDefault && locale != translator.defaultLocale) {
                    translator.translateOrNull(key, translator.defaultLocale, this)
                } else {
                    result
                }
            }
        }

    @JvmStatic
    fun interpolateIndexed(vararg args: Any): TranslationRequestProxy = mapValue { it.interpolateIndexed(*args) }

    @JvmStatic
    fun interpolate(args: Map<String, Any>): TranslationRequestProxy = mapValue { it.interpolate(args) }

    @JvmStatic
    fun interpolate(vararg args: Pair<String, Any>): TranslationRequestProxy = mapValue { it.interpolate(*args) }

    @JvmStatic
    @JvmOverloads
    fun pluralize(count: Int, args: Map<String, Any> = emptyMap()): TranslationRequestProxy =
        TranslationRequestProxy { translator, key, locale ->
            translator.translate(pluralizeKey(key, count), locale).interpolate(args)
        }

    @JvmStatic
    fun pluralize(count: Int, vararg args: Pair<String, Any>): TranslationRequestProxy =
        TranslationRequestProxy { translator, key, locale ->
            translator.translate(pluralizeKey(key, count), locale).interpolate(*args)
        }

    @JvmStatic
    fun pluralizeIndexed(count: Int, vararg args: Any): TranslationRequestProxy =
        TranslationRequestProxy { translator, key, locale ->
            translator.translate(pluralizeKey(key, count), locale).interpolateIndexed(*args)
        }

    @JvmStatic
    fun contextualize(context: String): TranslationRequestProxy = mapKey { "${it}_$context" }

    @JvmStatic
    fun gender(isMale: Boolean): TranslationRequestProxy = contextualize(if (isMale) MALE else FEMALE)

    private fun pluralizeKey(key: String, count: Int): String = when (count) {
        0 -> "${key}_zero"
        1 -> "${key}_one"
        else -> "${key}_other"
    }

    private const val MALE = "male"

    private const val FEMALE = "female"

}
