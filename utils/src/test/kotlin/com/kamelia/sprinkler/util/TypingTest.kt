package com.kamelia.sprinkler.util

import org.junit.jupiter.api.Assertions.assertEquals
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
    fun `castOrNull works correctly`() {
        val value: Any = 42
        val r1 = assertDoesNotThrow {
            value.castOrNull<Int>()?.plus(4) ?: 0
        }
        assertEquals(46, r1)
        val r2 = assertDoesNotThrow {
            value.castOrNull<String>()?.length ?: 0
        }
        assertEquals(0, r2)
    }

}
