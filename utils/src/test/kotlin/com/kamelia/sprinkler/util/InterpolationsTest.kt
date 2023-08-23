package com.kamelia.sprinkler.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class InterpolationsTest {

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
        val resolver = VariableResolver.fromArray(arrayOf(1, "a", true))
        assertEquals("1", resolver.value("0"))
        assertEquals("true", resolver.value("2"))
        assertEquals("a", resolver.value("1"))
    }

    @Test
    fun `array VariableResolver throws an exception if the index is not an integer`() {
        val resolver = VariableResolver.fromArray(arrayOf(1, "a", true))
        assertThrows<VariableResolver.ResolutionException> {
            resolver.value("a")
        }
    }

    @Test
    fun `array VariableResolver throws an exception if the index is negative`() {
        val resolver = VariableResolver.fromArray(arrayOf(1, "a", true))
        assertThrows<VariableResolver.ResolutionException> {
            resolver.value("-3")
        }
    }

    @Test
    fun `array VariableResolver throws an exception if the index is greater than the array size`() {
        val resolver = VariableResolver.fromArray(arrayOf(1, "a", true))
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

    @Test
    fun `interpolate(Array) replaces the found variables`() {
        val str = "Hello {0}, you are {1} years old"
        assertEquals("Hello John, you are 25 years old", str.interpolate(arrayOf("John", 25, "foo")))
    }

    @Test
    fun `interpolate(Array) throws an exception if the index is not an integer`() {
        val str = "Hello {0}, you are {a} years old"
        assertThrows<IllegalArgumentException> {
            str.interpolate(arrayOf("John", 25, "foo", "bar"))
        }
    }

    @Test
    fun `interpolate(Array) throws an exception if the index is negative`() {
        val str = "Hello {0}, you are {-1} years old"
        assertThrows<IllegalArgumentException> {
            str.interpolate(arrayOf("John", 25, "foo", "bar"))
        }
    }

    @Test
    fun `interpolate(Array) throws an exception if the index is greater than the array size`() {
        val str = "Hello {0}, you are {3} years old"
        assertThrows<IllegalArgumentException> {
            str.interpolate(arrayOf("John", 25))
        }
    }

    @Test
    fun `interpolate(Array) throws if the variable identifier is invalid`() {
        val str = "Hello {0}, you are {§} years old"
        assertThrows<IllegalArgumentException> {
            str.interpolate(arrayOf("John", 25, "foo", "bar"))
        }
    }

    @Test
    fun `interpolate(Array) throws if the curly braces are not closed`() {
        val str = "Hello {0}, you are {1"
        assertThrows<IllegalArgumentException> {
            str.interpolate(arrayOf("John", 25, "foo", "bar"))
        }
    }

    @Test
    fun `interpolate(Array) does not replace ignored variables`() {
        val str = "Hello {0}, you are \\{1} years old"
        assertEquals("Hello John, you are {1} years old", str.interpolate(arrayOf("John", "25")))
    }

    @Test
    fun `interpolate(vararg) replaces the found variables`() {
        val str = "Hello {0}, you are {1} years old"
        assertEquals("Hello John, you are 25 years old", str.interpolateWithVararg("John", 25, "foo"))
    }

    @Test
    fun `interpolate(vararg) throws an exception if the index is not an integer`() {
        val str = "Hello {0}, you are {a} years old"
        assertThrows<IllegalArgumentException> {
            str.interpolateWithVararg("John", 25, "foo", "bar")
        }
    }

    @Test
    fun `interpolate(vararg) throws an exception if the index is negative`() {
        val str = "Hello {0}, you are {-1} years old"
        assertThrows<IllegalArgumentException> {
            str.interpolateWithVararg("John", 25, "foo", "bar")
        }
    }

    @Test
    fun `interpolate(vararg) throws an exception if the index is greater than the array size`() {
        val str = "Hello {0}, you are {3} years old"
        assertThrows<IllegalArgumentException> {
            str.interpolateWithVararg("John", 25)
        }
    }

    @Test
    fun `interpolate(vararg) throws if the variable identifier is invalid`() {
        val str = "Hello {0}, you are {§} years old"
        assertThrows<IllegalArgumentException> {
            str.interpolateWithVararg("John", 25, "foo", "bar")
        }
    }

    @Test
    fun `interpolate(vararg) throws if the curly braces are not closed`() {
        val str = "Hello {0}, you are {1"
        assertThrows<IllegalArgumentException> {
            str.interpolateWithVararg("John", 25, "foo", "bar")
        }
    }

    @Test
    fun `interpolate(vararg) does not replace ignored variables`() {
        val str = "Hello {0}, you are \\{1} years old"
        assertEquals("Hello John, you are {1} years old", str.interpolateWithVararg("John", "25"))
    }

    @Test
    fun `interpolate(Map) replaces the found variables`() {
        val str = "Hello {name}, you are {age} years old"
        assertEquals(
            "Hello John, you are 25 years old",
            str.interpolate(mapOf("name" to "John", "age" to 25, "foo" to "bar"))
        )
    }

    @Test
    fun `interpolate(Map) returns the fallback if the key is not found`() {
        val str = "Hello {name}, you are {age} years old"
        assertEquals(
            "Hello John, you are 25 years old",
            str.interpolate(mapOf("name" to "John", "age" to 25), "fallback")
        )
    }

    @Test
    fun `interpolate(Map) throws an exception if the key is not found and no fallback is provided`() {
        val str = "Hello {name}, you are {age} years old"
        assertThrows<IllegalArgumentException> {
            str.interpolate(mapOf("name" to "John"))
        }
    }

    @Test
    fun `interpolate(Map) throws if the variable identifier is invalid`() {
        val str = "Hello {name}, you are {§} years old"
        assertThrows<IllegalArgumentException> {
            str.interpolate(mapOf("name" to "John", "age" to 25, "foo" to "bar"))
        }
    }

    @Test
    fun `interpolate(Map) throws if the curly braces are not closed`() {
        val str = "Hello {name}, you are {age"
        assertThrows<IllegalArgumentException> {
            str.interpolate(mapOf("name" to "John", "age" to 25, "foo" to "bar"))
        }
    }

    @Test
    fun `interpolate(Map) does not replace ignored variables`() {
        val str = "Hello {name}, you are \\{age} years old"
        assertEquals(
            "Hello John, you are {age} years old",
            str.interpolate(mapOf("name" to "John", "age" to 25))
        )
    }

    @Test
    fun `interpolate(Map) accepts '-'`() {
        val str = "Hello {name-}, you are {age} years old"
        assertEquals(
            "Hello John, you are 25 years old",
            str.interpolate(mapOf("name-" to "John", "age" to 25, "foo" to "bar"))
        )
    }

    @Test
    fun `interpolate(Map) accepts '_'`() {
        val str = "Hello {name_}, you are {age} years old"
        assertEquals(
            "Hello John, you are 25 years old",
            str.interpolate(mapOf("name_" to "John", "age" to 25, "foo" to "bar"))
        )
    }

    @Test
    fun `interpolate(Pairs) replaces the found variables`() {
        val str = "Hello {name}, you are {age} years old"
        assertEquals(
            "Hello John, you are 25 years old",
            str.interpolate("name" to "John", "age" to 25, "foo" to "bar")
        )
    }

    @Test
    fun `interpolate(Pairs) returns the fallback if the key is not found`() {
        val str = "Hello {name}, you are {age} years old"
        assertEquals(
            "Hello John, you are 25 years old",
            str.interpolate("name" to "John", "age" to 25, fallback = "fallback")
        )
    }

    @Test
    fun `interpolate(Pairs) throws an exception if the key is not found and no fallback is provided`() {
        val str = "Hello {name}, you are {age} years old"
        assertThrows<IllegalArgumentException> {
            str.interpolate("name" to "John")
        }
    }

    @Test
    fun `interpolate(Pairs) throws if the variable identifier is invalid`() {
        val str = "Hello {name}, you are {§} years old"
        assertThrows<IllegalArgumentException> {
            str.interpolate("name" to "John", "age" to 25, "foo" to "bar")
        }
    }

    @Test
    fun `interpolate(Pairs) throws if the curly braces are not closed`() {
        val str = "Hello {name}, you are {age"
        assertThrows<IllegalArgumentException> {
            str.interpolate("name" to "John", "age" to 25, "foo" to "bar")
        }
    }

    @Test
    fun `interpolate(Pairs) does not replace ignored variables`() {
        val str = "Hello {name}, you are \\{age} years old"
        assertEquals("Hello John, you are {age} years old", str.interpolate("name" to "John", "age" to 25))
    }

    @Test
    fun `interpolate(Pairs) accepts '-'`() {
        val str = "Hello {name-}, you are {age} years old"
        assertEquals(
            "Hello John, you are 25 years old",
            str.interpolate("name-" to "John", "age" to 25, "foo" to "bar")
        )
    }

    @Test
    fun `interpolate(Pairs) accepts '_'`() {
        val str = "Hello {name_}, you are {age} years old"
        assertEquals(
            "Hello John, you are 25 years old",
            str.interpolate("name_" to "John", "age" to 25, "foo" to "bar")
        )
    }

}
