@file:JvmName("Encoders")
@file:JvmMultifileClass

package com.kamelia.sprinkler.codec.binary.encoder

import com.kamelia.sprinkler.codec.binary.core.ASCII_NULL
import com.kamelia.sprinkler.codec.binary.core.UTF16_NULL
import com.kamelia.sprinkler.codec.binary.core.UTF8_NULL
import com.kamelia.sprinkler.codec.binary.encoder.core.Encoder
import com.kamelia.sprinkler.codec.binary.encoder.core.EncoderOutput
import java.nio.ByteOrder
import java.nio.charset.Charset

//region Primitive Encoders

fun ByteEncoder(): Encoder<Byte> =
    Encoder { obj, output ->
        output.write(obj)
    }

@JvmOverloads
fun ShortEncoder(endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Encoder<Short> =
    Encoder { obj, output ->
        val offset = if (endianness == ByteOrder.BIG_ENDIAN) Short.SIZE_BYTES - 1 else 0
        repeat(Short.SIZE_BYTES) {
            output.write((obj.toInt() shr (8 * (offset - it))).toByte())
        }
    }

@JvmOverloads
fun IntEncoder(endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Encoder<Int> =
    Encoder { obj, output ->
        val offset = if (endianness == ByteOrder.BIG_ENDIAN) Int.SIZE_BYTES - 1 else 0
        repeat(Int.SIZE_BYTES) {
            output.write((obj shr (8 * (offset - it))).toByte())
        }
    }

@JvmOverloads
fun LongEncoder(endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Encoder<Long> =
    Encoder { obj, output ->
        val offset = if (endianness == ByteOrder.BIG_ENDIAN) Long.SIZE_BYTES - 1 else 0
        repeat(Long.SIZE_BYTES) {
            output.write((obj shr (8 * (offset - it))).toByte())
        }
    }

@JvmOverloads
fun FloatEncoder(endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Encoder<Float> =
    Encoder { obj, output ->
        val offset = if (endianness == ByteOrder.BIG_ENDIAN) Float.SIZE_BYTES - 1 else 0
        val asInt = obj.toRawBits()
        repeat(Float.SIZE_BYTES) {
            output.write((asInt shr (8 * (offset - it))).toByte())
        }
    }

@JvmOverloads
fun DoubleEncoder(endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Encoder<Double> =
    Encoder { obj, output ->
        val offset = if (endianness == ByteOrder.BIG_ENDIAN) Double.SIZE_BYTES - 1 else 0
        val asLong = obj.toRawBits()
        repeat(Double.SIZE_BYTES) {
            output.write((asLong shr (8 * (offset - it))).toByte())
        }
    }

fun BooleanEncoder(): Encoder<Boolean> = Encoder { obj, output ->
    output.write(if (obj) 1 else 0)
}

//endregion

//region String Encoders

@JvmOverloads
fun UTF8StringEncoder(sizeEncoder: Encoder<Int> = IntEncoder()): Encoder<String> =
    StringEncoder(Charsets.UTF_8, sizeEncoder)

@JvmOverloads
fun UTF8StringEncoderEM(endMarker: ByteArray = UTF8_NULL): Encoder<String> {
    require(endMarker.isNotEmpty()) { "End marker must be at least 1 byte long for UTF-8 (got ${endMarker.size})" }
    return StringEncoderEM(Charsets.UTF_8, endMarker)
}

@JvmOverloads
fun UTF16StringEncoder(sizeEncoder: Encoder<Int> = IntEncoder()): Encoder<String> =
    StringEncoder(Charsets.UTF_16, sizeEncoder)

@JvmOverloads
fun UTF16StringEncoderEM(endMarker: ByteArray = UTF16_NULL): Encoder<String> {
    require(endMarker.size >= 2) { "End marker must be at least 2 bytes long for UTF-16 (got ${endMarker.size})" }
    return StringEncoderEM(Charsets.UTF_16, endMarker)
}

@JvmOverloads
fun ASCIIStringEncoder(sizeEncoder: Encoder<Int> = IntEncoder()): Encoder<String> =
    StringEncoder(Charsets.US_ASCII, sizeEncoder)

@JvmOverloads
fun ASCIIStringEncoderEM(endMarker: ByteArray = ASCII_NULL): Encoder<String> {
    require(endMarker.isNotEmpty()) { "End marker must be at least 1 byte long for ASCII (got ${endMarker.size})" }
    return StringEncoderEM(Charsets.US_ASCII, endMarker)
}

@JvmOverloads
fun StringEncoder(
    charset: Charset,
    sizeEncoder: Encoder<Int> = IntEncoder(),
): Encoder<String> = Encoder { obj, output ->
    val bytes = obj.toByteArray(charset)
    sizeEncoder.encode(bytes.size, output)
    output.write(bytes)
}

@JvmOverloads
fun StringEncoderEM(
    charset: Charset = Charsets.UTF_8,
    endMarker: ByteArray = byteArrayOf(0),
): Encoder<String> = Encoder { obj, output ->
    val bytes = obj.toByteArray(charset)
    output.write(bytes)
    output.write(endMarker)
}

//endregion

//region Basic Encoders

@JvmOverloads
fun <T : Enum<T>> EnumEncoder(intEncoder: Encoder<Int> = IntEncoder()): Encoder<T> = object : Encoder<T> {

    override fun encode(obj: T, output: EncoderOutput): Unit = intEncoder.encode(obj.ordinal, output)

}

@JvmOverloads
fun <T : Enum<T>> EnumEncoderString(
    stringEncoder: Encoder<String> = UTF8StringEncoder(),
): Encoder<T> = object : Encoder<T> {

    override fun encode(obj: T, output: EncoderOutput): Unit = stringEncoder.encode(obj.name, output)

}

//endregion

//region Special Encoders

fun <T> NoOpEncoder(): Encoder<T> = Encoder { _, _ -> /* do nothing */ }

//endregion
