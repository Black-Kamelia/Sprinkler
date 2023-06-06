package com.kamelia.sprinkler.codec.binary.decoder.composer

import com.kamelia.sprinkler.codec.binary.decoder.IntDecoder
import com.kamelia.sprinkler.codec.binary.decoder.core.Decoder
import com.kamelia.sprinkler.codec.binary.decoder.toList
import com.kamelia.sprinkler.codec.binary.decoder.toOptional
import com.kamelia.sprinkler.codec.binary.decoder.util.assertDoneAndGet
import com.kamelia.sprinkler.util.byte
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DecoderComposerBasicTest {

    data class Person(val name: String, val age: Int)

    @Test
    fun `compose simple object using then and reduce`() {
        val decoder = composedDecoder {
            val name = string()
            val age = int()

            Person(name, age)
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
        val decoder = composedDecoder {
            val name = string()
            skip(4)
            val age = int()
            Person(name, age)
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
        val decoder = composedDecoder {
            val first = byte()
            val second = byte()
            first to second
        }

        val data = byteArrayOf(2)
        val result = decoder.decode(data)
        assertEquals(Decoder.State.Processing::class.java, result.javaClass)
    }

    @Test
    fun `compose with simple map`() {
        val decoder = composedDecoder<Number> {
            val opcode = byte()
            val number = when (opcode.toInt()) {
                1 -> byte()
                2 -> short()
                4 -> int()
                8 -> long()
                else -> errorState(Decoder.State.Error("Invalid opcode"))
            }
            number
        }.toList(3)

        val data = byteArrayOf(1, 2, 2, 0, 5, 4, 0, 0, 0, 17)
        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(listOf<Number>(2.toByte(), 5.toShort(), 17), result)
    }

    @Test
    fun `decode several times with the same decoder`() {
        val decoder = composedDecoder {
            val optionalInt by once { IntDecoder().toOptional() }

            val first = byte()
            val second = decode(optionalInt)
            val third = string()

            Triple(first, second, third)
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

        val expected = Triple(byte, null, string)
        repeat(5) {
            val result = decoder.decode(data).assertDoneAndGet()
            assertEquals(expected, result)
        }
    }

}
