package com.kamelia.sprinkler.binary.decoder.composer.step

import com.kamelia.sprinkler.binary.decoder.Decoder
import com.kamelia.sprinkler.binary.decoder.DecoderDataInput
import com.kamelia.sprinkler.binary.decoder.composer.ComposedDecoderElementsAccumulator
import com.zwendo.restrikt.annotation.PackagePrivate

@PackagePrivate
internal class ReduceStep(private val reducer: ComposedDecoderElementsAccumulator.() -> Any?) : CompositionStep() {

    override fun decoder(context: ComposedDecoderElementsAccumulator): Decoder<*> = object : Decoder<Any?> {

        override fun decode(input: DecoderDataInput): Decoder.State<Any?> = Decoder.State.Done(reducer(context))

        override fun reset() {
            // nothing to do
        }

    }

}
