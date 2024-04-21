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
        val translator = TranslatorBuilder.create(Locale.ENGLISH, configuration = config).build()
        assertThrows<IllegalArgumentException> {
            translator.t("missing")
        }
    }

    @Test
    fun `MissingKeyPolicy RETURN_KEY make the translator return the key when a key is not found`() {
        val config = TranslatorConfiguration.builder()
            .withMissingKeyPolicy(TranslatorConfiguration.MissingKeyPolicy.RETURN_KEY)
            .build()
        val translator = TranslatorBuilder.create(Locale.ENGLISH, configuration = config).build()
        val key = "missing"
        assertEquals(key, translator.t(key))
    }

    @Test
    fun `interpolationDelimiter is used for interpolation`() {
        val config = TranslatorConfiguration.builder()
            .setInterpolationDelimiter(TranslatorConfiguration.InterpolationDelimiter.create("[", "]"))
            .build()
        val translator = TranslatorBuilder.create(Locale.ENGLISH, configuration = config)
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

}
