package com.kamelia.sprinkler.binary.decoder

import java.io.ByteArrayInputStream
import java.io.InputStream
import kotlin.properties.Delegates


data class DecoderCollector<C, in E, out R>(
    val supplier: () -> C,
    val accumulator: C.(E) -> Unit,
    val finisher: C.() -> R
) {

    companion object {

        fun <E> list(): DecoderCollector<MutableList<E>, E, List<E>> = DecoderCollector(
            { mutableListOf() },
            { add(it) },
            { this }
        )

    }

}

fun <C, E, R> Decoder<E>.repeat(
    collector: DecoderCollector<C, E, R>,
    sizeDecoder: Decoder<Int> = IntDecoder(),
): Decoder<R> = Decoder { stream ->
    val size = sizeDecoder.decode(stream)
    val collection = collector.supplier()
    val accumulator = collector.accumulator
    repeat(size) {
        collection.accumulator(decode(stream))
    }

    collector.finisher(collection)
}

fun <E> Decoder<E>.repeat(
    sizeDecoder: Decoder<Int> = IntDecoder(),
): Decoder<List<E>> = repeat(DecoderCollector.list(), sizeDecoder)

fun <C, E, R> Decoder<E>.repeat(
    amount: Int,
    collector: DecoderCollector<C, E, R>,
): Decoder<R> = Decoder { stream ->
    val collection = collector.supplier()
    val accumulator = collector.accumulator
    repeat(amount) {
        collection.accumulator(decode(stream))
    }

    collector.finisher(collection)
}

fun <E> Decoder<E>.repeat(
    amount: Int,
): Decoder<List<E>> = repeat(amount, DecoderCollector.list())

fun <E, R> Decoder<E>.then(
    nextDecoder: Decoder<R>,
    sideEffect: (E) -> Unit = {},
): Decoder<R> = Decoder { stream ->
    sideEffect(decode(stream))
    nextDecoder.decode(stream)
}

fun <E, R> Decoder<E>.then(
    nextDecoder: () -> Decoder<R>,
    sideEffect: (E) -> Unit = {},
): Decoder<R> = Decoder { stream ->
    sideEffect(decode(stream))
    nextDecoder().decode(stream)
}

fun <E> Decoder<E>.map(block: (E) -> Unit): Decoder<Unit> = Decoder { stream ->
    block(decode(stream))
}

fun <E, R> Decoder<E>.finally(resultMapper: (E) -> R): Decoder<R> = Decoder { stream ->
    resultMapper(decode(stream))
}

object NoOpDecoder : Decoder<Unit> {

    override fun decode(input: InputStream) = Unit

}


fun main() {
    var int by Delegates.notNull<Int>()
    lateinit var string: String
    lateinit var list: List<String>

    val decoder = IntDecoder()
        .then(UTF8StringDecoder()) { int = it }
        .then(UTF8StringDecoder().repeat(3)) { string = it }
        .finally { list = it }

    val stream = ByteArrayInputStream(byteArrayOf(
        0x00, 0x00, 0x00, 0x01,
        0x00, 0x00, 0x00, 0x03, 0x61, 0x62, 0x63,
        0x00, 0x00, 0x00, 0x03, 0x64, 0x65, 0x66,
        0x00, 0x00, 0x00, 0x03, 0x67, 0x68, 0x69,
        0x00, 0x00, 0x00, 0x03, 0x6A, 0x6B, 0x6C,
    ))

    decoder.decode(stream)
    println(int)
    println(string)
    println(list)
}
