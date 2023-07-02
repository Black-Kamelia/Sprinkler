package com.kamelia.sprinkler.transcoder.binary.encoder.core

import java.io.ByteArrayOutputStream
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
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
        val output = EncoderOutput.from { list += it.toByte() }
        val bytes = byteArrayOf(4, 23, 87, -1)
        bytes.forEach(output::write)
        assertArrayEquals(bytes, list.toByteArray())
    }

    @Test
    fun `write byte array works correctly with lambda`() {
        val list = mutableListOf<Byte>()
        val output = EncoderOutput.from { list += it.toByte() }
        val bytes = byteArrayOf(4, 23, 87, -1)
        output.write(bytes)
        assertArrayEquals(bytes, list.toByteArray())
    }

    @Test
    fun `write byte array with start and length indices works correctly with lambda`() {
        val list = mutableListOf<Byte>()
        val output = EncoderOutput.from { list += it.toByte() }
        val bytes = byteArrayOf(4, 23, 87, -1)
        output.write(bytes, 1, 2)
        assertArrayEquals(byteArrayOf(23, 87), list.toByteArray())
    }

    @Test
    fun `write byte array with start index works correctly with lambda`() {
        val list = mutableListOf<Byte>()
        val output = EncoderOutput.from { list += it.toByte() }
        val bytes = byteArrayOf(4, 23, 87, -1)
        output.write(bytes, 1)
        assertArrayEquals(byteArrayOf(23, 87, -1), list.toByteArray())
    }

    @Test
    fun `write byte iterable works correctly with lambda`() {
        val list = mutableListOf<Byte>()
        val output = EncoderOutput.from { list += it.toByte() }
        val bytes = byteArrayOf(4, 23, 87, -1)
        output.write(bytes.asIterable())
        assertArrayEquals(bytes, list.toByteArray())
    }

    @Test
    fun `write byte array throws exception when start index is negative`() {
        val output = EncoderOutput.nullOutput()
        val bytes = byteArrayOf(4, 23, 87, -1)
        assertThrows<IndexOutOfBoundsException> { output.write(bytes, -1) }
    }

    @Test
    fun `write byte array throws exception when length is negative`() {
        val output = EncoderOutput.nullOutput()
        val bytes = byteArrayOf(4, 23, 87, -1)
        assertThrows<IndexOutOfBoundsException> { output.write(bytes, 0, -1) }
    }

    @Test
    fun `write byte array throws exception when start index is greater than array size`() {
        val output = EncoderOutput.nullOutput()
        val bytes = byteArrayOf(4, 23, 87, -1)
        assertThrows<IndexOutOfBoundsException> { output.write(bytes, bytes.size + 1) }
    }

    @Test
    fun `write byte array throws exception when start index plus length is greater than array size`() {
        val output = EncoderOutput.nullOutput()
        val bytes = byteArrayOf(4, 23, 87, -1)
        assertThrows<IndexOutOfBoundsException> { output.write(bytes, 1, bytes.size) }
    }

    @Test
    fun `write bit works correctly with int`() {
        val list = mutableListOf<Byte>()
        val output = EncoderOutput.from { list += it.toByte() }
        output.writeBit(1)
        output.flush()
        assertEquals(mutableListOf(0b1000_0000.toByte()), list)
    }

    @Test
    fun `write bit ignore other bits with int`() {
        val list = mutableListOf<Byte>()
        val output = EncoderOutput.from { list += it.toByte() }
        output.writeBit(0b0000_0010)
        output.flush()
        assertEquals(mutableListOf(0.toByte()), list)
    }

    @Test
    fun `partial bytes aren't written without calling flush`() {
        val list = mutableListOf<Byte>()
        val output = EncoderOutput.from { list += it.toByte() }
        output.writeBit(1)
        assertEquals(mutableListOf<Byte>(), list)
    }

    @Test
    fun `bytes are written once they are full`() {
        val list = mutableListOf<Byte>()
        val output = EncoderOutput.from { list += it.toByte() }
        repeat(7) {
            output.writeBit(0)
        }
        output.writeBit(1)
        output.flush()
        assertEquals(mutableListOf(1.toByte()), list)
    }

    @Test
    fun `write bit with boolean works correctly`() {
        val list = mutableListOf<Byte>()
        val output = EncoderOutput.from { list += it.toByte() }
        output.writeBit(false)
        output.writeBit(true)
        output.flush()
        assertEquals(mutableListOf(0b0100_0000.toByte()), list)
    }

    @Test
    fun `write bit with byte works correctly`() {
        val list = mutableListOf<Byte>()
        val output = EncoderOutput.from { list += it.toByte() }
        output.writeBit(0b0000_0001.toByte())
        output.flush()
        assertEquals(mutableListOf(0b1000_0000.toByte()), list)
    }

    @Test
    fun `write bit with byte ignores other bits`() {
        val list = mutableListOf<Byte>()
        val output = EncoderOutput.from { list += it.toByte() }
        output.writeBit(0b0000_0010.toByte())
        output.flush()
        assertEquals(mutableListOf(0.toByte()), list)
    }

    @Test
    fun `null output works correctly`() {
        val output = EncoderOutput.nullOutput()
        output.write(1)
        output.writeBit(1)
        output.writeBit(false)
        output.flush()
        assertDoesNotThrow { output.write(byteArrayOf(1, 2, 3)) }
        assertDoesNotThrow { output.writeBits(byteArrayOf(1, 2, 3), 0, 4) }
        assertDoesNotThrow { output.writeBits(3, 4) }
        assertThrows<IndexOutOfBoundsException> { output.write(byteArrayOf(1, 2, 3), 1, 3) }
        assertThrows<IndexOutOfBoundsException> { output.write(byteArrayOf(1, 2, 3), 4) }
        assertThrows<IndexOutOfBoundsException> { output.write(byteArrayOf(1, 2, 3), -1) }
        assertThrows<IndexOutOfBoundsException> { output.write(byteArrayOf(1, 2, 3), 0, 4) }
        assertThrows<IndexOutOfBoundsException> { output.write(byteArrayOf(1, 2, 3), 1, -1) }
        assertThrows<IndexOutOfBoundsException> { output.writeBits(byteArrayOf(1), 9, 0) }
        assertThrows<IndexOutOfBoundsException> { output.writeBits(byteArrayOf(1), 0, 9) }
        assertThrows<IndexOutOfBoundsException> { output.writeBits(byteArrayOf(1), 0, -1) }
        assertThrows<IndexOutOfBoundsException> { output.writeBits(byteArrayOf(1), -1, 1) }
        assertThrows<IndexOutOfBoundsException> { output.writeBits(byteArrayOf(1), 3, 6) }
    }

    @Test
    fun `default write bytes works correctly`() {
        val list = mutableListOf<Byte>()
        var buffer = 0
        var bufferBits = 0
        val output = object : EncoderOutput {
            override fun writeBit(bit: Int) {
                buffer = buffer or (bit shl (7 - bufferBits))
                bufferBits++
                if (bufferBits == 8) {
                    flush()
                }
            }

            override fun flush() {
                list += buffer.toByte()
                buffer = 0
                bufferBits = 0
            }
        }

        output.write(byteArrayOf(5, 6), 0, 1)
        assertEquals(mutableListOf(5.toByte()), list)
    }

    @Test
    fun `write bits from byte works correctly`() {
        val list = mutableListOf<Byte>()
        val output = EncoderOutput.from { list += it.toByte() }
        output.writeBits(0b1011_1111.toByte(), 3)
        output.flush()
        assertEquals(mutableListOf(0b1010_0000.toByte()), list)
    }

    @Test
    fun `write bits from byte with start works correctly`() {
        val list = mutableListOf<Byte>()
        val output = EncoderOutput.from { list += it.toByte() }
        output.writeBits(0b1011_1111.toByte(), 1, 3)
        output.flush()
        assertEquals(mutableListOf(0b0110_0000.toByte()), list)
    }

    @Test
    fun `write bits from byte array throws on invalid parameters`() {
        val output = EncoderOutput.from { }
        assertThrows<IndexOutOfBoundsException> { output.writeBits(byteArrayOf(1), 9) }
        assertThrows<IndexOutOfBoundsException> { output.writeBits(byteArrayOf(1), 9, 0) }
        assertThrows<IndexOutOfBoundsException> { output.writeBits(byteArrayOf(1), 0, -1) }
    }

    @Test
    fun `write works correctly after write bit`() {
        val list = mutableListOf<Byte>()
        val output = EncoderOutput.from { list += it.toByte() }
        output.writeBit(1)
        output.write(1)
        output.flush()
        assertEquals(mutableListOf(0b1000_0000.toByte(), 0b1000_0000.toByte()), list)
    }

    // NOTE FOR THE WRITE BITS TESTS
    // PP = Prefix part writing is called when start % 8 != 0 <=> writing doesn't start on a byte boundary
    // FB = Full bytes writing is called when length - start / 8 != 0 <=> there is at least one full byte to write
    // SP Suffix part writing is called when (length - PP - 8 * FB) % 8 != 0 <=> writing doesn't end on a byte boundary

    @Test
    fun `write bits from byte array works for FB only and start == 0`() {
        val list = mutableListOf<Byte>()
        val output = EncoderOutput.from { list += it.toByte() }
        output.writeBits(byteArrayOf(7, 1), 8)
        assertEquals(mutableListOf(7.toByte()), list)
    }

    @Test
    fun `write bits from byte array works for FB only and start 8 != 0`() {
        val list = mutableListOf<Byte>()
        val output = EncoderOutput.from { list += it.toByte() }
        output.writeBits(byteArrayOf(5, 1, 3, 5), 16, 8)
        assertEquals(mutableListOf(3.toByte()), list)
    }

    @Test
    fun `write bits from byte array works for PP only and start lt 7`() {
        val list = mutableListOf<Byte>()
        val output = EncoderOutput.from { list += it.toByte() }
        output.writeBits(byteArrayOf(0b1011_1111.toByte(), 7), 1, 5)
        output.flush()
        assertEquals(mutableListOf(0b0111_1000.toByte()), list)
    }

    @Test
    fun `write bits from byte array works for PP only and start gt 7`() {
        val list = mutableListOf<Byte>()
        val output = EncoderOutput.from { list += it.toByte() }
        output.writeBits(byteArrayOf(0b1011_1111.toByte(), 0b1110_0011.toByte()), 9, 5)
        output.flush()
        assertEquals(mutableListOf(0b1100_0000.toByte()), list)
    }

    @Test
    fun `write bits from byte array works for SP only and start lt 7`() {
        val list = mutableListOf<Byte>()
        val output = EncoderOutput.from { list += it.toByte() }
        output.writeBits(byteArrayOf(0b1011_1111.toByte(), 8), 0, 5)
        output.flush()
        assertEquals(mutableListOf(0b1011_1000.toByte()), list)
    }

    @Test
    fun `write bits from byte array works for SP only and start gt 7`() {
        val list = mutableListOf<Byte>()
        val output = EncoderOutput.from { list += it.toByte() }
        output.writeBits(byteArrayOf(-1, 0b1110_0110.toByte()), 8, 6)
        output.flush()
        assertEquals(mutableListOf(0b1110_0100.toByte()), list)
    }

    @Test
    fun `write bits from byte array works for PP and SP and start lt 8`() {
        val list = mutableListOf<Byte>()
        val output = EncoderOutput.from { list += it.toByte() }
        output.writeBits(byteArrayOf(0b1100_1001.toByte(), 0b1111_1111.toByte(), 3), 1, 10)
        output.flush()
        assertEquals(mutableListOf(0b1001_0011.toByte(), 0b1100_0000.toByte()), list)
    }

    @Test
    fun `write bits from byte array works for PP and SP and start gt 8`() {
        val list = mutableListOf<Byte>()
        val output = EncoderOutput.from { list += it.toByte() }
        output.writeBits(byteArrayOf(1, 0b1100_1001.toByte(), 0b1111_1111.toByte(), 5), 9, 10)
        output.flush()
        assertEquals(mutableListOf(0b1001_0011.toByte(), 0b1100_0000.toByte()), list)
    }

    @Test
    fun `write bits from byte array works for PP and FB and start lt 8`() {
        val list = mutableListOf<Byte>()
        val output = EncoderOutput.from { list += it.toByte() }
        output.writeBits(byteArrayOf(1, 3, 5), 7, 9)
        output.flush()
        assertEquals(mutableListOf(0b1000_0001.toByte(), 0b1000_0000.toByte()), list)
    }

    @Test
    fun `write bits from byte array works for PP and FB and start gt 8`() {
        val list = mutableListOf<Byte>()
        val output = EncoderOutput.from { list += it.toByte() }
        output.writeBits(byteArrayOf(0xFF.toByte(), 1, 3, 5), 15, 9)
        output.flush()
        assertEquals(mutableListOf(0b1000_0001.toByte(), 0b1000_0000.toByte()), list)
    }

    @Test
    fun `write bits from byte array works for FB SP and start == 0`() {
        val list = mutableListOf<Byte>()
        val output = EncoderOutput.from { list += it.toByte() }
        output.writeBits(byteArrayOf(0xa9.toByte(), 0b1101_1101.toByte()), 13)
        output.flush()
        assertEquals(mutableListOf(0xa9.toByte(), 0b1101_1000.toByte()), list)
    }

    @Test
    fun `write bits from byte array works for FB SP and start gt 0`() {
        val list = mutableListOf<Byte>()
        val output = EncoderOutput.from { list += it.toByte() }
        output.writeBits(byteArrayOf(0xFF.toByte(), 0xa9.toByte(), 0b1101_1101.toByte()), 8, 13)
        output.flush()
        assertEquals(mutableListOf(0xa9.toByte(), 0b1101_1000.toByte()), list)
    }

    @Test
    fun `write bits from byte array works for PP FB SP and start lt 8`() {
        val list = mutableListOf<Byte>()
        val output = EncoderOutput.from { list += it.toByte() }
        output.writeBits(byteArrayOf(0b0001_0110.toByte(), 0xFF.toByte(), 0b0011_1111.toByte()), 3, 17)
        output.flush()
        assertEquals(mutableListOf(0b1011_0111.toByte(), 0b1111_1001.toByte(), 0b1000_0000.toByte()), list)
    }

    @Test
    fun `write bits from byte array works for PP FB SP and start gt 8`() {
        val list = mutableListOf<Byte>()
        val output = EncoderOutput.from { list += it.toByte() }
        output.writeBits(
            byteArrayOf(0xFF.toByte(), 0b0001_0110.toByte(), 0xFF.toByte(), 0b0011_1111.toByte()),
            11,
            17
        )
        output.flush()
        assertEquals(mutableListOf(0b1011_0111.toByte(), 0b1111_1001.toByte(), 0b1000_0000.toByte()), list)
    }

}
