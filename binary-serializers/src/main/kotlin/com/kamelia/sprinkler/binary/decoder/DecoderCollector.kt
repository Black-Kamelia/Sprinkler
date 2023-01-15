package com.kamelia.sprinkler.binary.decoder

data class DecoderCollector<C, in E, out R>(
    val supplier: () -> C,
    val accumulator: C.(E) -> Unit,
    val finisher: C.() -> R,
) {

    companion object {

        fun <E> toList(): DecoderCollector<MutableList<E>, E, List<E>> = DecoderCollector(
            { mutableListOf() },
            { add(it) },
            { this }
        )

    }

}
