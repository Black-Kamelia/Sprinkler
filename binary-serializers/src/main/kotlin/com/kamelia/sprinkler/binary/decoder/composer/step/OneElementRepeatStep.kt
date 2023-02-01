package com.kamelia.sprinkler.binary.decoder.composer.step

import com.kamelia.sprinkler.binary.decoder.Decoder
import com.kamelia.sprinkler.binary.decoder.DecoderCollector
import com.kamelia.sprinkler.binary.decoder.NoOpDecoder
import com.kamelia.sprinkler.binary.decoder.composer.ComposedDecoderElementsAccumulator
import com.zwendo.restrikt.annotation.PackagePrivate

/**
 * Step for a repeat step where the number of elements is always 1.
 */
@PackagePrivate
internal class OneElementRepeatStep<C, E, R>(
    private val collector: DecoderCollector<C, E, R>,
) : CompositionStep() {

    override fun decoder(context: ComposedDecoderElementsAccumulator): Decoder<*> = NoOpDecoder

    override val storeResult: Boolean
        get() = false

    override fun nextStepCalculator(previous: NextStepCalculator?): NextStepCalculator =
        NextStepCalculator(previous) { _, accumulator ->
            val c = collector.supplier(1)
            collector.accumulator(c, accumulator.pop(), 0)
            accumulator.add(collector.finisher(c))
            StepTransition.Increment(true)
        }

}
