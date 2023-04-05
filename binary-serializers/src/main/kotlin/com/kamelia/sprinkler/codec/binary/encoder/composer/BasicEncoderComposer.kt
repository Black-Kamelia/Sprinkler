package com.kamelia.sprinkler.codec.binary.encoder.composer

import com.kamelia.sprinkler.codec.binary.encoder.BooleanEncoder
import com.kamelia.sprinkler.codec.binary.encoder.ByteEncoder
import com.kamelia.sprinkler.codec.binary.encoder.DoubleEncoder
import com.kamelia.sprinkler.codec.binary.encoder.FloatEncoder
import com.kamelia.sprinkler.codec.binary.encoder.IntEncoder
import com.kamelia.sprinkler.codec.binary.encoder.LongEncoder
import com.kamelia.sprinkler.codec.binary.encoder.ShortEncoder
import com.kamelia.sprinkler.codec.binary.encoder.UTF8StringEncoder
import com.kamelia.sprinkler.codec.binary.encoder.core.Encoder
import com.kamelia.sprinkler.codec.binary.encoder.core.EncoderOutput
import com.zwendo.restrikt.annotation.HideFromJava
import com.zwendo.restrikt.annotation.PackagePrivate
import java.nio.ByteOrder

@Suppress("INAPPLICABLE_JVM_NAME")
class BasicEncoderComposer<T : Any> private constructor(
    private val endianness: ByteOrder,
    private val stringEncoderFactory: () -> Encoder<String>,
) : EncoderComposer<T, BasicEncoderComposer<T>> {

    private var list = ArrayList<(T, EncoderOutput) -> Unit>()

    private val map by lazy { HashMap<Class<*>, Encoder<*>>() }

    private var inner = encoder(list)

    companion object {

        @JvmStatic
        fun <T : Any> builder(): Builder<T> = Builder()

        @JvmStatic
        fun <T : Any> create(): BasicEncoderComposer<T> = builder<T>().build()

    }

    class Builder<T : Any> @PackagePrivate internal constructor() {

        private var endianness = ByteOrder.BIG_ENDIAN
        private var stringEncoderFactory: () -> Encoder<String> = { UTF8StringEncoder() }

        fun endianness(endianness: ByteOrder): Builder<T> = apply {
            this.endianness = endianness
        }

        fun stringEncoderFactory(factory: () -> Encoder<String>): Builder<T> = apply {
            this.stringEncoderFactory = factory
        }

        fun build(): BasicEncoderComposer<T> = BasicEncoderComposer(endianness, stringEncoderFactory)

    }

    override fun <E> encodeWith(encoder: Encoder<E>, extractor: T.() -> E): BasicEncoderComposer<T> = apply {
        list += { value, output -> encoder.encode(value.extractor(), output) }
    }

    @JvmName("encodeByte")
    override fun encode(extractor: T.() -> Byte): BasicEncoderComposer<T> = encodeWith(extractor, ::ByteEncoder)

    @JvmName("encodeShort")
    override fun encode(extractor: T.() -> Short): BasicEncoderComposer<T> =
        encodeWith(extractor) { ShortEncoder(endianness) }

    @JvmName("encodeInt")
    override fun encode(extractor: T.() -> Int): BasicEncoderComposer<T> =
        encodeWith(extractor) { IntEncoder(endianness) }

    @JvmName("encodeLong")
    override fun encode(extractor: T.() -> Long): BasicEncoderComposer<T> =
        encodeWith(extractor) { LongEncoder(endianness) }

    @JvmName("encodeFloat")
    override fun encode(extractor: T.() -> Float): BasicEncoderComposer<T> =
        encodeWith(extractor) { FloatEncoder(endianness) }

    @JvmName("encodeDouble")
    override fun encode(extractor: T.() -> Double): BasicEncoderComposer<T> =
        encodeWith(extractor) { DoubleEncoder(endianness) }

    @JvmName("encodeBoolean")
    override fun encode(extractor: T.() -> Boolean): BasicEncoderComposer<T> = encodeWith(extractor, ::BooleanEncoder)

    @JvmName("encodeString")
    override fun encode(extractor: T.() -> String): BasicEncoderComposer<T> =
        encodeWith(extractor, stringEncoderFactory)

    override fun encodeWithSelf(block: BasicEncoderComposer<T>.(Encoder<T>) -> Unit): BasicEncoderComposer<T> = apply {
        block(this, inner)
    }

    private inline fun <reified E> encodeWith(
        noinline extractor: T.() -> E,
        crossinline block: () -> Encoder<E>,
    ): BasicEncoderComposer<T> {
        @Suppress("UNCHECKED_CAST")
        val encoder = map.computeIfAbsent(E::class.java) { block() } as Encoder<E>
        return encodeWith(encoder, extractor)
    }

    override fun build(): Encoder<T> {
        val result = inner
        list = ArrayList()
        inner = encoder(list)
        return result
    }

    private fun encoder(list: ArrayList<(T, EncoderOutput) -> Unit>): Encoder<T> = object : Encoder<T> {

        override fun encode(obj: T, output: EncoderOutput) = list.forEach { it(obj, output) }

    }

}

@HideFromJava
fun <T : Any> composedEncoder(): BasicEncoderComposer<T> = BasicEncoderComposer.create()
