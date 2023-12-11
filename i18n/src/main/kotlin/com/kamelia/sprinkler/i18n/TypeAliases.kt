package com.kamelia.sprinkler.i18n

/**
 * Represents an identifier used in this library. It is used in several places to identify elements, such as translation
 * keys, formatters, etc.
 *
 * It should respect the following rules:
 * - It cannot be empty
 * - It must start and end with an alphanumeric character
 * - It can contain alphanumeric characters, dashes and underscores
 * - It cannot contain two consecutive dashes or underscores (or a dash followed by an underscore or vice-versa).
 *
 * Here are some examples of valid [Identifier]s:
 * - `my-key`
 * - `my_key`
 * - `myKey`
 * - `1`
 *
 * Whenever a function accepts a [Identifier], it is the responsibility of the caller to ensure that the
 * given value respects the rules above. Any value that does not respect these rules will result in an exception being
 * thrown (the type of the exception will depend on the function).
 *
 * **NOTE**: the regex of [Identifier] is `[a-zA-Z\d]+(?:-|_[a-zA-Z\d]+)*`.
 *
 * @see TranslationKey
 * @see Translator
 */
typealias Identifier = String

/**
 * Represents the syntax of a translation variable, however, it does not take into account the delimiters, but only the
 * characters within the delimiters.
 *
 * It should respect the following rules:
 * - Empty variables are not allowed
 * - It can only contain alphanumeric characters, dashes and underscores
 */
typealias TranslationInterpolationVariable = String

/**
 * Represents a translation key. It is used to identify a translation in a [Translator]. It is composed of one or more
 * [Identifier]s separated by a dot.
 *
 * Here are some examples of valid [TranslationKey]s:
 * - `my-root.my-node.my-key`
 * - `my_root.my_node.my_key`
 * - `myRoot.myNode.myKey`
 * - `my-root.my-node.my-table.1`
 *
 * Whenever a function accepts a [TranslationKey], it is the responsibility of the caller to ensure that the given
 * value respects the rules above. Any value that does not respect these rules will result in an exception being thrown
 * (the type of the exception will depend on the function).
 *
 * **NOTE**: the regex of [TranslationKey] is `[a-zA-Z\d]+(?:-|_[a-zA-Z\d]+)*(?:\.[a-zA-Z\d]+(?:-|_[a-zA-Z\d]+)*)*`. as
 * defined in [Identifier].
 *
 * @see Identifier
 * @see Translator
 */
typealias TranslationKey = String

/**
 * Represents the data stored in a [TranslationSourceMap], used to build a [Translator], using a [TranslatorBuilder].
 *
 * It can be one of the following types:
 * - [String]
 * - [Boolean]
 * - Subtype of [Number] (e.g. [Int], [Long], [Double], etc.)
 * - [List] of [TranslationSourceData]
 * - [TranslationSourceMap]
 *
 * Whenever a function accepts a [TranslationSourceData], it is the responsibility of the caller to ensure that the
 * given value respects the rules above. Any value that does not respect these rules will result in an exception being
 * thrown (the type of the exception will depend on the function).
 *
 * @see TranslatorBuilder
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
 * @see TranslatorBuilder
 * @see TranslationKey
 * @see TranslationSourceData
 * @see Translator
 */
typealias TranslationSourceMap = Map<TranslationKey, TranslationSourceData>
