package com.kamelia.sprinkler.binary.decoder.composer.step

import com.kamelia.sprinkler.binary.decoder.Decoder
import com.kamelia.sprinkler.binary.decoder.DecoderCollector
import com.kamelia.sprinkler.binary.decoder.DecoderDataInput
import com.kamelia.sprinkler.binary.decoder.composer.ComposedDecoderElementsAccumulator
import com.zwendo.restrikt.annotation.PackagePrivate


/**
 * Step for a repeat step where the number of elements is not prefixed before the elements:
 * - Size is known at compile time
 * - The number of elements to read is not prefixed and the decoder reads until a certain condition is met
 */
@PackagePrivate
internal class NonPrefixedSizeRepeatStep<E, R> private constructor(
    private val repeatDecoder: RepeatDecoder<E, R>,
) : CompositionStep() {

    override fun decoder(context: ComposedDecoderElementsAccumulator): Decoder<*> = repeatDecoder

    override val storeResult: Boolean
        get() = repeatDecoder.isFull

    override fun nextStepCalculator(previous: NextStepCalculator?) =
        NextStepCalculator(previous) { index, accumulator ->
            when (index) {
                this.index -> { // occurs 2 times
                    if (repeatDecoder.isFull) { // second time when the decoder has been filled with all elements
                        StepTransition.Increment(true)
                    } else { // first time when the decoder is encountered for the first time
                        repeatDecoder.addElement(accumulator.pop())
                        StepTransition.Rewind()
                    }
                }
                this.index - 1 -> { // occurs n - 1 times, where n is the number of elements in the repeat
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

    companion object {

        fun <C, E, R> fromConstantSize(
            collector: DecoderCollector<C, E, R>,
            times: Int,
        ) = NonPrefixedSizeRepeatStep(ConstantSizeRepeatDecoder(collector, times))

        fun <C, E, R> fromUntil(
            collector: DecoderCollector<C, E, R>,
            addLast: Boolean,
            predicate: (E) -> Boolean,
        ) = NonPrefixedSizeRepeatStep(UntilDecoder(predicate, collector, addLast))

    }

    /**
     * Repeat decoder where the size is known at compile time
     */
    private class ConstantSizeRepeatDecoder<C, E, R>(
        collector: DecoderCollector<C, E, R>,
        times: Int,
    ) : DeterminedSizeRepeatDecoder<C, E, R>(collector) {

        init {
            this.times = times
        }

    }

    /**
     * Repeat decoder where the size is unknown and the elements are terminated when a predicate is met
     */
    private class UntilDecoder<C, E, R>(
        private val predicate: (E) -> Boolean,
        private val collector: DecoderCollector<C, E, R>,
        private val addLast: Boolean,
    ) : RepeatDecoder<E, R> {

        private var collection: C? = null

        override var isFull = false
            private set

        override fun addElement(element: E) {
            val collection = collection ?: collector.supplier(-1).also { this.collection = it }
            if (predicate(element)) {
                isFull = true
                if (addLast) {
                    collector.accumulator(collection, element, -1)
                }
            } else {
                collector.accumulator(collection, element, -1)
            }
        }

        override fun decode(input: DecoderDataInput): Decoder.State<R> {
            return if (collection == null) { // first encounter
                // not stored because the step call to store value
                Decoder.State.Done { throw AssertionError("Dummy return should never be read.") }
            } else { // second and last encounter
                Decoder.State.Done(collector.finisher(collection!!))
            }
        }

        override fun reset() {
            collection = null
            isFull = false
        }

    }

}
