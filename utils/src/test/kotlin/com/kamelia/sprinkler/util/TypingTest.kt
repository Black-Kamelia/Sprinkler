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
        val r = assertDoesNotThrow {
            value.castOrNull<Int>() ?: 0
        }
        assertEquals(42, r)
    }

    @Test
    fun `castOrNull returns null when the receiver has the wrong type`() {
        val value: Any = "qsdqfgd"
        val r = assertDoesNotThrow {
            value.castOrNull<Int>()?.plus(4) ?: 0
        }
        assertEquals(0, r)
    }

    @Test
    fun `castOrNull returns null when the receiver is null`() {
        val value: Any? = null
        val r = assertDoesNotThrow {
            value.castOrNull<Int>() ?: 0
        }
        assertEquals(0, r)
    }

    @Test
    fun `castIfNotNull casts when the receiver has the correct type`() {
        val value: Any = 42
        val r = assertDoesNotThrow {
            value.castIfNotNull<Int>()?.plus(4) ?: 0
        }
        assertEquals(46, r)
    }

    @Test
    fun `castIfNotNull throws when the receiver has the wrong type`() {
        val value: Any = "qsdqfgd"
        assertThrows<ClassCastException> {
            value.castIfNotNull<Int>()?.plus(4) ?: 0
        }
    }

    @Test
    fun `castIfNotNull returns null when the receiver is null`() {
        val value: Any? = null
        val r = assertDoesNotThrow {
            value.castIfNotNull<Int>()
        }
        assertNull(r)
    }

    @Test
    fun `cast throws when the receiver has the wrong type`() {
        val value: Any = "qsdqfgd"
        assertThrows<ClassCastException> {
            value.cast<Int>()
        }
    }

    @Test
    fun `cast returns the receiver when it has the correct type`() {
        val value: Any = 42
        val r = assertDoesNotThrow {
            value.cast<Int>()
        }
        assertEquals(42, r)
    }

    @Test
    fun `cast throws NPE when the receiver is null`() {
        val value: Any? = null
        assertThrows<NullPointerException> {
            value.cast<Int>()
        }
    }

}
