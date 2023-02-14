package com.kamelia.sprinkler.binary.decoder.core

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DecoderCollectorTest {

    @Test
    fun `to list works correctly`() {
        val collector = DecoderCollector.toList<Int>()

        val list = collector.supplier()

        collector.accumulator(list, 1, 0)
        collector.accumulator(list, 2, 1)

        val result = collector.finisher(list)

        assertEquals(listOf(1, 2), result)
    }

    @Test
    fun `to set works correctly`() {
        val collector = DecoderCollector.toSet<Int>()

        val set = collector.supplier()

        collector.accumulator(set, 1, 0)
        collector.accumulator(set, 2, 1)

        val result = collector.finisher(set)

        assertEquals(setOf(1, 2), result)
    }

    @Test
    fun `to map works correctly`() {
        val collector = DecoderCollector.toMap<Int, String>()

        val map = collector.supplier()

        collector.accumulator(map, 1 to "one", 0)
        collector.accumulator(map, 2 to "two", 1)

        val result = collector.finisher(map)

        assertEquals(mapOf(1 to "one", 2 to "two"), result)
    }

    @Test
    fun `to array works correctly`() {
        val collector = DecoderCollector.toArray<Int>(::arrayOfNulls)

        val array = collector.supplier()

        collector.accumulator(array, 1, 0)
        collector.accumulator(array, 2, 1)

        val result = collector.finisher(array)

        assertArrayEquals(arrayOf(1, 2), result)
    }

    @Test
    fun `to collection works correctly`() {
        val collector = DecoderCollector.toCollection<Int> { mutableListOf() }

        val collection = collector.supplier()

        collector.accumulator(collection, 1, 0)
        collector.accumulator(collection, 2, 1)

        val result = collector.finisher(collection)

        assertEquals(mutableListOf(1, 2), result)
    }

}
