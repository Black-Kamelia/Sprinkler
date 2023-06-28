package com.kamelia.sprinkler.transcoder.binary.encoder.core

import com.kamelia.sprinkler.util.bit
import java.io.OutputStream
import java.lang.Integer.min
import java.util.*
import kotlin.experimental.or

/**
 * Abstraction allowing [Encoders][Encoder] to write bytes. This interface provides methods for writing bytes in various
 * ways, including writing a single byte, writing a [ByteArray], writing an [Iterable] of bytes, etc.
 *
 * [write] is the only method that must be implemented. All other methods are default implemented depending on this
 * method, meaning that this interface is actually a functional interface and can be implemented as a lambda, as shown
 * below:
 *
 * &nbsp;
 *
 * ```
 * val myOutput = EncoderOutput { byte -> println(byte) } // myOutput.write(byte) will print the byte
 * ```
 *
 * @see Encoder
 */
interface EncoderOutput {

    fun writeBit(bit: Int)

    fun writeBit(bit: Byte) = writeBit(bit.toInt())

    fun writeBit(bit: Boolean) = writeBit(if (bit) 1 else 0)

    fun flush()

    fun writeBits(byte: Int, start: Int, length: Int) {
        Objects.checkFromIndexSize(start, length, 8)
        repeat(length) {
            writeBit(byte.bit(7 - start - it))
        }
    }

    fun writeBits(byte: Int, length: Int) = writeBits(byte, 0, length)

    fun writeBits(byte: Byte, start: Int, length: Int) = writeBits(byte.toInt(), start, length)

    fun writeBits(byte: Byte, length: Int) = writeBits(byte.toInt(), 0, length)

    fun writeBits(byteArray: ByteArray, start: Int, length: Int) {
        Objects.checkFromIndexSize(start, length, byteArray.size * 8)
        val actualStart = start / 8

        // write the partial byte at the start
        val prefixOffset = start and 7
        val writtenFromPrefix = min(8 - prefixOffset, length)
        writeBits(byteArray[actualStart], prefixOffset, writtenFromPrefix)

        // write the full bytes
        val fullBytes = (length - prefixOffset) / 8
        val fullBytesStart = if (prefixOffset == 0) actualStart else actualStart + 1
        write(byteArray, fullBytesStart, fullBytes)

        // write the partial byte at the end
        val suffixOffset = (length - writtenFromPrefix - fullBytes * 8) and 7
        val lastIndex = (start + length) / 8
        writeBits(byteArray[lastIndex], 0, suffixOffset)
    }

    fun writeBits(bytes: ByteArray, start: Int) = writeBits(bytes, start, bytes.size * 8 - start)

    fun writeBits(bytes: ByteArray) = writeBits(bytes, 0, bytes.size * 8)

    /**
     * Writes a single byte to the output.
     *
     * @param byte the byte to write
     */
    fun write(byte: Byte) {
        repeat(8) {
            writeBit(byte.bit(7 - it))
        }
    }

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
        val lastIndex = start + length

        for (index in start until lastIndex) {
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

        @JvmStatic
        fun nullOutput(): EncoderOutput = object : EncoderOutput {
            override fun writeBit(bit: Int) = Unit
            override fun write(byte: Byte) = Unit
            override fun writeBits(byte: Byte, start: Int, length: Int) = Unit
            override fun write(bytes: ByteArray, start: Int, length: Int) = Unit
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

            override fun write(byte: Byte) {
                if (currentBitIndex == 0) {
                    output.write(byte.toInt())
                    return
                }
                super.write(byte)
            }

            private fun tryFlush() {
                if (currentBitIndex == 8) {
                    flush()
                }
            }

            override fun write(bytes: ByteArray, start: Int, length: Int) {
                Objects.checkFromIndexSize(start, length, bytes.size)
                output.write(bytes, start, length)
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
