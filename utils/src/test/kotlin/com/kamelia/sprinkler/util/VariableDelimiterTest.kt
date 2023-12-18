package com.kamelia.sprinkler.util

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class VariableDelimiterTest {

    @Test
    fun `constructor throws an exception if delimiters are the same`() {
        assertThrows<IllegalArgumentException> {
            VariableDelimiter.create("{", "{")
        }
    }

    @Test
    fun `constructor throws an exception if start is a backslash`() {
        assertThrows<IllegalArgumentException> {
            VariableDelimiter.create("\\", "}")
        }
    }

    @Test
    fun `constructor throws an exception if end is a backslash`() {
        assertThrows<IllegalArgumentException> {
            VariableDelimiter.create("{", "\\")
        }
    }

    @Test
    fun `constructor throws an exception if start is blank`() {
        assertThrows<IllegalArgumentException> {
            VariableDelimiter.create("   ", "}")
        }
    }

    @Test
    fun `constructor throws an exception if end is blank`() {
        assertThrows<IllegalArgumentException> {
            VariableDelimiter.create("{", "   ")
        }
    }

    @Test
    fun `constructor does not throw for 1 char delimiters`() {
        assertDoesNotThrow {
            VariableDelimiter.create("{", "}")
        }
    }

    @Test
    fun `constructor does not throw for several chars delimiters`() {
        assertDoesNotThrow {
            VariableDelimiter.create("{{", "}}")
        }
    }

}
