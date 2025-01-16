package com.kamelia.sprinkler.i18n.pluralization

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PluralTest {

    @Test
    fun `mapCardinal with float defaults to double overload`() {
        val mapper = object : PluralMapper {
            override fun mapCardinal(count: Double): Plural {
                throw PluralTestException
            }

            override fun mapOrdinal(count: Long): Plural {
                throw AssertionError()
            }
        }
        assertThrows<PluralTestException> {
            mapper.mapCardinal(0f)
        }
    }

    @Test
    fun `mapCardinal with byte defaults to long overload`() {
        val mapper = object : PluralMapper {
            override fun mapCardinal(count: Double): Plural {
                throw AssertionError()
            }

            override fun mapCardinal(count: Long): Plural {
                throw PluralTestException
            }

            override fun mapOrdinal(count: Long): Plural {
                throw AssertionError()
            }
        }
        assertThrows<PluralTestException> {
            mapper.mapCardinal(1.toByte())
        }
    }

    @Test
    fun `mapCardinal with short defaults to long overload`() {
        val mapper = object : PluralMapper {
            override fun mapCardinal(count: Double): Plural {
                throw AssertionError()
            }

            override fun mapCardinal(count: Long): Plural {
                throw PluralTestException
            }

            override fun mapOrdinal(count: Long): Plural {
                throw AssertionError()
            }
        }
        assertThrows<PluralTestException> {
            mapper.mapCardinal(1.toShort())
        }
    }

    @Test
    fun `mapCardinal with int defaults to long overload`() {
        val mapper = object : PluralMapper {
            override fun mapCardinal(count: Double): Plural {
                throw AssertionError()
            }

            override fun mapCardinal(count: Long): Plural {
                throw PluralTestException
            }

            override fun mapOrdinal(count: Long): Plural {
                throw AssertionError()
            }
        }
        assertThrows<PluralTestException> {
            val n: Number = 1
            mapper.mapCardinal(n)
        }
    }

    @Test
    fun `mapCardinal with double as number defaults to double overload`() {
        val mapper = object : PluralMapper {
            override fun mapCardinal(count: Double): Plural {
                throw PluralTestException
            }

            override fun mapOrdinal(count: Long): Plural {
                throw AssertionError()
            }
        }
        assertThrows<PluralTestException> {
            val n: Number = 1.0
            mapper.mapCardinal(n)
        }
    }

    @Test
    fun `mapCardinal with ScientificNotationNumber defaults to the number actual value`() {
        val mapper = object : PluralMapper {
            override fun mapCardinal(count: Double): Plural {
                return when (count) {
                    5.0 -> Plural.MANY
                    2.0 -> Plural.ZERO
                    else -> Plural.OTHER
                }
            }

            override fun mapOrdinal(count: Long): Plural {
                throw AssertionError()
            }
        }
        assertEquals(Plural.MANY, mapper.mapCardinal(ScientificNotationNumber.from(5.0)))
        assertEquals(Plural.ZERO, mapper.mapCardinal(ScientificNotationNumber.from(2.0)))
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
        val mapper = object : PluralMapper {
            override fun mapCardinal(count: Double): Plural {
                throw AssertionError()
            }

            override fun mapOrdinal(count: Long): Plural {
                throw AssertionError()
            }
        }
        assertThrows<IllegalArgumentException> {
            mapper.mapCardinal(customNumber)
        }
    }

    @Test
    fun `mapOrdinal with float defaults to double overload`() {
        val mapper = object : PluralMapper {
            override fun mapCardinal(count: Double): Plural {
                throw AssertionError()
            }

            override fun mapOrdinal(count: Double): Plural {
                throw PluralTestException
            }

            override fun mapOrdinal(count: Long): Plural {
                throw AssertionError()
            }
        }
        assertThrows<PluralTestException> {
            mapper.mapOrdinal(0f)
        }
    }

    @Test
    fun `mapOrdinal with byte defaults to long overload`() {
        val mapper = object : PluralMapper {
            override fun mapCardinal(count: Double): Plural {
                throw AssertionError()
            }

            override fun mapOrdinal(count: Double): Plural {
                throw AssertionError()
            }

            override fun mapOrdinal(count: Long): Plural {
                throw PluralTestException
            }
        }
        assertThrows<PluralTestException> {
            mapper.mapOrdinal(1.toByte())
        }
    }

    @Test
    fun `mapOrdinal with short defaults to long overload`() {
        val mapper = object : PluralMapper {
            override fun mapCardinal(count: Double): Plural {
                throw AssertionError()
            }

            override fun mapOrdinal(count: Double): Plural {
                throw AssertionError()
            }

            override fun mapOrdinal(count: Long): Plural {
                throw PluralTestException
            }
        }
        assertThrows<PluralTestException> {
            mapper.mapOrdinal(1.toShort())
        }
    }

    @Test
    fun `mapOrdinal with int defaults to long overload`() {
        val mapper = object : PluralMapper {
            override fun mapCardinal(count: Double): Plural {
                throw AssertionError()
            }

            override fun mapOrdinal(count: Double): Plural {
                throw AssertionError()
            }

            override fun mapOrdinal(count: Long): Plural {
                throw PluralTestException
            }
        }
        assertThrows<PluralTestException> {
            val n: Number = 1
            mapper.mapOrdinal(n)
        }
    }

    @Test
    fun `mapOrdinal with double as number defaults to double overload`() {
        val mapper = object : PluralMapper {
            override fun mapCardinal(count: Double): Plural {
                throw AssertionError()
            }

            override fun mapOrdinal(count: Double): Plural {
                throw PluralTestException
            }

            override fun mapOrdinal(count: Long): Plural {
                throw AssertionError()
            }
        }
        assertThrows<PluralTestException> {
            val n: Number = 1.0
            mapper.mapOrdinal(n)
        }
    }

    @Test
    fun `mapOrdinal with ScientificNotationNumber defaults to the number actual value`() {
        val mapper = object : PluralMapper {
            override fun mapCardinal(count: Double): Plural {
                throw AssertionError()
            }

            override fun mapOrdinal(count: Long): Plural {
                return when (count) {
                    5L -> Plural.MANY
                    2L -> Plural.ZERO
                    else -> Plural.OTHER
                }
            }
        }
        assertEquals(Plural.MANY, mapper.mapOrdinal(ScientificNotationNumber.from(5)))
        assertEquals(Plural.ZERO, mapper.mapOrdinal(ScientificNotationNumber.from(2)))
    }

    @Test
    fun `mapOrdinal(double) defaults to other`() {
        val mapper = object : PluralMapper {
            override fun mapCardinal(count: Double): Plural {
                throw AssertionError()
            }

            override fun mapOrdinal(count: Long): Plural {
                throw AssertionError()
            }
        }
        assertEquals(Plural.OTHER, mapper.mapOrdinal(1.0))
        assertEquals(Plural.OTHER, mapper.mapOrdinal(8.0))
    }

    @Test
    fun `mapOrdinal(double) throws when called with a non-positive number`() {
        val mapper = object : PluralMapper {
            override fun mapCardinal(count: Double): Plural {
                throw AssertionError()
            }

            override fun mapOrdinal(count: Long): Plural {
                throw AssertionError()
            }
        }
        assertThrows<IllegalArgumentException> {
            mapper.mapOrdinal(0.0)
        }
        assertThrows<IllegalArgumentException> {
            mapper.mapOrdinal(-1.0)
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
        val mapper = object : PluralMapper {
            override fun mapCardinal(count: Double): Plural {
                throw AssertionError()
            }

            override fun mapOrdinal(count: Double): Plural {
                throw AssertionError()
            }

            override fun mapOrdinal(count: Long): Plural {
                throw AssertionError()
            }
        }
        assertThrows<IllegalArgumentException> {
            mapper.mapOrdinal(customNumber)
        }
    }

    private object PluralTestException : RuntimeException(null, null, false, false)

}
