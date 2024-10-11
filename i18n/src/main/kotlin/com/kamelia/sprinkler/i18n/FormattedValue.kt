@file:JvmName("FormattedValues")

package com.kamelia.sprinkler.i18n

import com.zwendo.restrikt2.annotation.PackagePrivate

/**
 * Represents a value and the parameters of the format to be applied to it. It is used to pass parameters to the
 * formatter on translation call instead of directly put them in the translation string.
 *
 * It can be used in the following way:
 *
 * ```
 * // the value of the key "my.key" is "Hello, {name, myFormatter}!"
 * val translator: Translator = ...
 * val value = translator.t(
 *     "my.key",
 *     mapOf(
 *         "name" to p("World", mapOf("myFormatterParam" to "foo"))
 *     )
 * )
 * ```
 *
 * In the above example, after retrieving the value of the key "my.key" from the translator, the formatter `myFormatter`
 * will be called on the value "World" with the parameter "myFormatterParam" set to "foo".
 *
 * @see p
 */
class FormattedValue @PackagePrivate internal constructor(
    internal val value: Any,
    internal val formatParams: Map<String, Any>,
)

/**
 * Creates a [FormattedValue] instance with the given value and format parameters.
 *
 * @param value the value to be formatted
 * @param formatParams the parameters to be used in the format
 * @return a [FormattedValue] instance with the given value and format parameters
 */
fun p(value: Any, formatParams: Map<String, Any>): FormattedValue =
    FormattedValue(value, java.util.Map.copyOf(formatParams))

/**
 * Creates a [FormattedValue] instance with the given value and format parameters.
 *
 * @param value the value to be formatted
 * @param formatParams the parameters to be used in the format
 * @return a [FormattedValue] instance with the given value and format parameters
 */
fun p(value: Any, vararg formatParams: Pair<String, Any>): FormattedValue =
    FormattedValue(value, formatParams.toMap())
