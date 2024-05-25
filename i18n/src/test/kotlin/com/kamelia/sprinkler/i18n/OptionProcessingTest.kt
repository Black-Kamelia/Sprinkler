package com.kamelia.sprinkler.i18n

import java.util.Locale
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class OptionProcessingTest {

    @Test
    fun `context option correctly appended to the base key`() {
        val key = TranslationProcessor.buildKey(
            "base.key",
            Locale.FRANCE,
            mapOf(Options.CONTEXT to "context"),
            Plural.defaultMapper()
        )
        assertEquals("base.key_context", key)
    }

    @Test
    fun `plural option correctly appended to the base key`() {
        val key = TranslationProcessor.buildKey(
            "base.key",
            Locale.FRANCE,
            mapOf(Options.COUNT to 5),
            Plural.defaultMapper()
        )
        assertEquals("base.key_other", key)
    }

    @Test
    fun `context and plural options correctly appended to the base key`() {
        val key = TranslationProcessor.buildKey(
            "base.key",
            Locale.FRANCE,
            mapOf(Options.CONTEXT to "context", Options.COUNT to 1),
            Plural.defaultMapper()
        )
        assertEquals("base.key_context_one", key)
    }

    @Test
    fun `buildKey returns the base key when no option is provided`() {
        val key = TranslationProcessor.buildKey(
            "base.key",
            Locale.FRANCE,
            mapOf(),
            Plural.defaultMapper()
        )
        assertEquals("base.key", key)
    }

    @Test
    fun `plural ordinal option is correctly appended to the base key`() {
        val key = TranslationProcessor.buildKey(
            "base.key",
            Locale.FRANCE,
            mapOf(Options.COUNT to 2, Options.ORDINAL to true),
            Plural.defaultMapper()
        )
        assertEquals("base.key_ordinal_two", key)
    }

    @Test
    fun `plural ordinal and context options are correctly appended to the base key`() {
        val key = TranslationProcessor.buildKey(
            "base.key",
            Locale.FRANCE,
            mapOf(Options.CONTEXT to "context", Options.COUNT to 2, Options.ORDINAL to true),
            Plural.defaultMapper()
        )
        assertEquals("base.key_context_ordinal_two", key)
    }

    @Test
    fun `buildKey throws if the context option is not a string`() {
        assertThrows<IllegalArgumentException> {
            TranslationProcessor.buildKey(
                "base.key",
                Locale.FRANCE,
                mapOf(Options.CONTEXT to 1),
                Plural.defaultMapper()
            )
        }
    }

    @Test
    fun `buildKey throws if the count option is not a number`() {
        assertThrows<IllegalArgumentException> {
            TranslationProcessor.buildKey(
                "base.key",
                Locale.FRANCE,
                mapOf(Options.COUNT to "one"),
                Plural.defaultMapper()
            )
        }
    }

    @Test
    fun `buildKey throws if the ordinal option is not a boolean`() {
        assertThrows<IllegalArgumentException> {
            TranslationProcessor.buildKey(
                "base.key",
                Locale.FRANCE,
                mapOf(Options.ORDINAL to 1),
                Plural.defaultMapper()
            )
        }
    }

        @Test
        fun `interpolate correctly replaces variables`() {
            val value = TranslationProcessor.translate(
                TranslatorData(Locale.US, mapOf(Locale.US to mapOf("hello" to "Hello {{name}}")), TranslatorConfiguration.builder().build()),
                "hello",
                mapOf("name" to "John"),
                Locale.US,
            )
            assertEquals("Hello John", value)
        }
    /*

        @Test
        fun `interpolate uses the option value if the variable is not found`() {
            val value = TranslationProcessor.translate(
                "Hello {{name}}",
                Locale.US,
                mapOf(),
                mapOf("name" to "John"),
                VariableDelimiter.default,
                VariableFormatter.builtins(),
            )
            assertEquals("Hello John", value)
        }

        @Test
        fun `interpolate uses the interpolation map before the option map`() {
            val value = TranslationProcessor.translate(
                "Hello {{name}}",
                Locale.US,
                mapOf("name" to "Jane"),
                mapOf("name" to "John"),
                VariableDelimiter.default,
                VariableFormatter.builtins(),
            )
            assertEquals("Hello Jane", value)
        }

        @Test
        fun `interpolate throws an IAE if the variable is not found and no option is provided`() {
            assertThrows<IllegalArgumentException> {
                TranslationProcessor.translate(
                    "Hello {{name}}",
                    Locale.US,
                    mapOf(),
                    mapOf(),
                    VariableDelimiter.default,
                    VariableFormatter.builtins(),
                )
            }
        }

        @Test
        fun `interpolate uses the format to format the variable`() {
            val value = TranslationProcessor.translate(
                "Hello {{d, time}}",
                Locale.US,
                mapOf(),
                mapOf("d" to LocalTime.of(2, 2, 3)),
                VariableDelimiter.default,
                VariableFormatter.builtins(),
            )
            assertEquals("Hello 2:02:03 AM", value)
        }

        @Test
        fun `interpolate passes the params to the format`() {
            VariableFormatter
            val value = TranslationProcessor.translate(
                "Hello {{d, time(timeStyle:short)}}",
                Locale.US,
                mapOf(),
                mapOf("d" to LocalTime.of(2, 2, 3)),
                VariableDelimiter.default,
                VariableFormatter.builtins(),
            )
            assertEquals("Hello 2:02 AM", value)
        }
    */

    @Test
    fun `translate returns null if the locale is not found`() {
        val value = TranslationProcessor.translate(
            TranslatorData(Locale.FRANCE, mapOf(), TranslatorConfiguration.builder().build()),
            "foo",
            mapOf(),
            Locale.FRANCE,
        )
        assertNull(value)
    }

    @Test
    fun `translate returns null if the key is not found`() {
        val value = TranslationProcessor.translate(
            TranslatorData(Locale.FRANCE, mapOf(Locale.FRANCE to mapOf()), TranslatorConfiguration.builder().build()),
            "foo",
            mapOf(),
            Locale.FRANCE,
        )
        assertNull(value)
    }

    @Test
    fun `translate returns the value if the key is found`() {
        val value = TranslationProcessor.translate(
            TranslatorData(
                Locale.FRANCE,
                mapOf(Locale.FRANCE to mapOf("foo" to "bar")),
                TranslatorConfiguration.builder().build()
            ),
            "foo",
            mapOf(),
            Locale.FRANCE,
        )
        assertEquals("bar", value)
    }

    @Test
    fun `translate applies the options to the key`() {
        val value = TranslationProcessor.translate(
            TranslatorData(
                Locale.FRANCE,
                mapOf(Locale.FRANCE to mapOf("foo_context" to "bar")),
                TranslatorConfiguration.builder().build()
            ),
            "foo",
            mapOf(options(Options.CONTEXT to "context")),
            Locale.FRANCE,
        )
        assertEquals("bar", value)
    }

    @Test
    fun `translate throws if options is not a map`() {
        assertThrows<IllegalArgumentException> {
            TranslationProcessor.translate(
                TranslatorData(
                    Locale.FRANCE,
                    mapOf(Locale.FRANCE to mapOf("foo" to "bar")),
                    TranslatorConfiguration.builder().build()
                ),
                "foo",
                mapOf("options" to "context"),
                Locale.FRANCE,
            )
        }
    }

}
