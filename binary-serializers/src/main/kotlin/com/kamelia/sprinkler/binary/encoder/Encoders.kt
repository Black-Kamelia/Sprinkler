package com.kamelia.sprinkler.binary.encoder

import com.kamelia.sprinkler.binary.common.ByteEndianness
import java.nio.ByteBuffer
import java.nio.charset.Charset

//region Primitive Encoders

fun ByteEncoder(): Encoder<Byte> = object : Encoder<Byte> {

    override fun encode(obj: Byte): ByteArray = byteArrayOf(obj)

    override fun encode(obj: Byte, accumulator: EncodingAccumulator) = accumulator.addByte(obj)

}

@JvmOverloads
fun ShortEncoder(endianness: ByteEndianness = ByteEndianness.BIG_ENDIAN): Encoder<Short> = object : Encoder<Short> {

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

@JvmOverloads
fun IntEncoder(endianness: ByteEndianness = ByteEndianness.BIG_ENDIAN): Encoder<Int> = object : Encoder<Int> {

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

@JvmOverloads
fun LongEncoder(endianness: ByteEndianness = ByteEndianness.BIG_ENDIAN): Encoder<Long> = object : Encoder<Long> {

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

@JvmOverloads
fun FloatEncoder(endianness: ByteEndianness = ByteEndianness.BIG_ENDIAN): Encoder<Float> = object : Encoder<Float> {
    private val inner = IntEncoder(endianness)

    override fun encode(obj: Float): ByteArray = inner.encode(obj.toRawBits())

    override fun encode(obj: Float, accumulator: EncodingAccumulator) = inner.encode(obj.toRawBits(), accumulator)

}

@JvmOverloads
fun DoubleEncoder(endianness: ByteEndianness = ByteEndianness.BIG_ENDIAN): Encoder<Double> = object : Encoder<Double> {
    private val inner = LongEncoder(endianness)

    override fun encode(obj: Double): ByteArray = inner.encode(obj.toRawBits())

    override fun encode(obj: Double, accumulator: EncodingAccumulator) = inner.encode(obj.toRawBits(), accumulator)

}

fun BooleanEncoder(): Encoder<Boolean> = object : Encoder<Boolean> {

    override fun encode(obj: Boolean): ByteArray = byteArrayOf(if (obj) 1 else 0)

    override fun encode(obj: Boolean, accumulator: EncodingAccumulator) = accumulator.addByte(if (obj) 1 else 0)

}

//endregion

