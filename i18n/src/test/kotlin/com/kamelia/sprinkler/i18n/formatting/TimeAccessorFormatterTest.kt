package com.kamelia.sprinkler.i18n.formatting

import com.kamelia.sprinkler.i18n.format
import com.kamelia.sprinkler.i18n.formatting.VariableFormatter.Companion.formatArgument
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.util.Locale
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class TimeAccessorFormatterTest {

    @Test
    fun `time formatter ignores if an unknown extra args is provided`() {
        assertDoesNotThrow {
            VariableFormatter.time().format(LocalTime.now(), Locale.ENGLISH, formatArgument("foo", "bar"))
        }
    }

    @Test
    fun `time formatter formats a temporal accessor input`() {
        val result = VariableFormatter.time().format(LocalTime.of(1, 0), Locale.US)
        assertEquals("1:00:00 AM", result)
    }

    @Test
    fun `time formatter timeStyle param is correctly applied`() {
        val result =
            VariableFormatter.time().format(LocalTime.of(1, 0), Locale.US, formatArgument("timeStyle", "short"))
        assertEquals("1:00 AM", result)
    }

    @Test
    fun `time formatter throws on invalid timeStyle value`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.time().format(LocalTime.of(1, 0), Locale.US, formatArgument("timeStyle", "foo"))
        }
    }

    @Test
    fun `date formatter ignore if an unknown extra args is provided`() {
        assertDoesNotThrow {
            VariableFormatter.date().format(LocalDate.now(), Locale.ENGLISH, formatArgument("foo", "bar"))
        }
    }

    @Test
    fun `date formatter formats a temporal accessor input`() {
        val result = VariableFormatter.date().format(LocalDate.of(1969, Month.DECEMBER, 31), Locale.US)
        assertEquals("Dec 31, 1969", result)
    }

    @Test
    fun `date formatter dateStyle param is correctly applied`() {
        val result =
            VariableFormatter.date()
                .format(LocalDate.of(1969, Month.DECEMBER, 31), Locale.US, formatArgument("dateStyle", "short"))
        assertEquals("12/31/69", result)
    }

    @Test
    fun `date formatter throws on invalid dateStyle value`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.date()
                .format(LocalDate.of(1969, Month.DECEMBER, 31), Locale.US, formatArgument("dateStyle", "foo"))
        }
    }

    @Test
    fun `datetime formatter throws an IAE if an unknown extra args is provided`() {
        assertDoesNotThrow {
            VariableFormatter.datetime().format(LocalDateTime.now(), Locale.ENGLISH, formatArgument("foo", "bar"))
        }
    }

    @Test
    fun `datetime formatter formats a temporal accessor input`() {
        val result =
            VariableFormatter.datetime().format(LocalDateTime.of(1969, Month.DECEMBER, 31, 0, 0), Locale.US)
        assertEquals("Dec 31, 1969, 12:00:00 AM", result)
    }

    @Test
    fun `datetime formatter dateStyle param is correctly applied`() {
        val result =
            VariableFormatter.datetime()
                .format(
                    LocalDateTime.of(1969, Month.DECEMBER, 31, 0, 0),
                    Locale.US,
                    formatArgument("dateStyle", "short")
                )
        assertEquals("12/31/69, 12:00:00 AM", result)
    }

    @Test
    fun `datetime formatter timeStyle param is correctly applied`() {
        val result =
            VariableFormatter.datetime()
                .format(
                    LocalDateTime.of(1969, Month.DECEMBER, 31, 0, 0),
                    Locale.US,
                    formatArgument("timeStyle", "short")
                )
        assertEquals("Dec 31, 1969, 12:00 AM", result)
    }

    @Test
    fun `datetime formatter throws on invalid dateStyle value`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.datetime().format(LocalDateTime.now(), Locale.US, formatArgument("dateStyle", "foo"))
        }
    }

    @Test
    fun `datetime formatter throws on invalid timeStyle value`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.datetime().format(LocalDate.now(), Locale.US, formatArgument("timeStyle", "foo"))
        }
    }

    @Test
    fun `builtins coverage`() {
        VariableFormatter.builtins()
    }

}
