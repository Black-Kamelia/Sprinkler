package com.kamelia.sprinkler.binary.decoder.core

class ConstantDecoder<T>(private val factory: () -> T) : Decoder<T> {

    constructor(value: T) : this({ value })

    override fun decode(input: DecoderDataInput): Decoder.State<T> = Decoder.State.Done(factory())

    override fun reset() = Unit

}
