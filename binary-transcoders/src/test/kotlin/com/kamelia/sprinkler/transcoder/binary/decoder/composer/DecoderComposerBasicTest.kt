package com.kamelia.sprinkler.transcoder.binary.decoder.composer

import com.kamelia.sprinkler.transcoder.binary.decoder.ShortDecoder
import com.kamelia.sprinkler.transcoder.binary.decoder.core.Decoder
import com.kamelia.sprinkler.transcoder.binary.decoder.core.NothingDecoder
import com.kamelia.sprinkler.transcoder.binary.decoder.util.assertDoneAndGet
import com.kamelia.sprinkler.util.byte
import java.nio.ByteOrder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class DecoderComposerBasicTest {

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
    fun `selfList works correctly with recursion`() {
        val decoder = composedDecoder {
            val age = byte()
            val children = selfList()
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

}
