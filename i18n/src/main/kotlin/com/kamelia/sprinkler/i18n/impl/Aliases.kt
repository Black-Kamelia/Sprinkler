package com.kamelia.sprinkler.i18n.impl

import com.kamelia.sprinkler.i18n.Identifier
import com.kamelia.sprinkler.i18n.TranslationKey
import com.kamelia.sprinkler.i18n.Translator

/**
 * Represents the syntax of a translation variable, however, it does not take into account the delimiters, but only the
 * characters within the delimiters.
 *
 * It should be structured as follows:
 * - Can start with an arbitrary number of `' '` (spaces) followed by a mandatory [Identifier] representing the name of
 * the variable, followed by an arbitrary number of `' '`.
 * - It can optionally be followed by a `,` to indicate the presence of a format.
 * - An arbitrary number of ` ` can be present after the `,` which will be followed by the format name.
 * - The format name must respect the [Identifier] rules, and must have been previously registered in the
 * [configuration][TranslatorBuilder.Configuration].
 * - Optionally, the format can be followed by an arbitrary number of parameters which are presented in the next bullets
 * - The format name can be directly followed by an arbitrary number of `' '` and a `(` to indicate the presence of format
 * parameters (always at least one).
 * - Parameters are represented as key-value pairs, separated by `:`. Keys cannot contain `:` or `,` (unless escaped),
 * and values cannot contain `,` or `)` (unless escaped). Note that spaces are not trimmed and are considered part of
 * the key or value.
 * - Each parameter is separated by a (non-escaped) `,`.
 * - The last parameter must be followed by a `)`.
 * - The ')' can be followed by an arbitrary number of `' '`.
 *
 * Here are some examples of valid [TranslationInterpolationVariables][TranslationInterpolationVariable]:
 * - `'my-variable'`
 * - `'my-variable '`
 * - `' my-variable  ,my-format'`
 * - `'my-variable,    format'`
 * - `' my-variable    ,  my-format(param1:value1)'`
 * - `'my-variable, my-format    (param1\:still-name:value1,param2:value2)   '`
 *
 * Whenever a [String] is supposed to represent a [TranslationInterpolationVariable], and does not respect the rules
 * above, it will result in an exception being thrown (the type of the exception will depend on the function).
 */
typealias TranslationInterpolationVariable = String

/**
 * Represents the data stored in a [TranslationSourceMap], used to build a [Translator], using a [TranslatorBuilder].
 *
 * It can be one of the following types:
 * - [TranslationInterpolationVariable]
 * - [Boolean]
 * - Subtype of [Number] (e.g. [Int], [Long], [Double], etc.)
 * - [List] of [TranslationSourceData]
 * - [TranslationSourceMap]
 *
 * Whenever a function accepts a [TranslationSourceData], it is the responsibility of the caller to ensure that the
 * given value respects the rules above. Any value that does not respect these rules will result in an exception being
 * thrown (the type of the exception will depend on the function).
 *
 * @see TranslationSourceMap
 * @see Translator
 */
typealias TranslationSourceData = Any

/**
 * Represents a map that can be used to build a [Translator], using a [TranslatorBuilder]. Keys of this map are
 * [TranslationKeys][TranslationKey] and values are [TranslationSourceData]s.
 *
 * Whenever a function accepts a [TranslationSourceMap], it is the responsibility of the caller to ensure that the
 * given value respects the rules above. Any value that does not respect these rules will result in an exception being
 * thrown (the type of the exception will depend on the function).
 *
 * @see TranslationKey
 * @see TranslationSourceData
 * @see Translator
 */
typealias TranslationSourceMap = Map<TranslationKey, TranslationSourceData>
