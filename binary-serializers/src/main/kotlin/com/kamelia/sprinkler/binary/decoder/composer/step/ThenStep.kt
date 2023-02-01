package com.kamelia.sprinkler.binary.decoder.composer.step

import com.kamelia.sprinkler.binary.decoder.Decoder
import com.kamelia.sprinkler.binary.decoder.composer.ComposedDecoderElementsAccumulator
import com.zwendo.restrikt.annotation.PackagePrivate

@PackagePrivate
internal class ThenStep(private val decoder: Decoder<*>) : CompositionStep() {

    override fun decoder(context: ComposedDecoderElementsAccumulator): Decoder<*> = decoder

}
