package com.kamelia.sprinkler.codec.binary.encoder.composer

import com.kamelia.sprinkler.codec.binary.encoder.UTF8StringEncoder
import com.kamelia.sprinkler.codec.binary.encoder.core.Encoder
import com.zwendo.restrikt.annotation.HideFromJava
import com.zwendo.restrikt.annotation.PackagePrivate
import java.nio.ByteOrder
import kotlin.experimental.ExperimentalTypeInference

@Suppress("INAPPLICABLE_JVM_NAME")
sealed interface EncoderComposer<T : Any, B : EncoderComposer.Basic<T>> {

    fun <E> encodeWith(encoder: Encoder<E>, extractor: T.() -> E): B

    @JvmName("encodeByte")
    @OverloadResolutionByLambdaReturnType
    @OptIn(ExperimentalTypeInference::class)
    fun encode(extractor: T.() -> Byte): B

    @JvmName("encodeShort")
    fun encode(extractor: T.() -> Short): B

    @JvmName("encodeInt")
    fun encode(extractor: T.() -> Int): B

    @JvmName("encodeLong")
    fun encode(extractor: T.() -> Long): B

    @JvmName("encodeFloat")
    fun encode(extractor: T.() -> Float): B

    @JvmName("encodeDouble")
    fun encode(extractor: T.() -> Double): B

    @JvmName("encodeBoolean")
    fun encode(extractor: T.() -> Boolean): B

    @JvmName("encodeString")
    fun encode(extractor: T.() -> String): B

    @JvmName("encodeWithSelf")
    fun encodeWithSelf(block: B.(Encoder<T>) -> Unit): B

    fun build(): Encoder<T>

    sealed interface Basic<T : Any> : EncoderComposer<T, Basic<T>> {

        class Builder<T : Any> @PackagePrivate internal constructor() {

            private var endianness = ByteOrder.BIG_ENDIAN
            private var stringEncoderFactory: () -> Encoder<String> = { UTF8StringEncoder() }

            fun endianness(endianness: ByteOrder): Builder<T> = apply {
                this.endianness = endianness
            }

            fun stringEncoderFactory(factory: () -> Encoder<String>): Builder<T> = apply {
                this.stringEncoderFactory = factory
            }

            fun build(): Basic<T> = BasicEncoderComposer(endianness, stringEncoderFactory)

        }

    }

    companion object {

        @JvmStatic
        fun <T : Any> builder(): Basic.Builder<T> = Basic.Builder()

        @JvmStatic
        fun <T : Any> create(): Basic<T> = builder<T>().build()

    }


}

@HideFromJava
fun <T : Any> composedEncoder(): EncoderComposer.Basic<T> = EncoderComposer.create()
