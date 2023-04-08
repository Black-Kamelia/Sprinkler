package com.kamelia.sprinkler.codec.binary.encoder.core

import java.io.ByteArrayOutputStream
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class EncoderOutputTest {

    @Test
    fun `write byte works correctly with output stream`() {
        val stream = ByteArrayOutputStream()
        val output = EncoderOutput.from(stream)
        val bytes = byteArrayOf(4, 23, 87, -1)
        bytes.forEach(output::write)
        assertArrayEquals(bytes, stream.toByteArray())
    }

    @Test
    fun `write byte array works correctly with output stream`() {
        val stream = ByteArrayOutputStream()
        val output = EncoderOutput.from(stream)
        val bytes = byteArrayOf(4, 23, 87, -1)
        output.write(bytes)
        assertArrayEquals(bytes, stream.toByteArray())
    }

    @Test
    fun `write byte array with start and length indices works correctly with output stream`() {
        val stream = ByteArrayOutputStream()
        val output = EncoderOutput.from(stream)
        val bytes = byteArrayOf(4, 23, 87, -1)
        output.write(bytes, 1, 2)
        assertArrayEquals(byteArrayOf(23, 87), stream.toByteArray())
    }

    @Test
    fun `write byte array with start index works correctly with output stream`() {
        val stream = ByteArrayOutputStream()
        val output = EncoderOutput.from(stream)
        val bytes = byteArrayOf(4, 23, 87, -1)
        output.write(bytes, 1)
        assertArrayEquals(byteArrayOf(23, 87, -1), stream.toByteArray())
    }

    @Test
    fun `write byte iterable works correctly with output stream`() {
        val stream = ByteArrayOutputStream()
        val output = EncoderOutput.from(stream)
        val bytes = byteArrayOf(4, 23, 87, -1)
        output.write(bytes.asIterable())
        assertArrayEquals(bytes, stream.toByteArray())
    }

    @Test
    fun `write byte works correctly with lambda`() {
        val list = mutableListOf<Byte>()
        val output = EncoderOutput { list += it }
        val bytes = byteArrayOf(4, 23, 87, -1)
        bytes.forEach(output::write)
        assertArrayEquals(bytes, list.toByteArray())
    }

    @Test
    fun `write byte array works correctly with lambda`() {
        val list = mutableListOf<Byte>()
        val output = EncoderOutput { list += it }
        val bytes = byteArrayOf(4, 23, 87, -1)
        output.write(bytes)
        assertArrayEquals(bytes, list.toByteArray())
    }

    @Test
    fun `write byte array with start and length indices works correctly with lambda`() {
        val list = mutableListOf<Byte>()
        val output = EncoderOutput { list += it }
        val bytes = byteArrayOf(4, 23, 87, -1)
        output.write(bytes, 1, 2)
        assertArrayEquals(byteArrayOf(23, 87), list.toByteArray())
    }

    @Test
    fun `write byte array with start index works correctly with lambda`() {
        val list = mutableListOf<Byte>()
        val output = EncoderOutput { list += it }
        val bytes = byteArrayOf(4, 23, 87, -1)
        output.write(bytes, 1)
        assertArrayEquals(byteArrayOf(23, 87, -1), list.toByteArray())
    }

    @Test
    fun `write byte iterable works correctly with lambda`() {
        val list = mutableListOf<Byte>()
        val output = EncoderOutput { list += it }
        val bytes = byteArrayOf(4, 23, 87, -1)
        output.write(bytes.asIterable())
        assertArrayEquals(bytes, list.toByteArray())
    }

    @Test
    fun `write byte array throws exception when start index is negative`() {
        val output = EncoderOutput { }
        val bytes = byteArrayOf(4, 23, 87, -1)
        assertThrows<IndexOutOfBoundsException> { output.write(bytes, -1) }
    }

    @Test
    fun `write byte array throws exception when length is negative`() {
        val output = EncoderOutput { }
        val bytes = byteArrayOf(4, 23, 87, -1)
        assertThrows<IndexOutOfBoundsException> { output.write(bytes, 0, -1) }
    }

    @Test
    fun `write byte array throws exception when start index is greater than array size`() {
        val output = EncoderOutput { }
        val bytes = byteArrayOf(4, 23, 87, -1)
        assertThrows<IndexOutOfBoundsException> { output.write(bytes, bytes.size + 1) }
    }

    @Test
    fun `write byte array throws exception when start index plus length is greater than array size`() {
        val output = EncoderOutput { }
        val bytes = byteArrayOf(4, 23, 87, -1)
        assertThrows<IndexOutOfBoundsException> { output.write(bytes, 1, bytes.size) }
    }

}
