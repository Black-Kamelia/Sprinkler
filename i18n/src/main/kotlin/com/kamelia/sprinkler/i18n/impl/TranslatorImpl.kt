package com.kamelia.sprinkler.i18n.impl

import com.kamelia.sprinkler.i18n.TranslationKey
import com.kamelia.sprinkler.i18n.Translator
import com.kamelia.sprinkler.i18n.Utils
import com.kamelia.sprinkler.util.ExtendedCollectors
import com.kamelia.sprinkler.util.illegalArgument
import com.zwendo.restrikt2.annotation.PackagePrivate
import java.util.Locale

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
        extraArgs: Map<String, Any>,
        locale: Locale,
        fallbackLocale: Locale?,
        vararg fallbacks: String,
    ): String? {
        require(Translator.keyRegex().matches(key)) { "Invalid key '$key'. ${Utils.KEY_DOCUMENTATION}" }
        val actualKey = prefix?.let { "$it.$key" } ?: key

        innerTranslate(actualKey, locale, extraArgs, fallbacks)?.let { return it }

        if (fallbackLocale != null && locale != fallbackLocale) { // to avoid a second lookup with the same key
            innerTranslate(actualKey, fallbackLocale, extraArgs, fallbacks)?.let { return it }
        }

        return null
    }

    override fun t(
        key: TranslationKey,
        extraArgs: Map<String, Any>,
        locale: Locale,
        fallbackLocale: Locale?,
        vararg fallbacks: String,
    ): String {
        val result = tn(key, extraArgs, locale, fallbackLocale, *fallbacks)
        if (result != null) return result
        val actualKey = if (locale in data.translations.keys) {
            TranslationProcessor.buildKey(key, extraArgs, data.pluralMapper(locale))
        } else {
            key
        }
        return when (data.missingKeyPolicy) {
            TranslatorBuilder.MissingKeyPolicy.THROW_EXCEPTION -> {
                val message = keyNotFoundMessage(
                    actualKey,
                    extraArgs,
                    locale,
                    fallbackLocale,
                    fallbacks
                )
                illegalArgument(message)
            }

            TranslatorBuilder.MissingKeyPolicy.RETURN_KEY -> actualKey
        }
    }

    override fun section(key: String): Translator {
        require(Translator.keyRegex().matches(key)) { "Invalid key '$key'. ${Utils.KEY_DOCUMENTATION}" }
        val newRootKey = prefix?.let { "$it.$key" } ?: key
        return TranslatorImpl(newRootKey, currentLocale, data)
    }

    override fun toMap(): Map<Locale, Map<String, String>> {
        val root = prefix
        return if (root == null) {
            data.translations.mapValuesTo(LinkedHashMap(data.translations.size)) { (_, map) -> // simple deep copy
                LinkedHashMap(map)
            }
        } else {
            data.translations.mapValuesTo(LinkedHashMap(data.translations.size)) { (_, map) -> // deep copy with filtering and key prefix removal
                map.entries
                    .stream() // the stream is ordered as the underlying set is a LinkedHashSet
                    // we must check that the char at root.length is a dot to avoid removing keys that start with the
                    // same prefix but are not direct children of the root e.g. prefix='a' and key='ab'
                    // NOTE: we first check the dot instead of the startWith because it is cheaper
                    .filter { (key, _) -> key.length > root.length && '.' == key[root.length] && key.startsWith(root) }
                    .map { (key, value) -> key.substring(root.length + 1) to value } // + 1 to remove the dot
                    .collect(ExtendedCollectors.toLinkedHashMap())
            }
        }
    }

    override fun withNewCurrentLocale(locale: Locale): Translator =
        if (currentLocale == locale) {
            this
        } else {
            TranslatorImpl(prefix, locale, data)
        }

    override fun asRoot(): Translator = if (isRoot) {
        this
    } else {
        TranslatorImpl(null, currentLocale, data)
    }

    override fun toString(): String =
        "Translator(prefix=$prefix, defaultLocale=$defaultLocale, currentLocale=$currentLocale, missingKeyPolicy=${data.missingKeyPolicy}, variableDelimiter=('${data.interpolationDelimiter.startDelimiter}', '${data.interpolationDelimiter.endDelimiter}') formatters=${data.formatters}, pluralMappers=${data.pluralMapper}, translations=`use toMap()`)"

    private fun innerTranslate(
        key: String,
        locale: Locale,
        options: Map<String, Any>,
        fallbacks: Array<out String>,
    ): String? {
        val tr = TranslationProcessor.translate(data, key, options, locale)
        if (tr != null) return tr

        fallbacks.forEach { fallback ->
            val fb = TranslationProcessor.translate(data, fallback, options, locale)
            if (fb != null) return fb
        }

        return null
    }

    private fun keyNotFoundMessage(
        key: TranslationKey,
        options: Map<String, Any>,
        locale: Locale,
        fallbackLocale: Locale?,
        fallbacks: Array<out String>,
    ): String = buildString {
        append("No translation found for parameters: key='")
        append(key)
        append("', locale='")
        append(locale)
        append("', fallbackLocale='")
        append(fallbackLocale)
        append("', fallbacks='")
        fallbacks.joinTo(this, ", ", "[", "]")
        append("', extraArgs='")
        append(options)
        append("'. ")

    }

}
