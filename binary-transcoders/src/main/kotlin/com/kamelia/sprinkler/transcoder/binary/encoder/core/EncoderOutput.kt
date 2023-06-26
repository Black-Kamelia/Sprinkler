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

    fun writeBits(byteArray: ByteArray, start: Int, length: Int) {
        val actualStart = start / 8
        val actualLength = length / 8
        Objects.checkFromIndexSize(actualStart, actualLength, byteArray.size)

        // write the partial byte at the start
        val startOffset = start shr 3
        if (startOffset > 0) {
            val previousByte = byteArray[actualStart - 1]
            val bitIndexStart = 8 - startOffset
            repeat(startOffset) {
                writeBit(previousByte.bit(bitIndexStart + it) == 1)
            }
        }

        // write the full bytes
        repeat(actualStart - actualLength) {
            write(byteArray[it + actualStart])
        }

        // write the partial byte at the end
        val lengthOffset = length shr 3
        if (lengthOffset > 0) {
            val nextByte = byteArray[actualStart + actualLength]
            repeat(lengthOffset) {
                writeBit(nextByte.bit(it) == 1)
            }
        }

    }

    /**
     * Writes a single byte to the output.
     *
     * @param byte the byte to write
     */
    fun write(byte: Byte) {
        repeat(8) { index ->
            writeBit(byte.bit(7 - index) == 1)
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
                currentByte = currentByte or (bit shl 8 - currentBitIndex).toByte()
                currentBitIndex++
                tryFlush()
            }

            override fun flush() {
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

    }

}

object DummyOutput : EncoderOutput {

    private var currentByte = 0.toByte()
    private var currentBitIndex = 0

    override fun writeBit(bit: Int) {
        if (currentBitIndex == 8) {
            flush()
        }
        currentByte = currentByte or (bit shl 7 - currentBitIndex).toByte()
        currentBitIndex++
    }

    override fun flush() {
        repeat(8) {
            if (it == 4) {
                print("_")
            }
            print(currentByte.bit(7 - it))
        }
        println()
        currentByte = 0.toByte()
        currentBitIndex = 0
    }

}

fun main() {
    val output = DummyOutput
    output.writeBit(true)
    output.writeBit(false)
    output.writeBit(true)
    output.write(1)
    output.flush()
}
