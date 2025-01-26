package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.i18n.Translator.Companion.invoke
import com.zwendo.restrikt2.annotation.HideFromJava
import com.zwendo.restrikt2.annotation.HideFromKotlin
import java.util.Locale
import java.util.function.Consumer

/**
 * Interface representing an object that can be used to translate strings. Through the [t] method, a key can be
 * translated using a [Locale].
 *
 * It can be used as shown in the following example:
 * ```
 * val translator: Translator = Translator {
 *     translations {
 *         map(Locale.ENGLISH to mapOf("my.key" to "My translation"))
 *         map(Locale.GERMAN to mapOf("my.key" to "Meine Übersetzung"))
 *     }
 * }
 *
 * val englishTranslation = translator.t("my.key", selectedLocale(Locale.ENGLISH))
 * val germanTranslation = translator.t("my.key", selectedLocale(Locale.GERMAN))
 * ```
 *
 * In addition to finding translations for a given key and locale, you can also provide different
 * [arguments][TranslationArgument] to have a more flexible translation system.
 *
 *
 * Keys must follow the rules defined by the [TranslationKey] typealias (see its documentation).
 * Any key that does not abide to these rules will result in an exception being thrown.
 *
 * This interface also provides a set of utility functions (such as [section] or [withNewCurrentLocale]) to reduce
 * boilerplate code when translating strings.
 *
 * To instantiate a [Translator], use the [factory][invoke] method.
 *
 * @see TranslationKey
 * @see TranslationArgument
 * @see TranslatorBuilder
 */
sealed interface Translator {

    /**
     * The prefix prepended to all keys used to translate values. If null, the [Translator] is a root [Translator].
     *
     * @see isRoot
     */
    val prefix: String?

    /**
     * Whether this [Translator] is a root [Translator], meaning that no prefix is prepended to the keys used to
     * translate values. In other words, it means that any given [TranslationKey] should represent the complete path
     * to get the translation value.
     *
     * @see prefix
     */
    val isRoot: Boolean

    /**
     * The default locale that is used as a fallback when a translation is not found for the chosen locale.
     */
    val defaultLocale: Locale?

    /**
     * The current locale used to translate keys when no locale is provided.
     */
    val currentLocale: Locale

    /**
     * Returns the translation using the provided information. If the translation is not found, `null` is returned.
     *
     * Here are the list of [TranslationArgument] default values used if not provided:
     * - The [TranslationArgument.selectedLocale] defaults to the [currentLocale].
     * - The [TranslationArgument.fallbackLocale] defaults to the [defaultLocale].
     * - The [TranslationArgument.fallbacks] defaults to an empty array.
     *
     * When looking for the translation, the order of resolution is the following (finding the key stops the search):
     * 1. First, the translation is searched for the given [key] and `selected locale`.
     *
     * 2. Then, it will try to find a valid translation for the keys provided as `fallback` in order (still using the
     * `selected locale`).
     *
     * 3. The next step is to use the
     * [localeSpecializationReduction][TranslatorBuilder.Configuration.localeSpecializationReduction] function to try to
     * find a valid translation for a more generic locale (still using the `selected locale` as a base), by repeating
     * the steps `1` and `2`.
     *
     * 4. If still no translation is found, the next step is to repeat the steps `1`, `2`, and `3` using the `fallback
     * locale` as a base (only if the `fallback locale` is not null and different from the `selected locale`).
     *
     * 5. Finally, if no translation is found, `null` is returned.
     *
     * **NOTE**: During all the steps above, the [args] remains the same for each translation attempt.
     *
     * **NOTE**: This method does not check for argument duplicates during the translation process, meaning that you can
     * pass the same argument multiple times, but only the first occurrence will be used.
     *
     * @param key the key to translate
     * @param args the extra arguments to use
     * @return the translation using the provided information or `null` if not found
     * @throws IllegalArgumentException if the key is not [valid][TranslationKey]
     */
    fun tn(key: TranslationKey, vararg args: TranslationArgument): String?

    /**
     * Returns the translation using the provided information. If the translation is not found, the behavior depends on
     * the implementation.
     *
     * Here are the list of [TranslationArgument] default values used if not provided:
     * - The [TranslationArgument.selectedLocale] defaults to the [currentLocale].
     * - The [TranslationArgument.fallbackLocale] defaults to the [defaultLocale].
     * - The [TranslationArgument.fallbacks] defaults to an empty array.
     *
     * When looking for the translation, the order of resolution is the following (finding the key stops the search):
     * 1. First, the translation is searched for the given [key] and `selected locale`.
     *
     * 2. Then, it will try to find a valid translation for the keys provided as `fallback` in order (still using the
     * `selected locale`).
     *
     * 3. The next step is to use the
     * [localeSpecializationReduction][TranslatorBuilder.Configuration.localeSpecializationReduction] function to try to
     * find a valid translation for a more generic locale (still using the `selected locale` as a base), by repeating
     * the steps `1` and `2`.
     *
     * 4. If still no translation is found, the next step is to repeat the steps `1`, `2`, and `3` using the `fallback
     * locale` as a base (only if the `fallback locale` is not null and different from the `selected locale`).
     *
     * 5. Finally, if no translation is found, the behavior depends on the implementation.
     *
     * **NOTE**: During all the steps above, the [args] remains the same for each translation attempt.
     *
     * **NOTE**: This method does not check for argument duplicates during the translation process, meaning that you can
     * pass the same argument multiple times, but only the first occurrence will be used.
     *
     * @param key the key to translate
     * @param args the extra arguments to use
     * @return the translation using the provided information or a value depending on the implementation if not found
     * @throws IllegalArgumentException if the key is not [valid][TranslationKey]
     * @throws RuntimeException if the implementation throws an exception when a translation is not found
     */
    fun t(key: TranslationKey, vararg args: TranslationArgument): String

    /**
     * Returns a [Translator] with the given [key] as root key prefix (it can return itself after a state mutation,
     * depending on the implementation). The [key] will be prepended to all keys used to translate values.
     *
     * In the example below:
     * ```
     * val translator: Translator = ...
     *
     * val sectionTranslator = translator.section("my")
     * val myKey = sectionTranslator.t("key")
     * ```
     *
     * is equivalent to:
     * ```
     * val translator: Translator = ...
     *
     * val myKey = translator.t("my.key")
     * ```
     *
     * **NOTE**: This method does not check if the key actually exists in the translations.
     *
     * @param key the root key
     * @return a new [Translator] with the given [key] as root key
     * @throws IllegalArgumentException if the key is not [valid][TranslationKey]
     */
    fun section(key: TranslationKey): Translator

    /**
     * Returns a [Translator] with the given [locale] as current locale (it can return itself after a state mutation,
     * depending on the implementation).
     *
     * In the example below:
     * ```
     * val translator: Translator = ...
     *
     * val germanTranslator = translator.withNewCurrentLocale(Locale.GERMAN)
     * val germanTranslation = germanTranslator.t("my.key")
     * ```
     *
     * is equivalent to:
     * ```
     * val translator: Translator = ...
     *
     * val germanTranslation = translator.t("my.key", Locale.GERMAN)
     * ```
     *
     * **NOTE**: This method does not check if the [locale] is actually supported by this [Translator].
     *
     * @param locale the new current locale
     * @return a [Translator] with the given [locale] as current locale
     */
    fun withNewCurrentLocale(locale: Locale): Translator

    /**
     * Returns the root [Translator] version of this [Translator] (it can return itself after a state mutation,
     * depending on the implementation). The root translator is a translator with its prefix set to null.
     *
     * @return the root [Translator] version of this [Translator]
     */
    fun asRoot(): Translator

    /**
     * Returns the parent [Translator] of this [Translator] (it can return itself after a state mutation, depending on
     * the implementation). The parent translator is the translator with the same configuration as this one, but with
     * the last key of the prefix removed. If the prefix is null, it returns itself.
     *
     * @return the parent [Translator] of this [Translator]
     */
    fun asParent(): Translator

    /**
     * Returns a map containing all translations for all locales.
     *
     * **NOTE**: If this [Translator] is a section, only the translations for the keys under the section will be
     * returned.
     *
     * @return a map containing all translations for all locales
     */
    fun toMap(): Map<Locale, Map<TranslationKey, String>>

    companion object {

        /**
         * Creates a new [Translator] using a [TranslatorBuilder] configured with the provided [block]
         *
         * @param block the configuration block
         * @return the created translator
         *
         * @throws Exception if any method called in the [block] throws an exception
         */
        @HideFromJava
        operator fun invoke(block: TranslatorBuilder.() -> Unit): Translator {
            val caller = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).callerClass
            return TranslatorBuilderImpl(caller).apply(block).build()
        }

        /**
         * The key regex used to validate keys.
         *
         * @return the key regex
         */
        @JvmStatic
        fun keyRegex(): Regex = KEY_REGEX

        //region Java mirror API

        /**
         * Creates a new [Translator] using a [TranslatorBuilder] configured with the provided [block].
         *
         * @param block the configuration block
         * @return the created translator
         *
         * @throws Exception if any method called in the [block] throws an exception
         */
        @JvmStatic
        @HideFromKotlin
        fun create(block: Consumer<TranslatorBuilder>): Translator {
            val walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
            val potentialCaller = walker.callerClass

            // Workaround for kotlin JvmStatic methods called from Java.
            // JvmStatic methods call their companion object method, which adds an extra frame to the stack.
            // This if statement is used to remove that extra frame conditionally.
            val caller = if (potentialCaller == Translator::class.java) {
                walker.walk { it.skip(2).findFirst() }.get().declaringClass
            } else {
                potentialCaller
            }
            return TranslatorBuilderImpl(caller).apply { block.accept(this) }.build()
        }

        //endregion

        private val KEY_REGEX = """${IDENTIFIER}(?:\.${IDENTIFIER})*""".toRegex()

    }

}
