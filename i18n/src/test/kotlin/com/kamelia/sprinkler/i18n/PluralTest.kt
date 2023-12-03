package com.kamelia.sprinkler.i18n

import java.util.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PluralTest {

    @Test
    fun `the defaultMapper mapPlural returns the values of the English language`() {
        val mapper = Plural.defaultMapper()
        assertEquals(Plural.ONE, mapper.mapPlural(Locale.ENGLISH, 1))
        repeat(10_000) { count ->
            if (count == 1) return@repeat
            assertEquals(Plural.OTHER, mapper.mapPlural(Locale.ENGLISH, count))
        }
    }

    @Test
    fun `the defaultMapper mapPlural returns the values of the English language even if the locale is not English`() {
        val mapper = Plural.defaultMapper()
        assertEquals(Plural.ONE, mapper.mapPlural(Locale.JAPAN, 1))
        repeat(10_000) { count ->
            if (count == 1) return@repeat
            assertEquals(Plural.OTHER, mapper.mapPlural(Locale.GERMAN, count))
        }
    }

    @Test
    fun `the defaultMapp mapPlural throws an IAE if the count is negative`() {
        val mapper = Plural.defaultMapper()
        assertThrows<IllegalArgumentException> { mapper.mapPlural(Locale.ENGLISH, -1) }
    }

    @Test
    fun `the defaultMapper mapOrdinal returns the values of the English language`() {
        val mapper = Plural.defaultMapper()
        repeat(10_000) { count ->
            if (count == 0) return@repeat
            val plural = mapper.mapOrdinal(Locale.ENGLISH, count)
            when (count % 10) {
                1 -> assertEquals(Plural.ONE, plural)
                2 -> assertEquals(Plural.TWO, plural)
                3 -> assertEquals(Plural.FEW, plural)
                else -> assertEquals(Plural.OTHER, plural)
            }
        }
    }

    @Test
    fun `the defaultMapper mapOrdinal returns the values of the English language even if the locale is not English`() {
        val mapper = Plural.defaultMapper()
        repeat(10_000) { count ->
            if (count == 0) return@repeat
            val plural = mapper.mapOrdinal(Locale.JAPAN, count)
            when (count % 10) {
                1 -> assertEquals(Plural.ONE, plural)
                2 -> assertEquals(Plural.TWO, plural)
                3 -> assertEquals(Plural.FEW, plural)
                else -> assertEquals(Plural.OTHER, plural)
            }
        }
    }

    @Test
    fun `the defaultMapper mapOrdinal throws an IAE if the count is negative or zero`() {
        val mapper = Plural.defaultMapper()
        assertThrows<IllegalArgumentException> { mapper.mapOrdinal(Locale.ENGLISH, -1) }
        assertThrows<IllegalArgumentException> { mapper.mapOrdinal(Locale.ENGLISH, 0) }
    }

}
