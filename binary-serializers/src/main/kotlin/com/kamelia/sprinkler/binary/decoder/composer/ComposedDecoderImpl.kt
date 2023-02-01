package com.kamelia.sprinkler.binary.decoder.composer

import com.kamelia.sprinkler.binary.decoder.Decoder
import com.kamelia.sprinkler.binary.decoder.DecoderDataInput
import com.kamelia.sprinkler.binary.decoder.composer.step.CompositionStep
import com.kamelia.sprinkler.binary.decoder.composer.step.NextStepCalculator
import com.kamelia.sprinkler.binary.decoder.composer.step.StepTransition


@PublishedApi
internal class ComposedDecoderImpl<T>(
    steps: List<CompositionStep>,
) : Decoder<T> {

    private val steps = steps.mapIndexed { i, e -> e.setIndex(i) }

    private var index = 0
    private var accumulator = ComposedDecoderElementsAccumulator()
    private var currentDecoder: Decoder<*> = this.steps[0].decoder(accumulator)
    private var currentCalculator: NextStepCalculator? = this.steps[0].nextStepCalculator(null)

    override fun decode(input: DecoderDataInput): Decoder.State<T> {
        while (true) {
            println("$index ${steps[index]} $accumulator")
            when (val state = currentDecoder.decode(input)) {
                is Decoder.State.Done -> {
                    if (steps[index].storeResult) {
                        accumulator.add(state.value)
                    }
                    computeNextIndex()
                    if (index >= steps.size) {
                        if (currentCalculator?.onFinish?.invoke(accumulator) == false) {
                            index = currentCalculator!!.start + 1
                            currentDecoder = steps[index].decoder(accumulator)
                            currentCalculator = currentCalculator!!.previous
                            continue
                        }
                        return Decoder.State.Done(@Suppress("UNCHECKED_CAST") (accumulator.pop() as T))
                    }
                }
                else -> return state.mapEmptyState()
            }
        }
    }

    override fun reset() {
        index = 0

        accumulator = ComposedDecoderElementsAccumulator()

        currentDecoder.reset()
        currentDecoder = steps[0].decoder(accumulator)

        currentCalculator = steps[0].nextStepCalculator(null)
    }

    /**
     * Computes the next decoder's index in the list of steps using the current observer.
     */
    private fun computeNextIndex() {
        val stepCalculator = currentCalculator
        if (stepCalculator == null) {
            index++
        } else {
            val next = stepCalculator.computeTransition(index, accumulator)
            when (next) {
                is StepTransition.Increment -> index++
                is StepTransition.GoTo -> index = next.index
                is StepTransition.Rewind -> index = stepCalculator.start
            }
            if (next.pop) {
                currentCalculator = stepCalculator.previous
            }
        }

        if (index >= steps.size) return // no more steps

        currentDecoder = steps[index].decoder(accumulator)
        steps[index].nextStepCalculator(currentCalculator)?.let {
            currentCalculator = it
        }
    }

}
