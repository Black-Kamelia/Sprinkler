package com.kamelia.sprinkler.codec.binary.decoder.composer

import com.kamelia.sprinkler.codec.binary.decoder.BooleanDecoder
import com.kamelia.sprinkler.codec.binary.decoder.core.Decoder
import com.kamelia.sprinkler.codec.binary.decoder.mapResult
import com.zwendo.restrikt.annotation.PackagePrivate


class DecoderComposer2<B, T1, T2> @PackagePrivate internal constructor(
    previous: DecoderComposer<B, *, *>,
    decoder: Decoder<T2>,
) : DecoderComposer<B, T2, DecoderComposer2<B, T1, T2>>(previous, decoder) {

    fun <R> map(block: (T2) -> Decoder<R>): DecoderComposer2<B, T1, R> = thisCast { mapStep(block) }

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

    fun <R> map(block: (T3) -> Decoder<R>): DecoderComposer3<B, T1, T2, R> = thisCast { mapStep(block) }

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

    fun <R> map(block: (T4) -> Decoder<R>): DecoderComposer4<B, T1, T2, T3, R> = thisCast { mapStep(block) }

    fun <R> then(decoder: Decoder<R>): DecoderComposer5<B, T1, T2, T3, T4, R> = DecoderComposer5(this, decoder)

    fun <E, R> then(decoder: Decoder<E>, mapper: (E) -> R): DecoderComposer5<B, T1, T2, T3, T4, R> =
        DecoderComposer5(this, decoder.mapResult(mapper))

    fun thenItselfOrNull(nullabilityDecoder: Decoder<Boolean>): DecoderComposer5<B, T1, T2, T3, T4, B?> =
        DecoderComposer5(this, thenItselfOrNullStep(nullabilityDecoder))

    fun <R> reduce(reducer: (T1, T2, T3, T4) -> R): DecoderComposer1<B, R> {
        reduceStep { reducer(next(), next(), next(), next()) }
        return DecoderComposer1(this)
    }

}

class DecoderComposer5<B, T1, T2, T3, T4, T5> @PackagePrivate internal constructor(
    previous: DecoderComposer<B, *, *>,
    decoder: Decoder<T5>,
) : DecoderComposer<B, T5, DecoderComposer5<B, T1, T2, T3, T4, T5>>(previous, decoder) {

    fun <R> map(block: (T5) -> Decoder<R>): DecoderComposer5<B, T1, T2, T3, T4, R> = thisCast { mapStep(block) }

    fun <R> then(decoder: Decoder<R>): DecoderComposer6<B, T1, T2, T3, T4, T5, R> = DecoderComposer6(this, decoder)

    fun <E, R> then(decoder: Decoder<E>, mapper: (E) -> R): DecoderComposer6<B, T1, T2, T3, T4, T5, R> =
        DecoderComposer6(this, decoder.mapResult(mapper))

    fun thenItselfOrNull(nullabilityDecoder: Decoder<Boolean>): DecoderComposer6<B, T1, T2, T3, T4, T5, B?> =
        DecoderComposer6(this, thenItselfOrNullStep(nullabilityDecoder))

    fun <R> reduce(reducer: (T1, T2, T3, T4, T5) -> R): DecoderComposer1<B, R> {
        reduceStep { reducer(next(), next(), next(), next(), next()) }
        return DecoderComposer1(this)
    }

}

class DecoderComposer6<B, T1, T2, T3, T4, T5, T6> @PackagePrivate internal constructor(
    previous: DecoderComposer<B, *, *>,
    decoder: Decoder<T6>,
) : DecoderComposer<B, T6, DecoderComposer6<B, T1, T2, T3, T4, T5, T6>>(previous, decoder) {

    fun <R> map(block: (T6) -> Decoder<R>): DecoderComposer6<B, T1, T2, T3, T4, T5, R> = thisCast { mapStep(block) }

    fun <R> then(decoder: Decoder<R>): DecoderComposer7<B, T1, T2, T3, T4, T5, T6, R> = DecoderComposer7(this, decoder)

    fun <E, R> then(decoder: Decoder<E>, mapper: (E) -> R): DecoderComposer7<B, T1, T2, T3, T4, T5, T6, R> =
        DecoderComposer7(this, decoder.mapResult(mapper))

    fun thenItselfOrNull(nullabilityDecoder: Decoder<Boolean>): DecoderComposer7<B, T1, T2, T3, T4, T5, T6, B?> =
        DecoderComposer7(this, thenItselfOrNullStep(nullabilityDecoder))

    fun <R> reduce(reducer: (T1, T2, T3, T4, T5, T6) -> R): DecoderComposer1<B, R> {
        reduceStep { reducer(next(), next(), next(), next(), next(), next()) }
        return DecoderComposer1(this)
    }

}

class DecoderComposer7<B, T1, T2, T3, T4, T5, T6, T7> @PackagePrivate internal constructor(
    previous: DecoderComposer<B, *, *>,
    decoder: Decoder<T7>,
) : DecoderComposer<B, T7, DecoderComposer7<B, T1, T2, T3, T4, T5, T6, T7>>(previous, decoder) {

    fun <R> map(block: (T7) -> Decoder<R>): DecoderComposer7<B, T1, T2, T3, T4, T5, T6, R> = thisCast { mapStep(block) }

    fun <R> then(decoder: Decoder<R>): DecoderComposer8<B, T1, T2, T3, T4, T5, T6, T7, R> = DecoderComposer8(this, decoder)

    fun <E, R> then(decoder: Decoder<E>, mapper: (E) -> R): DecoderComposer8<B, T1, T2, T3, T4, T5, T6, T7, R> =
        DecoderComposer8(this, decoder.mapResult(mapper))

    fun thenItselfOrNull(nullabilityDecoder: Decoder<Boolean>): DecoderComposer8<B, T1, T2, T3, T4, T5, T6, T7, B?> =
        DecoderComposer8(this, thenItselfOrNullStep(nullabilityDecoder))

    fun <R> reduce(reducer: (T1, T2, T3, T4, T5, T6, T7) -> R): DecoderComposer1<B, R> {
        reduceStep { reducer(next(), next(), next(), next(), next(), next(), next()) }
        return DecoderComposer1(this)
    }

}

class DecoderComposer8<B, T1, T2, T3, T4, T5, T6, T7, T8> @PackagePrivate internal constructor(
    previous: DecoderComposer<B, *, *>,
    decoder: Decoder<T8>,
) : DecoderComposer<B, T8, DecoderComposer8<B, T1, T2, T3, T4, T5, T6, T7, T8>>(previous, decoder) {

    fun <R> map(block: (T8) -> Decoder<R>): DecoderComposer8<B, T1, T2, T3, T4, T5, T6, T7, R> = thisCast { mapStep(block) }

    fun <R> then(decoder: Decoder<R>): DecoderComposer9<B, T1, T2, T3, T4, T5, T6, T7, T8, R> = DecoderComposer9(this, decoder)

    fun <E, R> then(decoder: Decoder<E>, mapper: (E) -> R): DecoderComposer9<B, T1, T2, T3, T4, T5, T6, T7, T8, R> =
        DecoderComposer9(this, decoder.mapResult(mapper))

    fun thenItselfOrNull(nullabilityDecoder: Decoder<Boolean>): DecoderComposer9<B, T1, T2, T3, T4, T5, T6, T7, T8, B?> =
        DecoderComposer9(this, thenItselfOrNullStep(nullabilityDecoder))

    fun <R> reduce(reducer: (T1, T2, T3, T4, T5, T6, T7, T8) -> R): DecoderComposer1<B, R> {
        reduceStep { reducer(next(), next(), next(), next(), next(), next(), next(), next()) }
        return DecoderComposer1(this)
    }

}

class DecoderComposer9<B, T1, T2, T3, T4, T5, T6, T7, T8, T9> @PackagePrivate internal constructor(
    previous: DecoderComposer<B, *, *>,
    decoder: Decoder<T9>,
) : DecoderComposer<B, T9, DecoderComposer9<B, T1, T2, T3, T4, T5, T6, T7, T8, T9>>(previous, decoder) {

    fun <R> map(block: (T9) -> Decoder<R>): DecoderComposer9<B, T1, T2, T3, T4, T5, T6, T7, T8, R> = thisCast { mapStep(block) }

    fun <R> reduce(reducer: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R): DecoderComposer1<B, R> {
        reduceStep { reducer(next(), next(), next(), next(), next(), next(), next(), next(), next()) }
        return DecoderComposer1(this)
    }

}
