@file:JvmName("EncoderUtils")

package com.kamelia.sprinkler.codec.binary.encoder

import com.kamelia.sprinkler.codec.binary.encoder.core.Encoder

inline fun <T, R> Encoder<T>.withMappedInput(crossinline mapper: (R) -> T): Encoder<R> =
    Encoder { obj, output -> this@withMappedInput.encode(mapper(obj), output) }

fun <T> Encoder<T>.toIterable(endMarker: T): Encoder<Iterable<T>> = Encoder { obj, output ->
    obj.forEach { this@toIterable.encode(it, output) }
    this@toIterable.encode(endMarker, output)
}

@JvmOverloads
fun <T> Encoder<T>.toCollection(sizeEncoder: Encoder<Int> = IntEncoder()): Encoder<Collection<T>> =
    Encoder { obj, output ->
        sizeEncoder.encode(obj.size, output)
        obj.forEach { this@toCollection.encode(it, output) }
    }

@JvmOverloads
fun <K, V> Encoder<Map.Entry<K, V>>.toMap(sizeEncoder: Encoder<Int> = IntEncoder()): Encoder<Map<K, V>> =
    toCollection(sizeEncoder).withMappedInput(Map<K, V>::entries)

fun <K, V> Encoder<Map.Entry<K, V>>.toMap(endMarker: Pair<K, V>): Encoder<Map<K, V>> {
    return toIterable(endMarker.toEntry()).withMappedInput(Map<K, V>::entries)
}

fun <K, V> Encoder<K>.toMap(valueEncoder: Encoder<V>, sizeEncoder: Encoder<Int> = IntEncoder()): Encoder<Map<K, V>> =
    toMapEntryEncoder(valueEncoder).toMap(sizeEncoder)

fun <K, V> Encoder<K>.toMap(valueEncoder: Encoder<V>, endMarker: Pair<K, V>): Encoder<Map<K, V>> {
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

private fun <K, V> Pair<K, V>.toEntry(): Map.Entry<K, V> = object : Map.Entry<K, V> {
    override val key: K
        get() = first
    override val value: V
        get() = second
}
