package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.i18n.formatting.VariableFormatter
import com.kamelia.sprinkler.i18n.pluralization.Plural
import com.kamelia.sprinkler.i18n.pluralization.PluralRuleProvider
import com.kamelia.sprinkler.i18n.pluralization.ScientificNotationNumber
import com.zwendo.restrikt2.annotation.PackagePrivate
import java.util.Locale

/**
 * Common supertype for all translation parameters. All parameters passed to the [Translator.t] method implement this
 * interface. This interface is sealed and cannot be implemented outside of this module.
 */
sealed interface TranslationArgument {

    companion object {

        /**
         * Creates an argument representing the locale to use when translating a key. When no locale is provided, the
         * [currentLocale][Translator.currentLocale] is used.
         *
         * The most common use case is to translate a key to a specific locale, like in the following example:
         *
         * ```
         * // content:
         * // {
         * //   "en": { "greetings": "Hello" },
         * //   "es": { "greetings": "Hola" }
         * // }
         * val translator: Translator = ...
         *
         * val value = translator.t("greetings", selectedLocale(Locale.ENGLISH))
         * println(value) // prints "Hello"
         * ```
         *
         * @param locale the locale to use
         * @return an argument representing the selected locale
         * @see Translator.currentLocale
         */
        @JvmStatic
        fun selectedLocale(locale: Locale): TranslationArgument = SelectedLocale(locale)

        /**
         * Creates an argument representing the locale to use as a fallback when translating a key or `null` to disable
         * fallback. When no locale is provided, the [defaultLocale][Translator.defaultLocale] is used.
         *
         * The most common use case is to provide a fallback locale when the translation for the selected locale is not
         * available, like in the following example:
         *
         * ```
         * // content:
         * // {
         * //   "en": { "greetings": "Hello" }
         * // }
         * val translator: Translator = ...
         *
         * val value = translator.t("greetings", selectedLocale(Locale.FRENCH), fallbackLocale(Locale.ENGLISH))
         * println(value) // prints "Hello"
         * ```
         *
         * @param locale the locale to use
         * @return an argument representing the fallback locale
         * @see Translator.defaultLocale
         */
        @JvmStatic
        fun fallbackLocale(locale: Locale?): TranslationArgument = FallbackLocale(locale)

        /**
         * Creates an argument representing the keys to use as fallback when translating a key. When no keys are
         * provided, no fallback key is used.
         *
         * The most common use case is to provide a list of keys to use as fallback when the translation for the
         * selected key is not available, like in the following example:
         *
         * ```
         * // content:
         * // {
         * //   "en": { "greetings": "Hello" }
         * // }
         * val translator: Translator = ...
         *
         * val value = translator.t("my_key", fallbacks("greetings"))
         * println(value) // prints "Hello"
         * ```
         *
         * @param keys the key to use
         * @return an argument representing the fallback key
         */
        @JvmStatic
        fun fallbacks(vararg keys: String): TranslationArgument = Fallbacks(keys)

        /**
         * Creates an argument representing the count value of the translation, used to disambiguate plural forms (in
         * conjunction with a [PluralRuleProvider]).
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
         * fun items(number: Long) {
         *    val value = translator.t(
         *        "item",
         *        count(number)
         *    )
         *    println(value)
         * }
         *
         * items(5) // prints "I have several items"
         * ```
         *
         * As shown in the example above, the plural value actually appends the value to the key, separated by an
         * underscore.
         *
         * **NOTE**: The plural value is appended to the key after the [context] (e.g., `key_male_one`).
         *
         * @param count the count value to be used
         * @return an argument representing the count
         * @see Plural
         */
        @JvmStatic
        fun count(count: Long): TranslationArgument = Count(count)

        /**
         * Creates an argument representing the count value of the translation, used to disambiguate plural forms (in
         * conjunction with a [PluralRuleProvider]).
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
         * fun items(number: Double) {
         *    val value = translator.t(
         *        "item",
         *        count(number)
         *    )
         *    println(value)
         * }
         *
         * items(2.5) // prints "I have several items"
         * ```
         *
         * As shown in the example above, the plural value actually appends the value to the key, separated by an
         * underscore.
         *
         * **NOTE**: The plural value is appended to the key after the [context] (e.g., `key_male_one`).
         *
         * @param count the count value to be used
         * @return an argument representing the count
         *
         * @see Plural
         */
        @JvmStatic
        fun count(count: Double): TranslationArgument = Count(count)

        /**
         * Creates an argument representing the count value of the translation, used to disambiguate plural forms (in
         * conjunction with a [PluralRuleProvider]).
         *
         * The most common use case is to disambiguate the plural form of a word, like in the following example:
         *
         * ```
         * // content:
         * // {
         * //   "item_zero": "I have no items",
         * //   "item_one": "I have {{count, number}} item",
         * //   "item_other": "I have {{count, number}} items"
         * // }
         * val translator: Translator = ...
         *
         * fun items(number: ScientificNotationNumber) {
         *    val value = translator.t(
         *        "item",
         *        count(number)
         *    )
         *    println(value)
         * }
         *
         * items(ScientificNotation.from(2.5)) // prints "I have several items"
         * ```
         *
         * As shown in the example above, the plural value actually appends the value to the key, separated by an
         * underscore.
         *
         * **NOTE**: The plural value is appended to the key after the [context] (e.g., `key_male_one`).
         *
         * @param count the count value to be used
         * @return an argument representing the count
         */
        @JvmStatic
        fun count(count: ScientificNotationNumber): TranslationArgument = Count(count)

        /**
         * Creates an argument representing the context of the translation. It can be used to disambiguate translations,
         * by adding a context to the key (e.g., `key` with context `male` becomes `key_male`).
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
         *         context(if (isMale) "male" else "female")
         *     )
         *     println(value)
         * }
         *
         * greetings(true) // prints "Hello mister"
         * ```
         *
         * As shown in the example above, the context actually appends the value to the key, separated by an underscore.
         *
         * **NOTE**: The context is appended to the key before the [plural][Plural] value which is
         * defined by the [count] option (e.g., a possible key with context and plural `key_male_one`).
         *
         * @param context the context value to be used
         * @return an argument representing the context
         *
         * @see Plural
         */
        @JvmStatic
        fun context(context: String): TranslationArgument = Context(context)

        /**
         * Creates an argument indicating whether the count value should be treated as an ordinal number. It can be used
         * to disambiguate ordinal forms (in conjunction with a [PluralRuleProvider]). This parameter is meaningful only if a
         * [count] parameter is also provided.
         *
         * The most common use case is to disambiguate the ordinal form of a word, like in the following example:
         *
         * ```
         * // content:
         * // {
         * //   "item_ordinal_one": "I arrived {{rank}}st",
         * //   "item_ordinal_two": "I arrived {{rank}}nd",
         * //   "item_ordinal_few": "I arrived {{rank}}rd",
         * //   "item_ordinal_other": "I arrived {{rank}}th"
         * // }
         * val translator: Translator = ...
         *
         * fun rank(count: Int) {
         *    val value = translator.t(
         *        "item",
         *        variable("rank", count),
         *        count(count),
         *        ordinal(true),
         *    )
         *    println(value)
         * }
         *
         * rank(3) // prints "I arrived 3rd"
         * ```
         *
         * As shown in the example above, the `ordinal` literal is appended to the key, separated by an underscore.
         *
         * **NOTE**: The value is right before the [plural][Plural] value (e.g., a possible key with
         * `key_male_ordinal_one`).
         *
         * @param value whether the count value should be treated as an ordinal number
         * @return an argument representing the ordinal value
         *
         * @see Plural
         * @see variable
         */
        @JvmStatic
        @JvmOverloads
        fun ordinal(value: Boolean = true): TranslationArgument = Ordinal(value)

        /**
         * Creates an argument representing a variable to be used in the translation. It can be used to replace a
         * placeholder in the translation string with a value.
         *
         * The most common use case is to replace a placeholder in the translation string with a value, like in the
         * following example:
         *
         * ```
         * // content:
         * // {
         * //   "greetings": "Hello, {{name}}!"
         * // }
         * val translator: Translator = ...
         *
         * val value = translator.t(
         *    "greetings",
         *    variable("name", "World"),
         * )
         * println(value) // prints "Hello, World!"
         * ```
         *
         * Additionally, this method also accepts a list of [VariableFormatter.Argument] to format the variable value.
         * They are passed to the [VariableFormatter.format] method.
         *
         * It can be used as shown below:
         *
         * ```
         * // content:
         * // {
         * //   "cost": "It costs {{price, currency}}!"
         * // }
         * val translator: Translator = ...
         *
         * // The current locale of the translator is Locale.US
         * val value = translator.t(
         *    "cost",
         *    variable("price", 5, formatArgument("minFracDigits", 1)),
         * )
         * println(value) // prints "It costs $5.0!"
         * ```
         *
         * **NOTE**: Format arguments passed through this method override the arguments written in the translation
         * string (only arguments with the same name are overridden).
         *
         * @param name the name of the variable
         * @param value the value of the variable
         * @param formatArguments the format arguments to be used
         * @return an argument representing the variable
         */
        @JvmStatic
        fun variable(
            name: String,
            value: Any,
            vararg formatArguments: VariableFormatter.Argument,
        ): TranslationArgument =
            Variable(name, value, formatArguments)

        //region Internal

        @PackagePrivate
        internal class SelectedLocale(val value: Locale) : TranslationArgument {

            override fun toString(): String = "selectedLocale=$value"
        }

        @PackagePrivate
        internal class FallbackLocale(val value: Locale?) : TranslationArgument {

            override fun toString(): String = "fallbackLocale=$value"
        }

        @PackagePrivate
        internal class Fallbacks(val value: Array<out String>) : TranslationArgument {

            override fun toString(): String = "fallbacks=${value.joinToString(", ", "[", "]") { "'$it'" }}"
        }

        @PackagePrivate
        internal class Count(val value: Any) : TranslationArgument {

            override fun toString(): String = "count=$value"
        }

        @PackagePrivate
        internal class Ordinal(val value: Boolean) : TranslationArgument {

            override fun toString(): String = "ordinal=${value}"
        }

        @PackagePrivate
        internal class Context(val value: String) : TranslationArgument {

            override fun toString(): String = "context='$value'"
        }

        @PackagePrivate
        internal class Variable(
            val name: String,
            val value: Any,
            val args: Array<out VariableFormatter.Argument>,
        ) : TranslationArgument {

            override fun toString(): String =
                "variable='$name' -> '$value' (args=${args.joinToString(", ", "[", "]") { "'$it'" }})"
        }

        //endregion

    }

}
