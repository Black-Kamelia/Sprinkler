package com.kamelia.sprinkler.transcoder.binary.decoder.core

import java.io.InputStream
import java.nio.ByteBuffer
import java.util.*
import kotlin.math.min

/**
 * Abstraction allowing [Decoders][Decoder] to read bytes from a source. This interface provides methods for reading
 * bytes in various ways, including reading a single byte, reading bytes into a [ByteArray], reading bytes into a
 * [MutableCollection], etc.
 *
 * [read] is the only method that must be implemented. All other methods are default implemented depending on this
 * method, meaning that this interface is actually a functional interface and can be implemented as a lambda, as shown
 * below:
 *
 * &nbsp;
 *
 * ```
 * var byte = 0.toByte()
 * val myInput = DecoderInput { byte++ } // myInput.read() will return 0, 1, 2, 3, ...
 * ```
 *
 * @see Decoder
 */
interface DecoderInput {

    /**
     * Reads a single bit from the source. Returns -1 if there are no more bits to read.
     *
     * @return the bit read, or -1 if there are no more bits to read
     */
    fun readBit(): Int

    /**
     * Reads a full byte from the source. Returns -1 if there is less than 1 byte left to read.
     *
     * @return the byte read, or -1 if there is less than 1 byte left to read
     */
    fun read(): Int

    /**
     * Reads [length] bits from the source and writes them to the given [bytes] byte array. The bits are written
     * starting at the [start] bit index in the [bytes] array. Returns the number of bits actually read.
     *
     * @param bytes the byte array to write the bits to
     * @param start the start bit index in the [bytes] array
     * @param length the number of bits to read
     * @return the number of bits actually read
     * @throws IndexOutOfBoundsException if [start] < 0 or [length] < 0 or [start] + [length] > [ByteArray.size] * 8
     */
    fun readBits(bytes: ByteArray, start: Int, length: Int): Int {
        Objects.checkFromIndexSize(start, length, bytes.size * 8)
        val actualStart = start / 8
        var readBits = 0

        val prefixOffset = start and 7
        val readPre = innerReadBits(bytes, actualStart, prefixOffset, min(8 - prefixOffset, length))
        if (readPre == -1) return 0
        readBits += readPre

        val fullBytes = (length - start) / 8
        val fullBytesStart = if (prefixOffset == 0) actualStart else actualStart + 1
        val fullBytesRead = read(bytes, fullBytesStart, fullBytes)
        if (fullBytesRead == -1) return readBits
        readBits += fullBytesRead * 8
        if (fullBytesRead < fullBytes) return readBits

        val suffixOffset = (length - readBits) and 7
        val lastIndex = (start + length) / 8
        val readPost = innerReadBits(bytes, lastIndex, 0, suffixOffset)
        if (readPost == -1) return readBits
        readBits += readPost

        return readBits
    }

    /**
     * Reads bytes from the source into the given [ByteArray] and returns the number of bytes read. The [start] and
     * [length] parameters specify the range of indices in the [ByteArray] to read into. The [start] parameter is
     * inclusive, and the [length] parameter is exclusive.
     *
     * @param bytes the [ByteArray] to read into
     * @param start the inclusive start index in the [ByteArray] to read into
     * @param length the exclusive end index in the [ByteArray] to read into
     * @return the number of bytes read
     * @throws IndexOutOfBoundsException if [start] < 0 or [length] < 0 or [start] + [length] > [ByteArray.size]
     */
    fun read(bytes: ByteArray, start: Int, length: Int): Int {
        Objects.checkFromIndexSize(start, length, bytes.size)
        var index = start
        val end = start + length
        while (index < end) {
            val read = read()
            if (read == -1) break
            bytes[index] = read.toByte()
            index++
        }
        return index - start
    }

    /**
     * Reads bytes from the source into the given [ByteArray] and returns the number of bytes read. The [start]
     * parameter specifies the start index in the [ByteArray] to read into. The method will read as many bytes as
     * possible, up to the end of the [ByteArray].
     *
     * @param bytes the [ByteArray] to read into
     * @param start the inclusive start index in the [ByteArray] to read into
     * @return the number of bytes read
     */
    fun read(bytes: ByteArray, start: Int): Int = read(bytes, start, bytes.size - start)

    /**
     * Reads bytes from the source into the given [ByteArray] and returns the number of bytes read. The method will
     * try to fill the entire [ByteArray].
     *
     * @param bytes the [ByteArray] to read into
     * @return the number of bytes read
     */
    fun read(bytes: ByteArray): Int = read(bytes, 0, bytes.size)

    /**
     * Reads bytes from the source into the given [MutableCollection] and returns the number of bytes read. The [length]
     * parameter specifies the maximum number of bytes to read.
     *
     * @param bytes the [MutableCollection] to read into
     * @param length the maximum number of bytes to read
     */
    fun read(bytes: MutableCollection<Byte>, length: Int): Int {
        var count = 0
        while (count < length && bytes.size < Int.MAX_VALUE) {
            val read = read()
            if (read == -1) break
            bytes += read.toByte()
            count++
        }
        return count
    }

    /**
     * Reads bytes from the source into the given [MutableCollection] and returns the number of bytes read. The method
     * will read as many bytes as possible, up to [Int.MAX_VALUE] element.
     *
     * @param bytes the [MutableCollection] to read into
     * @return the number of bytes read
     */
    fun read(bytes: MutableCollection<Byte>): Int = read(bytes, Int.MAX_VALUE)

    /**
     * Skips the given number of bytes. Returns the number of bytes actually skipped.
     *
     * @param n the number of bytes to skip
     * @return the number of bytes actually skipped
     */
    fun skip(n: Long): Long {
        var skipped = 0L
        while (skipped < n) {
            val read = read()
            if (read == -1) break
            skipped++
        }
        return skipped
    }

    companion object {

        /**
         * Creates a [DecoderInput] that always returns -1 when reading.
         *
         * @return a [DecoderInput] that always returns -1 when reading
         */
        @JvmStatic
        fun nullInput(): DecoderInput = object : DecoderInput {
            override fun readBit(): Int = -1
            override fun read(): Int = -1
            override fun readBits(bytes: ByteArray, start: Int, length: Int): Int = -1
            override fun read(bytes: ByteArray, start: Int, length: Int): Int = -1
        }

        /**
         * Creates a [DecoderInput] from the given [InputStream]. All changes to the [InputStream] will be reflected
         * in the [DecoderInput] and vice versa.
         *
         * @param inner the [InputStream] to read from
         * @return a [DecoderInput] that reads from the given [InputStream]
         */
        @JvmStatic
        fun from(inner: InputStream): DecoderInput = object : AbstractDecoderInput() {
            override fun readByte(): Int = inner.read()
        }

        /**
         * Creates a [DecoderInput] from the given [ByteBuffer]. All changes to the [ByteBuffer] will be reflected
         * in the [DecoderInput] and vice versa.
         *
         * All reading methods will expect the [ByteBuffer] to be in write mode before the method is called and will
         * leave it in write mode after the method is called. This means that the [ByteBuffer] will be flipped before
         * reading and compacted after reading. This implementation allows to keep the buffer in write mode without
         * having to flip it back and forth.
         *
         * @param inner the [ByteBuffer] to read from
         * @return a [DecoderInput] that reads from the given [ByteBuffer]
         */
        @JvmStatic
        fun from(inner: ByteBuffer): DecoderInput = object : AbstractDecoderInput() {

            override fun readBits(bytes: ByteArray, start: Int, length: Int): Int {
                inner.flip()
                val result = super.readBits(bytes, start, length)
                inner.compact()
                return result
            }

            override fun read(bytes: ByteArray, start: Int, length: Int): Int {
                Objects.checkFromIndexSize(start, length, bytes.size)
                if (bitLeft != 0) return readBits(bytes, start, length * 8)
                if (inner.position() == 0) return 0
                inner.flip()

                val actualLength = min(length, inner.remaining())
                inner.get(bytes, start, actualLength)
                inner.compact()
                return actualLength
            }

            override fun readByte(): Int {
                inner.flip()
                val byte = if (inner.hasRemaining()) {
                    inner.get().toInt() and 0xFF
                } else {
                    -1
                }
                inner.compact()
                return byte
            }

        }

        /**
         * Creates a [DecoderInput] from the given [ByteArray]. The [ByteArray] will not be modified by the returned
         * [DecoderInput]. However, the [ByteArray] will not be copied, so any changes to the [ByteArray] will be
         * reflected in the [DecoderInput].
         *
         * @param inner the [ByteArray] to read from
         * @return a [DecoderInput] that reads from the given [ByteArray]
         */
        @JvmStatic
        fun from(inner: ByteArray): DecoderInput = object : AbstractDecoderInput() {

            private var index = 0

            override fun readByte(): Int = if (index < inner.size) {
                inner[index++].toInt() and 0xFF
            } else {
                -1
            }

            override fun skip(n: Long): Long {
                val oldIndex = index
                index = min(index + n.toInt(), inner.size)
                return index - oldIndex.toLong()
            }

        }

        /**
         * Creates a [DecoderInput] from the given function. The function will be called whenever a byte is read. The
         * function should return -1 when there are no more bytes to read.
         *
         * @param readByte the function to call when a byte is read
         * @return a [DecoderInput] that reads from the given function
         */
        @JvmStatic
        fun from(readByte: () -> Int): DecoderInput = object : AbstractDecoderInput() {
            override fun readByte(): Int = readByte()
        }

    }

}

private fun DecoderInput.innerReadBits(bytes: ByteArray, index: Int, bitIndex: Int, length: Int): Int {
    var result = 0
    var readBits = 0
    for (it in 0 until length) {
        val bit = readBit()
        if (bit == -1) break
        readBits++
        result = result or (bit shl 7 - bitIndex - it)
    }
    bytes[index] = (bytes[index].toInt() or result).toByte()
    return readBits
}

private abstract class AbstractDecoderInput : DecoderInput {

    // the bits in the buffer are simply shifted to the left when reading
    // the reading is always done starting from the 15th bit (where 0 is the lsb)
    protected var buffer = 0 // 2 bytes buffer
        private set
    protected var bitLeft = 0 // number of bits left in the buffer [0-15]
        private set

    final override fun readBit(): Int {
        if (bitLeft == 0 && !refillBuffer()) return -1
        val result = (buffer ushr 15) and 1
        buffer = buffer shl 1
        bitLeft--
        return result
    }

    final override fun read(): Int = if (bitLeft == 0) {
        readByte()
    } else {
        if (bitLeft < 8 && !refillBuffer()) {
            -1
        } else {
            // read 8 bits from the buffer
            val result = ((buffer and 0xFF00) ushr 8) and 0xFF
            buffer = buffer shl 8
            bitLeft -= 8
            result
        }
    }

    private fun refillBuffer(): Boolean {
        val nextByte = readByte()
        if (nextByte == -1) return false
        val shiftNextByte = nextByte shl (16 - bitLeft - 8)
        buffer = buffer or shiftNextByte
        bitLeft += 8
        return true
    }

    protected abstract fun readByte(): Int

}
