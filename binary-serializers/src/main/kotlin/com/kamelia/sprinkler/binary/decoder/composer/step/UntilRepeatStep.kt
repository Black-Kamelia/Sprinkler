package com.kamelia.sprinkler.binary.decoder.composer.step

import com.kamelia.sprinkler.binary.decoder.composer.ElementsAccumulator
import com.kamelia.sprinkler.binary.decoder.core.Decoder
import com.kamelia.sprinkler.utils.accumulate
import com.kamelia.sprinkler.utils.finish
import com.kamelia.sprinkler.utils.supply
import com.zwendo.restrikt.annotation.PackagePrivate
import java.util.stream.Collector

@PackagePrivate
internal class UntilRepeatStep<C, E, R> private constructor(
    private val collector: Collector<E, C, R>,
    private val addLast: Boolean,
    private val startIndex: Int,
    private val predicate: (E) -> Boolean,
) : CompositionStep {

    private var collection: C? = null
    private var size = 0

    override fun decoder(accumulator: ElementsAccumulator): Decoder<*> =
        throw AssertionError("Should not be called")

    override fun onArrive(accumulator: ElementsAccumulator, currentIndex: Int): Int {
        val collection = collection ?: collector.supply().also { collection = it }

        val element = accumulator.pop<E>()
        if (predicate(element)) {
            if (addLast) {
                collector.accumulate(collection, element)
            }
            accumulator.add(collector.finish(collection))
            reset()
            return currentIndex + 1
        }

        collector.accumulate(collection, element)
        return startIndex
    }

    override fun reset() {
        collection = null
        size = 0
    }

    companion object {

        fun <C, E, R> addStep(
            builder: CompositionStepList.Builder,
            collector: Collector<E, C, R>,
            addLast: Boolean,
            predicate: (E) -> Boolean,
        ) {
            val startIndex = builder.nextPrefixIndex + 1
            builder.addStep(UntilRepeatStep(collector, addLast, startIndex, predicate))
        }

    }

}
