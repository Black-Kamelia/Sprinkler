package com.kamelia.sprinkler.util

import java.nio.ByteOrder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ByteAccessTest {

    @Test
    fun `can read a bit from a byte`() {
        val byte = 0x11.toByte()
        val bit = byte.bit(0)
        assertEquals(0x1, bit)
    }

    @Test
    fun `can read a byte from a short`() {
        val short = 0x1234.toShort()
        val byte = short.byte(0, ByteOrder.BIG_ENDIAN)
        assertEquals(0x34.toByte(), byte)
    }

    @Test
    fun `can read a byte from a short in little-endian order`() {
        val short = 0x1234.toShort()
        val byte = short.byte(0, ByteOrder.LITTLE_ENDIAN)
        assertEquals(0x12.toByte(), byte)
    }

    @Test
    fun `can read a bit from a short`() {
        val short = 0x1234.toShort()
        val bit = short.bit(0)
        assertEquals(0x0, bit)
    }

    @Test
    fun `can read a bit from a short in little-endian order`() {
        val short = 0x0100.toShort()
        val bit = short.bit(0, ByteOrder.LITTLE_ENDIAN)
        assertEquals(0x1, bit)
    }

    @Test
    fun `can read a byte from an int`() {
        val int = 0x12345678
        val byte = int.byte(0)
        assertEquals(0x78.toByte(), byte)
    }

    @Test
    fun `can read a byte from an int in little-endian order`() {
        val int = 0x12345678
        val byte = int.byte(1, ByteOrder.LITTLE_ENDIAN)
        assertEquals(0x34.toByte(), byte)
    }

    @Test
    fun `can read a bit from an int`() {
        val int = 2
        val bit = int.bit(0)
        assertEquals(0x0, bit)
    }

    @Test
    fun `can read a bit from an int in little-endian order`() {
        val int = 0x00010000
        val bit = int.bit(8, ByteOrder.LITTLE_ENDIAN)
        assertEquals(0x1, bit)
    }

    @Test
    fun `can read a byte from a long`() {
        val long = 0x1234567890ABCDEF
        val byte = long.byte(0)
        assertEquals(0xEF.toByte(), byte)
    }

    @Test
    fun `can read a byte from a long in little-endian order`() {
        val long = 0x1234567890ABCDEF
        val byte = long.byte(1, ByteOrder.LITTLE_ENDIAN)
        assertEquals(0x34.toByte(), byte)
    }

    @Test
    fun `can read a bit from a long`() {
        val long = 0x1234567890ABCDEF
        val bit = long.bit(0)
        assertEquals(0x1, bit)
    }

    @Test
    fun `can read a bit from a long in little-endian order`() {
        val long = 0x0001000000000000
        val bit = long.bit(8, ByteOrder.LITTLE_ENDIAN)
        assertEquals(0x1, bit)
    }

    @Test
    fun `can read a byte from a float`() {
        val float = Float.fromBits(0x12345678)
        val byte = float.byte(0)
        assertEquals(0x78.toByte(), byte)
    }

    @Test
    fun `can read a byte from a float in little-endian order`() {
        val float = Float.fromBits(0x12345678)
        val byte = float.byte(1, ByteOrder.LITTLE_ENDIAN)
        assertEquals(0x34.toByte(), byte)
    }

    @Test
    fun `can read a bit from a float`() {
        val float = 0x12345678.toFloat()
        val bit = float.bit(0)
        assertEquals(0x0, bit)
    }

    @Test
    fun `can read a bit from a float in little-endian order`() {
        val float = Float.fromBits(0x00010000)
        val bit = float.bit(8, ByteOrder.LITTLE_ENDIAN)
        assertEquals(0x1, bit)
    }

    @Test
    fun `can read a byte from a double`() {
        val double = Double.fromBits(0x1234567890ABCDEF)
        val byte = double.byte(0)
        assertEquals(0xEF.toByte(), byte)
    }

    @Test
    fun `can read a byte from a double in little-endian order`() {
        val double = Double.fromBits(0x1234567890ABCDEF)
        val byte = double.byte(1, ByteOrder.LITTLE_ENDIAN)
        assertEquals(0x34.toByte(), byte)
    }

    @Test
    fun `can read a bit from a double`() {
        val double = Double.fromBits(0x1234567890ABCDEF)
        val bit = double.bit(0)
        assertEquals(0x1, bit)
    }

    @Test
    fun `can read a bit from a double in little-endian order`() {
        val double = Double.fromBits(0x0001000000000000)
        val bit = double.bit(8, ByteOrder.LITTLE_ENDIAN)
        assertEquals(0x1, bit)
    }

}
