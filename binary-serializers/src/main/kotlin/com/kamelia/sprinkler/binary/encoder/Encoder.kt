package com.kamelia.sprinkler.binary.encoder

fun interface Encoder<in T> {

    fun encode(obj: T): ByteArray

    fun encode(obj: T, accumulator: EncodingAccumulator) = accumulator.addBytes(encode(obj))

}
