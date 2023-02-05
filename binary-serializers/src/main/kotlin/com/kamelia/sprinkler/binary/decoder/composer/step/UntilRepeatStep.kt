package com.kamelia.sprinkler.binary.decoder.composer.step

import com.kamelia.sprinkler.binary.decoder.Decoder
import com.kamelia.sprinkler.binary.decoder.DecoderCollector
import com.kamelia.sprinkler.binary.decoder.composer.ComposedDecoderElementsAccumulator
import com.zwendo.restrikt.annotation.PackagePrivate

@PackagePrivate
internal class UntilRepeatStep<C, E, R> private constructor(
    private val collector: DecoderCollector<C, E, R>,
    private val addLast: Boolean,
    private val startIndex: Int,
    private val predicate: (E) -> Boolean,
) : CompositionStep {

    private var collection: C? = null
    private var size = 0

    override fun decoder(accumulator: ComposedDecoderElementsAccumulator): Decoder<*> =
        throw AssertionError("Should not be called")

    override fun onArrive(accumulator: ComposedDecoderElementsAccumulator, currentIndex: Int): Int {
        val collection = collection ?: collector.supplier(-1).also { collection = it }

        val element = accumulator.pop<E>()
        if (predicate(element)) {
            if (addLast) {
                collector.accumulator(collection, element, size)
            }
            accumulator.add(collector.finisher(collection))
            reset()
            return currentIndex + 1
        }

        collector.accumulator(collection, element, size)
        return startIndex
    }

    override fun reset() {
        collection = null
        size = 0
    }

    companion object {

        fun <C, E, R> addStep(
            builder: CompositionStepList.Builder,
            collector: DecoderCollector<C, E, R>,
            addLast: Boolean,
            predicate: (E) -> Boolean,
        ) {
            val startIndex = builder.nextPrefixIndex + 1
            builder.addStep(UntilRepeatStep(collector, addLast, startIndex, predicate))
        }

    }

}
