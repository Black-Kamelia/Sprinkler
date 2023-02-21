package com.kamelia.sprinkler.binary.decoder

import com.kamelia.sprinkler.binary.decoder.util.assertDoneAndGet
import com.kamelia.sprinkler.util.byte
import java.nio.ByteOrder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DecodersTest {

    @Test
    fun `byte decoder works correctly`() {
        val decoder = ByteDecoder()
        val value = 7.toByte()
        val data = byteArrayOf(value)

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(value, result)
    }

    @Test
    fun `short decoder works correctly`() {
        val decoder = ShortDecoder()
        val value = 12.toShort()
        val data = byteArrayOf(value.byte(1), value.byte(0))

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(value, result)
    }

    @Test
    fun `little endian short decoder works correctly`() {
        val decoder = ShortDecoder(ByteOrder.LITTLE_ENDIAN)
        val value = 35.toShort()
        val data = byteArrayOf(value.byte(0), value.byte(1))

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(value, result)
    }

    @Test
    fun `int decoder works correctly`() {
        val decoder = IntDecoder()
        val value = 12
        val data = byteArrayOf(value.byte(3), value.byte(2), value.byte(1), value.byte(0))

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(value, result)
    }

    @Test
    fun `little endian int decoder works correctly`() {
        val decoder = IntDecoder(ByteOrder.LITTLE_ENDIAN)
        val value = 35
        val data = byteArrayOf(value.byte(0), value.byte(1), value.byte(2), value.byte(3))

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(value, result)
    }

    @Test
    fun `long decoder works correctly`() {
        val decoder = LongDecoder()
        val value = 12L
        val data =
            byteArrayOf(
                value.byte(7),
                value.byte(6),
                value.byte(5),
                value.byte(4),
                value.byte(3),
                value.byte(2),
                value.byte(1),
                value.byte(0)
            )

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(value, result)
    }

    @Test
    fun `little endian long decoder works correctly`() {
        val decoder = LongDecoder(ByteOrder.LITTLE_ENDIAN)
        val value = 35L
        val data =
            byteArrayOf(
                value.byte(0),
                value.byte(1),
                value.byte(2),
                value.byte(3),
                value.byte(4),
                value.byte(5),
                value.byte(6),
                value.byte(7)
            )

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(value, result)
    }

    @Test
    fun `float decoder works correctly`() {
        val decoder = FloatDecoder()
        val value = 23.19f
        val data = byteArrayOf(value.byte(3), value.byte(2), value.byte(1), value.byte(0))

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(value, result)
    }

    @Test
    fun `little endian float decoder works correctly`() {
        val decoder = FloatDecoder(ByteOrder.LITTLE_ENDIAN)
        val value = 93.12f
        val data = byteArrayOf(value.byte(0), value.byte(1), value.byte(2), value.byte(3))

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(value, result)
    }

    @Test
    fun `double decoder works correctly`() {
        val decoder = DoubleDecoder()
        val value = 12.213
        val data =
            byteArrayOf(
                value.byte(7),
                value.byte(6),
                value.byte(5),
                value.byte(4),
                value.byte(3),
                value.byte(2),
                value.byte(1),
                value.byte(0)
            )

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(value, result)
    }

    @Test
    fun `little endian double decoder works correctly`() {
        val decoder = DoubleDecoder(ByteOrder.LITTLE_ENDIAN)
        val value = 35.312
        val data =
            byteArrayOf(
                value.byte(0),
                value.byte(1),
                value.byte(2),
                value.byte(3),
                value.byte(4),
                value.byte(5),
                value.byte(6),
                value.byte(7)
            )

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(value, result)
    }

    @Test
    fun `size prefixed string decoder works correctly`() {
        val decoder = UTF8StringDecoder()

        val value = "Hello World"
        val bytes = value.toByteArray()
        val size = bytes.size
        val data = byteArrayOf(size.byte(3), size.byte(2), size.byte(1), size.byte(0)) + bytes

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(value, result)
    }

    @Test
    fun `size prefixed string decoder works correctly with different encoding`() {
        val charset = Charsets.UTF_16
        val decoder = StringDecoder(charset = charset)

        val value = "Hello World"
        val bytes = value.toByteArray(charset)
        val size = bytes.size
        val data = byteArrayOf(size.byte(3), size.byte(2), size.byte(1), size.byte(0)) + bytes

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(value, result)
    }

    @Test
    fun `end marker string decoder works correctly`() {
        val decoder = UTF8StringDecoderEM()

        val value = "Hello World"
        val bytes = value.toByteArray()
        val data = bytes + UTF8_NULL

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(value, result)
    }

    @Test
    fun `end marker string decoder works correctly with different encoding`() {
        val decoder = UTF16StringDecoderEM()

        val value = "Hello World"
        val bytes = value.toByteArray(Charsets.UTF_16)
        val data = bytes + UTF16_NULL

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(value, result)
    }

}
