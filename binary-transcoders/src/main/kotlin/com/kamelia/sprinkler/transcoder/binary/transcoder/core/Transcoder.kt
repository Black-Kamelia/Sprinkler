package com.kamelia.sprinkler.transcoder.binary.transcoder.core

import com.kamelia.sprinkler.transcoder.binary.decoder.core.Decoder
import com.kamelia.sprinkler.transcoder.binary.encoder.core.Encoder

/**
 * Represents an object that can both encode and decode values of type [T].
 *
 * @param T the type of values that can be encoded and decoded
 * @see Encoder
 * @see Decoder
 */
interface Transcoder<T> : Encoder<T>, Decoder<T> {

    companion object {

        /**
         * Creates a [Transcoder] from the given [encoder] and [decoder].
         *
         * @param encoder the encoder used to encode values of type [T]
         * @param decoder the decoder used to decode values of type [T]
         * @param T the type of values that can be encoded and decoded
         */
        @JvmStatic
        fun <T> create(encoder: Encoder<T>, decoder: Decoder<T>): Transcoder<T> =
            object : Transcoder<T>, Encoder<T> by encoder, Decoder<T> by decoder {}

    }

}
