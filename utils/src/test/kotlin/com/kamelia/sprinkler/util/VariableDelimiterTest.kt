package com.kamelia.sprinkler.util

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class VariableDelimiterTest {

    @Test
    fun `constructor throws an exception if delimiters are the same`() {
        assertThrows<IllegalArgumentException> {
            VariableDelimiter('{', '{')
        }
    }

}
