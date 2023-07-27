package com.kamelia.sprinkler.collection.readonly

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class IterableTest {

    @Test
    fun `read only iterable cannot be cast to mutable iterable`() {
        val readOnlyIterable = listOf(1, 2, 3).asReadOnlyIterable()

        @Suppress("UNCHECKED_CAST")
        assertThrows<ClassCastException> { readOnlyIterable as MutableIterable<Int> }
    }

    @Test
    fun `asReadOnlyIterable simply wraps the original iterable`() {
        val iterable = listOf(1, 2, 3)
        val readOnlyIterable = iterable.asReadOnlyIterable()

        assertTrue(readOnlyIterable == iterable)
    }

    @Test
    fun `asReadOnlyIterable does not wrap the original iterable if it is already read only`() {
        val iterable = listOf(1, 2, 3).asReadOnlyIterable()
        val readOnlyIterable = iterable.asReadOnlyIterable()

        assertTrue(readOnlyIterable === iterable)
    }

    @Test
    fun `readOnlyIterator methods does not wrap the original iterator if the iterable is already read only`() {
        val list = readOnlyListOf(1, 2, 3).subList(0, 3)
        val iterable = list as ReadOnlyIterable<Int>

        val listIterator = list.iterator()
        val iterableIterator = iterable.readOnlyIterator()

        assertTrue(listIterator.javaClass == iterableIterator.javaClass)
    }

    @Test
    fun `read only iterable correctly delegates methods from any to the inner iterable`() {
        val list1 = listOf(1, 5, 7)

        val iterable1 = list1.asReadOnlyIterable()
        val iterable2 = listOf(24).asReadOnlyIterable()

        assertEquals(list1.hashCode(), iterable1.hashCode())
        assertEquals(list1.toString(), iterable1.toString())
        assertNotEquals(list1.hashCode(), iterable2.hashCode())
        assertNotEquals(list1.toString(), iterable2.toString())
        assertNotEquals(iterable1, iterable2)
        assertEquals(iterable1, list1)
        assertNotEquals(iterable2, list1)
        assertNotEquals(list1, iterable1) // false because list equals expects a list
        iterable1.iterator()
    }

    @Test
    fun `read only iterable iterator cannot be cast`() {
        val iterable = listOf(1, 2, 3).asReadOnlyIterable()

        @Suppress("UNCHECKED_CAST")
        assertThrows<ClassCastException> { iterable.iterator() as MutableIterator<Int> }
    }

}
