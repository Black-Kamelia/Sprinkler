package com.kamelia.sprinkler.decoder.composer

import com.kamelia.sprinkler.binary.decoder.ByteDecoder
import com.kamelia.sprinkler.binary.decoder.DecoderCollector
import com.kamelia.sprinkler.binary.decoder.IntDecoder
import com.kamelia.sprinkler.binary.decoder.composer.composedDecoder
import com.kamelia.sprinkler.decoder.util.assertDoneAndGet
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class PrefixedSizeRepeatStepTest {

    @Test
    fun `compose with prefixed size repetition greater than 1`() {
        val decoder = composedDecoder<List<Byte>> {
            beginWith(ByteDecoder())
                .repeat(DecoderCollector.toList(), IntDecoder())
        }

        val data = byteArrayOf(0, 0, 0, 2, 1, 6)
        val result = decoder.decode(data).assertDoneAndGet()
        Assertions.assertEquals(listOf<Byte>(1, 6), result)
    }

    @Test
    fun `compose with prefixed size repetition equal to 1`() {
        val decoder = composedDecoder<List<Byte>> {
            beginWith(ByteDecoder())
                .repeat(DecoderCollector.toList(), IntDecoder())
        }

        val data = byteArrayOf(0, 0, 0, 1, 1)
        val result = decoder.decode(data).assertDoneAndGet()
        Assertions.assertEquals(listOf<Byte>(1), result)
    }

    @Test
    fun `compose with prefixed size repetition equal to 0`() {
        val decoder = composedDecoder<List<Byte>> {
            beginWith(ByteDecoder())
                .repeat(DecoderCollector.toList(), IntDecoder())
        }

        val data = byteArrayOf(0, 0, 0, 0)
        val result = decoder.decode(data).assertDoneAndGet()
        Assertions.assertEquals(emptyList<Byte>(), result)
    }


}
