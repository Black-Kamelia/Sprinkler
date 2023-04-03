package com.kamelia.sprinkler.codec.binary.decoder.composer

import com.kamelia.sprinkler.codec.binary.decoder.core.Decoder

/**
 * The initial composer, which is used to start the composition. It only has one method, [beginWith], which allow to
 * start composing by adding a decoder to the composition.
 */
class DecoderComposer0<B> @PublishedApi internal constructor() : DecoderComposer<B, Nothing, DecoderComposer0<B>>() {

    /**
     * Adds a decoder to the composition.
     */
    fun <R> beginWith(decoder: Decoder<R>): DecoderComposer1<B, R> = DecoderComposer1(this, decoder)

}
