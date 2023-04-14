@file:JvmName("EncoderUtils")

package com.kamelia.sprinkler.codec.binary.encoder

import com.kamelia.sprinkler.codec.binary.encoder.core.Encoder

/**
 * Creates an encoder that encodes an object [R] using a given encoder of type [T]. The created encoder will map the
 * input object [R] to an object of type [T] using the given [mapper] function, and then delegate the encoding to the
 * original encoder.
 *
 * Here is an example of how to use this function:
 *
 * ```
 * val longEncoder = LongEncoder()
 * val instantEncoder: Encoder<Instant> = longEncoder.withMappedInput(Instant::toEpochMilli)
 * ```
 *
 * The above code creates an encoder that encodes [Instant][java.time.Instant] objects by first converting them to a
 * long value using [Instant.toEpochMilli][java.time.Instant.toEpochMilli], and then encoding the long value using the
 * longEncoder.
 *
 * @receiver the original encoder of type [T]
 * @param mapper the function that maps the input object [R] to an object of type [T]
 * @return an encoder that encodes an object [R]
 */
fun <T, R> Encoder<T>.withMappedInput(mapper: (R) -> T): Encoder<R> =
    Encoder { obj, output -> this@withMappedInput.encode(mapper(obj), output) }

/**
 * Creates an encoder that encodes an [Iterable] of objects of type [T]. The created encoder will encode each object
 * in the iterable using the original encoder, and then encode the [endMarker] using the original encoder. The end
 * marker is used to indicate the end of the iterable.
 *
 * @receiver the original encoder of type [T]
 * @param endMarker the end marker that is used to indicate the end of the iterable
 * @return an encoder that encodes an [Iterable] of objects of type [T]
 */
fun <T> Encoder<T>.toIterable(endMarker: T): Encoder<Iterable<T>> =
    Encoder { obj, output ->
        obj.forEach { this@toIterable.encode(it, output) }
        this@toIterable.encode(endMarker, output)
    }

/**
 * Creates an encoder that encodes a [Collection] of objects of type [T]. The created encoder will encode the size of
 * the collection using the given [sizeEncoder], and then encode each object in the collection using the original
 * encoder.
 *
 * @receiver the original encoder of type [T]
 * @param sizeEncoder the encoder that is used to encode the size of the collection (defaults to the default
 * [IntEncoder])
 * @return an encoder that encodes a [Collection] of objects of type [T]
 */
@JvmOverloads
fun <T> Encoder<T>.toCollection(sizeEncoder: Encoder<Int> = IntEncoder()): Encoder<Collection<T>> =
    Encoder { obj, output ->
        sizeEncoder.encode(obj.size, output)
        obj.forEach { this@toCollection.encode(it, output) }
    }

/**
 * Creates an encoder that encodes a [Map] of objects of type [K] and [V]. The created encoder will encode the size of
 * the map using the given [sizeEncoder], and then encode each entry in the map as a [Pair] using the original encoder.
 *
 * @receiver the original encoder of [Pairs][Pair] of [K] and [V]
 * @param sizeEncoder the encoder that is used to encode the size of the map (defaults to the default [IntEncoder])
 * @return an encoder that encodes a [Map] of objects of type [K] and [V]
 */
@JvmOverloads
fun <K, V> Encoder<Pair<K, V>>.toMap(sizeEncoder: Encoder<Int> = IntEncoder()): Encoder<Map<K, V>> =
    Encoder { obj, output ->
        sizeEncoder.encode(obj.size, output)
        obj.forEach { (key, value) -> this@toMap.encode(key to value, output) }
    }

/**
 * Creates an encoder that encodes a [Map] of objects of type [K] and [V]. The created encoder will encode each entry
 * in the map as a [Pair] using the original encoder, and then encode the [endMarker] using the original encoder. The
 * end marker is used to indicate the end of the map.
 *
 * @receiver the original encoder of [Pairs][Pair] of [K] and [V]
 * @param endMarker the end marker that is used to indicate the end of the map
 * @return an encoder that encodes a [Map] of objects of type [K] and [V]
 */
fun <K, V> Encoder<Pair<K, V>>.toMap(endMarker: Pair<K, V>): Encoder<Map<K, V>> =
    Encoder { obj, output ->
        obj.forEach { (key, value) ->
            this@toMap.encode(key to value, output)
        }
        this@toMap.encode(endMarker, output)
    }

/**
 * Creates an encoder that encodes a [Map] of objects of type [K] and [V]. The created encoder will encode the size of
 * the map using the given [sizeEncoder], and then encode each key and value in the map using the original encoder for
 * keys and the given [valueEncoder] for values.
 *
 * @receiver the original encoder of keys of type [K]
 * @param valueEncoder the encoder that is used to encode the values of the map
 * @param sizeEncoder the encoder that is used to encode the size of the map (defaults to the default [IntEncoder])
 * @return an encoder that encodes a [Map] of objects of type [K] and [V]
 */
fun <K, V> Encoder<K>.toMap(valueEncoder: Encoder<V>, sizeEncoder: Encoder<Int> = IntEncoder()): Encoder<Map<K, V>> =
    Encoder { obj, output ->
        sizeEncoder.encode(obj.size, output)
        obj.forEach { (key, value) ->
            this@toMap.encode(key, output)
            valueEncoder.encode(value, output)
        }
    }

/**
 * Creates an encoder that encodes a [Map] of objects of type [K] and [V]. The created encoder will encode each key and
 * value in the map using the original encoder for keys and the given [valueEncoder] for values, and then encode the
 * [endMarker] in the same way. The end marker is used to indicate the end of the map.
 *
 * @receiver the original encoder of keys of type [K]
 * @param valueEncoder the encoder that is used to encode the values of the map
 * @param endMarker the end marker that is used to indicate the end of the map
 * @return an encoder that encodes a [Map] of objects of type [K] and [V]
 */
fun <K, V> Encoder<K>.toMap(valueEncoder: Encoder<V>, endMarker: Pair<K, V>): Encoder<Map<K, V>> =
    Encoder { obj, output ->
        obj.forEach { (key, value) ->
            this@toMap.encode(key, output)
            valueEncoder.encode(value, output)
        }
        this@toMap.encode(endMarker.first, output)
        valueEncoder.encode(endMarker.second, output)
    }

/**
 * Creates an encoder that encodes an [Array] of objects of type [T]. The created encoder will encode the size of the
 * array using the given [sizeEncoder], and then encode each object in the array using the original encoder.
 *
 * @receiver the original encoder of type [T]
 * @param sizeEncoder the encoder that is used to encode the size of the array (defaults to the default [IntEncoder])
 * @return an encoder that encodes an [Array] of objects of type [T]
 */
@JvmOverloads
fun <T> Encoder<T>.toArray(sizeEncoder: Encoder<Int> = IntEncoder()): Encoder<Array<T>> =
    Encoder { obj, output ->
        sizeEncoder.encode(obj.size, output)
        obj.forEach { this@toArray.encode(it, output) }
    }

/**
 * Creates an encoder that encodes an [Array] of objects of type [T]. The created encoder will encode each object in
 * the array using the original encoder, and then encode the [endMarker] in the same way. The end marker is used to
 * indicate the end of the array.
 *
 * @receiver the original encoder of type [T]
 * @param endMarker the end marker that is used to indicate the end of the array
 * @return an encoder that encodes an [Array] of objects of type [T]
 */
fun <T> Encoder<T>.toArray(endMarker: T): Encoder<Array<T>> =
    Encoder { obj, output ->
        obj.forEach { this@toArray.encode(it, output) }
        this@toArray.encode(endMarker, output)
    }

/**
 * Creates an encoder that encodes a nullable [T] object. The created encoder will encode a [Boolean] indicating
 * whether the object is null or not, and then encode the object using the original encoder if it is not null.
 *
 * @receiver the original encoder of type [T]
 * @param nullabilityEncoder the encoder that is used to encode the nullability of the object (defaults to the
 * default [BooleanEncoder])
 * @return an encoder that encodes a nullable [T] object
 */
@JvmOverloads
fun <T : Any> Encoder<T>.toOptional(nullabilityEncoder: Encoder<Boolean> = BooleanEncoder()): Encoder<T?> =
    Encoder { obj, output ->
        val isNotNull = obj != null
        nullabilityEncoder.encode(isNotNull, output)
        if (isNotNull) {
            this@toOptional.encode(obj!!, output)
        }
    }
