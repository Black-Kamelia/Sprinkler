package com.kamelia.sprinkler.codec.binary.bijection

import com.kamelia.sprinkler.codec.binary.decoder.core.Decoder
import com.kamelia.sprinkler.codec.binary.encoder.core.Encoder

interface Bijection<T> {

    val encoder: Encoder<T>

    val decoderFactory: () -> Decoder<T>

    fun decoder(): Decoder<T> = decoderFactory()

    operator fun component1(): Encoder<T> = encoder

    operator fun component2(): Decoder<T> = decoderFactory()

    companion object {

        @JvmStatic
        fun <T> of(encoder: Encoder<T>, decoderFactory: () -> Decoder<T>): Bijection<T> =
            BasicBijectionImpl(encoder, decoderFactory)

    }

}
