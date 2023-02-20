package com.kamelia.sprinkler.binary.decoder.composer

import com.kamelia.sprinkler.binary.decoder.BooleanDecoder
import com.kamelia.sprinkler.binary.decoder.ByteDecoder
import com.kamelia.sprinkler.binary.decoder.IntDecoder
import com.kamelia.sprinkler.binary.decoder.UTF8StringDecoder
import com.kamelia.sprinkler.binary.decoder.util.assertDoneAndGet
import com.kamelia.sprinkler.binary.decoder.util.get
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RecursionStepTest {

    private data class Person(val name: String, val age: Int, val father: Person?)

    private data class Node<T>(val value: T, val left: Node<T>?, val right: Node<T>?)

    @Test
    fun `compose with recursion with depth 0`() {
        val decoder = composedDecoder<Person> {
            beginWith(UTF8StringDecoder())
                .then(IntDecoder())
                .thenItselfOrNull(BooleanDecoder())
                .reduce(RecursionStepTest::Person)
        }

        val name = "John"
        val nameBytes = name.toByteArray()
        val size = nameBytes.size
        val age = 42

        val data = byteArrayOf(
            size[3], size[2], size[1], size[0],
            *nameBytes,
            age[3], age[2], age[1], age[0],
            0
        )

        val expected = Person(name, age, null)
        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(expected, result)
    }

    @Test
    fun `compose with recursion with depth greater than 0`() {
        val decoder = composedDecoder<Person> {
            beginWith(UTF8StringDecoder())
                .then(IntDecoder())
                .thenItselfOrNull(BooleanDecoder())
                .reduce(RecursionStepTest::Person)
        }

        val name = "John"
        val nameBytes = name.toByteArray()
        val size = nameBytes.size
        val age = 42

        val data = byteArrayOf(
            size[3], size[2], size[1], size[0],
            *nameBytes,
            age[3], age[2], age[1], age[0],
            1,
            size[3], size[2], size[1], size[0],
            *nameBytes,
            age[3], age[2], age[1], age[0],
            1,
            size[3], size[2], size[1], size[0],
            *nameBytes,
            age[3], age[2], age[1], age[0],
            0
        )

        val expected = Person(name, age, Person(name, age, Person(name, age, null)))
        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(expected, result)
    }

    @Test
    fun `compose with complex recursion`() {
        val decoder = composedDecoder<Node<Byte>> {
            beginWith(ByteDecoder())
                .thenItselfOrNull(BooleanDecoder())
                .thenItselfOrNull(BooleanDecoder())
                .reduce(RecursionStepTest::Node)
        }

        val data = byteArrayOf(1, 1, 2, 0, 0, 1, 5, 0, 1, 3, 0, 0)

        val expected: Node<Byte> = Node(
            1,
            Node(
                2,
                null,
                null
            ),
            Node(
                5,
                null,
                Node(
                    3,
                    null,
                    null
                )
            )
        )
        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(expected, result)
    }

}
