@file:JvmName("Decoders")

package com.kamelia.sprinkler.binary.decoder

import com.kamelia.sprinkler.binary.common.ByteEndianness
import com.kamelia.sprinkler.binary.decoder.core.ConstantSizeDecoder
import com.kamelia.sprinkler.binary.decoder.core.Decoder
import com.kamelia.sprinkler.binary.decoder.core.VariableSizeEndMarkerDecoder
import com.kamelia.sprinkler.binary.decoder.core.VariableSizePrefixedSizeDecoder
import com.kamelia.sprinkler.binary.decoder.util.*
import java.nio.charset.Charset
import java.util.*

//region Primitive Decoders

fun ByteDecoder(): Decoder<Byte> = ConstantSizeDecoder(Byte.SIZE_BYTES) { readByte() }

@JvmOverloads
fun ShortDecoder(endianness: ByteEndianness = ByteEndianness.BIG_ENDIAN): Decoder<Short> =
    ConstantSizeDecoder(Short.SIZE_BYTES) { readShort(endianness) }

@JvmOverloads
fun IntDecoder(endianness: ByteEndianness = ByteEndianness.BIG_ENDIAN): Decoder<Int> =
    ConstantSizeDecoder(Int.SIZE_BYTES) { readInt(endianness) }

@JvmOverloads
fun LongDecoder(endianness: ByteEndianness = ByteEndianness.BIG_ENDIAN): Decoder<Long> =
    ConstantSizeDecoder(Long.SIZE_BYTES) { readLong(endianness) }

@JvmOverloads
fun FloatDecoder(endianness: ByteEndianness = ByteEndianness.BIG_ENDIAN): Decoder<Float> =
    ConstantSizeDecoder(Float.SIZE_BYTES) { readFloat(endianness) }

@JvmOverloads
fun DoubleDecoder(endianness: ByteEndianness = ByteEndianness.BIG_ENDIAN): Decoder<Double> =
    ConstantSizeDecoder(Double.SIZE_BYTES) { readDouble(endianness) }

fun BooleanDecoder(): Decoder<Boolean> = ConstantSizeDecoder(1) { readBoolean() }

//endregion

//region String Decoders

@JvmOverloads
fun UTF8StringDecoder(sizeDecoder: Decoder<Number> = IntDecoder()): Decoder<String> =
    StringDecoder(Charsets.UTF_8, sizeDecoder)

@JvmOverloads
fun UTF8StringDecoderEM(endMarker: ByteArray = UTF8_NULL): Decoder<String> {
    require(endMarker.isNotEmpty()) { "End marker must be at least 1 byte long for UTF-8 (got ${endMarker.size})" }
    return StringDecoder(Charsets.UTF_8, endMarker)
}

@JvmOverloads
fun UTF16StringDecoder(sizeDecoder: Decoder<Number> = IntDecoder()): Decoder<String> =
    StringDecoder(Charsets.UTF_16, sizeDecoder)

@JvmOverloads
fun UTF16StringDecoderEM(endMarker: ByteArray = UTF16_NULL): Decoder<String> {
    require(endMarker.size >= 2) { "End marker must be at least 2 bytes long for UTF-16 (got ${endMarker.size})" }
    return StringDecoder(Charsets.UTF_16, endMarker)
}

@JvmOverloads
fun ASCIIStringDecoder(sizeDecoder: Decoder<Number> = IntDecoder()): Decoder<String> =
    StringDecoder(Charsets.US_ASCII, sizeDecoder)

@JvmOverloads
fun ASCIIStringDecoderEM(endMarker: ByteArray = ASCII_NULL): Decoder<String> =
    StringDecoder(Charsets.US_ASCII, endMarker)

@JvmOverloads
fun StringDecoder(charset: Charset, sizeDecoder: Decoder<Number> = IntDecoder()): Decoder<String> =
    VariableSizePrefixedSizeDecoder(sizeDecoder) { readString(charset, it) }

fun StringDecoder(charset: Charset, endMarker: ByteArray): Decoder<String> {
    require(endMarker.isNotEmpty()) { "End marker must be at least 1 byte long (got ${endMarker.size})" }
    return VariableSizeEndMarkerDecoder(endMarker) { readString(charset, it) }
}

//endregion

//region Basic Decoders

@JvmOverloads
fun <T : Enum<T>> EnumDecoder(enumClass: Class<T>, ordinalDecoder: Decoder<Int> = IntDecoder()): Decoder<T> =
    ordinalDecoder.mapResult { enumClass.enumConstants[it] }

@JvmOverloads
@JvmName("EnumDecoderString")
fun <T : Enum<T>> EnumDecoder(
    enumClass: Class<T>,
    stringDecoder: Decoder<String> = UTF8StringDecoderEM(),
): Decoder<T> = stringDecoder.mapResult { s -> enumClass.enumConstants.first { s == it.name } }

//endregion

//region Special Decoders

fun <T> ConstantDecoder(element: T): Decoder<T> = ConstantSizeDecoder(0) { element }

fun <T> ConstantDecoder(factory: () -> T): Decoder<T> = ConstantSizeDecoder(0) { factory() }

fun NoOpDecoder(): Decoder<Unit> = ConstantDecoder(Unit)

fun <T> NullDecoder(): Decoder<T?> = ConstantDecoder(null)

//endregion

//region Constants

@JvmField
val ASCII_NULL = byteArrayOf(0)

@JvmField
val UTF8_NULL = ASCII_NULL

@JvmField
val UTF16_NULL = byteArrayOf(0, 0)

//endregion
