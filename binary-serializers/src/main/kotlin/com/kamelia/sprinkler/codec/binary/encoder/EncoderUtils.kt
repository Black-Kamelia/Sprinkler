@file:JvmName("EncoderUtils")

package com.kamelia.sprinkler.codec.binary.encoder

import com.kamelia.sprinkler.codec.binary.encoder.core.Encoder
import com.kamelia.sprinkler.codec.binary.encoder.core.EncoderOutput

inline fun <T, R> Encoder<T>.withMappedInput(crossinline mapper: (R) -> T): Encoder<R> = object : Encoder<R> {

    override fun encode(obj: R, output: EncoderOutput): Unit = this@withMappedInput.encode(mapper(obj), output)

}

fun <T> Encoder<T>.toIterable(endMarker: ByteArray): Encoder<Iterable<T>> = object : Encoder<Iterable<T>> {

    override fun encode(obj: Iterable<T>, output: EncoderOutput) {
        obj.forEach { this@toIterable.encode(it, output) }
        output.write(endMarker)
    }

}

@JvmOverloads
fun <T> Encoder<T>.toCollection(
    sizeEncoder: Encoder<Int> = IntEncoder(),
): Encoder<Collection<T>> = object : Encoder<Collection<T>> {

    override fun encode(obj: Collection<T>, output: EncoderOutput) {
        sizeEncoder.encode(obj.size, output)
        obj.forEach { this@toCollection.encode(it, output) }
    }

}

@JvmOverloads
fun <K, V> Encoder<Map.Entry<K, V>>.toMap(
    sizeEncoder: Encoder<Int> = IntEncoder(),
): Encoder<Map<K, V>> = toCollection(sizeEncoder).withMappedInput { it.entries }

fun <K, V> Encoder<Map.Entry<K, V>>.toMap(endMarker: ByteArray): Encoder<Map<K, V>> =
    toIterable(endMarker).withMappedInput { it.entries }

fun <K, V> Encoder<K>.toMap(
    valueEncoder: Encoder<V>,
    sizeEncoder: Encoder<Int> = IntEncoder(),
): Encoder<Map<K, V>> = toMapEntryEncoder(valueEncoder).toMap(sizeEncoder)

fun <K, V> Encoder<K>.toMap(
    valueEncoder: Encoder<V>,
    endMarker: ByteArray,
): Encoder<Map<K, V>> = toMapEntryEncoder(valueEncoder).toMap(endMarker)


@JvmOverloads
fun <T : Any> Encoder<T>.toOptional(
    nullabilityEncoder: Encoder<Boolean> = BooleanEncoder(),
): Encoder<T?> = object : Encoder<T?> {

    override fun encode(obj: T?, output: EncoderOutput) {
        val isNotNull = obj != null
        nullabilityEncoder.encode(isNotNull, output)
        if (isNotNull) {
            this@toOptional.encode(obj!!, output)
        }
    }

}

@JvmOverloads
fun <T> Encoder<T>.toArray(
    sizeEncoder: Encoder<Int> = IntEncoder(),
): Encoder<Array<T>> = object : Encoder<Array<T>> {

    override fun encode(obj: Array<T>, output: EncoderOutput) {
        sizeEncoder.encode(obj.size, output)
        obj.forEach { this@toArray.encode(it, output) }
    }

}

private fun <K, V> Encoder<K>.toMapEntryEncoder(
    valueEncoder: Encoder<V>,
): Encoder<Map.Entry<K, V>> = object : Encoder<Map.Entry<K, V>> {

    override fun encode(obj: Map.Entry<K, V>, output: EncoderOutput) {
        this@toMapEntryEncoder.encode(obj.key, output)
        valueEncoder.encode(obj.value, output)
    }

}
