package com.kamelia.sprinkler.binary.decoder.composer.step

import com.kamelia.sprinkler.binary.decoder.composer.ElementsAccumulator
import com.kamelia.sprinkler.binary.decoder.core.Decoder
import com.kamelia.sprinkler.binary.decoder.core.DecoderDataInput
import java.util.stream.Collector


internal fun CompositionStepList.Builder.addThenStep(decoder: Decoder<*>) = addStep { decoder }

internal fun <T> CompositionStepList.Builder.addMapStep(popMappingElement: Boolean, decoderFactory: (T) -> Decoder<*>) =
    addStep {
        val element: T = if (popMappingElement) it.pop() else it.peek()
        decoderFactory(element)
    }

internal fun CompositionStepList.Builder.addReduceStep(reducer: (ElementsAccumulator) -> Any?) =
    addStep {
        object : Decoder<Any?> {

            override fun decode(input: DecoderDataInput): Decoder.State<Any?> = Decoder.State.Done(reducer(it))

            override fun reset() {
                // nothing to do
            }

        }
    }

internal fun CompositionStepList.Builder.addSkipStep(size: Long) = addStep(SkipStep(size))

internal fun <C, E, R> CompositionStepList.Builder.addConstantSizeRepeatStep(
    times: Int,
    collector: Collector<E, C, R>,
) = ConstantSizeRepeatStep.addStep(this, collector, times)

internal fun <C, E, R> CompositionStepList.Builder.addPrefixedSizeRepeatStep(
    collector: Collector<E, C, R>,
    sizeDecoder: Decoder<Int>,
) = PrefixedSizeRepeatStep.addStep(this, sizeDecoder, collector)

internal fun <C, E, R> CompositionStepList.Builder.addUntilRepeatStep(
    collector: Collector<E, C, R>,
    addLast: Boolean,
    predicate: (E) -> Boolean,
) = UntilRepeatStep.addStep(this, collector, addLast, predicate)


internal fun CompositionStepList.Builder.addOptionalStep(nullabilityDecoder: Decoder<Boolean>) =
    OptionalStep.addStep(this, nullabilityDecoder)

internal fun CompositionStepList.Builder.addThenItselfOrNullStep(nullabilityDecoder: Decoder<Boolean>) =
    ThenItselfStep.addStep(this, nullabilityDecoder)

