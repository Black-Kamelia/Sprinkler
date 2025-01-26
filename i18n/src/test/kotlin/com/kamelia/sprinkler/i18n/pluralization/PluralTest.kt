package com.kamelia.sprinkler.i18n.pluralization

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PluralTest {

    @Test
    fun `mapCardinal with float defaults to double overload`() {
        val mapper = object : PluralRuleProvider {
            override fun cardinal(count: Double): Plural {
                throw PluralTestException
            }

            override fun ordinal(count: Long): Plural {
                throw AssertionError()
            }
        }
        assertThrows<PluralTestException> {
            mapper.cardinal(0f)
        }
    }

    @Test
    fun `mapCardinal with byte defaults to long overload`() {
        val mapper = object : PluralRuleProvider {
            override fun cardinal(count: Double): Plural {
                throw AssertionError()
            }

            override fun cardinal(count: Long): Plural {
                throw PluralTestException
            }

            override fun ordinal(count: Long): Plural {
                throw AssertionError()
            }
        }
        assertThrows<PluralTestException> {
            mapper.cardinal(1.toByte())
        }
    }

    @Test
    fun `mapCardinal with short defaults to long overload`() {
        val mapper = object : PluralRuleProvider {
            override fun cardinal(count: Double): Plural {
                throw AssertionError()
            }

            override fun cardinal(count: Long): Plural {
                throw PluralTestException
            }

            override fun ordinal(count: Long): Plural {
                throw AssertionError()
            }
        }
        assertThrows<PluralTestException> {
            mapper.cardinal(1.toShort())
        }
    }

    @Test
    fun `mapCardinal with int defaults to long overload`() {
        val mapper = object : PluralRuleProvider {
            override fun cardinal(count: Double): Plural {
                throw AssertionError()
            }

            override fun cardinal(count: Long): Plural {
                throw PluralTestException
            }

            override fun ordinal(count: Long): Plural {
                throw AssertionError()
            }
        }
        assertThrows<PluralTestException> {
            val n: Number = 1
            mapper.cardinal(n)
        }
    }

    @Test
    fun `mapCardinal with double as number defaults to double overload`() {
        val mapper = object : PluralRuleProvider {
            override fun cardinal(count: Double): Plural {
                throw PluralTestException
            }

            override fun ordinal(count: Long): Plural {
                throw AssertionError()
            }
        }
        assertThrows<PluralTestException> {
            val n: Number = 1.0
            mapper.cardinal(n)
        }
    }

    @Test
    fun `mapCardinal with ScientificNotationNumber defaults to the number actual value`() {
        val mapper = object : PluralRuleProvider {
            override fun cardinal(count: Double): Plural {
                return when (count) {
                    5.0 -> Plural.MANY
                    2.0 -> Plural.ZERO
                    else -> Plural.OTHER
                }
            }

            override fun ordinal(count: Long): Plural {
                throw AssertionError()
            }
        }
        assertEquals(Plural.MANY, mapper.cardinal(ScientificNotationNumber.from(5.0)))
        assertEquals(Plural.ZERO, mapper.cardinal(ScientificNotationNumber.from(2.0)))
    }

    @Test
    fun `mapCardinal throws when called with an unknown subtype of number`() {
        val customNumber = object : Number() {
            override fun toByte(): Byte {
                throw AssertionError()
            }

            override fun toDouble(): Double {
                throw AssertionError()
            }

            override fun toFloat(): Float {
                throw AssertionError()
            }

            override fun toInt(): Int {
                throw AssertionError()
            }

            override fun toLong(): Long {
                throw AssertionError()
            }

            override fun toShort(): Short {
                throw AssertionError()
            }

        }
        val mapper = object : PluralRuleProvider {
            override fun cardinal(count: Double): Plural {
                throw AssertionError()
            }

            override fun ordinal(count: Long): Plural {
                throw AssertionError()
            }
        }
        assertThrows<IllegalArgumentException> {
            mapper.cardinal(customNumber)
        }
    }

    @Test
    fun `mapOrdinal with float defaults to double overload`() {
        val mapper = object : PluralRuleProvider {
            override fun cardinal(count: Double): Plural {
                throw AssertionError()
            }

            override fun ordinal(count: Double): Plural {
                throw PluralTestException
            }

            override fun ordinal(count: Long): Plural {
                throw AssertionError()
            }
        }
        assertThrows<PluralTestException> {
            mapper.ordinal(0f)
        }
    }

    @Test
    fun `mapOrdinal with byte defaults to long overload`() {
        val mapper = object : PluralRuleProvider {
            override fun cardinal(count: Double): Plural {
                throw AssertionError()
            }

            override fun ordinal(count: Double): Plural {
                throw AssertionError()
            }

            override fun ordinal(count: Long): Plural {
                throw PluralTestException
            }
        }
        assertThrows<PluralTestException> {
            mapper.ordinal(1.toByte())
        }
    }

    @Test
    fun `mapOrdinal with short defaults to long overload`() {
        val mapper = object : PluralRuleProvider {
            override fun cardinal(count: Double): Plural {
                throw AssertionError()
            }

            override fun ordinal(count: Double): Plural {
                throw AssertionError()
            }

            override fun ordinal(count: Long): Plural {
                throw PluralTestException
            }
        }
        assertThrows<PluralTestException> {
            mapper.ordinal(1.toShort())
        }
    }

    @Test
    fun `mapOrdinal with int defaults to long overload`() {
        val mapper = object : PluralRuleProvider {
            override fun cardinal(count: Double): Plural {
                throw AssertionError()
            }

            override fun ordinal(count: Double): Plural {
                throw AssertionError()
            }

            override fun ordinal(count: Long): Plural {
                throw PluralTestException
            }
        }
        assertThrows<PluralTestException> {
            val n: Number = 1
            mapper.ordinal(n)
        }
    }

    @Test
    fun `mapOrdinal with double as number defaults to double overload`() {
        val mapper = object : PluralRuleProvider {
            override fun cardinal(count: Double): Plural {
                throw AssertionError()
            }

            override fun ordinal(count: Double): Plural {
                throw PluralTestException
            }

            override fun ordinal(count: Long): Plural {
                throw AssertionError()
            }
        }
        assertThrows<PluralTestException> {
            val n: Number = 1.0
            mapper.ordinal(n)
        }
    }

    @Test
    fun `mapOrdinal with ScientificNotationNumber defaults to the number actual value`() {
        val mapper = object : PluralRuleProvider {
            override fun cardinal(count: Double): Plural {
                throw AssertionError()
            }

            override fun ordinal(count: Long): Plural {
                return when (count) {
                    5L -> Plural.MANY
                    2L -> Plural.ZERO
                    else -> Plural.OTHER
                }
            }
        }
        assertEquals(Plural.MANY, mapper.ordinal(ScientificNotationNumber.from(5)))
        assertEquals(Plural.ZERO, mapper.ordinal(ScientificNotationNumber.from(2)))
    }

    @Test
    fun `mapOrdinal(double) defaults to other`() {
        val mapper = object : PluralRuleProvider {
            override fun cardinal(count: Double): Plural {
                throw AssertionError()
            }

            override fun ordinal(count: Long): Plural {
                throw AssertionError()
            }
        }
        assertEquals(Plural.OTHER, mapper.ordinal(1.0))
        assertEquals(Plural.OTHER, mapper.ordinal(8.0))
    }

    @Test
    fun `mapOrdinal(double) throws when called with a non-positive number`() {
        val mapper = object : PluralRuleProvider {
            override fun cardinal(count: Double): Plural {
                throw AssertionError()
            }

            override fun ordinal(count: Long): Plural {
                throw AssertionError()
            }
        }
        assertThrows<IllegalArgumentException> {
            mapper.ordinal(0.0)
        }
        assertThrows<IllegalArgumentException> {
            mapper.ordinal(-1.0)
        }
    }

    @Test
    fun `mapOrdinal throws when called with an unknown subtype of number`() {
        val customNumber = object : Number() {
            override fun toByte(): Byte {
                throw AssertionError()
            }

            override fun toDouble(): Double {
                throw AssertionError()
            }

            override fun toFloat(): Float {
                throw AssertionError()
            }

            override fun toInt(): Int {
                throw AssertionError()
            }

            override fun toLong(): Long {
                throw AssertionError()
            }

            override fun toShort(): Short {
                throw AssertionError()
            }

        }
        val mapper = object : PluralRuleProvider {
            override fun cardinal(count: Double): Plural {
                throw AssertionError()
            }

            override fun ordinal(count: Double): Plural {
                throw AssertionError()
            }

            override fun ordinal(count: Long): Plural {
                throw AssertionError()
            }
        }
        assertThrows<IllegalArgumentException> {
            mapper.ordinal(customNumber)
        }
    }

    @Test
    fun `java api coverage`() {
        PluralRuleProvider.builtinsJava()
    }

    private object PluralTestException : RuntimeException(null, null, false, false)

}
