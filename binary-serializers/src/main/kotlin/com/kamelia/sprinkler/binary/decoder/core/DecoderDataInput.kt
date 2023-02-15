package com.kamelia.sprinkler.binary.decoder.core

import java.io.InputStream
import java.nio.ByteBuffer
import kotlin.math.min

/**
 * Abstraction allowing [Decoders][Decoder] to read bytes from a source. This interface provides methods for reading
 * bytes in various ways, including reading a single byte, reading a [ByteArray], reading a [MutableCollection], etc.
 *
 * There is only one method that must be implemented: [read]. All other methods are implemented in terms of this method,
 * meaning that this interface is actually a functional interface and can be implemented as a lambda.
 */
fun interface DecoderDataInput {

    /**
     * Reads a single byte from the source. Returns -1 if there are no more bytes to read.
     *
     * @return the byte read, or -1 if there are no more bytes to read
     */
    fun read(): Int

    /**
     * Reads bytes from the source into the given [ByteArray] and returns the number of bytes read. The [start] and
     * [length] parameters specify the range of indices in the [ByteArray] to read into. The [start] parameter is
     * inclusive, and the [length] parameter is exclusive.
     *
     * @param bytes the [ByteArray] to read into
     * @param start the inclusive start index in the [ByteArray] to read into
     * @param length the exclusive end index in the [ByteArray] to read into
     * @return the number of bytes read
     */
    fun read(bytes: ByteArray, start: Int, length: Int): Int {
        require(start >= 0) { "start must be >= 0, but was $start" }
        require(length >= 0) { "length must be >= 0, but was $length" }
        var index = start
        val end = min(start + length, bytes.size)
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
     * @param collection the [MutableCollection] to read into
     * @param length the maximum number of bytes to read
     */
    fun read(collection: MutableCollection<Byte>, length: Int): Int {
        var count = 0
        while (count < length && collection.size < Int.MAX_VALUE) {
            val read = read()
            if (read == -1) break
            collection.add(read.toByte())
            count++
        }
        return count
    }

    /**
     * Reads bytes from the source into the given [MutableCollection] and returns the number of bytes read. The method
     * will read as many bytes as possible, up to [Int.MAX_VALUE] element.
     *
     * @param collection the [MutableCollection] to read into
     * @return the number of bytes read
     */
    fun read(collection: MutableCollection<Byte>): Int = read(collection, Int.MAX_VALUE)

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
         * An empty [DecoderDataInput] that always returns -1.
         */
        @JvmField
        val EMPTY_INPUT = DecoderDataInput { -1 }

        /**
         * Creates a [DecoderDataInput] from the given [InputStream]. All changes to the [InputStream] will be reflected
         * in the [DecoderDataInput] and vice versa.
         *
         * @param inner the [InputStream] to read from
         * @return a [DecoderDataInput] that reads from the given [InputStream]
         */
        fun from(inner: InputStream): DecoderDataInput = DecoderDataInput(inner::read)

        /**
         * Creates a [DecoderDataInput] from the given [ByteBuffer]. All changes to the [ByteBuffer] will be reflected
         * in the [DecoderDataInput] and vice versa.
         *
         * All reading methods will expect the [ByteBuffer] to be in write mode before the method is called and will
         * leave it in write mode after the method is called. This means that the [ByteBuffer] will be flipped before
         * reading and compacted after reading. This implementation allows to keep the buffer in write mode without
         * having to flip it back and forth.
         *
         * @param inner the [ByteBuffer] to read from
         * @return a [DecoderDataInput] that reads from the given [ByteBuffer]
         */
        fun from(inner: ByteBuffer): DecoderDataInput = object : DecoderDataInput {

            override fun read(): Int {
                inner.flip()
                val byte = if (inner.hasRemaining()) {
                    inner.get().toInt() and 0xFF
                } else {
                    -1
                }
                inner.compact()
                return byte
            }

            override fun read(bytes: ByteArray, start: Int, length: Int): Int {
                require(start >= 0) { "start must be >= 0, but was $start" }
                require(length >= 0) { "length must be >= 0, but was $length" }
                if (inner.position() == 0) return 0
                inner.flip()

                val actualLength = min(length, inner.remaining())
                inner.get(bytes, start, actualLength)
                inner.compact()
                return actualLength
            }

        }

        /**
         * Creates a [DecoderDataInput] from the given [ByteArray]. The [ByteArray] will not be modified by the returned
         * [DecoderDataInput]. However, the [ByteArray] will not be copied, so any changes to the [ByteArray] will be
         * reflected in the [DecoderDataInput].
         *
         * @param inner the [ByteArray] to read from
         * @return a [DecoderDataInput] that reads from the given [ByteArray]
         */
        fun from(inner: ByteArray): DecoderDataInput = object : DecoderDataInput {
            private var index = 0

            override fun read(): Int = if (index < inner.size) {
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

    }

}

