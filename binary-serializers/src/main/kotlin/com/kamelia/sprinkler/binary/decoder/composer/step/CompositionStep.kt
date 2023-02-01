package com.kamelia.sprinkler.binary.decoder.composer.step

import com.kamelia.sprinkler.binary.decoder.Decoder
import com.kamelia.sprinkler.binary.decoder.composer.ComposedDecoderElementsAccumulator

@PublishedApi
internal abstract class CompositionStep {

    var index: Int = -1
        private set

    open val storeResult: Boolean
        get() = true

    abstract fun decoder(context: ComposedDecoderElementsAccumulator): Decoder<*>

    open fun nextStepCalculator(previous: NextStepCalculator?): NextStepCalculator? = null

    fun setIndex(index: Int): CompositionStep = apply {
        require(this.index == -1) { "Index already set" }
        this.index = index
    }

}
