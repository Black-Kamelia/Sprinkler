package com.kamelia.sprinkler.collection.readonly

import java.util.NoSuchElementException
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

        assert(readOnlyList.contains(1) == list.contains(1))
        assert(!readOnlyList.contains(5) == !list.contains(5))
        assert(readOnlyList.containsAll(listOf(1, 2)) == list.containsAll(listOf(1, 2)))
        assert(!readOnlyList.containsAll(listOf(1, 5)) == !list.containsAll(listOf(1, 5)))
        assert(!readOnlyList.isEmpty() == !list.isEmpty())
        assert(readOnlyList.size == list.size)
        assert(readOnlyList[0] == list[0])
        assert(readOnlyList.indexOf(1) == list.indexOf(1))
        assert(readOnlyList.lastIndexOf(1) == list.lastIndexOf(1))
        assert(readOnlyList.subList(0, 2) == list.subList(0, 2))
    }

    @Test
    fun `methods inherited from Any are delegated to the inner list`() {
        val list = listOf(1, 2, 3)
        val readOnlyList = list.asReadOnlyList()

        assert(readOnlyList.toString() == list.toString())
        assert(readOnlyList.hashCode() == list.hashCode())
        assert(readOnlyList == list)
    }

    @Test
    fun `toReadOnlyList correctly creates a copy of the original iterable`() {
        val list = mutableListOf(1, 2, 3)
        val readOnlyList = list.toReadOnlyList()
        list += 5

        assert(list.size == 4)
        assert(readOnlyList.size == 3)
    }

    @Test
    fun `asReadOnlyList simply wraps the original list`() {
        val list = mutableListOf(1, 2, 3)
        val readOnlyList = list.asReadOnlyList()
        list += 5

        assert(list !== readOnlyList)
        assert(list.size == 4)
        assert(readOnlyList.size == 4)
    }

    @Test
    fun `asReadOnlyList does not wrap the original list if it is already read only`() {
        val list = readOnlyListOf(1, 2, 3)
        val readOnlyList = list.asReadOnlyList()

        assert(readOnlyList === list)
    }

    @Test
    fun `Array#toReadOnlyList creates a list that is independent of the original array`() {
        val array = arrayOf(1, 2, 3)
        val readOnlyList = array.toReadOnlyList()
        array[0] = 5

        assert(array[0] == 5)
        assert(readOnlyList.size == 3)
        assert(5 !in readOnlyList)
    }

    @Test
    fun `Array#asReadOnlyList wraps the original array`() {
        val array = arrayOf(1, 2, 3)
        val readOnlyList = array.asReadOnlyList()
        array[0] = 5

        assert(array[0] == readOnlyList[0])
        assert(readOnlyList.size == 3)
        assert(5 in readOnlyList)
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

        assert(readOnlyListIterator.hasNext() == listIterator.hasNext())
        assert(readOnlyListIterator.hasPrevious() == listIterator.hasPrevious())
        assert(readOnlyListIterator.next() == listIterator.next())
        assert(readOnlyListIterator.nextIndex() == listIterator.nextIndex())
        assert(readOnlyListIterator.previous() == listIterator.previous())
        assert(readOnlyListIterator.previousIndex() == listIterator.previousIndex())
    }

    @Test
    fun `read only subList cannot be casted to mutable subList`() {
        val readOnlyList = listOf(1, 2, 3).asReadOnlyList()
        val subList = readOnlyList.subList(0, 2)

        @Suppress("UNCHECKED_CAST")
        assertThrows<ClassCastException> { subList as MutableList<Int> }
    }

    @Test
    fun `read only subList correctly overrides methods from List`() {
        val readOnlyList = listOf(1, 2, 3).asReadOnlyList()
        val subList = readOnlyList.subList(0, 2)

        assert(1 in subList)
        assert(3 !in subList)
        assert(subList.containsAll(listOf(1, 2)))
        assert(!subList.containsAll(listOf(1, 5)))
        assert(!subList.isEmpty())
        assert(subList.size == 2)
        assert(subList[0] == 1)
        assert(subList.indexOf(1) == 0)
        assert(subList.lastIndexOf(1) == 0)
        assert(subList.subList(0, 1) == listOf(1))
    }

    @Test
    fun `read only subList iterator cannot be casted to mutable subList iterator`() {
        val readOnlyList = listOf(1, 2, 3).asReadOnlyList()
        val subList = readOnlyList.subList(0, 2)
        val iterator = subList.iterator()

        @Suppress("UNCHECKED_CAST")
        assertThrows<ClassCastException> { iterator as MutableListIterator<Int> }
    }

    @Test
    fun `read only subList listIterator cannot be casted to mutable subList listIterator`() {
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

        assert(readOnlyListIterator.hasNext() == listIterator.hasNext())
        assert(readOnlyListIterator.hasPrevious() == listIterator.hasPrevious())
        assert(readOnlyListIterator.next() == listIterator.next())

        assertThrows<NoSuchElementException> {
            readOnlyListIterator.next()
            readOnlyListIterator.next()
        }
        readOnlyListIterator.previous()

        assert(readOnlyListIterator.nextIndex() == listIterator.nextIndex())
        assert(readOnlyListIterator.previous() == listIterator.previous())
        assert(readOnlyListIterator.previousIndex() == listIterator.previousIndex())
    }

}
