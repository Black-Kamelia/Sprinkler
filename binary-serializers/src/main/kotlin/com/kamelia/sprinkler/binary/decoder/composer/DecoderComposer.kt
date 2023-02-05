package com.kamelia.sprinkler.binary.decoder.composer

import com.kamelia.sprinkler.binary.decoder.Decoder
import com.kamelia.sprinkler.binary.decoder.DecoderCollector
import com.kamelia.sprinkler.binary.decoder.NothingDecoder
import com.kamelia.sprinkler.binary.decoder.composer.step.CompositionStepList
import com.kamelia.sprinkler.binary.decoder.composer.step.addConstantSizeRepeatStep
import com.kamelia.sprinkler.binary.decoder.composer.step.addMapStep
import com.kamelia.sprinkler.binary.decoder.composer.step.addOptionalRecursionStep
import com.kamelia.sprinkler.binary.decoder.composer.step.addOptionalStep
import com.kamelia.sprinkler.binary.decoder.composer.step.addPrefixedSizeRepeatStep
import com.kamelia.sprinkler.binary.decoder.composer.step.addReduceStep
import com.kamelia.sprinkler.binary.decoder.composer.step.addSkipStep
import com.kamelia.sprinkler.binary.decoder.composer.step.addThenStep
import com.kamelia.sprinkler.binary.decoder.composer.step.addUntilRepeatStep

abstract class DecoderComposer<B, T, D : DecoderComposer<B, T, D>> {

    @PublishedApi
    internal val builder: CompositionStepList.Builder

    internal constructor(decoder: Decoder<T>) {
        builder = CompositionStepList.Builder()
        thenStep(decoder)
    }

    protected constructor(previous: DecoderComposer<B, *, *>, decoder: Decoder<T>) {
        builder = previous.builder
        if (decoder !== RecursionMarkerDecoder) {
            thenStep(decoder)
        }
    }

    internal constructor(previous: DecoderComposer<B, *, *>) {
        builder = previous.builder
    }

    fun skip(amount: Long): D {
        builder.addSkipStep(amount)
        @Suppress("UNCHECKED_CAST")
        return this as D
    }

    //region Subclasses API

    protected fun <R> mapStep(mapper: (T) -> Decoder<R>) = builder.addMapStep(mapper)

    protected fun <R> reduceStep(reducer: ComposedDecoderElementsAccumulator.() -> R) = builder.addReduceStep(reducer)

    protected fun finallyStep(block: ComposedDecoderElementsAccumulator.() -> B): Decoder<B> {
        builder.addReduceStep(block)
        return ComposedDecoderImpl(builder)
    }

    protected fun <R> optionalRecursionStep(nullabilityDecoder: Decoder<Boolean>): Decoder<R> {
        builder.addOptionalRecursionStep(nullabilityDecoder)
        return RecursionMarkerDecoder
    }

    protected inline fun <R> thisCasted(block: () -> Unit): R {
        block()
        @Suppress("UNCHECKED_CAST")
        return this as R
    }

    //endregion

    //region Internal

    internal fun <C, R> repeatStep(collector: DecoderCollector<C, T, R>, sizeDecoder: Decoder<Int>) =
        builder.addPrefixedSizeRepeatStep(collector, sizeDecoder)

    internal fun <C, R> repeatStep(times: Int, collector: DecoderCollector<C, T, R>) =
        builder.addConstantSizeRepeatStep(times, collector)

    internal fun <C, R> untilStep(
        collector: DecoderCollector<C, T, R>,
        addLast: Boolean,
        predicate: (T) -> Boolean,
    ) = builder.addUntilRepeatStep(collector, addLast, predicate)

    internal fun optionalStep(nullabilityDecoder: Decoder<Boolean>) = builder.addOptionalStep(nullabilityDecoder)

    private fun <R> thenStep(decoder: Decoder<R>) = builder.addThenStep(decoder)

    private object RecursionMarkerDecoder : Decoder<Nothing> by NothingDecoder()

    //endregion

}
