package com.kamelia.sprinkler.binary.decoder.core

class NothingDecoder(
    private val error: Throwable = IllegalStateException("NothingDecoder always fails."),
) : Decoder<Nothing> {

    constructor(message: String) : this(IllegalStateException(message))

    override fun decode(input: DecoderDataInput): Decoder.State<Nothing> = Decoder.State.Error(error)

    override fun reset() = Unit

}
