@file:JvmName("EncoderUtils")

package com.kamelia.sprinkler.codec.binary.encoder

import com.kamelia.sprinkler.codec.binary.encoder.core.Encoder

inline fun <T, R> Encoder<T>.withMappedInput(crossinline mapper: (R) -> T): Encoder<R> =
    Encoder { obj, output -> this@withMappedInput.encode(mapper(obj), output) }

fun <T> Encoder<T>.toIterable(endMarker: ByteArray): Encoder<Iterable<T>> {
    require(endMarker.isNotEmpty()) { "End marker must not be empty" }
    return Encoder { obj, output ->
        obj.forEach { this@toIterable.encode(it, output) }
        output.write(endMarker)
    }
}

@JvmOverloads
fun <T> Encoder<T>.toCollection(sizeEncoder: Encoder<Int> = IntEncoder()): Encoder<Collection<T>> =
    Encoder { obj, output ->
        sizeEncoder.encode(obj.size, output)
        obj.forEach { this@toCollection.encode(it, output) }
    }

@JvmOverloads
fun <K, V> Encoder<Map.Entry<K, V>>.toMap(sizeEncoder: Encoder<Int> = IntEncoder()): Encoder<Map<K, V>> =
    toCollection(sizeEncoder).withMappedInput { it.entries }

fun <K, V> Encoder<Map.Entry<K, V>>.toMap(endMarker: ByteArray): Encoder<Map<K, V>> {
    require(endMarker.isNotEmpty()) { "End marker must not be empty" }
    return toIterable(endMarker).withMappedInput { it.entries }
}

fun <K, V> Encoder<K>.toMap(valueEncoder: Encoder<V>, sizeEncoder: Encoder<Int> = IntEncoder()): Encoder<Map<K, V>> =
    toMapEntryEncoder(valueEncoder).toMap(sizeEncoder)

fun <K, V> Encoder<K>.toMap(valueEncoder: Encoder<V>, endMarker: ByteArray): Encoder<Map<K, V>> {
    require(endMarker.isNotEmpty()) { "End marker must not be empty" }
    return toMapEntryEncoder(valueEncoder).toMap(endMarker)
}

@JvmOverloads
fun <T : Any> Encoder<T>.toOptional(nullabilityEncoder: Encoder<Boolean> = BooleanEncoder()): Encoder<T?> =
    Encoder { obj, output ->
        val isNotNull = obj != null
        nullabilityEncoder.encode(isNotNull, output)
        if (isNotNull) {
            this@toOptional.encode(obj!!, output)
        }
    }

@JvmOverloads
fun <T> Encoder<T>.toArray(sizeEncoder: Encoder<Int> = IntEncoder()): Encoder<Array<T>> =
    Encoder { obj, output ->
        sizeEncoder.encode(obj.size, output)
        obj.forEach { this@toArray.encode(it, output) }
    }

fun <K, V> Encoder<K>.toMapEntryEncoder(valueEncoder: Encoder<V>): Encoder<Map.Entry<K, V>> =
    Encoder { obj, output ->
        this@toMapEntryEncoder.encode(obj.key, output)
        valueEncoder.encode(obj.value, output)
    }
