package com.kamelia.sprinkler.binary.decoder.composer.step

import com.kamelia.sprinkler.binary.decoder.Decoder
import com.kamelia.sprinkler.binary.decoder.composer.ComposedDecoderElementsAccumulator
import com.zwendo.restrikt.annotation.PackagePrivate

@PackagePrivate
internal class OptionalStep private constructor(
    private val jumpIndex: Int,
    nullabilityDecoder: Decoder<Boolean>,
) : AbstractOptionalStep(nullabilityDecoder) {

    override fun onLeave(accumulator: ComposedDecoderElementsAccumulator, currentIndex: Int): Int = if (isNull) {
        accumulator.add(null)
        jumpIndex + 1
    } else {
        currentIndex + 1
    }

    companion object {

        fun addStep(builder: CompositionStepList.Builder, nullabilityDecoder: Decoder<Boolean>) =
            builder.addPrefixStep(OptionalStep(builder.nextRegularIndex, nullabilityDecoder))

    }

}
