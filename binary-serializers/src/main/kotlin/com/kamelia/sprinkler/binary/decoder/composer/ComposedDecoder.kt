@file:JvmName("ComposedDecoder")
package com.kamelia.sprinkler.binary.decoder.composer

import com.kamelia.sprinkler.binary.decoder.Decoder

@JvmName("create")
inline fun <T> composedDecoder(block: DecoderComposer0<T>.() -> DecoderComposer1<T, T>): Decoder<T> =
    DecoderComposer0<T>()
        .block()
        .run { ComposedDecoderImpl(steps) }
