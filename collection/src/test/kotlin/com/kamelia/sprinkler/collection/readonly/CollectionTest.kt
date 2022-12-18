package com.kamelia.sprinkler.collection.readonly

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CollectionTest {

    @Test
    fun `read only collection cannot be casted to mutable collection`() {
        val readOnlyCollection = listOf(1, 2, 3).asReadOnlyCollection()

        @Suppress("UNCHECKED_CAST")
        assertThrows<ClassCastException> { readOnlyCollection as MutableCollection<Int> }
    }

    @Test
    fun `read only collection iterator cannot be casted to mutable iterator`() {
        val readOnlyCollection = listOf(1, 2, 3).asReadOnlyCollection()
        val iterator = readOnlyCollection.iterator()

        @Suppress("UNCHECKED_CAST")
        assertThrows<ClassCastException> { iterator as MutableIterator<Int> }
    }

    @Test
    fun `methods inherited from Collection are delegated to the inner collection`() {
        val collection = listOf(1, 2, 3) as Collection<Int>
        val readOnlyCollection = collection.asReadOnlyCollection()

        assert(1 in readOnlyCollection == 1 in collection)
        assert(5 !in readOnlyCollection == 5 !in collection)
        assert(readOnlyCollection.containsAll(listOf(1, 2)) == collection.containsAll(listOf(1, 2)))
        assert(!readOnlyCollection.containsAll(listOf(1, 5)) == !collection.containsAll(listOf(1, 5)))
        assert(!readOnlyCollection.isEmpty() == !collection.isEmpty())
        assert(readOnlyCollection.size == collection.size)
    }

    @Test
    fun `methods inherited from Any are delegated to the inner collection`() {
        val collection = listOf(1, 2, 3) as Collection<Int>
        val readOnlyCollection = collection.asReadOnlyCollection()

        assert(readOnlyCollection.toString() == collection.toString())
        assert(readOnlyCollection.hashCode() == collection.hashCode())
        assert(readOnlyCollection == collection)
    }

    @Test
    fun `toReadOnlyCollection correctly creates a copy of the original iterable`() {
        @Suppress("UNCHECKED_CAST")
        val collection = mutableListOf(1, 2, 3) as MutableCollection<Int>
        val readOnlyCollection = collection.toReadOnlyCollection()
        collection += 5

        assert(collection.size == 4)
        assert(readOnlyCollection.size == 3)
    }

    @Test
    fun `asReadOnlyCollection simply wraps the original collection`() {
        @Suppress("UNCHECKED_CAST")
        val collection = mutableListOf(1, 2, 3) as MutableCollection<Int>
        val readOnlyCollection = collection.asReadOnlyCollection()
        collection += 5

        assert(collection !== readOnlyCollection)
        assert(collection.size == 4)
        assert(readOnlyCollection.size == 4)
    }

    @Test
    fun `asReadOnlyCollection does not wrap the original collection if it is already read only`() {
        val collection = listOf(1, 2, 3).asReadOnlyCollection()
        val readOnlyCollection = collection.asReadOnlyCollection()

        assert(collection === readOnlyCollection)
    }

}
