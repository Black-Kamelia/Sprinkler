@file:JvmName("Encoders")
@file:JvmMultifileClass

package com.kamelia.sprinkler.codec.binary.encoder

import com.kamelia.sprinkler.codec.binary.encoder.core.Encoder
import com.kamelia.sprinkler.codec.binary.encoder.core.EncoderOutput
import java.nio.ByteOrder
import java.nio.charset.Charset

//region Primitive Encoders

fun ByteEncoder(): Encoder<Byte> = object : Encoder<Byte> {

    override fun <R : EncoderOutput> encode(obj: Byte, output: R): R = output.apply { write(obj) }

}

@JvmOverloads
fun ShortEncoder(endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Encoder<Short> = object : Encoder<Short> {

    override fun <O : EncoderOutput> encode(obj: Short, output: O): O = output.apply {
        val offset = if (endianness == ByteOrder.BIG_ENDIAN) Short.SIZE_BYTES - 1 else 0
        repeat(Short.SIZE_BYTES) {
            write((obj.toInt() shr (8 * (offset - it))).toByte())
        }
    }

}

@JvmOverloads
fun IntEncoder(endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Encoder<Int> = object : Encoder<Int> {

    override fun <O : EncoderOutput> encode(obj: Int, output: O): O = output.apply {
        val offset = if (endianness == ByteOrder.BIG_ENDIAN) Int.SIZE_BYTES - 1 else 0
        repeat(Int.SIZE_BYTES) {
            write((obj shr (8 * (offset - it))).toByte())
        }
    }

}

@JvmOverloads
fun LongEncoder(endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Encoder<Long> = object : Encoder<Long> {

    override fun <O : EncoderOutput> encode(obj: Long, output: O): O = output.apply {
        val offset = if (endianness == ByteOrder.BIG_ENDIAN) Long.SIZE_BYTES - 1 else 0
        repeat(Long.SIZE_BYTES) {
            write((obj shr (8 * (offset - it))).toByte())
        }
    }

}

@JvmOverloads
fun FloatEncoder(endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Encoder<Float> = IntEncoder(endianness).withMappedInput {
    it.toRawBits()
}


@JvmOverloads
fun DoubleEncoder(endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Encoder<Double> = LongEncoder(endianness).withMappedInput {
    it.toRawBits()
}


fun BooleanEncoder(): Encoder<Boolean> = ByteEncoder().withMappedInput {
    if (it) 1 else 0
}

//endregion

//region String Encoders

@JvmOverloads
fun UTF8StringEncoder(sizeEncoder: Encoder<Int> = IntEncoder()): Encoder<String> =
    StringEncoder(Charsets.UTF_8, sizeEncoder)

@JvmOverloads
fun UTF8StringEncoderEM(endMarker: ByteArray = byteArrayOf(0)): Encoder<String> {
    require(endMarker.isNotEmpty()) { "End marker must be at least 1 byte long for UTF-8 (got ${endMarker.size})" }
    return StringEncoderEM(Charsets.UTF_8, endMarker)
}

@JvmOverloads
fun UTF16StringEncoder(sizeEncoder: Encoder<Int> = IntEncoder()): Encoder<String> =
    StringEncoder(Charsets.UTF_16, sizeEncoder)

@JvmOverloads
fun UTF16StringEncoderEM(endMarker: ByteArray = byteArrayOf(0)): Encoder<String> {
    require(endMarker.size >= 2) { "End marker must be at least 2 bytes long for UTF-16 (got ${endMarker.size})" }
    return StringEncoderEM(Charsets.UTF_16, endMarker)
}

@JvmOverloads
fun ASCIIStringEncoder(sizeEncoder: Encoder<Int> = IntEncoder()): Encoder<String> =
    StringEncoder(Charsets.US_ASCII, sizeEncoder)

@JvmOverloads
fun ASCIIStringEncoderEM(endMarker: ByteArray = byteArrayOf(0)): Encoder<String> {
    require(endMarker.isNotEmpty()) { "End marker must be at least 1 byte long for ASCII (got ${endMarker.size})" }
    return StringEncoderEM(Charsets.US_ASCII, endMarker)
}

@JvmOverloads
fun StringEncoder(
    charset: Charset,
    sizeEncoder: Encoder<Int> = IntEncoder(),
): Encoder<String> = object : Encoder<String> {

    override fun <R : EncoderOutput> encode(obj: String, output: R): R = output.apply {
        val bytes = obj.toByteArray(charset)
        sizeEncoder.encode(bytes.size, this)
        write(bytes)
    }

}

@JvmOverloads
fun StringEncoderEM(
    charset: Charset = Charsets.UTF_8,
    endMarker: ByteArray = byteArrayOf(0),
): Encoder<String> = object : Encoder<String> {

    override fun <R : EncoderOutput> encode(obj: String, output: R): R = output.apply {
        val bytes = obj.toByteArray(charset)
        write(bytes)
        write(endMarker)
    }

}

//endregion

//region Basic Encoders

@JvmOverloads
fun <T : Enum<T>> EnumEncoder(intEncoder: Encoder<Int> = IntEncoder()): Encoder<T> = object : Encoder<T> {

    override fun <R : EncoderOutput> encode(obj: T, output: R): R = output.apply {
        intEncoder.encode(obj.ordinal, this)
    }

}

@JvmOverloads
fun <T : Enum<T>> EnumEncoderString(
    stringEncoder: Encoder<String> = UTF8StringEncoder(),
): Encoder<T> = object : Encoder<T> {

    override fun <R : EncoderOutput> encode(obj: T, output: R): R = output.apply {
        stringEncoder.encode(obj.name, this)
    }

}

//endregion
