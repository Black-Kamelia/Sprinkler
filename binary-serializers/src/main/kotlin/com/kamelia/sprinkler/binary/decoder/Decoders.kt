@file:JvmName("ConstantSizedDecoders")

package com.kamelia.sprinkler.binary.decoder

import java.nio.charset.Charset

//region Primitive Decoders

fun ByteDecoder(): Decoder<Byte> = ConstantSizeDecoder(
    Byte.SIZE_BYTES,
    ByteEndianness.BIG_ENDIAN
) { readByte() }

@JvmOverloads
fun ShortDecoder(endianness: ByteEndianness = ByteEndianness.BIG_ENDIAN): Decoder<Short> = ConstantSizeDecoder(
    Short.SIZE_BYTES,
    endianness,
) { readShort(endianness) }

@JvmOverloads
fun IntDecoder(endianness: ByteEndianness = ByteEndianness.BIG_ENDIAN): Decoder<Int> = ConstantSizeDecoder(
    Int.SIZE_BYTES,
    endianness,
    ByteArray::readInt
)

@JvmOverloads
fun LongDecoder(endianness: ByteEndianness = ByteEndianness.BIG_ENDIAN): Decoder<Long> = ConstantSizeDecoder(
    Long.SIZE_BYTES,
    endianness,
    ByteArray::readLong
)

@JvmOverloads
fun FloatDecoder(endianness: ByteEndianness = ByteEndianness.BIG_ENDIAN): Decoder<Float> = ConstantSizeDecoder(
    Float.SIZE_BYTES,
    endianness,
    ByteArray::readFloat
)

@JvmOverloads
fun DoubleDecoder(endianness: ByteEndianness = ByteEndianness.BIG_ENDIAN): Decoder<Double> = ConstantSizeDecoder(
    Double.SIZE_BYTES,
    endianness,
    ByteArray::readDouble
)

fun BooleanDecoder(): Decoder<Boolean> = ConstantSizeDecoder(1) { readBoolean() }

//endregion

//region String Decoders

@JvmOverloads
fun UTF8StringDecoder(kind: VariableSizeDecoderKind = VariableSizeDecoderKind.PREFIXED_SIZE): Decoder<String> =
    when (kind) {
        VariableSizeDecoderKind.PREFIXED_SIZE -> StringDecoder()
        VariableSizeDecoderKind.END_MARKER -> StringDecoderEM()
    }

@JvmOverloads
fun ASCIIStringDecoder(kind: VariableSizeDecoderKind = VariableSizeDecoderKind.PREFIXED_SIZE): Decoder<String> =
    when (kind) {
        VariableSizeDecoderKind.PREFIXED_SIZE -> StringDecoder(Charsets.US_ASCII)
        VariableSizeDecoderKind.END_MARKER -> StringDecoderEM(Charsets.US_ASCII)
    }

@JvmOverloads
fun StringDecoder(
    charset: Charset = Charsets.UTF_8,
    sizeDecoder: Decoder<Number> = IntDecoder(),
): Decoder<String> = VariableSizePrefixedSizeDecoder(sizeDecoder) { _, length -> readString(charset, length) }

@JvmOverloads
fun StringDecoderEM(
    charset: Charset = Charsets.UTF_8,
    endMarker: Byte = 0b0,
): Decoder<String> = VariableSizeEndMarkerDecoder(endMarker) { _, length -> readString(charset, length) }

//endregion

//region Special Decoders

object NoOpDecoder : Decoder<Unit> {

    override fun decode(input: DecoderDataInput): Decoder.State<Unit> = Decoder.State.Done(Unit)

    override fun reset() {
        // no-op
    }

}

//endregion
