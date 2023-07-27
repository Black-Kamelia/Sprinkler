package com.kamelia.sprinkler.transcoder.binary.decoder.composer

import com.kamelia.sprinkler.transcoder.binary.decoder.ShortDecoder
import com.kamelia.sprinkler.transcoder.binary.decoder.core.Decoder
import com.kamelia.sprinkler.transcoder.binary.decoder.core.NothingDecoder
import com.kamelia.sprinkler.transcoder.binary.decoder.util.assertDoneAndGet
import com.kamelia.sprinkler.util.byte
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.nio.ByteOrder
import java.util.stream.Collectors

class DecodingScopeImplTest {

    @Test
    fun `inconsistent shorthand calls in scope between decode calls can cause errors`() {
        var called = false
        val decoder = composedDecoder {
            if (!called) {
                called = true
                byte()
            } else {
                string()
            }
            byte()
        }

        val bytes1 = byteArrayOf(1)
        val bytes2 = byteArrayOf(1, 5)
        val result1 = decoder.decode(bytes1)
        assertEquals(Decoder.State.Processing, result1)

        assertThrows<IllegalStateException> {
            decoder.decode(bytes2)
        }
    }

    @Test
    fun `scope byte shorthand works correctly`() {
        val decoder = composedDecoder {
            byte()
        }
        val bytes = byteArrayOf(0x01)
        val result = decoder.decode(bytes).assertDoneAndGet()
        assertEquals(1, result)
    }

    @Test
    fun `scope short shorthand works correctly`() {
        val decoder = composedDecoder {
            short()
        }
        val bytes = byteArrayOf(0x00, 0x01)
        val result = decoder.decode(bytes).assertDoneAndGet()
        assertEquals(1, result)
    }

    @Test
    fun `scope int shorthand works correctly`() {
        val decoder = composedDecoder {
            int()
        }
        val bytes = byteArrayOf(0x00, 0x00, 0x00, 0x01)
        val result = decoder.decode(bytes).assertDoneAndGet()
        assertEquals(1, result)
    }

    @Test
    fun `scope long shorthand works correctly`() {
        val decoder = composedDecoder {
            long()
        }
        val bytes = byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01)
        val result = decoder.decode(bytes).assertDoneAndGet()
        assertEquals(1, result)
    }

    @Test
    fun `scope float shorthand works correctly`() {
        val decoder = composedDecoder {
            float()
        }
        val value = 1f.toRawBits()
        val bytes = byteArrayOf(value.byte(3), value.byte(2), value.byte(1), value.byte(0))
        val result = decoder.decode(bytes).assertDoneAndGet()
        assertEquals(1.0f, result)
    }

    @Test
    fun `scope double shorthand works correctly`() {
        val decoder = composedDecoder {
            double()
        }
        val value = 1.0.toRawBits()
        val bytes =
            byteArrayOf(
                value.byte(7),
                value.byte(6),
                value.byte(5),
                value.byte(4),
                value.byte(3),
                value.byte(2),
                value.byte(1),
                value.byte(0)
            )
        val result = decoder.decode(bytes).assertDoneAndGet()
        assertEquals(1.0, result)
    }

    @Test
    fun `scope boolean shorthand works correctly`() {
        val decoder = composedDecoder {
            boolean()
        }
        val bytes = byteArrayOf(0x01)
        val result = decoder.decode(bytes).assertDoneAndGet()
        assertEquals(true, result)
    }

    @Test
    fun `scope string shorthand works correctly`() {
        val decoder = composedDecoder {
            string()
        }
        val value = "hello"
        val array = value.encodeToByteArray()
        val size = array.size
        val bytes = byteArrayOf(size.byte(3), size.byte(2), size.byte(1), size.byte(0)) + array
        val result = decoder.decode(bytes).assertDoneAndGet()
        assertEquals(value, result)
    }

    @Test
    fun `scope custom decode works correctly`() {
        val inner = ShortDecoder(ByteOrder.LITTLE_ENDIAN)
        val decoder = composedDecoder {
            decode(inner)
        }
        val bytes = byteArrayOf(0x01, 0x00)
        val result = decoder.decode(bytes).assertDoneAndGet()
        assertEquals(1, result)
    }

    @Test
    fun `scope errorState works correctly`() {
        val error = Decoder.State.Error("an error occurred")
        val decoder = composedDecoder {
            errorState(error)
        }
        val result = decoder.decode(byteArrayOf())
        val errorResult = assertInstanceOf(Decoder.State.Error::class.java, result)
        assertEquals(error.error.message, errorResult.error.message)
    }

    @Test
    fun `skip works correctly with 0`() {
        val decoder = composedDecoder {
            skip(0)
            byte()
        }
        val bytes = byteArrayOf(4, 1)
        val result = decoder.decode(bytes).assertDoneAndGet()
        assertEquals(4, result)
    }

    @Test
    fun `skip throws on negative value`() {
        val decoder = composedDecoder {
            skip(-1)
        }
        assertThrows<IllegalArgumentException> {
            decoder.decode(byteArrayOf())
        }
    }

    @Test
    fun `skip does not skip several times the count`() {
        val decoder = composedDecoder {
            skip(1)
            byte()
        }
        val bytes1 = byteArrayOf(1)
        val bytes2 = byteArrayOf(5)
        val result1 = decoder.decode(bytes1)
        assertEquals(Decoder.State.Processing, result1)

        val result2 = decoder.decode(bytes2).assertDoneAndGet()
        assertEquals(5, result2)
    }

    @Test
    fun `skip keeps track of the bytes left to skip`() {
        val decoder = composedDecoder {
            skip(2)
            byte()
        }
        val bytes1 = byteArrayOf(1)
        val bytes2 = byteArrayOf(3, 5)
        val result1 = decoder.decode(bytes1)
        assertEquals(Decoder.State.Processing, result1)

        val result2 = decoder.decode(bytes2).assertDoneAndGet()
        assertEquals(5, result2)
    }

    @Test
    fun `decode in several times works correctly`() {
        val decoder = composedDecoder {
            short()
        }
        val processing = decoder.decode(byteArrayOf(0x00))
        assertEquals(Decoder.State.Processing, processing)
        val result = decoder.decode(byteArrayOf(0x01)).assertDoneAndGet()
        assertEquals(1, result)
    }

    @Test
    fun `decode on error works correctly`() {
        val decoder = composedDecoder {
            decode<Int>(NothingDecoder())
        }
        val result = decoder.decode(byteArrayOf())
        assertInstanceOf(Decoder.State.Error::class.java, result)
    }

    @Test
    fun `decoded element is not decoded again`() {
        val decoder = composedDecoder {
            val byte = byte()
            val short = short()
            byte to short
        }
        val bytes1 = byteArrayOf(0x02)
        val bytes2 = byteArrayOf(0x00, 0x01)
        assertEquals(Decoder.State.Processing, decoder.decode(bytes1))
        val result = decoder.decode(bytes2).assertDoneAndGet()
        assertEquals(2, result.first)
        assertEquals(1, result.second)
    }

    data class Person(val age: Byte, val father: Person?)

    @Test
    fun `selfOrNull works correctly with recursion`() {
        val decoder = composedDecoder {
            val age = byte()
            val father = selfOrNull()
            Person(age, father)
        }

        val bytes = byteArrayOf(1, 1, 5, 0)
        val result = decoder.decode(bytes).assertDoneAndGet()
        assertEquals(1, result.age)
        assertNotNull(result.father)
        assertEquals(5, result.father!!.age)
    }

    @Test
    fun `selfOrNull works correctly without recursion`() {
        val decoder = composedDecoder {
            val age = byte()
            val father = selfOrNull()
            Person(age, father)
        }

        val bytes = byteArrayOf(1, 0)
        val result = decoder.decode(bytes).assertDoneAndGet()
        assertEquals(1, result.age)
        assertNull(result.father)
    }

    data class Person1(val age: Byte, val children: List<Person1>)

    @Test
    fun `selfCollection works correctly with recursion`() {
        val decoder = composedDecoder<Person1> {
            val age = byte()
            val children = selfCollection(Collectors.toList())
            Person1(age, children)
        }
        val fatherAge = 34.toByte()
        val child1Age = 8.toByte()
        val child2Age = 3.toByte()
        val bytes = byteArrayOf(fatherAge, 0, 0, 0, 2, child1Age, 0, 0, 0, 0, child2Age, 0, 0, 0, 0)
        val result = decoder.decode(bytes).assertDoneAndGet()
        assertEquals(fatherAge, result.age)
        assertEquals(2, result.children.size)
        assertEquals(child1Age, result.children[0].age)
        assertEquals(child2Age, result.children[1].age)
    }

    @Test
    fun `selfCollection works correctly without recursion`() {
        val decoder = composedDecoder<Person1> {
            val age = byte()
            val children = selfCollection(Collectors.toList())
            Person1(age, children)
        }
        val fatherAge = 34.toByte()
        val bytes = byteArrayOf(fatherAge, 0, 0, 0, 0)
        val result = decoder.decode(bytes).assertDoneAndGet()
        assertEquals(fatherAge, result.age)
        assertEquals(0, result.children.size)
    }

    data class Person2(val age: Byte, val children: List<Person2>?)

    @Test
    fun `selfCollectionOrNull works correctly with recursion`() {
        val decoder = composedDecoder<Person2> {
            val age = byte()
            val children = selfCollectionOrNull(Collectors.toList())
            Person2(age, children)
        }
        val fatherAge = 34.toByte()
        val child1Age = 8.toByte()
        val child2Age = 3.toByte()
        val bytes = byteArrayOf(fatherAge, 1, 0, 0, 0, 2, child1Age, 1, 0, 0, 0, 0, child2Age, 0)
        val result = decoder.decode(bytes).assertDoneAndGet()
        assertEquals(fatherAge, result.age)
        assertNotNull(result.children)
        assertEquals(2, result.children!!.size)
        assertEquals(child1Age, result.children[0].age)
        assertEquals(child2Age, result.children[1].age)
        assertNotNull(result.children[0].children)
        assertNull(result.children[1].children)
    }

    @Test
    fun `selfCollectionOrNull works correctly without recursion`() {
        val decoder = composedDecoder<Person2> {
            val age = byte()
            val children = selfCollectionOrNull(Collectors.toList())
            Person2(age, children)
        }
        val fatherAge = 34.toByte()
        val bytes = byteArrayOf(fatherAge, 0)
        val result = decoder.decode(bytes).assertDoneAndGet()
        assertEquals(fatherAge, result.age)
        assertNull(result.children)
    }



}
