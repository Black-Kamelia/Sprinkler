package com.kamelia.sprinkler.binary.decoder.composer

import com.kamelia.sprinkler.binary.decoder.BooleanDecoder
import com.kamelia.sprinkler.binary.decoder.Decoder
import com.kamelia.sprinkler.binary.decoder.mapResult
import com.zwendo.restrikt.annotation.PackagePrivate


class DecoderComposer2<B, T1, T2> @PackagePrivate internal constructor(
    previous: DecoderComposer<B, *, *>,
    decoder: Decoder<T2>,
) : DecoderComposer<B, T2, DecoderComposer2<B, T1, T2>>(previous, decoder) {

    fun <R> map(block: (T2) -> Decoder<R>): DecoderComposer2<B, T1, R> = thisCasted { mapStep(block) }

    fun <R> then(decoder: Decoder<R>): DecoderComposer3<B, T1, T2, R> = DecoderComposer3(this, decoder)

    fun <E, R> then(decoder: Decoder<E>, mapper: (E) -> R): DecoderComposer3<B, T1, T2, R> =
        DecoderComposer3(this, decoder.mapResult(mapper))

    @JvmOverloads
    fun thenItselfOrNull(nullabilityDecoder: Decoder<Boolean> = BooleanDecoder()): DecoderComposer3<B, T1, T2, B?> =
        DecoderComposer3(this, thenItselfOrNullStep(nullabilityDecoder))

    fun <R> reduce(reducer: (T1, T2) -> R): DecoderComposer1<B, R> {
        reduceStep { reducer(next(), next()) }
        return DecoderComposer1(this)
    }

}

class DecoderComposer3<B, T1, T2, T3> @PackagePrivate internal constructor(
    previous: DecoderComposer<B, *, *>,
    decoder: Decoder<T3>,
) : DecoderComposer<B, T3, DecoderComposer3<B, T1, T2, T3>>(previous, decoder) {

    fun <R> map(block: (T3) -> Decoder<R>): DecoderComposer3<B, T1, T2, R> = thisCasted { mapStep(block) }

    fun <R> then(decoder: Decoder<R>): DecoderComposer4<B, T1, T2, T3, R> = DecoderComposer4(this, decoder)

    fun <E, R> then(decoder: Decoder<E>, mapper: (E) -> R): DecoderComposer4<B, T1, T2, T3, R> =
        DecoderComposer4(this, decoder.mapResult(mapper))

    fun thenItselfOrNull(nullabilityDecoder: Decoder<Boolean>): DecoderComposer4<B, T1, T2, T3, B?> =
        DecoderComposer4(this, thenItselfOrNullStep(nullabilityDecoder))

    fun <R> reduce(reducer: (T1, T2, T3) -> R): DecoderComposer1<B, R> {
        reduceStep { reducer(next(), next(), next()) }
        return DecoderComposer1(this)
    }

}

class DecoderComposer4<B, T1, T2, T3, T4> @PackagePrivate internal constructor(
    previous: DecoderComposer<B, *, *>,
    decoder: Decoder<T4>,
) : DecoderComposer<B, T4, DecoderComposer4<B, T1, T2, T3, T4>>(previous, decoder) {

    fun <R> map(block: (T4) -> Decoder<R>): DecoderComposer4<B, T1, T2, T3, R> = thisCasted { mapStep(block) }

    fun <R> reduce(reducer: (T1, T2, T3, T4) -> R): DecoderComposer1<B, R> {
        reduceStep { reducer(next(), next(), next(), next()) }
        return DecoderComposer1(this)
    }

}
