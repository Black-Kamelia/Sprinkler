@file:HideFromJava

package com.kamelia.sprinkler.binary.decoder.composer

import com.kamelia.sprinkler.binary.decoder.Decoder
import com.zwendo.restrikt.annotation.HideFromJava


sealed interface Context0

fun <T, R> DecoderComposer<T, Context0>.then(nextDecoder: Decoder<R>): DecoderComposer<R, Context1<T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

fun <T, R> DecoderComposer<T, Context0>.then(nextDecoder: () -> Decoder<R>): DecoderComposer<R, Context1<T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

fun <T, R> DecoderComposer<T, Context0>.finally(block: (T) -> R): DecoderComposer<R, Context0> {
    val next = DecoderComposerUtils.finallyDecoder(this, block)
    return DecoderComposer.createFrom(this, next)
}

sealed interface Context1<T1>

@JvmName("then1")
fun <T, T1, R> DecoderComposer<T, Context1<T1>>.then(nextDecoder: Decoder<R>): DecoderComposer<R, Context2<T1, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("then1")
fun <T, T1, R> DecoderComposer<T, Context1<T1>>.then(
    nextDecoder: () -> Decoder<R>,
): DecoderComposer<R, Context2<T1, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("finally1")
fun <T, T1, R> DecoderComposer<T, Context1<T>>.finally(block: (T1, T) -> R): DecoderComposer<R, Context0> {
    val next = DecoderComposerUtils.finallyDecoder(this) {
        DecoderComposerUtils.ContextIterator(this).run {
            block(next(), it)
        }
    }
    return DecoderComposer.createFrom(this, next)
}


sealed interface Context2<T1, T2>

@JvmName("then2")
fun <T, T1, T2, R> DecoderComposer<T, Context2<T1, T2>>.then(
    nextDecoder: Decoder<R>,
): DecoderComposer<R, Context3<T1, T2, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("then2")
fun <T, T1, T2, R> DecoderComposer<T, Context2<T1, T2>>.then(
    nextDecoder: () -> Decoder<R>,
): DecoderComposer<R, Context3<T1, T2, T>> {
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
fun <T, T1, T2, T3, R> DecoderComposer<T, Context3<T1, T2, T3>>.then(
    nextDecoder: Decoder<R>,
): DecoderComposer<R, Context4<T1, T2, T3, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("then3")
fun <T, T1, T2, T3, R> DecoderComposer<T, Context3<T1, T2, T3>>.then(
    nextDecoder: () -> Decoder<R>,
): DecoderComposer<R, Context4<T1, T2, T3, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("finally3")
fun <T, T1, T2, T3, R> DecoderComposer<T, Context3<T1, T2, T3>>.finally(
    block: (T1, T2, T3, T) -> R,
): DecoderComposer<R, Context0> {
    val next = DecoderComposerUtils.finallyDecoder(this) {
        DecoderComposerUtils.ContextIterator(this).run {
            block(next(), next(), next(), it)
        }
    }
    return DecoderComposer.createFrom(this, next)
}


sealed interface Context4<T1, T2, T3, T4>

@JvmName("then4")
fun <T, T1, T2, T3, T4, R> DecoderComposer<T, Context4<T1, T2, T3, T4>>.then(
    nextDecoder: Decoder<R>,
): DecoderComposer<R, Context5<T1, T2, T3, T4, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("then4")
fun <T, T1, T2, T3, T4, R> DecoderComposer<T, Context4<T1, T2, T3, T4>>.then(
    nextDecoder: () -> Decoder<R>,
): DecoderComposer<R, Context5<T1, T2, T3, T4, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("finally4")
fun <T, T1, T2, T3, T4, R> DecoderComposer<T, Context4<T1, T2, T3, T4>>.finally(
    block: (T1, T2, T3, T4, T) -> R,
): DecoderComposer<R, Context0> {
    val next = DecoderComposerUtils.finallyDecoder(this) {
        DecoderComposerUtils.ContextIterator(this).run {
            block(next(), next(), next(), next(), it)
        }
    }
    return DecoderComposer.createFrom(this, next)
}


sealed interface Context5<T1, T2, T3, T4, T5>

@JvmName("then5")
fun <T, T1, T2, T3, T4, T5, R> DecoderComposer<T, Context5<T1, T2, T3, T4, T5>>.then(
    nextDecoder: Decoder<R>,
): DecoderComposer<R, Context6<T1, T2, T3, T4, T5, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("then5")
fun <T, T1, T2, T3, T4, T5, R> DecoderComposer<T, Context5<T1, T2, T3, T4, T5>>.then(
    nextDecoder: () -> Decoder<R>,
): DecoderComposer<R, Context6<T1, T2, T3, T4, T5, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("finally5")
fun <T, T1, T2, T3, T4, T5, R> DecoderComposer<T, Context5<T1, T2, T3, T4, T5>>.finally(
    block: (T1, T2, T3, T4, T5, T) -> R,
): DecoderComposer<R, Context0> {
    val next = DecoderComposerUtils.finallyDecoder(this) {
        DecoderComposerUtils.ContextIterator(this).run {
            block(next(), next(), next(), next(), next(), it)
        }
    }
    return DecoderComposer.createFrom(this, next)
}


sealed interface Context6<T1, T2, T3, T4, T5, T6>

@JvmName("then6")
fun <T, T1, T2, T3, T4, T5, T6, R> DecoderComposer<T, Context6<T1, T2, T3, T4, T5, T6>>.then(
    nextDecoder: Decoder<R>,
): DecoderComposer<R, Context7<T1, T2, T3, T4, T5, T6, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("then6")
fun <T, T1, T2, T3, T4, T5, T6, R> DecoderComposer<T, Context6<T1, T2, T3, T4, T5, T6>>.then(
    nextDecoder: () -> Decoder<R>,
): DecoderComposer<R, Context7<T1, T2, T3, T4, T5, T6, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("finally6")
fun <T, T1, T2, T3, T4, T5, T6, R> DecoderComposer<T, Context6<T1, T2, T3, T4, T5, T6>>.finally(
    block: (T1, T2, T3, T4, T5, T6, T) -> R,
): DecoderComposer<R, Context0> {
    val next = DecoderComposerUtils.finallyDecoder(this) {
        DecoderComposerUtils.ContextIterator(this).run {
            block(next(), next(), next(), next(), next(), next(), it)
        }
    }
    return DecoderComposer.createFrom(this, next)
}


sealed interface Context7<T1, T2, T3, T4, T5, T6, T7>

@JvmName("then7")
fun <T, T1, T2, T3, T4, T5, T6, T7, R> DecoderComposer<T, Context7<T1, T2, T3, T4, T5, T6, T7>>.then(
    nextDecoder: Decoder<R>,
): DecoderComposer<R, Context8<T1, T2, T3, T4, T5, T6, T7, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("then7")
fun <T, T1, T2, T3, T4, T5, T6, T7, R> DecoderComposer<T, Context7<T1, T2, T3, T4, T5, T6, T7>>.then(
    nextDecoder: () -> Decoder<R>,
): DecoderComposer<R, Context8<T1, T2, T3, T4, T5, T6, T7, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("finally7")
fun <T, T1, T2, T3, T4, T5, T6, T7, R> DecoderComposer<T, Context7<T1, T2, T3, T4, T5, T6, T7>>.finally(
    block: (T1, T2, T3, T4, T5, T6, T7, T) -> R,
): DecoderComposer<R, Context0> {
    val next = DecoderComposerUtils.finallyDecoder(this) {
        DecoderComposerUtils.ContextIterator(this).run {
            block(next(), next(), next(), next(), next(), next(), next(), it)
        }
    }
    return DecoderComposer.createFrom(this, next)
}

sealed interface Context8<T1, T2, T3, T4, T5, T6, T7, T8>

@JvmName("then8")
fun <T, T1, T2, T3, T4, T5, T6, T7, T8, R> DecoderComposer<T, Context8<T1, T2, T3, T4, T5, T6, T7, T8>>.then(
    nextDecoder: Decoder<R>,
): DecoderComposer<R, Context9<T1, T2, T3, T4, T5, T6, T7, T8, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("then8")
fun <T, T1, T2, T3, T4, T5, T6, T7, T8, R> DecoderComposer<T, Context8<T1, T2, T3, T4, T5, T6, T7, T8>>.then(
    nextDecoder: () -> Decoder<R>,
): DecoderComposer<R, Context9<T1, T2, T3, T4, T5, T6, T7, T8, T>> {
    val next = DecoderComposerUtils.thenDecoder(this, nextDecoder)
    return DecoderComposer.createFrom(this, next)
}

@JvmName("finally8")
fun <T, T1, T2, T3, T4, T5, T6, T7, T8, R> DecoderComposer<T, Context8<T1, T2, T3, T4, T5, T6, T7, T8>>.finally(
    block: (T1, T2, T3, T4, T5, T6, T7, T8, T) -> R,
): DecoderComposer<R, Context0> {
    val next = DecoderComposerUtils.finallyDecoder(this) {
        DecoderComposerUtils.ContextIterator(this).run {
            block(next(), next(), next(), next(), next(), next(), next(), next(), it)
        }
    }
    return DecoderComposer.createFrom(this, next)
}

sealed interface Context9<T1, T2, T3, T4, T5, T6, T7, T8, T9>

sealed interface Context10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>

sealed interface Context11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>

sealed interface Context12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>

sealed interface Context13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>

sealed interface Context14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>

sealed interface Context15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>
