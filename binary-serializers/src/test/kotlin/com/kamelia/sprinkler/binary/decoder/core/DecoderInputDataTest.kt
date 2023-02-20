package com.kamelia.sprinkler.binary.decoder.core

import java.io.ByteArrayInputStream
import java.nio.ByteBuffer
import java.util.stream.Stream
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class DecoderInputDataTest {

    @ParameterizedTest
    @MethodSource("decoderDataInputImplementations")
    fun `read works correctly`(factory: (ByteArray) -> DecoderInputData) {
        val b1 = 3.toByte()
        val b2 = 5.toByte()
        val input = factory(byteArrayOf(b1, b2))

        assertEquals(b1, input.read().toByte())
        assertEquals(b2, input.read().toByte())
        assertEquals(-1, input.read().toByte())
    }

    @ParameterizedTest
    @MethodSource("decoderDataInputImplementations")
    fun `read to byte array works correctly`(factory: (ByteArray) -> DecoderInputData) {
        val b1 = 3.toByte()
        val b2 = 5.toByte()
        val b3 = 19.toByte()
        val input = factory(byteArrayOf(b1, b2, b3))
        val receiver = ByteArray(4)
        assertEquals(1, input.read(receiver, 0, 1))
        assertEquals(b1, receiver[0])
        assertEquals(2, input.read(receiver, 2, 2))
        assertEquals(b2, receiver[2])
        assertEquals(b3, receiver[3])
        assertEquals(0, receiver[1]) // not overwritten
        assertEquals(0, input.read(receiver))
    }

    @ParameterizedTest
    @MethodSource("decoderDataInputImplementations")
    fun `read to byte array throws IllegalArgumentException with invalid parameters`(
        factory: (ByteArray) -> DecoderInputData,
    ) {
        val input = factory(byteArrayOf())
        val receiver = ByteArray(1)
        assertThrows<IllegalArgumentException> {
            input.read(receiver, -1, 1)
        }
        assertThrows<IllegalArgumentException> {
            input.read(receiver, 1, -1)
        }
    }

    @ParameterizedTest
    @MethodSource("decoderDataInputImplementations")
    fun `read to collection works correctly`(factory: (ByteArray) -> DecoderInputData) {
        val b1 = 3.toByte()
        val b2 = 5.toByte()
        val b3 = 7.toByte()
        val input = factory(byteArrayOf(b1, b2, b3))
        val receiver = mutableListOf<Byte>()
        assertEquals(1, input.read(receiver, 1))
        assertEquals(listOf(b1), receiver)
        assertEquals(2, input.read(receiver))
        assertEquals(listOf(b1, b2, b3), receiver)
        assertEquals(0, input.read(receiver))
    }

    @ParameterizedTest
    @MethodSource("decoderDataInputImplementations")
    fun `read to collection stops when collection size has reached max int`(factory: (ByteArray) -> DecoderInputData) {
        val b1 = 3.toByte()

        var isMaxValue = false
        val inner = ArrayList<Byte>()
        val dummyList: MutableList<Byte> = object : MutableList<Byte> by inner {
            override val size: Int
                get() = if (isMaxValue) Int.MAX_VALUE else inner.size
        }

        val input = factory(byteArrayOf(b1))
        assertEquals(1, input.read(dummyList))
        assertEquals(listOf(b1), dummyList)
        isMaxValue = true
        assertEquals(0, input.read(dummyList))
    }

    @ParameterizedTest
    @MethodSource("decoderDataInputImplementations")
    fun `skip works correctly`(factory: (ByteArray) -> DecoderInputData) {
        val b1 = 3.toByte()
        val b2 = 5.toByte()
        val b3 = 7.toByte()
        val input = factory(byteArrayOf(b1, b2, b3))
        assertEquals(1, input.skip(1))
        assertEquals(b2, input.read().toByte())
        assertEquals(1, input.skip(10))
        assertEquals(-1, input.read().toByte())
    }

    private companion object {

        @JvmStatic
        fun decoderDataInputImplementations(): Stream<(ByteArray) -> DecoderInputData> = Stream.of(
            DecoderInputData.Companion::from,
            { DecoderInputData.from(ByteArrayInputStream(it)) },
            { DecoderInputData.from(ByteBuffer.wrap(it).apply { position(limit()) }) },
        )

    }

}
