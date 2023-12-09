package com.kamelia.sprinkler.i18n

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.util.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TimeAccessorsVariableFormatterTest {

    @Test
    fun `time formatter throws an IAE if a non key-param extra args is provided`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.time().format(LocalTime.now(), Locale.ENGLISH, listOf("foo"))
        }
    }

    @Test
    fun `time formatter throws an IAE if an unknown extra args is provided`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.time().format(LocalTime.now(), Locale.ENGLISH, listOf("foo:bar"))
        }
    }

    @Test
    fun `time formatter throws a CCE if the time is not temporal accessor`() {
        assertThrows<ClassCastException> {
            VariableFormatter.time().format("1", Locale.ENGLISH)
        }
    }

    @Test
    fun `time formatter formats a temporal accessor input`() {
        val result = VariableFormatter.time().format(LocalTime.of(1, 0), Locale.US)
        assertEquals("1:00:00 AM", result)
    }

    @Test
    fun `time formatter timeStyle param is correctly applied`() {
        val result = VariableFormatter.time().format(LocalTime.of(1, 0), Locale.US, listOf("timeStyle:short"))
        assertEquals("1:00 AM", result)
    }

    @Test
    fun `time formatter does not accept dateStyle param`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.time().format(LocalTime.of(1, 0), Locale.US, listOf("dateStyle:short"))
        }
    }

    @Test
    fun `time formatter throws on invalid timeStyle value`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.time().format(LocalTime.of(1, 0), Locale.US, listOf("timeStyle:foo"))
        }
    }

    @Test
    fun `date formatter throws an IAE if a non key-param extra args is provided`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.date().format(LocalDate.now(), Locale.ENGLISH, listOf("foo"))
        }
    }

    @Test
    fun `date formatter throws an IAE if an unknown extra args is provided`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.date().format(LocalDate.now(), Locale.ENGLISH, listOf("foo:bar"))
        }
    }

    @Test
    fun `date formatter throws a CCE if the date is not temporal accessor`() {
        assertThrows<ClassCastException> {
            VariableFormatter.date().format("1", Locale.ENGLISH)
        }
    }

    @Test
    fun `date formatter formats a temporal accessor input`() {
        val result = VariableFormatter.date().format(LocalDate.of(1969, Month.DECEMBER, 31), Locale.US)
        assertEquals("Dec 31, 1969", result)
    }

    @Test
    fun `date formatter dateStyle param is correctly applied`() {
        val result = VariableFormatter.date().format(LocalDate.of(1969, Month.DECEMBER, 31), Locale.US, listOf("dateStyle:short"))
        assertEquals("12/31/69", result)
    }

    @Test
    fun `date formatter does not accept timeStyle param`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.date().format(LocalDate.of(1969, Month.DECEMBER, 31), Locale.US, listOf("timeStyle:short"))
        }
    }

    @Test
    fun `date formatter throws on invalid dateStyle value`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.date().format(LocalDate.of(1969, Month.DECEMBER, 31), Locale.US, listOf("dateStyle:foo"))
        }
    }

    @Test
    fun `datetime formatter throws an IAE if a non key-param extra args is provided`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.datetime().format(LocalDate.now(), Locale.ENGLISH, listOf("foo"))
        }
    }

    @Test
    fun `datetime formatter throws an IAE if an unknown extra args is provided`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.datetime().format(LocalDate.now(), Locale.ENGLISH, listOf("foo:bar"))
        }
    }

    @Test
    fun `datetime formatter throws a CCE if the date is not temporal accessor`() {
        assertThrows<ClassCastException> {
            VariableFormatter.datetime().format("1", Locale.ENGLISH)
        }
    }

    @Test
    fun `datetime formatter formats a temporal accessor input`() {
        val result = VariableFormatter.datetime().format(LocalDateTime.of(1969, Month.DECEMBER, 31, 0, 0), Locale.US)
        assertEquals("Dec 31, 1969, 12:00:00 AM", result)
    }

    @Test
    fun `datetime formatter dateStyle param is correctly applied`() {
        val result = VariableFormatter.datetime().format(LocalDateTime.of(1969, Month.DECEMBER, 31, 0, 0), Locale.US, listOf("dateStyle:short"))
        assertEquals("12/31/69, 12:00:00 AM", result)
    }

    @Test
    fun `datetime formatter timeStyle param is correctly applied`() {
        val result = VariableFormatter.datetime().format(LocalDateTime.of(1969, Month.DECEMBER, 31, 0, 0), Locale.US, listOf("timeStyle:short"))
        assertEquals("Dec 31, 1969, 12:00 AM", result)
    }

    @Test
    fun `datetime formatter throws on invalid dateStyle value`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.datetime().format(LocalDateTime.now(), Locale.US, listOf("dateStyle:foo"))
        }
    }

    @Test
    fun `datetime formatter throws on invalid timeStyle value`() {
        assertThrows<IllegalArgumentException> {
            VariableFormatter.datetime().format(LocalDate.now(), Locale.US, listOf("timeStyle:foo"))
        }
    }

}
