@file:JvmName("Decoders")

package com.kamelia.sprinkler.binary.decoder

import com.kamelia.sprinkler.binary.common.ByteEndianness
import com.kamelia.sprinkler.binary.decoder.core.ConstantSizeDecoder
import com.kamelia.sprinkler.binary.decoder.core.Decoder
import com.kamelia.sprinkler.binary.decoder.core.DecoderDataInput
import com.kamelia.sprinkler.binary.decoder.core.VariableSizeEndMarkerDecoder
import com.kamelia.sprinkler.binary.decoder.core.VariableSizePrefixedSizeDecoder
import com.kamelia.sprinkler.binary.decoder.util.*
import java.nio.charset.Charset
import java.util.*

//region Primitive Decoders

fun ByteDecoder(): Decoder<Byte> = byteDecoderInstance

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

fun BooleanDecoder(): Decoder<Boolean> = booleanDecoderInstance

//endregion

//region String Decoders

@JvmOverloads
fun UTF8StringDecoder(sizeDecoder: Decoder<Number> = IntDecoder()): Decoder<String> =
    StringDecoder(Charsets.UTF_8, sizeDecoder)

@JvmOverloads
fun UTF8StringDecoderEM(endMarker: ByteArray = UTF8_NULL): Decoder<String> {
    require(endMarker.isNotEmpty()) { "End marker must be at least 1 byte long for UTF-8 (got ${endMarker.size})" }
    return StringDecoderEM(Charsets.UTF_8, endMarker)
}

@JvmOverloads
fun UTF16StringDecoder(sizeDecoder: Decoder<Number> = IntDecoder()): Decoder<String> =
    StringDecoder(Charsets.UTF_16, sizeDecoder)

@JvmOverloads
fun UTF16StringDecoderEM(endMarker: ByteArray = UTF16_NULL): Decoder<String> {
    require(endMarker.size >= 2) { "End marker must be at least 2 bytes long for UTF-16 (got ${endMarker.size})" }
    return StringDecoderEM(Charsets.UTF_16, endMarker)
}

@JvmOverloads
fun ASCIIStringDecoder(sizeDecoder: Decoder<Number> = IntDecoder()): Decoder<String> =
    StringDecoder(Charsets.US_ASCII, sizeDecoder)

@JvmOverloads
fun ASCIIStringDecoderEM(endMarker: ByteArray = ASCII_NULL): Decoder<String> =
    StringDecoderEM(Charsets.US_ASCII, endMarker)


@JvmOverloads
fun StringDecoder(charset: Charset, sizeDecoder: Decoder<Number> = IntDecoder()): Decoder<String> =
    VariableSizePrefixedSizeDecoder(sizeDecoder) { readString(charset, it) }

fun StringDecoderEM(charset: Charset, endMarker: ByteArray): Decoder<String> {
    require(endMarker.isNotEmpty()) { "End marker must be at least 1 byte long (got ${endMarker.size})" }
    return VariableSizeEndMarkerDecoder(endMarker) { readString(charset, it) }
}

//endregion

//region Common Decoders

@JvmOverloads
fun <T : Enum<T>> EnumDecoder(enumClass: Class<T>, ordinalDecoder: Decoder<Int> = IntDecoder()): Decoder<T> =
    ordinalDecoder.mapResult { enumClass.enumConstants[it] }

@JvmOverloads
@JvmName("EnumDecoderString")
fun <T : Enum<T>> EnumDecoder(
    enumClass: Class<T>,
    stringDecoder: Decoder<String> = UTF8StringDecoder(),
): Decoder<T> = stringDecoder.mapResult { s -> enumClass.enumConstants.first { s == it.name } }

//endregion

//region Special Decoders

@JvmField
val NoOpDecoder = ConstantDecoder(Unit)

class NothingDecoder(
    private val error: Throwable = IllegalStateException("NothingDecoder always fails."),
) : Decoder<Nothing> {

    constructor(message: String) : this(IllegalStateException(message))

    override fun decode(input: DecoderDataInput): Decoder.State<Nothing> = Decoder.State.Error(error)

    override fun reset() = Unit

}

fun <T> NullDecoder(): Decoder<T?> = @Suppress("UNCHECKED_CAST") (NullDecoder as Decoder<T?>)

class ConstantDecoder<T>(private val factory: () -> T) : Decoder<T> {

    constructor(value: T) : this({ value })

    override fun decode(input: DecoderDataInput): Decoder.State<T> = Decoder.State.Done(factory())

    override fun reset() = Unit

}

//endregion

//region Constants

@JvmField
val ASCII_NULL = byteArrayOf(0)

@JvmField
val UTF8_NULL = ASCII_NULL

@JvmField
val UTF16_NULL = byteArrayOf(0, 0)

//endregion

//region Internal

private object NullDecoder : Decoder<Any?> {

    override fun decode(input: DecoderDataInput): Decoder.State<Any?> = Decoder.State.Done(null)

    override fun reset() {
        // no-op
    }

}

private val byteDecoderInstance = ConstantSizeDecoder(Byte.SIZE_BYTES) { readByte() }

private val booleanDecoderInstance = ConstantSizeDecoder(1) { readBoolean() }

//endregion
