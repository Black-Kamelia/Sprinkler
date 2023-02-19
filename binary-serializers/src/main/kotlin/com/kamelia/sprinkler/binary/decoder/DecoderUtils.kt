@file:JvmName("DecoderUtils")

package com.kamelia.sprinkler.binary.decoder

import com.kamelia.sprinkler.binary.decoder.core.ConstantSizeCollectionDecoder
import com.kamelia.sprinkler.binary.decoder.core.Decoder
import com.kamelia.sprinkler.binary.decoder.core.DecoderDataInput
import com.kamelia.sprinkler.binary.decoder.core.MarkerElementCollectionDecoder
import com.kamelia.sprinkler.binary.decoder.core.PrefixedSizeCollectionDecoder
import com.zwendo.restrikt.annotation.HideFromJava
import java.util.stream.Collector
import java.util.stream.Collectors

fun <T, R> Decoder<T>.mapTo(block: (T) -> Decoder<R>): Decoder<R> = object : Decoder<R> {
    private var nextReader: Decoder<R>? = null

    override fun decode(input: DecoderDataInput): Decoder.State<R> = if (nextReader == null) {
        this@mapTo.decode(input).mapState {
            nextReader = block(it)
            decodeNext(input)
        }
    } else {
        decodeNext(input)
    }

    private fun decodeNext(input: DecoderDataInput) = nextReader!!.decode(input).ifDone { nextReader = null }

    override fun reset() {
        this@mapTo.reset()
        nextReader = null
    }

}

fun <T, E> Decoder<T>.mapResult(block: (T) -> E): Decoder<E> = object : Decoder<E> {

    override fun decode(input: DecoderDataInput): Decoder.State<E> = this@mapResult.decode(input).mapResult(block)

    override fun reset() = this@mapResult.reset()

}

@JvmOverloads
fun <T : Any> Decoder<T>.toOptional(
    nullabilityDecoder: Decoder<Boolean> = BooleanDecoder(),
): Decoder<T?> = nullabilityDecoder.mapTo {
    if (it) {
        this@toOptional
    } else {
        NullDecoder()
    }
}

@JvmOverloads
fun <T, C, R> Decoder<T>.toCollection(
    collector: Collector<T, C, R>,
    sizeDecoder: Decoder<Number> = IntDecoder(),
): Decoder<R> = PrefixedSizeCollectionDecoder(collector, this, sizeDecoder)

fun <T, C, R> Decoder<T>.toCollection(
    collector: Collector<T, C, R>,
    size: Int,
): Decoder<R> = ConstantSizeCollectionDecoder(collector, this, size)

@JvmOverloads
fun <T, C, R> Decoder<T>.toCollection(
    collector: Collector<T, C, R>,
    keepLast: Boolean = false,
    predicate: (T) -> Boolean,
): Decoder<R> = MarkerElementCollectionDecoder(collector, this, keepLast, predicate)

@JvmOverloads
fun <T> Decoder<T>.toList(sizeDecoder: Decoder<Number> = IntDecoder()): Decoder<List<T>> =
    toCollection(Collectors.toList(), sizeDecoder)

@JvmOverloads
fun <T> Decoder<T>.toList(keepLast: Boolean = false, predicate: (T) -> Boolean): Decoder<List<T>> =
    toCollection(Collectors.toList(), keepLast, predicate)

fun <T> Decoder<T>.toList(size: Int): Decoder<List<T>> {
    require(size >= 0) { "Size must be non-negative, but was $size" }
    return when (size) {
        0 -> ConstantDecoder(emptyList())
        1 -> this.mapResult { listOf(it) }
        else -> toCollection(Collectors.toList(), size)
    }
}

@JvmOverloads
fun <T> Decoder<T>.toSet(sizeDecoder: Decoder<Number> = IntDecoder()): Decoder<Set<T>> =
    toCollection(Collectors.toSet(), sizeDecoder)

fun <T> Decoder<T>.toSet(size: Int): Decoder<Set<T>> {
    require(size >= 0) { "Size must be non-negative, but was $size" }
    return when (size) {
        0 -> ConstantDecoder(emptySet())
        1 -> this.mapResult { setOf(it) }
        else -> toCollection(Collectors.toSet(), size)
    }
}

@JvmOverloads
fun <T> Decoder<T>.toSet(keepLast: Boolean = false, predicate: (T) -> Boolean): Decoder<Set<T>> =
    toCollection(Collectors.toSet(), keepLast, predicate)

@JvmOverloads
fun <K, V> Decoder<Pair<K, V>>.toMap(sizeDecoder: Decoder<Number> = IntDecoder()): Decoder<Map<K, V>> =
    toCollection(Collectors.toMap(), sizeDecoder)

fun <K, V> Decoder<Pair<K, V>>.toMap(size: Int): Decoder<Map<K, V>> {
    require(size >= 0) { "Size must be non-negative, but was $size" }
    return when (size) {
        0 -> ConstantDecoder(emptyMap())
        1 -> this.mapResult { mapOf(it) }
        else -> toCollection(Collectors.toMap(), size)
    }
}

@JvmOverloads
fun <K, V> Decoder<Pair<K, V>>.toMap(
    keepLast: Boolean = false,
    predicate: (Pair<K, V>) -> Boolean,
): Decoder<Map<K, V>> =
    toCollection(Collectors.toMap(), keepLast, predicate)

@JvmOverloads
fun <T> Decoder<T>.toArray(
    factory: (Int) -> Array<T?>,
    sizeDecoder: Decoder<Number> = IntDecoder(),
): Decoder<Array<T>> =
    toCollection(Collectors.toArray(factory), sizeDecoder)

fun <T> Decoder<T>.toArray(size: Int, factory: (Int) -> Array<T?>): Decoder<Array<T>> {
    require(size >= 0) { "Size must be non-negative, but was $size" }
    return when (size) {
        0 -> ConstantDecoder(@Suppress("UNCHECKED_CAST") (arrayOf<Any>() as Array<T>))
        1 -> this.mapResult { @Suppress("UNCHECKED_CAST") (arrayOf<Any?>(it) as Array<T>) }
        else -> toCollection(Collectors.toArray(factory), size)
    }
}

@JvmOverloads
fun <T> Decoder<T>.toArray(
    factory: (Int) -> Array<T?>,
    keepLast: Boolean = false,
    predicate: (T) -> Boolean,
): Decoder<Array<T>> =
    toCollection(Collectors.toArray(factory), keepLast, predicate)

@HideFromJava
infix fun <T, U> Decoder<T>.and(other: Decoder<U>): Decoder<Pair<T, U>> = PairDecoder(this, other)
