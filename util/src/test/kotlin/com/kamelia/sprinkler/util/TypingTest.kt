package com.kamelia.sprinkler.util

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

}
