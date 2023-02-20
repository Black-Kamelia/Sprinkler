package com.kamelia.sprinkler.binary.decoder.composer

import com.kamelia.sprinkler.binary.decoder.composer.step.CompositionStepList
import com.kamelia.sprinkler.binary.decoder.composer.step.addConstantSizeRepeatStep
import com.kamelia.sprinkler.binary.decoder.composer.step.addMapStep
import com.kamelia.sprinkler.binary.decoder.composer.step.addOptionalStep
import com.kamelia.sprinkler.binary.decoder.composer.step.addPrefixedSizeRepeatStep
import com.kamelia.sprinkler.binary.decoder.composer.step.addReduceStep
import com.kamelia.sprinkler.binary.decoder.composer.step.addSkipStep
import com.kamelia.sprinkler.binary.decoder.composer.step.addThenItselfOrNullStep
import com.kamelia.sprinkler.binary.decoder.composer.step.addThenStep
import com.kamelia.sprinkler.binary.decoder.composer.step.addUntilRepeatStep
import com.kamelia.sprinkler.binary.decoder.core.Decoder
import com.kamelia.sprinkler.binary.decoder.core.NothingDecoder
import java.util.stream.Collector

abstract class DecoderComposer<B, T, D : DecoderComposer<B, T, D>> {

    @PublishedApi
    internal val builder: CompositionStepList.Builder

    internal constructor() {
        builder = CompositionStepList.Builder()
    }

    protected constructor(previous: DecoderComposer<B, *, *>, decoder: Decoder<T>) {
        builder = previous.builder
        if (decoder !== MARKER_DECODER) {
            thenStep(decoder)
        }
    }

    internal constructor(previous: DecoderComposer<B, *, *>) {
        builder = previous.builder
    }

    fun skip(amount: Long): D = thisCasted { builder.addSkipStep(amount) }

    //region Subclasses API

    protected fun <R> mapStep(mapper: (T) -> Decoder<R>) = builder.addMapStep(true, mapper)

    protected fun <R> mapAndStoreStep(mapper: (T) -> Decoder<R>): Decoder<R> {
        builder.addMapStep(false, mapper)
        return MARKER_DECODER
    }

    protected fun <R> reduceStep(reducer: ElementsAccumulator.() -> R) = builder.addReduceStep(reducer)

    protected fun <R> thenItselfOrNullStep(nullabilityDecoder: Decoder<Boolean>): Decoder<R> {
        builder.addThenItselfOrNullStep(nullabilityDecoder)
        return MARKER_DECODER
    }

    protected inline fun <R> thisCasted(block: () -> Unit): R {
        block()
        @Suppress("UNCHECKED_CAST")
        return this as R
    }

    //endregion

    //region Internal

    internal fun <C, R> repeatStep(collector: Collector<T, C, R>, sizeDecoder: Decoder<Int>) =
        builder.addPrefixedSizeRepeatStep(collector, sizeDecoder)

    internal fun <C, R> repeatStep(times: Int, collector: Collector<T, C, R>) =
        builder.addConstantSizeRepeatStep(times, collector)

    internal fun <C, R> untilStep(
        collector: Collector<T, C, R>,
        addLast: Boolean,
        predicate: (T) -> Boolean,
    ) = builder.addUntilRepeatStep(collector, addLast, predicate)

    internal fun optionalStep(nullabilityDecoder: Decoder<Boolean>) = builder.addOptionalStep(nullabilityDecoder)

    private fun <R> thenStep(decoder: Decoder<R>) = builder.addThenStep(decoder)

    private companion object {

        val MARKER_DECODER = NothingDecoder()
    }

    //endregion

}
