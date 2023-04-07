package com.kamelia.sprinkler.collection.readonly

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ListTest {

    @Test
    fun `read only list cannot be casted to mutable list`() {
        val readOnlyList = readOnlyListOf(1, 2, 3)

        @Suppress("UNCHECKED_CAST")
        assertThrows<ClassCastException> { readOnlyList as MutableList<Int> }
    }

    @Test
    fun `read only list iterator cannot be casted to mutable iterator`() {
        val readOnlyList = readOnlyListOf(1, 2, 3)
        val iterator = readOnlyList.iterator()

        @Suppress("UNCHECKED_CAST")
        assertThrows<ClassCastException> { iterator as MutableListIterator<Int> }
    }

    @Test
    fun `methods inherited from List are delegated to the inner list`() {
        val list = listOf(1, 2, 3)
        val readOnlyList = list.asReadOnlyList()

        assertEquals(1 in readOnlyList, 1 in list)
        assertEquals(5 in readOnlyList, 5 in list)
        assertEquals(readOnlyList.containsAll(listOf(1, 2)), list.containsAll(listOf(1, 2)))
        assertEquals(readOnlyList.containsAll(listOf(1, 5)), list.containsAll(listOf(1, 5)))
        assertEquals(readOnlyList.isEmpty(), list.isEmpty())
        assertEquals(readOnlyList.size, list.size)
        assertEquals(readOnlyList[0], list[0])
        assertEquals(readOnlyList.indexOf(1), list.indexOf(1))
        assertEquals(readOnlyList.lastIndexOf(1), list.lastIndexOf(1))
        assertEquals(readOnlyList.subList(0, 2), list.subList(0, 2))
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

        assertEquals(array[0], 5)
        assertEquals(readOnlyList.size, 3)
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
    fun `read only listIterator cannot be casted to mutable listIterator`() {
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

}
