package com.kamelia.sprinkler.binary.decoder.composer

import com.kamelia.sprinkler.binary.decoder.Decoder
import com.kamelia.sprinkler.binary.decoder.DecoderCollector
import com.kamelia.sprinkler.binary.decoder.NoOpDecoder
import com.kamelia.sprinkler.binary.decoder.composer.step.CompositionStep
import com.kamelia.sprinkler.binary.decoder.composer.step.CompositionSteps

abstract class DecoderComposer<B, T, D : DecoderComposer<B, T, D>> {

    @PublishedApi
    internal val steps: ArrayDeque<CompositionStep>

    internal constructor(decoder: Decoder<T>) {
        steps = ArrayDeque()
        thenStep(decoder)
    }

    protected constructor(previous: DecoderComposer<B, *, *>, decoder: Decoder<T>?) {
        steps = ArrayDeque(previous.steps)
        decoder?.let {
            if (it !== MarkerDecoder) {
                thenStep(decoder)
            }
        }
    }

    fun skip(amount: Long): D {
        steps += CompositionSteps.skip(amount)
        @Suppress("UNCHECKED_CAST")
        return this as D
    }

    @PublishedApi
    internal companion object {

    }

    //region Subclasses API

    protected fun <R> mapStep(block: (T) -> Decoder<R>) {
        steps += CompositionSteps.map(block)
    }

    protected fun <R> reduceStep(mapper: ComposedDecoderElementsAccumulator.() -> R) {
        steps += CompositionSteps.reduce(mapper)
    }

    protected fun finallyStep(block: ComposedDecoderElementsAccumulator.() -> B): Decoder<B> {
        steps += CompositionSteps.reduce(block)
        return ComposedDecoderImpl(steps)
    }

    protected fun <R> optionalRecursionStep(nullabilityDecoder: Decoder<Boolean>): Decoder<R> {
        steps += CompositionSteps.optionalRecursion(nullabilityDecoder)
        return MarkerDecoder
    }

    protected inline fun <R> thisCasted(block: () -> Unit): R {
        block()
        @Suppress("UNCHECKED_CAST")
        return this as R
    }

    //endregion

    //region Internal

    internal fun <C, R> repeatStep(collector: DecoderCollector<C, T, R>, sizeDecoder: Decoder<Int>) {
        val (sizeStep, repeatStep) = CompositionSteps.repeat(collector, sizeDecoder)
        steps.addFirst(sizeStep)
        steps += repeatStep
    }

    internal fun <C, R> repeatStep(times: Int, collector: DecoderCollector<C, T, R>) {
        steps += CompositionSteps.repeat(collector, times)
    }

    internal fun <C, R> untilStep(
        collector: DecoderCollector<C, T, R>,
        addLast: Boolean,
        predicate: (T) -> Boolean,
    ) {
        steps += CompositionSteps.until(collector, addLast, predicate)
    }

    internal fun optionalStep(nullabilityDecoder: Decoder<Boolean>) {
        val (nullabilityStep, dummyStep) = CompositionSteps.optional(nullabilityDecoder)
        steps.addFirst(nullabilityStep)
        steps += dummyStep
    }

    private fun <R> thenStep(decoder: Decoder<R>) {
        steps += CompositionSteps.then(decoder)
    }

    private object MarkerDecoder : Decoder<Nothing> by NoOpDecoder

    //endregion

}
