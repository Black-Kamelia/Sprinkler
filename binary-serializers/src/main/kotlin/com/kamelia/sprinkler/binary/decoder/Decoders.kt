package com.kamelia.sprinkler.binary.decoder


data class Collector<C, E, R>(val supplier: () -> C, val accumulator: C.(E) -> Unit, val finisher: C.() -> R) {

    companion object {

        fun <E> list(): Collector<MutableList<E>, E, List<E>> = Collector(
            { mutableListOf() },
            { add(it) },
            { this }
        )

    }

}

fun <C, E, R> Decoder<E>.repeat(
    elementDecoder: Decoder<E>,
    sizeDecoder: Decoder<Int> = IntDecoder(),
    collector: Collector<C, E, R>,
): Decoder<R> = Decoder { stream ->
    val size = sizeDecoder.decode(stream)
    val collection = collector.supplier()
    val accumulator = collector.accumulator
    repeat(size) {
        collection.accumulator(elementDecoder.decode(stream))
    }

    collector.finisher(collection)
}
