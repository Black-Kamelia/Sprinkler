package com.kamelia.sprinkler.i18n

/**
 * Represents an identifier used in this library. It is used in several places to identify elements, such as translation
 * keys, formatters, etc.
 *
 * It should respect the following rules:
 * - It cannot be empty
 * - It must start and end with an alphanumeric character
 * - It can contain alphanumeric characters, `-` (dashes) and `_` (underscores)
 * - It cannot contain two consecutive `-` or `_` (or a `-` followed by a `_` or vice versa).
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
 * Represents a translation key. It is used to identify a translation in a [Translator]. It is composed of one or more
 * [Identifier]s separated by a dot.
 *
 * Here are some examples of valid [TranslationKey]s:
 * - `my-root.my_node.my-key`
 * - `myRoot.myNode.myKey`
 * - `my-root.my-node.my-table.1`
 *
 * Whenever a function accepts a [TranslationKey], it is the responsibility of the caller to ensure that the given
 * value respects the rules above. Any value that does not respect these rules will result in an exception being thrown
 * (the type of the exception will depend on the function).
 *
 * **NOTE**: the regex of [TranslationKey] is `[a-zA-Z\d]+(?:-|_[a-zA-Z\d]+)*(?:\.[a-zA-Z\d]+(?:-|_[a-zA-Z\d]+)*)*`. as
 * defined in [Identifier]. It can be obtained through the [keyRegex()][Translator.keyRegex] method.
 *
 * @see Identifier
 * @see Translator
 */
typealias TranslationKey = String
