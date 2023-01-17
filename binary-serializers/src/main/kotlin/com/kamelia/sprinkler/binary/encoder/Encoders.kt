@file:JvmName("Encoders")

package com.kamelia.sprinkler.binary.encoder

import com.kamelia.sprinkler.binary.common.ByteEndianness
import java.nio.charset.Charset

//region Primitive Encoders

@JvmField
val ByteEncoder: Encoder<Byte> = object : Encoder<Byte> {

    override fun encode(obj: Byte): ByteArray = byteArrayOf(obj)

    override fun encode(obj: Byte, accumulator: EncodingAccumulator) = accumulator.addByte(obj)

}

@JvmField
val ShortEncoder: Encoder<Short> = shortEncoder(ByteEndianness.BIG_ENDIAN)

@JvmField
val ShortLittleEndianEncoder: Encoder<Short> = shortEncoder(ByteEndianness.LITTLE_ENDIAN)

@JvmField
val IntEncoder: Encoder<Int> = intEncoder(ByteEndianness.BIG_ENDIAN)

@JvmField
val IntLittleEndianEncoder: Encoder<Int> = intEncoder(ByteEndianness.LITTLE_ENDIAN)

@JvmField
val LongEncoder: Encoder<Long> = longEncoder(ByteEndianness.BIG_ENDIAN)

@JvmField
val LongLittleEndianEncoder: Encoder<Long> = longEncoder(ByteEndianness.LITTLE_ENDIAN)

@JvmField
val FloatEncoder: Encoder<Float> = floatEncoder(ByteEndianness.BIG_ENDIAN)

@JvmField
val FloatLittleEndianEncoder: Encoder<Float> = floatEncoder(ByteEndianness.LITTLE_ENDIAN)

@JvmField
val DoubleEncoder: Encoder<Double> = doubleEncoder(ByteEndianness.BIG_ENDIAN)

@JvmField
val DoubleLittleEndianEncoder: Encoder<Double> = doubleEncoder(ByteEndianness.LITTLE_ENDIAN)


@JvmField
val BooleanEncoder: Encoder<Boolean> = object : Encoder<Boolean> {

    override fun encode(obj: Boolean): ByteArray = byteArrayOf(if (obj) 1 else 0)

    override fun encode(obj: Boolean, accumulator: EncodingAccumulator) = accumulator.addByte(if (obj) 1 else 0)

}

//endregion

//region String Encoders

@JvmOverloads
fun StringEncoder(charset: Charset = Charsets.UTF_8, sizeEncoder: Encoder<Int>): Encoder<String> = Encoder {
    val bytes = it.toByteArray(charset)
    val sizeBytes = sizeEncoder.encode(bytes.size)
    sizeBytes + bytes
}

@JvmOverloads
fun StringEncoder(charset: Charset = Charsets.UTF_8): Encoder<String> = StringEncoder(charset, IntEncoder)

@JvmOverloads
fun StringEncoderEM(
    charset: Charset = Charsets.UTF_8,
    endMarker: ByteArray = byteArrayOf(0),
): Encoder<String> = Encoder {
    it.toByteArray(charset) + endMarker
}

//endregion

//region Internal

private fun shortEncoder(endianness: ByteEndianness): Encoder<Short> = object : Encoder<Short> {

    override fun encode(obj: Short): ByteArray = ByteArray(Short.SIZE_BYTES) {
        if (endianness.isBigEndian) {
            (obj.toInt() shr (8 * (Short.SIZE_BYTES - 1 - it))).toByte()
        } else {
            (obj.toInt() shr (8 * it)).toByte()
        }
    }

    override fun encode(obj: Short, accumulator: EncodingAccumulator) {
        val offset = if (endianness.isBigEndian) Short.SIZE_BYTES - 1 else 0
        repeat(Short.SIZE_BYTES) {
            accumulator.addByte((obj.toInt() shr (8 * (offset - it))).toByte())
        }
    }

}

private fun intEncoder(endianness: ByteEndianness): Encoder<Int> = object : Encoder<Int> {

    override fun encode(obj: Int): ByteArray = ByteArray(Int.SIZE_BYTES) {
        if (endianness.isBigEndian) {
            (obj shr (8 * (Int.SIZE_BYTES - 1 - it))).toByte()
        } else {
            (obj shr (8 * it)).toByte()
        }
    }

    override fun encode(obj: Int, accumulator: EncodingAccumulator) {
        val offset = if (endianness.isBigEndian) Int.SIZE_BYTES - 1 else 0
        repeat(Int.SIZE_BYTES) {
            accumulator.addByte((obj shr (8 * (offset - it))).toByte())
        }
    }

}

private fun longEncoder(endianness: ByteEndianness): Encoder<Long> = object : Encoder<Long> {

    override fun encode(obj: Long): ByteArray = ByteArray(Long.SIZE_BYTES) {
        if (endianness.isBigEndian) {
            (obj shr (8 * (Long.SIZE_BYTES - 1 - it))).toByte()
        } else {
            (obj shr (8 * it)).toByte()
        }
    }

    override fun encode(obj: Long, accumulator: EncodingAccumulator) {
        val offset = if (endianness.isBigEndian) Long.SIZE_BYTES - 1 else 0
        repeat(Long.SIZE_BYTES) {
            accumulator.addByte((obj shr (8 * (offset - it))).toByte())
        }
    }

}

private fun floatEncoder(endianness: ByteEndianness): Encoder<Float> = object : Encoder<Float> {
    private val inner = if (endianness.isBigEndian) IntEncoder else IntLittleEndianEncoder

    override fun encode(obj: Float): ByteArray = inner.encode(obj.toRawBits())

    override fun encode(obj: Float, accumulator: EncodingAccumulator) = inner.encode(obj.toRawBits(), accumulator)

}

private fun doubleEncoder(endianness: ByteEndianness): Encoder<Double> = object : Encoder<Double> {
    private val inner = if (endianness.isBigEndian) LongEncoder else LongLittleEndianEncoder

    override fun encode(obj: Double): ByteArray = inner.encode(obj.toRawBits())

    override fun encode(obj: Double, accumulator: EncodingAccumulator) = inner.encode(obj.toRawBits(), accumulator)

}

//endregion
