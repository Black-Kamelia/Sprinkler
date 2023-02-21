package com.kamelia.sprinkler.binary.decoder.composer

import com.kamelia.sprinkler.binary.decoder.ByteDecoder
import com.kamelia.sprinkler.binary.decoder.IntDecoder
import com.kamelia.sprinkler.binary.decoder.LongDecoder
import com.kamelia.sprinkler.binary.decoder.ShortDecoder
import com.kamelia.sprinkler.binary.decoder.UTF8StringDecoder
import com.kamelia.sprinkler.binary.decoder.core.Decoder
import com.kamelia.sprinkler.binary.decoder.core.NothingDecoder
import com.kamelia.sprinkler.binary.decoder.toList
import com.kamelia.sprinkler.binary.decoder.toOptional
import com.kamelia.sprinkler.binary.decoder.util.assertDoneAndGet
import com.kamelia.sprinkler.util.byte
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
            size.byte(3), size.byte(2), size.byte(1), size.byte(0),
            *nameBytes,
            age.byte(3), age.byte(2), age.byte(1), age.byte(0),
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
            size.byte(3), size.byte(2), size.byte(1), size.byte(0),
            *nameBytes,
            1, 3, 2, 5,
            age.byte(3), age.byte(2), age.byte(1), age.byte(0),
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
        val decoder = composedDecoder<Number> {
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
        }.toList(3)

        val data = byteArrayOf(1, 2, 2, 0, 5, 4, 0, 0, 0, 17)
        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(listOf<Number>(2.toByte(), 5.toShort(), 17), result)
    }

    @Test
    fun `decode several times with the same decoder`() {
        val decoder = composedDecoder<Triple<Byte, Int?, String>> {
            beginWith(ByteDecoder())
                .then(IntDecoder().toOptional())
                .then(UTF8StringDecoder())
                .reduce(::Triple)
        }

        val byte = 19.toByte()
        val string = "Hello world!"
        val stringArray = string.toByteArray()
        val size = stringArray.size
        val data = byteArrayOf(
            byte,
            0,
            size.byte(3), size.byte(2), size.byte(1), size.byte(0),
            *stringArray
        )
    }

}
