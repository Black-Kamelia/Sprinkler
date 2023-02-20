package com.kamelia.sprinkler.binary.decoder.composer.step

import com.kamelia.sprinkler.binary.decoder.composer.ElementsAccumulator
import com.kamelia.sprinkler.binary.decoder.core.Decoder
import com.zwendo.restrikt.annotation.PackagePrivate

@PackagePrivate
internal class ThenItselfStep private constructor(
    nullabilityDecoder: Decoder<Boolean>,
) : AbstractOptionalStep(nullabilityDecoder) {

    override fun onLeave(accumulator: ElementsAccumulator, currentIndex: Int) = if (isNull) {
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
