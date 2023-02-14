package com.kamelia.sprinkler.binary.decoder.composer

import com.kamelia.sprinkler.binary.decoder.ByteDecoder
import com.kamelia.sprinkler.binary.decoder.IntDecoder
import com.kamelia.sprinkler.binary.decoder.UTF8StringDecoder
import com.kamelia.sprinkler.binary.decoder.core.DecoderCollector
import com.kamelia.sprinkler.binary.decoder.util.assertDoneAndGet
import com.kamelia.sprinkler.binary.decoder.util.get
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ConstantSizeRepeatStepTest {

    private data class SimplePerson(val name: String, val age: Int)

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
    fun `compose primitive's nested collection using constant size repetition`() {
        val decoder = composedDecoder<List<Set<Int>>> {
            beginWith(IntDecoder())
                .repeat(1, DecoderCollector.toSet())
                .repeat(2, DecoderCollector.toList())
        }

        val data = byteArrayOf(
            0, 0, 0, 1,
            0, 0, 0, 2,
        )
        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(listOf(setOf(1), setOf(2)), result)
    }

    @Test
    fun `compose simple object's nested collection using constant size repetition`() {
        val decoder = composedDecoder<List<Set<SimplePerson>>> {
            beginWith(UTF8StringDecoder())
                .then(IntDecoder())
                .reduce(ConstantSizeRepeatStepTest::SimplePerson)
                .repeat(1, DecoderCollector.toSet())
                .repeat(2, DecoderCollector.toList())
        }

        val name1 = "John"
        val age1 = 42
        val nameBytes1 = name1.toByteArray()
        val nameSize1 = nameBytes1.size

        val name2 = "Jane"
        val age2 = 43
        val nameBytes2 = name2.toByteArray()
        val nameSize2 = nameBytes2.size

        val data = byteArrayOf(
            nameSize1[3], nameSize1[2], nameSize1[1], nameSize1[0],
            *nameBytes1,
            age1[3], age1[2], age1[1], age1[0],
            nameSize2[3], nameSize2[2], nameSize2[1], nameSize2[0],
            *nameBytes2,
            age2[3], age2[2], age2[1], age2[0],
        )

        val expected = listOf(setOf(SimplePerson(name1, age1)), setOf(SimplePerson(name2, age2)))
        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(expected, result)
    }

}
