package com.kamelia.sprinkler.binary.decoder.composer.step

import com.kamelia.sprinkler.binary.decoder.DecoderCollector
import com.kamelia.sprinkler.binary.decoder.composer.ComposedDecoderElementsAccumulator
import com.zwendo.restrikt.annotation.PackagePrivate

@PackagePrivate
internal class ConstantSizeRepeatStep<C, E, R> private constructor(
    private val collector: DecoderCollector<C, E, R>,
    private val times: Int,
    private val prefixIndex: Int,
) : CompositionStep {

    private var collection: C? = null
    private var index = 0

    init {
        require(times > 0) { "Times must be strictly positive" }
    }

    override fun decoder(accumulator: ComposedDecoderElementsAccumulator) =
        throw AssertionError("Should not be called")

    override fun onArrive(accumulator: ComposedDecoderElementsAccumulator, currentIndex: Int): Int {
        val collection = collection ?: collector.supplier(times).also { this.collection = it }

        return when (index) {
            times - 1 -> { // last element
                collector.accumulator(collection, accumulator.pop(), index)
                accumulator.add(collector.finisher(collection))
                reset()

                currentIndex + 1 // directly go to next step
            }
            else -> { // any other element
                collector.accumulator(collection, accumulator.pop(), index)
                index++
                prefixIndex + 1
            }
        }
    }

    override fun reset() {
        collection = null
        index = 0
    }

    companion object {

        fun <C, E, R> addStep(
            builder: CompositionStepList.Builder,
            collector: DecoderCollector<C, E, R>,
            times: Int,
        ) = if (times == 0) {
            builder.addPrefixStep(NoElementStep(collector, builder.nextRegularIndex))
        } else {
            builder.addStep(ConstantSizeRepeatStep(collector, times, builder.nextPrefixIndex))
        }
    }

    private class NoElementStep<C, E, R>(
        private val collector: DecoderCollector<C, E, R>,
        private val jumpIndex: Int,
    ) : CompositionStep {

        override fun decoder(accumulator: ComposedDecoderElementsAccumulator) =
            throw AssertionError("Should not be called")

        override fun onArrive(accumulator: ComposedDecoderElementsAccumulator, currentIndex: Int): Int {
            val collection = collector.supplier(0)
            accumulator.add(collector.finisher(collection))
            return jumpIndex + 1
        }

    }

}
