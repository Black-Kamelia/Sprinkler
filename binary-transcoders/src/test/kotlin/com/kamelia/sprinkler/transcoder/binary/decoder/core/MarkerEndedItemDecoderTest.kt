package com.kamelia.sprinkler.transcoder.binary.decoder.core

import com.kamelia.sprinkler.transcoder.binary.decoder.util.assertDoneAndGet
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MarkerEndedItemDecoderTest {

    @Test
    fun `basic test works correctly`() {
        val endMarker = byteArrayOf(0x0)
        val decoder = MarkerEndedItemDecoder(endMarker) { String(this, 0, it, Charsets.US_ASCII) }

        val value = "Hello World"
        val data = value.toByteArray(Charsets.US_ASCII) + endMarker
        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(value, result)
    }

    @Test
    fun `throws on empty end marker`() {
        assertThrows<IllegalArgumentException> {
            MarkerEndedItemDecoder(byteArrayOf()) { "" }
        }
    }

    @Test
    fun `can decode in several times`() {
        val endMarker = byteArrayOf(0x0)
        val decoder = MarkerEndedItemDecoder(endMarker) { String(this, 0, it, Charsets.US_ASCII) }

        val value = "Hello World!"
        val data = value.toByteArray(Charsets.US_ASCII)

        val processing = decoder.decode(byteArrayOf())
        assertInstanceOf(Decoder.State.Processing::class.java, processing)

        val processing1 = decoder.decode(data)
        assertInstanceOf(Decoder.State.Processing::class.java, processing1)

        val result = decoder.decode(endMarker).assertDoneAndGet()
        assertEquals(value, result)
    }

    @Test
    fun `reset works correctly`() {
        val endMarker = byteArrayOf(0x0)
        val decoder = MarkerEndedItemDecoder(endMarker) { String(this, 0, it, Charsets.US_ASCII) }

        val value = "Hello World!"
        val data = value.toByteArray(Charsets.US_ASCII)
        val processing = decoder.decode(data)
        assertInstanceOf(Decoder.State.Processing::class.java, processing)

        decoder.reset()
        val result = decoder.decode(endMarker).assertDoneAndGet()
        assertEquals("", result)
    }

}
