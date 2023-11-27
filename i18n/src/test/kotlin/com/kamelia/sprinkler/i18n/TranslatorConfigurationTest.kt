package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.util.VariableDelimiter
import java.util.*
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
        val config = TranslatorConfiguration.create {
            missingKeyPolicy = TranslatorConfiguration.MissingKeyPolicy.RETURN_KEY
        }
        val translator = Translator.builder(Locale.ENGLISH)
            .withConfiguration(config)
            .build()
        val key = "missing"
        assertEquals(key, translator.t(key))
    }

    @Test
    fun `interpolationDelimiter is used for interpolation`() {
        val config = TranslatorConfiguration.create {
            interpolationDelimiter = VariableDelimiter('[', ']')
        }
        val translator = Translator.builder(Locale.ENGLISH)
            .withConfiguration(config)
            .addMap(Locale.ENGLISH, mapOf("interpolation" to "This is a [value]."))
            .build()
        val value = "dog"
        assertEquals("This is a $value.", translator.t("interpolation", mapOf("value" to value)))
    }

}

fun main() {
    val translator = Translator.builder(Locale.ENGLISH)
        .addMap(Locale.ENGLISH, mapOf("test_male_other" to "This is {value} {count}."))
        .build()
    println(translator)
    val value = translator.t("test", mapOf("value" to "John", "count" to "aaa", options(Options.COUNT to "aaaaa", Options.CONTEXT to "male")))
    println(value)
}

