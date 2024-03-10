package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.i18n.Options.COUNT
import java.util.Locale

/**
 * The plural value of the translation. It can be used to disambiguate translations depending on the number of
 * items.
 *
 * @see COUNT
 * @see TranslatorConfiguration.Builder.pluralMapper
 */
enum class Plural {

    /**
     * Plural value usually used in case the count is 0.
     */
    ZERO,

    /**
     * Plural value usually used in case the count is 1.
     */
    ONE,

    /**
     * Plural value usually used in case the count is 2.
     */
    TWO,

    /**
     * Plural value usually used in case the count represents a few items.
     */
    FEW,

    /**
     * Plural value usually used in case the count represents many items.
     */
    MANY,

    /**
     * Plural value used as default when no other value matches.
     */
    OTHER,

    ;

    /**
     * A mapper that associates a [Locale] and a [count][Int] to a [Plural] value.
     */
    interface Mapper {

        /**
         * Maps the given [count] to a [Plural] value for the given [locale]. This method associates the parameters in
         * the context of a count of items.
         *
         * @param locale the [Locale] to use
         * @param count the count to use
         * @return the [Plural] value
         * @throws IllegalArgumentException if the given [count] is negative
         */
        fun mapPlural(locale: Locale, count: Int): Plural

        /**
         * Maps the given [count] to a [Plural] value for the given [locale]. This method associates the parameters in
         * the context of an ordinal number.
         *
         * @param locale the [Locale] to use
         * @param count the count to use
         * @return the [Plural] value
         * @throws IllegalArgumentException if the given [count] is negative or zero
         */
        fun mapOrdinal(locale: Locale, count: Int): Plural

    }

    companion object {

        /**
         * The default [Mapper] implementation. This implementation always returns plural values as if the locale was
         * [English][Locale.ENGLISH].
         *
         * @return the default implementation of the [Mapper] interface
         */
        @JvmStatic
        fun defaultMapper(): Mapper = object : Mapper {

            override fun mapPlural(locale: Locale, count: Int): Plural {
                require(count >= 0) { "count must be >= 0, but was $count" }
                return when (count) {
                    1 -> ONE
                    else -> OTHER
                }
            }

            override fun mapOrdinal(locale: Locale, count: Int): Plural {
                require(count >= 1) { "count must be >= 1, but was $count" }
                return when (count % 10) {
                    1 -> ONE
                    2 -> TWO
                    3 -> FEW
                    else -> OTHER
                }
            }

            override fun toString(): String = "Plural.defaultMapper()"

        }

    }

}
