package com.kamelia.sprinkler.codec.binary.decoder.composer

import com.kamelia.sprinkler.codec.binary.decoder.composer.step.CompositionStepList
import com.kamelia.sprinkler.codec.binary.decoder.composer.step.addMapStep
import com.kamelia.sprinkler.codec.binary.decoder.composer.step.addReduceStep
import com.kamelia.sprinkler.codec.binary.decoder.composer.step.addSkipStep
import com.kamelia.sprinkler.codec.binary.decoder.composer.step.addThenItselfOrNullStep
import com.kamelia.sprinkler.codec.binary.decoder.composer.step.addThenStep
import com.kamelia.sprinkler.codec.binary.decoder.core.Decoder
import com.kamelia.sprinkler.codec.binary.decoder.core.NothingDecoder

/**
 * Base class for all [DecoderComposer]s. It provides the basic functionality for composing decoders.
 *
 * @param B the type of the final result of the composition
 * @param T the type of the current decoder's result
 * @param D the concrete type of the subclass of [DecoderComposer]
 */
abstract class DecoderComposer<B, T, D : DecoderComposer<B, T, D>> {

    @PublishedApi
    internal val builder: CompositionStepList.Builder

    internal constructor() {
        builder = CompositionStepList.Builder()
    }

    /**
     * Creates a new [DecoderComposer], created using the previous [DecoderComposer] as a base. The [decoder] will be
     * added to the composition as a then step.
     *
     * This constructor should be called when the object [T] decoded by the current [DecoderComposer] should be stored
     * in the [ElementsAccumulator].
     *
     * @param previous the previous [DecoderComposer]
     * @param decoder the decoder to add to the composition
     */
    protected constructor(previous: DecoderComposer<B, *, *>, decoder: Decoder<T>) {
        builder = previous.builder
        if (decoder !== MARKER_DECODER) {
            thenStep(decoder)
        }
    }

    internal constructor(previous: DecoderComposer<B, *, *>) {
        builder = previous.builder
    }

    /**
     * Adds a composition step that skips the given [amount] of bytes.
     *
     * @param amount the amount of bytes to skip
     * @return the current [DecoderComposer] instance
     * @throws IllegalArgumentException if [amount] is negative
     */
    fun skip(amount: Long): D {
        require(amount >= 0) { "Amount of bytes to skip must be non-negative, but was $amount" }
        return thisCast { builder.addSkipStep(amount) }
    }

    //region Subclasses API

//    protected open fun <R> then(decoder: Decoder<R>): DecoderComposer<B, R, *> {
//        throw UnsupportedOperationException("This method should be overridden by subclasses")
//    }

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

    protected inline fun <R> thisCast(block: () -> Unit): R {
        block()
        @Suppress("UNCHECKED_CAST")
        return this as R
    }

    //endregion

    //region Internal

    private fun <R> thenStep(decoder: Decoder<R>) = builder.addThenStep(decoder)

    private companion object {

        val MARKER_DECODER = NothingDecoder()
    }

    //endregion

}
