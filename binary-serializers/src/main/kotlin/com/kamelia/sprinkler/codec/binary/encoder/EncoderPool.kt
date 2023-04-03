package com.kamelia.sprinkler.codec.binary.encoder

import com.kamelia.sprinkler.codec.binary.encoder.core.Encoder
import com.kamelia.sprinkler.codec.binary.util.PoolKey
import kotlin.reflect.KClass

interface EncoderPool {

    fun <T> encoder(key: PoolKey<T>): Encoder<T>

    fun <T> encoder(clazz: Class<T>): Encoder<T> = encoder(PoolKey.of(clazz))

    fun <T : Any> nullableEncoder(clazz: Class<T>): Encoder<T?> = encoder(PoolKey.ofNullable(clazz))

    fun <T> addEncoder(key: PoolKey<T>, encoder: Encoder<T>)

    fun <T> addEncoder(clazz: Class<T>, encoder: Encoder<T>): Unit = addEncoder(PoolKey.of(clazz), encoder)

    fun <T : Any> addNullableEncoder(clazz: Class<T>, encoder: Encoder<T?>): Unit =
        addEncoder(PoolKey.ofNullable(clazz), encoder)

    companion object {

        @JvmField
        val DEFAULT = object : EncoderPool {

            override fun <T> encoder(key: PoolKey<T>): Encoder<T> = throw NoSuchElementException("No encoder for $key")

            override fun <T> addEncoder(key: PoolKey<T>, encoder: Encoder<T>): Unit = Unit

        }
    }

}

inline fun <reified T : Any> EncoderPool.encoder(): Encoder<T> = encoder(PoolKey.of(T::class.primitiveOrSelf))

inline fun <reified T : Any> EncoderPool.encoder(extra: Any): Encoder<T> = encoder(PoolKey.of(T::class.primitiveOrSelf, extra))

inline fun <reified T : Any> EncoderPool.nullableEncoder(): Encoder<T?> = encoder(PoolKey.ofNullable(T::class.primitiveOrSelf))

inline fun <reified T : Any> EncoderPool.addEncoder(encoder: Encoder<T>): Unit =
    addEncoder(PoolKey.of(T::class.primitiveOrSelf), encoder)

inline fun <reified T : Any> EncoderPool.addEncoder(extra: Any, encoder: Encoder<T>): Unit =
    addEncoder(PoolKey.of(T::class.primitiveOrSelf, extra), encoder)

inline fun <reified T : Any> EncoderPool.addNullableEncoder(encoder: Encoder<T?>): Unit =
    addEncoder(PoolKey.ofNullable(T::class.primitiveOrSelf), encoder)



val <T : Any> KClass<T>.primitiveOrSelf: Class<T>
    get() = javaPrimitiveType ?: java
