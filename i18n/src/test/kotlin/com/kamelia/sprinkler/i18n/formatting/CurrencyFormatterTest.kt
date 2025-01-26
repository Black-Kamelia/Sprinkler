package com.kamelia.sprinkler.i18n.formatting

import com.kamelia.sprinkler.i18n.format
import com.kamelia.sprinkler.i18n.formatting.VariableFormatter.Companion.formatArgument
import java.math.RoundingMode
import java.util.Locale
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class CurrencyFormatterTest {

    @Test
    fun `currency formatter ignores if an unknown extra args is provided`() {
        assertDoesNotThrow {
            VariableFormatter.currency().format(1, Locale.ENGLISH, formatArgument("foo", "bar"))
        }
    }

    @Test
    fun `currency formatter formats a number input`() {
        val result = VariableFormatter.currency().format(1, Locale.US)
        assertEquals("$1.00", result)
    }

    @Test
    fun `currency minIntDigits extraArgs formatter throws an IAE if the value is a string that does not represent an int`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.currency().format(1, Locale.ENGLISH, formatArgument("minIntDigits", "foo"))
        }
    }

    @Test
    fun `currency minIntDigits extraArgs formatter throws an IAE if the value is not a string or an int`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.currency()
                .format(StringBuilder(), 1, Locale.ENGLISH, formatArgument("minIntDigits", Any()))
        }
    }

    @Test
    fun `currency maxIntDigits extraArgs formatter throws an IAE if the value is not an int representation`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.currency().format(1, Locale.ENGLISH, formatArgument("maxIntDigits", "1.5"))
        }
    }

    @Test
    fun `currency maxIntDigits extraArgs formatter throws an IAE if the value is a string or an int`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.currency()
                .format(StringBuilder(), 1, Locale.ENGLISH, formatArgument("maxIntDigits", Any()))
        }
    }

    @Test
    fun `currency minFracDigits extraArgs formatter throws an IAE if the value is not a number`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.currency().format(1, Locale.ENGLISH, formatArgument("minFracDigits", "foo"))
        }
    }

    @Test
    fun `currency maxFracDigits extraArgs formatter throws an IAE if the value is not a number`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.currency().format(1, Locale.ENGLISH, formatArgument("maxFracDigits", "1.5"))
        }
    }

    @Test
    fun `currency maxFracDigits extraArgs formatter throws an IAE if the value is a string or an int`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.currency()
                .format(StringBuilder(), 1, Locale.ENGLISH, formatArgument("maxFracDigits", Any()))
        }
    }

    @Test
    fun `currency groupingUsed extraArgs formatter throws an IAE if the value is not a boolean`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.currency().format(1, Locale.ENGLISH, formatArgument("groupingUsed", "foo"))
        }
    }

    @Test
    fun `currency groupingUsed extraArgs formatter throws an IAE if the value is not a string or a boolean`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.currency()
                .format(StringBuilder(), 1, Locale.ENGLISH, formatArgument("groupingUsed", Any()))
        }
    }

    @Test
    fun `currency roundingMode extraArgs formatter throws an IAE if the value is not a RoundingMode label`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.currency().format(1, Locale.ENGLISH, formatArgument("roundingMode", "foo"))
        }
    }

    @Test
    fun `currency roundingMode extraArgs formatter throws an IAE if the value is not a string or a RoundingMode label`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.currency()
                .format(StringBuilder(), 1, Locale.ENGLISH, formatArgument("roundingMode", Any()))
        }
    }

    @Test
    fun `currency minIntDigits param is correctly applied`() {
        val result = VariableFormatter.currency().format(1, Locale.US, formatArgument("minIntDigits", "2"))
        assertEquals("$01.00", result)
    }

    @Test
    fun `currency maxIntDigits param is correctly applied`() {
        val result = VariableFormatter.currency().format(11, Locale.US, formatArgument("maxIntDigits", "1"))
        assertEquals("$1.00", result)
    }

    @Test
    fun `currency minFracDigits param is correctly applied`() {
        val result = VariableFormatter.currency().format(1, Locale.US, formatArgument("minFracDigits", "3"))
        assertEquals("$1.000", result)
    }

    @Test
    fun `currency maxFracDigits param is correctly applied`() {
        val result = VariableFormatter.currency().format(1.111, Locale.US, formatArgument("maxFracDigits", "2"))
        assertEquals("$1.11", result)
    }

    @Test
    fun `currency groupingUsed param is correctly applied with true`() {
        val result = VariableFormatter.currency().format(1111, Locale.US, formatArgument("groupingUsed", "true"))
        assertEquals("$1,111.00", result)
    }

    @Test
    fun `currency groupingUsed param is correctly applied with false`() {
        val result = VariableFormatter.currency().format(1111, Locale.US, formatArgument("groupingUsed", "false"))
        assertEquals("$1111.00", result)
    }

    @Test
    fun `currency groupingUsed param is correctly applied with literal boolean`() {
        val result = buildString {
            VariableFormatter.currency().format(this, 1111, Locale.US, formatArgument("groupingUsed", false))
        }
        assertEquals("$1111.00", result)
    }

    @Test
    fun `currency roundingMode param is correctly applied`() {
        val result =
            VariableFormatter.currency()
                .format(1.5, Locale.US, formatArgument("maxFracDigits", "0"), formatArgument("roundingMode", "HALF_UP"))
        assertEquals("$2", result)
    }

    @Test
    fun `currency roundingMode param is correctly applied with literal RoundingMode`() {
        val result = buildString {
            VariableFormatter.currency()
                .format(
                    this,
                    1.5,
                    Locale.US,
                    formatArgument("maxFracDigits", 0),
                    formatArgument("roundingMode", RoundingMode.HALF_UP)
                )
        }
        assertEquals("$2", result)
    }

}
