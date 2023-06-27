package com.kamelia.sprinkler.transcoder.binary.encoder.core

import com.kamelia.sprinkler.util.bit
import java.io.OutputStream
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
            val bit = byte.bit(7 - start - it)
            writeBit(bit)
        }
    }

    fun writeBits(byte: Int, length: Int) = writeBits(byte, 0, length)

    fun writeBits(byte: Byte, start: Int, length: Int) = writeBits(byte.toInt(), start, length)

    fun writeBits(byte: Byte, length: Int) = writeBits(byte.toInt(), 0, length)

    fun writeBits(byteArray: ByteArray, start: Int, length: Int) {
        Objects.checkFromIndexSize(start, length, byteArray.size * 8)
        val actualStart = start / 8

        // write the partial byte at the start
        val prefixPart = start - 8 * actualStart // start % 8
        val hasPrefix = prefixPart > 0
        if (hasPrefix) {
            writeBits(byteArray[actualStart], prefixPart, 8 - prefixPart)
        }

        val prefixOffset = if (hasPrefix) 1 else 0
        // write the full bytes
        val bitLeft = length - if (hasPrefix) (8 - prefixPart) else 0
        val iterations = bitLeft / 8
        if (iterations > 0) {
            write(byteArray, actualStart + prefixOffset, iterations)
//            repeat(iterations) {
//                write(byteArray[actualStart + it + prefixOffset])
//            }
        }

        // write the partial byte at the end
        val suffixPartSize = bitLeft - 8 * iterations
        if (suffixPartSize > 0) {
            writeBits(byteArray[actualStart + iterations + prefixOffset], 0, suffixPartSize)
        }
    }

    fun writeBits(bytes: ByteArray, start: Int) = writeBits(bytes, start, bytes.size * 8 - start)

    fun writeBits(bytes: ByteArray) = writeBits(bytes, 0, bytes.size * 8)

    /**
     * Writes a single byte to the output.
     *
     * @param byte the byte to write
     */
    fun write(byte: Byte) {
        repeat(8) { index ->
            writeBit(byte.bit(7 - index))
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
                println("WRITE BIT $bit")
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

        fun from(writeByte: (Int) -> Unit): EncoderOutput {
            val obj = object : OutputStream() {
                override fun write(b: Int) = writeByte(b)
            }
            return from(obj)
        }

    }

}
