package com.kamelia.sprinkler.i18n.pluralization

import com.kamelia.sprinkler.i18n.FunctionAdapter
import com.kamelia.sprinkler.util.illegalArgument
import com.zwendo.restrikt2.annotation.HideFromJava
import com.zwendo.restrikt2.annotation.HideFromKotlin
import java.util.Locale
import java.util.function.Function

/**
 * Type associating a [count][Int] to a [Plural] value in the context of an ordinal or cardinal number.
 *
 * Because several languages treat in a different way integers and floating point numbers, for these languages, calls
 * like `cardinal(1)` and `cardinal(1.0)` should return different values.
 */
interface PluralRuleProvider {

    /**
     * Maps the given [count] to a [Plural] value. This method associates the parameters in the context of an item
     * count.
     *
     * @param count the count to use
     * @return the [Plural] value
     * @throws IllegalArgumentException if the given [count] is negative
     */
    fun cardinal(count: Double): Plural

    /**
     * Maps the given [count] to a [Plural] value. This method associates the parameters in the context of an item
     * count.
     *
     * @param count the count to use
     * @return the [Plural] value
     * @throws IllegalArgumentException if the given [count] is negative
     */
    fun cardinal(count: Long): Plural = cardinal(count.toDouble())

    /**
     * Maps the given [count] to a [Plural] value. This method associates the parameters in the context of an item
     * count.
     *
     * @param count the count to use
     * @return the [Plural] value
     * @throws IllegalArgumentException if the given [count] is negative
     * @throws IllegalArgumentException if the given [count] is not a known number
     */
    fun cardinal(count: Number): Plural =
        when (count) {
            is Byte,
            is Short,
            is Int,
                -> cardinal(count.toLong())

            is Long -> cardinal(count)
            is Float -> cardinal(count.toDouble())
            is Double -> cardinal(count)
            else -> illegalArgument("Unsupported number type: ${count.javaClass}")
        }

    /**
     * Maps the given [count] to a [Plural] value. This method associates the parameters in the context of an item
     * count.
     *
     * @param count the count to use
     * @return the [Plural] value
     */
    fun cardinal(count: ScientificNotationNumber): Plural = cardinal(count.value)

    /**
     * Maps the given [count] to a [Plural] value. This method associates the parameters in the context of an
     * ordinal number.
     *
     * @param count the count to use
     * @return the [Plural] value
     * @throws IllegalArgumentException if the given [count] is negative or zero
     */
    fun ordinal(count: Double): Plural {
        require(count > 0) { "The count must be positive: $count" }
        return Plural.OTHER
    }

    /**
     * Maps the given [count] to a [Plural] value. This method associates the parameters in the context of an
     * ordinal number.
     *
     * @param count the count to use
     * @return the [Plural] value
     * @throws IllegalArgumentException if the given [count] is negative or zero
     */
    fun ordinal(count: Long): Plural

    /**
     * Maps the given [count] to a [Plural] value. This method associates the parameters in the context of an
     * ordinal number.
     *
     * @param count the count to use
     * @return the [Plural] value
     * @throws IllegalArgumentException if the given [count] is negative or zero
     * @throws IllegalArgumentException if the given [count] is not a known number
     */
    fun ordinal(count: Number): Plural =
        when (count) {
            is Byte,
            is Short,
            is Int,
                -> ordinal(count.toLong())

            is Long -> ordinal(count)
            is Float -> ordinal(count.toDouble())
            is Double -> ordinal(count)
            else -> illegalArgument("Unsupported number type: ${count.javaClass}")
        }

    /**
     * Maps the given [count] to a [Plural] value. This method associates the parameters in the context of an
     * ordinal number.
     *
     * @param count the count to use
     * @return the [Plural] value
     */
    fun ordinal(count: ScientificNotationNumber): Plural = ordinal(count.value)

    companion object {

        /**
         * A [PluralRuleProvider] factory. This factory uses the plural rules defined by the
         * [Unicode CLDR](https://www.unicode.org/cldr/charts/45/supplemental/language_plural_rules.html) to create
         * the corresponding [PluralRuleProvider] implementation.
         *
         * If the locale is not supported by the Unicode CLDR, the factory will throw an [IllegalArgumentException].
         *
         * Moreover, if the locale of the built provider does not define some plural rules (e.g., no rules are defined
         * for the cardinal), an attempt to call the corresponding method will throw an [UnsupportedOperationException].
         *
         * @return a factory that creates [PluralRuleProvider] instances based on the locale
         */
        @HideFromJava
        @JvmName("builtinsKt")
        fun builtins(): (Locale) -> PluralRuleProvider = internalBuiltins()

        //region Java mirror API

        /**
         * A [PluralRuleProvider] factory. This factory uses the plural rules defined by the
         * [Unicode CLDR](https://www.unicode.org/cldr/charts/45/supplemental/language_plural_rules.html) to create
         * the corresponding [PluralRuleProvider] implementation.
         *
         * If the locale is not supported by the Unicode CLDR, the factory will throw an [IllegalArgumentException].
         *
         * Moreover, if the locale of the built provider does not define some plural rules (e.g., no rules are defined
         * for the cardinal), an attempt to call the corresponding method will throw an [UnsupportedOperationException].
         *
         * @return a factory that creates [PluralRuleProvider] instances based on the locale
         */
        @HideFromKotlin
        @JvmName("builtins")
        fun builtinsJava(): Function<Locale, PluralRuleProvider> = internalBuiltins()

        //endregion

        internal fun internalBuiltins(): FunctionAdapter<Locale, PluralRuleProvider> = BuiltinPluralRule.factory()

    }

}
