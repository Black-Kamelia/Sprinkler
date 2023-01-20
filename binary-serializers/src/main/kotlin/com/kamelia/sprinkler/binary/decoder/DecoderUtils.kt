@file:JvmName("DecoderUtils")

package com.kamelia.sprinkler.binary.decoder


fun <T> Decoder<T>.toOptional(
    nullabilityDecoder: Decoder<Boolean> = BooleanDecoder(),
): Decoder<T?> = nullabilityDecoder
    .compose()
    .map {
        if (it) {
            this@toOptional
        } else {
            NullDecoder()
        }
    }
    .assemble()

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

infix fun <T, U> Decoder<T>.and(other: Decoder<U>): Decoder<Pair<T, U>> = PairDecoder(this, other)
