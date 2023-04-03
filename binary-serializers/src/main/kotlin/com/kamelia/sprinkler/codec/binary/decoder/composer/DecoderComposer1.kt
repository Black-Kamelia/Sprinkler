package com.kamelia.sprinkler.codec.binary.decoder.composer

import com.kamelia.sprinkler.codec.binary.decoder.BooleanDecoder
import com.kamelia.sprinkler.codec.binary.decoder.core.Decoder
import com.kamelia.sprinkler.codec.binary.decoder.mapResult

class DecoderComposer1<B, T> : DecoderComposer<B, T, DecoderComposer1<B, T>> {

    @PublishedApi
    internal var decoder: Decoder<T>?

    internal constructor(previous: DecoderComposer0<B>, decoder: Decoder<T>) : super(previous, decoder) {
        this.decoder = decoder
    }

    /**
     * Creates a new instance of [DecoderComposer1] with the given [previous] composer.
     *
     * This constructor is intended to be used when a reduction step is performed, and should only be called when users
     * want to extend the [DecoderComposer] API.
     *
     * @param previous the previous composer
     */
    constructor(previous: DecoderComposer<B, *, *>) : super(previous) {
        decoder = null
    }

    /**
     * Maps the result of the current decoder to a new [Decoder] using the given [block].
     *
     * @param block the block to map the result of the current decoder
     * @return a new [DecoderComposer1] with the mapped decoder
     * @param R the type decoded by the new decoder
     */
    fun <R> map(block: (T) -> Decoder<R>): DecoderComposer1<B, R> = cast { mapStep(block) }

    /**
     * Maps the result of the current decoder to a new [Decoder] using the given [block] and also stores this result in
     * the accumulator.
     *
     * @param block the block to map the result of the current decoder
     * @return a [DecoderComposer2] with the mapped decoder and the stored result
     * @param R the type decoded by the new decoder
     */
    fun <R> mapAndStore(block: (T) -> Decoder<R>): DecoderComposer2<B, T, R> =
        DecoderComposer2(this, mapAndStoreStep(block))

    /**
     * Stores the result of the current decoder in the accumulator and then decodes a new value using the given
     * [decoder].
     *
     * @param decoder the decoder for the next value
     * @return a [DecoderComposer2] with the stored result and the decoded value
     * @param R the type decoded by the new decoder
     */
    fun <R> then(decoder: Decoder<R>): DecoderComposer2<B, T, R> = DecoderComposer2(this, decoder)

    /**
     * Stores the result of the current decoder in the accumulator. Then decodes a new value using the given
     * [decoder] and applies the given [mapper] to the result.
     *
     * @param decoder the decoder for the next value
     * @param mapper the mapper to apply to the result of the current decoder
     * @return a [DecoderComposer2] with the stored result and the decoded value
     * @param E the type read by the new decoder
     * @param R the type returned by the mapper
     */
    fun <E, R> then(decoder: Decoder<E>, mapper: (E) -> R): DecoderComposer2<B, T, R> =
        DecoderComposer2(this, decoder.mapResult(mapper))

    /**
     * Decodes recursively an object of type [B] using the final decoder of this composer and stores the result in the
     * accumulator. The [B] object is decoded only if the given [nullabilityDecoder] returns `true`. Otherwise, `null`
     * is stored in the accumulator.
     *
     * @param nullabilityDecoder the decoder to determine if the object should be decoded
     * @return a [DecoderComposer2] with the stored result and the decoded value
     */
    @JvmOverloads
    fun thenItselfOrNull(nullabilityDecoder: Decoder<Boolean> = BooleanDecoder()): DecoderComposer2<B, T, B?> =
        DecoderComposer2(this, thenItselfOrNullStep(nullabilityDecoder))

    private inline fun <R> cast(block: DecoderComposer1<B, T>.() -> Unit): DecoderComposer1<B, R> {
        block()
        decoder = null
        @Suppress("UNCHECKED_CAST")
        return this as DecoderComposer1<B, R>
    }

}
