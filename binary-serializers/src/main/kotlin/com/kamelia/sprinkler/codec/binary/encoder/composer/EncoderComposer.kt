package com.kamelia.sprinkler.codec.binary.encoder.composer

import com.kamelia.sprinkler.codec.binary.encoder.core.Encoder
import kotlin.experimental.ExperimentalTypeInference

@Suppress("INAPPLICABLE_JVM_NAME")
sealed interface EncoderComposer<T : Any, out B> {

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

}
