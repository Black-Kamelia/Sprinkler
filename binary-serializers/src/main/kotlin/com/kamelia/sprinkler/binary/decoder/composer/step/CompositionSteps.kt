package com.kamelia.sprinkler.binary.decoder.composer.step

import com.kamelia.sprinkler.binary.decoder.Decoder
import com.kamelia.sprinkler.binary.decoder.DecoderCollector
import com.kamelia.sprinkler.binary.decoder.composer.ComposedDecoderElementsAccumulator


internal object CompositionSteps {

    fun then(decoder: Decoder<*>): CompositionStep = ThenStep(decoder)

    fun <T> map(decoderFactory: (T) -> Decoder<*>): CompositionStep = MapStep(decoderFactory)

    fun reduce(mapper: (ComposedDecoderElementsAccumulator) -> Any?): CompositionStep = ReduceStep(mapper)

    fun skip(size: Long) = SkipStep(size)

    fun <C, E, R> repeat(collector: DecoderCollector<C, E, R>, times: Int): CompositionStep = if (times == 1) {
        OneElementRepeatStep(collector)
    } else {
        NonPrefixedSizeRepeatStep.fromConstantSize(collector, times)
    }

    fun <C, E, R> repeat(
        collector: DecoderCollector<C, E, R>,
        sizeDecoder: Decoder<Int>
    ): Pair<CompositionStep, CompositionStep> =
        PrefixedSizeRepeatStep.create(sizeDecoder, collector)

    fun <C, E, R> until(
        collector: DecoderCollector<C, E, R>,
        addLast: Boolean,
        predicate: (E) -> Boolean
    ): CompositionStep =
        NonPrefixedSizeRepeatStep.fromUntil(collector, addLast, predicate)

    fun optional(nullabilityDecoder: Decoder<Boolean>): Pair<CompositionStep, CompositionStep> =
        OptionalStep.create(nullabilityDecoder)

    fun optionalRecursion(nullabilityDecoder: Decoder<Boolean>): CompositionStep =
        OptionalRecursionStep(nullabilityDecoder)
    
}
