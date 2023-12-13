package com.kamelia.sprinkler.i18n

import java.util.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class NumberVariableFormatterTest {

    @Test
    fun `number formatter throws an IAE if an unknown extra args is provided`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.number().format(1, Locale.ENGLISH, listOf("foo" to "bar"))
        }
    }

    @Test
    fun `number formatter throws a CCE if the number is not a number`() {
        assertThrows<ClassCastException> {
            VariableFormatter.number().format("1", Locale.ENGLISH)
        }
    }

    @Test
    fun `number formatter formats a number input`() {
        val result = VariableFormatter.number().format(1, Locale.US)
        assertEquals("1", result)
    }

    @Test
    fun `number minIntDigits extraArgs formatter throws an IAE if the value is not a number`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.number().format(1, Locale.ENGLISH, listOf("minIntDigits" to "foo"))
        }
    }

    @Test
    fun `number maxIntDigits extraArgs formatter throws an IAE if the value is not a number`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.number().format(1, Locale.ENGLISH, listOf("maxIntDigits" to "1.5"))
        }
    }

    @Test
    fun `number minFracDigits extraArgs formatter throws an IAE if the value is not a number`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.number().format(1, Locale.ENGLISH, listOf("minFracDigits" to "foo"))
        }
    }

    @Test
    fun `number maxFracDigits extraArgs formatter throws an IAE if the value is not a number`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.number().format(1, Locale.ENGLISH, listOf("maxFracDigits" to "1.5"))
        }
    }

    @Test
    fun `number groupingUsed extraArgs formatter throws an IAE if the value is not a boolean`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.number().format(1, Locale.ENGLISH, listOf("groupingUsed" to "foo"))
        }
    }

    @Test
    fun `number minIntDigits param is correctly applied`() {
        val result = VariableFormatter.number().format(1, Locale.US, listOf("minIntDigits" to "2"))
        assertEquals("01", result)
    }

    @Test
    fun `number maxIntDigits param is correctly applied`() {
        val result = VariableFormatter.number().format(11, Locale.US, listOf("maxIntDigits" to "1"))
        assertEquals("1", result)
    }

    @Test
    fun `number minFracDigits param is correctly applied`() {
        val result = VariableFormatter.number().format(1.1, Locale.US, listOf("minFracDigits" to "5"))
        assertEquals("1.10000", result)
    }

    @Test
    fun `number maxFracDigits param is correctly applied`() {
        val result = VariableFormatter.number().format(1.15, Locale.US, listOf("maxFracDigits" to "1"))
        assertEquals("1.1", result)
    }

    @Test
    fun `number groupingUsed param is correctly applied`() {
        val result = VariableFormatter.number().format(1000, Locale.US, listOf("groupingUsed" to "true"))
        assertEquals("1,000", result)
    }

    @Test
    fun `number roundingMode param is correctly applied`() {
        val result =
            VariableFormatter.number()
                .format(1.5, Locale.US, listOf("roundingMode" to "HALF_DOWN", "maxFracDigits" to "0"))
        assertEquals("1", result)
    }

}
