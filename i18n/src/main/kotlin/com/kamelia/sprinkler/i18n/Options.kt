@file:JvmName("OptionUtils")

package com.kamelia.sprinkler.i18n


import com.kamelia.sprinkler.i18n.Options.OPTIONS

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
     * The most common use case is to disambiguate gender, like in the following example:
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
     * **NOTE**: The context is appended to the key before the [plural][Plural] value which is
     * defined by the [COUNT] option (e.g. a possible key with context and plural `key_male_one`).
     *
     * @see Plural
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
     * **NOTE**: The plural value is appended to the key after the [context][CONTEXT] (e.g. `key_male_one`).
     *
     * @see Plural
     * @see CONTEXT
     */
    const val COUNT = "count"

    /**
     * Whether the count value should be treated as an ordinal number. It can be used to disambiguate ordinal forms.
     * It is only used if the [COUNT] option is also present (otherwise it is ignored).
     *
     * Valid values:
     * - [Boolean]
     *
     * The most common use case is to disambiguate the ordinal form of a word, like in the following example:
     *
     * ```
     * // content:
     * // {
     * //   "item_ordinal_one": "I arrived {count}st",
     * //   "item_ordinal_two": "I arrived {count}nd",
     * //   "item_ordinal_few": "I arrived {count}rd",
     * //   "item_ordinal_other": "I arrived {rank}th"
     * // }
     * val translator: Translator = ...
     *
     * fun rank(count: Int) {
     *    val value = translator.t(
     *        "item",
     *        mapOf(
     *            Options.OPTIONS to mapOf(
     *                Options.COUNT to count,
     *                Options.ORDINAL to true
     *            )
     *        )
     *    )
     *    println(value)
     * }
     * ```
     *
     * As shown in the example above, the `ordinal` literal is appended to the key, separated by an underscore.
     *
     * **NOTE**: The value is right before the [plural][Plural] value (e.g. a possible key with
     * `key_male_ordinal_one`).
     */
    const val ORDINAL = "ordinal"

}

/**
 * Shorthand method to create a [Pair] of [Options.OPTIONS] and a [Map] of [String] to [Any].
 *
 * Here is an example:
 * ```
 * val translator: Translator = ...
 * translator.t(
 *    "my.translation.key",
 *    mapOf(options(Options.CONTEXT to "my-context"))
 * )
 * ```
 *
 * @param pairs the pairs to put in the map
 * @return the created pair
 */
@Suppress("NOTHING_TO_INLINE")
inline fun options(vararg pairs: Pair<String, Any>): Pair<String, Map<String, Any>> = OPTIONS to mapOf(*pairs)
