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

        fun <E> toCollection(collection: MutableCollection<E>): DecoderCollector<MutableCollection<E>, E, MutableCollection<E>> = DecoderCollector(
            { collection },
            { e, _ -> add(e) },
            { this }
        )
    }

}
