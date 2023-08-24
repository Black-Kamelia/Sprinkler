package com.kamelia.sprinkler.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class InterpolationsTest {

    @Test
    fun `empty variables are substituted by the current count of variable`() {
        var index = 0
        val resolver = VariableResolver {
            assertEquals(index.toString(), it)
            index++
            it
        }
        "Hello {} {}".interpolate(resolver)
    }

    @Test
    fun `variable count also count non-empty variables`() {
        var index = 0
        val resolver = VariableResolver {
            assertEquals(index.toString(), it)
            index++
            it
        }
        "Hello {0} {} {} {3} {}".interpolate(resolver)
    }

    @Test
    fun `interpolate(List) replaces the found variables`() {
        val str = "Hello {0}, you are {1} years old"
        assertEquals("Hello John, you are 25 years old", str.interpolateIndexed(listOf("John", 25, "foo")))
    }

    @Test
    fun `interpolate(List) throws an exception if the index is not an integer`() {
        val str = "Hello {0}, you are {a} years old"
        assertThrows<IllegalArgumentException> {
            str.interpolateIndexed(arrayOf("John", 25, "foo", "bar"))
        }
    }

    @Test
    fun `interpolate(List) throws an exception if the index is negative`() {
        val str = "Hello {0}, you are {-1} years old"
        assertThrows<IllegalArgumentException> {
            str.interpolateIndexed(arrayOf("John", 25, "foo", "bar"))
        }
    }

    @Test
    fun `interpolate(List) throws an exception if the index is greater than the array size`() {
        val str = "Hello {0}, you are {3} years old"
        assertThrows<IllegalArgumentException> {
            str.interpolateIndexed(arrayOf("John", 25))
        }
    }

    @Test
    fun `interpolate(List) throws if the variable identifier is invalid`() {
        val str = "Hello {0}, you are {§} years old"
        assertThrows<IllegalArgumentException> {
            str.interpolateIndexed(arrayOf("John", 25, "foo", "bar"))
        }
    }

    @Test
    fun `interpolate(List) throws if the curly braces are not closed`() {
        val str = "Hello {0}, you are {1"
        assertThrows<IllegalArgumentException> {
            str.interpolateIndexed(arrayOf("John", 25, "foo", "bar"))
        }
    }

    @Test
    fun `interpolate(List) does not replace ignored variables`() {
        val str = "Hello {0}, you are \\{1} years old"
        assertEquals("Hello John, you are {1} years old", str.interpolateIndexed(listOf("John", "25")))
    }

    @Test
    fun `interpolate(vararg) replaces the found variables`() {
        val str = "Hello {0}, you are {1} years old"
        assertEquals("Hello John, you are 25 years old", str.interpolateIndexed("John", 25, "foo"))
    }

    @Test
    fun `interpolate(vararg) throws an exception if the index is not an integer`() {
        val str = "Hello {0}, you are {a} years old"
        assertThrows<IllegalArgumentException> {
            str.interpolateIndexed("John", 25, "foo", "bar")
        }
    }

    @Test
    fun `interpolate(vararg) throws an exception if the index is negative`() {
        val str = "Hello {0}, you are {-1} years old"
        assertThrows<IllegalArgumentException> {
            str.interpolateIndexed("John", 25, "foo", "bar")
        }
    }

    @Test
    fun `interpolate(vararg) throws an exception if the index is greater than the array size`() {
        val str = "Hello {0}, you are {3} years old"
        assertThrows<IllegalArgumentException> {
            str.interpolateIndexed("John", 25)
        }
    }

    @Test
    fun `interpolate(vararg) throws if the variable identifier is invalid`() {
        val str = "Hello {0}, you are {§} years old"
        assertThrows<IllegalArgumentException> {
            str.interpolateIndexed("John", 25, "foo", "bar")
        }
    }

    @Test
    fun `interpolate(vararg) throws if the curly braces are not closed`() {
        val str = "Hello {0}, you are {1"
        assertThrows<IllegalArgumentException> {
            str.interpolateIndexed("John", 25, "foo", "bar")
        }
    }

    @Test
    fun `interpolate(vararg) does not replace ignored variables`() {
        val str = "Hello {0}, you are \\{1} years old"
        assertEquals("Hello John, you are {1} years old", str.interpolateIndexed("John", "25"))
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
