package com.kamelia.sprinkler.util

import java.nio.ByteOrder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ByteArrayDecodingTest {

    @Test
    fun `readByte works correctly`() {
        val b1 = 0x01.toByte()
        val b2 = 0x02.toByte()
        val array = byteArrayOf(b1, b2)
        assertEquals(b1, array.readByte())
        assertEquals(b2, array.readByte(1))
    }

    @Test
    fun `readByte throws on invalid index`() {
        val array = byteArrayOf(0x01.toByte())
        assertThrows<IllegalArgumentException> { array.readByte(-1) }
        assertThrows<IllegalArgumentException> { array.readByte(1) }
    }

    @Test
    fun `readShort works correctly`() {
        val array = byteArrayOf(0x01.toByte(), 0x02.toByte(), 0x03.toByte())
        assertEquals(0x0102.toShort(), array.readShort())
        assertEquals(0x0201.toShort(), array.readShort(ByteOrder.LITTLE_ENDIAN))
        assertEquals(0x0203.toShort(), array.readShort(start = 1))
    }

    @Test
    fun `readShort throws on invalid index`() {
        val array = ByteArray(5)
        assertThrows<IllegalArgumentException> { array.readShort(start = -1) }
        assertThrows<IllegalArgumentException> { array.readShort(start = 10) }
    }

    @Test
    fun `readInt works correctly`() {
        val array = byteArrayOf(0x01.toByte(), 0x02.toByte(), 0x03.toByte(), 0x04.toByte(), 0x05.toByte())
        assertEquals(0x01020304, array.readInt())
        assertEquals(0x04030201, array.readInt(ByteOrder.LITTLE_ENDIAN))
        assertEquals(0x02030405, array.readInt(start = 1))
    }

    @Test
    fun `readInt throws on invalid index`() {
        val array = ByteArray(8)
        assertThrows<IllegalArgumentException> { array.readInt(start = -1) }
        assertThrows<IllegalArgumentException> { array.readInt(start = 10) }
    }

    @Test
    fun `readLong works correctly`() {
        val array = byteArrayOf(
            0x01.toByte(),
            0x02.toByte(),
            0x03.toByte(),
            0x04.toByte(),
            0x05.toByte(),
            0x06.toByte(),
            0x07.toByte(),
            0x08.toByte(),
            0x09.toByte()
        )
        assertEquals(0x0102030405060708L, array.readLong())
        assertEquals(0x0807060504030201L, array.readLong(ByteOrder.LITTLE_ENDIAN))
        assertEquals(0x0203040506070809L, array.readLong(start = 1))
    }

    @Test
    fun `readLong throws on invalid index`() {
        val array = ByteArray(8)
        assertThrows<IllegalArgumentException> { array.readLong(start = -1) }
        assertThrows<IllegalArgumentException> { array.readLong(start = 3) }
    }

    @Test
    fun `readFloat works correctly`() {
        val array = byteArrayOf(0x01.toByte(), 0x02.toByte(), 0x03.toByte(), 0x04.toByte(), 0x05.toByte())
        assertEquals(Float.fromBits(0x01020304), array.readFloat())
        assertEquals(Float.fromBits(0x04030201), array.readFloat(ByteOrder.LITTLE_ENDIAN))
        assertEquals(Float.fromBits(0x02030405), array.readFloat(start = 1))
    }

    @Test
    fun `readFloat throws on invalid index`() {
        val array = ByteArray(5)
        assertThrows<IllegalArgumentException> { array.readFloat(start = -1) }
        assertThrows<IllegalArgumentException> { array.readFloat(start = 2) }
    }

    @Test
    fun `readDouble works correctly`() {
        val array = byteArrayOf(
            0x01.toByte(),
            0x02.toByte(),
            0x03.toByte(),
            0x04.toByte(),
            0x05.toByte(),
            0x06.toByte(),
            0x07.toByte(),
            0x08.toByte(),
            0x09.toByte()
        )
        assertEquals(Double.fromBits(0x0102030405060708), array.readDouble())
        assertEquals(Double.fromBits(0x0807060504030201), array.readDouble(ByteOrder.LITTLE_ENDIAN))
        assertEquals(Double.fromBits(0x0203040506070809), array.readDouble(start = 1))
    }

    @Test
    fun `readDouble throws on invalid index`() {
        val array = ByteArray(8)
        assertThrows<IllegalArgumentException> { array.readDouble(start = -1) }
        assertThrows<IllegalArgumentException> { array.readDouble(start = 1) }
    }

    @Test
    fun `readString works correctly`() {
        val value = "Hello World!"
        val utf8 = value.toByteArray()
        val utf16 = value.toByteArray(Charsets.UTF_16)
        assertEquals(value, utf8.readString())
        assertEquals(value, (utf8 + utf16).readString(length = utf8.size))
        assertEquals(value, utf16.readString(Charsets.UTF_16))
    }

    @Test
    fun `readString throws on invalid index`() {
        val array = ByteArray(0)
        assertThrows<IllegalArgumentException> { array.readString(start = -1) }
        assertThrows<IllegalArgumentException> { array.readString(start = 1) }
    }

    @Test
    fun `readString throws on invalid length`() {
        val array = ByteArray(0)
        assertThrows<IllegalArgumentException> { array.readString(length = -1) }
        assertThrows<IllegalArgumentException> { array.readString(length = 5) }
    }

    @Test
    fun `readBoolean works correctly`() {
        val array = byteArrayOf(0x01.toByte(), 0x00.toByte(), 0x06.toByte(), 0x00.toByte())
        assertTrue(array.readBoolean())
        assertFalse(array.readBoolean(start = 1))
        assertTrue(array.readBoolean(start = 2))
        assertFalse(array.readBoolean(start = 3))
    }

    @Test
    fun `readBoolean throws on invalid index`() {
        val array = ByteArray(5)
        assertThrows<IllegalArgumentException> { array.readBoolean(start = -1) }
        assertThrows<IllegalArgumentException> { array.readBoolean(start = 10) }
    }

}
