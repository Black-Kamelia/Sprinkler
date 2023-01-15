package com.kamelia.sprinkler.binary.decoder

import java.io.InputStream
import java.nio.ByteBuffer
import kotlin.math.max
import kotlin.math.min


fun interface DecoderDataInput {

    fun read(): Int

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

    fun read(bytes: ByteArray, start: Int): Int = read(bytes, start, bytes.size - start)

    fun read(bytes: ByteArray): Int = read(bytes, 0, bytes.size)

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

        fun from(inner: InputStream): DecoderDataInput = DecoderDataInput {
            inner.read()
        }

        fun from(inner: ByteBuffer): DecoderDataInput = object : DecoderDataInput {

            override fun read(): Int {
                inner.flip()
                val byte = if (inner.hasRemaining()) {
                    inner.get().toInt()
                } else {
                    -1
                }
                inner.compact()
                return byte
            }

            override fun read(bytes: ByteArray, start: Int, length: Int): Int {
                require(start >= 0) { "Start index must be greater than or equal to 0, got $start" }
                inner.flip()
                val remainingBefore = inner.remaining()
                inner.get(bytes, start, length)
                val remainingAfter = inner.remaining()
                inner.compact()
                return remainingBefore - remainingAfter
            }

        }

        fun from(inner: ByteArray): DecoderDataInput = object : DecoderDataInput {
            private var index = 0

            override fun read(): Int = if (index < inner.size) {
                inner[index++].toInt()
            } else {
                -1
            }

            override fun skip(n: Long): Long {
                index += n.toInt()
                return min(n.toInt(), inner.size - index).toLong()
            }
        }

    }

}
