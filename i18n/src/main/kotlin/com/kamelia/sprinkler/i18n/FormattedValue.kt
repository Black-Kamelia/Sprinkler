@file:JvmName("FormattedValues")

package com.kamelia.sprinkler.i18n

import com.zwendo.restrikt2.annotation.PackagePrivate
import java.util.Map as JavaMap

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
 *         "name" to "World".formattedWith("myFormatterParam" to "foo")
 *     )
 * )
 * ```
 *
 * In the above example, after retrieving the value of the key "my.key" from the translator, the formatter `myFormatter`
 * will be called on the value "World" with the parameter "myFormatterParam" set to "foo".
 *
 * @see formattedWith
 */
@PackagePrivate
internal class FormattedValue(
    val value: Any,
    val formatParams: Map<String, Any>,
)

/**
 * Creates a [FormattedValue] instance with the given value and format parameters.
 *
 * @receiver the value to be formatted
 * @param formatParams the parameters to be used in the format
 * @return a [FormattedValue] instance with the given value and format parameters
 */
@JvmName("formatted")
fun Any.formattedWith(formatParams: Map<String, Any>): Any = FormattedValue(this, JavaMap.copyOf(formatParams))

/**
 * Creates a [FormattedValue] instance with the given value and format parameters.
 *
 * @receiver the value to be formatted
 * @param formatParams the parameters to be used in the format
 * @return a [FormattedValue] instance with the given value and format parameters
 */
@JvmName("formatted")
fun Any.formattedWith(vararg formatParams: Pair<String, Any>): Any = FormattedValue(this, formatParams.toMap())
