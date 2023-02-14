package com.kamelia.sprinkler.binary.decoder.core

data class DecoderCollector<C, in E, out R>(
    val supplier: () -> C,
    val accumulator: C.(E, Int) -> Unit,
    val finisher: C.() -> R,
) {

    companion object {

        fun <E> toList(): DecoderCollector<MutableList<E>, E, List<E>> = DecoderCollector(
            { mutableListOf() },
            { e, _ -> add(e) },
            { this }
        )

        fun <E> toSet(): DecoderCollector<MutableSet<E>, E, Set<E>> = DecoderCollector(
            { mutableSetOf() },
            { e, _ -> add(e) },
            { this }
        )

        fun <K, V> toMap(): DecoderCollector<MutableList<Pair<K, V>>, Pair<K, V>, Map<K, V>> = DecoderCollector(
            { mutableListOf() },
            { e, _ -> add(e) },
            { toMap() }
        )

        inline fun <E> toCollection(
            crossinline factory: () -> MutableCollection<E>
        ): DecoderCollector<MutableCollection<E>, E, Collection<E>> = DecoderCollector(
            { factory() },
            { e, _ -> add(e) },
            { this }
        )

        fun <E> toArray(factory: (Int) -> Array<E?>): DecoderCollector<MutableList<E>, E, Array<E>> = DecoderCollector(
            { ArrayList() },
            { e, _ -> add(e) },
            {
                @Suppress("UNCHECKED_CAST")
                val array = factory(size) as Array<E>
                forEachIndexed { index, e -> array[index] = e }
                array
            }
        )

    }

}
