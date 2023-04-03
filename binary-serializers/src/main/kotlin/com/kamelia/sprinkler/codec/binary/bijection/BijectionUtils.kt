package com.kamelia.sprinkler.codec.binary.bijection

import com.kamelia.sprinkler.codec.binary.decoder.IntDecoder
import com.kamelia.sprinkler.codec.binary.decoder.mapResult
import com.kamelia.sprinkler.codec.binary.decoder.toOptional
import com.kamelia.sprinkler.codec.binary.encoder.IntEncoder
import com.kamelia.sprinkler.codec.binary.encoder.toOptional
import com.kamelia.sprinkler.codec.binary.encoder.withMappedInput
import com.kamelia.sprinkler.util.readInt

fun <T : Any> Bijection<T>.toOptional(booleanBijection: Bijection<Boolean>): Bijection<T?> =
    Bijection.of(encoder.toOptional()) { decoderFactory().toOptional() }

typealias BijectionPool = Map<Class<*>, Bijection<*>>

inline fun <reified T1, reified T2, R> Bijection(
    factory: (T1, T2) -> R,
    bijectionPool: BijectionPool = emptyMap(),
): Bijection<R> {
    val first = bijectionPool[T1::class.java] as? Bijection<T1>
    val second = bijectionPool[T2::class.java] as? Bijection<T2>
    println("${T1::class.java} ${T2::class.java}")
    return TODO()
}


data class MyPair(val a: Int, val b: String)

fun main() {
    val a = IntEncoder().withMappedInput<_, String> { it.toInt() }
    val res = a.encodeToByteArray("123")
    println(res.readInt())

    val x = IntDecoder().mapResult { it.toString() }
    println(x.decode(res))
}
