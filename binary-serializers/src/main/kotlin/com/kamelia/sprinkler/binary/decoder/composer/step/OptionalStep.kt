package com.kamelia.sprinkler.binary.decoder.composer.step

import com.kamelia.sprinkler.binary.decoder.Decoder
import com.kamelia.sprinkler.binary.decoder.DecoderDataInput
import com.kamelia.sprinkler.binary.decoder.NoOpDecoder
import com.kamelia.sprinkler.binary.decoder.composer.ComposedDecoderElementsAccumulator
import com.zwendo.restrikt.annotation.PackagePrivate

@PackagePrivate
internal class OptionalStep private constructor() : CompositionStep() {

    override fun decoder(context: ComposedDecoderElementsAccumulator) = NoOpDecoder

    override val storeResult: Boolean
        get() = false

    companion object {

        fun create(nullabilityDecoder: Decoder<Boolean>): Pair<CompositionStep, CompositionStep> {
            val optionalStep = OptionalStep()
            val nullabilityStep = optionalStep.NullabilityStep(nullabilityDecoder)
            return nullabilityStep to optionalStep
        }

    }

    private inner class NullabilityStep(nullabilityDecoder: Decoder<Boolean>) : CompositionStep() {

        private var isNull = false

        private val decoder = object : Decoder<Boolean> {
            override fun decode(input: DecoderDataInput): Decoder.State<Boolean> =
                nullabilityDecoder.decode(input).ifDone { isNull = !it }

            override fun reset() = nullabilityDecoder.reset()
        }

        override fun decoder(context: ComposedDecoderElementsAccumulator): Decoder<*> = decoder

        override fun nextStepCalculator(previous: NextStepCalculator?): NextStepCalculator =
            NextStepCalculator(previous) { _, accumulator ->
                if (isNull) {
                    accumulator.add(null)
                    StepTransition.GoTo(this@OptionalStep.index + 1)
                } else {
                    StepTransition.Increment(true)
                }
            }

        override val storeResult: Boolean
            get() = false

    }
}
