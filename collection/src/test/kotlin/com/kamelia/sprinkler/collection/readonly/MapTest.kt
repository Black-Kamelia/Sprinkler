package com.kamelia.sprinkler.collection.readonly

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MapTest {

    @Test
    fun `read only map cannot be casted to mutable map`() {
        val readOnlyMap = readOnlyMapOf(1 to "one", 2 to "two", 3 to "three")

        @Suppress("UNCHECKED_CAST")
        assertThrows<ClassCastException> { readOnlyMap as MutableMap<Int, String> }
    }

    @Test
    fun `read only map iterator cannot be casted to mutable iterator`() {
        val readOnlyMap = readOnlyMapOf(1 to "one", 2 to "two", 3 to "three")
        val iterator = readOnlyMap.iterator()

        @Suppress("UNCHECKED_CAST")
        assertThrows<ClassCastException> { iterator as MutableIterator<MutableMap.MutableEntry<Int, String>> }
    }

    @Test
    fun `methods inherited from Map are delegated to the inner map`() {
        val map = mapOf(1 to "one", 2 to "two", 3 to "three")
        val readOnlyMap = map.asReadOnlyMap()

        assert(readOnlyMap.contains(1) == map.contains(1))
        assert(!readOnlyMap.contains(5) == !map.contains(5))
        assert(!readOnlyMap.isEmpty() == !map.isEmpty())
        assert(readOnlyMap.size == map.size)
        assert(readOnlyMap[1] == map[1])
        assert(readOnlyMap.entries == map.entries)
        assert(readOnlyMap.keys == map.keys)
        assert(readOnlyMap.values == map.values)
    }

    @Test
    fun `methods inherited from Any are delegated to the inner map`() {
        val map = mapOf(1 to "one", 2 to "two", 3 to "three")
        val readOnlyMap = map.asReadOnlyMap()

        assert(readOnlyMap.toString() == map.toString())
        assert(readOnlyMap.hashCode() == map.hashCode())
        assert(readOnlyMap == map)
    }

    @Test
    fun `toReadOnlyMap correctly creates a copy of the original iterable`() {
        val map = mutableMapOf(1 to "one", 2 to "two", 3 to "three")
        val readOnlyMap = map.toReadOnlyMap()
        map[5] = "five"

        assert(map.size == 4)
        assert(readOnlyMap.size == 3)
    }

    @Test
    fun `toReadOnlyMap on Iterable correctly creates a copy of the original iterable`() {
        val list = mutableListOf(1 to "one", 2 to "two", 3 to "three")
        val readOnlyMap = list.toReadOnlyMap()
        list += 5 to "five"

        assert(list.size == 4)
        assert(readOnlyMap.size == 3)
        assert(readOnlyMap[5] == null)
    }

    @Test
    fun `asReadOnlyMap simply wraps the original map`() {
        val map = mutableMapOf(1 to "one", 2 to "two", 3 to "three")
        val readOnlyMap = map.asReadOnlyMap()
        val keys = readOnlyMap.keys
        val values = readOnlyMap.values
        val entries = readOnlyMap.entries
        map[5] = "five"

        assert(map.size == 4)
        assert(readOnlyMap.size == 4)
        assert(keys.size == 4)
        assert(values.size == 4)
        assert(entries.size == 4)
    }

    @Test
    fun `asReadOnlyList does not wrap the original list if it is already read only`() {
        val readOnlyMap = readOnlyMapOf(1 to "one", 2 to "two", 3 to "three")
        val readOnlyMap2 = readOnlyMap.asReadOnlyMap()

        assert(readOnlyMap === readOnlyMap2)
    }

    @Test
    fun `Array#toReadOnlyMap correctly creates a copy of the original array`() {
        val array = arrayOf(1 to "one", 2 to "two", 3 to "three")
        val readOnlyMap = array.toReadOnlyMap()
        array[0] = 5 to "five"

        assert(array.size == 3)
        assert(readOnlyMap.size == 3)
        assert(readOnlyMap[5] == null)
    }

    @Test
    fun `map entries cannot be casted to mutable map entries`() {
        val readOnlyMap = readOnlyMapOf(1 to "one", 2 to "two", 3 to "three")
        val entries = readOnlyMap.entries

        @Suppress("UNCHECKED_CAST")
        assertThrows<ClassCastException> { entries as MutableSet<MutableMap.MutableEntry<Int, String>> }
    }

    @Test
    fun `map keys cannot be casted to mutable map keys`() {
        val readOnlyMap = mapOf(1 to "one", 2 to "two", 3 to "three").asReadOnlyMap()
        val keys = readOnlyMap.keys

        @Suppress("UNCHECKED_CAST")
        assertThrows<ClassCastException> { keys as MutableSet<Int> }
    }

    @Test
    fun `map values cannot be casted to mutable map values`() {
        val readOnlyMap = readOnlyMapOf(1 to "one", 2 to "two", 3 to "three")
        val values = readOnlyMap.values

        @Suppress("UNCHECKED_CAST")
        assertThrows<ClassCastException> { values as MutableCollection<String> }
    }

    @Test
    fun `map entries iterator cannot be casted to mutable map entries iterator`() {
        val readOnlyMap = readOnlyMapOf(1 to "one", 2 to "two", 3 to "three")
        val entries = readOnlyMap.entries
        val iterator = entries.iterator()

        @Suppress("UNCHECKED_CAST")
        assertThrows<ClassCastException> { iterator as MutableIterator<MutableMap.MutableEntry<Int, String>> }
    }

    @Test
    fun `entry methods inherited from Map$Entry are delegated to the inner entry`() {
        val map = mapOf(1 to "one", 2 to "two", 3 to "three")
        val readOnlyMap = map.asReadOnlyMap()
        val readOnlyEntry = readOnlyMap.entries.first()
        val entry = map.entries.first()

        assert(readOnlyEntry.key == entry.key)
        assert(readOnlyEntry.value == entry.value)
    }

    @Test
    fun `entry methods inherited from Any are delegated to the inner entry`() {
        val map = mapOf(1 to "one", 2 to "two", 3 to "three")
        val readOnlyMap = map.asReadOnlyMap()
        val readOnlyEntry = readOnlyMap.entries.first()
        val entry = map.entries.first()

        assert(readOnlyEntry.toString() == entry.toString())
        assert(readOnlyEntry.hashCode() == entry.hashCode())
        assert(readOnlyEntry == entry)
    }

}
