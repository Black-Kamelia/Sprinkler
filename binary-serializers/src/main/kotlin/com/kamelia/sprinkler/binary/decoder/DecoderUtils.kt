@file:JvmName("DecoderUtils")

package com.kamelia.sprinkler.binary.decoder

import com.zwendo.restrikt.annotation.HideFromJava

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
fun <C, T, R> Decoder<T>.toCollection(
    collector: DecoderCollector<C, T, R>,
    sizeDecoder: Decoder<Number> = IntDecoder(),
): Decoder<R> = TODO()//compose().repeat(collector, sizeDecoder).assemble()

@JvmOverloads
fun <T> Decoder<T>.toList(sizeDecoder: Decoder<Number> = IntDecoder()): Decoder<List<T>> =
    toCollection(DecoderCollector.toList(), sizeDecoder)

@JvmOverloads
fun <T> Decoder<T>.toSet(sizeDecoder: Decoder<Number> = IntDecoder()): Decoder<Set<T>> =
    toCollection(DecoderCollector.toSet(), sizeDecoder)

@JvmOverloads
fun <K, V> Decoder<Pair<K, V>>.toMap(sizeDecoder: Decoder<Number> = IntDecoder()): Decoder<Map<K, V>> =
    toCollection(DecoderCollector.toMap(), sizeDecoder)

@HideFromJava
infix fun <T, U> Decoder<T>.and(other: Decoder<U>): Decoder<Pair<T, U>> = PairDecoder(this, other)
