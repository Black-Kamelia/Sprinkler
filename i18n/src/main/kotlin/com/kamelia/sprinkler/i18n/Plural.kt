package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.i18n.Options.COUNT
import com.kamelia.sprinkler.util.assertionFailed
import com.kamelia.sprinkler.util.entryOf
import com.kamelia.sprinkler.util.unmodifiableMapOfEntriesArray
import com.kamelia.sprinkler.util.unsafeCast
import java.util.*
import java.util.stream.Collector

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
     *
     * Because several languages treat differently integers and floating point numbers, for these languages, calls like
     * `mapCardinal(1)` and `mapCardinal(1.0)` should return different values.
     */
    interface Mapper {

        fun mapCardinal(count: Double): Plural

        /**
         * Maps the given [count] to a [Plural] value. This method associates the parameters in
         * the context of a count of items.
         *
         * @param count the count to use
         * @return the [Plural] value
         * @throws IllegalArgumentException if the given [count] is negative
         */
        fun mapCardinal(count: Long): Plural = mapCardinal(count.toDouble())

        fun mapCardinal(count: Number): Plural =
            when (count) {
                is Byte,
                is Short,
                is Int,
                    -> mapCardinal(count.toLong())

                is Long -> mapCardinal(count)
                is Float -> mapCardinal(count.toDouble())
                is Double -> mapCardinal(count)
                else -> assertionFailed("Unsupported number type: ${count.javaClass}")
            }

        fun mapOrdinal(count: Double): Plural =
            error("Floating point numbers are not supported for ordinal numbers")

        /**
         * Maps the given [count] to a [Plural] value. This method associates the parameters in
         * the context of an ordinal number.
         *
         * @param count the count to use
         * @return the [Plural] value
         * @throws IllegalArgumentException if the given [count] is negative or zero
         */
        fun mapOrdinal(count: Long): Plural

        fun mapOrdinal(count: Number): Plural =
            when (count) {
                is Byte,
                is Short,
                is Int,
                    -> mapOrdinal(count.toLong())

                is Long -> mapOrdinal(count)
                is Float -> mapOrdinal(count.toDouble())
                is Double -> mapOrdinal(count)
                else -> assertionFailed("Unsupported number type: ${count.javaClass}")
            }

    }

    companion object {

        /**
         * The default [Mapper] implementation. This implementation always returns plural values as if the locale was
         * [English][Locale.ENGLISH].
         *
         * @return the default implementation of the [Mapper] interface
         */
        @JvmStatic
        fun englishMapper(): Mapper = object : Mapper {

            override fun mapCardinal(count: Double): Plural {
                require(count >= 0.0) { "count must be >= 0, but was $count" }
                return OTHER
            }

            override fun mapCardinal(count: Long): Plural {
                require(count >= 0L) { "count must be >= 0, but was $count" }
                return when (count) {
                    1L -> ONE
                    else -> OTHER
                }
            }

            override fun mapOrdinal(count: Long): Plural {
                require(count >= 1L) { "count must be >= 1, but was $count" }
                return when (count % 10) {
                    1L -> ONE
                    2L -> TWO
                    3L -> FEW
                    else -> OTHER
                }
            }

            override fun toString(): String = "Plural.englishMapper()"

        }

        @JvmStatic
        fun builtinMapper(locale: Locale): Mapper = BuiltinPluralMappers.factory()(locale)

        @JvmStatic
        fun builtinMappers(locales: Set<Locale>): Map<Locale, Mapper> {
            val factory = BuiltinPluralMappers.factory()
            var i = 0
            return locales
                .stream()
                .map { entryOf(it, factory(it)) }
                .collect(
                    Collector.of(
                        {
                            arrayOfNulls<Map.Entry<Locale, Mapper>>(locales.size)
                                .unsafeCast<Array<Map.Entry<Locale, Mapper>>>()
                        },
                        { acc, e -> acc[i++] = e },
                        { a, b -> a + b },
                        { a -> unmodifiableMapOfEntriesArray(a) }
                    )
                )
        }


    }

    /**
     * Used to create a translation key with the plural value.
     */
    internal val representation: String
        get() = name.lowercase()

}
