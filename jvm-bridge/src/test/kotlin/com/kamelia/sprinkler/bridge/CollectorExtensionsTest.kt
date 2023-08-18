package com.kamelia.sprinkler.bridge

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.stream.Collector
import java.util.stream.Collector.Characteristics

class CollectorExtensionsTest {

    @Test
    fun `basic test`() {
        val collector = toListCollector()

        val collection = collector.supply()

        val expected = listOf(1, 2)
        expected.forEach {
            collector.accumulate(collection, it)
        }

        val result = collector.finish(collection)

        assertEquals(expected, result)
    }

    @Test
    fun `characteristics test`() {
        val collector = toListCollector(Characteristics.UNORDERED)
        assertEquals(setOf(Characteristics.UNORDERED), collector.characteristics)
    }

    @Test
    fun `combiner test`() {
        val collector = toListCollector()

        val collection1 = collector.supply()
        val collection2 = collector.supply()

        val expected1 = listOf(1, 2)
        val expected2 = listOf(3, 4)

        expected1.forEach {
            collector.accumulate(collection1, it)
        }
        expected2.forEach {
            collector.accumulate(collection2, it)
        }

        val combined = collector.combine(collection1, collection2)

        val result = collector.finish(combined)

        assertEquals(expected1 + expected2, result)
    }

    private fun toListCollector(vararg characteristics: Characteristics) = Collector.of<Int?, MutableCollection<Int>?, List<Int>?>(
        ::mutableListOf,
        { c: MutableCollection<Int>, i: Int -> c += i },
        { c1: MutableCollection<Int>, c2: MutableCollection<Int> -> c1.apply { c1 += c2 } },
        MutableCollection<Int>::toList,
        *characteristics,
    )

}
