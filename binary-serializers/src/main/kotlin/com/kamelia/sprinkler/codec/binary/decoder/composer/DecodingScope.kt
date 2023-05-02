package com.kamelia.sprinkler.codec.binary.decoder.composer

import com.kamelia.sprinkler.codec.binary.decoder.core.Decoder
import java.util.stream.Collector
import java.util.stream.Collectors

sealed interface DecodingScope<E> {

    fun self(usedDirectlyInScope: Boolean): Decoder<E>

    fun self(): Decoder<E> = self(false)

    fun <T> decode(decoder: Decoder<T>): T

    fun <T> oncePerObject(block: () -> T): T

    fun skip(count: Long)

    @JvmName("decodeByte")
    @Suppress("INAPPLICABLE_JVM_NAME")
    fun byte(): Byte

    @JvmName("decodeShort")
    @Suppress("INAPPLICABLE_JVM_NAME")
    fun short(): Short

    @JvmName("decodeInt")
    @Suppress("INAPPLICABLE_JVM_NAME")
    fun int(): Int

    @JvmName("decodeLong")
    @Suppress("INAPPLICABLE_JVM_NAME")
    fun long(): Long

    @JvmName("decodeFloat")
    @Suppress("INAPPLICABLE_JVM_NAME")
    fun float(): Float

    @JvmName("decodeDouble")
    @Suppress("INAPPLICABLE_JVM_NAME")
    fun double(): Double

    @JvmName("decodeBoolean")
    @Suppress("INAPPLICABLE_JVM_NAME")
    fun boolean(): Boolean

    @JvmName("decodeString")
    @Suppress("INAPPLICABLE_JVM_NAME")
    fun string(): String

    @JvmName("decodeSelfOrNull")
    @Suppress("INAPPLICABLE_JVM_NAME")
    fun selfOrNull(nullabilityDecoder: Decoder<Boolean>): E?

    @JvmName("decodeSelfOrNull")
    @Suppress("INAPPLICABLE_JVM_NAME")
    fun selfOrNull(): E?

    @JvmName("decodeSelfCollection")
    @Suppress("INAPPLICABLE_JVM_NAME")
    fun <R> selfCollection(collector: Collector<E, *, R>): R

    @JvmName("decodeSelfList")
    @Suppress("INAPPLICABLE_JVM_NAME")
    fun selfList(): List<E> = selfCollection(Collectors.toList())

    @JvmName("decodeSelfSet")
    @Suppress("INAPPLICABLE_JVM_NAME")
    fun selfSet(): Set<E> = selfCollection(Collectors.toSet())

}
