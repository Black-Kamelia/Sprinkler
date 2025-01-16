package com.kamelia.sprinkler.i18n.pluralization

import com.kamelia.sprinkler.util.illegalArgument
import java.util.Locale

/**
 * A mapper that associates a [Locale] and a [count][Int] to a [Plural] value.
 *
 * Because several languages treat differently integers and floating point numbers, for these languages, calls like
 * `mapCardinal(1)` and `mapCardinal(1.0)` should return different values.
 */
interface PluralMapper {

    /**
     * Maps the given [count] to a [Plural] value. This method associates the parameters in the context of a count
     * of items.
     *
     * @param count the count to use
     * @return the [Plural] value
     * @throws IllegalArgumentException if the given [count] is negative
     */
    fun mapCardinal(count: Double): Plural

    /**
     * Maps the given [count] to a [Plural] value. This method associates the parameters in the context of a count
     * of items.
     *
     * @param count the count to use
     * @return the [Plural] value
     * @throws IllegalArgumentException if the given [count] is negative
     */
    fun mapCardinal(count: Long): Plural = mapCardinal(count.toDouble())

    /**
     * Maps the given [count] to a [Plural] value. This method associates the parameters in the context of a count
     * of items.
     *
     * @param count the count to use
     * @return the [Plural] value
     * @throws IllegalArgumentException if the given [count] is negative
     * @throws IllegalArgumentException if the given [count] is not a known number
     */
    fun mapCardinal(count: Number): Plural =
        when (count) {
            is Byte,
            is Short,
            is Int,
                -> mapCardinal(count.toLong())

            is Long -> mapCardinal(count)
            is Float -> mapCardinal(count.toDouble())
            is Double -> mapCardinal(count)
            else -> illegalArgument("Unsupported number type: ${count.javaClass}")
        }

    /**
     * Maps the given [count] to a [Plural] value. This method associates the parameters in the context of a count
     * of items.
     *
     * @param count the count to use
     * @return the [Plural] value
     */
    fun mapCardinal(count: ScientificNotationNumber): Plural = mapCardinal(count.value)

    /**
     * Maps the given [count] to a [Plural] value. This method associates the parameters in the context of an
     * ordinal number.
     *
     * @param count the count to use
     * @return the [Plural] value
     * @throws IllegalArgumentException if the given [count] is negative or zero
     */
    fun mapOrdinal(count: Double): Plural {
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
    fun mapOrdinal(count: Long): Plural

    /**
     * Maps the given [count] to a [Plural] value. This method associates the parameters in the context of an
     * ordinal number.
     *
     * @param count the count to use
     * @return the [Plural] value
     * @throws IllegalArgumentException if the given [count] is negative or zero
     * @throws IllegalArgumentException if the given [count] is not a known number
     */
    fun mapOrdinal(count: Number): Plural =
        when (count) {
            is Byte,
            is Short,
            is Int,
                -> mapOrdinal(count.toLong())

            is Long -> mapOrdinal(count)
            is Float -> mapOrdinal(count.toDouble())
            is Double -> mapOrdinal(count)
            else -> illegalArgument("Unsupported number type: ${count.javaClass}")
        }

    /**
     * Maps the given [count] to a [Plural] value. This method associates the parameters in the context of an
     * ordinal number.
     *
     * @param count the count to use
     * @return the [Plural] value
     */
    fun mapOrdinal(count: ScientificNotationNumber): Plural = mapOrdinal(count.value)

    companion object {

        /**
         * A [PluralMapper] factory. This factory uses the plural rules defined by the
         * [Unicode CLDR](https://www.unicode.org/cldr/charts/45/supplemental/language_plural_rules.html) to create
         * the corresponding [PluralMapper] implementation.
         *
         * If the locale is not supported by the Unicode CLDR, the factory will throw an [IllegalArgumentException].
         *
         * Moreover, if the locale of the built mapper does not define some plural rules (e.g. no rules are defined for
         * the cardinal), an attempt to call the corresponding method will throw an [UnsupportedOperationException].
         *
         * @return a factory that creates [PluralMapper] instances based on the locale
         */
        @JvmStatic
        fun builtins(): (Locale) -> PluralMapper = BuiltinPluralMappers.factory()

    }

}
