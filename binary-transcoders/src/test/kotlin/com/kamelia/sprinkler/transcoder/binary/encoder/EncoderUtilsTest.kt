package com.kamelia.sprinkler.transcoder.binary.encoder

import com.kamelia.sprinkler.util.readInt
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EncoderUtilsTest {

    @Test
    fun `with mapped input works correctly`() {
        val intEncoder = IntEncoder()
        val strLenEncoder = intEncoder.withMappedInput(String::length)

        val str = "Hello"
        val array = strLenEncoder.encode(str)

        assertEquals(str.length, array.readInt())
    }

    @Test
    fun `toIterable works correctly`() {
        val byteEncoder = ByteEncoder()
        val iterableEncoder = byteEncoder.toIterable(0)

        val list = listOf<Byte>(1, 2, 3)
        val array = iterableEncoder.encode(list)

        assertEquals(list, array.slice(0 until array.size - 1))
        assertEquals(0, array.last())
    }

    @Test
    fun `toCollection works correctly`() {
        val byteEncoder = ByteEncoder()
        val collectionEncoder = byteEncoder.toCollection()

        val list = listOf<Byte>(1, 2, 3)
        val array = collectionEncoder.encode(list)

        assertEquals(list.size, array.readInt())
        assertEquals(list, array.slice(4 until array.size))
    }

    @Test
    fun `toMap with two encoders works correctly`() {
        val byteEncoder = ByteEncoder()
        val mapEncoder = byteEncoder.toMap(byteEncoder)

        val map = mapOf<Byte, Byte>(1.toByte() to 2, 3.toByte() to 4)
        val array = mapEncoder.encode(map)

        assertEquals(map.size, array.readInt())
        assertEquals(map.keys.toList(), array.slice(4 until array.size step 2))
        assertEquals(map.values.toList(), array.slice(5 until array.size step 2))
    }

    @Test
    fun `toMap with pair encoder works correctly`() {
        val byteEncoder = ByteEncoder()
        val mapEncoder = PairEncoder(byteEncoder, byteEncoder).toMap()

        val map = mapOf<Byte, Byte>(1.toByte() to 2, 3.toByte() to 4)
        val array = mapEncoder.encode(map)

        assertEquals(map.size, array.readInt())
        assertEquals(map.keys.toList(), array.slice(4 until array.size step 2))
        assertEquals(map.values.toList(), array.slice(5 until array.size step 2))
    }

    @Test
    fun `toMap with two encoders works correctly with end marker`() {
        val byteEncoder = ByteEncoder()
        val mapEncoder = byteEncoder.toMap(byteEncoder, 0.toByte() to 0)

        val map = mapOf<Byte, Byte>(1.toByte() to 2, 3.toByte() to 4)
        val array = mapEncoder.encode(map)

        assertEquals(map.keys.toList(), array.slice(0 until array.size - 2 step 2))
        assertEquals(map.values.toList(), array.slice(1 until array.size - 2 step 2))
        assertEquals(0, array[array.size - 2])
        assertEquals(0, array.last())
    }

    @Test
    fun `toMap with pair encoder works correctly with end marker`() {
        val byteEncoder = ByteEncoder()
        val mapEncoder = (byteEncoder and byteEncoder).toMap(0.toByte() to 0)

        val map = mapOf<Byte, Byte>(1.toByte() to 2, 3.toByte() to 4)
        val array = mapEncoder.encode(map)

        assertEquals(map.keys.toList(), array.slice(0 until array.size - 2 step 2))
        assertEquals(map.values.toList(), array.slice(1 until array.size - 2 step 2))
        assertEquals(0, array[array.size - 2])
        assertEquals(0, array.last())
    }

    @Test
    fun `toArray works correctly`() {
        val byteEncoder = ByteEncoder()
        val arrayEncoder = byteEncoder.toArray()

        val input = arrayOf<Byte>(1, 2, 3)
        val array = arrayEncoder.encode(input)

        assertEquals(input.size, array.readInt())
        assertArrayEquals(input, array.sliceArray(Int.SIZE_BYTES until array.size).toTypedArray())
    }

    @Test
    fun `toArray works correctly with end marker`() {
        val byteEncoder = ByteEncoder()
        val arrayEncoder = byteEncoder.toArray(0)

        val input = arrayOf<Byte>(1, 2, 3)
        val array = arrayEncoder.encode(input)

        assertEquals(input.size, array.size - 1)
        assertArrayEquals(input, array.sliceArray(0 until array.size - 1).toTypedArray())
        assertEquals(0, array.last())
    }

    @Test
    fun `toOptional works correctly`() {
        val byteEncoder = ByteEncoder()
        val optionalEncoder = byteEncoder.toOptional()

        val input = 2.toByte()
        val array = optionalEncoder.encode(input)

        assertEquals(2, array.size)
        assertEquals(1, array[0])
        assertEquals(input, array[1])
    }

    @Test
    fun `toOptional works correctly with null`() {
        val byteEncoder = ByteEncoder()
        val optionalEncoder = byteEncoder.toOptional()

        val input: Byte? = null
        val array = optionalEncoder.encode(input)

        assertEquals(1, array.size)
        assertEquals(0, array[0])
    }

}
