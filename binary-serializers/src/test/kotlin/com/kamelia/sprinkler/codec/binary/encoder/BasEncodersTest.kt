package com.kamelia.sprinkler.codec.binary.encoder

import com.kamelia.sprinkler.util.byte
import java.nio.ByteOrder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BasEncodersTest {

    @Test
    fun `byte encoder works correctly`() {
        val encoder = ByteEncoder()
        val byte = 19.toByte()
        val bytes = encoder.encode(byte)

        assertEquals(Byte.SIZE_BYTES, bytes.size)
        assertEquals(byte, bytes[0])
    }

    @Test
    fun `short encoder works correctly`() {
        val encoder = ShortEncoder()
        val short = 7.toShort()
        val bytes = encoder.encode(short)

        assertEquals(Short.SIZE_BYTES, bytes.size)
        for (i in 0 until Short.SIZE_BYTES) {
            assertEquals(short.byte(Short.SIZE_BYTES - i - 1), bytes[i])
        }
    }

    @Test
    fun `short encoder works correctly with little endian`() {
        val encoder = ShortEncoder(ByteOrder.LITTLE_ENDIAN)
        val short = 7.toShort()
        val bytes = encoder.encode(short)

        assertEquals(Short.SIZE_BYTES, bytes.size)
        for (i in 0 until Short.SIZE_BYTES) {
            assertEquals(short.byte(i), bytes[i])
        }
    }

    @Test
    fun `int encoder works correctly`() {
        val encoder = IntEncoder()
        val int = 7
        val bytes = encoder.encode(int)

        assertEquals(Int.SIZE_BYTES, bytes.size)
        for (i in 0 until Int.SIZE_BYTES) {
            assertEquals(int.byte(Int.SIZE_BYTES - i - 1), bytes[i])
        }
    }

    @Test
    fun `int encoder works correctly with little endian`() {
        val encoder = IntEncoder(ByteOrder.LITTLE_ENDIAN)
        val int = 7
        val bytes = encoder.encode(int)

        assertEquals(Int.SIZE_BYTES, bytes.size)
        for (i in 0 until Int.SIZE_BYTES) {
            assertEquals(int.byte(i), bytes[i])
        }
    }

    @Test
    fun `long encoder works correctly`() {
        val encoder = LongEncoder()
        val long = 7L
        val bytes = encoder.encode(long)

        assertEquals(Long.SIZE_BYTES, bytes.size)
        for (i in 0 until Long.SIZE_BYTES) {
            assertEquals(long.byte(Long.SIZE_BYTES - i - 1), bytes[i])
        }
    }

    @Test
    fun `long encoder works correctly with little endian`() {
        val encoder = LongEncoder(ByteOrder.LITTLE_ENDIAN)
        val long = 7L
        val bytes = encoder.encode(long)

        assertEquals(Long.SIZE_BYTES, bytes.size)
        for (i in 0 until Long.SIZE_BYTES) {
            assertEquals(long.byte(i), bytes[i])
        }
    }

    @Test
    fun `float encoder works correctly`() {
        val encoder = FloatEncoder()
        val float = -36.05f
        val bytes = encoder.encode(float)

        assertEquals(Float.SIZE_BYTES, bytes.size)
        for (i in 0 until Float.SIZE_BYTES) {
            assertEquals(float.byte(Float.SIZE_BYTES - i - 1), bytes[i])
        }
    }

    @Test
    fun `float encoder works correctly with little endian`() {
        val encoder = FloatEncoder(ByteOrder.LITTLE_ENDIAN)
        val float = -36.05f
        val bytes = encoder.encode(float)

        assertEquals(Float.SIZE_BYTES, bytes.size)
        for (i in 0 until Float.SIZE_BYTES) {
            println(i)
            assertEquals(float.byte(i), bytes[i])
        }
    }

    @Test
    fun `double encoder works correctly`() {
        val encoder = DoubleEncoder()
        val double = -36.2368
        val bytes = encoder.encode(double)

        assertEquals(Double.SIZE_BYTES, bytes.size)
        for (i in 0 until Double.SIZE_BYTES) {
            assertEquals(double.byte(Double.SIZE_BYTES - i - 1), bytes[i])
        }
    }

    @Test
    fun `double encoder works correctly with little endian`() {
        val encoder = DoubleEncoder(ByteOrder.LITTLE_ENDIAN)
        val double = -36.2368
        val bytes = encoder.encode(double)

        assertEquals(Double.SIZE_BYTES, bytes.size)
        for (i in 0 until Double.SIZE_BYTES) {
            assertEquals(double.byte(i), bytes[i])
        }
    }

}
