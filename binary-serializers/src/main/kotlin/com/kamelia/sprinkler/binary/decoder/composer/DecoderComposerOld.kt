package com.kamelia.sprinkler.binary.decoder.composer

import com.kamelia.sprinkler.binary.decoder.Decoder
import com.kamelia.sprinkler.binary.decoder.DecoderCollector
import com.kamelia.sprinkler.binary.decoder.IntDecoder

sealed interface DecoderComposerOld<T, D> {

    fun <R> map(block: (T) -> Decoder<R>): DecoderComposerOld<R, D>

    fun skip(amount: Long): DecoderComposerOld<T, D>

    fun <C, R> repeat(collector: DecoderCollector<C, T, R>, sizeDecoder: Decoder<Number>): DecoderComposerOld<R, D>

    fun <C, R> repeat(collector: DecoderCollector<C, T, R>): DecoderComposerOld<R, D> = repeat(collector, IntDecoder())

    fun repeat(sizeDecoder: Decoder<Number>): DecoderComposerOld<List<T>, D> =
        repeat(DecoderCollector.toList(), sizeDecoder)

    fun repeat(): DecoderComposerOld<List<T>, D> = repeat(IntDecoder())

    fun <C, R> repeat(
        times: Int,
        collector: DecoderCollector<C, T, R>,
    ): DecoderComposerOld<R, D>

    fun repeat(times: Int): DecoderComposerOld<List<T>, D> = repeat(times, DecoderCollector.toList())

    fun <C, R> repeat(
        predicate: (T) -> Boolean,
        collector: DecoderCollector<C, T, R>,
        addLast: Boolean,
    ): DecoderComposerOld<R, D>

    fun repeat(predicate: (T) -> Boolean, addLast: Boolean): DecoderComposerOld<List<T>, D> =
        repeat(predicate, DecoderCollector.toList(), addLast)

    fun <C, R> repeat(predicate: (T) -> Boolean, collector: DecoderCollector<C, T, R>): DecoderComposerOld<R, D> =
        repeat(predicate, collector, false)

    fun repeat(predicate: (T) -> Boolean): DecoderComposerOld<List<T>, D> = repeat(predicate, false)

    fun assemble(): Decoder<T>

}
