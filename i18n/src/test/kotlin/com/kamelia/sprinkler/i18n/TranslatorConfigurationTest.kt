package com.kamelia.sprinkler.i18n

import java.util.Locale
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TranslatorConfigurationTest {

    @Test
    fun `MissingKeyPolicy THROW_EXCEPTION make the translator throw when a key is not found`() {
        val config = TranslatorConfiguration.builder()
            .withMissingKeyPolicy(TranslatorConfiguration.MissingKeyPolicy.THROW_EXCEPTION)
            .build()
        val translator = TranslatorBuilder.create(configuration = config, defaultLocale = Locale.ENGLISH).build()
        assertThrows<IllegalArgumentException> {
            translator.t("missing")
        }
    }

    @Test
    fun `MissingKeyPolicy RETURN_KEY make the translator return the key when a key is not found`() {
        val config = TranslatorConfiguration.builder()
            .withMissingKeyPolicy(TranslatorConfiguration.MissingKeyPolicy.RETURN_KEY)
            .build()
        val translator = TranslatorBuilder.create(configuration = config, defaultLocale = Locale.ENGLISH).build()
        val key = "missing"
        assertEquals(key, translator.t(key))
    }

    @Test
    fun `interpolationDelimiter is used for interpolation`() {
        val config = TranslatorConfiguration.builder()
            .setInterpolationDelimiter(TranslatorConfiguration.InterpolationDelimiter.create("[", "]"))
            .build()
        val translator = TranslatorBuilder.create(configuration = config, defaultLocale = Locale.ENGLISH)
            .addMap(Locale.ENGLISH, mapOf("interpolation" to "This is a [value]."))
            .build()
        val value = "dog"
        assertEquals("This is a $value.", translator.t("interpolation", mapOf("value" to value)))
    }

    @Test
    fun `throws an ISE if the interpolationDelimiter startDelimiter contains forbidden  characters`() {
        assertThrows<IllegalStateException> {
            TranslatorConfiguration.InterpolationDelimiter.create("a(ee", "}}")
        }
    }

    @Test
    fun `throws an ISE if the interpolationDelimiter endDelimiter contains forbidden characters`() {
        assertThrows<IllegalStateException> {
            TranslatorConfiguration.InterpolationDelimiter.create("{{", "a)ee")
        }
    }

    @Test
    fun `withPluralMapper sets the pluralMapper`() {
        val pluralMapper = object : Plural.Mapper {
            override fun mapPlural(locale: Locale, count: Int): Plural {
                return Plural.TWO
            }

            override fun mapOrdinal(locale: Locale, count: Int): Plural {
                return Plural.FEW
            }
        }
        val config = TranslatorConfiguration.builder()
            .withPluralMapper(pluralMapper)
            .build()
        val translator = TranslatorBuilder.create(config).addMap(Locale.ENGLISH, mapOf("foo_two" to "hello", "bar_ordinal_few" to "world")).build()
        assertEquals("hello", translator.t("foo", mapOf( options(Options.COUNT to 1))))
        assertEquals("world", translator.t("bar", mapOf( options(Options.COUNT to 1, Options.ORDINAL to true))))
    }

    @Test
    fun `addFormatter adds a formatter`() {
        val formatter = VariableFormatter.date()
        val config = TranslatorConfiguration.builder()
            .addFormatter("foo", formatter)
            .build()
        assertEquals(formatter, config.formatters["foo"])
    }

    @Test
    fun `clearFormatters clears the formatters`() {
        val config = TranslatorConfiguration.builder()
            .addFormatter("foo", VariableFormatter.date())
            .clearFormatters()
            .build()
        assertEquals(emptyMap<String, VariableFormatter>(), config.formatters)
    }

    @Test
    fun `withFormatters sets the formatters`() {
        val formatters = mapOf("foo" to VariableFormatter.date())
        val config = TranslatorConfiguration.builder()
            .withFormatters(formatters)
            .build()
        assertEquals(formatters, config.formatters)
    }

}
