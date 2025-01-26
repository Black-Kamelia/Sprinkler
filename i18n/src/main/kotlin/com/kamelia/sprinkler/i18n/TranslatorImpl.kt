package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.util.entryOf
import com.kamelia.sprinkler.util.illegalArgument
import com.zwendo.restrikt2.annotation.PackagePrivate
import java.util.Locale
import java.util.stream.Collectors

@PackagePrivate
internal class TranslatorImpl private constructor(
    override val prefix: String?,
    override val currentLocale: Locale,
    private val data: TranslatorData,
) : Translator {

    constructor(currentLocale: Locale, data: TranslatorData) : this(null, currentLocale, data)

    override val isRoot: Boolean
        get() = prefix == null

    override val defaultLocale: Locale?
        get() = data.defaultLocale

    override fun tn(key: String, vararg args: TranslationArgument): String? = translate(key, args, true)

    override fun t(key: TranslationKey, vararg args: TranslationArgument): String = translate(key, args, false)!!

    private fun translate(key: TranslationKey, args: TranslationArgs, allowNull: Boolean): String? {
        require(Translator.keyRegex().matches(key)) { "Invalid key '$key'. $KEY_DOCUMENTATION" }
        val actualKey = prefix?.let { "$it.$key" } ?: key

        val locale = args.selectedLocale() ?: currentLocale
        val fallbacks = args.fallbackKeys() ?: emptyArray()
        val fallbackLocale = args.fallbackLocale()
            ?: data.defaultLocale

        val tr = innerTranslate(actualKey, locale, fallbacks, args)
        if (tr != null) return tr

        if (fallbackLocale != null && locale != fallbackLocale) { // to avoid a second lookup with the same key
            val fb = innerTranslate(actualKey, fallbackLocale, fallbacks, args)
            if (fb != null) return fb
        }

        if (allowNull) return null

        val displayedKey = if (locale in data.translations.keys) {
            TranslationProcessor.buildKey(key, args, data.pluralRuleProvider(locale))
        } else {
            key
        }
        return when (data.missingKeyPolicy) {
            TranslatorBuilder.MissingKeyPolicy.THROW_EXCEPTION -> {
                val message = keyNotFoundMessage(displayedKey, args)
                illegalArgument(message)
            }

            TranslatorBuilder.MissingKeyPolicy.RETURN_KEY -> actualKey
        }
    }

    private fun innerTranslate(
        key: String,
        locale: Locale,
        fallbacks: Array<out String>,
        args: TranslationArgs,
    ): String? {
        val tr = TranslationProcessor.translate(data, key, args, locale)
        if (tr != null) return tr

        fallbacks.forEach { fallback ->
            val fb = TranslationProcessor.translate(data, fallback, args, locale)
            if (fb != null) return fb
        }

        return null
    }

    override fun section(key: String): Translator {
        require(Translator.keyRegex().matches(key)) { "Invalid key '$key'. $KEY_DOCUMENTATION" }
        val newRootKey = prefix?.let { "$it.$key" } ?: key
        return TranslatorImpl(newRootKey, currentLocale, data)
    }

    override fun toMap(): Map<Locale, Map<String, String>> {
        val root = prefix
        return if (root == null) {
            data.translations // the map is already unmodifiable
        } else {
            data.translations
                .entries
                .stream()
                .map { (locale, map) ->
                    val value = map.entries
                        .stream()
                        // we must check that the char at root.length is a dot to avoid removing keys that start with the
                        // same prefix but are not direct children of the root e.g., prefix=`a` and key=`ab`
                        // NOTE: we first check the dot instead of the startWith because it is less expensive
                        .filter { (key, _) -> key.length > root.length && '.' == key[root.length] && key.startsWith(root) }
                        .map { (key, value) -> key.substring(root.length + 1) to value } // + 1 to remove the dot
                        .collect(Collectors.toUnmodifiableMap({ it.first }, { it.second }))
                    entryOf(locale, value)
                }
                .collect(Collectors.toUnmodifiableMap({ it.key }, { it.value }))
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

    override fun asParent(): Translator = if (isRoot) {
        this
    } else {
        // we know that prefix is not null AND that there is at least one dot in it
        val prefix = prefix!!
        val lastDotIndex = prefix.lastIndexOf('.')
        TranslatorImpl(prefix.substring(0, lastDotIndex), currentLocale, data)
    }

    override fun toString(): String = "Translator(prefix=$prefix, currentLocale=$currentLocale, $data)"

    private fun keyNotFoundMessage(
        key: TranslationKey,
        args: TranslationArgs,
    ): String = buildString {
        append("No translation found for parameters: key='")
        append(key)
        append("', args=")
        args.joinTo(this, ", ", "[", "]") { it.toString() }
        append(". ")

    }

    private fun TranslationArgs.selectedLocale(): Locale? =
        findKind<TranslationArgument.Companion.SelectedLocale>()?.value

    private fun TranslationArgs.fallbackKeys(): Array<out String>? =
        findKind<TranslationArgument.Companion.Fallbacks>()?.value

    private fun TranslationArgs.fallbackLocale(): Locale? =
        findKind<TranslationArgument.Companion.FallbackLocale>()?.value

}
