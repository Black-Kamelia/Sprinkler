package com.kamelia.sprinkler.binary.decoder

import java.io.InputStream

interface DecoderComposer<T> {

    fun <R> then(nextDecoder: Decoder<R>, sideEffect: (R) -> Unit = {}): Intermediate

    fun <R> then(nextDecoder: () -> Decoder<R>, sideEffect: (R) -> Unit = {}): Intermediate

    fun <R> map(block: (T) -> Decoder<R>): DecoderComposer<R>

    @JvmName("andFinally")
    @Suppress("INAPPLICABLE_JVM_NAME")
    fun <R> finally(resultMapper: (T) -> R): DecoderComposer<R>

    fun <C, R> repeat(
        sizeReader: Decoder<Number> = IntDecoder(),
        collector: DecoderCollector<C, T, R>,
    ): DecoderComposer<R>

    fun repeat(sizeReader: Decoder<Number> = IntDecoder()): DecoderComposer<List<T>>

    fun <C, R> repeat(amount: Number, collector: DecoderCollector<C, T, R>): DecoderComposer<R>

    fun skip(size: Int): Intermediate

    fun repeat(amount: Int): DecoderComposer<List<T>> = repeat(amount, DecoderCollector.toList())

    fun <C, R> repeatUntil(collector: DecoderCollector<C, T, R>, predicate: (T) -> Boolean): DecoderComposer<R>

    fun repeatUntil(predicate: (T) -> Boolean): DecoderComposer<List<T>>

    fun assemble(): Decoder<T>

    interface Intermediate : DecoderComposer<Unit>

    companion object {

        fun <T> new(decoder: Decoder<T>): DecoderComposer<T> = TODO()//DecoderComposerImpl(decoder)

    }

}

