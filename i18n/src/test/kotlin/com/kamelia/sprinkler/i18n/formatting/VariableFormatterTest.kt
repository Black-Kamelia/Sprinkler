package com.kamelia.sprinkler.i18n.formatting

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class VariableFormatterTest {

    @Test
    fun `parameter toString is redefined`() {
        val arg = VariableFormatter.formatArgument("name", "value")
        assertEquals("name: value", arg.toString())
    }

}
