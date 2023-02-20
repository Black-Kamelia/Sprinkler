package com.kamelia.sprinkler.binary.decoder.composer

import com.kamelia.sprinkler.binary.decoder.BooleanDecoder
import com.kamelia.sprinkler.binary.decoder.IntDecoder
import com.kamelia.sprinkler.binary.decoder.core.Decoder
import com.kamelia.sprinkler.binary.decoder.mapResult
import java.util.stream.Collector
import java.util.stream.Collectors

class DecoderComposer1<B, T> : DecoderComposer<B, T, DecoderComposer1<B, T>> {

    @PublishedApi
    internal var decoder: Decoder<T>?

    internal constructor(previous: DecoderComposer0<B>, decoder: Decoder<T>) : super(previous, decoder) {
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

    fun <C, R> repeat(times: Int, collector: Collector<T, C, R>): DecoderComposer1<B, R> = casted {
        repeatStep(times, collector)
    }

    fun repeat(times: Int): DecoderComposer1<B, List<T>> = repeat(times, Collectors.toList())

    @JvmOverloads
    fun <C, R> repeat(
        collector: Collector<T, C, R>,
        sizeDecoder: Decoder<Int> = IntDecoder(),
    ): DecoderComposer1<B, R> = casted { repeatStep(collector, sizeDecoder) }

    @JvmOverloads
    fun repeat(sizeDecoder: Decoder<Int> = IntDecoder()): DecoderComposer1<B, List<T>> =
        repeat(Collectors.toList(), sizeDecoder)

    @JvmOverloads
    fun <C, R> until(
        collector: Collector<T, C, R>,
        addLast: Boolean = false,
        predicate: (T) -> Boolean,
    ): DecoderComposer1<B, R> = casted { untilStep(collector, addLast, predicate) }

    @JvmOverloads
    fun until(addLast: Boolean = false, predicate: (T) -> Boolean): DecoderComposer1<B, List<T>> =
        until(Collectors.toList(), addLast, predicate)

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
