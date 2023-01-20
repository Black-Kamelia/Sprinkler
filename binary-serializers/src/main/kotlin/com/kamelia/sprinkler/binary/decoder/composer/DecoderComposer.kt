package com.kamelia.sprinkler.binary.decoder.composer

import com.kamelia.sprinkler.binary.decoder.Decoder
import com.kamelia.sprinkler.binary.decoder.DecoderCollector
import com.kamelia.sprinkler.binary.decoder.IntDecoder
import com.zwendo.restrikt.annotation.HideFromJava

sealed interface DecoderComposer<T, D> {

    fun <R> map(block: (T) -> Decoder<R>): DecoderComposer<R, D>

    fun <R> andThen(nextDecoder: Decoder<R>, block: (T) -> Unit): DecoderComposer<R, D> = map {
        block(it)
        nextDecoder
    }

    fun <R> andThen(nextDecoder: () -> Decoder<R>, block: (T) -> Unit): DecoderComposer<R, D> = map {
        block(it)
        nextDecoder()
    }

    fun <R> andFinally(block: (T) -> R): DecoderComposer<R, D>

    fun <C, R> repeat(collector: DecoderCollector<C, T, R>, sizeDecoder: Decoder<Number>): DecoderComposer<R, D>

    fun <C, R> repeat(collector: DecoderCollector<C, T, R>): DecoderComposer<R, D> = repeat(collector, IntDecoder())

    fun repeat(sizeDecoder: Decoder<Number>): DecoderComposer<List<T>, D> =
        repeat(DecoderCollector.toList(), sizeDecoder)

    fun repeat(): DecoderComposer<List<T>, D> = repeat(IntDecoder())

    fun <C, R> repeat(
        times: Int,
        collector: DecoderCollector<C, T, R>,
    ): DecoderComposer<R, D>

    fun repeat(times: Int): DecoderComposer<List<T>, D> = repeat(times, DecoderCollector.toList())

    fun skip(amount: Long): DecoderComposer<T, D>

    fun <C, R> repeat(
        predicate: (T) -> Boolean,
        collector: DecoderCollector<C, T, R>,
        addLast: Boolean,
    ): DecoderComposer<R, D>

    fun repeat(predicate: (T) -> Boolean, addLast: Boolean): DecoderComposer<List<T>, D> =
        repeat(predicate, DecoderCollector.toList(), addLast)

    fun <C, R> repeat(predicate: (T) -> Boolean, collector: DecoderCollector<C, T, R>): DecoderComposer<R, D> =
        repeat(predicate, collector, false)

    fun repeat(predicate: (T) -> Boolean): DecoderComposer<List<T>, D> = repeat(predicate, false)

    fun assemble(): Decoder<T>

    companion object {

        @HideFromJava
        @JvmName("createWithContext")
        fun <T> create(decoder: Decoder<T>): DecoderComposer<T, Context0> =
            DecoderComposerImpl.createWithContext(decoder)

        @JvmName("create")
        fun <T> createWithoutContext(decoder: Decoder<T>): DecoderComposer<T, Unit> =
            DecoderComposerImpl.create(decoder)

        @HideFromJava
        fun <T, D> createFrom(composer: DecoderComposer<*, *>, next: Decoder<T>): DecoderComposer<T, D> =
            DecoderComposerImpl.create(composer as DecoderComposerImpl, next)

    }

}
