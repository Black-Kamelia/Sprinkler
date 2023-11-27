@file:JvmName("OptionUtils")

package com.kamelia.sprinkler.i18n


import com.kamelia.sprinkler.i18n.Options.OPTIONS
import java.util.*

/**
 * Class defining the options that can be passed as extra arguments during translations.
 *
 * All options are passed in a map bound to the [OPTIONS] key.
 * It looks like this:
 * ```
 * val translator: Translator = ...
 * translator.t(
 *     "my.translation.key",
 *     mapOf(
 *         "extraArg1" to "value1",
 *         Options.OPTIONS to mapOf(
 *             "option1" to "value1",
 *             "option2" to "value2"
 *         )
 *     )
 * )
 * ```
 *
 * **NOTE**: The options defined in this class specify authorized values for each of them. Any value that does not
 * match the authorized values will result in an [IllegalArgumentException] being thrown when used with a [Translator]
 * created from a [TranslatorBuilder].
 *
 * @see Translator
 */
object Options {

    /**
     * The root key for options in extra arguments.
     *
     * Valid values:
     * - [Map] of [String] to [Any].
     */
    const val OPTIONS = "options"

    /**
     * The context of the translation. It can be used to disambiguate translations.
     *
     * Valid values:
     * - [String]
     *
     * The most common use case is to
     * disambiguate gender, like in the following example:
     *
     * ```
     * // content:
     * // {
     * //   "greetings_male": "Hello mister",
     * //   "greetings_female": "Hello miss"
     * // }
     * val translator: Translator = ...
     *
     * fun greetings(isMale: Boolean) {
     *     val value = translator.t(
     *         "greetings",
     *         mapOf(
     *             Options.OPTIONS to mapOf(
     *                 Options.CONTEXT to (if (isMale) "male" else "female")
     *             )
     *         )
     *     )
     *     println(value)
     * }
     * ```
     *
     * As shown in the example above, the context actually appends the value to the key, separated by an underscore.
     *
     * **NOTE**: the context is appended to the key before the [plural][Plurals] value (e.g. `key_male_one`).
     *
     * @see Plurals
     * @see COUNT
     */
    const val CONTEXT = "context"

    /**
     * The count value of the translation, used to disambiguate plural forms.
     *
     * Valid values:
     * - positive [Int]
     *
     * The most common use case is to disambiguate the plural form of a word, like in the following example:
     *
     * ```
     * // content:
     * // {
     * //   "item_zero": "I have no items",
     * //   "item_one": "I have one item",
     * //   "item_other": "I have several items"
     * // }
     * val translator: Translator = ...
     *
     * fun items(count: Int) {
     *    val value = translator.t(
     *        "item",
     *        mapOf(
     *            Options.OPTIONS to mapOf(
     *                Options.COUNT to count
     *            )
     *        )
     *    )
     *    println(value)
     * }
     * ```
     *
     * As shown in the example above, the plural value actually appends the value to the key, separated by an
     * underscore.
     *
     * **NOTE**: the plural value is appended to the key after the [context][CONTEXT] (e.g. `key_male_one`).
     *
     * @see Plurals
     * @see CONTEXT
     */
    const val COUNT = "count"

    /**
     * The plural value of the translation. It can be used to disambiguate translations depending on the number of
     * items.
     *
     * @see COUNT
     * @see TranslatorConfiguration.Builder.pluralMapper
     */
    enum class Plurals {

        /**
         * Plural value usually used in case the count is 0.
         */
        ZERO,
        /**
         * Plural value usually used in case the count is 1.
         */
        ONE,

        /**
         * Plural value usually used in case the count is 2.
         */
        TWO,

        /**
         * Plural value usually used in case the count represents a few items.
         */
        FEW,

        /**
         * Plural value usually used in case the count represents many items.
         */
        MANY,

        /**
         * Plural value used as default when no other value matches.
         */
        OTHER,

        ;

        internal val representation: String = name.lowercase()

        companion object {

            /**
             * Default mapper function for pluralization. It is based on the english
             * [Unicode plural rules chart](https://unicode.org/cldr/charts/latest/supplemental/language_plural_rules.html).
             *
             * @param locale the locale to use
             * @param count the count to use
             * @return the plural value
             */
            @JvmStatic
            fun defaultCountMapper(locale: Locale, count: Int): Plurals {
                require(count >= 0) { "count must be >= 0, but was $count" }
                return when (count) {
                    1 -> ONE
                    else -> OTHER
                }
            }

        }

    }

}

/**
 * Shorthand method to create a [Pair] of [Options.OPTIONS] and a [Map] of [String] to [Any].
 *
 * Here is an example:
 * ```
 * val translator: Translator = ...
 * translator.t(
 *    "my.translation.key",
 *    options(Options.CONTEXT to "my-context")
 * )
 * ```
 *
 * @param pairs the pairs to put in the map
 * @return the created pair
 */
@Suppress("NOTHING_TO_INLINE")
inline fun options(vararg pairs: Pair<String, Any>): Pair<String, Map<String, Any>> = OPTIONS to mapOf(*pairs)
