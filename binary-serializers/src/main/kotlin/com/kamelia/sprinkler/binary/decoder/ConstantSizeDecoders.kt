@file:JvmName("ConstantSizedDecoders")
package com.kamelia.sprinkler.binary.decoder

import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder


fun IntDecoder(): Decoder<Int> = ConstantSizeDecoder(
    Int.SIZE_BYTES,
    extractor = ByteBuffer::getInt
)


fun IntLittleEndianDecoder(): Decoder<Int> = ConstantSizeDecoder(
    Int.SIZE_BYTES,
    ByteOrder.LITTLE_ENDIAN,
    ByteBuffer::getInt,
)


fun ByteDecoder(): Decoder<Byte> = ConstantSizeDecoder(
    Byte.SIZE_BYTES,
    extractor = ByteBuffer::get
)


fun LongDecoder(): Decoder<Long> = ConstantSizeDecoder(
    Long.SIZE_BYTES,
    extractor = ByteBuffer::getLong
)


fun LongLittleEndianDecoder(): Decoder<Long> = ConstantSizeDecoder(
    Long.SIZE_BYTES,
    ByteOrder.LITTLE_ENDIAN,
    ByteBuffer::getLong
)


fun ShortDecoder(): Decoder<Short> = ConstantSizeDecoder(
    Short.SIZE_BYTES,
    extractor = ByteBuffer::getShort
)


fun ShortLittleEndianDecoder(): Decoder<Short> = ConstantSizeDecoder(
    Short.SIZE_BYTES,
    ByteOrder.LITTLE_ENDIAN,
    ByteBuffer::getShort
)


fun FloatDecoder(): Decoder<Float> = ConstantSizeDecoder(
    Float.SIZE_BYTES,
    extractor = ByteBuffer::getFloat
)


fun FloatLittleEndianDecoder(): Decoder<Float> = ConstantSizeDecoder(
    Float.SIZE_BYTES,
    ByteOrder.LITTLE_ENDIAN,
    ByteBuffer::getFloat
)


fun DoubleDecoder(): Decoder<Double> = ConstantSizeDecoder(
    Double.SIZE_BYTES,
    extractor = ByteBuffer::getDouble
)


fun DoubleLittleEndianDecoder(): Decoder<Double> = ConstantSizeDecoder(
    Double.SIZE_BYTES,
    ByteOrder.LITTLE_ENDIAN,
    ByteBuffer::getDouble
)


fun BooleanDecoder(): Decoder<Boolean> = ConstantSizeDecoder(1) { int != 0 }


private class ConstantSizeDecoder<E>(
    private val byteSize: Int,
    private val endianness: ByteOrder = ByteOrder.BIG_ENDIAN,
    private val extractor: ByteBuffer.() -> E
) : Decoder<E> {

    init {
        require(byteSize > 0) { "Number of bytes must be greater than 0 got $byteSize" }
    }

    private val buffer = ByteBuffer.allocate(byteSize)

    override fun decode(stream: InputStream): E {
        buffer.clear()
        stream.read(buffer.array())
        checkDecoding(buffer.remaining() > 0) {
            "Not enough bytes to read, expected: $byteSize but got ${buffer.position()}"
        }

        val readOnly = buffer.asReadOnlyBuffer().order(endianness)
        return extractor(readOnly)
    }

}
