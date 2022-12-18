package com.kamelia.sprinkler.collection.readonly

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SetTest {

    @Test
    fun `read only set cannot be casted to mutable set`() {
        val readOnlySet = readOnlySetOf(1, 2, 3)

        @Suppress("UNCHECKED_CAST")
        assertThrows<ClassCastException> { readOnlySet as MutableSet<Int> }
    }

    @Test
    fun `read only set iterator cannot be casted to mutable iterator`() {
        val readOnlySet = readOnlySetOf(1, 2, 3)
        val iterator = readOnlySet.iterator()

        @Suppress("UNCHECKED_CAST")
        assertThrows<ClassCastException> { iterator as MutableIterator<Int> }
    }

    @Test
    fun `methods inherited from Set are delegated to the inner set`() {
        val set = setOf(1, 2, 3)
        val readOnlySet = set.asReadOnlySet()

        assertTrue(readOnlySet.contains(1) == set.contains(1))
        assertTrue(!readOnlySet.contains(5) == !set.contains(5))
        assertTrue(readOnlySet.containsAll(listOf(1, 2)) == set.containsAll(listOf(1, 2)))
        assertTrue(!readOnlySet.containsAll(listOf(1, 5)) == !set.containsAll(listOf(1, 5)))
        assertTrue(!readOnlySet.isEmpty() == !set.isEmpty())
        assertTrue(readOnlySet.size == set.size)
    }

    @Test
    fun `methods inherited from Any are delegated to the inner set`() {
        val set = setOf(1, 2, 3)
        val readOnlySet = set.asReadOnlySet()

        assertTrue(readOnlySet.toString() == set.toString())
        assertTrue(readOnlySet.hashCode() == set.hashCode())
        assertTrue(readOnlySet == set)
    }

    @Test
    fun `toReadOnlySet correctly creates a copy of the original iterable`() {
        val set = mutableSetOf(1, 2, 3)
        val readOnlySet = set.toReadOnlySet()
        set += 5

        assertTrue(set.size == 4)
        assertTrue(readOnlySet.size == 3)
    }

    @Test
    fun `asReadOnlySet simply wraps the original set`() {
        val set = mutableSetOf(1, 2, 3)
        val readOnlySet = set.asReadOnlySet()
        set += 5

        assertTrue(set !== readOnlySet)
        assertTrue(set.size == 4)
        assertTrue(readOnlySet.size == 4)
    }

    @Test
    fun `asReadOnlySet does not wrap the original set if it is already read only`() {
        val set = setOf(1, 2, 3).asReadOnlySet()
        val readOnlySet = set.asReadOnlySet()

        assertTrue(set === readOnlySet)
    }

    @Test
    fun `Array#toReadOnlySet creates a set that is independent of the original array`() {
        val array = arrayOf(1, 2, 3)
        val readOnlySet = array.toReadOnlySet()
        array[0] = 5

        assertTrue(array[0] == 5)
        assertTrue(readOnlySet.size == 3)
        assertTrue(5 !in readOnlySet)
    }

}
