package com.kamelia.sprinkler.binary.decoder.composer.step

import com.kamelia.sprinkler.binary.decoder.composer.ComposedDecoderElementsAccumulator

internal class NextStepCalculator(
    val previous: NextStepCalculator?,
    val onFinish: (ComposedDecoderElementsAccumulator) -> Boolean = { true },
    val computeTransition: (Int, ComposedDecoderElementsAccumulator) -> StepTransition
)

internal sealed class StepTransition(val pop: Boolean) {

    class Rewind(pop: Boolean = false) : StepTransition(pop)

    class Increment(pop: Boolean) : StepTransition(pop)

    class GoTo(val index: Int, pop: Boolean = false) : StepTransition(pop) {
        init {
            require(index >= 0) { "Index must be positive" }
        }
    }

}
