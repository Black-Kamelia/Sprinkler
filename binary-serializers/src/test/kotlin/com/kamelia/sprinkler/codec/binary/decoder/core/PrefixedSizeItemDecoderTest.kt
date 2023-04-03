package com.kamelia.sprinkler.codec.binary.decoder.core

import com.kamelia.sprinkler.codec.binary.decoder.util.assertDoneAndGet
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test

class PrefixedSizeItemDecoderTest {

    @Test
    fun `basic test works correctly`() {
        val decoder = PrefixedSizeItemDecoder(ConstantSizedItemDecoder(1) { get(0) }) {
            String(this, 0, it, Charsets.US_ASCII)
        }

        val value = "Hello World"
        val array = value.toByteArray(Charsets.US_ASCII)
        val data = byteArrayOf(array.size.toByte()) + array

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(value, result)
    }

    @Test
    fun `negative size returns an error state`() {
        val decoder = PrefixedSizeItemDecoder(ConstantSizedItemDecoder(1) { get(0) }) {
            String(this, 0, it, Charsets.US_ASCII)
        }

        val data = byteArrayOf(-1)
        val result = decoder.decode(data)
        assertInstanceOf(Decoder.State.Error::class.java, result)
    }

    @Test
    fun `can decode in several times`() {
        val decoder = PrefixedSizeItemDecoder(ConstantSizedItemDecoder(1) { get(0) }) {
            String(this, 0, it, Charsets.US_ASCII)
        }

        val pre = "Hello World"
        val post = "!"
        val value = pre + post

        val preArray = pre.toByteArray(Charsets.US_ASCII)
        val postArray = post.toByteArray(Charsets.US_ASCII)
        val preData = byteArrayOf((preArray.size + postArray.size).toByte()) + preArray

        val processing = decoder.decode(byteArrayOf())
        assertInstanceOf(Decoder.State.Processing::class.java, processing)

        val processing1 = decoder.decode(preData)
        assertInstanceOf(Decoder.State.Processing::class.java, processing1)

        val result = decoder.decode(post.toByteArray(Charsets.US_ASCII)).assertDoneAndGet()
        assertEquals(value, result)
    }

    @Test
    fun `can read a size of 0`() {
        val decoder = PrefixedSizeItemDecoder(ConstantSizedItemDecoder(1) { get(0) }) {
            String(this, 0, it, Charsets.US_ASCII)
        }

        val data = byteArrayOf(0)
        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals("", result)
    }

    @Test
    fun `reset works correctly`() {
        val decoder = PrefixedSizeItemDecoder(ConstantSizedItemDecoder(1) { get(0) }) {
            String(this, 0, it, Charsets.US_ASCII)
        }

        val value = "a"
        val array = value.toByteArray(Charsets.US_ASCII)
        val data = byteArrayOf(array.size.toByte()) + array

        val processing = decoder.decode(byteArrayOf(5, 1))
        assertInstanceOf(Decoder.State.Processing::class.java, processing)

        decoder.reset()

        val done = decoder.decode(data).assertDoneAndGet()
        assertEquals(value, done)
    }

    @Test
    fun `can decode elements with a length inferior to the array size`() {
        val decoder = PrefixedSizeItemDecoder(ConstantSizedItemDecoder(1) { get(0) }) {
            String(this, 0, it, Charsets.US_ASCII)
        }

        val first = "aaa"
        val second = "bb" // smaller than the array size
        val third = "cccc" // resizes the array
        val fourth = "d" // smaller than the array size

        val firstArray = first.toByteArray(Charsets.US_ASCII)
        val secondArray = second.toByteArray(Charsets.US_ASCII)
        val thirdArray = third.toByteArray(Charsets.US_ASCII)
        val fourthArray = fourth.toByteArray(Charsets.US_ASCII)

        val firstData = byteArrayOf(firstArray.size.toByte()) + firstArray
        val secondData = byteArrayOf(secondArray.size.toByte()) + secondArray
        val thirdData = byteArrayOf(thirdArray.size.toByte()) + thirdArray
        val fourthData = byteArrayOf(fourthArray.size.toByte()) + fourthArray

        val firstResult = decoder.decode(firstData).assertDoneAndGet()
        assertEquals(first, firstResult)

        val secondResult = decoder.decode(secondData).assertDoneAndGet()
        assertEquals(second, secondResult)

        val thirdResult = decoder.decode(thirdData).assertDoneAndGet()
        assertEquals(third, thirdResult)

        val fourthResult = decoder.decode(fourthData).assertDoneAndGet()
        assertEquals(fourth, fourthResult)
    }

}
