package com.kamelia.sprinkler.binary.decoder.composer.step

import com.kamelia.sprinkler.binary.decoder.composer.ElementsAccumulator
import com.kamelia.sprinkler.binary.decoder.core.Decoder
import com.kamelia.sprinkler.binary.decoder.core.DecoderInputData
import com.zwendo.restrikt.annotation.PackagePrivate

@PackagePrivate
internal class ThenItselfOrNullStep private constructor(nullabilityDecoder: Decoder<Boolean>) : CompositionStep {

    private var isNull = false

    override val storeResult: Boolean
        get() = false

    private val decoder = object : Decoder<Boolean> {

        override fun decode(input: DecoderInputData): Decoder.State<Boolean> =
            nullabilityDecoder.decode(input).ifDone { isNull = !it }

        override fun reset() = nullabilityDecoder.reset()

    }

    override fun decoder(accumulator: ElementsAccumulator): Decoder<*> = decoder

    override fun onLeave(accumulator: ElementsAccumulator, currentIndex: Int) = if (isNull) {
        accumulator.add(null)
        currentIndex + 1
    } else {
        accumulator.addStep()
        currentIndex // recurse
    }

    companion object {

        fun addStep(builder: CompositionStepList.Builder, nullabilityDecoder: Decoder<Boolean>) =
            builder.addStep(ThenItselfOrNullStep(nullabilityDecoder))

    }
}
