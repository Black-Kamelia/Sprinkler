package com.kamelia.sprinkler.binary.decoder

import java.io.InputStream

internal open class DecoderComposerImpl<T, I, O, D : AbstractDecoder<I, O>>(
    private val factory: ((I) -> O) -> D,
    private val decoder: D
) : DecoderComposer<T, D> {

    override fun <R, I2, O2, D2 : AbstractDecoder<I2, O2>> then(
        nextDecoder: D2,
        sideEffect: (R) -> Unit,
    ): IntermediateDecoderComposer<D> = factory {
        decoder.decode(it)
        sideEffect(nextDecoder.decode(it))
    }.let(::IntermediateDecoderComposerImpl)

    override fun <R> then(
        nextDecoder: () -> Decoder<R>,
        sideEffect: (R) -> Unit,
    ): IntermediateDecoderComposer<D> = Decoder {
        decoder.decode(it)
        sideEffect(nextDecoder().decode(it))
    }.let(::IntermediateDecoderComposerImpl)

    override fun map(block: (InputStream) -> Unit): IntermediateDecoderComposer<D> = Decoder {
        decoder.decode(it)
        block(it)
    }.let(::IntermediateDecoderComposerImpl)

    override fun <R> finally(resultMapper: (T) -> R): DecoderComposerImpl<R> = Decoder {
        resultMapper(decoder.decode(it))
    }.let(::DecoderComposerImpl)

    override fun <C, R> repeat(
        sizeReader: Decoder<Number>,
        collector: DecoderCollector<C, T, R>,
    ): DecoderComposerImpl<R> = Decoder { stream ->
        val collection = collector.supplier()
        val size = sizeReader.decode(stream).toInt()

        repeat(size) {
            val element = decoder.decode(stream)
            collector.accumulator(collection, element)
        }

        collector.finisher(collection)
    }.let(::DecoderComposerImpl)

    override fun repeat(sizeReader: Decoder<Number>): DecoderComposerImpl<List<T>> =
        repeat(sizeReader, DecoderCollector.list())

    override fun <C, R> repeat(
        amount: Number,
        collector: DecoderCollector<C, T, R>,
    ): DecoderComposerImpl<R> = Decoder { stream ->
        check(amount !is Float && amount !is Double) { "Amount must be of an integer type" }
        val collection = collector.supplier()

        for (i in 0 until amount.toLong()) {
            val element = decoder.decode(stream)
            collector.accumulator(collection, element)
        }
        collector.finisher(collection)
    }.let(::DecoderComposerImpl)

    override fun skip(size: Int): IntermediateDecoderComposer<D> = Decoder {
        decoder.decode(it)
        it.readNBytes(size)
        Unit
    }.let(::IntermediateDecoderComposerImpl)

    override fun repeat(amount: Number): DecoderComposerImpl<List<T>> = repeat(amount, DecoderCollector.list())

    override fun <C, R> repeatUntil(
        collector: DecoderCollector<C, T, R>,
        predicate: (T) -> Boolean,
    ): DecoderComposerImpl<R> = Decoder {
        val collection = collector.supplier()
        while (true) {
            val element = decoder.decode(it)

            if (!predicate(element)) break

            collector.accumulator(collection, element)
        }
        collector.finisher(collection)
    }.let(::DecoderComposerImpl)

    override fun repeatUntil(predicate: (T) -> Boolean): DecoderComposerImpl<List<T>> =
        repeatUntil(DecoderCollector.list(), predicate)

    override fun assemble(): Decoder<T> = decoder

}


private class IntermediateDecoderComposerImpl(
    decoder: Decoder<Unit>,
) : IntermediateDecoderComposer<D>, DecoderComposerImpl<Unit>(decoder)
