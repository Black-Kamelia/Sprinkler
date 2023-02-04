@file:JvmName("Decoders")

package com.kamelia.sprinkler.binary.decoder

import com.kamelia.sprinkler.binary.common.ByteEndianness
import com.kamelia.sprinkler.binary.common.VariableSizeDelimitationKind
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
fun UTF8StringDecoder(kind: VariableSizeDelimitationKind = VariableSizeDelimitationKind.PREFIXED_SIZE): Decoder<String> =
    when (kind) {
        VariableSizeDelimitationKind.PREFIXED_SIZE -> StringDecoder()
        VariableSizeDelimitationKind.END_MARKER -> StringDecoderEM()
    }

@JvmOverloads
fun ASCIIStringDecoder(kind: VariableSizeDelimitationKind = VariableSizeDelimitationKind.PREFIXED_SIZE): Decoder<String> =
    when (kind) {
        VariableSizeDelimitationKind.PREFIXED_SIZE -> StringDecoder(charset = Charsets.US_ASCII)
        VariableSizeDelimitationKind.END_MARKER -> StringDecoderEM(charset = Charsets.US_ASCII)
    }

@JvmOverloads
fun StringDecoder(sizeDecoder: Decoder<Number> = IntDecoder(), charset: Charset = Charsets.UTF_8): Decoder<String> =
    VariableSizePrefixedSizeDecoder(sizeDecoder) { readString(charset, it) }

@JvmOverloads
fun StringDecoderEM(endMarker: ByteArray = byteArrayOf(0), charset: Charset = Charsets.UTF_8): Decoder<String> =
    VariableSizeEndMarkerDecoder(endMarker) { readString(charset, it) }

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

@JvmOverloads
fun UUIDDecoder(longDecoder: Decoder<Long> = LongDecoder()): Decoder<UUID> {
    var msb = 0L
    return longDecoder
        .mapTo { msb = it; longDecoder }
        .mapResult { UUID(msb, it) }
}

@JvmOverloads
fun UUIDDecoderString(stringDecoder: Decoder<String> = UTF8StringDecoder()): Decoder<UUID> =
    stringDecoder.mapResult { UUID.fromString(it) }

// TODO date/time decoders

//endregion

//region Special Decoders

object NoOpDecoder : Decoder<Unit> {

    override fun decode(input: DecoderDataInput): Decoder.State<Unit> = Decoder.State.Done(Unit)

    override fun reset() = Unit

}

class NothingDecoder(
    private val error: Throwable = IllegalStateException("NothingDecoder always fails."),
) : Decoder<Nothing> {

    constructor(message: String) : this(IllegalStateException(message))

    override fun decode(input: DecoderDataInput): Decoder.State<Nothing> = Decoder.State.Error(error)

    override fun reset() = Unit

}

fun <T> NullDecoder(): Decoder<T?> = @Suppress("UNCHECKED_CAST") (NullDecoder as Decoder<T?>)

fun <T, U> PairDecoder(firstDecoder: Decoder<T>, secondDecoder: Decoder<U>): Decoder<Pair<T, U>> {
    var f: T? = null
    return firstDecoder
        .mapTo { f = it; secondDecoder }
        .mapResult { (@Suppress("UNCHECKED_CAST") (f as T)) to it }
}

//endregion

//region Internal

private object NullDecoder : Decoder<Any?> {

    override fun decode(input: DecoderDataInput): Decoder.State<Any?> = Decoder.State.Done(null)

    override fun reset() {
        // no-op
    }

}

//endregion
