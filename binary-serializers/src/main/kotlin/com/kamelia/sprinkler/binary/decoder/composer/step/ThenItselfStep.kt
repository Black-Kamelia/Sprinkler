package com.kamelia.sprinkler.binary.decoder.composer.step

import com.kamelia.sprinkler.binary.decoder.Decoder
import com.kamelia.sprinkler.binary.decoder.composer.ComposedDecoderElementsAccumulator
import com.zwendo.restrikt.annotation.PackagePrivate

@PackagePrivate
internal class ThenItselfStep private constructor(
    nullabilityDecoder: Decoder<Boolean>,
) : AbstractOptionalStep(nullabilityDecoder) {

    override fun onLeave(accumulator: ComposedDecoderElementsAccumulator, currentIndex: Int) = if (isNull) {
        accumulator.add(null)
        currentIndex + 1
    } else {
        accumulator.addStep()
        null
    }

    companion object {

        fun addStep(builder: CompositionStepList.Builder, nullabilityDecoder: Decoder<Boolean>) =
            builder.addStep(ThenItselfStep(nullabilityDecoder))

    }
}
