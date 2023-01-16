@file:HideFromJava

package com.kamelia.sprinkler.binary.decoder.composer

import com.kamelia.sprinkler.binary.decoder.Decoder
import com.zwendo.restrikt.annotation.HideFromJava


sealed interface Context0

@JvmName("then0")
fun <T, R> DecoderComposer<T, Context0>.then(nextDecoder: Decoder<R>): DecoderComposer<R, Context1<T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("then0")
fun <T, R> DecoderComposer<T, Context0>.then(nextDecoder: () -> Decoder<R>): DecoderComposer<R, Context1<T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("finally0")
fun <T, R> DecoderComposer<T, Context0>.finally(block: (T) -> R): DecoderComposer<R, Context0> {
    val next = DecoderComposerUtils.finallyDecoder(this) {
        DecoderComposerUtils.ContextIterator(this).run {
            block(it)
        }
    }
    return DecoderComposer.createFrom(this, next)
}

sealed interface Context1<T1>

@JvmName("then1")
fun <T, T1, R> DecoderComposer<T, Context1<T1>>.then(nextDecoder: Decoder<R>): DecoderComposer<R, Context2<T1, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("then1")
fun <T, T1, R> DecoderComposer<T, Context1<T1>>.then(nextDecoder: () -> Decoder<R>): DecoderComposer<R, Context2<T1, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("finally1")
fun <T, T1, R> DecoderComposer<T, Context1<T1>>.finally(block: (T1, T) -> R): DecoderComposer<R, Context0> {
    val next = DecoderComposerUtils.finallyDecoder(this) {
        DecoderComposerUtils.ContextIterator(this).run {
            block(next(), it)
        }
    }
    return DecoderComposer.createFrom(this, next)
}

sealed interface Context2<T1, T2>

@JvmName("then2")
fun <T, T1, T2, R> DecoderComposer<T, Context2<T1, T2>>.then(nextDecoder: Decoder<R>): DecoderComposer<R, Context3<T1, T2, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("then2")
fun <T, T1, T2, R> DecoderComposer<T, Context2<T1, T2>>.then(nextDecoder: () -> Decoder<R>): DecoderComposer<R, Context3<T1, T2, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("finally2")
fun <T, T1, T2, R> DecoderComposer<T, Context2<T1, T2>>.finally(block: (T1, T2, T) -> R): DecoderComposer<R, Context0> {
    val next = DecoderComposerUtils.finallyDecoder(this) {
        DecoderComposerUtils.ContextIterator(this).run {
            block(next(), next(), it)
        }
    }
    return DecoderComposer.createFrom(this, next)
}

sealed interface Context3<T1, T2, T3>

@JvmName("then3")
fun <T, T1, T2, T3, R> DecoderComposer<T, Context3<T1, T2, T3>>.then(nextDecoder: Decoder<R>): DecoderComposer<R, Context4<T1, T2, T3, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("then3")
fun <T, T1, T2, T3, R> DecoderComposer<T, Context3<T1, T2, T3>>.then(nextDecoder: () -> Decoder<R>): DecoderComposer<R, Context4<T1, T2, T3, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("finally3")
fun <T, T1, T2, T3, R> DecoderComposer<T, Context3<T1, T2, T3>>.finally(block: (T1, T2, T3, T) -> R): DecoderComposer<R, Context0> {
    val next = DecoderComposerUtils.finallyDecoder(this) {
        DecoderComposerUtils.ContextIterator(this).run {
            block(next(), next(), next(), it)
        }
    }
    return DecoderComposer.createFrom(this, next)
}

sealed interface Context4<T1, T2, T3, T4>

@JvmName("then4")
fun <T, T1, T2, T3, T4, R> DecoderComposer<T, Context4<T1, T2, T3, T4>>.then(nextDecoder: Decoder<R>): DecoderComposer<R, Context5<T1, T2, T3, T4, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("then4")
fun <T, T1, T2, T3, T4, R> DecoderComposer<T, Context4<T1, T2, T3, T4>>.then(nextDecoder: () -> Decoder<R>): DecoderComposer<R, Context5<T1, T2, T3, T4, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("finally4")
fun <T, T1, T2, T3, T4, R> DecoderComposer<T, Context4<T1, T2, T3, T4>>.finally(block: (T1, T2, T3, T4, T) -> R): DecoderComposer<R, Context0> {
    val next = DecoderComposerUtils.finallyDecoder(this) {
        DecoderComposerUtils.ContextIterator(this).run {
            block(next(), next(), next(), next(), it)
        }
    }
    return DecoderComposer.createFrom(this, next)
}

sealed interface Context5<T1, T2, T3, T4, T5>

@JvmName("then5")
fun <T, T1, T2, T3, T4, T5, R> DecoderComposer<T, Context5<T1, T2, T3, T4, T5>>.then(nextDecoder: Decoder<R>): DecoderComposer<R, Context6<T1, T2, T3, T4, T5, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("then5")
fun <T, T1, T2, T3, T4, T5, R> DecoderComposer<T, Context5<T1, T2, T3, T4, T5>>.then(nextDecoder: () -> Decoder<R>): DecoderComposer<R, Context6<T1, T2, T3, T4, T5, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("finally5")
fun <T, T1, T2, T3, T4, T5, R> DecoderComposer<T, Context5<T1, T2, T3, T4, T5>>.finally(block: (T1, T2, T3, T4, T5, T) -> R): DecoderComposer<R, Context0> {
    val next = DecoderComposerUtils.finallyDecoder(this) {
        DecoderComposerUtils.ContextIterator(this).run {
            block(next(), next(), next(), next(), next(), it)
        }
    }
    return DecoderComposer.createFrom(this, next)
}

sealed interface Context6<T1, T2, T3, T4, T5, T6>

@JvmName("then6")
fun <T, T1, T2, T3, T4, T5, T6, R> DecoderComposer<T, Context6<T1, T2, T3, T4, T5, T6>>.then(nextDecoder: Decoder<R>): DecoderComposer<R, Context7<T1, T2, T3, T4, T5, T6, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("then6")
fun <T, T1, T2, T3, T4, T5, T6, R> DecoderComposer<T, Context6<T1, T2, T3, T4, T5, T6>>.then(nextDecoder: () -> Decoder<R>): DecoderComposer<R, Context7<T1, T2, T3, T4, T5, T6, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("finally6")
fun <T, T1, T2, T3, T4, T5, T6, R> DecoderComposer<T, Context6<T1, T2, T3, T4, T5, T6>>.finally(block: (T1, T2, T3, T4, T5, T6, T) -> R): DecoderComposer<R, Context0> {
    val next = DecoderComposerUtils.finallyDecoder(this) {
        DecoderComposerUtils.ContextIterator(this).run {
            block(next(), next(), next(), next(), next(), next(), it)
        }
    }
    return DecoderComposer.createFrom(this, next)
}

sealed interface Context7<T1, T2, T3, T4, T5, T6, T7>

@JvmName("then7")
fun <T, T1, T2, T3, T4, T5, T6, T7, R> DecoderComposer<T, Context7<T1, T2, T3, T4, T5, T6, T7>>.then(nextDecoder: Decoder<R>): DecoderComposer<R, Context8<T1, T2, T3, T4, T5, T6, T7, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("then7")
fun <T, T1, T2, T3, T4, T5, T6, T7, R> DecoderComposer<T, Context7<T1, T2, T3, T4, T5, T6, T7>>.then(nextDecoder: () -> Decoder<R>): DecoderComposer<R, Context8<T1, T2, T3, T4, T5, T6, T7, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("finally7")
fun <T, T1, T2, T3, T4, T5, T6, T7, R> DecoderComposer<T, Context7<T1, T2, T3, T4, T5, T6, T7>>.finally(block: (T1, T2, T3, T4, T5, T6, T7, T) -> R): DecoderComposer<R, Context0> {
    val next = DecoderComposerUtils.finallyDecoder(this) {
        DecoderComposerUtils.ContextIterator(this).run {
            block(next(), next(), next(), next(), next(), next(), next(), it)
        }
    }
    return DecoderComposer.createFrom(this, next)
}

sealed interface Context8<T1, T2, T3, T4, T5, T6, T7, T8>

@JvmName("then8")
fun <T, T1, T2, T3, T4, T5, T6, T7, T8, R> DecoderComposer<T, Context8<T1, T2, T3, T4, T5, T6, T7, T8>>.then(nextDecoder: Decoder<R>): DecoderComposer<R, Context9<T1, T2, T3, T4, T5, T6, T7, T8, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("then8")
fun <T, T1, T2, T3, T4, T5, T6, T7, T8, R> DecoderComposer<T, Context8<T1, T2, T3, T4, T5, T6, T7, T8>>.then(nextDecoder: () -> Decoder<R>): DecoderComposer<R, Context9<T1, T2, T3, T4, T5, T6, T7, T8, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("finally8")
fun <T, T1, T2, T3, T4, T5, T6, T7, T8, R> DecoderComposer<T, Context8<T1, T2, T3, T4, T5, T6, T7, T8>>.finally(block: (T1, T2, T3, T4, T5, T6, T7, T8, T) -> R): DecoderComposer<R, Context0> {
    val next = DecoderComposerUtils.finallyDecoder(this) {
        DecoderComposerUtils.ContextIterator(this).run {
            block(next(), next(), next(), next(), next(), next(), next(), next(), it)
        }
    }
    return DecoderComposer.createFrom(this, next)
}

sealed interface Context9<T1, T2, T3, T4, T5, T6, T7, T8, T9>

@JvmName("then9")
fun <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, R> DecoderComposer<T, Context9<T1, T2, T3, T4, T5, T6, T7, T8, T9>>.then(
    nextDecoder: Decoder<R>,
): DecoderComposer<R, Context10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("then9")
fun <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, R> DecoderComposer<T, Context9<T1, T2, T3, T4, T5, T6, T7, T8, T9>>.then(
    nextDecoder: () -> Decoder<R>,
): DecoderComposer<R, Context10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("finally9")
fun <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, R> DecoderComposer<T, Context9<T1, T2, T3, T4, T5, T6, T7, T8, T9>>.finally(
    block: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T) -> R,
): DecoderComposer<R, Context0> {
    val next = DecoderComposerUtils.finallyDecoder(this) {
        DecoderComposerUtils.ContextIterator(this).run {
            block(next(), next(), next(), next(), next(), next(), next(), next(), next(), it)
        }
    }
    return DecoderComposer.createFrom(this, next)
}

sealed interface Context10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>

@JvmName("then10")
fun <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> DecoderComposer<T, Context10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>>.then(
    nextDecoder: Decoder<R>,
): DecoderComposer<R, Context11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("then10")
fun <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> DecoderComposer<T, Context10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>>.then(
    nextDecoder: () -> Decoder<R>,
): DecoderComposer<R, Context11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("finally10")
fun <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> DecoderComposer<T, Context10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>>.finally(
    block: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T) -> R,
): DecoderComposer<R, Context0> {
    val next = DecoderComposerUtils.finallyDecoder(this) {
        DecoderComposerUtils.ContextIterator(this).run {
            block(next(), next(), next(), next(), next(), next(), next(), next(), next(), next(), it)
        }
    }
    return DecoderComposer.createFrom(this, next)
}

sealed interface Context11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>

@JvmName("then11")
fun <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> DecoderComposer<T, Context11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>>.then(
    nextDecoder: Decoder<R>,
): DecoderComposer<R, Context12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("then11")
fun <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> DecoderComposer<T, Context11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>>.then(
    nextDecoder: () -> Decoder<R>,
): DecoderComposer<R, Context12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("finally11")
fun <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> DecoderComposer<T, Context11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>>.finally(
    block: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T) -> R,
): DecoderComposer<R, Context0> {
    val next = DecoderComposerUtils.finallyDecoder(this) {
        DecoderComposerUtils.ContextIterator(this).run {
            block(next(), next(), next(), next(), next(), next(), next(), next(), next(), next(), next(), it)
        }
    }
    return DecoderComposer.createFrom(this, next)
}

sealed interface Context12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>

@JvmName("then12")
fun <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> DecoderComposer<T, Context12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>>.then(
    nextDecoder: Decoder<R>,
): DecoderComposer<R, Context13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("then12")
fun <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> DecoderComposer<T, Context12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>>.then(
    nextDecoder: () -> Decoder<R>,
): DecoderComposer<R, Context13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("finally12")
fun <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> DecoderComposer<T, Context12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>>.finally(
    block: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T) -> R,
): DecoderComposer<R, Context0> {
    val next = DecoderComposerUtils.finallyDecoder(this) {
        DecoderComposerUtils.ContextIterator(this).run {
            block(next(), next(), next(), next(), next(), next(), next(), next(), next(), next(), next(), next(), it)
        }
    }
    return DecoderComposer.createFrom(this, next)
}

sealed interface Context13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>

@JvmName("then13")
fun <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> DecoderComposer<T, Context13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>>.then(
    nextDecoder: Decoder<R>,
): DecoderComposer<R, Context14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("then13")
fun <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> DecoderComposer<T, Context13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>>.then(
    nextDecoder: () -> Decoder<R>,
): DecoderComposer<R, Context14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("finally13")
fun <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> DecoderComposer<T, Context13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>>.finally(
    block: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T) -> R,
): DecoderComposer<R, Context0> {
    val next = DecoderComposerUtils.finallyDecoder(this) {
        DecoderComposerUtils.ContextIterator(this).run {
            block(
                next(),
                next(),
                next(),
                next(),
                next(),
                next(),
                next(),
                next(),
                next(),
                next(),
                next(),
                next(),
                next(),
                it
            )
        }
    }
    return DecoderComposer.createFrom(this, next)
}

sealed interface Context14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>

@JvmName("then14")
fun <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> DecoderComposer<T, Context14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>>.then(
    nextDecoder: Decoder<R>,
): DecoderComposer<R, Context15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("then14")
fun <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> DecoderComposer<T, Context14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>>.then(
    nextDecoder: () -> Decoder<R>,
): DecoderComposer<R, Context15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("finally14")
fun <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> DecoderComposer<T, Context14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>>.finally(
    block: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T) -> R,
): DecoderComposer<R, Context0> {
    val next = DecoderComposerUtils.finallyDecoder(this) {
        DecoderComposerUtils.ContextIterator(this).run {
            block(
                next(),
                next(),
                next(),
                next(),
                next(),
                next(),
                next(),
                next(),
                next(),
                next(),
                next(),
                next(),
                next(),
                next(),
                it
            )
        }
    }
    return DecoderComposer.createFrom(this, next)
}

sealed interface Context15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>

@JvmName("finally15")
fun <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> DecoderComposer<T, Context15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>>.finally(
    block: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T) -> R,
): DecoderComposer<R, Context0> {
    val next = DecoderComposerUtils.finallyDecoder(this) {
        DecoderComposerUtils.ContextIterator(this).run {
            block(
                next(),
                next(),
                next(),
                next(),
                next(),
                next(),
                next(),
                next(),
                next(),
                next(),
                next(),
                next(),
                next(),
                next(),
                next(),
                it
            )
        }
    }
    return DecoderComposer.createFrom(this, next)
}
