package com.kamelia.sprinkler.binary.decoder.composer.step

import com.kamelia.sprinkler.binary.decoder.Decoder
import com.kamelia.sprinkler.binary.decoder.DecoderDataInput
import com.kamelia.sprinkler.binary.decoder.composer.ComposedDecoderElementsAccumulator
import com.zwendo.restrikt.annotation.PackagePrivate

@PackagePrivate
internal abstract class AbstractOptionalStep(
    nullabilityDecoder: Decoder<Boolean>
): CompositionStep {

    protected var isNull = false
        private set

    private val decoder = object : Decoder<Boolean> {

        override fun decode(input: DecoderDataInput): Decoder.State<Boolean> =
            nullabilityDecoder.decode(input).ifDone { isNull = !it }

        override fun reset() = nullabilityDecoder.reset()

    }

    final override val storeResult: Boolean
        get() = false

    final override fun decoder(accumulator: ComposedDecoderElementsAccumulator): Decoder<*> = decoder

    final override fun onArrive(accumulator: ComposedDecoderElementsAccumulator, currentIndex: Int): Int =
        super.onArrive(accumulator, currentIndex)

    abstract override fun onLeave(accumulator: ComposedDecoderElementsAccumulator, currentIndex: Int): Int?

}
