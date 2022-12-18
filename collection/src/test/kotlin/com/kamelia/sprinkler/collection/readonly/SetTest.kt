package com.kamelia.sprinkler.collection.readonly

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SetTest {

    @Test
    fun `read only set cannot be casted to mutable set`() {
        val readOnlySet = setOf(1, 2, 3).asReadOnlySet()

        @Suppress("UNCHECKED_CAST")
        assertThrows<ClassCastException> { readOnlySet as MutableSet<Int> }
    }

    @Test
    fun `read only set iterator cannot be casted to mutable iterator`() {
        val readOnlySet = setOf(1, 2, 3).asReadOnlySet()
        val iterator = readOnlySet.iterator()

        @Suppress("UNCHECKED_CAST")
        assertThrows<ClassCastException> { iterator as MutableIterator<Int> }
    }

    @Test
    fun `methods inherited from Set are delegated to the inner set`() {
        val set = setOf(1, 2, 3)
        val readOnlySet = set.asReadOnlySet()

        assert(readOnlySet.contains(1) == set.contains(1))
        assert(!readOnlySet.contains(5) == !set.contains(5))
        assert(readOnlySet.containsAll(listOf(1, 2)) == set.containsAll(listOf(1, 2)))
        assert(!readOnlySet.containsAll(listOf(1, 5)) == !set.containsAll(listOf(1, 5)))
        assert(!readOnlySet.isEmpty() == !set.isEmpty())
        assert(readOnlySet.size == set.size)
    }

    @Test
    fun `methods inherited from Any are delegated to the inner set`() {
        val set = setOf(1, 2, 3)
        val readOnlySet = set.asReadOnlySet()

        assert(readOnlySet.toString() == set.toString())
        assert(readOnlySet.hashCode() == set.hashCode())
        assert(readOnlySet == set)
    }

    @Test
    fun `toReadOnlySet correctly creates a copy of the original iterable`() {
        val set = mutableSetOf(1, 2, 3)
        val readOnlySet = set.toReadOnlySet()
        set += 5

        assert(set.size == 4)
        assert(readOnlySet.size == 3)
    }

    @Test
    fun `asReadOnlySet simply wraps the original set`() {
        val set = mutableSetOf(1, 2, 3)
        val readOnlySet = set.asReadOnlySet()
        set += 5

        assert(set !== readOnlySet)
        assert(set.size == 4)
        assert(readOnlySet.size == 4)
    }

    @Test
    fun `asReadOnlySet does not wrap the original set if it is already read only`() {
        val set = setOf(1, 2, 3).asReadOnlySet()
        val readOnlySet = set.asReadOnlySet()

        assert(set === readOnlySet)
    }

    @Test
    fun `Array#toReadOnlySet creates a set that is independent of the original array`() {
        val array = arrayOf(1, 2, 3)
        val readOnlySet = array.toReadOnlySet()
        array[0] = 5

        assert(array[0] == 5)
        assert(readOnlySet.size == 3)
        assert(5 !in readOnlySet)
    }

}
