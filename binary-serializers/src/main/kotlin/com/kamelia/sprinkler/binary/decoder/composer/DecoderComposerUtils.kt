package com.kamelia.sprinkler.binary.decoder.composer

import com.kamelia.sprinkler.binary.decoder.Decoder
import com.zwendo.restrikt.annotation.HideFromJava

@HideFromJava
object DecoderComposerUtils {

    private fun <T, R> mapDecoder(decoder: DecoderComposer<T, *>, block: (T) -> Decoder<R>): Decoder<R> =
        (decoder as DecoderComposerImpl<T, *>).run {
            mapDecoder {
                context?.add(it as Any)
                block(it)
            }
        }

    fun <T, R> thenDecoder(decoder: DecoderComposer<T, *>, nextDecoder: Decoder<R>): Decoder<R> =
        mapDecoder(decoder) { nextDecoder }

    fun <T, R> thenDecoder(decoder: DecoderComposer<T, *>, nextDecoder: () -> Decoder<R>): Decoder<R> =
        mapDecoder(decoder) { nextDecoder() }

    fun <T, R> finallyDecoder(decoder: DecoderComposer<T, *>, block: (T) -> R): Decoder<R> =
        (decoder as DecoderComposerImpl<T, *>).finallyDecoder(block)

    class ContextIterator(composer: DecoderComposer<*, *>) {
        private val context = (composer as DecoderComposerImpl).context ?: throw IllegalStateException("No context found")
        private var index = 0

        @Suppress("UNCHECKED_CAST")
        fun <T> next(): T {
            check(index < context.size) { "No more elements in context" }
            return context[index++] as T
        }

    }

}
