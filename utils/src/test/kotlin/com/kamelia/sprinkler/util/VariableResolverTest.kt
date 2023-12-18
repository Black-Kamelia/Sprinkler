package com.kamelia.sprinkler.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class VariableResolverTest {

    @Test
    fun `vararg VariableResolver returns the variable at the index`() {
        val array = arrayOf(1, "a", true)
        val resolver = VariableResolver.fromArray()
        assertEquals("1", resolver.resolve("0", array))
        assertEquals("true", resolver.resolve("2", array))
        assertEquals("a", resolver.resolve("1", array))
    }

    @Test
    fun `vararg VariableResolver throws an exception if the index is not an integer`() {
        val array = arrayOf(1, "a", true)
        val resolver = VariableResolver.fromArray()
        assertThrows<IllegalArgumentException> {
            resolver.resolve("a", array)
        }
    }

    @Test
    fun `vararg VariableResolver throws an exception if the index is negative`() {
        val array = arrayOf(1, "a", true)
        val resolver = VariableResolver.fromArray()
        assertThrows<IllegalArgumentException> {
            resolver.resolve("-3", array)
        }
    }

    @Test
    fun `vararg VariableResolver throws an exception if the index is greater than the array size`() {
        val array = arrayOf(1, "a", true)
        val resolver = VariableResolver.fromArray()
        assertThrows<IllegalArgumentException> {
            resolver.resolve("3", array)
        }
    }

    @Test
    fun `array VariableResolver returns the variable at the index`() {
        val list = listOf(1, "a", true)
        val resolver = VariableResolver.fromList()
        assertEquals("1", resolver.resolve("0", list))
        assertEquals("true", resolver.resolve("2", list))
        assertEquals("a", resolver.resolve("1", list))
    }

    @Test
    fun `array VariableResolver throws an exception if the index is not an integer`() {
        val list = listOf(1, "a", true)
        val resolver = VariableResolver.fromList()
        assertThrows<IllegalArgumentException> {
            resolver.resolve("a", list)
        }
    }

    @Test
    fun `array VariableResolver throws an exception if the index is negative`() {
        val list = listOf(1, "a", true)
        val resolver = VariableResolver.fromList()
        assertThrows<IllegalArgumentException> {
            resolver.resolve("-3", list)
        }
    }

    @Test
    fun `array VariableResolver throws an exception if the index is greater than the array size`() {
        val list = listOf(1, "a", true)
        val resolver = VariableResolver.fromList()
        assertThrows<IllegalArgumentException> {
            resolver.resolve("3", list)
        }
    }

    @Test
    fun `fromMap VariableResolver returns the variable with the given key`() {
        val map = mapOf("a" to 1, "b" to "a", "c" to true)
        val resolver = VariableResolver.fromMap()
        assertEquals("1", resolver.resolve("a", map))
        assertEquals("true", resolver.resolve("c", map))
        assertEquals("a", resolver.resolve("b", map))
    }

    @Test
    fun `fromMap VariableResolver throws an exception if the key is not found`() {
        val map = mapOf<String, Any>()
        val resolver = VariableResolver.fromMap()
        assertThrows<IllegalArgumentException> {
            resolver.resolve("d", map)
        }
    }

    @Test
    fun `fromIterator VariableResolver returns variables in order`() {
        val iterator = listOf(1, "a", true).iterator()
        val resolver = VariableResolver.fromIterator()
        assertEquals("1", resolver.resolve("", iterator))
        assertEquals("a", resolver.resolve("", iterator))
        assertEquals("true", resolver.resolve("", iterator))
    }

    @Test
    fun `fromIterator VariableResolver throws an exception if the iterator is empty`() {
        val iterator = listOf<Any>().iterator()
        val resolver = VariableResolver.fromIterator()
        assertThrows<IllegalArgumentException> {
            resolver.resolve("", iterator)
        }
    }
}
