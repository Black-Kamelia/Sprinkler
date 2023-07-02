package com.kamelia.sprinkler.transcoder.binary.encoder.core

import com.kamelia.sprinkler.util.bit
import java.io.OutputStream
import java.util.*
import kotlin.experimental.or
import kotlin.math.min

/**
 * Abstraction allowing [Encoders][Encoder] to write bytes and bits. This interface provides methods for writing bytes
 * and bits in various ways, including writing a single byte/bit, writing a [ByteArray], writing an [Iterable] of bytes,
 * etc.
 *
 * This interface provides a factory [EncoderOutput.from] to create an [EncoderOutput] by only providing a function that
 * writes one byte, allowing to easily create an [EncoderOutput] from any kind of output, as shown in the example below:
 *
 * &nbsp;
 *
 * ```
 * val myOutput = EncoderOutput.from { byte -> println(byte) } // myOutput.write(byte) will print the byte
 * ```
 *
 * @see Encoder
 */
interface EncoderOutput {

    /**
     * Writes a single bit to the output. Only the least significant bit of the given [Int] is written, and all other
     * bits are ignored.
     *
     * @param bit the bit to write
     */
    fun writeBit(bit: Int)

    /**
     * Flushes the output, to force any buffered bytes to be written. This method is useful when the writing of byte is
     * finished but the last byte is not full and therefore has not been written yet.
     *
     * All the padding bits appended to the last byte are set to `0`.
     */
    fun flush()

    /**
     * Writes a single bit to the output. Only the least significant bit of the given [Byte] is written, and all other
     * bits are ignored.
     *
     * @param bit the bit to write
     */
    fun writeBit(bit: Byte) = writeBit(bit.toInt())

    /**
     * Writes a single bit to the output. If the given [Boolean] is `true`, then a `1` is written. Otherwise, a `0` is
     * written.
     *
     * @param bit the bit to write
     */
    fun writeBit(bit: Boolean) = writeBit(if (bit) 1 else 0)

    /**
     * Write up to 8 bits from the given [Int] to the output. The [start] and [length] parameters specify the range of
     * bits in the [Int] to write. The [start] parameter is inclusive, and the [length] parameter is exclusive.
     *
     * @param byte the [Int] to write
     * @param start the inclusive start index in the [Int] to write
     * @param length the exclusive end index in the [Int] to write
     * @throws IndexOutOfBoundsException if [start] < 0 or [length] < 0 or [start] + [length] > 8
     */
    fun writeBits(byte: Int, start: Int, length: Int) {
        Objects.checkFromIndexSize(start, length, 8)
        repeat(length) {
            writeBit(byte.bit(7 - start - it))
        }
    }

    /**
     * Write up to 8 bits from the given [Int] to the output. The [length] parameter specifies the number of bits to
     * write, starting from the most significant bit. The [length] parameter must be between 0 and 8, inclusive.
     *
     * @param byte the [Int] to write
     * @param length the number of bits to write
     * @throws IndexOutOfBoundsException if [length] < 0 or [length] > 8
     */
    fun writeBits(byte: Int, length: Int) = writeBits(byte, 0, length)

    /**
     * Write up to 8 bits from the given [Byte] to the output. The [start] and [length] parameters specify the range of
     * bits in the [Byte] to write. The [start] parameter is inclusive, and the [length] parameter is exclusive.
     *
     * @param byte the [Byte] to write
     * @param start the inclusive start index in the [Byte] to write
     * @param length the exclusive end index in the [Byte] to write
     * @throws IndexOutOfBoundsException if [start] < 0 or [length] < 0 or [start] + [length] > 8
     */
    fun writeBits(byte: Byte, start: Int, length: Int) = writeBits(byte.toInt(), start, length)

    /**
     * Write up to 8 bits from the given [Byte] to the output. The [length] parameter specifies the number of bits to
     * write, starting from the most significant bit. The [length] parameter must be between 0 and 8, inclusive.
     *
     * @param byte the [Byte] to write
     * @param length the number of bits to write
     * @throws IndexOutOfBoundsException if [length] < 0 or [length] > 8
     */
    fun writeBits(byte: Byte, length: Int) = writeBits(byte.toInt(), 0, length)

    /**
     * Write several bits from the given [ByteArray] to the output. The [start] and [length] parameters specify the
     * range of bits in the [ByteArray] to write. The [start] parameter is inclusive, and the [length] parameter is
     * exclusive.
     *
     * @param bytes the [ByteArray] to write
     * @param start the inclusive start index in the [ByteArray] to write
     * @param length the exclusive end index in the [ByteArray] to write
     * @throws IndexOutOfBoundsException if [start] < 0 or [length] < 0 or [start] + [length] > [bytes].size * 8
     */
    fun writeBits(bytes: ByteArray, start: Int, length: Int) {
        Objects.checkFromIndexSize(start, length, bytes.size * 8)
        val actualStart = start / 8

        // write the partial byte at the start
        val prefixOffset = start and 7
        val hasPrefix = prefixOffset > 0
        val writtenFromPrefix = if (hasPrefix) min(8 - prefixOffset, length) else 0
        writeBits(bytes[actualStart], prefixOffset, writtenFromPrefix)

        // write the full bytes
        val fullBytes = (length - writtenFromPrefix) / 8
        val fullBytesStart = if (hasPrefix) actualStart + 1 else actualStart
        write(bytes, fullBytesStart, fullBytes)

        // write the partial byte at the end
        val suffixOffset = (length - writtenFromPrefix - fullBytes * 8) and 7
        val lastIndex = (start + length) / 8
        writeBits(bytes[lastIndex], 0, suffixOffset)
    }

    /**
     * Write several bits from the given [ByteArray] to the output. The [length] parameter specifies the number of bits
     * to write, starting from the most significant bit. The [length] parameter must be between 0 and the number of bits
     * in the [ByteArray], inclusive.
     *
     * @param bytes the [ByteArray] to write
     * @param length the number of bits to write
     * @throws IndexOutOfBoundsException if [length] < 0 or [length] > [bytes].size * 8
     */
    fun writeBits(bytes: ByteArray, length: Int) = writeBits(bytes, 0, length)

    /**
     * Writes a single byte to the output.
     *
     * @param byte the byte to write
     */
    fun write(byte: Int): Unit = writeBits(byte, 0, 8)

    /**
     * Writes a single byte to the output.
     *
     * @param byte the byte to write
     */
    fun write(byte: Byte): Unit = write(byte.toInt())

    /**
     * Writes bytes from the given [ByteArray] to the output. The [start] and [length] parameters specify the range of
     * indices in the [ByteArray] to write. The [start] parameter is inclusive, and the [length] parameter is exclusive.
     *
     * @param bytes the [ByteArray] to write
     * @param start the inclusive start index in the [ByteArray] to write
     * @param length the exclusive end index in the [ByteArray] to write
     * @throws IndexOutOfBoundsException if [start] < 0 or [length] < 0 or [start] + [length] > [ByteArray.size]
     */
    fun write(bytes: ByteArray, start: Int, length: Int) {
        Objects.checkFromIndexSize(start, length, bytes.size)
        for (index in start until start + length) {
            write(bytes[index])
        }
    }

    /**
     * Writes bytes from the given [ByteArray] to the output. The [start] parameter specifies the inclusive start index
     * in the [ByteArray] to write. The method will write all bytes from the [start] index to the end of the
     * [ByteArray].
     *
     * @param bytes the [ByteArray] to write
     * @param start the inclusive start index in the [ByteArray] to write
     */
    fun write(bytes: ByteArray, start: Int): Unit = write(bytes, start, bytes.size - start)

    /**
     * Writes bytes from the given [ByteArray] to the output. The method will write all bytes from the [ByteArray].
     *
     * @param bytes the [ByteArray] to write
     */
    fun write(bytes: ByteArray): Unit = write(bytes, 0, bytes.size)

    /**
     * Writes bytes from the given [Iterable] of bytes to the output.
     *
     * @param bytes the [Iterable] of bytes to write
     */
    fun write(bytes: Iterable<Byte>): Unit = bytes.forEach(::write)

    companion object {

        /**
         * Creates an [EncoderOutput] that does nothing.
         *
         * @return the [EncoderOutput] that does nothing
         */
        @JvmStatic
        fun nullOutput(): EncoderOutput = object : EncoderOutput {
            override fun writeBit(bit: Int) = Unit
            override fun write(byte: Int) = Unit
            override fun writeBits(byte: Int, start: Int, length: Int) {
                Objects.checkFromIndexSize(start, length, 8)
            }

            override fun writeBits(bytes: ByteArray, start: Int, length: Int) {
                Objects.checkFromIndexSize(start, length, bytes.size * 8)
            }

            override fun write(bytes: ByteArray, start: Int, length: Int) {
                Objects.checkFromIndexSize(start, length, bytes.size)
            }

            override fun flush() = Unit
        }

        /**
         * Creates an [EncoderOutput] that writes to the given [OutputStream].
         *
         * @param output the [OutputStream] to write to
         * @return the [EncoderOutput] that writes to the given [OutputStream]
         */
        @JvmStatic
        fun from(output: OutputStream): EncoderOutput = object : EncoderOutput {

            private var currentByte = 0.toByte()
            private var currentBitIndex = 0

            override fun writeBit(bit: Int) {
                currentByte = currentByte or (bit shl 7 - currentBitIndex).toByte()
                currentBitIndex++

                tryFlush()
            }

            override fun flush() {
                if (currentBitIndex == 0) return
                output.write(currentByte.toInt())
                currentByte = 0.toByte()
                currentBitIndex = 0
            }

            override fun write(byte: Int) =
                if (currentBitIndex == 0) {
                    output.write(byte)
                } else {
                    super.write(byte)
                }

            private fun tryFlush() {
                if (currentBitIndex == 8) {
                    flush()
                }
            }

            override fun write(bytes: ByteArray, start: Int, length: Int) =
                if (currentBitIndex == 0) {
                    output.write(bytes, start, length)
                } else {
                    super.write(bytes, start, length)
                }

        }

        /**
         * Creates an [EncoderOutput] that writes to the given [writeByte] function.
         *
         * @param writeByte the function to write bytes to
         * @return the [EncoderOutput] that writes to the given [writeByte] function
         */
        @JvmStatic
        fun from(writeByte: (Int) -> Unit): EncoderOutput {
            val obj = object : OutputStream() {
                override fun write(b: Int) = writeByte(b)
            }
            return from(obj)
        }

    }

}
