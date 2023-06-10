package com.kamelia.sprinkler.transcoder.binary.decoder.composer

import com.kamelia.sprinkler.transcoder.binary.decoder.BooleanDecoder
import com.kamelia.sprinkler.transcoder.binary.decoder.util.assertDoneAndGet
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DecodingScopeTest {

    data class Person1(val age: Byte, val children: List<Person1>)

    @Test
    fun `selfList works correctly with recursion`() {
        val decoder = composedDecoder {
            val age = byte()
            val children = selfList()
            Person1(age, children)
        }
        val bytes = byteArrayOf(34, 0, 0, 0, 1, 3, 0, 0, 0, 0)
        val result = decoder.decode(bytes).assertDoneAndGet()
        assertEquals(34, result.age)
        assertEquals(1, result.children.size)
        assertEquals(3, result.children[0].age)
    }

    @Test
    fun `selfList works correctly without recursion`() {
        val decoder = composedDecoder {
            val age = byte()
            val children = selfList()
            Person1(age, children)
        }
        val bytes = byteArrayOf(34, 0, 0, 0, 0)
        val result = decoder.decode(bytes).assertDoneAndGet()
        assertEquals(34, result.age)
        assertEquals(0, result.children.size)
    }

    data class Person2(val age: Byte, val children: Set<Person2>)

    @Test
    fun `selfSet works correctly with recursion`() {
        val decoder = composedDecoder {
            val age = byte()
            val children = selfSet()
            Person2(age, children)
        }
        val bytes = byteArrayOf(34, 0, 0, 0, 1, 3, 0, 0, 0, 0)
        val result = decoder.decode(bytes).assertDoneAndGet()
        assertEquals(34, result.age)
        assertEquals(1, result.children.size)
        assertEquals(3, result.children.first().age)
    }

    @Test
    fun `selfSet works correctly without recursion`() {
        val decoder = composedDecoder {
            val age = byte()
            val children = selfSet()
            Person2(age, children)
        }
        val bytes = byteArrayOf(34, 0, 0, 0, 0)
        val result = decoder.decode(bytes).assertDoneAndGet()
        assertEquals(34, result.age)
        assertEquals(0, result.children.size)
    }

    data class Person3(val age: Byte, val children: Array<Person3>)

    @Test
    fun `selfArray works correctly with recursion`() {
        val decoder = composedDecoder {
            val age = byte()
            val children = selfArray(::arrayOfNulls)
            Person3(age, children)
        }
        val bytes = byteArrayOf(34, 0, 0, 0, 1, 3, 0, 0, 0, 0)
        val result = decoder.decode(bytes).assertDoneAndGet()
        assertEquals(34, result.age)
        assertEquals(1, result.children.size)
        assertEquals(3, result.children[0].age)
    }

    @Test
    fun `selfArray works correctly without recursion`() {
        val decoder = composedDecoder {
            val age = byte()
            val children = selfArray(::arrayOfNulls)
            Person3(age, children)
        }
        val bytes = byteArrayOf(34, 0, 0, 0, 0)
        val result = decoder.decode(bytes).assertDoneAndGet()
        assertEquals(34, result.age)
        assertEquals(0, result.children.size)
    }

    data class Person4(val age: Byte, val children: List<Person4>?)

    @Test
    fun `selfListOrNull works correctly with recursion`() {
        val decoder = composedDecoder {
            val age = byte()
            val children = selfListOrNull()
            Person4(age, children)
        }
        val bytes = byteArrayOf(34, 1, 0, 0, 0, 1, 3, 1, 0, 0, 0, 0)
        val result = decoder.decode(bytes).assertDoneAndGet()
        assertEquals(34, result.age)
        assertEquals(1, result.children?.size)
        assertEquals(3, result.children?.get(0)?.age)
    }

    @Test
    fun `selfListOrNull works correctly without recursion`() {
        val decoder = composedDecoder {
            val age = byte()
            val children = selfListOrNull()
            Person4(age, children)
        }
        val bytes = byteArrayOf(34, 0)
        val result = decoder.decode(bytes).assertDoneAndGet()
        assertEquals(34, result.age)
        assertEquals(null, result.children)
    }

    @Test
    fun `selfListOrNull works correctly with recursion of size 0`() {
        val recursionDecoder = BooleanDecoder()
        val decoder = composedDecoder {
            val age = byte()
            val children = selfListOrNull()
            Person4(age, children)
        }
        val bytes = byteArrayOf(34, 1, 0, 0, 0, 0)
        val result = decoder.decode(bytes).assertDoneAndGet()
        assertEquals(34, result.age)
        assertEquals(0, result.children?.size)
    }

    data class Person5(val age: Byte, val children: Set<Person5>?)

    @Test
    fun `selfSetOrNull works correctly with recursion`() {
        val decoder = composedDecoder {
            val age = byte()
            val children = selfSetOrNull()
            Person5(age, children)
        }
        val bytes = byteArrayOf(34, 1, 0, 0, 0, 1, 3, 1, 0, 0, 0, 0)
        val result = decoder.decode(bytes).assertDoneAndGet()
        assertEquals(34, result.age)
        assertEquals(1, result.children?.size)
        assertEquals(3, result.children?.first()?.age)
    }

    @Test
    fun `selfSetOrNull works correctly without recursion`() {
        val decoder = composedDecoder {
            val age = byte()
            val children = selfSetOrNull()
            Person5(age, children)
        }
        val bytes = byteArrayOf(34, 0)
        val result = decoder.decode(bytes).assertDoneAndGet()
        assertEquals(34, result.age)
        assertEquals(null, result.children)
    }

    @Test
    fun `selfSetOrNull works correctly with recursion of size 0`() {
        val decoder = composedDecoder {
            val age = byte()
            val children = selfSetOrNull()
            Person5(age, children)
        }
        val bytes = byteArrayOf(34, 1, 0, 0, 0, 0)
        val result = decoder.decode(bytes).assertDoneAndGet()
        assertEquals(34, result.age)
        assertEquals(0, result.children?.size)
    }

    data class Person6(val age: Byte, val children: Array<Person6>?)

    @Test
    fun `selfArrayOrNull works correctly with recursion`() {
        val decoder = composedDecoder {
            val age = byte()
            val children = selfArrayOrNull(::arrayOfNulls)
            Person6(age, children)
        }
        val bytes = byteArrayOf(34, 1, 0, 0, 0, 1, 3, 1, 0, 0, 0, 0)
        val result = decoder.decode(bytes).assertDoneAndGet()
        assertEquals(34, result.age)
        assertEquals(1, result.children?.size)
        assertEquals(3, result.children?.get(0)?.age)
    }

    @Test
    fun `selfArrayOrNull works correctly without recursion`() {
        val decoder = composedDecoder {
            val age = byte()
            val children = selfArrayOrNull(::arrayOfNulls)
            Person6(age, children)
        }
        val bytes = byteArrayOf(34, 0)
        val result = decoder.decode(bytes).assertDoneAndGet()
        assertEquals(34, result.age)
        assertEquals(null, result.children)
    }

    @Test
    fun `selfArrayOrNull works correctly with recursion of size 0`() {
        val decoder = composedDecoder {
            val age = byte()
            val children = selfArrayOrNull(::arrayOfNulls)
            Person6(age, children)
        }
        val bytes = byteArrayOf(34, 1, 0, 0, 0, 0)
        val result = decoder.decode(bytes).assertDoneAndGet()
        assertEquals(34, result.age)
        assertEquals(0, result.children?.size)
    }

}
