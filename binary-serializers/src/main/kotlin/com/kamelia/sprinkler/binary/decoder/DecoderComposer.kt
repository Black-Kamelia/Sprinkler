package com.kamelia.sprinkler.binary.decoder

import java.io.InputStream

internal class DecoderComposer<T>(private val decoder: Decoder<T>) {

    fun <R> then(
        nextDecoder: Decoder<R>,
        sideEffect: (R) -> Unit = {},
    ): DecoderComposer<Unit> = Decoder {
        decoder.decode(it)
        sideEffect(nextDecoder.decode(it))
    }.let(::DecoderComposer)

    fun <R> then(
        nextDecoder: () -> Decoder<R>,
        sideEffect: (R) -> Unit = {},
    ): DecoderComposer<Unit> = Decoder {
        decoder.decode(it)
        sideEffect(nextDecoder().decode(it))
    }.let(::DecoderComposer)

    fun map(block: (InputStream) -> Unit): DecoderComposer<Unit> = Decoder {
        decoder.decode(it)
        block(it)
    }.let(::DecoderComposer)

    fun <R> finally(resultMapper: (T) -> R): DecoderComposer<R> = Decoder {
        resultMapper(decoder.decode(it))
    }.let(::DecoderComposer)

    fun <C, R> repeat(
        sizeReader: Decoder<Number> = IntDecoder(),
        collector: DecoderCollector<C, T, R>,
    ): DecoderComposer<R> = Decoder { stream ->
        val collection = collector.supplier()
        val size = sizeReader.decode(stream).toInt()

        repeat(size) {
            val element = decoder.decode(stream)
            collector.accumulator(collection, element)
        }

        collector.finisher(collection)
    }.let(::DecoderComposer)

    fun repeat(sizeReader: Decoder<Number> = IntDecoder()): DecoderComposer<List<T>> =
        repeat(sizeReader, DecoderCollector.list())

    fun <C, R> repeat(
        amount: Number,
        collector: DecoderCollector<C, T, R>,
    ): DecoderComposer<R> = Decoder { stream ->
        check(amount !is Float && amount !is Double) { "Amount must be of an integer type" }
        val collection = collector.supplier()

        for (i in 0 until amount.toLong()) {
            val element = decoder.decode(stream)
            collector.accumulator(collection, element)
        }
        collector.finisher(collection)
    }.let(::DecoderComposer)

    fun skip(size: Int): DecoderComposer<Unit> = Decoder {
        decoder.decode(it)
        it.readNBytes(size)
        Unit
    }.let(::DecoderComposer)

    fun repeat(amount: Number): DecoderComposer<List<T>> = repeat(amount, DecoderCollector.list())

    fun <C, R> repeatUntil(
        collector: DecoderCollector<C, T, R>,
        predicate: (T) -> Boolean,
    ): DecoderComposer<R> = Decoder {
        val collection = collector.supplier()
        while (true) {
            val element = decoder.decode(it)

            if (!predicate(element)) break

            collector.accumulator(collection, element)
        }
        collector.finisher(collection)
    }.let(::DecoderComposer)

    fun repeatUntil(predicate: (T) -> Boolean): DecoderComposer<List<T>> =
        repeatUntil(DecoderCollector.list(), predicate)

    fun assemble(): Decoder<T> = decoder
}
