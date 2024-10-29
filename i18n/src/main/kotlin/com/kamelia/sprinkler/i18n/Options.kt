@file:JvmName("OptionUtils")
@file:HideFromJava

package com.kamelia.sprinkler.i18n


import com.kamelia.sprinkler.i18n.Options.COUNT
import com.zwendo.restrikt2.annotation.HideFromJava
import com.zwendo.restrikt2.annotation.HideFromKotlin

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
     *             Options.CONTEXT to (if (isMale) "male" else "female")
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
    const val CONTEXT = "_context"

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
     *        mapOf(Options.COUNT to count)
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
    const val COUNT = "_count"

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
     *            Options.COUNT to count,
     *            Options.ORDINAL to true
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
    const val ORDINAL = "_ordinal"


    @JvmStatic
    @HideFromKotlin
    fun count(count: Long): Map.Entry<String, Any> = java.util.Map.entry(COUNT, count)

    @JvmStatic
    @HideFromKotlin
    fun count(count: Double): Map.Entry<String, Any> = java.util.Map.entry(COUNT, count)

    @JvmStatic
    @HideFromKotlin
    fun context(context: String): Map.Entry<String, Any> = java.util.Map.entry(CONTEXT, context)

    @JvmStatic
    @HideFromKotlin
    fun ordinal(value: Boolean): Map.Entry<String, Any> = java.util.Map.entry(ORDINAL, value)

    @JvmStatic
    @HideFromKotlin
    fun ordinal(): Map.Entry<String, Any> = ordinal(true)

}

fun count(count: Long): Pair<String, Any> = COUNT to count

fun count(count: Double): Pair<String, Any> = COUNT to count

fun context(context: String): Pair<String, Any> = Options.CONTEXT to context

fun ordinal(value: Boolean = true): Pair<String, Any> = Options.ORDINAL to value
