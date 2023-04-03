@file:JvmName("EncoderUtils")

package com.kamelia.sprinkler.codec.binary.encoder

import com.kamelia.sprinkler.codec.binary.encoder.core.Encoder
import com.kamelia.sprinkler.codec.binary.encoder.core.EncoderOutput

inline fun <T, R> Encoder<T>.withMappedInput(crossinline mapper: (R) -> T): Encoder<R> = object : Encoder<R> {

    override fun <O : EncoderOutput> encode(obj: R, output: O): O = this@withMappedInput.encode(mapper(obj), output)

}

fun <T> Encoder<T>.toIterable(endMarker: ByteArray): Encoder<Iterable<T>> = object : Encoder<Iterable<T>> {

    override fun <O : EncoderOutput> encode(obj: Iterable<T>, output: O): O = output.apply {
        obj.forEach { this@toIterable.encode(it, this) }
        write(endMarker)
    }

}

fun <T> Encoder<T>.toCollection(
    sizeEncoder: Encoder<Int> = IntEncoder(),
): Encoder<Collection<T>> = object : Encoder<Collection<T>> {

    override fun <O : EncoderOutput> encode(obj: Collection<T>, output: O): O = output.apply {
        sizeEncoder.encode(obj.size, output)
        obj.forEach { this@toCollection.encode(it, this) }
    }

}

fun <T : Any> Encoder<T>.toOptional(
    nullabilityEncoder: Encoder<Boolean> = BooleanEncoder(),
): Encoder<T?> = object : Encoder<T?> {

    override fun <O : EncoderOutput> encode(obj: T?, output: O): O = output.apply {
        val isNotNull = obj != null
        nullabilityEncoder.encode(isNotNull, this)
        if (isNotNull) {
            this@toOptional.encode(obj!!, this)
        }
    }

}
