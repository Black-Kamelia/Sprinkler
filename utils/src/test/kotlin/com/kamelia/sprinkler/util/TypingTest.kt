package com.kamelia.sprinkler.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class TypingTest {

    @Test
    fun `unsafeCast works correctly`() {
        val value: Any = 42
        assertDoesNotThrow {
            value.unsafeCast<Int>().plus(4)
        }
        assertThrows<ClassCastException> {
            value.unsafeCast<String>().length
        }
    }

    @Test
    fun `castOrNull casts when the receiver has the correct type`() {
        val value: Any = 42
        assertEquals(value, value.castOrNull<Int>())
    }

    @Test
    fun `castOrNull returns null when the receiver has the wrong type`() {
        assertNull("qsdqfgd".castOrNull<Int>())
    }

    @Test
    fun `castOrNull returns null when the receiver is null`() {
        assertNull(null.castOrNull<Int>())
    }

    @Test
    fun `castIfNotNull casts when the receiver has the correct type`() {
        val value: Any = 42
        assertEquals(value, value.castIfNotNull<Int>())
    }

    @Test
    fun `castIfNotNull throws when the receiver has the wrong type`() {
        assertThrows<ClassCastException> {
            "aaaaa".castIfNotNull<Int>()
        }
    }

    @Test
    fun `castIfNotNull returns null when the receiver is null`() {
        assertNull(null.castIfNotNull<Int>())
    }

    @Test
    fun `cast throws when the receiver has the wrong type`() {
        assertThrows<ClassCastException> {
            "qsdqfgd".cast<Int>()
        }
    }

    @Test
    fun `cast returns the receiver when it has the correct type`() {
        val value: Any = 42
        assertEquals(value, value.cast<Int>())
    }

    @Test
    fun `cast throws NPE when the receiver is null`() {
        val value: Any? = null
        assertThrows<NullPointerException> {
            value.cast<Int>()
        }
    }

}
