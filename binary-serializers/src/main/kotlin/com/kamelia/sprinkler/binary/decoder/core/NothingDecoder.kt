package com.kamelia.sprinkler.binary.decoder.core

/**
 * A [Decoder] that always returns a [Decoder.State.Error] with the given [error].
 *
 * @constructor Creates a [NothingDecoder] with the given [Throwable] [error].
 * @param error the [Throwable] to return in [Decoder.State.Error]
 */
class NothingDecoder(
    private val error: Throwable = IllegalStateException("NothingDecoder always fails."),
) : Decoder<Nothing> {

    /**
     * Creates a [NothingDecoder] with the given [message]. The [Throwable] will be of type [IllegalStateException].
     *
     * @param message the message to use in the [IllegalStateException]
     */
    constructor(message: String) : this(IllegalStateException(message))

    override fun decode(input: DecoderInput): Decoder.State<Nothing> = Decoder.State.Error(error)

    override fun reset() = Unit

}
