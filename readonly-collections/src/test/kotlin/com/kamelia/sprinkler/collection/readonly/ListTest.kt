package com.kamelia.sprinkler.collection.readonly

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ListTest {

    @Test
    fun `read only list cannot be cast to mutable list`() {
        val readOnlyList = readOnlyListOf(1, 2, 3)

        @Suppress("UNCHECKED_CAST")
        assertThrows<ClassCastException> { readOnlyList as MutableList<Int> }
    }

    @Test
    fun `read only list iterator cannot be cast to mutable iterator`() {
        val readOnlyList = readOnlyListOf(1, 2, 3)
        val iterator = readOnlyList.iterator()

        @Suppress("UNCHECKED_CAST")
        assertThrows<ClassCastException> { iterator as MutableListIterator<Int> }
    }

    @Test
    fun `methods inherited from List are delegated to the inner list`() {
        val list = listOf(1, 2, 3)
        val readOnlyList = list.asReadOnlyList()

        assertTrue(readOnlyList.contains(1) == list.contains(1))
        assertTrue(!readOnlyList.contains(5) == !list.contains(5))
        assertTrue(readOnlyList.containsAll(listOf(1, 2)) == list.containsAll(listOf(1, 2)))
        assertTrue(!readOnlyList.containsAll(listOf(1, 5)) == !list.containsAll(listOf(1, 5)))
        assertTrue(!readOnlyList.isEmpty() == !list.isEmpty())
        assertTrue(readOnlyList.size == list.size)
        assertTrue(readOnlyList[0] == list[0])
        assertTrue(readOnlyList.indexOf(1) == list.indexOf(1))
        assertTrue(readOnlyList.lastIndexOf(1) == list.lastIndexOf(1))
        assertTrue(readOnlyList.subList(0, 2) == list.subList(0, 2))
    }

    @Test
    fun `methods inherited from Any are delegated to the inner list`() {
        val list = listOf(1, 2, 3)
        val readOnlyList = list.asReadOnlyList()

        assertTrue(readOnlyList.toString() == list.toString())
        assertTrue(readOnlyList.hashCode() == list.hashCode())
        assertTrue(readOnlyList == list)
    }

    @Test
    fun `toReadOnlyList correctly creates a copy of the original iterable`() {
        val list = mutableListOf(1, 2, 3)
        val readOnlyList = list.toReadOnlyList()
        list += 5

        assertTrue(list.size == 4)
        assertTrue(readOnlyList.size == 3)
    }

    @Test
    fun `asReadOnlyList simply wraps the original list`() {
        val list = mutableListOf(1, 2, 3)
        val readOnlyList = list.asReadOnlyList()
        list += 5

        assertTrue(list !== readOnlyList)
        assertTrue(list.size == 4)
        assertTrue(readOnlyList.size == 4)
    }

    @Test
    fun `asReadOnlyList does not wrap the original list if it is already read only`() {
        val list = readOnlyListOf(1, 2, 3)
        val readOnlyList = list.asReadOnlyList()

        assertTrue(readOnlyList === list)
    }

    @Test
    fun `Array#toReadOnlyList creates a list that is independent of the original array`() {
        val array = arrayOf(1, 2, 3)
        val readOnlyList = array.toReadOnlyList()
        array[0] = 5

        assertTrue(array[0] == 5)
        assertTrue(readOnlyList.size == 3)
        assertTrue(5 !in readOnlyList)
    }

    @Test
    fun `Array#asReadOnlyList wraps the original array`() {
        val array = arrayOf(1, 2, 3)
        val readOnlyList = array.asReadOnlyList()
        array[0] = 5

        assertTrue(array[0] == readOnlyList[0])
        assertTrue(readOnlyList.size == 3)
        assertTrue(5 in readOnlyList)
    }

    @Test
    fun `read only listIterator cannot be cast to mutable listIterator`() {
        val readOnlyList = readOnlyListOf(1, 2, 3)
        val listIterator = readOnlyList.listIterator()

        @Suppress("UNCHECKED_CAST")
        assertThrows<ClassCastException> { listIterator as MutableListIterator<Int> }
    }

    @Test
    fun `listIterator methods inherited from ListIterator are delegated to the inner listIterator`() {
        val list = listOf(1, 2, 3)
        val readOnlyList = list.asReadOnlyList()
        val readOnlyListIterator = readOnlyList.listIterator()
        val listIterator = list.listIterator()

        assertTrue(readOnlyListIterator.hasNext() == listIterator.hasNext())
        assertTrue(readOnlyListIterator.hasPrevious() == listIterator.hasPrevious())
        assertTrue(readOnlyListIterator.next() == listIterator.next())
        assertTrue(readOnlyListIterator.nextIndex() == listIterator.nextIndex())
        assertTrue(readOnlyListIterator.previous() == listIterator.previous())
        assertTrue(readOnlyListIterator.previousIndex() == listIterator.previousIndex())
    }

    @Test
    fun `read only subList cannot be cast to mutable subList`() {
        val readOnlyList = listOf(1, 2, 3).asReadOnlyList()
        val subList = readOnlyList.subList(0, 2)

        @Suppress("UNCHECKED_CAST")
        assertThrows<ClassCastException> { subList as MutableList<Int> }
    }

    @Test
    fun `read only subList correctly overrides methods from List`() {
        val readOnlyList = listOf(1, 2, 3).asReadOnlyList()
        val subList = readOnlyList.subList(0, 2)

        assertTrue(1 in subList)
        assertTrue(3 !in subList)
        assertTrue(subList.containsAll(listOf(1, 2)))
        assertTrue(!subList.containsAll(listOf(1, 5)))
        assertTrue(!subList.isEmpty())
        assertTrue(subList.size == 2)
        assertTrue(subList[0] == 1)
        assertTrue(subList.indexOf(1) == 0)
        assertTrue(subList.lastIndexOf(1) == 0)
        assertTrue(subList.subList(0, 1) == listOf(1))
    }

    @Test
    fun `read only subList iterator cannot be cast to mutable subList iterator`() {
        val readOnlyList = listOf(1, 2, 3).asReadOnlyList()
        val subList = readOnlyList.subList(0, 2)
        val iterator = subList.iterator()

        @Suppress("UNCHECKED_CAST")
        assertThrows<ClassCastException> { iterator as MutableListIterator<Int> }
    }

    @Test
    fun `read only subList listIterator cannot be cast to mutable subList listIterator`() {
        val readOnlyList = listOf(1, 2, 3).asReadOnlyList()
        val subList = readOnlyList.subList(0, 2)
        val listIterator = subList.listIterator()

        @Suppress("UNCHECKED_CAST")
        assertThrows<ClassCastException> { listIterator as MutableListIterator<Int> }
    }

    @Test
    fun `read only subList listIterator methods inherited from ListIterator are delegated to the inner listIterator`() {
        val list = listOf(1, 2)
        val readOnlyList = list.asReadOnlyList()
        val subList = readOnlyList.subList(0, 2)
        val readOnlyListIterator = subList.listIterator()
        val listIterator = list.listIterator()

        assertThrows<NoSuchElementException> { readOnlyListIterator.previous() }

        assertTrue(readOnlyListIterator.hasNext() == listIterator.hasNext())
        assertTrue(readOnlyListIterator.hasPrevious() == listIterator.hasPrevious())
        assertTrue(readOnlyListIterator.next() == listIterator.next())

        assertThrows<NoSuchElementException> {
            readOnlyListIterator.next()
            readOnlyListIterator.next()
        }
        readOnlyListIterator.previous()

        assertTrue(readOnlyListIterator.nextIndex() == listIterator.nextIndex())
        assertTrue(readOnlyListIterator.previous() == listIterator.previous())
        assertTrue(readOnlyListIterator.previousIndex() == listIterator.previousIndex())
    }

}
