package com.kamelia.sprinkler.i18n.formatting

import com.kamelia.sprinkler.i18n.format
import com.kamelia.sprinkler.i18n.formatting.VariableFormatter.Companion.formatArgument
import java.util.Locale
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class NumberFormatterTest {

    @Test
    fun `number formatter formats a number input`() {
        val result = VariableFormatter.number().format(1, Locale.US)
        assertEquals("1", result)
    }

    @Test
    fun `number minIntDigits extraArgs formatter throws an IAE if the value is not a number`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.number().format(1, Locale.ENGLISH, formatArgument("minIntDigits", "foo"))
        }
    }

    @Test
    fun `number maxIntDigits extraArgs formatter throws an IAE if the value is not a number`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.number().format(1, Locale.ENGLISH, formatArgument("maxIntDigits", "1.5"))
        }
    }

    @Test
    fun `number minFracDigits extraArgs formatter throws an IAE if the value is not a number`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.number().format(1, Locale.ENGLISH, formatArgument("minFracDigits", "foo"))
        }
    }

    @Test
    fun `number maxFracDigits extraArgs formatter throws an IAE if the value is not a number`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.number().format(1, Locale.ENGLISH, formatArgument("maxFracDigits", "1.5"))
        }
    }

    @Test
    fun `number groupingUsed extraArgs formatter throws an IAE if the value is not a boolean`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.number().format(1, Locale.ENGLISH, formatArgument("groupingUsed", "foo"))
        }
    }

    @Test
    fun `number minIntDigits param is correctly applied`() {
        val result = VariableFormatter.number().format(1, Locale.US, formatArgument("minIntDigits", "2"))
        assertEquals("01", result)
    }

    @Test
    fun `number maxIntDigits param is correctly applied`() {
        val result = VariableFormatter.number().format(11, Locale.US, formatArgument("maxIntDigits", "1"))
        assertEquals("1", result)
    }

    @Test
    fun `number minFracDigits param is correctly applied`() {
        val result = VariableFormatter.number().format(1.1, Locale.US, formatArgument("minFracDigits", "5"))
        assertEquals("1.10000", result)
    }

    @Test
    fun `number maxFracDigits param is correctly applied`() {
        val result = VariableFormatter.number().format(1.15, Locale.US, formatArgument("maxFracDigits", "1"))
        assertEquals("1.1", result)
    }

    @Test
    fun `number groupingUsed param is correctly applied`() {
        val result = VariableFormatter.number().format(1000, Locale.US, formatArgument("groupingUsed", "true"))
        assertEquals("1,000", result)
    }

    @Test
    fun `number roundingMode param is correctly applied`() {
        val result =
            VariableFormatter.number()
                .format(
                    1.5,
                    Locale.US,
                    formatArgument("roundingMode", "HALF_DOWN"),
                    formatArgument("maxFracDigits", "0")
                )
        assertEquals("1", result)
    }

}
