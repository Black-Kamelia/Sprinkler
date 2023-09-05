package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.util.illegalArgument
import java.util.*

/**
 * Interface representing an object that can be used to translate strings. Through [t] method (and its
 * shorthand [t]), a key can be translated using a [Locale].
 *
 * Keys are represented as strings, and must respect the following syntax:
 * - A key is composed of one or more key **identifiers** separated by a **dot** ;
 * - **Identifiers** must be **at least one character long**, **start and end** with an **alphanumeric character**, and can contain
 * **alphanumeric characters**, **dashes** and **underscores**.
 *
 * For example, the following keys are valid:
 * - `foo`
 * - `foo.bar`
 * - `foo-bar`
 * - `foo_bar`
 *
 * Any other key is invalid and will throw an [IllegalArgumentException] when used to translate a value.
 *
 * If a valid key is passed to the [t] method, the [Translator] will try to find a translation for the given
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

    fun tn(
        key: String,
        options: Map<String, Any>,
        locale: Locale,
        fallbackLocale: Locale?,
        vararg fallbacks: String,
    ): String?

    fun tn(key: String, options: Map<String, Any>, locale: Locale, vararg fallbacks: String): String? =
        tn(key, options, locale, defaultLocale, *fallbacks)

    fun tn(key: String, options: Map<String, Any>, vararg fallbacks: String): String? =
        tn(key, options, currentLocale, defaultLocale, *fallbacks)

    fun tn(key: String, vararg fallbacks: String): String? =
        tn(key, emptyMap(), currentLocale, defaultLocale, *fallbacks)

    fun tn(key: String): String? = tn(key, emptyMap(), currentLocale, defaultLocale)

    fun t(
        key: String,
        options: Map<String, Any>,
        locale: Locale,
        fallbackLocale: Locale?,
        vararg fallbacks: String,
    ): String =
        tn(key, options, locale, fallbackLocale, *fallbacks)
            ?: illegalArgument("Key '$key' not found for locale '$locale'.")

    fun t(key: String, options: Map<String, Any>, locale: Locale, vararg fallbacks: String): String =
        t(key, options, locale, defaultLocale, *fallbacks)

    fun t(key: String, options: Map<String, Any>, vararg fallbacks: String): String =
        t(key, options, currentLocale, defaultLocale, *fallbacks)

    fun t(key: String, vararg fallbacks: String): String = t(key, emptyMap(), currentLocale, defaultLocale, *fallbacks)

    fun t(key: String): String = t(key, emptyMap(), currentLocale, defaultLocale)

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
     * Returns a map containing all translations for all locales.
     *
     * **NOTE**: If this [Translator] is a section, only the translations for the keys under the section will be
     * returned.
     *
     * @return a map containing all translations for all locales
     */
    fun toMap(): Map<Locale, Map<String, String>>

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

    fun baseTranslateOrNull(key: String, locale: Locale): String?

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
