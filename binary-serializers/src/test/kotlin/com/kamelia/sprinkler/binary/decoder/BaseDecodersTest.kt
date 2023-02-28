package com.kamelia.sprinkler.binary.decoder

import com.kamelia.sprinkler.binary.decoder.core.Decoder
import com.kamelia.sprinkler.binary.decoder.util.assertDoneAndGet
import com.kamelia.sprinkler.util.byte
import java.nio.ByteOrder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BaseDecodersTest {

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
        val data = byteArrayOf(
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
        val data = byteArrayOf(
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
        val data = byteArrayOf(
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
        val data = byteArrayOf(
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

    @Test
    fun `boolean decoder works correctly`() {
        val decoder = BooleanDecoder()
        val value = true
        val data = byteArrayOf(if (value) 1 else 0)

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(value, result)
    }

    @Test
    fun `utf8 string decoder throws on invalid end marker`() {
        assertThrows<IllegalArgumentException> {
            UTF8StringDecoderEM(byteArrayOf())
        }
    }

    @Test
    fun `utf16 string decoder throws on invalid end marker`() {
        assertThrows<IllegalArgumentException> {
            UTF16StringDecoderEM(byteArrayOf())
        }
        assertThrows<IllegalArgumentException> {
            UTF16StringDecoderEM(byteArrayOf(5))
        }
    }

    @Test
    fun `utf16 string decoder prefixed works correctly`() {
        val decoder = UTF16StringDecoder()

        val value = "Hello World"
        val bytes = value.toByteArray(Charsets.UTF_16)
        val size = bytes.size
        val data = byteArrayOf(size.byte(3), size.byte(2), size.byte(1), size.byte(0)) + bytes

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(value, result)
    }

    @Test
    fun `ascii string decoder em works correctly`() {
        val decoder = ASCIIStringDecoderEM()

        val value = "Hello World"
        val bytes = value.toByteArray(Charsets.US_ASCII)
        val data = bytes + ASCII_NULL

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(value, result)
    }

    @Test
    fun `ascii string decoder throws on invalid endmarker`() {
        assertThrows<IllegalArgumentException> {
            ASCIIStringDecoderEM(byteArrayOf())
        }
    }

    @Test
    fun `ascii string decoder prefixed works correctly`() {
        val decoder = ASCIIStringDecoder()

        val value = "Hello World"
        val bytes = value.toByteArray(Charsets.US_ASCII)
        val size = bytes.size
        val data = byteArrayOf(size.byte(3), size.byte(2), size.byte(1), size.byte(0)) + bytes

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(value, result)
    }

    @Test
    fun `enum decoder works correctly`() {
        val decoder = EnumDecoder(DummyEnum::class.java)

        val value = DummyEnum.B
        val ord = value.ordinal
        val data = byteArrayOf(ord.byte(3), ord.byte(2), ord.byte(1), ord.byte(0))

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(value, result)
    }

    @Test
    fun `enum decoder returns error on too big ordinal`() {
        val decoder = EnumDecoder(DummyEnum::class.java)

        val data = byteArrayOf(0, 0, 0, 5)

        val result = decoder.decode(data)
        assertInstanceOf(Decoder.State.Error::class.java, result)
    }

    @Test
    fun `enum decoder returns error on too small ordinal`() {
        val decoder = EnumDecoder(DummyEnum::class.java)

        val index = -1
        val data = byteArrayOf(index.byte(3), index.byte(2), index.byte(1), index.byte(0))

        val result = decoder.decode(data)
        assertInstanceOf(Decoder.State.Error::class.java, result)
    }

    @Test
    fun `enum decoder string works correctly`() {
        val decoder = EnumDecoderString(DummyEnum::class.java)

        val value = DummyEnum.B
        val array = value.name.toByteArray()
        val size = array.size
        val data = byteArrayOf(
            size.byte(3),
            size.byte(2),
            size.byte(1),
            size.byte(0)
        ) + array

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(value, result)
    }

    @Test
    fun `enum decoder string returns error on invalid name`() {
        val decoder = EnumDecoderString(DummyEnum::class.java)

        val array = "D".toByteArray()
        val size = array.size
        val data = byteArrayOf(size.byte(3), size.byte(2), size.byte(1), size.byte(0)) + array

        val result = decoder.decode(data)
        assertInstanceOf(Decoder.State.Error::class.java, result)
    }

    @Test
    fun `constant decoder works correctly`() {
        val value = "Hello World"
        val decoder = ConstantDecoder(value)

        val data = byteArrayOf(1).inputStream()
        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(value, result)
    }

    @Test
    fun `constant decoder doesnt reads the input`() {
        val decoder = ConstantDecoder(4)
        val inputValue = 4.toByte()
        val data = byteArrayOf(inputValue).inputStream()

        decoder.decode(data).assertDoneAndGet()
        assertEquals(inputValue, data.read().toByte())
    }

    @Test
    fun `constant decoder with factory works correctly`() {
        var value = 0
        val decoder = ConstantDecoder { value }

        val data = byteArrayOf(0).inputStream()

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(value, result)

        value = 167

        val result2 = decoder.decode(data).assertDoneAndGet()
        assertEquals(value, result2)
    }

    @Test
    fun `constant decoder with factory doesnt reads the input`() {
        val decoder = ConstantDecoder { 4 }
        val inputValue = 4.toByte()
        val data = byteArrayOf(inputValue).inputStream()

        decoder.decode(data).assertDoneAndGet()
        assertEquals(inputValue, data.read().toByte())
    }

    @Test
    fun `no op decoder works correctly`() {
        val decoder = NoOpDecoder()

        val data = byteArrayOf(1).inputStream()
        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(Unit, result)
    }

    @Test
    fun `no op decoder doesnt reads the input`() {
        val decoder = NoOpDecoder()
        val inputValue = 4.toByte()
        val data = byteArrayOf(inputValue).inputStream()

        decoder.decode(data).assertDoneAndGet()
        assertEquals(inputValue, data.read().toByte())
    }

    @Test
    fun `null decoder works correctly`() {
        val decoder = NullDecoder<Any>()

        val data = byteArrayOf(1).inputStream()
        val result = decoder.decode(data).assertDoneAndGet()
        assertNull(result)
    }

    @Test
    fun `null decoder doesnt reads the input`() {
        val decoder = NullDecoder<Any>()
        val inputValue = 4.toByte()
        val data = byteArrayOf(inputValue).inputStream()

        decoder.decode(data).assertDoneAndGet()
        assertEquals(inputValue, data.read().toByte())
    }

}

private enum class DummyEnum {
    A, B, C
}
