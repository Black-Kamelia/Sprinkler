package com.kamelia.sprinkler.binary.encoder

import java.nio.charset.Charset

class ComplexEncoderBuilder<T> {

    private var encoders: MutableList<Encoder<T>> = ArrayList()

    fun <E> encodeWith(encoder: Encoder<E>, extractor: T.() -> E): ComplexEncoderBuilder<T> = apply {
        encoders += object : Encoder<T> {
            override fun encode(obj: T): ByteArray = encoder.encode(obj.extractor())

            override fun encode(obj: T, accumulator: EncodingAccumulator) = encoder.encode(obj.extractor(), accumulator)
        }
    }

    fun encodeRecursivelyWith(extractor: T.() -> T?): ComplexEncoderBuilder<T> =
        encodeWith(encoder(encoders).toOptional(), extractor)

    fun build(): Encoder<T> = encoder(encoders.toList()).also {
        // change the list reference to prevent recursive encoding to
        // encode encoders added after this point
        encoders = encoders.toMutableList()
    }

    @JvmName("encodeByte")
    fun encode(extractor: T.() -> Byte): ComplexEncoderBuilder<T> = encodeWith(ByteEncoder, extractor)

    @JvmName("encodeShort")
    fun encode(extractor: T.() -> Short): ComplexEncoderBuilder<T> = encodeWith(ShortEncoder, extractor)

    @JvmName("encodeShortLittleEndian")
    fun encodeLittleEndian(extractor: T.() -> Short): ComplexEncoderBuilder<T> =
        encodeWith(ShortLittleEndianEncoder, extractor)

    @JvmName("encodeInt")
    fun encode(extractor: T.() -> Int): ComplexEncoderBuilder<T> = encodeWith(IntEncoder, extractor)

    @JvmName("encodeIntLittleEndian")
    fun encodeLittleEndian(extractor: T.() -> Int): ComplexEncoderBuilder<T> =
        encodeWith(IntLittleEndianEncoder, extractor)

    @JvmName("encodeLong")
    fun encode(extractor: T.() -> Long): ComplexEncoderBuilder<T> = encodeWith(LongEncoder, extractor)

    @JvmName("encodeLongLittleEndian")
    fun encodeLittleEndian(extractor: T.() -> Long): ComplexEncoderBuilder<T> =
        encodeWith(LongLittleEndianEncoder, extractor)

    @JvmName("encodeFloat")
    fun encode(extractor: T.() -> Float): ComplexEncoderBuilder<T> = encodeWith(FloatEncoder, extractor)

    @JvmName("encodeFloatLittleEndian")
    fun encodeLittleEndian(extractor: T.() -> Float): ComplexEncoderBuilder<T> =
        encodeWith(FloatLittleEndianEncoder, extractor)

    @JvmName("encodeDouble")
    fun encode(extractor: T.() -> Double): ComplexEncoderBuilder<T> = encodeWith(DoubleEncoder, extractor)

    @JvmName("encodeDoubleLittleEndian")
    fun encodeLittleEndian(extractor: T.() -> Double): ComplexEncoderBuilder<T> =
        encodeWith(DoubleLittleEndianEncoder, extractor)

    @JvmName("encodeBoolean")
    fun encode(extractor: T.() -> Boolean): ComplexEncoderBuilder<T> = encodeWith(BooleanEncoder, extractor)

    @JvmName("encodeString")
    fun encode(charset: Charset, sizeEncoder: Encoder<Int>, extractor: T.() -> String): ComplexEncoderBuilder<T> =
        encodeWith(StringEncoder(charset, sizeEncoder), extractor)

    @JvmName("encodeString")
    fun encode(charset: Charset, extractor: T.() -> String): ComplexEncoderBuilder<T> =
        encode(charset, IntEncoder, extractor)

    @JvmName("encodeString")
    fun encode(extractor: T.() -> String): ComplexEncoderBuilder<T> = encodeWith(UTF8StringEncoder, extractor)

    private fun encoder(list: List<Encoder<T>>): Encoder<T> = object : Encoder<T> {

        override fun encode(obj: T): ByteArray = EncodingAccumulator().apply { encode(obj, this) }.toByteArray()

        override fun encode(obj: T, accumulator: EncodingAccumulator) = list.forEach { it.encode(obj, accumulator) }

    }

}
