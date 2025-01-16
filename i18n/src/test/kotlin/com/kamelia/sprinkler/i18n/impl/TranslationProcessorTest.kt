package com.kamelia.sprinkler.i18n.impl

import com.kamelia.sprinkler.i18n.impl.TranslationProcessor.context
import com.kamelia.sprinkler.i18n.impl.TranslationProcessor.count
import com.kamelia.sprinkler.i18n.impl.TranslationProcessor.ordinal
import com.kamelia.sprinkler.i18n.pluralization.Plural
import com.kamelia.sprinkler.i18n.pluralization.PluralMapper
import com.kamelia.sprinkler.i18n.pluralization.ScientificNotationNumber
import com.kamelia.sprinkler.util.unsafeCast
import java.util.Locale
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TranslationProcessorTest {

    @Test
    fun `context option correctly appended to the base key`() {
        val key = TranslationProcessor.buildKey(
            "base.key",
            mapOf(context("context")),
            englishMapper()
        )
        assertEquals("base.key_context", key)
    }

    @Test
    fun `plural option correctly appended to the base key`() {
        val key = TranslationProcessor.buildKey(
            "base.key",
            mapOf(Options.COUNT to 5),
            englishMapper()
        )
        assertEquals("base.key_other", key)
    }

    @Test
    fun `context and plural options correctly appended to the base key`() {
        val key = TranslationProcessor.buildKey(
            "base.key",
            mapOf(Options.CONTEXT to "context", Options.COUNT to 1),
            englishMapper()
        )
        assertEquals("base.key_context_one", key)
    }

    @Test
    fun `context and plural options correctly appended to the base key for ScientificNotationNumber`() {
        val key = TranslationProcessor.buildKey(
            "base.key",
            mapOf(Options.CONTEXT to "context", Options.COUNT to ScientificNotationNumber.from(1)),
            englishMapper()
        )
        assertEquals("base.key_context_one", key)
    }

    @Test
    fun `buildKey returns the base key when no option is provided`() {
        val key = TranslationProcessor.buildKey(
            "base.key",
            mapOf(),
            englishMapper()
        )
        assertEquals("base.key", key)
    }

    @Test
    fun `plural ordinal option is correctly appended to the base key`() {
        val key = TranslationProcessor.buildKey(
            "base.key",
            mapOf(Options.COUNT to 2, Options.ORDINAL to true),
            englishMapper()
        )
        assertEquals("base.key_ordinal_two", key)
    }

    @Test
    fun `plural ordinal option is correctly appended to the base key for ScientificNotationNumber`() {
        val key = TranslationProcessor.buildKey(
            "base.key",
            mapOf(Options.COUNT to ScientificNotationNumber.from(2), Options.ORDINAL to true),
            englishMapper()
        )
        assertEquals("base.key_ordinal_two", key)
    }

    @Test
    fun `plural ordinal and context options are correctly appended to the base key`() {
        val key = TranslationProcessor.buildKey(
            "base.key",
            mapOf(Options.CONTEXT to "context", Options.COUNT to 2, Options.ORDINAL to true),
            englishMapper()
        )
        assertEquals("base.key_context_ordinal_two", key)
    }

    @Test
    fun `buildKey throws if the context option is not a string`() {
        assertThrows<IllegalArgumentException> {
            TranslationProcessor.buildKey(
                "base.key",
                mapOf(Options.CONTEXT to 1),
                englishMapper()
            )
        }
    }

    @Test
    fun `buildKey throws if the count option is not a number`() {
        assertThrows<IllegalArgumentException> {
            TranslationProcessor.buildKey(
                "base.key",
                mapOf(Options.COUNT to "one"),
                englishMapper()
            )
        }
    }

    @Test
    fun `buildKey throws if the ordinal option is not a boolean`() {
        assertThrows<IllegalArgumentException> {
            TranslationProcessor.buildKey(
                "base.key",
                mapOf(Options.ORDINAL to 1),
                englishMapper()
            )
        }
    }

    @Test
    fun `interpolate correctly replaces variables`() {
        val conf = ConfigImpl()
        val value = TranslationProcessor.translate(
            TranslatorData(
                Locale.US,
                mapOf(Locale.US to mapOf("hello" to "Hello {{name}}")),
                conf.interpolationDelimiter.inner,
                conf.pluralMapperFactory,
                { conf.formatters[it]!! },
                conf.missingKeyPolicy
            ),
            "hello",
            mapOf("name" to "John"),
            Locale.US,
        )
        assertEquals("Hello John", value)
    }

    @Test
    fun `translate returns null if the locale is not found`() {
        val conf = ConfigImpl()
        val value = TranslationProcessor.translate(
            TranslatorData(
                Locale.FRANCE,
                mapOf(),
                conf.interpolationDelimiter.inner,
                conf.pluralMapperFactory,
                { conf.formatters[it]!! },
                conf.missingKeyPolicy
            ),
            "foo",
            mapOf(),
            Locale.FRANCE,
        )
        assertNull(value)
    }

    @Test
    fun `translate returns null if the key is not found`() {
        val conf = ConfigImpl()
        val value = TranslationProcessor.translate(
            TranslatorData(
                Locale.FRANCE,
                mapOf(Locale.FRANCE to mapOf()),
                conf.interpolationDelimiter.inner,
                conf.pluralMapperFactory,
                { conf.formatters[it]!! },
                conf.missingKeyPolicy
            ),
            "foo",
            mapOf(),
            Locale.FRANCE,
        )
        assertNull(value)
    }

    @Test
    fun `translate returns the value if the key is found`() {
        val conf = ConfigImpl()
        val value = TranslationProcessor.translate(
            TranslatorData(
                Locale.FRANCE,
                mapOf(Locale.FRANCE to mapOf("foo" to "bar")),
                conf.interpolationDelimiter.inner,
                conf.pluralMapperFactory,
                { conf.formatters[it]!! },
                conf.missingKeyPolicy
            ),
            "foo",
            mapOf(),
            Locale.FRANCE,
        )
        assertEquals("bar", value)
    }

    @Test
    fun `translate applies the options to the key`() {
        val conf = ConfigImpl()
        val value = TranslationProcessor.translate(
            TranslatorData(
                Locale.FRANCE,
                mapOf(Locale.FRANCE to mapOf("foo_context" to "bar")),
                conf.interpolationDelimiter.inner,
                conf.pluralMapperFactory,
                { conf.formatters[it]!! },
                conf.missingKeyPolicy
            ),
            "foo",
            mapOf(context("context")),
            Locale.FRANCE,
        )
        assertEquals("bar", value)
    }

    @Test
    fun `translate uses the option value in case where the key of a interpolation variable is not found`() {
        val conf = ConfigImpl()
        val value = TranslationProcessor.translate(
            TranslatorData(
                Locale.ENGLISH,
                mapOf(Locale.ENGLISH to mapOf("foo_other" to "Hello {{count}}")),
                conf.interpolationDelimiter.inner,
                conf.pluralMapperFactory,
                { conf.formatters[it]!! },
                conf.missingKeyPolicy
            ),
            "foo",
            mapOf(count(5)),
            Locale.ENGLISH,
        )
        assertEquals("Hello 5", value)
    }

    @Test
    fun `translate throws if an interpolation variable is missing`() {
        val conf = ConfigImpl()
        assertThrows<IllegalArgumentException> {
            TranslationProcessor.translate(
                TranslatorData(
                    Locale.ENGLISH,
                    mapOf(Locale.ENGLISH to mapOf("foo" to "Hello {{name}}")),
                    conf.interpolationDelimiter.inner,
                    conf.pluralMapperFactory,
                    { conf.formatters[it]!! },
                    conf.missingKeyPolicy
                ),
                "foo",
                mapOf(),
                Locale.ENGLISH,
            )
        }
    }

    @Test
    fun `context extract the value from a formatted value`() {
        val ctx = "hi"
        val context = mapOf(Options.CONTEXT to formatted(ctx, emptyMap()))
        assertEquals(ctx, context.context())
    }

    @Test
    fun `ordinal extract the value from a formatted value`() {
        val ordinal = true
        val ord = mapOf(Options.ORDINAL to formatted(ordinal, emptyMap()))
        assertEquals(ordinal, ord.ordinal())
    }

    @Test
    fun `count extract the value from a formatted value`() {
        val count = 5
        val mapper = englishMapper()
        val cnt = mapOf(count(formatted(count, emptyMap())))
        assertEquals(mapper.mapOrdinal(count), cnt.count(false, mapper))
    }

    @Test
    fun `TranslationProcessor VariableResolver coverage`() {
        val conf = ConfigImpl()
        assertThrows<AssertionError> {
            TranslationProcessor.customResolver.resolve(
                "foo",
                TranslationProcessor.ProcessingContext(
                    { conf.formatters[it]!!.unsafeCast() },
                    Locale.ENGLISH,
                    mapOf()
                )
            )
        }
    }

    private fun englishMapper(): PluralMapper = object : PluralMapper {

        override fun mapCardinal(count: Double): Plural {
            require(count >= 0.0) { "count must be >= 0, but was $count" }
            return Plural.OTHER
        }

        override fun mapCardinal(count: Long): Plural {
            require(count >= 0L) { "count must be >= 0, but was $count" }
            return when (count) {
                1L -> Plural.ONE
                else -> Plural.OTHER
            }
        }

        override fun mapOrdinal(count: Long): Plural {
            require(count >= 1L) { "count must be >= 1, but was $count" }
            return when (count % 10) {
                1L -> Plural.ONE
                2L -> Plural.TWO
                3L -> Plural.FEW
                else -> Plural.OTHER
            }
        }

    }


}

