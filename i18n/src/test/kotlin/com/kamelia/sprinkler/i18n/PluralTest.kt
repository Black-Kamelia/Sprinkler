package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.i18n.Plural.Companion.nullMapper
import java.util.Locale
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PluralTest {

    @Test
    fun `the englishMapper mapCardinal returns the values of the English language`() {
        val mapper = Plural.englishMapper()
        assertEquals(Plural.ONE, mapper.mapCardinal(1))
        repeat(10_000) { count ->
            if (count == 1) return@repeat
            assertEquals(Plural.OTHER, mapper.mapCardinal(count))
        }
    }

    @Test
    fun `the englishMapper mapCardinal throws an IAE if the count is negative (long)`() {
        val mapper = Plural.englishMapper()
        assertThrows<IllegalArgumentException> { mapper.mapCardinal(-1) }
    }

    @Test
    fun `the englishMapper mapCardinal throws an IAE if the count is negative (double)`() {
        val mapper = Plural.englishMapper()
        assertThrows<IllegalArgumentException> { mapper.mapCardinal(-1.0) }
    }

    @Test
    fun `the englishMapper mapCardinal returns the values of the English language (double)`() {
        val mapper = Plural.englishMapper()
        repeat(10_000) { count ->
            val cnt = count.toDouble()
            if (cnt == .0) return@repeat
            val plural = mapper.mapCardinal(cnt)
            assertEquals(Plural.OTHER, plural)
        }
    }

    @Test
    fun `the englishMapper mapOrdinal returns the values of the English language (long)`() {
        val mapper = Plural.englishMapper()
        repeat(10_000) { count ->
            if (count == 0) return@repeat
            val plural = mapper.mapOrdinal(count)
            when (count % 10) {
                1 -> assertEquals(Plural.ONE, plural)
                2 -> assertEquals(Plural.TWO, plural)
                3 -> assertEquals(Plural.FEW, plural)
                else -> assertEquals(Plural.OTHER, plural)
            }
        }
    }

    @Test
    fun `the englishMapper mapOrdinal throws an IAE if the count is negative or zero (long)`() {
        val mapper = Plural.englishMapper()
        assertThrows<IllegalArgumentException> { mapper.mapOrdinal(-1) }
        assertThrows<IllegalArgumentException> { mapper.mapOrdinal(0) }
    }

    @Test
    fun `the englishMapper mapOrdinal returns other if the count is a double`() {
        val mapper = Plural.englishMapper()
        assertEquals(Plural.OTHER, mapper.mapOrdinal(1.0))
    }

    @Test
    fun `the englishMapper toString returns the name of the class`() {
        val mapper = Plural.englishMapper()
        assertEquals("Plural.englishMapper()", mapper.toString())
    }

    @Test
    fun `mapCardinal with float defaults to double overload`() {
        val mapper = object : Plural.Mapper {
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
        val mapper = object : Plural.Mapper {
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
        val mapper = object : Plural.Mapper {
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
        val mapper = object : Plural.Mapper {
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
        val mapper = object : Plural.Mapper {
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
        val mapper = object : Plural.Mapper {
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
        val mapper = object : Plural.Mapper {
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
        val mapper = object : Plural.Mapper {
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
        val mapper = object : Plural.Mapper {
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
        val mapper = object : Plural.Mapper {
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
        val mapper = object : Plural.Mapper {
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
        val mapper = object : Plural.Mapper {
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
        val mapper = object : Plural.Mapper {
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
        val mapper = object : Plural.Mapper {
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

    @Test
    fun `nullMapper mapOrdinal return OTHER for any count`() {
        val mapper = nullMapper()
        repeat(4) { type ->
            repeat(10) {
                val e = it + 1
                val count = when (type) {
                    0 -> e.toByte()
                    1 -> e.toShort()
                    2 -> e
                    else -> e.toLong()
                }
                assertEquals(Plural.OTHER, mapper.mapOrdinal(count))
            }
        }
    }

    @Test
    fun `nullMapper mapCardinal return OTHER for any count`() {
        val mapper = nullMapper()
        repeat(4) { type ->
            repeat(10) {
                val count = when (type) {
                    0 -> it.toByte()
                    1 -> it.toShort()
                    2 -> it
                    else -> it.toLong()
                }
                assertEquals(Plural.OTHER, mapper.mapCardinal(count))
            }
        }
    }

    @Test
    fun `nullMapper mapOrdinal throws if count is negative`() {
        val mapper = nullMapper()
        assertThrows<IllegalArgumentException> { mapper.mapOrdinal(-1) }
    }

    @Test
    fun `nullMapper mapCardinal throws if count is negative`() {
        val mapper = nullMapper()
        assertThrows<IllegalArgumentException> { mapper.mapCardinal(-1) }
    }

    @Test
    fun `nullMapper toString returns the name of the class`() {
        val mapper = nullMapper()
        assertEquals("Plural.nullMapper()", mapper.toString())
    }

    @Test
    fun `nullFactory returns a factory that always returns the nullMapper implementation`() {
        val nullMapper = nullMapper()
        val factory = Plural.nullFactory()
        repeat(10) {
            val mapper = factory(Locale.ENGLISH)
            assertEquals(nullMapper::class.java, mapper::class.java)
        }
    }

    @Test
    fun `builtinMappers throws if an unknown locale is passed`() {
        assertThrows<IllegalArgumentException> {
            Plural.builtinMappers(setOf(Locale("unknown")))
        }
    }

    private object PluralTestException : RuntimeException(null, null, false, false)

}
