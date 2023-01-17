@file:JvmName("Decoders")

package com.kamelia.sprinkler.binary.decoder

import com.kamelia.sprinkler.binary.common.ByteEndianness
import com.kamelia.sprinkler.binary.common.VariableSizeDelimitationKind
import com.kamelia.sprinkler.binary.decoder.composer.finally
import com.kamelia.sprinkler.binary.decoder.composer.then
import java.nio.charset.Charset

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

//region Special Decoders

object NoOpDecoder : Decoder<Nothing> {

    override fun decode(input: DecoderDataInput): Decoder.State<Nothing> = Decoder.State.Done {
        throw IllegalStateException("NoOpDecoder value is not available.")
    }

    override fun reset() {
        // no-op
    }

}

fun <T, U> PairDecoder(firstDecoder: Decoder<T>, secondDecoder: Decoder<U>): Decoder<Pair<T, U>> = firstDecoder
    .compose()
    .then(secondDecoder)
    .finally(::Pair)
    .assemble()

infix fun <T, U> Decoder<T>.and(other: Decoder<U>): Decoder<Pair<T, U>> = PairDecoder(this, other)

//endregion
