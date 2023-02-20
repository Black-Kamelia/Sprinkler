package com.kamelia.sprinkler.binary.decoder.core

import com.kamelia.sprinkler.binary.decoder.util.assertDoneAndGet
import java.util.stream.Collectors
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test

class MarkerElementCollectionDecoderTest {

    @Test
    fun `basic test works correctly`() {
        var value = 1
        val decoder = MarkerElementCollectionDecoder(
            Collectors.toList(),
            ConstantSizeDecoder(1) { value },
        ) {
            it == 0
        }

        val size = 10
        val result = decoder.decode(ByteArray(size))
        assertInstanceOf(Decoder.State.Processing::class.java, result)

        value = 0
        val result2 = decoder.decode(byteArrayOf(1)).assertDoneAndGet()
        assertEquals(size, result2.size)
        assertEquals(1, result2.toSet().size)
    }

    @Test
    fun `keepLast correctly keeps the last element`() {
        val decoder = MarkerElementCollectionDecoder(
            Collectors.toList(),
            ConstantSizeDecoder(1) { get(0) },
            true
        ) {
            it == 0.toByte()
        }

        val data = byteArrayOf(1, 2, 3, 0)
        val result = decoder.decode(data).assertDoneAndGet()
        val expected = data.toList()
        assertEquals(expected, result)
    }

    @Test
    fun `reset works correctly`() {
        val decoder = MarkerElementCollectionDecoder(
            Collectors.toList(),
            ConstantSizeDecoder(1) { get(0) },
        ) {
            it == 0.toByte()
        }

        val data = byteArrayOf(1, 2, 3)
        val processing = decoder.decode(data)
        assertInstanceOf(Decoder.State.Processing::class.java, processing)

        decoder.reset()
        val result = decoder.decode(byteArrayOf(0)).assertDoneAndGet()
        assertEquals(emptyList<Byte>(), result)
    }

}
