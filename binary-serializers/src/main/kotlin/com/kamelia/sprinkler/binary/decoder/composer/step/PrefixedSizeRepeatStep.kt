package com.kamelia.sprinkler.binary.decoder.composer.step

import com.kamelia.sprinkler.binary.decoder.Decoder
import com.kamelia.sprinkler.binary.decoder.DecoderCollector
import com.kamelia.sprinkler.binary.decoder.DecoderDataInput
import com.kamelia.sprinkler.binary.decoder.composer.ComposedDecoderElementsAccumulator
import com.zwendo.restrikt.annotation.PackagePrivate

@PackagePrivate
internal class PrefixedSizeRepeatStep<C, E, R> private constructor(
    private val collector: DecoderCollector<C, E, R>,
    private val prefixIndex: Int,
) : CompositionStep {

    private var collection: C? = null
    private var size = -1
    private var index = -1

    override fun decoder(accumulator: ComposedDecoderElementsAccumulator): Decoder<*> =
        throw AssertionError("Should not be called")

    override fun onArrive(accumulator: ComposedDecoderElementsAccumulator, currentIndex: Int): Int {
        if (size == -1) throw AssertionError("Size should have been initialized")

        val collection = collection ?: collector.supplier(size).also { this.collection = it }

        return when {
            size == 0 -> {  // empty collection
                accumulator.add(collector.finisher(collection))
                reset()

                currentIndex + 1 // directly go to next step
            }
            index == size - 1 -> { // last element
                collector.accumulator(collection, accumulator.pop(), index)
                accumulator.add(collector.finisher(collection))
                reset()

                currentIndex + 1 // directly go to next step
            }
            else -> { // any other element
                collector.accumulator(collection, accumulator.pop(), index)
                index++

                prefixIndex + 1 // rewind to the size decoder
            }
        }
    }

    private fun setSize(size: Int) {
        if (this.size != -1) throw AssertionError("Size should not have been initialized")
        require(size >= 0) { "Size must be positive" }
        this.size = size
        index = 0
    }

    override fun reset() {
        collection = null
        size = -1
        index = -1
    }

    companion object {

        fun <C, E, R> addStep(
            builder: CompositionStepList.Builder,
            sizeDecoder: Decoder<Int>,
            collector: DecoderCollector<C, E, R>,
        ) {
            val regular = PrefixedSizeRepeatStep(collector, builder.nextPrefixIndex)
            val prefix = regular.SizeStep(sizeDecoder, builder.nextRegularIndex)

            builder.addPrefixStep(prefix)
            builder.addStep(regular)
        }

    }

    private inner class SizeStep(
        inner: Decoder<Int>,
        private val regularIndex: Int,
    ) : CompositionStep {

        private val proxy = object : Decoder<Int> {

            override fun decode(input: DecoderDataInput): Decoder.State<Int> =
                inner.decode(input).ifDone(this@PrefixedSizeRepeatStep::setSize)

            override fun reset() = Unit

        }

        override fun decoder(accumulator: ComposedDecoderElementsAccumulator): Decoder<*> = proxy

        override val storeResult: Boolean
            get() = false

        override fun onLeave(accumulator: ComposedDecoderElementsAccumulator, currentIndex: Int): Int =
            if (this@PrefixedSizeRepeatStep.size == 0) { // short-circuit for empty collection
                regularIndex
            } else {
                currentIndex + 1
            }
    }

}

