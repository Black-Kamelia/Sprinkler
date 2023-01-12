package com.kamelia.sprinkler.binary.decoder

import com.kamelia.sprinkler.binary.decoder.stream.StreamDecoder
import java.io.InputStream

interface DecoderComposer<T, D : AbstractDecoder<*, *>> {

    fun <R> then(nextDecoder: Decoder<R>, sideEffect: (R) -> Unit = {}): IntermediateDecoderComposer<D>

    fun <R> then(nextDecoder: () -> Decoder<R>, sideEffect: (R) -> Unit = {}): IntermediateDecoderComposer<D>

    fun map(block: (InputStream) -> Unit): IntermediateDecoderComposer<D>

    fun <R> finally(resultMapper: (T) -> R): DecoderComposer<R, D>

    fun <C, R> repeat(
        sizeReader: Decoder<Number> = IntDecoder(),
        collector: DecoderCollector<C, T, R>,
    ): DecoderComposer<R, D>

    fun repeat(sizeReader: Decoder<Number> = IntDecoder()): DecoderComposer<List<T>, D>

    fun <C, R> repeat(amount: Number, collector: DecoderCollector<C, T, R>): DecoderComposer<R, D>

    fun skip(size: Int): IntermediateDecoderComposer<D>

    fun repeat(amount: Number): DecoderComposer<List<T>, D> = repeat(amount, DecoderCollector.list())

    fun <C, R> repeatUntil(collector: DecoderCollector<C, T, R>, predicate: (T) -> Boolean): DecoderComposer<R, D>

    fun repeatUntil(predicate: (T) -> Boolean): DecoderComposer<List<T>, D>

    fun assemble(): Decoder<T>

    companion object {

        fun <T> new(decoder: Decoder<T>): DecoderComposer<T, Decoder<T>> = TODO()//DecoderComposerImpl(decoder)

        fun <T> new(decoder: StreamDecoder<T>): DecoderComposer<T, Decoder<T>> = TODO()//DecoderComposerImpl(decoder)

    }

}

interface IntermediateDecoderComposer<D : AbstractDecoder<*, *>> : DecoderComposer<Unit, D>



fun main() {
    var age = 0
    lateinit var name: String

    val c = IntDecoder()
        .compose { age = it }
        .then(UTF8StringDecoder()) { name = it }
        .finally { "$name is $age years old" }
        .assemble()

    val n = "Khimmy".toByteArray()
    val a = 25.toByte()
    val input = byteArrayOf(0, 0, 0, a, *n)
    c.decode(input.inputStream())
}
