@file:JvmName("DecoderUtils")

package com.kamelia.sprinkler.binary.decoder

import com.zwendo.restrikt.annotation.HideFromJava

fun <T, R> Decoder<T>.map(block: (T) -> Decoder<R>): Decoder<R> = object : Decoder<R> {
    private var nextReader: Decoder<R>? = null

    override fun decode(input: DecoderDataInput): Decoder.State<R> {
        if (nextReader == null) {
            when (val state = this@map.decode(input)) {
                is Decoder.State.Done -> nextReader = block(state.value)
                else -> return state.mapEmptyState()
            }
        }

        return nextReader!!.decode(input).ifDone { nextReader = null }
    }

    override fun reset() {
        this@map.reset()
        nextReader = null
    }

    override fun createNew(): Decoder<R> = this@map.createNew().map(block)

}

fun <T, E> Decoder<T>.mapResult(block: (T) -> E): Decoder<E> = object : Decoder<E> {

    override fun decode(input: DecoderDataInput): Decoder.State<E> = this@mapResult.decode(input).map(block)

    override fun reset() = this@mapResult.reset()

    override fun createNew(): Decoder<E> = this@mapResult.createNew().mapResult(block)

}

@JvmOverloads
fun <T> Decoder<T>.toOptional(
    nullabilityDecoder: Decoder<Boolean> = BooleanDecoder(),
): Decoder<T?> = nullabilityDecoder.map {
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
): Decoder<R> = compose().repeat(collector, sizeDecoder).assemble()

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
