package com.kamelia.sprinkler.binary.decoder.composer.step

internal class CompositionStepList(
    private val list: List<CompositionStep>,
    private val firstRegularStepIndex: Int,
) {

    /*
        Negative indexes are used to access prefix steps.

        Example:

         0  1  2  3  4
        [A, B, C, D, E]
               ^ firstRegularStepIndex = 2

        final index = firstRegularStepIndex + index

        get(0)  = firstRegularStepIndex + 0 = 2             => C
        get(-1) = firstRegularStepIndex + (-1) = 2 - 1 = 1  => B
        get(-2) = firstRegularStepIndex + (-2) = 2 - 2 = 0  => A
    */

    operator fun get(index: Int): CompositionStep {
        require((index >= 0 && index < list.size) || (index < 0 && index >= -firstRegularStepIndex)) {
            "Index $index is out of bounds ([0, ${list.size}[)."
        }
        return list[firstRegularStepIndex + index]
    }

    val start: Int
        get() = -firstRegularStepIndex

    val maxIndex: Int
        get() = list.size - firstRegularStepIndex

    override fun toString(): String = list.joinToString("\n") { it.toString() }

    internal class Builder {

        private val steps = ArrayDeque<CompositionStep>()
        private var prefixStepCount = 0

        val nextPrefixIndex: Int
            get() = -prefixStepCount - 1

        val nextRegularIndex: Int
            get() = steps.size - prefixStepCount

        fun addStep(step: CompositionStep) {
            steps.add(step)
        }

        fun addPrefixStep(step: CompositionStep) {
            steps.addFirst(step)
            prefixStepCount++
        }

        fun build(): CompositionStepList = CompositionStepList(steps.toList(), prefixStepCount)

    }
}
