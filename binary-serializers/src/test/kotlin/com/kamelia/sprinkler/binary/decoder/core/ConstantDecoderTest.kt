package com.kamelia.sprinkler.binary.decoder.core

import com.kamelia.sprinkler.binary.decoder.util.assertDoneAndGet
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ConstantDecoderTest {

    @Test
    fun `decoder works with constant value passed in constructor`() {
        val any = Any()
        val decoder = ConstantDecoder(any)

        val data = byteArrayOf()
        val result = decoder.decode(data).assertDoneAndGet()
        assertTrue(result === any)
    }

    @Test
    fun `decoder works with constant value wrapped in factory`() {
        val any = Any()
        val decoder = ConstantDecoder { any }

        val data = byteArrayOf()
        val result = decoder.decode(data).assertDoneAndGet()
        assertTrue(result === any)
    }

    @Test
    fun `decoder factory correctly creates a new instance`() {
        val decoder = ConstantDecoder { listOf(5) }

        val data = byteArrayOf()
        val result = decoder.decode(data).assertDoneAndGet()
        val result2 = decoder.decode(data).assertDoneAndGet()
        assertTrue(result !== result2)
        assertEquals(result, result2)
    }

    @Test
    fun `decoder doesn't modify the input`() {
        val value = 5
        val decoder = ConstantDecoder(value)

        val data = byteArrayOf(1, 2, 3, 4, 5)
        val copy = data.copyOf()
        val result = decoder.decode(data).assertDoneAndGet()

        assertEquals(value, result)
        assertArrayEquals(copy, data)
    }

    @Test
    fun `reset method doesn't have any effect`() {
        val value = 1
        val decoder = ConstantDecoder(value)

        val data = byteArrayOf()
        val result = decoder.decode(data).assertDoneAndGet()
        decoder.reset()
        val result2 = decoder.decode(data).assertDoneAndGet()

        assertEquals(value, result)
        assertEquals(value, result2)
    }

}
