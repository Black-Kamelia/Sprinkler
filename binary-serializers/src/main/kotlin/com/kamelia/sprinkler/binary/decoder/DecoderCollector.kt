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

    }

}
