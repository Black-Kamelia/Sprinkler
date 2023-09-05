package com.kamelia.sprinkler.i18n

/**
 * Yes
 */
typealias TranslationOption = Any

/**
 * String/Number/Boolean/List/Map
 */
typealias TranslatorSourceData = Any

/**
 * Represents a part of a translation key.
 *
 * It should respect the following rules:
 * - It cannot be empty ;
 * - It can only be composed of alphanumeric characters, dashes and underscores ;
 *
 * Whenever a function accepts a [TranslationKeyPart], it is the responsibility of the caller to ensure that the
 * given value respects the rules above. Any value that does not respect these rules will result in an exception being
 * thrown (the type of the exception will depend on the function).
 *
 * **NOTE**: Here is the regular expression used to validate a [TranslationKeyPart]: `[\w\d-](?:[\w\d-]*[a-zA-Z0-9])?`
 */
typealias TranslationKeyPart = String

typealias TranslationKey = String

/**
 * Implementations of this interface are responsible for parsing the file and returning:
 * - The locale of the file ;
 * - A map of the parsed data.
 *
 * Maps returned from [parseFile] must be structured as follows:
 * - Keys should be valid key identifiers, as defined in [Translator] ;
 * - Values cannot be null ;
 * - Values must be of type [String], [Number], [Boolean], [List] or [Map] ;
 * - Lists and maps can only contain the types listed above.
 */
typealias TranslationSourceMap = Map<TranslationKey, TranslatorSourceData>
