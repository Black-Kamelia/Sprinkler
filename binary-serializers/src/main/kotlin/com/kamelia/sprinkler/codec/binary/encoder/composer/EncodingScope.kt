package com.kamelia.sprinkler.codec.binary.encoder.composer

import com.kamelia.sprinkler.codec.binary.encoder.core.Encoder
import com.kamelia.sprinkler.util.jvmlambda.KotlinDslAdapter

sealed interface EncodingScope<E> : KotlinDslAdapter {

    val self: Encoder<E>

    @JvmName("encodeWith")
    @Suppress("INAPPLICABLE_JVM_NAME")
    fun <T> encode(obj: T, encoder: Encoder<T>): EncodingScope<E>

    fun encode(obj: Byte): EncodingScope<E>

    fun encode(obj: Short): EncodingScope<E>

    fun encode(obj: Int): EncodingScope<E>

    fun encode(obj: Long): EncodingScope<E>

    fun encode(obj: Float): EncodingScope<E>

    fun encode(obj: Double): EncodingScope<E>

    fun encode(obj: Boolean): EncodingScope<E>

    fun encode(obj: String): EncodingScope<E>

    fun encode(obj: E?, nullabilityEncoder: Encoder<Boolean>): EncodingScope<E>

    fun encode(obj: E?): EncodingScope<E>

    fun encode(obj: Collection<E>, sizeEncoder: Encoder<Int>): EncodingScope<E>

    fun encode(obj: Collection<E>): EncodingScope<E>

    fun encode(obj: Iterable<E>, endMarker: E): EncodingScope<E>

    fun encode(obj: Array<E>, sizeEncoder: Encoder<Int>): EncodingScope<E>

    fun encode(obj: Array<E>): EncodingScope<E>

}
