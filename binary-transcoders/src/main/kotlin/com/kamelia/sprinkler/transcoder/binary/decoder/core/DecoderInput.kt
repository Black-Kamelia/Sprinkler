package com.kamelia.sprinkler.transcoder.binary.decoder.core

import com.kamelia.sprinkler.transcoder.binary.common.BitOrder
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * Abstraction allowing [Decoders][Decoder] to read bytes and bits from a source. This interface provides methods for
 * reading bytes and bits in various ways, including reading a single byte/bit, reading (from the source to write)
 * bytes/bits into a [ByteArray], reading bytes into a [MutableCollection], etc.
 *
 * This interface provides a factory [DecoderInput.from] to create a [DecoderInput] by only providing a function that
 * reads the next byte from the source, allowing to easily create a [DecoderInput] from any king of input, as shown in
 * the example below:
 *
 * &nbsp;
 *
 * ```
 * var byte = 0.toByte()
 * val myInput = DecoderInput.from { byte++ } // myInput.read() will return 0, 1, 2, 3, ...
 * ```
 *
 * @see Decoder
 */
interface DecoderInput {

    /**
     * Reads a single bit from the source. Returns -1 if there are no more bits to read.
     *
     * **Note:** This method is similar to [InputStream.read] in the sense that the returned value is an [Int] and not
     * a [Boolean]. This is because the returned value is -1 if there is less than 1 bit left to read, and -1 is not a
     * valid bit value, so there is no risk of ambiguity with a valid byte value (0 or 1).
     *
     * @return the bit read, or -1 if there are no more bits to read
     * @throws IOException if an I/O error occurs
     */
    fun readBit(): Int

    /**
     * Reads a full byte from the source. Returns -1 if there is less than 1 byte left to read.
     *
     * **Note:** This method is similar to [InputStream.read] in the sense that the returned value is an [Int] and not
     * a [Byte]. This is because the returned value is -1 if there is less than 1 byte left to read, and -1 is not a
     * valid byte value, so there is no risk of ambiguity with a valid byte value (0 to 255).
     *
     * @return the byte read, or -1 if there is less than 1 byte left to read
     * @throws IOException if an I/O error occurs
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
     * @throws IOException if an I/O error occurs
     */
    fun readBits(bytes: ByteArray, start: Int, length: Int): Int {
        Objects.checkFromIndexSize(start, length, bytes.size * 8)
        if (bytes.isEmpty() || length == 0) return 0

        val actualStart = start / 8
        var readBits = 0

        val prefixOffset = start and 7
        val readFromPrefix = if (prefixOffset > 0) min(8 - prefixOffset, length) else 0
        val readPre = innerReadBits(bytes, actualStart, prefixOffset, readFromPrefix)
        if (readPre == 0) return -1
        readBits += readPre
        if (readPre < readFromPrefix) return readBits

        val fullBytes = (length - readFromPrefix) / 8
        val fullBytesStart = if (prefixOffset == 0) actualStart else actualStart + 1
        val fullBytesRead = max(0, read(bytes, fullBytesStart, fullBytes))
        readBits += fullBytesRead * 8
        if (fullBytesRead < fullBytes) { // less than 1 byte left to read, we still try to read the remaining bits
            // there is at most 7 bits left to read
            val bitsRead = innerReadBits(bytes, fullBytesStart + fullBytesRead, 0, 7)
            readBits += bitsRead
            return readBits
        }

        val suffixOffset = (length - readBits) and 7
        val lastIndex = (start + length) / 8
        val readPost = innerReadBits(bytes, lastIndex, 0, suffixOffset)
        readBits += readPost
        if (readPost < suffixOffset) return readBits

        return readBits
    }

    /**
     * Reads bytes from the source into the given [ByteArray] and returns the number of bytes read. The [start] and
     * [length] parameters specify the range of indices in the [ByteArray] to read into. The [start] parameter is
     * inclusive, and the [length] parameter is exclusive.
     *
     * If [length] is zero, then no bytes are read and `0` is returned; otherwise, there is an attempt to read at
     * least one byte. If no byte is available because the stream is at end of file, the value `-1` is returned;
     * otherwise, at least one byte is read and stored into the [ByteArray][bytes].
     *
     * @param bytes the [ByteArray] to read into
     * @param start the inclusive start index in the [ByteArray] to read into
     * @param length the exclusive end index in the [ByteArray] to read into
     * @return the number of bytes read
     * @throws IndexOutOfBoundsException if [start] < 0 or [length] < 0 or [start] + [length] > [ByteArray.size]
     * @throws IOException if an I/O error occurs
     */
    fun read(bytes: ByteArray, start: Int, length: Int): Int {
        Objects.checkFromIndexSize(start, length, bytes.size)

        // We need to try to read the first byte, to check if the stream is at the end of file
        // If that's the case, we return -1 immediately
        // Otherwise we proceed normally
        if (length == 0) return 0
        val firstByte = read()
        if (firstByte == -1) return -1
        bytes[start] = firstByte.toByte()

        for (i in start + 1 until start + length) {
            val read = read()
            if (read == -1) return i - start
            bytes[i] = read.toByte()
        }
        return length
    }

    /**
     * Reads bytes from the source into the given [ByteArray] and returns the number of bytes read. The [start]
     * parameter specifies the start index in the [ByteArray] to read into. The method will read as many bytes as
     * possible, up to the end of the [ByteArray].
     *
     * If the size to read (`[bytes].size - [start]`) is zero, then no bytes are read and `0` is returned; otherwise,
     * there is an attempt to read at least one byte. If no byte is available because the stream is at end of file,
     * the value `-1` is returned; otherwise, at least one byte is read and stored into the [ByteArray][bytes].
     *
     * @param bytes the [ByteArray] to read into
     * @param start the inclusive start index in the [ByteArray] to read into
     * @return the number of bytes read
     * @throws IOException if an I/O error occurs
     */
    fun read(bytes: ByteArray, start: Int): Int = read(bytes, start, bytes.size - start)

    /**
     * Reads bytes from the source into the given [ByteArray] and returns the number of bytes read. The method will
     * try to fill the entire [ByteArray].
     *
     * If the [ByteArray][bytes]'s length is zero, then no bytes are read and `0` is returned; otherwise,
     * there is an attempt to read at least one byte. If no byte is available because the stream is at end of file,
     * the value `-1` is returned; otherwise, at least one byte is read and stored into the [ByteArray][bytes].
     *
     * @param bytes the [ByteArray] to read into
     * @return the number of bytes read
     */
    fun read(bytes: ByteArray): Int = read(bytes, 0, bytes.size)

    /**
     * Reads bytes from the source into the given [MutableCollection] and returns the number of bytes read. The [length]
     * parameter specifies the maximum number of bytes to read.
     *
     * If [length] is zero, then no bytes are read and `0` is returned; otherwise, there is an attempt to read at
     * least one byte. If no byte is available because the stream is at end of file, the value `-1` is returned;
     * otherwise, at least one byte is read and stored into the [MutableCollection][bytes].
     *
     * @param bytes the [MutableCollection] to read into
     * @param length the maximum number of bytes to read
     * @throws IOException if an I/O error occurs
     */
    fun read(bytes: MutableCollection<Byte>, length: Int): Int {
        if (length == 0) return 0
        var read = 0

        // We need to try to read the first byte, to check if the stream is at the end of file
        // If that's the case, we return -1 immediately
        // Otherwise we proceed normally
        val firstByte = read()
        if (firstByte == -1) return -1
        if (bytes.add(firstByte.toByte())) read++
        else return 0

        for (i in 1 until length) {
            val readByte = read()
            if (readByte == -1) break
            if (!bytes.add(readByte.toByte())) break
            read++
        }
        return read
    }

    /**
     * Reads bytes from the source into the given [MutableCollection] and returns the number of bytes read. The method
     * will read as many bytes as possible, up to [Int.MAX_VALUE] element.
     *
     * If no byte is available because the stream is at end of file, the value `-1` is returned; otherwise, at least one
     * byte is read and stored into the [MutableCollection][bytes].
     *
     * @param bytes the [MutableCollection] to read into
     * @return the number of bytes read
     * @throws IOException if an I/O error occurs
     */
    fun read(bytes: MutableCollection<Byte>): Int = read(bytes, Int.MAX_VALUE)

    /**
     * Skips the given number of bytes. Returns the number of bytes actually skipped.
     *
     * @param n the number of bytes to skip
     * @return the number of bytes actually skipped
     * @throws IOException if an I/O error occurs
     */
    fun skip(n: Long): Long {
        var skipped = 0L
        for (i in 0 until n) {
            if (read() == -1) break
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
            override fun readBits(bytes: ByteArray, start: Int, length: Int): Int {
                Objects.checkFromIndexSize(start, length, bytes.size * 8)
                return -1
            }

            override fun read(bytes: ByteArray, start: Int, length: Int): Int {
                Objects.checkFromIndexSize(start, length, bytes.size)
                return -1
            }
        }

        /**
         * Creates a [DecoderInput] from the given [InputStream]. The [order] parameter specifies the order in which
         * bits are read from the [InputStream].
         *
         * All changes to the [InputStream] will be reflected in the [DecoderInput] and vice versa.
         *
         * @param inner the [InputStream] to read from
         * @param order the [BitOrder] to use when reading bits
         * @return a [DecoderInput] that reads from the given [InputStream]
         */
        @JvmStatic
        @JvmOverloads
        fun from(inner: InputStream, order: BitOrder = BitOrder.MSB_FIRST): DecoderInput =
            object : AbstractDecoderInput() {
                override fun readByte(): Int = inner.read()
            }

        /**
         * Creates a [DecoderInput] from the given [ByteBuffer]. The [order] parameter specifies the order in which
         * bits are read from the [ByteBuffer].
         *
         * All changes to the [ByteBuffer] will be reflected in the [DecoderInput] and vice versa.
         *
         * All reading methods will expect the [ByteBuffer] to be in write mode before the method is called and will
         * leave it in write mode after the method is called. This means that the [ByteBuffer] will be flipped before
         * reading and compacted after reading. This implementation allows to keep the buffer in write mode without
         * having to flip it back and forth.
         *
         * @param inner the [ByteBuffer] to read from
         * @param order the [BitOrder] to use when reading bits
         * @return a [DecoderInput] that reads from the given [ByteBuffer]
         */
        @JvmStatic
        @JvmOverloads
        fun from(inner: ByteBuffer, order: BitOrder = BitOrder.MSB_FIRST): DecoderInput =
            object : AbstractDecoderInput() {

                private var isInWriteMode = true

                override fun read(bytes: ByteArray, start: Int, length: Int): Int {
                    Objects.checkFromIndexSize(start, length, bytes.size)
                    if (length == 0 || inner.position() == 0) return -1

                    inner.flip()
                    isInWriteMode = false

                    val read = if (bitLeft != 0) {
                        super.read(bytes, start, length)
                    } else {
                        val actualLength = min(length, inner.remaining())
                        inner.get(bytes, start, actualLength)
                        actualLength
                    }

                    inner.compact()
                    isInWriteMode = true

                    return read
                }

                override fun readByte(): Int {
                    if (isInWriteMode) {
                        inner.flip()
                    }
                    val byte = if (inner.hasRemaining()) {
                        inner.get().toInt() and 0xFF
                    } else {
                        -1
                    }
                    if (isInWriteMode) {
                        inner.compact()
                    }
                    return byte
                }

            }

        /**
         * Creates a [DecoderInput] from the given [ByteArray]. The [order] parameter specifies the order in which
         * bits are read from the [ByteArray].
         *
         * The [ByteArray] will not be modified by the returned [DecoderInput]. However, the [ByteArray] will not be
         * copied, so any changes to the [ByteArray] will be reflected in the [DecoderInput].
         *
         * @param inner the [ByteArray] to read from
         * @param order the [BitOrder] to use when reading bits
         * @return a [DecoderInput] that reads from the given [ByteArray]
         */
        @JvmStatic
        @JvmOverloads
        fun from(inner: ByteArray, order: BitOrder = BitOrder.MSB_FIRST): DecoderInput =
            object : AbstractDecoderInput() {

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
         * Creates a [DecoderInput] from the given function. The [order] parameter specifies the order in which bits
         * are read from the function.
         *
         * The function will be called whenever a byte is read. The
         * function should return -1 when there are no more bytes to read.
         *
         * @param order the [BitOrder] to use when reading bits
         * @param readByte the function to call when a byte is read
         * @return a [DecoderInput] that reads from the given function
         */
        @JvmStatic
        @JvmOverloads
        fun from(order: BitOrder = BitOrder.MSB_FIRST, readByte: () -> Int): DecoderInput =
            object : AbstractDecoderInput() {
                override fun readByte(): Int = readByte()
            }

    }

}

private fun DecoderInput.innerReadBits(bytes: ByteArray, index: Int, bitIndex: Int, length: Int): Int {
    if (length == 0) return 0
    var result = 0
    var readBits = 0
    for (it in 0 until length) {
        val bit = readBit()
        if (bit == -1) {
            if (it == 0) return 0
            break
        }
        readBits++
        result = result or (bit shl 7 - bitIndex - it)
    }
    val maskPre = (0xFF shl 8 - bitIndex) // mask where all the bits before bitIndex are 1
    val maskPost = (0xFF ushr bitIndex + readBits) // mask where all the bits after bitIndex + length are 1
    val mask = maskPre or maskPost
    val old = bytes[index].toInt() and mask
    bytes[index] = (old or result).toByte()
    return readBits
}

private abstract class AbstractDecoderInput : DecoderInput {

    // the bits in the buffer are simply shifted to the left when reading
    // the reading is always done starting from the 15th bit (where 0 is the lsb)
    private var buffer = 0 // 2 bytes buffer
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
        if (!refillBuffer()) {
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
