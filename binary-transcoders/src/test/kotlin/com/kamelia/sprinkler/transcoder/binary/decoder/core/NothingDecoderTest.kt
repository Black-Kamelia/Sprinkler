package com.kamelia.sprinkler.transcoder.binary.decoder.core

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test

class NothingDecoderTest {

    @Test
    fun `returns error on decode`() {
        val decoder = NothingDecoder()
        val result = decoder.decode(byteArrayOf())
        assertInstanceOf(Decoder.State.Error::class.java, result)
    }

    @Test
    fun `returns error on decode with custom message`() {
        val message = "test"
        val decoder = NothingDecoder(message)
        val result = decoder.decode(byteArrayOf())
        assertInstanceOf(Decoder.State.Error::class.java, result)
        assertEquals(message, (result as Decoder.State.Error).error.message)
    }

    @Test
    fun `returns error on decode using given throwable`() {
        val exception = RuntimeException()
        val decoder = NothingDecoder(exception)
        val result = decoder.decode(byteArrayOf())
        assertInstanceOf(Decoder.State.Error::class.java, result)
        assertEquals(exception, (result as Decoder.State.Error).error)
    }

    @Test
    fun `reset doesn't throw`() {
        val decoder = NothingDecoder()
        assertDoesNotThrow {
            decoder.reset()
        }
    }

}
