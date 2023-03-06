package com.kamelia.sprinkler.binary.decoder

import com.kamelia.sprinkler.binary.decoder.core.Decoder
import com.kamelia.sprinkler.binary.decoder.util.assertDoneAndGet
import com.kamelia.sprinkler.util.byte
import java.util.stream.Collectors
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DecoderUtilsTest {

    @Test
    fun `mapTo works correctly`() {
        val decoder = ByteDecoder().mapTo {
            when (it.toInt()) {
                0 -> ByteDecoder()
                1 -> ShortDecoder()
                2 -> IntDecoder()
                else -> LongDecoder()
            }
        }

        val data = byteArrayOf(1, 0, 7)

        val result = decoder.decode(data).assertDoneAndGet()
        assertInstanceOf(Short::class.javaObjectType, result)
        assertEquals(7.toShort(), result)
    }

    @Test
    fun `mapTo works correctly can decoder in several steps`() {
        val decoder = ByteDecoder().mapTo {
            when (it.toInt()) {
                0 -> ByteDecoder()
                1 -> ShortDecoder()
                2 -> IntDecoder()
                else -> LongDecoder()
            }
        }

        val data = byteArrayOf(1)
        val data2 = byteArrayOf(0, 7)

        val processing = decoder.decode(data)
        assertInstanceOf(Decoder.State.Processing::class.java, processing)

        val result = decoder.decode(data2).assertDoneAndGet()
        assertInstanceOf(Short::class.javaObjectType, result)
        assertEquals(7.toShort(), result)
    }

    @Test
    fun `mapTo reset works correctly`() {
        val decoder = ByteDecoder().mapTo {
            when (it.toInt()) {
                0 -> ByteDecoder()
                1 -> ShortDecoder()
                2 -> IntDecoder()
                else -> LongDecoder()
            }
        }

        val data = byteArrayOf(2)

        val processing = decoder.decode(data)
        assertInstanceOf(Decoder.State.Processing::class.java, processing)

        decoder.reset()

        val data2 = byteArrayOf(1, 0, 7)
        val result = decoder.decode(data2).assertDoneAndGet()
        assertInstanceOf(Short::class.javaObjectType, result)
    }

    @Test
    fun `mapResult works correctly`() {
        val decoder = ByteDecoder().mapResult { it * it }

        val value = 5.toByte()
        val data = byteArrayOf(value)

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(value * value, result)
    }

    @Test
    fun `mapResult reset works correctly`() {
        val decoder = ShortDecoder().mapResult { it * it }

        val processing = decoder.decode(byteArrayOf(1))
        assertInstanceOf(Decoder.State.Processing::class.java, processing)

        decoder.reset()

        val value = 5.toShort()
        val data = byteArrayOf(value.byte(1), value.byte(0))
        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(value * value, result)
    }

    @Test
    fun `mapState works correctly`() {
        val decoder = ByteDecoder().mapState {
            if (it.toInt() == 0) {
                Decoder.State.Error("Cannot invert 0")
            } else {
                Decoder.State.Done(1.0 / it)
            }
        }

        val data = byteArrayOf(0)
        val result = decoder.decode(data)
        assertInstanceOf(Decoder.State.Error::class.java, result)

        val value = 2.toByte()
        val data2 = byteArrayOf(value)
        val result2 = decoder.decode(data2).assertDoneAndGet()
        assertEquals(1.0 / value, result2)
    }

    @Test
    fun `mapState reset works correctly`() {
        val decoder = ShortDecoder().mapState {
            if (it.toInt() == 0) {
                Decoder.State.Error("Cannot invert 0")
            } else {
                Decoder.State.Done(1.0 / it)
            }
        }

        val data = byteArrayOf(1)
        val result = decoder.decode(data)
        assertInstanceOf(Decoder.State.Processing::class.java, result)

        decoder.reset()

        val value = 2.toShort()
        val data2 = byteArrayOf(value.byte(1), value.byte(0))
        val result2 = decoder.decode(data2).assertDoneAndGet()
        assertEquals(1.0 / value, result2)
    }

    @Test
    fun `toOptional works correctly when element is absent`() {
        val decoder = NoOpDecoder().toOptional()

        val data = byteArrayOf(0)
        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(null, result)
    }

    @Test
    fun `toOptional works correctly when element is present`() {
        val decoder = ByteDecoder().toOptional()

        val value = 5.toByte()
        val data = byteArrayOf(1, value)
        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(value, result)
    }

    @Test
    fun `toCollection(sizeDecoder) works correctly`() {
        val decoder = ByteDecoder().toCollection(Collectors.toList())

        val data = byteArrayOf(54, -3)
        val size = data.size
        val array = byteArrayOf(size.byte(3), size.byte(2), size.byte(1), size.byte(0)) + data
        val result = decoder.decode(array).assertDoneAndGet()
        assertEquals(data.toList(), result)
    }

    @Test
    fun `toCollection(size) works correctly`() {
        val decoder = ByteDecoder().toCollection(Collectors.toList(), 2)

        val data = byteArrayOf(54, -3)
        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(data.toList(), result)
    }

    @Test
    fun `toCollection(size) throws on invalid size`() {
        assertThrows<IllegalArgumentException> {
            ByteDecoder().toCollection(Collectors.toList(), -1)
        }
    }

    @Test
    fun `toCollection(predicate) works correctly`() {
        val decoder = ByteDecoder().toCollection(Collectors.toList()) { it == 0.toByte() }

        val data = byteArrayOf(54, -3)
        val array = data + 0.toByte()
        val result = decoder.decode(array).assertDoneAndGet()
        assertEquals(data.toList(), result)
    }

    @Test
    fun `toCollection(predicate) with keepLast works correctly`() {
        val decoder = ByteDecoder().toCollection(Collectors.toList(), true) { it == 0.toByte() }

        val data = byteArrayOf(54, -3, 0)
        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(data.toList(), result)
    }

    @Test
    fun `toList(sizeDecoder) works correctly`() {
        val decoder = ByteDecoder().toList()

        val data = listOf<Byte>(54, -3)
        val size = data.size
        val array = byteArrayOf(size.byte(3), size.byte(2), size.byte(1), size.byte(0)) + data
        val result = decoder.decode(array).assertDoneAndGet()
        assertEquals(data, result)
    }

    @Test
    fun `toList(size) works correctly`() {
        val decoder = ByteDecoder().toList(2)

        val data = byteArrayOf(54, -3)
        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(data.toList(), result)
    }

    @Test
    fun `toList(size) throws on invalid size`() {
        assertThrows<IllegalArgumentException> {
            ByteDecoder().toList(-1)
        }
    }

    @Test
    fun `toList(predicate) works correctly`() {
        val decoder = ByteDecoder().toList { it == 0.toByte() }

        val data = byteArrayOf(54, -3)
        val array = data + 0.toByte()
        val result = decoder.decode(array).assertDoneAndGet()
        assertEquals(data.toList(), result)
    }

    @Test
    fun `toList(predicate) with keepLast works correctly`() {
        val decoder = ByteDecoder().toList(true) { it == 0.toByte() }

        val data = byteArrayOf(54, -3, 0)
        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(data.toList(), result)
    }

    @Test
    fun `toSet(sizeDecoder) works correctly`() {
        val decoder = ByteDecoder().toSet()

        val data = listOf<Byte>(54, -3)
        val size = data.size
        val array = byteArrayOf(size.byte(3), size.byte(2), size.byte(1), size.byte(0)) + data
        val result = decoder.decode(array).assertDoneAndGet()
        assertEquals(data.toSet(), result)
    }

    @Test
    fun `toSet(size) works correctly`() {
        val decoder = ByteDecoder().toSet(2)

        val data = byteArrayOf(54, -3)
        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(data.toSet(), result)
    }

    @Test
    fun `toSet(size) throws on invalid size`() {
        assertThrows<IllegalArgumentException> {
            ByteDecoder().toSet(-1)
        }
    }

    @Test
    fun `toSet(predicate) works correctly`() {
        val decoder = ByteDecoder().toSet { it == 0.toByte() }

        val data = byteArrayOf(54, -3)
        val array = data + 0.toByte()
        val result = decoder.decode(array).assertDoneAndGet()
        assertEquals(data.toSet(), result)
    }

    @Test
    fun `toSet(predicate) with keepLast works correctly`() {
        val decoder = ByteDecoder().toSet(true) { it == 0.toByte() }

        val data = byteArrayOf(54, -3, 0)
        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(data.toSet(), result)
    }

    @Test
    fun `toMap(sizeDecoder) works correctly`() {
        val decoder = (ByteDecoder() and ByteDecoder()).toMap()

        val data = mapOf(54.toByte() to (-3).toByte(), 0.toByte() to 1.toByte())
        val size = data.size
        val array = byteArrayOf(size.byte(3), size.byte(2), size.byte(1), size.byte(0)) +
                data.flatMap { listOf(it.key, it.value) }
        val result = decoder.decode(array).assertDoneAndGet()
        assertEquals(data.toMap(), result)
    }

    @Test
    fun `toMap(size) works correctly with size`() {
        val decoder = (ByteDecoder() and ByteDecoder()).toMap(2)

        val data = mapOf(54.toByte() to (-3).toByte(), 0.toByte() to 1.toByte())
        val array = data.flatMap { listOf(it.key, it.value) }.toByteArray()
        val result = decoder.decode(array).assertDoneAndGet()
        assertEquals(data.toMap(), result)
    }

    @Test
    fun `toMap(size) throws on invalid size`() {
        assertThrows<IllegalArgumentException> {
            (ByteDecoder() and ByteDecoder()).toMap(-1)
        }
    }

    @Test
    fun `toMap(predicate) works correctly`() {
        val decoder = (ByteDecoder() and ByteDecoder()).toMap { it.first == 0.toByte() }

        val data = mapOf(54.toByte() to (-3).toByte(), 5.toByte() to 1.toByte())
        val array = data.flatMap { listOf(it.key, it.value) }.toByteArray() + byteArrayOf(0, 0)
        val result = decoder.decode(array).assertDoneAndGet()
        assertEquals(data.toMap(), result)
    }

    @Test
    fun `toMap(predicate) with keepLast works correctly`() {
        val decoder = (ByteDecoder() and ByteDecoder()).toMap(true) { it.first == 0.toByte() }

        val data = mapOf(54.toByte() to (-3).toByte(), 5.toByte() to 1.toByte(), 0.toByte() to 0.toByte())
        val array = data.flatMap { listOf(it.key, it.value) }.toByteArray()
        val result = decoder.decode(array).assertDoneAndGet()
        assertEquals(data.toMap(), result)
    }

    @Test
    fun `toArray(sizeDecoder) works correctly`() {
        val decoder = ByteDecoder().toArray(::arrayOfNulls)

        val data = listOf<Byte>(54, -3)
        val size = data.size
        val array = byteArrayOf(size.byte(3), size.byte(2), size.byte(1), size.byte(0)) + data
        val result = decoder.decode(array).assertDoneAndGet()
        assertArrayEquals(data.toTypedArray(), result)
    }

    @Test
    fun `toArray(size) works correctly`() {
        val decoder = ByteDecoder().toArray(::arrayOfNulls, 2)

        val data = byteArrayOf(54, -3)
        val result = decoder.decode(data).assertDoneAndGet()
        assertArrayEquals(data.toTypedArray(), result)
    }

    @Test
    fun `toArray(size) throws on invalid size`() {
        assertThrows<IllegalArgumentException> {
            ByteDecoder().toArray(::arrayOfNulls, -1)
        }
    }

    @Test
    fun `toArray(predicate) works correctly`() {
        val decoder = ByteDecoder().toArray(::arrayOfNulls) { it == 0.toByte() }

        val data = byteArrayOf(54, -3)
        val array = data + 0.toByte()
        val result = decoder.decode(array).assertDoneAndGet()
        assertArrayEquals(data.toTypedArray(), result)
    }

}
