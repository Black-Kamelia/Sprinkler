package com.kamelia.sprinkler.binary.decoder

import com.kamelia.sprinkler.binary.decoder.stream.StreamDecoder
import java.io.InputStream


/**
 *
 * @param T decoded type
 * @param D decoder type
 */
sealed interface AbstractDecoderComposer<T, D : AbstractDecoder<*, *>> {

    fun <R> then(nextDecoder: Decoder<R>, sideEffect: (R) -> Unit = {}): IntermediateDecoderComposer<D>

    fun <R> then(nextDecoder: () -> Decoder<R>, sideEffect: (R) -> Unit = {}): IntermediateDecoderComposer<D>

    fun map(block: (InputStream) -> Unit): IntermediateDecoderComposer<D>

    fun <R> finally(resultMapper: (T) -> R): AbstractDecoderComposer<R, D>

    fun <C, R, S : AbstractDecoderComposer<R, D>> repeat(
        sizeReader: Decoder<Number> = IntDecoder(),
        collector: DecoderCollector<C, T, R>,
    ): S

    fun repeat(sizeReader: Decoder<Number> = IntDecoder()): AbstractDecoderComposer<List<T>, D>

    fun <C, R> repeat(amount: Number, collector: DecoderCollector<C, T, R>): AbstractDecoderComposer<R, D>

    fun skip(size: Int): IntermediateDecoderComposer<D>

    fun repeat(amount: Number): AbstractDecoderComposer<List<T>, D> = repeat(amount, DecoderCollector.list())

    fun <C, R> repeatUntil(collector: DecoderCollector<C, T, R>, predicate: (T) -> Boolean): AbstractDecoderComposer<R, D>

    fun repeatUntil(predicate: (T) -> Boolean): AbstractDecoderComposer<List<T>, D>

    fun assemble(): Decoder<T>

    companion object {

        fun <T> new(decoder: Decoder<T>): AbstractDecoderComposer<T, Decoder<T>> = TODO()//DecoderComposerImpl(decoder)

        fun <T> new(decoder: StreamDecoder<T>): AbstractDecoderComposer<T, Decoder<T>> = TODO()//DecoderComposerImpl(decoder)

    }

}

interface IDecoderComposer<T> : AbstractDecoderComposer<T, Decoder<T>> {

}

fun main() {
    lateinit var d: IDecoderComposer<Int>

    val a = d.repeat(5)
}
