package com.kamelia.sprinkler.binary.decoder.composer

import com.kamelia.sprinkler.binary.decoder.ByteDecoder
import com.kamelia.sprinkler.binary.decoder.core.Decoder
import com.kamelia.sprinkler.binary.decoder.util.assertDoneAndGet
import java.util.stream.Collectors
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UntilRepeatStepTest {

    @Test
    fun `compose primitive collection with undefined repetition`() {
        val decoder = composedDecoder<List<Byte>> {
            beginWith(ByteDecoder())
                .until(Collectors.toList()) { it == 0.toByte() }
        }

        val data = byteArrayOf(1, 2, 3, 0, 4, 5, 6)
        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(listOf<Byte>(1, 2, 3), result)
    }

    @Test
    fun `compose primitive collection with undefined repetition and addLast = true`() {
        val decoder = composedDecoder<List<Byte>> {
            beginWith(ByteDecoder())
                .until(Collectors.toList(), true) { it == 0.toByte() }
        }

        val data = byteArrayOf(1, 2, 3, 0, 4, 5, 6)
        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(listOf<Byte>(1, 2, 3, 0), result)
    }

    @Test
    fun `compose collection with missing bytes`() {
        val decoder = composedDecoder<List<Byte>> {
            beginWith(ByteDecoder())
                .until(Collectors.toList()) { it == 0.toByte() }
        }

        val data = byteArrayOf(1, 2, 3)
        val result = decoder.decode(data)
        assertEquals(Decoder.State.Processing::class.java, result.javaClass)
    }

}
