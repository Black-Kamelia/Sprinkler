package com.kamelia.sprinkler.codec.binary.encoder.core

import java.io.OutputStream

fun interface EncoderOutput {

    fun write(byte: Byte)

    fun write(bytes: ByteArray, start: Int, length: Int) {
        require(start >= 0) { "Start must be >= 0, was $start" }
        require(length >= 0) { "Length must be >= 0, was $length" }
        val lastIndex = start + length
        require(lastIndex <= bytes.size) {
            "start + length must be <= bytes.size, sum was ${lastIndex}, bytes.size was ${bytes.size}"
        }
        for (index in start until lastIndex) {
            write(bytes[index])
        }
    }

    fun write(bytes: ByteArray, start: Int): Unit = write(bytes, start, bytes.size - start)

    fun write(bytes: ByteArray): Unit = write(bytes, 0, bytes.size)

    fun write(bytes: Iterable<Byte>): Unit = bytes.forEach(::write)

    class ByteListOutput : EncoderOutput {

        private val inner = ArrayList<Byte>()

        override fun write(byte: Byte) {
            inner.add(byte)
        }

        fun toByteArray(): ByteArray = inner.toByteArray()

        fun toList(): List<Byte> = inner.toList()

        override fun toString(): String = inner.toString()

    }

    class OutputStreamOutput(private val output: OutputStream) : EncoderOutput {

        override fun write(byte: Byte): Unit = output.write(byte.toInt())

        override fun write(bytes: ByteArray) {
            output.write(bytes)
        }

        override fun toString(): String = output.toString()

    }

}
