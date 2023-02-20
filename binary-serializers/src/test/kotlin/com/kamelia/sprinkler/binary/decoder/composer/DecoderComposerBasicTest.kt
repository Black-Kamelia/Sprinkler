package com.kamelia.sprinkler.binary.decoder.composer

import com.kamelia.sprinkler.binary.decoder.BooleanDecoder
import com.kamelia.sprinkler.binary.decoder.ByteDecoder
import com.kamelia.sprinkler.binary.decoder.IntDecoder
import com.kamelia.sprinkler.binary.decoder.LongDecoder
import com.kamelia.sprinkler.binary.decoder.ShortDecoder
import com.kamelia.sprinkler.binary.decoder.UTF8StringDecoder
import com.kamelia.sprinkler.binary.decoder.UTF8StringDecoderEM
import com.kamelia.sprinkler.binary.decoder.core.Decoder
import com.kamelia.sprinkler.binary.decoder.core.NothingDecoder
import com.kamelia.sprinkler.binary.decoder.util.assertDoneAndGet
import com.kamelia.sprinkler.binary.decoder.util.get
import java.util.stream.Collectors
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DecoderComposerBasicTest {

    data class Person(val name: String, val age: Int)

    @Test
    fun `compose simple object using then and reduce`() {
        val decoder = composedDecoder<Person> {
            beginWith(UTF8StringDecoder())
                .then(IntDecoder())
                .reduce(DecoderComposerBasicTest::Person)
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

        val expected = Person(name, age)
        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(expected, result)
    }

    @Test
    fun `compose simple object using then reduce and skip`() {
        val decoder = composedDecoder<Person> {
            beginWith(UTF8StringDecoder())
                .skip(4)
                .then(IntDecoder())
                .reduce(DecoderComposerBasicTest::Person)
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

        val expected = Person(name, age)
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
    fun `compose with simple map`() {
        val decoder = composedDecoder<List<Number>> {
            beginWith(ByteDecoder())
                .map<Number> {
                    when (it.toInt()) {
                        1 -> ByteDecoder()
                        2 -> ShortDecoder()
                        4 -> IntDecoder()
                        8 -> LongDecoder()
                        else -> NothingDecoder("Unexpected value: $it")
                    }
                }
                .repeat(3, Collectors.toList())
        }

        val data = byteArrayOf(1, 2, 2, 0, 5, 4, 0, 0, 0, 17)
        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(listOf<Number>(2.toByte(), 5.toShort(), 17), result)
    }

    @Test
    fun `compose with present optional value`() {
        val decoder = composedDecoder<Person?> {
            beginWith(UTF8StringDecoder())
                .then(IntDecoder())
                .reduce(DecoderComposerBasicTest::Person)
                .optional(BooleanDecoder())
        }

        val name = "John"
        val age = 42
        val nameBytes = name.toByteArray()
        val size = nameBytes.size

        val data = byteArrayOf(
            1,
            size[3], size[2], size[1], size[0],
            *nameBytes,
            age[3], age[2], age[1], age[0],
        )

        val expected = Person(name, age)
        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(expected, result)
    }

    @Test
    fun `compose with absent optional value`() {
        val decoder = composedDecoder<Person?> {
            beginWith(UTF8StringDecoderEM())
                .then(IntDecoder())
                .reduce(DecoderComposerBasicTest::Person)
                .optional(BooleanDecoder())
        }

        val data = byteArrayOf(0)

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(null, result)
    }

}
