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
    fun interpolateI(vararg args: Any): TranslationRequestProxy = mapValue { it.interpolateIndexed(*args) }

    @JvmStatic
    fun interpolate(args: Map<String, Any>): TranslationRequestProxy = mapValue { it.interpolate(args) }

    @JvmStatic
    fun interpolate(vararg args: Pair<String, Any>): TranslationRequestProxy = mapValue { it.interpolate(*args) }

    @JvmStatic
    fun contextualize(context: String): TranslationRequestProxy = mapKey { "$it$CTX_SEP$context" }

    @JvmStatic
    @JvmOverloads
    fun pluralize(count: Int, args: Map<String, Any> = emptyMap()): TranslationRequestProxy =
        TranslationRequestProxy { translator, key, locale ->
            val actualKey = "$key$CTX_SEP${pluralizeSuffix(count)}"
            translator.translate(actualKey, locale).interpolate(args)
        }

    @JvmStatic
    fun pluralize(count: Int, vararg args: Pair<String, Any>): TranslationRequestProxy =
        TranslationRequestProxy { translator, key, locale ->
            val actualKey = "$key$CTX_SEP${pluralizeSuffix(count)}"
            translator.translate(actualKey, locale).interpolate(*args)
        }

    @JvmStatic
    fun pluralizeIndexed(count: Int, vararg args: Any): TranslationRequestProxy =
        TranslationRequestProxy { translator, key, locale ->
            val actualKey = "$key$CTX_SEP${pluralizeSuffix(count)}"
            translator.translate(actualKey, locale).interpolateIndexed(*args)
        }

    @JvmStatic
    fun contextualizePlural(count: Int, context: String, args: Map<String, Any>): TranslationRequestProxy =
        TranslationRequestProxy { translator, key, locale ->
            val actualKey = "$key$CTX_SEP$context$CTX_SEP${pluralizeSuffix(count)}"
            translator.translate(actualKey, locale).interpolate(args)
        }

    @JvmStatic
    fun contextualizePlural(count: Int, context: String, vararg args: Pair<String, Any>): TranslationRequestProxy =
        TranslationRequestProxy { translator, key, locale ->
            val actualKey = "$key$CTX_SEP$context$CTX_SEP${pluralizeSuffix(count)}"
            translator.translate(actualKey, locale).interpolate(*args)
        }

    @JvmStatic
    fun contextualizePluralI(count: Int, context: String, vararg args: Any): TranslationRequestProxy =
        TranslationRequestProxy { translator, key, locale ->
            val actualKey = "$key$CTX_SEP$context$CTX_SEP${pluralizeSuffix(count)}"
            translator.translate(actualKey, locale).interpolateIndexed(*args)
        }

    @JvmStatic
    fun gender(isMale: Boolean): TranslationRequestProxy = contextualize(if (isMale) MALE else FEMALE)

    private fun pluralizeSuffix(count: Int): String = when (count) {
        0 -> "zero"
        1 -> "one"
        else -> "other"
    }

    private const val MALE = "male"

    private const val FEMALE = "female"

    private const val CTX_SEP = "_"

}
