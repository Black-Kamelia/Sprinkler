package com.kamelia.sprinkler.binary.decoder.composer.step

import com.kamelia.sprinkler.binary.decoder.Decoder
import com.kamelia.sprinkler.binary.decoder.composer.ComposedDecoderElementsAccumulator
import com.zwendo.restrikt.annotation.PackagePrivate

@PackagePrivate
internal class MapStep<T>(private val factory: (T) -> Decoder<*>) : CompositionStep() {

    override fun decoder(context: ComposedDecoderElementsAccumulator): Decoder<*> = factory(context.pop())

}
