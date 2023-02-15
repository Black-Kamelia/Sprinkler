package com.kamelia.sprinkler.binary.decoder.core

import com.kamelia.sprinkler.binary.decoder.IntDecoder
import com.kamelia.sprinkler.binary.decoder.util.assertDoneAndGet
import com.kamelia.sprinkler.binary.decoder.util.get
import java.io.ByteArrayInputStream
import java.nio.ByteBuffer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class DecoderTest {

    @Test
    fun `decode behaves the same way for all overloads`() {
        val value = 5
        val byteArray = byteArrayOf(value[3], value[2], value[1], value[0])
        val stream = ByteArrayInputStream(byteArray.copyOf())
        val buffer = ByteBuffer.wrap(byteArray.copyOf()).apply { position(limit()) }

        val decoder = IntDecoder()
        val byteArrayResult = decoder.decode(byteArray).assertDoneAndGet()
        val streamResult = decoder.decode(stream).assertDoneAndGet()
        val bufferResult = decoder.decode(buffer).assertDoneAndGet()
        assertEquals(value, byteArrayResult)
        assertEquals(value, streamResult)
        assertEquals(value, bufferResult)
    }

}
