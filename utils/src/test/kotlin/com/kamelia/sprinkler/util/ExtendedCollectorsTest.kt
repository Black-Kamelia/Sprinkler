package com.kamelia.sprinkler.util

import java.util.function.BiConsumer
import java.util.function.BiFunction
import java.util.function.Function
import java.util.function.Supplier
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@Suppress("UNCHECKED_CAST")
class ExtendedCollectorsTest {

    @Test
    fun `toMap works correctly`() {
        val collector = ExtendedCollectors.toMap<String, Int>()
        val p1 = "one" to 1
        val p2 = "two" to 2
        val p3 = "three" to 3
        val expected = mapOf(p1, p2, p3)
        val actual = listOf(p1, p2, p3).stream().collect(collector)
        assertEquals(expected, actual)
    }

    @Test
    fun `toArray works correctly`() {
        val collector = ExtendedCollectors.toArray<Int>(::arrayOfNulls)
        val expected = intArrayOf(1, 2, 3)
        val actual = listOf(1, 2, 3).stream().collect(collector)
        assertEquals(expected.toList(), actual.toList())
    }

    @Test
    fun `toIntArray works correctly`() {
        val collector = ExtendedCollectors.toIntArray()
        val expected = intArrayOf(1, 2, 3)
        val actual = listOf(1, 2, 3).stream().collect(collector)
        assertEquals(expected.toList(), actual.toList())
    }

    @Test
    fun `toLongArray works correctly`() {
        val collector = ExtendedCollectors.toLongArray()
        val expected = longArrayOf(1, 2, 3)
        val actual = listOf(1L, 2L, 3L).stream().collect(collector)
        assertEquals(expected.toList(), actual.toList())
    }

    @Test
    fun `toShortArray works correctly`() {
        val collector = ExtendedCollectors.toShortArray()
        val expected = shortArrayOf(1, 2, 3)
        val actual = listOf(1.toShort(), 2.toShort(), 3.toShort()).stream().collect(collector)
        assertEquals(expected.toList(), actual.toList())
    }

    @Test
    fun `toByteArray works correctly`() {
        val collector = ExtendedCollectors.toByteArray()
        val expected = byteArrayOf(1, 2, 3)
        val actual = listOf(1.toByte(), 2.toByte(), 3.toByte()).stream().collect(collector)
        assertEquals(expected.toList(), actual.toList())
    }

    @Test
    fun `toFloatArray works correctly`() {
        val collector = ExtendedCollectors.toFloatArray()
        val expected = floatArrayOf(1f, 2f, 3f)
        val actual = listOf(1f, 2f, 3f).stream().collect(collector)
        assertEquals(expected.toList(), actual.toList())
    }

    @Test
    fun `toDoubleArray works correctly`() {
        val collector = ExtendedCollectors.toDoubleArray()
        val expected = doubleArrayOf(1.0, 2.0, 3.0)
        val actual = listOf(1.0, 2.0, 3.0).stream().collect(collector)
        assertEquals(expected.toList(), actual.toList())
    }

    @Test
    fun `toCharArray works correctly`() {
        val collector = ExtendedCollectors.toCharArray()
        val expected = charArrayOf('a', 'b', 'c')
        val actual = listOf('a', 'b', 'c').stream().collect(collector)
        assertEquals(expected.toList(), actual.toList())
    }

    @Test
    fun `toBooleanArray works correctly`() {
        val collector = ExtendedCollectors.toBooleanArray()
        val expected = booleanArrayOf(true, false, true)
        val actual = listOf(true, false, true).stream().collect(collector)
        assertEquals(expected.toList(), actual.toList())
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun `toMap combiner works correctly`() {
        val collector = ExtendedCollectors.toMap<String, Int>()
        val p1 = "one" to 1
        val p2 = "two" to 2
        val p3 = "three" to 3

        val expected = mapOf(p1, p2, p3)
        val l1 = listOf(p1, p2)
        val l2 = listOf(p3)

        val supplier = collector.supplier() as Supplier<MutableMap<String, Int>>
        val accumulator = collector.accumulator() as BiConsumer<MutableMap<String, Int>, Pair<String, Int>>
        val combiner =
            collector.combiner() as BiFunction<MutableMap<String, Int>, MutableMap<String, Int>, Map<String, Int>>
        val finisher = collector.finisher() as Function<MutableMap<String, Int>, Map<String, Int>>

        val a = supplier.get()
        val b = supplier.get()
        l1.forEach { accumulator.accept(a, it) }
        l2.forEach { accumulator.accept(b, it) }

        combiner.apply(a, b)
        val actual = finisher.apply(a)
        assertEquals(expected, actual)
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun `toArray combiner works correctly`() {
        val collector = ExtendedCollectors.toArray<Int>(::arrayOfNulls)
        val expected = intArrayOf(1, 2, 3)
        val l1 = listOf(1, 2)
        val l2 = listOf(3)

        val supplier = collector.supplier() as Supplier<MutableList<Int>>
        val accumulator = collector.accumulator() as BiConsumer<MutableList<Int>, Int>
        val combiner = collector.combiner() as BiFunction<MutableList<Int>, MutableList<Int>, List<Int>>
        val finisher = collector.finisher() as Function<MutableList<Int>, Array<Int>>

        val a = supplier.get()
        val b = supplier.get()
        l1.forEach { accumulator.accept(a, it) }
        l2.forEach { accumulator.accept(b, it) }

        combiner.apply(a, b)
        val actual = finisher.apply(a)
        assertEquals(expected.toList(), actual.toList())
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun `toIntArray combiner works correctly`() {
        val collector = ExtendedCollectors.toIntArray()
        val expected = intArrayOf(1, 2, 3)
        val l1 = listOf(1, 2)
        val l2 = listOf(3)

        val supplier = collector.supplier() as Supplier<MutableList<Int>>
        val accumulator = collector.accumulator() as BiConsumer<MutableList<Int>, Int>
        val combiner = collector.combiner() as BiFunction<MutableList<Int>, MutableList<Int>, List<Int>>
        val finisher = collector.finisher() as Function<MutableList<Int>, IntArray>

        val a = supplier.get()
        val b = supplier.get()
        l1.forEach { accumulator.accept(a, it) }
        l2.forEach { accumulator.accept(b, it) }

        combiner.apply(a, b)
        val actual = finisher.apply(a)
        assertEquals(expected.toList(), actual.toList())
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun `toLongArray combiner works correctly`() {
        val collector = ExtendedCollectors.toLongArray()
        val expected = longArrayOf(1, 2, 3)
        val l1 = listOf(1L, 2L)
        val l2 = listOf(3L)

        val supplier = collector.supplier() as Supplier<MutableList<Long>>
        val accumulator = collector.accumulator() as BiConsumer<MutableList<Long>, Long>
        val combiner = collector.combiner() as BiFunction<MutableList<Long>, MutableList<Long>, List<Long>>
        val finisher = collector.finisher() as Function<MutableList<Long>, LongArray>

        val a = supplier.get()
        val b = supplier.get()
        l1.forEach { accumulator.accept(a, it) }
        l2.forEach { accumulator.accept(b, it) }

        combiner.apply(a, b)
        val actual = finisher.apply(a)
        assertEquals(expected.toList(), actual.toList())
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun `toShortArray combiner works correctly`() {
        val collector = ExtendedCollectors.toShortArray()
        val expected = shortArrayOf(1, 2, 3)
        val l1 = listOf(1.toShort(), 2.toShort())
        val l2 = listOf(3.toShort())

        val supplier = collector.supplier() as Supplier<MutableList<Short>>
        val accumulator = collector.accumulator() as BiConsumer<MutableList<Short>, Short>
        val combiner = collector.combiner() as BiFunction<MutableList<Short>, MutableList<Short>, List<Short>>
        val finisher = collector.finisher() as Function<MutableList<Short>, ShortArray>

        val a = supplier.get()
        val b = supplier.get()
        l1.forEach { accumulator.accept(a, it) }
        l2.forEach { accumulator.accept(b, it) }

        combiner.apply(a, b)
        val actual = finisher.apply(a)
        assertEquals(expected.toList(), actual.toList())
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun `toByteArray combiner works correctly`() {
        val collector = ExtendedCollectors.toByteArray()
        val expected = byteArrayOf(1, 2, 3)
        val l1 = listOf(1.toByte(), 2.toByte())
        val l2 = listOf(3.toByte())

        val supplier = collector.supplier() as Supplier<MutableList<Byte>>
        val accumulator = collector.accumulator() as BiConsumer<MutableList<Byte>, Byte>
        val combiner = collector.combiner() as BiFunction<MutableList<Byte>, MutableList<Byte>, List<Byte>>
        val finisher = collector.finisher() as Function<MutableList<Byte>, ByteArray>

        val a = supplier.get()
        val b = supplier.get()
        l1.forEach { accumulator.accept(a, it) }
        l2.forEach { accumulator.accept(b, it) }

        combiner.apply(a, b)
        val actual = finisher.apply(a)
        assertEquals(expected.toList(), actual.toList())
    }

    @Test
    fun `toFloatArray combiner works correctly`() {
        val collector = ExtendedCollectors.toFloatArray()
        val expected = floatArrayOf(1f, 2f, 3f)
        val l1 = listOf(1f, 2f)
        val l2 = listOf(3f)

        val supplier = collector.supplier() as Supplier<MutableList<Float>>
        val accumulator = collector.accumulator() as BiConsumer<MutableList<Float>, Float>
        val combiner = collector.combiner() as BiFunction<MutableList<Float>, MutableList<Float>, List<Float>>
        val finisher = collector.finisher() as Function<MutableList<Float>, FloatArray>

        val a = supplier.get()
        val b = supplier.get()
        l1.forEach { accumulator.accept(a, it) }
        l2.forEach { accumulator.accept(b, it) }

        combiner.apply(a, b)
        val actual = finisher.apply(a)
        assertEquals(expected.toList(), actual.toList())
    }

    @Test
    fun `toDoubleArray combiner works correctly`() {
        val collector = ExtendedCollectors.toDoubleArray()
        val expected = doubleArrayOf(1.0, 2.0, 3.0)
        val l1 = listOf(1.0, 2.0)
        val l2 = listOf(3.0)

        val supplier = collector.supplier() as Supplier<MutableList<Double>>
        val accumulator = collector.accumulator() as BiConsumer<MutableList<Double>, Double>
        val combiner = collector.combiner() as BiFunction<MutableList<Double>, MutableList<Double>, List<Double>>
        val finisher = collector.finisher() as Function<MutableList<Double>, DoubleArray>

        val a = supplier.get()
        val b = supplier.get()
        l1.forEach { accumulator.accept(a, it) }
        l2.forEach { accumulator.accept(b, it) }

        combiner.apply(a, b)
        val actual = finisher.apply(a)
        assertEquals(expected.toList(), actual.toList())
    }

    @Test
    fun `toCharArray combiner works correctly`() {
        val collector = ExtendedCollectors.toCharArray()
        val expected = charArrayOf('1', '2', '3')
        val l1 = listOf('1', '2')
        val l2 = listOf('3')

        val supplier = collector.supplier() as Supplier<MutableList<Char>>
        val accumulator = collector.accumulator() as BiConsumer<MutableList<Char>, Char>
        val combiner = collector.combiner() as BiFunction<MutableList<Char>, MutableList<Char>, List<Char>>
        val finisher = collector.finisher() as Function<MutableList<Char>, CharArray>

        val a = supplier.get()
        val b = supplier.get()
        l1.forEach { accumulator.accept(a, it) }
        l2.forEach { accumulator.accept(b, it) }

        combiner.apply(a, b)
        val actual = finisher.apply(a)
        assertEquals(expected.toList(), actual.toList())
    }

    @Test
    fun `toBooleanArray combiner works correctly`() {
        val collector = ExtendedCollectors.toBooleanArray()
        val expected = booleanArrayOf(true, false, true)
        val l1 = listOf(true, false)
        val l2 = listOf(true)

        val supplier = collector.supplier() as Supplier<MutableList<Boolean>>
        val accumulator = collector.accumulator() as BiConsumer<MutableList<Boolean>, Boolean>
        val combiner = collector.combiner() as BiFunction<MutableList<Boolean>, MutableList<Boolean>, List<Boolean>>
        val finisher = collector.finisher() as Function<MutableList<Boolean>, BooleanArray>

        val a = supplier.get()
        val b = supplier.get()
        l1.forEach { accumulator.accept(a, it) }
        l2.forEach { accumulator.accept(b, it) }

        combiner.apply(a, b)
        val actual = finisher.apply(a)
        assertEquals(expected.toList(), actual.toList())
    }

}
