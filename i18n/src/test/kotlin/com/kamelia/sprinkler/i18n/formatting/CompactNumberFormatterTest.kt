package com.kamelia.sprinkler.i18n.formatting

import com.kamelia.sprinkler.i18n.format
import com.kamelia.sprinkler.i18n.formatting.VariableFormatter.Companion.formatArgument
import java.math.RoundingMode
import java.util.Locale
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class CompactNumberFormatterTest {

    @Test
    fun `compactNumber formatter ignores if an unknown extra args is provided`() {
        assertDoesNotThrow {
            VariableFormatter.compactNumber().format(1, Locale.ENGLISH, formatArgument("foo", "bar"))
        }
    }

    @Test
    fun `compactNumber formatter formats a number input`() {
        val result = VariableFormatter.compactNumber().format(1e3, Locale.US)
        assertEquals("1K", result)
    }

    @Test
    fun `compactNumber minIntDigits extraArgs formatter throws an IAE if the value is a string that does not represent an int`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.compactNumber().format(1, Locale.ENGLISH, formatArgument("minIntDigits", "foo"))
        }
    }

    @Test
    fun `compactNumber minIntDigits extraArgs formatter throws an IAE if the value is not a string or an int`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.compactNumber()
                .format(StringBuilder(), 1, Locale.ENGLISH, formatArgument("minIntDigits", Any()))
        }
    }

    @Test
    fun `compactNumber maxIntDigits extraArgs formatter throws an IAE if the value is not an int representation`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.compactNumber().format(1, Locale.ENGLISH, formatArgument("maxIntDigits", "1.5"))
        }
    }

    @Test
    fun `compactNumber maxIntDigits extraArgs formatter throws an IAE if the value is a string or an int`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.compactNumber()
                .format(StringBuilder(), 1, Locale.ENGLISH, formatArgument("maxIntDigits", Any()))
        }
    }

    @Test
    fun `compactNumber minFracDigits extraArgs formatter throws an IAE if the value is not a number`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.compactNumber().format(1, Locale.ENGLISH, formatArgument("minFracDigits", "foo"))
        }
    }

    @Test
    fun `compactNumber maxFracDigits extraArgs formatter throws an IAE if the value is not a number`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.compactNumber().format(1, Locale.ENGLISH, formatArgument("maxFracDigits", "1.5"))
        }
    }

    @Test
    fun `compactNumber maxFracDigits extraArgs formatter throws an IAE if the value is a string or an int`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.compactNumber()
                .format(StringBuilder(), 1, Locale.ENGLISH, formatArgument("maxFracDigits", Any()))
        }
    }

    @Test
    fun `compactNumber groupingUsed extraArgs formatter throws an IAE if the value is not a boolean`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.compactNumber().format(1, Locale.ENGLISH, formatArgument("groupingUsed", "foo"))
        }
    }

    @Test
    fun `compactNumber groupingUsed extraArgs formatter throws an IAE if the value is not a string or a boolean`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.compactNumber()
                .format(StringBuilder(), 1, Locale.ENGLISH, formatArgument("groupingUsed", Any()))
        }
    }

    @Test
    fun `compactNumber roundingMode extraArgs formatter throws an IAE if the value is not a RoundingMode label`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.compactNumber().format(1, Locale.ENGLISH, formatArgument("roundingMode", "foo"))
        }
    }

    @Test
    fun `compactNumber roundingMode extraArgs formatter throws an IAE if the value is not a string or a RoundingMode label`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.compactNumber()
                .format(StringBuilder(), 1, Locale.ENGLISH, formatArgument("roundingMode", Any()))
        }
    }

    @Test
    fun `compactNumber minIntDigits param is correctly applied`() {
        val result = VariableFormatter.compactNumber().format(1e3, Locale.US, formatArgument("minIntDigits", "2"))
        assertEquals("01K", result)
    }

    @Test
    fun `compactNumber maxIntDigits param is correctly applied`() {
        val result = VariableFormatter.compactNumber().format(11e3, Locale.US, formatArgument("maxIntDigits", "1"))
        assertEquals("1K", result)
    }

    @Test
    fun `compactNumber minFracDigits param is correctly applied`() {
        val result = VariableFormatter.compactNumber().format(1.2e3, Locale.US, formatArgument("minFracDigits", "3"))
        assertEquals("1.200K", result)
    }

    @Test
    fun `compactNumber maxFracDigits param is correctly applied`() {
        val result = VariableFormatter.compactNumber().format(1.111e3, Locale.US, formatArgument("maxFracDigits", "2"))
        assertEquals("1.11K", result)
    }

    @Test
    fun `compactNumber roundingMode param is correctly applied`() {
        val result =
            VariableFormatter.compactNumber()
                .format(2.5e3, Locale.US, formatArgument("maxFracDigits", "0"), formatArgument("roundingMode", "HALF_UP"))
        assertEquals("3K", result)
    }

    @Test
    fun `compactNumber roundingMode param is correctly applied with literal RoundingMode`() {
        val result = buildString {
            VariableFormatter.compactNumber()
                .format(
                    this,
                    2.5e3,
                    Locale.US,
                    formatArgument("maxFracDigits", 0),
                    formatArgument("roundingMode", RoundingMode.HALF_DOWN)
                )
        }
        assertEquals("2K", result)
    }

}
