package com.kamelia.sprinkler.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ExceptionsTest {

    @Test
    fun `illegalArgument throws IllegalArgumentException with the given message`() {
        val ex = assertThrows<IllegalArgumentException> {
            illegalArgument("foo")
        }
        assertEquals("foo", ex.message)
    }

    @Test
    fun `assertionFailed throws AssertionError with the given message`() {
        val ex = assertThrows<AssertionError> {
            assertionFailed("foo")
        }
        assertEquals("foo", ex.message)
    }

}
