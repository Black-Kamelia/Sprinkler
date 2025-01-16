package com.kamelia.sprinkler.i18n.impl

import com.kamelia.sprinkler.i18n.pluralization.ScientificNotationNumber
import com.kamelia.sprinkler.util.entryOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class OptionTest {

    @Test
    fun `count (formatted) throws if the formatted value is not a number`() {
        assertThrows<IllegalArgumentException> {
            count(formatted("foo", emptyMap()))
        }
    }

    @Test
    fun `count (formatted) returns a pair with the count`() {
        val value = 42L
        val count = count(formatted(value, emptyMap()))

        assertEquals(FormattedValueImpl::class.java, count.second.javaClass)
        val formattedValue = count.second as FormattedValueImpl
        assertEquals(value, formattedValue.value)
        assertEquals(Options.COUNT, count.first)
    }

    @Test
    fun `count (long) returns a pair with the count`() {
        val value = 42L
        val count = count(value)

        assertEquals(Options.COUNT, count.first)
        assertEquals(value, count.second)
    }

    @Test
    fun `count (double) returns a pair with the count`() {
        val value = 42.0
        val count = count(value)

        assertEquals(Options.COUNT, count.first)
        assertEquals(value, count.second)
    }

    @Test
    fun `count(ScientificNotationNumber) returns a pair with the count`() {
        val value = ScientificNotationNumber.from(42.0)
        val count = count(value)

        assertEquals(Options.COUNT, count.first)
        assertEquals(value, count.second)
    }

    @Test
    fun `context returns a pair with the context`() {
        val context = context("foo")

        assertEquals(Options.CONTEXT, context.first)
        assertEquals("foo", context.second)
    }

    @Test
    fun `ordinal returns a pair with the ordinal value`() {
        val ordinal = ordinal(false)

        assertEquals(Options.ORDINAL, ordinal.first)
        (ordinal.second)
    }

    @Test
    fun `ordinal default value is true`() {
        val ordinal = ordinal()

        assertEquals(Options.ORDINAL, ordinal.first)
        assertEquals(true, ordinal.second)
    }

    @Test
    fun `java count throws if the formatted value is not a number`() {
        assertThrows<IllegalArgumentException> {
            Options.count(formatted("foo", emptyMap()))
        }
    }

    @Test
    fun `java count returns an entry with the count`() {
        val value = 42L
        val count = Options.count(formatted(value, entryOf("foo", 5)))

        assertEquals(FormattedValueImpl::class.java, count.value.javaClass)
        val formattedValue = count.value as FormattedValueImpl
        assertEquals(value, formattedValue.value)
        assertEquals(Options.COUNT, count.key)
    }

    @Test
    fun `java count (long) returns an entry with the count`() {
        val value = 42L
        val count = Options.count(value)

        assertEquals(Options.COUNT, count.key)
        assertEquals(value, count.value)
    }

    @Test
    fun `java count (double) returns an entry with the count`() {
        val value = 42.0
        val count = Options.count(value)

        assertEquals(Options.COUNT, count.key)
        assertEquals(value, count.value)
    }

    @Test
    fun `java count (ScientificNotationNumber) returns an entry with the count`() {
        val value = ScientificNotationNumber.from(42.0)
        val count = Options.count(value)

        assertEquals(Options.COUNT, count.key)
        assertEquals(value, count.value)
    }

    @Test
    fun `java context returns an entry with the context`() {
        val context = Options.context("foo")

        assertEquals(Options.CONTEXT, context.key)
        assertEquals("foo", context.value)
    }

    @Test
    fun `java ordinal returns an entry with the ordinal value`() {
        val ordinal = Options.ordinal(false)

        assertEquals(Options.ORDINAL, ordinal.key)
        assertEquals(false, ordinal.value)
    }

    @Test
    fun `java ordinal default value is true`() {
        val ordinal = Options.ordinal()

        assertEquals(Options.ORDINAL, ordinal.key)
        assertEquals(true, ordinal.value)
    }


}
