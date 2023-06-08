package com.kamelia.sprinkler.transcoder.binary.encoder.core

import java.io.OutputStream
import java.util.*

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
fun interface EncoderOutput {

    /**
     * Writes a single byte to the output.
     *
     * @param byte the byte to write
     */
    fun write(byte: Byte)

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

            override fun write(byte: Byte): Unit = output.write(byte.toInt())

            override fun write(bytes: ByteArray, start: Int, length: Int) {
                Objects.checkFromIndexSize(start, length, bytes.size)
                output.write(bytes, start, length)
            }

        }

    }

}
