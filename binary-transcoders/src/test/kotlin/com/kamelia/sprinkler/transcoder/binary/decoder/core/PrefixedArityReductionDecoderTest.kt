package com.kamelia.sprinkler.transcoder.binary.decoder.core

import com.kamelia.sprinkler.transcoder.binary.decoder.util.assertDoneAndGet
import java.util.stream.Collectors
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test

class PrefixedArityReductionDecoderTest {

    @Test
    fun `basic test works correctly`() {
        val decoder = PrefixedArityReductionDecoder(
            Collectors.toList(),
            ConstantSizedItemDecoder(1) { get(0).toString() },
            ConstantSizedItemDecoder(1) { get(0) },
        )

        val array = byteArrayOf(5, 19)
        val data = byteArrayOf(array.size.toByte()) + array
        val result = decoder.decode(data).assertDoneAndGet()
        val expected = array.mapTo(mutableListOf()) { it.toString() }
        assertEquals(expected, result)
    }

    @Test
    fun `error on invalid size`() {
        val decoder = PrefixedArityReductionDecoder(
            Collectors.toList(),
            ConstantSizedItemDecoder(0) { 1 },
            ConstantSizedItemDecoder(1) { get(0) },
        )

        val data = byteArrayOf(-2)
        val result = decoder.decode(data)
        assertInstanceOf(Decoder.State.Error::class.java, result)
    }

    @Test
    fun `can decode in several times`() {
        val decoder = PrefixedArityReductionDecoder(
            Collectors.toList(),
            ConstantSizedItemDecoder(1) { get(0).toString() },
            ConstantSizedItemDecoder(1) { get(0) },
        )

        val b1 = 5.toByte()
        val b2 = 19.toByte()
        val processing = decoder.decode(byteArrayOf())
        assertInstanceOf(Decoder.State.Processing::class.java, processing)
        val processing1 = decoder.decode(byteArrayOf(2, b1))
        assertInstanceOf(Decoder.State.Processing::class.java, processing1)

        val result = decoder.decode(byteArrayOf(b2)).assertDoneAndGet()
        val expected = listOf(b1.toString(), b2.toString())
        assertEquals(expected, result)
    }

    @Test
    fun `reset works correctly`() {
        val decoder = PrefixedArityReductionDecoder(
            Collectors.toList(),
            ConstantSizedItemDecoder(1) { get(0).toString() },
            ConstantSizedItemDecoder(1) { get(0) },
        )

        val processing = decoder.decode(byteArrayOf(50, 1, 2))
        assertInstanceOf(Decoder.State.Processing::class.java, processing)
        decoder.reset()
        val result = decoder.decode(byteArrayOf(0)).assertDoneAndGet()
        assertEquals(emptyList<String>(), result)
    }

}
