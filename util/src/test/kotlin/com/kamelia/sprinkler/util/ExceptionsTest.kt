package com.kamelia.sprinkler.util

import java.io.IOException
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
    fun `illegalArgument withouth message throws IllegalArgumentException`() {
        val ex = assertThrows<IllegalArgumentException> {
            illegalArgument()
        }
        assertEquals(null, ex.message)
    }

    @Test
    fun `assertionFailed throws AssertionError with the given message`() {
        val ex = assertThrows<AssertionError> {
            assertionFailed("foo")
        }
        assertEquals("foo", ex.message)
    }

    @Test
    fun `assertionFailed without message throws AssertionError`() {
        val ex = assertThrows<AssertionError> {
            assertionFailed()
        }
        assertEquals(null, ex.message)
    }

    @Test
    fun `illegalState throws IllegalStateException with the given message`() {
        val ex = assertThrows<IllegalStateException> {
            illegalState("foo")
        }
        assertEquals("foo", ex.message)
    }

    @Test
    fun `illegalState without message throws IllegalStateException`() {
        val ex = assertThrows<IllegalStateException> {
            illegalState()
        }
        assertEquals(null, ex.message)
    }

    @Test
    fun `unsupportedOperation throws UnsupportedOperationException with the given message`() {
        val ex = assertThrows<UnsupportedOperationException> {
            unsupportedOperation("foo")
        }
        assertEquals("foo", ex.message)
    }

    @Test
    fun `unsupportedOperation without message throws UnsupportedOperationException`() {
        val ex = assertThrows<UnsupportedOperationException> {
            unsupportedOperation()
        }
        assertEquals(null, ex.message)
    }

    @Test
    fun `indexOutOfBounds throws IndexOutOfBoundsException with the given message`() {
        val ex = assertThrows<IndexOutOfBoundsException> {
            indexOutOfBounds("foo")
        }
        assertEquals("foo", ex.message)
    }

    @Test
    fun `indexOutOfBounds without message throws IndexOutOfBoundsException`() {
        val ex = assertThrows<IndexOutOfBoundsException> {
            indexOutOfBounds()
        }
        assertEquals(null, ex.message)
    }

    @Test
    fun `noSuchElement throws NoSuchElementException with the given message`() {
        val ex = assertThrows<NoSuchElementException> {
            noSuchElement("foo")
        }
        assertEquals("foo", ex.message)
    }

    @Test
    fun `noSuchElement without message throws NoSuchElementException`() {
        val ex = assertThrows<NoSuchElementException> {
            noSuchElement()
        }
        assertEquals(null, ex.message)
    }

    @Test
    fun `ioException throws IOException with the given message`() {
        val ex = assertThrows<IOException> {
            ioException("foo")
        }
        assertEquals("foo", ex.message)
    }

    @Test
    fun `ioException without message throws IOException`() {
        val ex = assertThrows<IOException> {
            ioException()
        }
        assertEquals(null, ex.message)
    }

}
