package com.kamelia.sprinkler.binary.decoder

data class DecoderCollector<C, in E, out R>(
    val supplier: (Int) -> C,
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

        fun <E> toArray(): DecoderCollector<MutableList<E>, E, Array<E>> = DecoderCollector(
            { ArrayList() },
            { e, _ -> add(e) },
            {
                val array = arrayOfNulls<Any?>(size)
                forEachIndexed { index, e -> array[index] = e }
                @Suppress("UNCHECKED_CAST") (array as Array<E>)
            }
        )

    }

}
