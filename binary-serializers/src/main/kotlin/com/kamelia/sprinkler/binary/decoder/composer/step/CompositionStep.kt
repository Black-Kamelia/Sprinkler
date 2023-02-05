package com.kamelia.sprinkler.binary.decoder.composer.step

import com.kamelia.sprinkler.binary.decoder.Decoder
import com.kamelia.sprinkler.binary.decoder.composer.ComposedDecoderElementsAccumulator

@PublishedApi
internal fun interface CompositionStep {

    val storeResult: Boolean
        get() = true

    fun decoder(accumulator: ComposedDecoderElementsAccumulator): Decoder<*>

    /**
     * Potentially modifies the current index when arriving at this step. By default, the index is not modified and
     * [currentIndex] is returned.
     */
    fun onArrive(accumulator: ComposedDecoderElementsAccumulator, currentIndex: Int): Int = currentIndex

    /**
     * Computes the next step index after this step. By default, the next step is [currentIndex + 1][currentIndex].
     */
    fun onLeave(accumulator: ComposedDecoderElementsAccumulator, currentIndex: Int): Int? = currentIndex + 1

    fun reset() = Unit

}
