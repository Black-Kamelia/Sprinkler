package com.kamelia.sprinkler.i18n

/**
 * Represents a translation option. It can be used to provide additional information to a translation.
 */
typealias TranslationOption = Any

/**
 * Represents a part of a translation key. It composes a [TranslationKey].
 *
 * It should respect the following rules:
 * - It cannot be empty
 * - It must start and end with an alphanumeric character
 * - It can contain alphanumeric characters, dashes and underscores
 * - It cannot contain two consecutive dashes or underscores (or a dash followed by an underscore or vice-versa).
 *
 * Whenever a function accepts a [TranslationKeyPart], it is the responsibility of the caller to ensure that the
 * given value respects the rules above. Any value that does not respect these rules will result in an exception being
 * thrown (the type of the exception will depend on the function).
 *
 * **NOTE**: the regex of [TranslationKeyPart] is `[a-zA-Z\d]+(-|_[a-zA-Z\d]+)*`.
 */
typealias TranslationKeyPart = String

/**
 * Represents a translation key. It is used to identify a translation in a [Translator]. It is composed of one or more
 * [TranslationKeyPart]s separated by a dot.
 *
 * Whenever a function accepts a [TranslationKey], it is the responsibility of the caller to ensure that the given
 * value respects the rules above. Any value that does not respect these rules will result in an exception being thrown
 * (the type of the exception will depend on the function).
 *
 * **NOTE**: the regex of [TranslationKey] is `[a-zA-Z\d]+(-|_[a-zA-Z\d]+)*(\.[a-zA-Z\d]+(-|_[a-zA-Z\d]+)*)*`, as
 * defined in [TranslationKeyPart].
 *
 * @see TranslationKeyPart
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
 * [TranslationKeyParts][TranslationKeyPart] and values are [TranslationSourceData]s.
 *
 * Whenever a function accepts a [TranslationSourceMap], it is the responsibility of the caller to ensure that the
 * given value respects the rules above. Any value that does not respect these rules will result in an exception being
 * thrown (the type of the exception will depend on the function).
 *
 * @see TranslatorBuilder
 * @see TranslationKeyPart
 * @see TranslationSourceData
 * @see Translator
 */
typealias TranslationSourceMap = Map<TranslationKeyPart, TranslationSourceData>
