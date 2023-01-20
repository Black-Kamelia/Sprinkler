@file:JvmName("EncoderUtils")

package com.kamelia.sprinkler.binary.encoder

fun <T> Encoder<T>.toIterable(endMarker: ByteArray): Encoder<Iterable<T>> = object : Encoder<Iterable<T>> {

    override fun encode(obj: Iterable<T>): ByteArray = EncodingAccumulator().apply {
        encode(obj, this)
    }.toByteArray()

    override fun encode(obj: Iterable<T>, accumulator: EncodingAccumulator) {
        obj.forEach { encode(it, accumulator) }
        accumulator.addBytes(endMarker)
    }

}

fun <T> Encoder<T>.toCollection(): Encoder<Collection<T>> = object : Encoder<Collection<T>> {

    override fun encode(obj: Collection<T>): ByteArray = EncodingAccumulator().apply {
        encode(obj, this)
    }.toByteArray()

    override fun encode(obj: Collection<T>, accumulator: EncodingAccumulator) {
        IntEncoder.encode(obj.size, accumulator)
        obj.forEach { encode(it, accumulator) }
    }

}

fun <T> Encoder<T>.toOptional(): Encoder<T?> = object : Encoder<T?> {

    override fun encode(obj: T?): ByteArray = if (obj == null) {
        byteArrayOf(0)
    } else {
        val value = this@toOptional.encode(obj)
        val array = ByteArray(value.size + 1)
        array[0] = 1
        value.copyInto(array, 1)
    }

    override fun encode(obj: T?, accumulator: EncodingAccumulator) = if (obj == null) {
        accumulator.addByte(0)
    } else {
        accumulator.addByte(1)
        this@toOptional.encode(obj, accumulator)
    }

}
