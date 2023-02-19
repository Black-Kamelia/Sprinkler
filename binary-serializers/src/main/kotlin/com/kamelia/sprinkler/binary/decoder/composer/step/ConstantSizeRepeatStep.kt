package com.kamelia.sprinkler.binary.decoder.composer.step

import com.kamelia.sprinkler.binary.decoder.composer.ElementsAccumulator
import com.kamelia.sprinkler.utils.accumulate
import com.kamelia.sprinkler.utils.finish
import com.kamelia.sprinkler.utils.supply
import com.zwendo.restrikt.annotation.PackagePrivate
import java.util.stream.Collector

@PackagePrivate
internal class ConstantSizeRepeatStep<C, E, R> private constructor(
    private val collector: Collector<E, C, R>,
    private val times: Int,
    private val prefixIndex: Int,
) : CompositionStep {

    private var collection: C? = null
    private var index = 0

    init {
        require(times > 0) { "Times must be strictly positive" }
    }

    override fun decoder(accumulator: ElementsAccumulator) =
        throw AssertionError("Should not be called")

    override fun onArrive(accumulator: ElementsAccumulator, currentIndex: Int): Int {
        val collection = collection ?: collector.supply().also { this.collection = it }

        return when (index) {
            times - 1 -> { // last element
                collector.accumulate(collection, accumulator.pop())
                accumulator.add(collector.finish(collection))
                reset()

                currentIndex + 1 // directly go to next step
            }
            else -> { // any other element
                collector.accumulate(collection, accumulator.pop())
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
            collector: Collector<E, C, R>,
            times: Int,
        ) = if (times == 0) {
            builder.addPrefixStep(NoElementStep(collector, builder.nextRegularIndex))
        } else {
            builder.addStep(ConstantSizeRepeatStep(collector, times, builder.nextPrefixIndex))
        }
    }

    private class NoElementStep<C, E, R>(
        private val collector: Collector<E, C, R>,
        private val jumpIndex: Int,
    ) : CompositionStep {

        override fun decoder(accumulator: ElementsAccumulator) =
            throw AssertionError("Should not be called")

        override fun onArrive(accumulator: ElementsAccumulator, currentIndex: Int): Int {
            val collection = collector.supply()
            accumulator.add(collector.finish(collection))
            return jumpIndex + 1
        }

    }

}
