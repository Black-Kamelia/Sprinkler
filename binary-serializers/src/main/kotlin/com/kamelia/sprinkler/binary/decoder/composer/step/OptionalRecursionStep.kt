package com.kamelia.sprinkler.binary.decoder.composer.step

import com.kamelia.sprinkler.binary.decoder.Decoder
import com.kamelia.sprinkler.binary.decoder.DecoderDataInput
import com.kamelia.sprinkler.binary.decoder.composer.ComposedDecoderElementsAccumulator
import com.zwendo.restrikt.annotation.PackagePrivate

@PackagePrivate
internal class OptionalRecursionStep(nullabilityDecoder: Decoder<Boolean>) : CompositionStep() {

    private var isNull = false

    private val decoder = object : Decoder<Boolean> {

        override fun decode(input: DecoderDataInput): Decoder.State<Boolean> =
            nullabilityDecoder.decode(input).ifDone { isNull = !it }

        override fun reset() = nullabilityDecoder.reset()

    }

    override val storeResult: Boolean
        get() = false

    override fun decoder(context: ComposedDecoderElementsAccumulator): Decoder<*> = decoder

    override fun nextStepCalculator(previous: NextStepCalculator?): NextStepCalculator =
        NextStepCalculator(
            previous,
            { it.size == 1 },
        ) { current, accumulator ->
            when {
                current != index -> StepTransition.Increment(false)
                isNull -> {
                    accumulator.add(null)
                    StepTransition.Increment(true)
                }
                else -> {
                    accumulator.addStep()
                    StepTransition.GoTo(0, false)
                }
            }

        }

}
