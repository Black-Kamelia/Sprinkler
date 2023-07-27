package com.kamelia.sprinkler.transcoder.binary.transcoder

import com.kamelia.sprinkler.transcoder.binary.decoder.IntDecoder
import com.kamelia.sprinkler.transcoder.binary.decoder.util.assertDoneAndGet
import com.kamelia.sprinkler.transcoder.binary.encoder.IntEncoder
import com.kamelia.sprinkler.transcoder.binary.transcoder.core.Transcoder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TranscoderTest {

    @Test
    fun `transcoder can encode and decode`() {
        val transcoder = Transcoder.create(IntEncoder(), IntDecoder())
        val value = 42
        val encoded = transcoder.encode(value)
        val decoded = transcoder.decode(encoded).assertDoneAndGet()
        assertEquals(value, decoded)
    }

}
