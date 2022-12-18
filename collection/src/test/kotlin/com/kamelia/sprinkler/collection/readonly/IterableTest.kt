package com.kamelia.sprinkler.collection.readonly

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class IterableTest {

    @Test
    fun `read only iterable cannot be casted to mutable iterable`() {
        val readOnlyIterable = listOf(1, 2, 3).asReadOnlyIterable()

        @Suppress("UNCHECKED_CAST")
        assertThrows<ClassCastException> { readOnlyIterable as MutableIterable<Int> }
    }

    @Test
    fun `asReadOnlyIterable simply wraps the original iterable`() {
        val iterable = listOf(1, 2, 3)
        val readOnlyIterable = iterable.asReadOnlyIterable()

        assert(readOnlyIterable == iterable)
    }

    @Test
    fun `asReadOnlyIterable does not wrap the original iterable if it is already read only`() {
        val iterable = listOf(1, 2, 3).asReadOnlyIterable()
        val readOnlyIterable = iterable.asReadOnlyIterable()

        assert(readOnlyIterable === iterable)
    }

    @Test
    fun `readOnlyIterator methods does not wrap the original iterator if the iterable is already read only`() {
        val list = readOnlyListOf(1, 2, 3).subList(0, 3)
        val iterable = list as ReadOnlyIterable<Int>

        val listIterator = list.iterator()
        val iterableIterator = iterable.readOnlyIterator()

        assert(listIterator.javaClass == iterableIterator.javaClass)
    }

}
