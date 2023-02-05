@file:JvmName("ComposedDecoder")

package com.kamelia.sprinkler.binary.decoder.composer

import com.kamelia.sprinkler.binary.decoder.Decoder
import com.kamelia.sprinkler.binary.decoder.DecoderDataInput
import com.kamelia.sprinkler.binary.decoder.composer.step.CompositionStepList

@JvmName("create")
inline fun <T> composedDecoder(block: DecoderComposer0<T>.() -> DecoderComposer1<T, T>): Decoder<T> =
    DecoderComposer0<T>()
        .block()
        .run { decoder ?: ComposedDecoderImpl(builder) } // decoder != null => composition contains only one step

@PublishedApi
internal class ComposedDecoderImpl<T>(builder: CompositionStepList.Builder) : Decoder<T> {

    private val steps: CompositionStepList = builder.build()
    private var index = steps.start
    private lateinit var accumulator: ComposedDecoderElementsAccumulator
    private var currentRecursion: RecursionNode? = null

    override fun decode(input: DecoderDataInput): Decoder.State<T> {
        if (!::accumulator.isInitialized) { // first call
            accumulator = ComposedDecoderElementsAccumulator()
            arriveAtStep(index)
        }

        while (true) {
            if (index >= steps.maxIndex) {
                if (currentRecursion == null) break
                val rec = currentRecursion!!
                index = rec.previousIndex
                currentRecursion = rec.previousNode
            }

            val currentStep = steps[index]
            val currentDecoder = currentStep.decoder(accumulator)

            when (val state = currentDecoder.decode(input)) {
                is Decoder.State.Done -> {
                    if (steps[index].storeResult) {
                        accumulator.add(state.value)
                    }

                    moveToNextStep()
                }
                else -> return state.mapEmptyState()
            }
        }

        return Decoder.State.Done(@Suppress("UNCHECKED_CAST") (accumulator.pop() as T))
    }

    override fun reset() {
        steps[index].reset()
        index = steps.start
        accumulator = ComposedDecoderElementsAccumulator()
        arriveAtStep(index)
    }

    private fun arriveAtStep(index: Int) {
        val step = steps[index]
        val returnedIndex = step.onArrive(accumulator, index)

        if (returnedIndex != index) {
            if (returnedIndex >= steps.maxIndex) { // last step
                this.index = returnedIndex
                return
            }
            arriveAtStep(returnedIndex)
        } else {
            this.index = returnedIndex
        }
    }

    private fun moveToNextStep() {
        val nextIndex = steps[index].onLeave(accumulator, index) ?: recurse()
        if (nextIndex >= steps.maxIndex) {
            index = nextIndex
            return
        }
        arriveAtStep(nextIndex)
    }

    private fun recurse(): Int {
        currentRecursion = RecursionNode(index + 1, currentRecursion)
        return steps.start
    }

    private class RecursionNode(
        @JvmField
        val previousIndex: Int,
        @JvmField
        val previousNode: RecursionNode?,
    )

}