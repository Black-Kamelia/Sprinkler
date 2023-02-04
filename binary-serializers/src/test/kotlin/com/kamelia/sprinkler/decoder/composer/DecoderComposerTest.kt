package com.kamelia.sprinkler.decoder.composer

import com.kamelia.sprinkler.binary.decoder.ByteDecoder
import com.kamelia.sprinkler.binary.decoder.Decoder
import com.kamelia.sprinkler.binary.decoder.DecoderCollector
import com.kamelia.sprinkler.binary.decoder.IntDecoder
import com.kamelia.sprinkler.binary.decoder.LongDecoder
import com.kamelia.sprinkler.binary.decoder.NothingDecoder
import com.kamelia.sprinkler.binary.decoder.ShortDecoder
import com.kamelia.sprinkler.binary.decoder.UTF8StringDecoder
import com.kamelia.sprinkler.binary.decoder.composer.composedDecoder
import com.kamelia.sprinkler.decoder.util.assertDoneAndGet
import com.kamelia.sprinkler.decoder.util.get
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DecoderComposerTest {

    data class SimplePerson(val name: String, val age: Int)

    @Test
    fun `compose simple objects using then and reduce`() {
        val decoder = composedDecoder<SimplePerson> {
            beginWith(UTF8StringDecoder())
                .then(IntDecoder())
                .reduce(::SimplePerson)
        }

        val name = "John"
        val age = 42

        val nameBytes = name.toByteArray()
        val size = nameBytes.size
        val data = byteArrayOf(
            size[3], size[2], size[1], size[0],
            *nameBytes,
            age[3], age[2], age[1], age[0],
        )

        val expected = SimplePerson(name, age)
        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(expected, result)
    }

    @Test
    fun `compose simple object using then reduce and skip`() {
        val decoder = composedDecoder<SimplePerson> {
            beginWith(UTF8StringDecoder())
                .skip(4)
                .then(IntDecoder())
                .reduce(::SimplePerson)
        }

        val name = "John"
        val age = 42

        val nameBytes = name.toByteArray()
        val size = nameBytes.size
        val data = byteArrayOf(
            size[3], size[2], size[1], size[0],
            *nameBytes,
            1, 3, 2, 5,
            age[3], age[2], age[1], age[0],
        )

        val expected = SimplePerson(name, age)
        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(expected, result)
    }

    @Test
    fun `compose with missing bytes`() {
        val decoder = composedDecoder<Pair<Byte, Byte>> {
            beginWith(ByteDecoder())
                .then(ByteDecoder())
                .reduce(::Pair)
        }

        val data = byteArrayOf(2)
        val result = decoder.decode(data)
        assertEquals(Decoder.State.Processing::class.java, result.javaClass)
    }

    @Test
    fun `one step composition directly returns the base decoder`() {
        val byteDecoder = ByteDecoder()
        val decoder = composedDecoder<Byte> {
            beginWith(byteDecoder)
        }

        assertEquals(byteDecoder, decoder)
    }

    @Test
    fun `compose with constant repetition greater than 1`() {
        val decoder = composedDecoder<List<Byte>> {
            beginWith(ByteDecoder())
                .repeat(3, DecoderCollector.toList())
        }

        val data = byteArrayOf(1, 2, 3)
        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(listOf<Byte>(1, 2, 3), result)
    }

    @Test
    fun `compose with constant repetition equal to 1`() {
        val decoder = composedDecoder<List<Byte>> {
            beginWith(ByteDecoder())
                .repeat(1, DecoderCollector.toList())
        }

        val data = byteArrayOf(1)
        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(listOf<Byte>(1), result)
    }

    @Test
    fun `compose with constant repetition equal to 0`() {
        val decoder = composedDecoder<List<Byte>> {
            beginWith(ByteDecoder())
                .repeat(0, DecoderCollector.toList())
        }

        val data = byteArrayOf()
        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(emptyList<Byte>(), result)
    }

    @Test
    fun `compose with prefixed size repetition greater than 1`() {
        val decoder = composedDecoder<List<Byte>> {
            beginWith(ByteDecoder())
                .repeat(DecoderCollector.toList(), IntDecoder())
        }

        val data = byteArrayOf(0, 0, 0, 2, 1, 6)
        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(listOf<Byte>(1, 6), result)
    }

    @Test
    fun `compose with prefixed size repetition equal to 1`() {
        val decoder = composedDecoder<List<Byte>> {
            beginWith(ByteDecoder())
                .repeat(DecoderCollector.toList(), IntDecoder())
        }

        val data = byteArrayOf(0, 0, 0, 1, 1)
        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(listOf<Byte>(1), result)
    }

    @Test
    fun `compose with prefixed size repetition equal to 0`() {
        val decoder = composedDecoder<List<Byte>> {
            beginWith(ByteDecoder())
                .repeat(DecoderCollector.toList(), IntDecoder())
        }

        val data = byteArrayOf(0, 0, 0, 0)
        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(emptyList<Byte>(), result)
    }

    @Test
    fun `compose with simple map`() {
        val decoder = composedDecoder<List<Number>> {
            beginWith(ByteDecoder())
                .map {
                    when (it.toInt()) {
                        1 -> ByteDecoder()
                        2 -> ShortDecoder()
                        4 -> IntDecoder()
                        8 -> LongDecoder()
                        else -> NothingDecoder("Unexpected value: $it")
                    }
                }
                .repeat(3, DecoderCollector.toList())
        }

        val data = byteArrayOf(1, 2, 2, 0, 5, 4, 0, 0, 0, 17)
        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(listOf<Number>(2.toByte(), 5.toShort(), 17), result)
    }

}
