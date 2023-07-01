package com.kamelia.sprinkler.transcoder.binary.decoder.core

import java.io.ByteArrayInputStream
import java.nio.ByteBuffer
import java.util.stream.Stream
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Named
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class DecoderInputTest {

    @ParameterizedTest
    @MethodSource("decoderDataInputImplementations")
    fun `read works correctly`(factory: (ByteArray) -> DecoderInput) {
        val b1 = 3.toByte()
        val b2 = 5.toByte()
        val input = factory(byteArrayOf(b1, b2))

        assertEquals(b1, input.read().toByte())
        assertEquals(b2, input.read().toByte())
        assertEquals(-1, input.read().toByte())
    }

    @ParameterizedTest
    @MethodSource("decoderDataInputImplementations")
    fun `read to byte array works correctly`(factory: (ByteArray) -> DecoderInput) {
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
        factory: (ByteArray) -> DecoderInput,
    ) {
        val input = factory(byteArrayOf())
        val receiver = ByteArray(1)
        assertThrows<IndexOutOfBoundsException> {
            input.read(receiver, -1, 1)
        }
        assertThrows<IndexOutOfBoundsException> {
            input.read(receiver, 1, -1)
        }
        assertThrows<IndexOutOfBoundsException> {
            input.read(receiver, 1, 2)
        }
    }

    @ParameterizedTest
    @MethodSource("decoderDataInputImplementations")
    fun `read to collection works correctly`(factory: (ByteArray) -> DecoderInput) {
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
    fun `read to collection stops when collection add returns false`(factory: (ByteArray) -> DecoderInput) {
        val b1 = 3.toByte()

        var returnFalse = false
        val inner = ArrayList<Byte>()
        val dummyList: MutableList<Byte> = object : MutableList<Byte> by inner {
            override fun add(element: Byte): Boolean = if (returnFalse) false else inner.add(element)
        }

        val input = factory(byteArrayOf(b1, b1, b1))
        assertEquals(1, input.read(dummyList, 1))
        assertEquals(listOf(b1), dummyList)
        returnFalse = true
        assertEquals(0, input.read(dummyList))
    }

    @ParameterizedTest
    @MethodSource("decoderDataInputImplementations")
    fun `skip works correctly`(factory: (ByteArray) -> DecoderInput) {
        val b1 = 3.toByte()
        val b2 = 5.toByte()
        val b3 = 7.toByte()
        val input = factory(byteArrayOf(b1, b2, b3))
        assertEquals(1, input.skip(1))
        assertEquals(b2, input.read().toByte())
        assertEquals(1, input.skip(10))
        assertEquals(-1, input.read().toByte())
    }

    @Test
    fun `nullInput works correctly`() {
        val input = DecoderInput.nullInput()
        assertEquals(-1, input.read().toByte())
        assertEquals(-1, input.readBit().toByte())
        assertEquals(0, input.read(ByteArray(1), 0, 1))
        assertEquals(0, input.readBits(ByteArray(1), 0, 1))
        assertEquals(0, input.read(mutableListOf()))
        assertEquals(0, input.skip(1))
        assertThrows<IndexOutOfBoundsException> { input.read(ByteArray(1), -1, 1) }
        assertThrows<IndexOutOfBoundsException> { input.read(ByteArray(1), 2, 0) }
        assertThrows<IndexOutOfBoundsException> { input.read(ByteArray(1), 0, 2) }
        assertThrows<IndexOutOfBoundsException> { input.read(ByteArray(1), 0, -1) }
        assertThrows<IndexOutOfBoundsException> { input.readBits(ByteArray(1), -1, 1) }
        assertThrows<IndexOutOfBoundsException> { input.readBits(ByteArray(1), 9, 0) }
        assertThrows<IndexOutOfBoundsException> { input.readBits(ByteArray(1), 0, 9) }
        assertThrows<IndexOutOfBoundsException> { input.readBits(ByteArray(1), 0, -1) }
    }

    @ParameterizedTest
    @MethodSource("decoderDataInputImplementations")
    fun `read bit works correctly`(factory: (ByteArray) -> DecoderInput) {
        val input = factory(byteArrayOf(0b1010_1111.toByte()))
        assertEquals(1, input.readBit())
        assertEquals(0, input.readBit())
        assertEquals(1, input.readBit())
        assertEquals(0, input.readBit())
        assertEquals(1, input.readBit())
        assertEquals(1, input.readBit())
        assertEquals(1, input.readBit())
        assertEquals(1, input.readBit())
        assertEquals(-1, input.readBit())
    }

    @ParameterizedTest
    @MethodSource("decoderDataInputImplementations")
    fun `read byte after read but works correctly`(factory: (ByteArray) -> DecoderInput) {
        val input = factory(byteArrayOf(0b0111_1110.toByte(), 0b1000_0000.toByte()))
        assertEquals(0, input.readBit())
        assertEquals(0b1111_1101, input.read())
    }


    private companion object {

        @JvmStatic
        fun decoderDataInputImplementations(): Stream<Arguments> = Stream.of(
            Arguments.of(Named.of<(ByteArray) -> DecoderInput>("ByteArray", DecoderInput.Companion::from)),
            Arguments.of(Named.of<(ByteArray) -> DecoderInput>("InputStream") { DecoderInput.from(ByteArrayInputStream(it)) }),
            Arguments.of(Named.of<(ByteArray) -> DecoderInput>("ByteBuffer") {
                DecoderInput.from(ByteBuffer.wrap(it).apply { position(limit()) })
            }),
            Arguments.of(Named.of<(ByteArray) -> DecoderInput>("CustomLambda") {
                var index = 0
                DecoderInput.from { if (index < it.size) (it[index++].toInt() and 0xFF) else -1 }
            }),
        )

    }

}
