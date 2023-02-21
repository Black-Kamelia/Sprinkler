package com.kamelia.sprinkler.binary.decoder.composer.step

import com.kamelia.sprinkler.binary.decoder.composer.ElementsAccumulator
import com.kamelia.sprinkler.binary.decoder.core.Decoder

@PublishedApi
internal fun interface CompositionStep {

    val storeResult: Boolean
        get() = true

    fun decoder(accumulator: ElementsAccumulator): Decoder<*>

    /**
     * Potentially modifies the current index when arriving at this step. By default, the index is not modified and
     * [currentIndex] is returned.
     */
    fun onArrive(accumulator: ElementsAccumulator, currentIndex: Int): Int = currentIndex

    /**
     * Computes the next step index after this step. By default, the next step is [currentIndex + 1][currentIndex].
     * To recurse the whole composition, return [currentIndex].
     */
    fun onLeave(accumulator: ElementsAccumulator, currentIndex: Int): Int = currentIndex + 1

    fun reset() = Unit

}
