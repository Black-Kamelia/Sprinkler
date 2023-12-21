package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.util.VariableDelimiter
import java.util.Locale
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TranslatorConfigurationTest {

    @Test
    fun `MissingKeyPolicy THROW_EXCEPTION make the translator throw when a key is not found`() {
        val config = TranslatorConfiguration.create {
            missingKeyPolicy = TranslatorConfiguration.MissingKeyPolicy.THROW_EXCEPTION
        }
        val translator = Translator.builder(Locale.ENGLISH)
            .withConfiguration(config)
            .build()
        assertThrows<IllegalArgumentException> {
            translator.t("missing")
        }
    }

    @Test
    fun `MissingKeyPolicy RETURN_KEY make the translator return the key when a key is not found`() {

        val translator = Translator.builder(Locale.ENGLISH)
            .withConfiguration {
                missingKeyPolicy = TranslatorConfiguration.MissingKeyPolicy.RETURN_KEY
            }
            .build()
        val key = "missing"
        assertEquals(key, translator.t(key))
    }

    @Test
    fun `interpolationDelimiter is used for interpolation`() {
        val config = TranslatorConfiguration.create {
            interpolationDelimiter = VariableDelimiter.create("[", "]")
        }
        val translator = Translator.builder(Locale.ENGLISH)
            .withConfiguration(config)
            .addMap(Locale.ENGLISH, mapOf("interpolation" to "This is a [value]."))
            .build()
        val value = "dog"
        assertEquals("This is a $value.", translator.t("interpolation", mapOf("value" to value)))
    }

    @Test
    fun `build throws an ISE if the interpolationDelimiter startDelimiter contains forbidden  characters`() {
        val delimiter = VariableDelimiter.create("a(ee", "}}")
        assertThrows<IllegalStateException> {
            TranslatorConfiguration.create {
                interpolationDelimiter = delimiter
            }
        }
    }

    @Test
    fun `build throws an ISE if the interpolationDelimiter endDelimiter contains forbidden characters`() {
        val delimiter = VariableDelimiter.create("{{", "a)ee")
        assertThrows<IllegalStateException> {
            TranslatorConfiguration.create {
                interpolationDelimiter = delimiter
            }
        }
    }

}
