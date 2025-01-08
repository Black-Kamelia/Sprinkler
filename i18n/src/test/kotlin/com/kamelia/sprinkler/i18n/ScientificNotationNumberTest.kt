package com.kamelia.sprinkler.i18n

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.math.pow

class ScientificNotationNumberTest {

    @Test
    fun `from(long) 0 returns 0e0`() {
        val number = ScientificNotationNumber.from(0L)
        assertEquals(0.0, number.significand)
        assertEquals(0, number.exponent)
    }

    @Test
    fun `from(long) create a valid instance (n being a one digit positive number)`() {
        val value = 7L
        val number = ScientificNotationNumber.from(value)

        assertEquals(value.toDouble(), number.significand)
        assertEquals(0, number.exponent)
    }

    @Test
    fun `from(long) create a valid instance (n being a one digit negative number)`() {
        val value = -7L
        val number = ScientificNotationNumber.from(value)

        assertEquals(value.toDouble(), number.significand)
        assertEquals(0, number.exponent)
        assertEquals(value, (number.significand * 10.0.pow(number.exponent)).toLong())
    }

    @Test
    fun `from(long) create a valid instance (n being a two digit positive number)`() {
        val value = 134657346457
        val number = ScientificNotationNumber.from(value)

        assertEquals(1.34657346457, number.significand)
        assertEquals(11, number.exponent)
        assertEquals(value, (number.significand * 10.0.pow(number.exponent)).toLong())
    }

    @Test
    fun `from(long) returns the correct value for toLong()`() {
        repeat(200) {
            val v = it - 100L
            if (v == 0L) return@repeat
            val number = ScientificNotationNumber.from(v)
            assertEquals(v, number.toLong())
        }
    }

    @Test
    fun `from(long) returns the correct value for toDouble()`() {
        repeat(200) {
            val v = it - 100L
            if (v == 0L) return@repeat
            val number = ScientificNotationNumber.from(v)
            assertEquals(v.toDouble(), number.toDouble())
        }
    }

    @Test
    fun `from(long) returns the same value for toLong() and value`() {
        repeat(200) {
            val v = it - 100L
            if (v == 0L) return@repeat
            val number = ScientificNotationNumber.from(v)
            assertEquals(number.value, number.toLong())
        }
    }

    @Test
    fun `from(long) returns true for isInteger`() {
        assertEquals(true, ScientificNotationNumber.from(5).isInteger)
    }

    @Test
    fun `from(long) equals is redefined correctly`() {
        val n1 = ScientificNotationNumber.from(5)
        val n2 = ScientificNotationNumber.from(5)
        val n3 = ScientificNotationNumber.from(6)
        val n4 = ScientificNotationNumber.from(50)
        assertEquals(n1, n2)
        assertEquals(n1.hashCode(), n2.hashCode())
        assertNotEquals(n1, n3)
        assertNotEquals(n1, "foo")
        assertNotEquals(n1, n4)
    }

    @Test
    fun `from(long) toString is redefined correctly`() {
        val n1 = ScientificNotationNumber.from(5)
        assertEquals("5e0", n1.toString())
        val n2 = ScientificNotationNumber.from(510)
        assertEquals("5.1e2", n2.toString())
    }

    @Test
    fun `from(double) returns 0e0 for 0`() {
        val number = ScientificNotationNumber.from(0.0)
        assertEquals(0.0, number.significand)
        assertEquals(0, number.exponent)
    }

    @Test
    fun `from(double) throws with NaN`() {
        assertThrows<IllegalArgumentException> {
            ScientificNotationNumber.from(Double.NaN)
        }
    }

    @Test
    fun `from(double) throws with +Infinity`() {
        assertThrows<IllegalArgumentException> {
            ScientificNotationNumber.from(Double.POSITIVE_INFINITY)
        }
    }

    @Test
    fun `from(double) throws with -Infinity`() {
        assertThrows<IllegalArgumentException> {
            ScientificNotationNumber.from(Double.NEGATIVE_INFINITY)
        }
    }

    @Test
    fun `from(double) create a valid instance (n being a one digit positive number)`() {
        val value = 7.0
        val number = ScientificNotationNumber.from(value)

        assertEquals(value, number.significand)
        assertEquals(0, number.exponent)
    }

    @Test
    fun `from(double) create a valid instance (n being a one digit negative number)`() {
        val value = -7.0
        val number = ScientificNotationNumber.from(value)

        assertEquals(value, number.significand)
        assertEquals(0, number.exponent)
        assertEquals(value, (number.significand * 10.0.pow(number.exponent)))
    }

    @Test
    fun `from(double) create a valid instance (n being a two digit positive number)`() {
        val value = 134657346457.0
        val number = ScientificNotationNumber.from(value)

        assertEquals(1.34657346457, number.significand)
        assertEquals(11, number.exponent)
        assertEquals(value, (number.significand * 10.0.pow(number.exponent)))
    }

    @Test
    fun `from(double) create a valid instance (n being between -1 and 1)`() {
        val value = 0.7
        val number = ScientificNotationNumber.from(value)

        assertEquals(7.0, number.significand)
        assertEquals(-1, number.exponent)
        assertEquals(value, (number.significand * 10.0.pow(number.exponent)), 1e-9)
    }

    @Test
    fun `from(double) returns the correct value for toLong()`() {
        repeat(200) {
            val v = it - 100.0
            if (v == 0.0) return@repeat
            val number = ScientificNotationNumber.from(v)
            assertEquals(v.toLong(), number.toLong())
        }
    }

    @Test
    fun `from(double) returns the correct value for toDouble()`() {
        repeat(200) {
            val v = it - 100.0
            if (v == 0.0) return@repeat
            val number = ScientificNotationNumber.from(v)
            assertEquals(v, number.toDouble())
        }
    }

    @Test
    fun `from(double) returns the same value for toDouble() and value`() {
        repeat(200) {
            val v = it - 100.0
            if (v == 0.0) return@repeat
            val number = ScientificNotationNumber.from(v)
            assertEquals(number.value, number.toDouble())
        }
    }

    @Test
    fun `from(double) returns false for isInteger`() {
        assertEquals(false, ScientificNotationNumber.from(5.0).isInteger)
    }

    @Test
    fun `from(double) equals is redefined correctly`() {
        val n1 = ScientificNotationNumber.from(5.0)
        val n2 = ScientificNotationNumber.from(5.0)
        val n3 = ScientificNotationNumber.from(6.0)
        val n4 = ScientificNotationNumber.from(50.0)
        assertEquals(n1, n2)
        assertEquals(n1.hashCode(), n2.hashCode())
        assertNotEquals(n1, n3)
        assertNotEquals(n1, "foo")
        assertNotEquals(n1, n4)
    }

    @Test
    fun `from(double) toString is redefined correctly`() {
        val n1 = ScientificNotationNumber.from(5.0)
        assertEquals("5.0e0", n1.toString())
    }

    @Test
    fun `create returns an IntegerSci instance when the value is an integer`() {
        val number = ScientificNotationNumber.create(5.0, 0)
        assertEquals(5.0, number.significand)
        assertEquals(0, number.exponent)
        assertTrue(number.isInteger)
    }

    @Test
    fun `create returns a DecimalSci instance when the value is not an integer`() {
        val number = ScientificNotationNumber.create(5.23, 1)
        assertEquals(5.23, number.significand)
        assertEquals(1, number.exponent)
        assertFalse(number.isInteger)
    }

}
