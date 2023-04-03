@file:JvmName("Decoders")
@file:JvmMultifileClass

package com.kamelia.sprinkler.codec.binary.decoder

import com.kamelia.sprinkler.codec.binary.decoder.core.ConstantSizedItemDecoder
import com.kamelia.sprinkler.codec.binary.decoder.core.Decoder
import com.kamelia.sprinkler.codec.binary.decoder.core.MarkerEndedItemDecoder
import com.kamelia.sprinkler.codec.binary.decoder.core.PrefixedSizeItemDecoder
import com.kamelia.sprinkler.codec.binary.util.ASCII_NULL
import com.kamelia.sprinkler.codec.binary.util.UTF16_NULL
import com.kamelia.sprinkler.codec.binary.util.UTF8_NULL
import com.kamelia.sprinkler.util.readBoolean
import com.kamelia.sprinkler.util.readByte
import com.kamelia.sprinkler.util.readDouble
import com.kamelia.sprinkler.util.readFloat
import com.kamelia.sprinkler.util.readInt
import com.kamelia.sprinkler.util.readLong
import com.kamelia.sprinkler.util.readShort
import com.kamelia.sprinkler.util.readString
import java.nio.ByteOrder
import java.nio.charset.Charset

//region Primitive Decoders

/**
 * Creates a [Decoder] that reads a single [Byte] from the input.
 *
 * @return a [Decoder] that reads a single [Byte] from the input
 */
fun ByteDecoder(): Decoder<Byte> = ConstantSizedItemDecoder(Byte.SIZE_BYTES) { readByte() }

/**
 * Creates a [Decoder] that reads a [Short] from the input. The endianness of the short is specified by the [endianness]
 * parameter.
 *
 * @param endianness the endianness of the short (default is [ByteOrder.BIG_ENDIAN])
 * @return a [Decoder] that reads [Short] from the input
 */
@JvmOverloads
fun ShortDecoder(endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Decoder<Short> =
    ConstantSizedItemDecoder(Short.SIZE_BYTES) { readShort(endianness) }

/**
 * Creates a [Decoder] that reads an [Int] from the input. The endianness of the int is specified by the [endianness]
 * parameter.
 *
 * @param endianness the endianness of the int (default is [ByteOrder.BIG_ENDIAN])
 * @return a [Decoder] that reads [Int] from the input
 */
@JvmOverloads
fun IntDecoder(endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Decoder<Int> =
    ConstantSizedItemDecoder(Int.SIZE_BYTES) { readInt(endianness) }

/**
 * Creates a [Decoder] that reads a [Long] from the input. The endianness of the long is specified by the [endianness]
 * parameter.
 *
 * @param endianness the endianness of the long (default is [ByteOrder.BIG_ENDIAN])
 * @return a [Decoder] that reads [Long] from the input
 */
@JvmOverloads
fun LongDecoder(endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Decoder<Long> =
    ConstantSizedItemDecoder(Long.SIZE_BYTES) { readLong(endianness) }

/**
 * Creates a [Decoder] that reads a [Float] from the input. The endianness of the float is specified by the [endianness]
 * parameter.
 *
 * @param endianness the endianness of the float (default is [ByteOrder.BIG_ENDIAN])
 * @return a [Decoder] that reads [Float] from the input
 */
@JvmOverloads
fun FloatDecoder(endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Decoder<Float> =
    ConstantSizedItemDecoder(Float.SIZE_BYTES) { readFloat(endianness) }

/**
 * Creates a [Decoder] that reads a [Double] from the input. The endianness of the double is specified by the
 * [endianness] parameter.
 *
 * @param endianness the endianness of the double (default is [ByteOrder.BIG_ENDIAN])
 * @return a [Decoder] that reads [Double] from the input
 */
@JvmOverloads
fun DoubleDecoder(endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Decoder<Double> =
    ConstantSizedItemDecoder(Double.SIZE_BYTES) { readDouble(endianness) }

/**
 * Creates a [Decoder] that reads a [Boolean] from the input.
 *
 * The boolean is encoded as a single byte, where 0 represents `false` and any other value represents `true`.
 *
 * @return a [Decoder] that reads a [Boolean] from the input
 */
fun BooleanDecoder(): Decoder<Boolean> = ConstantSizedItemDecoder(1) { readBoolean() }

//endregion

//region String Decoders

/**
 * Creates a [Decoder] that reads a [String] from the input.
 *
 * The string is encoded using the [utf8][Charsets.UTF_8] charset, and is prefixed with the number of bytes in the
 * string. The size of the string is decoded using the [sizeDecoder] parameter.
 *
 * @param sizeDecoder the [Decoder] used to decode the size of the string (default is the default [IntDecoder])
 * @return a [Decoder] that reads a [String] from the input
 */
@JvmOverloads
fun UTF8StringDecoder(sizeDecoder: Decoder<Number> = IntDecoder()): Decoder<String> =
    StringDecoder(Charsets.UTF_8, sizeDecoder)

/**
 * Creates a [Decoder] that reads a [String] from the input.
 *
 * The string is encoded using the [utf8][Charsets.UTF_8] charset, and is terminated by a predefined sequence of bytes
 * represented by the [endMarker] parameter.
 *
 * @param endMarker the sequence of bytes that marks the end of the string (default is [UTF8_NULL])
 * @return a [Decoder] that reads a [String] from the input
 * @throws IllegalArgumentException if the [endMarker] is empty
 */
@JvmOverloads
fun UTF8StringDecoderEM(endMarker: ByteArray = UTF8_NULL): Decoder<String> {
    require(endMarker.isNotEmpty()) { "End marker must be at least 1 byte long for UTF-8 (got ${endMarker.size})" }
    return StringDecoder(Charsets.UTF_8, endMarker)
}

/**
 * Creates a [Decoder] that reads a [String] from the input.
 *
 * The string is encoded using the [utf16][Charsets.UTF_16] charset, and is prefixed with the number of bytes in the
 * string. The size of the string is decoded using the [sizeDecoder] parameter.
 *
 * @param sizeDecoder the [Decoder] used to decode the size of the string (default is the default [IntDecoder])
 * @return a [Decoder] that reads a [String] from the input
 */
@JvmOverloads
fun UTF16StringDecoder(sizeDecoder: Decoder<Number> = IntDecoder()): Decoder<String> =
    StringDecoder(Charsets.UTF_16, sizeDecoder)

/**
 * Creates a [Decoder] that reads a [String] from the input.
 *
 * The string is encoded using the [utf16][Charsets.UTF_16] charset, and is terminated by a predefined sequence of bytes
 * represented by the [endMarker] parameter.
 *
 * @param endMarker the sequence of bytes that marks the end of the string (default is [UTF16_NULL])
 * @return a [Decoder] that reads a [String] from the input
 * @throws IllegalArgumentException if the [endMarker] is less than 2 bytes long
 */
@JvmOverloads
fun UTF16StringDecoderEM(endMarker: ByteArray = UTF16_NULL): Decoder<String> {
    require(endMarker.size >= 2) { "End marker must be at least 2 bytes long for UTF-16 (got ${endMarker.size})" }
    return StringDecoder(Charsets.UTF_16, endMarker)
}

/**
 * Creates a [Decoder] that reads a [String] from the input.
 *
 * The string is encoded using the [ascii][Charsets.US_ASCII] charset, and is prefixed with the number of bytes in the
 * string. The size of the string is decoded using the [sizeDecoder] parameter.
 *
 * @param sizeDecoder the [Decoder] used to decode the size of the string (default is the default [IntDecoder])
 * @return a [Decoder] that reads a [String] from the input
 */
@JvmOverloads
fun ASCIIStringDecoder(sizeDecoder: Decoder<Number> = IntDecoder()): Decoder<String> =
    StringDecoder(Charsets.US_ASCII, sizeDecoder)

/**
 * Creates a [Decoder] that reads a [String] from the input.
 *
 * The string is encoded using the [ascii][Charsets.US_ASCII] charset, and is terminated by a predefined sequence of bytes
 * represented by the [endMarker] parameter.
 *
 * @param endMarker the sequence of bytes that marks the end of the string (default is [ASCII_NULL])
 * @return a [Decoder] that reads a [String] from the input
 * @throws IllegalArgumentException if the [endMarker] is empty
 */
@JvmOverloads
fun ASCIIStringDecoderEM(endMarker: ByteArray = ASCII_NULL): Decoder<String> =
    StringDecoder(Charsets.US_ASCII, endMarker)

/**
 * Creates a [Decoder] that reads a [String] from the input.
 *
 * The string is encoded using the [charset] parameter, and is prefixed with the number of bytes in the string. The size
 * of the string is decoded using the [sizeDecoder] parameter.
 *
 * @param charset the [Charset] used to encode the string
 * @param sizeDecoder the [Decoder] used to decode the size of the string (default is the default [IntDecoder])
 * @return a [Decoder] that reads a [String] from the input
 */
@JvmOverloads
fun StringDecoder(charset: Charset, sizeDecoder: Decoder<Number> = IntDecoder()): Decoder<String> =
    PrefixedSizeItemDecoder(sizeDecoder) { readString(charset, it) }

/**
 * Creates a [Decoder] that reads a [String] from the input.
 *
 * The string is encoded using the [charset] parameter, and is terminated by a predefined sequence of bytes represented
 * by the [endMarker] parameter.
 *
 * @param charset the [Charset] used to encode the string
 * @param endMarker the sequence of bytes that marks the end of the string
 * @return a [Decoder] that reads a [String] from the input
 * @throws IllegalArgumentException if the [endMarker] is empty
 */
fun StringDecoder(charset: Charset, endMarker: ByteArray): Decoder<String> {
    require(endMarker.isNotEmpty()) { "End marker must be at least 1 byte long (got ${endMarker.size})" }
    return MarkerEndedItemDecoder(endMarker) { readString(charset, it) }
}

//endregion

//region Basic Decoders

/**
 * Creates a [Decoder] that reads an [Enum] from the input.
 *
 * The enum is decoded using the [ordinalDecoder] parameter, which is used to decode the ordinal of the enum constant.
 *
 * **NOTE**:
 * The created decoder will return an [error][Decoder.State.Error] if the decoded ordinal is not a valid ordinal for the
 * given [enumClass].
 *
 * @param enumClass the [Class] of the enum
 * @param ordinalDecoder the [Decoder] used to decode the ordinal of the enum constant (default is the default
 * [IntDecoder])
 * @return a [Decoder] that reads a [Boolean] from the input
 */
@JvmOverloads
fun <T : Enum<T>> EnumDecoder(enumClass: Class<T>, ordinalDecoder: Decoder<Int> = IntDecoder()): Decoder<T> {
    val constants = enumClass.enumConstants
    return ordinalDecoder.mapState {
        if (it !in constants.indices) {
            Decoder.State.Error("(EnumDecoder) Invalid ordinal for enum $enumClass: $it")
        } else {
            Decoder.State.Done(constants[it])
        }
    }
}

/**
 * Creates a [Decoder] that reads an [Enum] from the input.
 *
 * The enum is decoded using the [stringDecoder] parameter, which is used to decode the name of the enum constant.
 *
 * **NOTE**:
 * The created decoder will return an [error][Decoder.State.Error] if the decoded string does not match any of the
 * enum constants.
 *
 * @param enumClass the [Class] of the enum
 * @param stringDecoder the [Decoder] used to decode the name of the enum constant (default is the default
 * [UTF8StringDecoder])
 * @return a [Decoder] that reads a [Boolean] from the input
 */
@JvmOverloads
fun <T : Enum<T>> EnumDecoderString(
    enumClass: Class<T>,
    stringDecoder: Decoder<String> = UTF8StringDecoder(),
): Decoder<T> {
    val constants = enumClass.enumConstants.associateBy { it.name }
    return stringDecoder.mapState {
        val str = constants[it]
            ?: return@mapState Decoder.State.Error("(EnumDecoderString) Unknown enum constant for enum $enumClass: $it")
        Decoder.State.Done(str)
    }
}

//endregion

//region Special Decoders

/**
 * Creates a [Decoder] that reads a constant value from the input.
 *
 * The decoder always returns the [element] parameter and actually reads nothing from the input.
 *
 * @param element the constant value to return
 * @return a [Decoder] that reads a constant value from the input
 */
fun <T> ConstantDecoder(element: T): Decoder<T> = ConstantSizedItemDecoder(0) { element }

/**
 * Creates a [Decoder] that reads a constant value from the input.
 *
 * The decoder always returns the value returned by the [factory] parameter and actually reads nothing from the input.
 *
 * @param factory the factory that creates the constant value to return
 * @return a [Decoder] that reads a constant value from the input
 */
fun <T> ConstantDecoder(factory: () -> T): Decoder<T> = ConstantSizedItemDecoder(0) { factory() }

/**
 * Creates a [Decoder] that reads [Unit] from the input.
 *
 * The decoder always returns [Unit] and actually reads nothing from the input.
 *
 * @return a [Decoder] that reads [Unit] from the input
 */
fun NoOpDecoder(): Decoder<Unit> = UNIT_DECODER

/**
 * Creates a [Decoder] that reads `null` from the input.
 *
 * The decoder always returns `null` and actually reads nothing from the input.
 *
 * @return a [Decoder] that reads a nullable value from the input
 */
fun <T : Any> NullDecoder(): Decoder<T?> = NULL_DECODER

//endregion

//region Internal

private val UNIT_DECODER = ConstantDecoder(Unit)

private val NULL_DECODER = ConstantDecoder(null)

//endregion
