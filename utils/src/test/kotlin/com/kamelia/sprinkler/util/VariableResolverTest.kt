package com.kamelia.sprinkler.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class VariableResolverTest {

    @Test
    fun `vararg VariableResolver returns the variable at the index`() {
        val resolver = VariableResolver.fromVararg(1, "a", true)
        assertEquals("1", resolver.value("0"))
        assertEquals("true", resolver.value("2"))
        assertEquals("a", resolver.value("1"))
    }

    @Test
    fun `vararg VariableResolver throws an exception if the index is not an integer`() {
        val resolver = VariableResolver.fromVararg(1, "a", true)
        assertThrows<VariableResolver.ResolutionException> {
            resolver.value("a")
        }
    }

    @Test
    fun `vararg VariableResolver throws an exception if the index is negative`() {
        val resolver = VariableResolver.fromVararg(1, "a", true)
        assertThrows<VariableResolver.ResolutionException> {
            resolver.value("-3")
        }
    }

    @Test
    fun `vararg VariableResolver throws an exception if the index is greater than the array size`() {
        val resolver = VariableResolver.fromVararg(1, "a", true)
        assertThrows<VariableResolver.ResolutionException> {
            resolver.value("3")
        }
    }

    @Test
    fun `array VariableResolver returns the variable at the index`() {
        val resolver = VariableResolver.fromList(listOf(1, "a", true))
        assertEquals("1", resolver.value("0"))
        assertEquals("true", resolver.value("2"))
        assertEquals("a", resolver.value("1"))
    }

    @Test
    fun `array VariableResolver throws an exception if the index is not an integer`() {
        val resolver = VariableResolver.fromList(listOf(1, "a", true))
        assertThrows<VariableResolver.ResolutionException> {
            resolver.value("a")
        }
    }

    @Test
    fun `array VariableResolver throws an exception if the index is negative`() {
        val resolver = VariableResolver.fromList(listOf(1, "a", true))
        assertThrows<VariableResolver.ResolutionException> {
            resolver.value("-3")
        }
    }

    @Test
    fun `array VariableResolver throws an exception if the index is greater than the array size`() {
        val resolver = VariableResolver.fromList(listOf(1, "a", true))
        assertThrows<VariableResolver.ResolutionException> {
            resolver.value("3")
        }
    }

    @Test
    fun `fromMap VariableResolver returns the variable with the given key`() {
        val resolver = VariableResolver.fromMap(mapOf("a" to 1, "b" to "a", "c" to true))
        assertEquals("1", resolver.value("a"))
        assertEquals("true", resolver.value("c"))
        assertEquals("a", resolver.value("b"))
    }

    @Test
    fun `fromMap VariableResolver returns the fallback if the key is not found`() {
        val resolver = VariableResolver.fromMap(mapOf("a" to 5), "fallback")
        assertEquals("fallback", resolver.value("d"))
    }

    @Test
    fun `fromMap VariableResolver throws an exception if the key is not found and no fallback is provided`() {
        val resolver = VariableResolver.fromMap(mapOf())
        assertThrows<VariableResolver.ResolutionException> {
            resolver.value("d")
        }
    }

    @Test
    fun `fromPairs VariableResolver returns the variable with the given key`() {
        val resolver = VariableResolver.fromPairs("a" to 1, "b" to "a", "c" to true)
        assertEquals("1", resolver.value("a"))
        assertEquals("true", resolver.value("c"))
        assertEquals("a", resolver.value("b"))
    }

    @Test
    fun `fromPairs VariableResolver returns the fallback if the key is not found`() {
        val resolver = VariableResolver.fromPairs("a" to 5, fallback = "fallback")
        assertEquals("fallback", resolver.value("d"))
    }

    private fun VariableResolver.value(key: String): String = this.value(key, VariableDelimiter.DEFAULT)

}
