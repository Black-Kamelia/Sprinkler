package com.kamelia.sprinkler.transcoder.binary.decoder.composer

import com.kamelia.sprinkler.transcoder.binary.decoder.core.Decoder
import com.kamelia.sprinkler.transcoder.binary.decoder.util.assertDoneAndGet
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ComposedDecoderTest {

    @Test
    fun `reset works with shorthand decoders`() {
        val decoder = composedDecoder {
            int()
        }
        val bytes1 = byteArrayOf(2, 2)
        val result1 = decoder.decode(bytes1)
        assertEquals(Decoder.State.Processing, result1)
        decoder.reset()
        val bytes2 = byteArrayOf(0, 0, 0, 1)
        val result2 = decoder.decode(bytes2).assertDoneAndGet()
        assertEquals(1, result2)
    }

}
