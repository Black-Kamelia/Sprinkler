package com.kamelia.sprinkler.transcoder.binary.decoder.core

/**
 * A [Decoder] that always returns a [Decoder.State.Error] with the given [error].
 *
 * @constructor Creates a [NothingDecoder] with the given [Throwable] [error].
 * @param error the [Throwable] to return in [Decoder.State.Error]
 */
class NothingDecoder(
    private val errorProvider: () -> Throwable,
) : Decoder<Nothing> {

    /**
     * Creates a [NothingDecoder] with the given [message]. The [Throwable] will be of type [IllegalStateException].
     *
     * @param message the message to use in the [IllegalStateException]
     */
    constructor(message: String) : this({ IllegalStateException(message) })

    constructor(error: Throwable) : this({ error })

    override fun decode(input: DecoderInput): Decoder.State<Nothing> = Decoder.State.Error(errorProvider())

    override fun reset() = Unit

}
