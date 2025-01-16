package com.kamelia.sprinkler.i18n.impl

import com.kamelia.sprinkler.i18n.pluralization.Plural
import com.kamelia.sprinkler.i18n.pluralization.PluralMapper
import java.util.Locale
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TranslatorConfigurationTest {

    @Test
    fun `MissingKeyPolicy THROW_EXCEPTION make the translator throw when a key is not found`() {
        val translator = Translator {
            configuration {
                missingKeyPolicy = TranslatorBuilder.MissingKeyPolicy.THROW_EXCEPTION
            }
        }
        assertThrows<IllegalArgumentException> {
            translator.t("missing")
        }
    }

    @Test
    fun `MissingKeyPolicy RETURN_KEY make the translator return the key when a key is not found`() {
        val translator = Translator {
            configuration {
                missingKeyPolicy = TranslatorBuilder.MissingKeyPolicy.RETURN_KEY
            }
        }
        val key = "missing"
        assertEquals(key, translator.t(key))
    }

    @Test
    fun `interpolationDelimiter is used for interpolation`() {
        val translator = Translator {
            configuration {
                interpolationDelimiter = TranslatorBuilder.InterpolationDelimiter.create("[", "]")
            }
            translations {
                map(Locale.ENGLISH, mapOf("interpolation" to "This is a [value]."))
            }
        }
        val value = "dog"
        assertEquals("This is a $value.", translator.t("interpolation", mapOf("value" to value)))
    }

    @Test
    fun `throws an ISE if the interpolationDelimiter startDelimiter contains forbidden  characters`() {
        assertThrows<IllegalStateException> {
            TranslatorBuilder.InterpolationDelimiter.create("a(ee", "}}")
        }
    }

    @Test
    fun `throws an ISE if the interpolationDelimiter endDelimiter contains forbidden characters`() {
        assertThrows<IllegalStateException> {
            TranslatorBuilder.InterpolationDelimiter.create("{{", "a)ee")
        }
    }

    @Test
    fun `withPluralMapper sets the pluralMapper`() {
        val pluralMapper = object : PluralMapper {
            override fun mapCardinal(count: Double): Plural {
                return Plural.TWO
            }

            override fun mapOrdinal(count: Long): Plural {
                return Plural.FEW
            }
        }
        val translator = Translator {
            configuration {
                pluralMapperFactory = { pluralMapper }
            }
            translations {
                map(Locale.ENGLISH, mapOf("foo_two" to "hello", "bar_ordinal_few" to "world"))
            }
        }
        assertEquals("hello", translator.t("foo", mapOf(count(1))))
        assertEquals("world", translator.t("bar", mapOf(count(1), ordinal())))
    }

}
