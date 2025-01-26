package com.kamelia.sprinkler.i18n.formatting

import com.kamelia.sprinkler.i18n.format
import com.kamelia.sprinkler.i18n.formatting.VariableFormatter.Companion.formatArgument
import java.math.RoundingMode
import java.util.Locale
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class PercentFormatterTest {

    @Test
    fun `percent formatter ignores if an unknown extra args is provided`() {
        assertDoesNotThrow {
            VariableFormatter.percent().format(1, Locale.ENGLISH, formatArgument("foo", "bar"))
        }
    }

    @Test
    fun `percent formatter formats a number input`() {
        val result = VariableFormatter.percent().format(1, Locale.US)
        assertEquals("100%", result)
    }

    @Test
    fun `percent minIntDigits extraArgs formatter throws an IAE if the value is a string that does not represent an int`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.percent().format(1, Locale.ENGLISH, formatArgument("minIntDigits", "foo"))
        }
    }

    @Test
    fun `percent minIntDigits extraArgs formatter throws an IAE if the value is not a string or an int`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.percent()
                .format(StringBuilder(), 1, Locale.ENGLISH, formatArgument("minIntDigits", Any()))
        }
    }

    @Test
    fun `percent maxIntDigits extraArgs formatter throws an IAE if the value is not an int representation`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.percent().format(1, Locale.ENGLISH, formatArgument("maxIntDigits", "1.5"))
        }
    }

    @Test
    fun `percent maxIntDigits extraArgs formatter throws an IAE if the value is a string or an int`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.percent()
                .format(StringBuilder(), 1, Locale.ENGLISH, formatArgument("maxIntDigits", Any()))
        }
    }

    @Test
    fun `percent minFracDigits extraArgs formatter throws an IAE if the value is not a number`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.percent().format(1, Locale.ENGLISH, formatArgument("minFracDigits", "foo"))
        }
    }

    @Test
    fun `percent maxFracDigits extraArgs formatter throws an IAE if the value is not a number`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.percent().format(1, Locale.ENGLISH, formatArgument("maxFracDigits", "1.5"))
        }
    }

    @Test
    fun `percent maxFracDigits extraArgs formatter throws an IAE if the value is a string or an int`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.percent()
                .format(StringBuilder(), 1, Locale.ENGLISH, formatArgument("maxFracDigits", Any()))
        }
    }

    @Test
    fun `percent groupingUsed extraArgs formatter throws an IAE if the value is not a boolean`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.percent().format(1, Locale.ENGLISH, formatArgument("groupingUsed", "foo"))
        }
    }

    @Test
    fun `percent groupingUsed extraArgs formatter throws an IAE if the value is not a string or a boolean`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.percent()
                .format(StringBuilder(), 1, Locale.ENGLISH, formatArgument("groupingUsed", Any()))
        }
    }

    @Test
    fun `percent roundingMode extraArgs formatter throws an IAE if the value is not a RoundingMode label`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.percent().format(1, Locale.ENGLISH, formatArgument("roundingMode", "foo"))
        }
    }

    @Test
    fun `percent roundingMode extraArgs formatter throws an IAE if the value is not a string or a RoundingMode label`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.percent()
                .format(StringBuilder(), 1, Locale.ENGLISH, formatArgument("roundingMode", Any()))
        }
    }

    @Test
    fun `percent minIntDigits param is correctly applied`() {
        val result = VariableFormatter.percent().format(0.01, Locale.US, formatArgument("minIntDigits", "2"))
        assertEquals("01%", result)
    }

    @Test
    fun `percent maxIntDigits param is correctly applied`() {
        val result = VariableFormatter.percent().format(0.11, Locale.US, formatArgument("maxIntDigits", "1"))
        assertEquals("1%", result)
    }

    @Test
    fun `percent minFracDigits param is correctly applied`() {
        val result = VariableFormatter.percent().format(0.01, Locale.US, formatArgument("minFracDigits", "3"))
        assertEquals("1.000%", result)
    }

    @Test
    fun `percent maxFracDigits param is correctly applied`() {
        val result = VariableFormatter.percent().format(0.01111, Locale.US, formatArgument("maxFracDigits", "2"))
        assertEquals("1.11%", result)
    }

    @Test
    fun `percent groupingUsed param is correctly applied with true`() {
        val result = VariableFormatter.percent().format(11.11, Locale.US, formatArgument("groupingUsed", "true"))
        assertEquals("1,111%", result)
    }

    @Test
    fun `percent groupingUsed param is correctly applied with false`() {
        val result = VariableFormatter.percent().format(11.11, Locale.US, formatArgument("groupingUsed", "false"))
        assertEquals("1111%", result)
    }

    @Test
    fun `percent groupingUsed param is correctly applied with literal boolean`() {
        val result = buildString {
            VariableFormatter.percent().format(this, 11.11, Locale.US, formatArgument("groupingUsed", false))
        }
        assertEquals("1111%", result)
    }

    @Test
    fun `percent roundingMode param is correctly applied`() {
        val result =
            VariableFormatter.percent()
                .format(0.015, Locale.US, formatArgument("maxFracDigits", "0"), formatArgument("roundingMode", "HALF_UP"))
        assertEquals("2%", result)
    }

    @Test
    fun `percent roundingMode param is correctly applied with literal RoundingMode`() {
        val result = buildString {
            VariableFormatter.percent()
                .format(
                    this,
                    0.015,
                    Locale.US,
                    formatArgument("maxFracDigits", 0),
                    formatArgument("roundingMode", RoundingMode.HALF_UP)
                )
        }
        assertEquals("2%", result)
    }

}
