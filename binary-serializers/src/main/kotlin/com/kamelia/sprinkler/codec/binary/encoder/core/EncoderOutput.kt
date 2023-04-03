package com.kamelia.sprinkler.codec.binary.encoder.core

import java.io.OutputStream

fun interface EncoderOutput {

    fun write(byte: Byte)

    fun write(bytes: ByteArray): Unit = bytes.forEach { write(it) }

    fun write(bytes: Iterable<Byte>): Unit = bytes.forEach { write(it) }

    class ByteArrayOutput : EncoderOutput {

        private val inner = ArrayList<Byte>()

        override fun write(byte: Byte) {
            inner.add(byte)
        }

        fun toByteArray(): ByteArray = inner.toByteArray()

        override fun toString(): String = inner.toString()

    }

    class OutputStreamOutput(private val output: OutputStream) : EncoderOutput {

        override fun write(byte: Byte) {
            output.write(byte.toInt())
        }

        override fun write(bytes: ByteArray) {
            output.write(bytes)
        }

    }

}
