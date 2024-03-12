@file:JvmName("FormattedValues")

package com.kamelia.sprinkler.i18n

import com.zwendo.restrikt.annotation.PackagePrivate

/**
 * Represents a value and the parameters of the format to be applied to it. It is used to pass parameters to the
 * formatter on translation call instead of directly put them in the translation string.
 *
 * @see p
 */
class FormattedValue @PackagePrivate internal constructor(
    internal val value: Any,
    internal val formatParams: Map<String, String>,
)

/**
 * Creates a [FormattedValue] instance with the given value and format parameters.
 *
 * @param value the value to be formatted
 * @param formatParams the parameters to be used in the format
 * @return a [FormattedValue] instance with the given value and format parameters
 */
fun p(value: Any, formatParams: Map<String, String>): FormattedValue =
    FormattedValue(value, java.util.Map.copyOf(formatParams))

/**
 * Creates a [FormattedValue] instance with the given value and format parameters.
 *
 * @param value the value to be formatted
 * @param formatParams the parameters to be used in the format
 * @return a [FormattedValue] instance with the given value and format parameters
 */
fun p(value: Any, vararg formatParams: Pair<String, String>): FormattedValue =
    FormattedValue(value, formatParams.toMap())
