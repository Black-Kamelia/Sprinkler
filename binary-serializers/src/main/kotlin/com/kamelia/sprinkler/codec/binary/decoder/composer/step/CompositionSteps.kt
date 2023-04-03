package com.kamelia.sprinkler.codec.binary.decoder.composer.step

import com.kamelia.sprinkler.codec.binary.decoder.composer.ElementsAccumulator
import com.kamelia.sprinkler.codec.binary.decoder.core.Decoder
import com.kamelia.sprinkler.codec.binary.decoder.core.DecoderInput


internal fun CompositionStepList.Builder.addThenStep(decoder: Decoder<*>) = addStep { decoder }

internal fun <T> CompositionStepList.Builder.addMapStep(popMappingElement: Boolean, decoderFactory: (T) -> Decoder<*>) =
    addStep {
        val element: T = if (popMappingElement) it.pop() else it.peek()
        decoderFactory(element)
    }

internal fun CompositionStepList.Builder.addReduceStep(reducer: (ElementsAccumulator) -> Any?) =
    addStep {
        object : Decoder<Any?> {

            override fun decode(input: DecoderInput): Decoder.State<Any?> = Decoder.State.Done(reducer(it))

            override fun reset() {
                // nothing to do
            }

        }
    }

internal fun CompositionStepList.Builder.addSkipStep(size: Long) = addStep(SkipStep(size))

internal fun CompositionStepList.Builder.addThenItselfOrNullStep(nullabilityDecoder: Decoder<Boolean>) =
    ThenItselfOrNullStep.addStep(this, nullabilityDecoder)

