package com.kamelia.sprinkler.binary.decoder.composer

import com.kamelia.sprinkler.binary.decoder.core.Decoder

class DecoderComposer0<B> @PublishedApi internal constructor() : DecoderComposer<B, Nothing, DecoderComposer0<B>>() {

    fun <R> beginWith(decoder: Decoder<R>): DecoderComposer1<B, R> = DecoderComposer1(this, decoder)

}
