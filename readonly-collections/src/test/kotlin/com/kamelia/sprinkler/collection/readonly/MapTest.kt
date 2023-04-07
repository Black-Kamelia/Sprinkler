package com.kamelia.sprinkler.collection.readonly

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
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

        assertTrue(readOnlyMap.contains(1) == map.contains(1))
        assertTrue(!readOnlyMap.contains(5) == !map.contains(5))
        assertTrue(!readOnlyMap.isEmpty() == !map.isEmpty())
        assertTrue(readOnlyMap.size == map.size)
        assertTrue(readOnlyMap[1] == map[1])
        assertTrue(readOnlyMap.entries == map.entries)
        assertTrue(readOnlyMap.keys == map.keys)
        assertTrue(readOnlyMap.values == map.values)
    }

    @Test
    fun `methods inherited from Any are delegated to the inner map`() {
        val map = mapOf(1 to "one", 2 to "two", 3 to "three")
        val readOnlyMap = map.asReadOnlyMap()

        assertTrue(readOnlyMap.toString() == map.toString())
        assertTrue(readOnlyMap.hashCode() == map.hashCode())
        assertTrue(readOnlyMap == map)
    }

    @Test
    fun `toReadOnlyMap correctly creates a copy of the original iterable`() {
        val map = mutableMapOf(1 to "one", 2 to "two", 3 to "three")
        val readOnlyMap = map.toReadOnlyMap()
        map[5] = "five"

        assertTrue(map.size == 4)
        assertTrue(readOnlyMap.size == 3)
    }

    @Test
    fun `toReadOnlyMap on Iterable correctly creates a copy of the original iterable`() {
        val list = mutableListOf(1 to "one", 2 to "two", 3 to "three")
        val readOnlyMap = list.toReadOnlyMap()
        list += 5 to "five"

        assertTrue(list.size == 4)
        assertTrue(readOnlyMap.size == 3)
        assertTrue(readOnlyMap[5] == null)
    }

    @Test
    fun `asReadOnlyMap simply wraps the original map`() {
        val map = mutableMapOf(1 to "one", 2 to "two", 3 to "three")
        val readOnlyMap = map.asReadOnlyMap()
        val keys = readOnlyMap.keys
        val values = readOnlyMap.values
        val entries = readOnlyMap.entries
        map[5] = "five"

        assertTrue(map.size == 4)
        assertTrue(readOnlyMap.size == 4)
        assertTrue(keys.size == 4)
        assertTrue(values.size == 4)
        assertTrue(entries.size == 4)
    }

    @Test
    fun `asReadOnlyList does not wrap the original list if it is already read only`() {
        val readOnlyMap = readOnlyMapOf(1 to "one", 2 to "two", 3 to "three")
        val readOnlyMap2 = readOnlyMap.asReadOnlyMap()

        assertTrue(readOnlyMap === readOnlyMap2)
    }

    @Test
    fun `Array#toReadOnlyMap correctly creates a copy of the original array`() {
        val array = arrayOf(1 to "one", 2 to "two", 3 to "three")
        val readOnlyMap = array.toReadOnlyMap()
        array[0] = 5 to "five"

        assertTrue(array.size == 3)
        assertTrue(readOnlyMap.size == 3)
        assertTrue(readOnlyMap[5] == null)
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
    fun `map entries methods inherited from Set are delegated to the inner set`() {
        val map = mutableMapOf(1 to "one", 2 to "two", 3 to "three")
        val readOnlyMap = map.asReadOnlyMap()
        val readOnlyEntries = readOnlyMap.entries as Set<Map.Entry<Int, String>>
        val entries = map.entries

        val entry = entries.first()
        assertTrue(readOnlyEntries.contains(entry))
        assertTrue(readOnlyEntries.containsAll(entries))
        entries.remove(entry)
        assertFalse(entry in readOnlyEntries)
        assertTrue(readOnlyEntries.isNotEmpty())
        assertEquals(readOnlyEntries.size, entries.size)
        assertTrue(readOnlyEntries.containsAll(entries))
    }

    @Test
    fun `map entries methods inherited from Any are delegated to the inner set`() {
        val map = mapOf(1 to "one", 2 to "two", 3 to "three")
        val readOnlyMap = map.asReadOnlyMap()
        val readOnlyEntries = readOnlyMap.entries
        val entries = map.entries

        assertTrue(readOnlyEntries.toString() == entries.toString())
        assertTrue(readOnlyEntries.hashCode() == entries.hashCode())
        assertTrue(readOnlyEntries == entries)
    }

    @Test
    fun `entry methods inherited from Map$Entry are delegated to the inner entry`() {
        val map = mapOf(1 to "one", 2 to "two", 3 to "three")
        val readOnlyMap = map.asReadOnlyMap()
        val readOnlyEntry = readOnlyMap.entries.first()
        val entry = map.entries.first()

        assertTrue(readOnlyEntry.key == entry.key)
        assertTrue(readOnlyEntry.value == entry.value)
    }

    @Test
    fun `entry methods inherited from Any are delegated to the inner entry`() {
        val map = mapOf(1 to "one", 2 to "two", 3 to "three")
        val readOnlyMap = map.asReadOnlyMap()
        val readOnlyEntry = readOnlyMap.entries.first()
        val entry = map.entries.first()

        assertTrue(readOnlyEntry.toString() == entry.toString())
        assertTrue(readOnlyEntry.hashCode() == entry.hashCode())
        assertTrue(readOnlyEntry == entry)
    }

}
