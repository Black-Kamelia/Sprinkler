package com.kamelia.sprinkler.binary.decoder

interface AbstractDecoder<I, out O> {

    fun decode(input: I): O

}
