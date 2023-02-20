package com.kamelia.sprinkler.collection.readonly

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CollectionTest {

    @Test
    fun `read only collection cannot be cast to mutable collection`() {
        val readOnlyCollection = listOf(1, 2, 3).asReadOnlyCollection()

        @Suppress("UNCHECKED_CAST")
        assertThrows<ClassCastException> { readOnlyCollection as MutableCollection<Int> }
    }

    @Test
    fun `read only collection iterator cannot be cast to mutable iterator`() {
        val readOnlyCollection = listOf(1, 2, 3).asReadOnlyCollection()
        val iterator = readOnlyCollection.iterator()

        @Suppress("UNCHECKED_CAST")
        assertThrows<ClassCastException> { iterator as MutableIterator<Int> }
    }

    @Test
    fun `methods inherited from Collection are delegated to the inner collection`() {
        val collection = listOf(1, 2, 3) as Collection<Int>
        val readOnlyCollection = collection.asReadOnlyCollection()

        assertTrue(1 in readOnlyCollection == 1 in collection)
        assertTrue(5 !in readOnlyCollection == 5 !in collection)
        assertTrue(readOnlyCollection.containsAll(listOf(1, 2)) == collection.containsAll(listOf(1, 2)))
        assertTrue(!readOnlyCollection.containsAll(listOf(1, 5)) == !collection.containsAll(listOf(1, 5)))
        assertTrue(!readOnlyCollection.isEmpty() == !collection.isEmpty())
        assertTrue(readOnlyCollection.size == collection.size)
    }

    @Test
    fun `methods inherited from Any are delegated to the inner collection`() {
        val collection = listOf(1, 2, 3) as Collection<Int>
        val readOnlyCollection = collection.asReadOnlyCollection()

        assertTrue(readOnlyCollection.toString() == collection.toString())
        assertTrue(readOnlyCollection.hashCode() == collection.hashCode())
        assertTrue(readOnlyCollection == collection)
    }

    @Test
    fun `toReadOnlyCollection correctly creates a copy of the original iterable`() {
        @Suppress("UNCHECKED_CAST")
        val collection = mutableListOf(1, 2, 3) as MutableCollection<Int>
        val readOnlyCollection = collection.toReadOnlyCollection()
        collection += 5

        assertTrue(collection.size == 4)
        assertTrue(readOnlyCollection.size == 3)
    }

    @Test
    fun `asReadOnlyCollection simply wraps the original collection`() {
        @Suppress("UNCHECKED_CAST")
        val collection = mutableListOf(1, 2, 3) as MutableCollection<Int>
        val readOnlyCollection = collection.asReadOnlyCollection()
        collection += 5

        assertTrue(collection !== readOnlyCollection)
        assertTrue(collection.size == 4)
        assertTrue(readOnlyCollection.size == 4)
    }

    @Test
    fun `asReadOnlyCollection does not wrap the original collection if it is already read only`() {
        val collection = listOf(1, 2, 3).asReadOnlyCollection()
        val readOnlyCollection = collection.asReadOnlyCollection()

        assertTrue(collection === readOnlyCollection)
    }

}
