@file:JvmName("Encoders")
@file:JvmMultifileClass

package com.kamelia.sprinkler.codec.binary.encoder

import com.kamelia.sprinkler.codec.binary.core.ASCII_NULL
import com.kamelia.sprinkler.codec.binary.core.UTF16_NULL
import com.kamelia.sprinkler.codec.binary.core.UTF8_NULL
import com.kamelia.sprinkler.codec.binary.encoder.core.Encoder
import com.kamelia.sprinkler.codec.binary.encoder.core.EncoderOutput
import com.kamelia.sprinkler.util.byte
import java.nio.ByteOrder
import java.nio.charset.Charset

//region Primitive Encoders

fun ByteEncoder(): Encoder<Byte> =
    Encoder { obj, output ->
        output.write(obj)
    }

@JvmOverloads
fun ShortEncoder(endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Encoder<Short> {
    val isLittleEndian = endianness === ByteOrder.LITTLE_ENDIAN
    return Encoder { obj, output ->
        repeat(Short.SIZE_BYTES) {
            val byte = obj.byte(it, isLittleEndian)
            output.write(byte)
        }
    }
}

@JvmOverloads
fun IntEncoder(endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Encoder<Int> {
    val isLittleEndian = endianness === ByteOrder.LITTLE_ENDIAN
    return Encoder { obj, output ->
        repeat(Int.SIZE_BYTES) {
            val byte = obj.byte(it, isLittleEndian)
            output.write(byte)
        }
    }
}

@JvmOverloads
fun LongEncoder(endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Encoder<Long> {
    val isLittleEndian = endianness === ByteOrder.LITTLE_ENDIAN
    return Encoder { obj, output ->
        repeat(Long.SIZE_BYTES) {
            val byte = obj.byte(it, isLittleEndian)
            output.write(byte)
        }
    }
}

@JvmOverloads
fun FloatEncoder(endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Encoder<Float> {
    val isLittleEndian = endianness === ByteOrder.LITTLE_ENDIAN
    return Encoder { obj, output ->
        val asInt = obj.toRawBits()
        repeat(Float.SIZE_BYTES) {
            val byte = asInt.byte(it, isLittleEndian)
            output.write(byte)
        }
    }
}

@JvmOverloads
fun DoubleEncoder(endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Encoder<Double> {
    val isLittleEndian = endianness === ByteOrder.LITTLE_ENDIAN
    return Encoder { obj, output ->
        val asLong = obj.toRawBits()
        repeat(Double.SIZE_BYTES) {
            val byte = asLong.byte(it, isLittleEndian)
            output.write(byte)
        }
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

fun StringEncoderEM(
    charset: Charset,
    endMarker: ByteArray,
): Encoder<String> {
    val copy = endMarker.copyOf()
    return Encoder { obj, output ->
        val bytes = obj.toByteArray(charset)
        output.write(bytes)
        output.write(copy)
    }
}

//endregion

//region Basic Encoders

@JvmOverloads
fun <T : Enum<T>> EnumEncoder(intEncoder: Encoder<Int> = IntEncoder()): Encoder<T> =
    Encoder { obj, output -> intEncoder.encode(obj.ordinal, output) }

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
