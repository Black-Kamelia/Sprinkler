package com.kamelia.sprinkler.binary.decoder.composer

import com.kamelia.sprinkler.binary.decoder.BooleanDecoder
import com.kamelia.sprinkler.binary.decoder.IntDecoder
import com.kamelia.sprinkler.binary.decoder.core.Decoder
import com.kamelia.sprinkler.binary.decoder.core.DecoderCollector
import com.kamelia.sprinkler.binary.decoder.mapResult

class DecoderComposer1<B, T> : DecoderComposer<B, T, DecoderComposer1<B, T>> {

    @PublishedApi
    internal var decoder: Decoder<T>?

    internal constructor(decoder: Decoder<T>) : super(decoder) {
        this.decoder = decoder
    }

    constructor(previous: DecoderComposer<B, *, *>) : super(previous) {
        decoder = null
    }

    fun <R> map(block: (T) -> Decoder<R>): DecoderComposer1<B, R> = casted { mapStep(block) }

    fun <R> mapAndStore(block: (T) -> Decoder<R>): DecoderComposer2<B, T, R> =
        DecoderComposer2(this, mapAndStoreStep(block))

    fun <R> then(decoder: Decoder<R>): DecoderComposer2<B, T, R> = DecoderComposer2(this, decoder)

    fun <E, R> then(decoder: Decoder<E>, mapper: (E) -> R): DecoderComposer2<B, T, R> =
        DecoderComposer2(this, decoder.mapResult(mapper))

    fun <C, R> repeat(times: Int, collector: DecoderCollector<C, T, R>): DecoderComposer1<B, R> = casted {
        repeatStep(times, collector)
    }

    fun repeat(times: Int): DecoderComposer1<B, List<T>> = repeat(times, DecoderCollector.toList())

    @JvmOverloads
    fun <C, R> repeat(
        collector: DecoderCollector<C, T, R>,
        sizeDecoder: Decoder<Int> = IntDecoder(),
    ): DecoderComposer1<B, R> = casted { repeatStep(collector, sizeDecoder) }

    @JvmOverloads
    fun repeat(sizeDecoder: Decoder<Int> = IntDecoder()): DecoderComposer1<B, List<T>> =
        repeat(DecoderCollector.toList(), sizeDecoder)

    @JvmOverloads
    fun <C, R> until(
        collector: DecoderCollector<C, T, R>,
        addLast: Boolean = false,
        predicate: (T) -> Boolean,
    ): DecoderComposer1<B, R> = casted { untilStep(collector, addLast, predicate) }

    @JvmOverloads
    fun until(addLast: Boolean = false, predicate: (T) -> Boolean): DecoderComposer1<B, List<T>> =
        until(DecoderCollector.toList(), addLast, predicate)

    @JvmOverloads
    fun optional(nullabilityDecoder: Decoder<Boolean> = BooleanDecoder()): DecoderComposer1<B, T?> = casted {
        optionalStep(nullabilityDecoder)
    }

    @JvmOverloads
    fun thenItselfOrNull(nullabilityDecoder: Decoder<Boolean> = BooleanDecoder()): DecoderComposer2<B, T, B?> =
        DecoderComposer2(this, thenItselfOrNullStep(nullabilityDecoder))

    private inline fun <R> casted(block: DecoderComposer1<B, T>.() -> Unit): DecoderComposer1<B, R> {
        block()
        decoder = null
        @Suppress("UNCHECKED_CAST")
        return this as DecoderComposer1<B, R>
    }

}
