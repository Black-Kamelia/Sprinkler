package com.kamelia.sprinkler.binary.decoder.composer.step

import com.kamelia.sprinkler.binary.decoder.Decoder
import com.kamelia.sprinkler.binary.decoder.DecoderCollector
import com.kamelia.sprinkler.binary.decoder.DecoderDataInput
import com.kamelia.sprinkler.binary.decoder.composer.ComposedDecoderElementsAccumulator
import com.zwendo.restrikt.annotation.PackagePrivate

@PackagePrivate
internal class PrefixedSizeRepeatStep private constructor(
    private val repeatDecoder: RepeatDecoder<*, *>,
) : CompositionStep() {

    override fun decoder(context: ComposedDecoderElementsAccumulator): Decoder<*> = repeatDecoder

    companion object {

        fun <C, E, R> create(
            sizeDecoder: Decoder<Int>,
            collector: DecoderCollector<C, E, R>,
        ): Pair<CompositionStep, CompositionStep> {
            val repeatDecoder = PrefixedSizeRepeatDecoder(collector)

            val proxy = object : Decoder<Int> {
                override fun decode(input: DecoderDataInput): Decoder.State<Int> =
                    sizeDecoder.decode(input).ifDone { repeatDecoder.setRepetition(it) }

                override fun reset() = sizeDecoder.reset()
            }

            val repeatStep = PrefixedSizeRepeatStep(repeatDecoder)
            val sizeDecodingStep = PrefixedSizeDecodingStep(proxy, repeatDecoder, repeatStep, collector)

            return sizeDecodingStep to repeatStep
        }

    }


    private class PrefixedSizeDecodingStep<C, E, R>(
        private val sizeDecoder: Decoder<Int>,
        private val repeatDecoder: PrefixedSizeRepeatDecoder<C, E, R>,
        private val repeatStep: PrefixedSizeRepeatStep,
        private val collector: DecoderCollector<C, E, R>,
    ) : CompositionStep() {

        override fun decoder(context: ComposedDecoderElementsAccumulator): Decoder<*> = sizeDecoder

        override fun nextStepCalculator(previous: NextStepCalculator?): NextStepCalculator =
            NextStepCalculator(previous) { index, accumulator ->
                when {
                    index == this.index && repeatDecoder.times == 0 -> { // empty collection
                        val c = collector.supplier(0)
                        accumulator.add(collector.finisher(c))
                        StepTransition.GoTo(repeatStep.index + 1)
                    }
                    index == repeatStep.index - 1 -> { // index before the repeat step
                        repeatDecoder.addElement(accumulator.pop())
                        if (repeatDecoder.isFull) {
                            StepTransition.Increment(true)
                        } else {
                            StepTransition.Rewind()
                        }
                    }
                    else -> StepTransition.Increment(false)
                }
            }

        override val storeResult: Boolean
            get() = false

    }

    /**
     * Repeat decoder where the size is prefixed to the elements
     */
    private class PrefixedSizeRepeatDecoder<C, E, R>(
        collector: DecoderCollector<C, E, R>,
    ) : DeterminedSizeRepeatDecoder<C, E, R>(collector) {

        override fun reset() {
            times = -1
            super.reset()
        }

        fun setRepetition(count: Int) {
            times = count
        }

    }

}

