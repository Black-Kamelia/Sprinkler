package com.kamelia.sprinkler.binary.decoder.composer

import com.kamelia.sprinkler.binary.decoder.ByteDecoder
import com.kamelia.sprinkler.binary.decoder.Decoder
import com.kamelia.sprinkler.binary.decoder.StringDecoder


class DecoderComposer1<T>(inner: Decoder<T>) : DecoderComposer<T, DecoderComposer1<*>>(inner) {

    override fun <R> then(nextDecoder: Decoder<R>): DecoderComposer2<T, R> =
        DecoderComposer2(thenDecoder(nextDecoder), this)

    override fun <R> then(nextDecoder: () -> Decoder<R>): DecoderComposer2<T, R> =
        DecoderComposer2(thenDecoder(nextDecoder), this)

    override fun <R> factory(decoder: Decoder<R>): DecoderComposer1<R> = DecoderComposer1(decoder)

    fun <R> finally(resultMapper: (T) -> R): DecoderComposer1<R> = finallyDecoder(resultMapper)
        .let(::DecoderComposer1)

}

class DecoderComposer2<P1, T>(
    inner: Decoder<T>,
    previous: DecoderComposer<*, *>,
) : DecoderComposer<T, DecoderComposer2<*, *>>(inner, previous) {

    override fun <R> then(nextDecoder: Decoder<R>): DecoderComposer3<P1, T, R> =
        DecoderComposer3(thenDecoder(nextDecoder), this)

    override fun <R> then(nextDecoder: () -> Decoder<R>): DecoderComposer3<P1, T, R> =
        DecoderComposer3(thenDecoder(nextDecoder), this)

    override fun <R> factory(decoder: Decoder<R>): DecoderComposer2<P1, R> = DecoderComposer2(decoder, this)

    fun <R> finally(resultMapper: (P1, T) -> R): DecoderComposer1<R> = finallyDecoder {
        @Suppress("UNCHECKED_CAST")
        resultMapper(next() as P1, it)
    }.let(::DecoderComposer1)

}

class DecoderComposer3<P1, P2, T>(
    inner: Decoder<T>,
    previous: DecoderComposer<*, *>,
) : DecoderComposer<T, DecoderComposer3<*, *, *>>(inner, previous) {

    override fun <R> then(nextDecoder: Decoder<R>): DecoderComposer4<P1, P2, T, R> =
        DecoderComposer4(thenDecoder(nextDecoder), this)

    override fun <R> then(nextDecoder: () -> Decoder<R>): DecoderComposer4<P1, P2, T, R> =
        DecoderComposer4(thenDecoder(nextDecoder), this)

    override fun <R> factory(decoder: Decoder<R>): DecoderComposer3<P1, P2, R> = DecoderComposer3(decoder, this)

    fun <R> finally(resultMapper: (P1, P2, T) -> R): DecoderComposer1<R> = finallyDecoder {
        @Suppress("UNCHECKED_CAST")
        resultMapper(next() as P1, next() as P2, it)
    }.let(::DecoderComposer1)

}

class DecoderComposer4<P1, P2, P3, T>(
    inner: Decoder<T>,
    previous: DecoderComposer<*, *>,
) : DecoderComposer<T, DecoderComposer4<*, *, *, *>>(inner, previous) {

    override fun <R> then(nextDecoder: Decoder<R>): DecoderComposer5<P1, P2, P3, T, R> =
        DecoderComposer5(thenDecoder(nextDecoder), this)

    override fun <R> then(nextDecoder: () -> Decoder<R>): DecoderComposer5<P1, P2, P3, T, R> =
        DecoderComposer5(thenDecoder(nextDecoder), this)

    override fun <R> factory(decoder: Decoder<R>): DecoderComposer4<P1, P2, P3, R> = DecoderComposer4(decoder, this)

    fun <R> finally(resultMapper: (P1, P2, P3, T) -> R): DecoderComposer1<R> = finallyDecoder {
        @Suppress("UNCHECKED_CAST")
        resultMapper(next() as P1, next() as P2, next() as P3, it)
    }.let(::DecoderComposer1)

}

class DecoderComposer5<P1, P2, P3, P4, T>(
    inner: Decoder<T>,
    previous: DecoderComposer<*, *>,
) : DecoderComposer<T, DecoderComposer5<*, *, *, *, *>>(inner, previous) {

    override fun <R> then(nextDecoder: Decoder<R>) = TODO()

    override fun <R> then(nextDecoder: () -> Decoder<R>) = TODO()

    override fun <R> factory(decoder: Decoder<R>): DecoderComposer5<P1, P2, P3, P4, R> = DecoderComposer5(decoder, this)

    fun <R> finally(resultMapper: (P1, P2, P3, P4, T) -> R): DecoderComposer1<R> = finallyDecoder {
        @Suppress("UNCHECKED_CAST")
        resultMapper(next() as P1, next() as P2, next() as P3, next() as P4, it)
    }.let(::DecoderComposer1)

}

data class Person(val firstname: String, val lastname: String, val age: Byte, val height: Byte)

fun data(firstname: String, lastname: String, age: Byte, height: Byte): ByteArray {
    val fn = firstname.toByteArray()
    val ln = lastname.toByteArray()
    return byteArrayOf(
        fn.size.toByte(),
        *fn,
        ln.size.toByte(),
        *ln,
        age,
        height
    )
}

fun main() {
    val byteDecoder = ByteDecoder()
    val utf8Decoder = StringDecoder(sizeDecoder = byteDecoder)
    val d = utf8Decoder.compose()
        .then(utf8Decoder)
        .then(byteDecoder)
        .then(byteDecoder)
        .finally(::Person)
        .repeat()
        .assemble()

    val data = byteArrayOf(
        0, 0, 0, 2,
        *data("John", "Doe", 30, 120),
        *data("Jane", "Doe", 25, 110)
    )

    val person = d.decode(data).get()
    println(person)
}
