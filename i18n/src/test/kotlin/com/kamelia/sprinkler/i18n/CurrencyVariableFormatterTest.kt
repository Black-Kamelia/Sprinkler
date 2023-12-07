package com.kamelia.sprinkler.i18n

import java.util.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CurrencyVariableFormatterTest {

    @Test
    fun `currency formatter throws an IAE if a non key-param extra args is provided`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.currency().format(1, Locale.ENGLISH, listOf("foo"))
        }
    }

    @Test
    fun `currency formatter throws an IAE if an unknown extra args is provided`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.currency().format(1, Locale.ENGLISH, listOf("foo:bar"))
        }
    }

    @Test
    fun `currency formatter throws a CCE if the currency is not a number`() {
        assertThrows<ClassCastException> {
            VariableFormatter.currency().format("1", Locale.ENGLISH)
        }
    }

    @Test
    fun `currency formatter formats a number input`() {
        val result = VariableFormatter.currency().format(1, Locale.US)
        assertEquals("$1.00", result)
    }

    @Test
    fun `currency minIntDigits extraArgs formatter throws an IAE if the value is not a number`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.currency().format(1, Locale.ENGLISH, listOf("minIntDigits:foo"))
        }
    }

    @Test
    fun `currency maxIntDigits extraArgs formatter throws an IAE if the value is not a number`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.currency().format(1, Locale.ENGLISH, listOf("maxIntDigits:1.5"))
        }
    }

    @Test
    fun `currency minFracDigits extraArgs formatter throws an IAE if the value is not a number`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.currency().format(1, Locale.ENGLISH, listOf("minFracDigits:foo"))
        }
    }

    @Test
    fun `currency maxFracDigits extraArgs formatter throws an IAE if the value is not a number`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.currency().format(1, Locale.ENGLISH, listOf("maxFracDigits:1.5"))
        }
    }

    @Test
    fun `currency groupingUsed extraArgs formatter throws an IAE if the value is not a boolean`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.currency().format(1, Locale.ENGLISH, listOf("groupingUsed:foo"))
        }
    }

    @Test
    fun `currency roundingMode extraArgs formatter throws an IAE if the value is not a RoundingMode label`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.currency().format(1, Locale.ENGLISH, listOf("roundingMode:foo"))
        }
    }

    @Test
    fun `currency minIntDigits param is correctly applied`() {
        val result = VariableFormatter.currency().format(1, Locale.US, listOf("minIntDigits:2"))
        assertEquals("$01.00", result)
    }

    @Test
    fun `currency maxIntDigits param is correctly applied`() {
        val result = VariableFormatter.currency().format(11, Locale.US, listOf("maxIntDigits:1"))
        assertEquals("$1.00", result)
    }

    @Test
    fun `currency minFracDigits param is correctly applied`() {
        val result = VariableFormatter.currency().format(1, Locale.US, listOf("minFracDigits:3"))
        assertEquals("$1.000", result)
    }

    @Test
    fun `currency maxFracDigits param is correctly applied`() {
        val result = VariableFormatter.currency().format(1.111, Locale.US, listOf("maxFracDigits:2"))
        assertEquals("$1.11", result)
    }

    @Test
    fun `currency groupingUsed param is correctly applied`() {
        val result = VariableFormatter.currency().format(1111, Locale.US, listOf("groupingUsed:true"))
        assertEquals("$1,111.00", result)
    }

    @Test
    fun `currency roundingMode param is correctly applied`() {
        val result = VariableFormatter.currency().format(1.5, Locale.US, listOf("maxFracDigits:0", "roundingMode:HALF_UP"))
        assertEquals("$2", result)
    }

}
