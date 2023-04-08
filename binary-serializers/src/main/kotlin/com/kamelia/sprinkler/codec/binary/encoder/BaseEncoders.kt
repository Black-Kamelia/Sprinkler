@file:JvmName("Encoders")
@file:JvmMultifileClass

package com.kamelia.sprinkler.codec.binary.encoder

import com.kamelia.sprinkler.codec.binary.core.ASCII_NULL
import com.kamelia.sprinkler.codec.binary.core.UTF16_NULL
import com.kamelia.sprinkler.codec.binary.core.UTF8_NULL
import com.kamelia.sprinkler.codec.binary.encoder.core.Encoder
import com.kamelia.sprinkler.util.byte
import java.nio.ByteOrder
import java.nio.charset.Charset

//region Primitive Encoders

/**
 * Creates an [Encoder] that writes a single [Byte] to the output.
 *
 * @return an [Encoder] that writes a single [Byte] to the output
 */
fun ByteEncoder(): Encoder<Byte> =
    Encoder { obj, output -> output.write(obj) }

/**
 * Creates an [Encoder] that writes a [Short] to the output. The endianness of the short is specified by the
 * [endianness] parameter.
 *
 * @param endianness the endianness of the short (defaults to [ByteOrder.BIG_ENDIAN])
 * @return an [Encoder] that writes a [Short] to the output
 */
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

/**
 * Creates an [Encoder] that writes an [Int] to the output. The endianness of the int is specified by the
 * [endianness] parameter.
 *
 * @param endianness the endianness of the int (defaults to [ByteOrder.BIG_ENDIAN])
 * @return an [Encoder] that writes an [Int] to the output
 */
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

/**
 * Creates an [Encoder] that writes a [Long] to the output. The endianness of the long is specified by the
 * [endianness] parameter.
 *
 * @param endianness the endianness of the long (defaults to [ByteOrder.BIG_ENDIAN])
 * @return an [Encoder] that writes a [Long] to the output
 */
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

/**
 * Creates an [Encoder] that writes a [Float] to the output. The endianness of the float is specified by the
 * [endianness] parameter.
 *
 * @param endianness the endianness of the float (defaults to [ByteOrder.BIG_ENDIAN])
 * @return an [Encoder] that writes a [Float] to the output
 */
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

/**
 * Creates an [Encoder] that writes a [Double] to the output. The endianness of the double is specified by the
 * [endianness] parameter.
 *
 * @param endianness the endianness of the double (defaults to [ByteOrder.BIG_ENDIAN])
 * @return an [Encoder] that writes a [Double] to the output
 */
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

/**
 * Creates an [Encoder] that writes a [Boolean] to the output. The boolean is encoded as a single byte, where 0
 * represents `false` and 1 represents `true`.
 *
 * @return an [Encoder] that writes a [Boolean] to the output
 */
fun BooleanEncoder(): Encoder<Boolean> = Encoder { obj, output ->
    output.write(if (obj) 1 else 0)
}

//endregion

//region String Encoders

/**
 * Creates an [Encoder] that writes a [String] to the output.
 *
 * The string is encoded using the [uft8][Charsets.UTF_8] charset, and is prefixed with the number of bytes of the
 * encoded string. The size of the string is encoded using the [sizeEncoder] parameter.
 *
 * @param sizeEncoder the encoder to use for encoding the size of the string (defaults to the default [IntEncoder])
 * @return an [Encoder] that writes a [String] to the output
 */
@JvmOverloads
fun UTF8StringEncoder(sizeEncoder: Encoder<Int> = IntEncoder()): Encoder<String> =
    StringEncoder(Charsets.UTF_8, sizeEncoder)

/**
 * Creates an [Encoder] that writes a [String] to the output.
 *
 * The string is encoded using the [uft8][Charsets.UTF_8] charset, and is terminated by a predefined sequence of bytes
 * represented by the [endMarker] parameter.
 *
 * @param endMarker the sequence of bytes that will be written at the end of the string (defaults to [UTF8_NULL]).
 * @return an [Encoder] that writes a [String] to the output
 * @throws IllegalArgumentException if the [endMarker] is empty
 */
@JvmOverloads
fun UTF8StringEncoderEM(endMarker: ByteArray = UTF8_NULL): Encoder<String> = StringEncoder(Charsets.UTF_8, endMarker)

/**
 * Creates an [Encoder] that writes a [String] to the output.
 *
 * The string is encoded using the [uft16][Charsets.UTF_16] charset, and is prefixed with the number of bytes of the
 * encoded string. The size of the string is encoded using the [sizeEncoder] parameter.
 *
 * @param sizeEncoder the encoder to use for encoding the size of the string (defaults to the default [IntEncoder])
 * @return an [Encoder] that writes a [String] to the output
 */
@JvmOverloads
fun UTF16StringEncoder(sizeEncoder: Encoder<Int> = IntEncoder()): Encoder<String> =
    StringEncoder(Charsets.UTF_16, sizeEncoder)

/**
 * Creates an [Encoder] that writes a [String] to the output.
 *
 * The string is encoded using the [uft16][Charsets.UTF_16] charset, and is terminated by a predefined sequence of bytes
 * represented by the [endMarker] parameter.
 *
 * @param endMarker the sequence of bytes that will be written at the end of the string (defaults to [UTF16_NULL])
 * @return an [Encoder] that writes a [String] to the output
 * @throws IllegalArgumentException if the [endMarker] is less than 2 bytes long
 */
@JvmOverloads
fun UTF16StringEncoderEM(endMarker: ByteArray = UTF16_NULL): Encoder<String> {
    require(endMarker.size >= 2) { "End marker must be at least 2 bytes long for UTF-16 (got ${endMarker.size})" }
    return StringEncoder(Charsets.UTF_16, endMarker)
}

/**
 * Creates an [Encoder] that writes a [String] to the output.
 *
 * The string is encoded using the [ascii][Charsets.US_ASCII] charset, and is prefixed with the number of bytes of the
 * encoded string. The size of the string is encoded using the [sizeEncoder] parameter.
 *
 * @param sizeEncoder the encoder to use for encoding the size of the string (defaults to the default [IntEncoder])
 * @return an [Encoder] that writes a [String] to the output
 */
@JvmOverloads
fun ASCIIStringEncoder(sizeEncoder: Encoder<Int> = IntEncoder()): Encoder<String> =
    StringEncoder(Charsets.US_ASCII, sizeEncoder)

/**
 * Creates an [Encoder] that writes a [String] to the output.
 *
 * The string is encoded using the [ascii][Charsets.US_ASCII] charset, and is terminated by a predefined sequence of
 * bytes represented by the [endMarker] parameter.
 *
 * @param endMarker the sequence of bytes that will be written at the end of the string (defaults to [ASCII_NULL])
 * @return an [Encoder] that writes a [String] to the output
 * @throws IllegalArgumentException if the [endMarker] is empty
 */
@JvmOverloads
fun ASCIIStringEncoderEM(endMarker: ByteArray = ASCII_NULL): Encoder<String> =
    StringEncoder(Charsets.US_ASCII, endMarker)

/**
 * Creates an [Encoder] that writes a [String] to the output.
 *
 * The string is encoded using the [charset] parameter, and is prefixed with the number of bytes of the encoded string.
 * The size of the string is encoded using the [sizeEncoder] parameter.
 *
 * @param charset the charset to use for encoding the string
 * @param sizeEncoder the encoder to use for encoding the size of the string (defaults to the default [IntEncoder])
 * @return an [Encoder] that writes a [String] to the output
 */
@JvmOverloads
fun StringEncoder(charset: Charset, sizeEncoder: Encoder<Int> = IntEncoder()): Encoder<String> =
    Encoder { obj, output ->
        val bytes = obj.toByteArray(charset)
        sizeEncoder.encode(bytes.size, output)
        output.write(bytes)
    }

/**
 * Creates an [Encoder] that writes a [String] to the output.
 *
 * The string is encoded using the [charset] parameter, and is terminated by a predefined sequence of bytes represented
 * by the [endMarker] parameter.
 *
 * &nbsp;
 *
 * **NOTE**: The user is responsible for ensuring the [endMarker] has a length that is compatible with the [charset]
 * parameter. For example, if the [charset] is [UTF-16][Charsets.UTF_16], the [endMarker] must be at least 2 bytes long,
 * otherwise several characters may be wrongly interpreted as the end marker.
 *
 * &nbsp;
 *
 * @param charset the charset to use for encoding the string
 * @param endMarker the sequence of bytes that will be written at the end of the string
 * @return an [Encoder] that writes a [String] to the output
 * @throws IllegalArgumentException if the [endMarker] is empty
 */
fun StringEncoder(charset: Charset, endMarker: ByteArray): Encoder<String> {
    require(endMarker.isNotEmpty()) { "End marker must be at least 1 byte long" }
    val copy = endMarker.copyOf()
    return Encoder { obj, output ->
        val bytes = obj.toByteArray(charset)
        output.write(bytes)
        output.write(copy)
    }
}

//endregion

//region Basic Encoders

/**
 * Creates an [Encoder] that writes an [Enum] to the output.
 *
 * The enum is encoded using its [ordinal][Enum.ordinal] value, and is encoded using the [intEncoder] parameter which
 * encodes the [ordinal][Enum.ordinal] value.
 *
 * @param intEncoder the encoder used to encode the ordinal value (defaults to the default [IntEncoder])
 * @return an [Encoder] that writes an [Enum] to the output
 * @param T the type of the enum
 */
@JvmOverloads
fun <T : Enum<T>> EnumEncoder(intEncoder: Encoder<Int> = IntEncoder()): Encoder<T> =
    Encoder { obj, output -> intEncoder.encode(obj.ordinal, output) }

/**
 * Creates an [Encoder] that writes an [Enum] to the output.
 *
 * The enum is encoded using the [stringEncoder] parameter, which encodes the [name][Enum.name] of the enum constant.
 *
 * @param stringEncoder the encoder used to encode the name of the enum constant (defaults to the default
 * [UTF8StringEncoder])
 * @return an [Encoder] that writes an [Enum] to the output
 * @param T the type of the enum
 */
@JvmOverloads
fun <T : Enum<T>> EnumEncoderString(stringEncoder: Encoder<String> = UTF8StringEncoder()): Encoder<T> =
    Encoder { obj, output -> stringEncoder.encode(obj.name, output) }

//endregion

//region Special Encoders

/**
 * Creates an [Encoder] that doesn't write anything to the output.
 *
 * @return an [Encoder] that doesn't write anything to the output
 * @param T the type of the object to encode
 */
fun <T> NoOpEncoder(): Encoder<T> = Encoder { _, _ -> /* do nothing */ }

//endregion
