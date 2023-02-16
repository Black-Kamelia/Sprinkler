package com.kamelia.sprinkler.binary.decoder.core

/**
 * Object that can be used to collect elements into a collection.
 *
 * It provides a supplier to create a new collection, an accumulator to add elements to the collection, and a finisher
 * to transform the collection into the final result.
 *
 * @constructor Creates a new [DecoderCollector].
 * @param supplier the supplier to create a new collection, a function that takes no arguments and returns a new,
 * mutable result container
 * @param accumulator the accumulator to add elements to the collection, a function that takes the collection and the
 * element to be added
 * @param finisher the finisher to transform the collection into the final result a function that takes the collection
 * and the index for the added element
 * @property supplier The supplier to create a new collection.
 * @property accumulator The accumulator to add elements to the collection.
 * @property finisher The finisher to transform the collection into the final result.
 * @param C the type of the collection where elements are accumulated
 * @param E the type of the elements to be accumulated
 * @param R the type of the result
 *
 */
data class DecoderCollector<C, in E, out R>(
    val supplier: () -> C,
    val accumulator: C.(E) -> Unit,
    val finisher: C.() -> R,
) {

    companion object {

        /**
         * Creates a [DecoderCollector] that accumulates the input elements into a new [List] and returns it.
         *
         * @return a [DecoderCollector] which collects all the input elements into a [List]
         */
        fun <E> toList(): DecoderCollector<MutableList<E>, E, List<E>> = DecoderCollector(
            { mutableListOf() },
            { add(it) },
            { this }
        )

        /**
         * Creates a [DecoderCollector] that accumulates the input elements into a new [Set] and returns it.
         *
         * @return a [DecoderCollector] which collects all the input elements into a [Set]
         */
        fun <E> toSet(): DecoderCollector<MutableSet<E>, E, Set<E>> = DecoderCollector(
            { mutableSetOf() },
            { add(it) },
            { this }
        )

        /**
         * Creates a [DecoderCollector] that accumulates [Pairs][Pair] into a [List] and returns a new [Map] with the
         * first element of the [Pair] as key and the second element as value.
         *
         * @return a [DecoderCollector] which collects all the input elements into a [Map]
         */
        fun <K, V> toMap(): DecoderCollector<MutableList<Pair<K, V>>, Pair<K, V>, Map<K, V>> = DecoderCollector(
            { mutableListOf() },
            { add(it) },
            { toMap() }
        )

        /**
         * Creates a [DecoderCollector] that accumulates the input elements into a [List] and returns a [Collection] [C]
         * with the elements of the [List].
         *
         * @param finisher the function that transforms the [List] into the [Collection] [C]
         * @return a [DecoderCollector] which collects all the input elements into a [Collection] [C]
         */
        inline fun <E, C : Collection<E>> toCollection(
            crossinline finisher: List<E>.() -> C
        ): DecoderCollector<MutableList<E>, E, Collection<E>> = DecoderCollector(
            { ArrayList() },
            { add(it) },
            { finisher() }
        )

        /**
         * Creates a [DecoderCollector] that accumulates the input elements into a [List] and returns an [Array] [E]
         * with the elements of the [List].
         *
         * @param factory the function that creates the [Array] [E] with the given size
         * @return a [DecoderCollector] which collects all the input elements into an [Array] [E]
         */
        fun <E> toArray(factory: (Int) -> Array<E?>): DecoderCollector<MutableList<E>, E, Array<E>> = DecoderCollector(
            { ArrayList() },
            { add(it) },
            {
                @Suppress("UNCHECKED_CAST")
                val array = factory(size) as Array<E>
                forEachIndexed { index, e -> array[index] = e }
                array
            }
        )

    }

}
