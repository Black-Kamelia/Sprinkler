package com.kamelia.sprinkler.codec.binary.encoder

import com.kamelia.sprinkler.codec.binary.common.ASCII_NULL
import com.kamelia.sprinkler.codec.binary.common.UTF16_NULL
import com.kamelia.sprinkler.codec.binary.common.UTF8_NULL
import com.kamelia.sprinkler.util.readDouble
import com.kamelia.sprinkler.util.readFloat
import com.kamelia.sprinkler.util.readInt
import com.kamelia.sprinkler.util.readLong
import com.kamelia.sprinkler.util.readShort
import java.nio.ByteOrder
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BaseEncodersTest {

    @Test
    fun `byte encoder works correctly`() {
        val encoder = ByteEncoder()
        val byte = 49.toByte()
        val bytes = encoder.encode(byte)
        assertEquals(Byte.SIZE_BYTES, bytes.size)
        assertEquals(byte, bytes[0])
    }

    @Test
    fun `short encoder works correctly`() {
        val encoder = ShortEncoder()
        val short = 49.toShort()
        val bytes = encoder.encode(short)
        assertEquals(Short.SIZE_BYTES, bytes.size)
        assertEquals(short, bytes.readShort())
    }

    @Test
    fun `little endian short encoder works correctly`() {
        val encoder = ShortEncoder(ByteOrder.LITTLE_ENDIAN)
        val short = 49.toShort()
        val bytes = encoder.encode(short)
        assertEquals(Short.SIZE_BYTES, bytes.size)
        assertEquals(short, bytes.readShort(ByteOrder.LITTLE_ENDIAN))
    }

    @Test
    fun `int encoder works correctly`() {
        val encoder = IntEncoder()
        val int = 49
        val bytes = encoder.encode(int)
        assertEquals(Int.SIZE_BYTES, bytes.size)
        assertEquals(int, bytes.readInt())
    }

    @Test
    fun `little endian int encoder works correctly`() {
        val encoder = IntEncoder(ByteOrder.LITTLE_ENDIAN)
        val int = 49
        val bytes = encoder.encode(int)
        assertEquals(Int.SIZE_BYTES, bytes.size)
        assertEquals(int, bytes.readInt(ByteOrder.LITTLE_ENDIAN))
    }

    @Test
    fun `long encoder works correctly`() {
        val encoder = LongEncoder()
        val long = 49L
        val bytes = encoder.encode(long)
        assertEquals(Long.SIZE_BYTES, bytes.size)
        assertEquals(long, bytes.readLong())
    }

    @Test
    fun `little endian long encoder works correctly`() {
        val encoder = LongEncoder(ByteOrder.LITTLE_ENDIAN)
        val long = 49L
        val bytes = encoder.encode(long)
        assertEquals(Long.SIZE_BYTES, bytes.size)
        assertEquals(long, bytes.readLong(ByteOrder.LITTLE_ENDIAN))
    }

    @Test
    fun `float encoder works correctly`() {
        val encoder = FloatEncoder()
        val float = 49.0f
        val bytes = encoder.encode(float)
        assertEquals(Float.SIZE_BYTES, bytes.size)
        assertEquals(float, bytes.readFloat())
    }

    @Test
    fun `little endian float encoder works correctly`() {
        val encoder = FloatEncoder(ByteOrder.LITTLE_ENDIAN)
        val float = 49.0f
        val bytes = encoder.encode(float)
        assertEquals(Float.SIZE_BYTES, bytes.size)
        assertEquals(float, bytes.readFloat(ByteOrder.LITTLE_ENDIAN))
    }

    @Test
    fun `double encoder works correctly`() {
        val encoder = DoubleEncoder()
        val double = 49.0
        val bytes = encoder.encode(double)
        assertEquals(Double.SIZE_BYTES, bytes.size)
        assertEquals(double, bytes.readDouble())
    }

    @Test
    fun `little endian double encoder works correctly`() {
        val encoder = DoubleEncoder(ByteOrder.LITTLE_ENDIAN)
        val double = 49.0
        val bytes = encoder.encode(double)
        assertEquals(Double.SIZE_BYTES, bytes.size)
        assertEquals(double, bytes.readDouble(ByteOrder.LITTLE_ENDIAN))
    }

    @Test
    fun `boolean encoder works correctly`() {
        val encoder = BooleanEncoder()
        val boolean = true
        val bytes = encoder.encode(boolean)
        assertEquals(1, bytes.size)
        assertEquals(boolean, bytes[0] != 0.toByte())
    }

    @Test
    fun `boolean encoder works with false`() {
        val encoder = BooleanEncoder()
        val boolean = false
        val bytes = encoder.encode(boolean)
        assertEquals(1, bytes.size)
        assertEquals(boolean, bytes[0] != 0.toByte())
    }

    @Test
    fun `size prefixed utf8 string encoder works correctly`() {
        val encoder = UTF8StringEncoder()
        val string = "Hello, world!"
        val bytes = encoder.encode(string)
        assertEquals(string.toByteArray().size, bytes.readInt())
        assertEquals(string, bytes.decodeToString(Int.SIZE_BYTES))
    }

    @Test
    fun `end marker utf8 string encoder works correctly`() {
        val encoder = UTF8StringEncoderEM()
        val string = "Hello, world!"
        val bytes = encoder.encode(string)
        assertEquals(string, bytes.decodeToString(endIndex = bytes.size - UTF8_NULL.size))
        assertArrayEquals(UTF8_NULL, bytes.sliceArray(bytes.size - UTF8_NULL.size until bytes.size))
    }

    @Test
    fun `end marker utf8 string encoder throws on empty end marker`() {
        assertThrows<IllegalArgumentException> {
            UTF8StringEncoderEM(byteArrayOf())
        }
    }

    @Test
    fun `size prefixed utf16 string encoder works correctly`() {
        val encoder = UTF16StringEncoder()
        val string = "Hello, world!"
        val bytes = encoder.encode(string)
        assertEquals(string.toByteArray(Charsets.UTF_16).size, bytes.readInt())
        assertEquals(
            string,
            String(bytes, Int.SIZE_BYTES, bytes.size - Int.SIZE_BYTES, Charsets.UTF_16)
        )
    }

    @Test
    fun `end marker utf16 string encoder works correctly`() {
        val encoder = UTF16StringEncoderEM()
        val string = "Hello, world!"
        val bytes = encoder.encode(string)
        assertEquals(
            string,
            String(bytes, 0, bytes.size - UTF16_NULL.size, Charsets.UTF_16)
        )
        assertArrayEquals(UTF8_NULL, bytes.sliceArray(bytes.size - UTF8_NULL.size until bytes.size))
    }

    @Test
    fun `end marker utf16 string encoder throws on too small end marker`() {
        assertThrows<IllegalArgumentException> {
            UTF16StringEncoderEM(ByteArray(1))
        }
    }

    @Test
    fun `size prefixed ascii string encoder works correctly`() {
        val encoder = ASCIIStringEncoder()
        val string = "Hello, world!"
        val bytes = encoder.encode(string)
        assertEquals(string.toByteArray(Charsets.US_ASCII).size, bytes.readInt())
        assertEquals(
            string,
            String(bytes, Int.SIZE_BYTES, bytes.size - Int.SIZE_BYTES, Charsets.US_ASCII)
        )
    }

    @Test
    fun `end marker ascii string encoder works correctly`() {
        val encoder = ASCIIStringEncoderEM()
        val string = "Hello, world!"
        val bytes = encoder.encode(string)
        assertEquals(
            string,
            String(bytes, 0, bytes.size - ASCII_NULL.size, Charsets.US_ASCII)
        )
        assertArrayEquals(ASCII_NULL, bytes.sliceArray(bytes.size - ASCII_NULL.size until bytes.size))
    }

    @Test
    fun `end marker ascii string encoder throws on empty end marker`() {
        assertThrows<IllegalArgumentException> {
            ASCIIStringEncoderEM(byteArrayOf())
        }
    }

    @Test
    fun `size prefixed string encoder works correctly`() {
        val charset = Charsets.UTF_32BE
        val encoder = StringEncoder(charset)
        val string = "Hello, world!"
        val bytes = encoder.encode(string)
        assertEquals(string.toByteArray(charset).size, bytes.readInt())
        assertEquals(
            string,
            String(bytes, Int.SIZE_BYTES, bytes.size - Int.SIZE_BYTES, charset)
        )
    }

    @Test
    fun `enum encoder works correctly`() {
        val encoder = EnumEncoder<AnnotationRetention>()
        val label = AnnotationRetention.SOURCE
        val bytes = encoder.encode(label)
        assertEquals(Int.SIZE_BYTES, bytes.size)
        assertEquals(label, AnnotationRetention.values()[bytes[0].toInt()])
    }

    @Test
    fun `enum encoder string works correctly`() {
        val encoder = EnumEncoderString<AnnotationRetention>()
        val label = AnnotationRetention.SOURCE
        val bytes = encoder.encode(label)
        assertEquals(label.name.toByteArray().size, bytes.readInt())
        assertEquals(
            label,
            AnnotationRetention.valueOf(bytes.decodeToString(Int.SIZE_BYTES))
        )
    }

    @Test
    fun `no op encoder works correctly`() {
        val encoder = NoOpEncoder<Int>()
        val bytes = encoder.encode(49)
        assertEquals(0, bytes.size)
    }

}
