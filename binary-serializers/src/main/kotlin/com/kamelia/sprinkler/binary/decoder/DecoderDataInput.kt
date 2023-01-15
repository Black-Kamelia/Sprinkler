package com.kamelia.sprinkler.binary.decoder

import java.io.InputStream
import java.nio.ByteBuffer
import kotlin.math.max


fun interface DecoderDataInput {

    fun read(): Int

    fun read(bytes: ByteArray, start: Int, length: Int): Int {
        require(start >= 0) { "start must be >= 0, but was $start" }
        require(length >= 0) { "length must be >= 0, but was $length" }
        var index = start
        val end = max(start + length, bytes.size)
        while (index < end) {
            val read = read()
            if (read == -1) break
            index++
        }
        return index - start
    }

    fun read(bytes: ByteArray, start: Int): Int = read(bytes, start, bytes.size - start)

    fun read(bytes: ByteArray): Int = read(bytes, 0, bytes.size)

    companion object {

        fun from(inputStream: InputStream): DecoderDataInput = DecoderDataInput { inputStream.read() }

        fun from(buffer: ByteBuffer): DecoderDataInput = object : DecoderDataInput {

            override fun read(): Int {
                buffer.flip()
                val byte = if (buffer.hasRemaining()) {
                    buffer.get().toInt()
                } else {
                    -1
                }
                buffer.compact()
                return byte
            }

            override fun read(bytes: ByteArray, start: Int, length: Int): Int {
                require(start >= 0) { "Start index must be greater than or equal to 0, got $start" }
                buffer.flip()
                val remainingBefore = buffer.remaining()
                buffer.get(bytes, start, length)
                val remainingAfter = buffer.remaining()
                buffer.compact()
                return remainingBefore - remainingAfter
            }
        }

    }

}
