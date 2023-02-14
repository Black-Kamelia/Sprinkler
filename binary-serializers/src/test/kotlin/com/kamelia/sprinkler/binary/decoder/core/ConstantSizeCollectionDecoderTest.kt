package com.kamelia.sprinkler.binary.decoder.core

import com.kamelia.sprinkler.binary.decoder.ASCIIStringDecoder
import com.kamelia.sprinkler.binary.decoder.ByteDecoder
import com.kamelia.sprinkler.binary.decoder.util.assertDoneAndGet
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ConstantSizeCollectionDecoderTest {

    @Test
    fun `works correctly`() {
        val decoder = ConstantSizeCollectionDecoder(DecoderCollector.toList(), ByteDecoder(), 3)

        val b1 = 5.toByte()
        val b2 = 57.toByte()
        val b3 = 127.toByte()
        val data = byteArrayOf(b1, b2, b3)

        val result = decoder.decode(data).assertDoneAndGet()
        val expected = listOf(b1, b2, b3)

        assertEquals(expected, result)
    }

    @Test
    fun `works correctly with empty collection`() {
        val decoder = ConstantSizeCollectionDecoder(DecoderCollector.toList(), ASCIIStringDecoder(), 0)

        val data = byteArrayOf()

        val result = decoder.decode(data).assertDoneAndGet()
        val expected = emptyList<String>()

        assertEquals(expected, result)
    }

    @Test
    fun `throws on negative size`() {
        assertThrows<IllegalArgumentException> {
            ConstantSizeCollectionDecoder(DecoderCollector.toList(), ASCIIStringDecoder(), -1)
        }
    }

    @Test
    fun `stores decoded element to decode in several steps`() {
        val decoder = ConstantSizeCollectionDecoder(DecoderCollector.toList(), ByteDecoder(), 2)

        val b1 = 9.toByte()
        val b2 = 111.toByte()

        val processing = decoder.decode(byteArrayOf(b1))
        assertInstanceOf(Decoder.State.Processing::class.java, processing)
        val result = decoder.decode(byteArrayOf(b2)).assertDoneAndGet()

        val expected = listOf(b1, b2)
        assertEquals(expected, result)
    }

    @Test
    fun `reset works correctly`() {
        val decoder = ConstantSizeCollectionDecoder(DecoderCollector.toList(), ByteDecoder(), 2)

        val b1 = 9.toByte()
        val b2 = 111.toByte()
        val data = byteArrayOf(b1, b2)

        val processing = decoder.decode(byteArrayOf(4))
        assertInstanceOf(Decoder.State.Processing::class.java, processing)
        decoder.reset()

        val result = decoder.decode(data).assertDoneAndGet()
        val expected = listOf(b1, b2)
        assertEquals(expected, result)
    }

    @Test
    fun `successive decoding works correctly`() {
        val decoder = ConstantSizeCollectionDecoder(DecoderCollector.toList(), ByteDecoder(), 2)

        val b1 = 9.toByte()
        val b2 = 111.toByte()
        val data = byteArrayOf(b1, b2)

        val result1 = decoder.decode(data).assertDoneAndGet()
        val expected1 = listOf(b1, b2)
        assertEquals(expected1, result1)

        val b3 = 5.toByte()
        val b4 = 57.toByte()
        val data2 = byteArrayOf(b3, b4)

        val result2 = decoder.decode(data2).assertDoneAndGet()
        val expected2 = listOf(b3, b4)
        assertEquals(expected2, result2)
    }

}
