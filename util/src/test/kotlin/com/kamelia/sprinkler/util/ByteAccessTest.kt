package com.kamelia.sprinkler.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ByteAccessTest {

    @Test
    fun `can read a bit from a byte`() {
        val byte = 0x11.toByte()
        val bit = byte.bit(0)
        assertEquals(0x1.toByte(), bit)
    }

    @Test
    fun `reading a bit from a byte with an invalid index throws an exception`() {
        val byte = 0x11.toByte()
        assertThrows<IllegalArgumentException> { byte.bit(8) }
        assertThrows<IllegalArgumentException> { byte.bit(-8) }
    }

    @Test
    fun `can read a byte from a short`() {
        val short = 0x1234.toShort()
        val byte = short.byte(0)
        assertEquals(0x34.toByte(), byte)
    }

    @Test
    fun `reading a byte from a short with an invalid index throws an exception`() {
        val short = 0x1234.toShort()
        assertThrows<IllegalArgumentException> { short.byte(2) }
        assertThrows<IllegalArgumentException> { short.byte(-2) }
    }

    @Test
    fun `can read a bit from a short`() {
        val short = 0x1234.toShort()
        val bit = short.bit(0)
        assertEquals(0x0.toByte(), bit)
    }

    @Test
    fun `reading a bit from a short with an invalid index throws an exception`() {
        val short = 0x1234.toShort()
        assertThrows<IllegalArgumentException> { short.bit(16) }
        assertThrows<IllegalArgumentException> { short.bit(-16) }
    }

    @Test
    fun `can read a byte from an int`() {
        val int = 0x12345678
        val byte = int.byte(0)
        assertEquals(0x78.toByte(), byte)
    }

    @Test
    fun `reading a byte from an int with an invalid index throws an exception`() {
        val int = 0x12345678
        assertThrows<IllegalArgumentException> { int.byte(4) }
        assertThrows<IllegalArgumentException> { int.byte(-4) }
    }

    @Test
    fun `can read a bit from an int`() {
        val int = 2
        val bit = int.bit(0)
        assertEquals(0x0.toByte(), bit)
    }

    @Test
    fun `reading a bit from an int with an invalid index throws an exception`() {
        val int = 1
        assertThrows<IllegalArgumentException> { int.bit(32) }
        assertThrows<IllegalArgumentException> { int.bit(-32) }
    }

    @Test
    fun `can read a byte from a long`() {
        val long = 0x1234567890ABCDEF
        val byte = long.byte(0)
        assertEquals(0xEF.toByte(), byte)
    }

    @Test
    fun `reading a byte from a long with an invalid index throws an exception`() {
        val long = 0x1234567890ABCDEF
        assertThrows<IllegalArgumentException> { long.byte(8) }
        assertThrows<IllegalArgumentException> { long.byte(-8) }
    }

    @Test
    fun `can read a bit from a long`() {
        val long = 0x1234567890ABCDEF
        val bit = long.bit(0)
        assertEquals(0x1.toByte(), bit)
    }

    @Test
    fun `reading a bit from a long with an invalid index throws an exception`() {
        val long = 0x1234567890ABCDEF
        assertThrows<IllegalArgumentException> { long.bit(64) }
        assertThrows<IllegalArgumentException> { long.bit(-64) }
    }

    @Test
    fun `can read a byte from a float`() {
        val float = Float.fromBits(0x12345678)
        val byte = float.byte(0)
        assertEquals(0x78.toByte(), byte)
    }

    @Test
    fun `reading a byte from a float with an invalid index throws an exception`() {
        val float = 0x12345678.toFloat()
        assertThrows<IllegalArgumentException> { float.byte(4) }
        assertThrows<IllegalArgumentException> { float.byte(-4) }
    }

    @Test
    fun `can read a bit from a float`() {
        val float = 0x12345678.toFloat()
        val bit = float.bit(0)
        assertEquals(0x0.toByte(), bit)
    }

    @Test
    fun `reading a bit from a float with an invalid index throws an exception`() {
        val float = 0x12345678.toFloat()
        assertThrows<IllegalArgumentException> { float.bit(32) }
        assertThrows<IllegalArgumentException> { float.bit(-32) }
    }

    @Test
    fun `can read a byte from a double`() {
        val double = Double.fromBits(0x1234567890ABCDEF)
        val byte = double.byte(0)
        assertEquals(0xEF.toByte(), byte)
    }

    @Test
    fun `reading a byte from a double with an invalid index throws an exception`() {
        val double = 0x1234567890ABCDEF.toDouble()
        assertThrows<IllegalArgumentException> { double.byte(8) }
        assertThrows<IllegalArgumentException> { double.byte(-8) }
    }

    @Test
    fun `can read a bit from a double`() {
        val double = Double.fromBits(0x1234567890ABCDEF)
        val bit = double.bit(0)
        assertEquals(0x1.toByte(), bit)
    }

    @Test
    fun `reading a bit from a double with an invalid index throws an exception`() {
        val double = 0x1234567890ABCDEF.toDouble()
        assertThrows<IllegalArgumentException> { double.bit(64) }
        assertThrows<IllegalArgumentException> { double.bit(-64) }
    }

}
