package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.i18n.TranslationArgument.Companion.context
import com.kamelia.sprinkler.i18n.TranslationArgument.Companion.count
import com.kamelia.sprinkler.i18n.TranslationArgument.Companion.ordinal
import com.kamelia.sprinkler.i18n.TranslationArgument.Companion.selectedLocale
import com.kamelia.sprinkler.i18n.TranslationArgument.Companion.variable
import com.kamelia.sprinkler.i18n.TranslationProcessor.countValue
import com.kamelia.sprinkler.i18n.TranslatorBuilder.Companion.inner
import com.kamelia.sprinkler.i18n.pluralization.Plural
import com.kamelia.sprinkler.i18n.pluralization.PluralRuleProvider
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
            arrayOf(context("context")),
            englishMapper()
        )
        assertEquals("base.key_context", key)
    }

    @Test
    fun `plural option correctly appended to the base key`() {
        val key = TranslationProcessor.buildKey(
            "base.key",
            arrayOf(count(5.0)),
            englishMapper()
        )
        assertEquals("base.key_other", key)
    }

    @Test
    fun `context and plural options correctly appended to the base key`() {
        val key = TranslationProcessor.buildKey(
            "base.key",
            arrayOf(context("context"), count(1)),
            englishMapper()
        )
        assertEquals("base.key_context_one", key)
    }

    @Test
    fun `context and plural options correctly appended to the base key for ScientificNotationNumber`() {
        val key = TranslationProcessor.buildKey(
            "base.key",
            arrayOf(context("context"), count(ScientificNotationNumber.from(1))),
            englishMapper()
        )
        assertEquals("base.key_context_one", key)
    }

    @Test
    fun `buildKey returns the base key when no option is provided`() {
        val key = TranslationProcessor.buildKey(
            "base.key",
            arrayOf(),
            englishMapper()
        )
        assertEquals("base.key", key)
    }

    @Test
    fun `plural ordinal option is correctly appended to the base key`() {
        val key = TranslationProcessor.buildKey(
            "base.key",
            arrayOf(count(2), ordinal(true)),
            englishMapper()
        )
        assertEquals("base.key_ordinal_two", key)
    }

    @Test
    fun `plural ordinal option is correctly appended to the base key for ScientificNotationNumber`() {
        val key = TranslationProcessor.buildKey(
            "base.key",
            arrayOf(count(ScientificNotationNumber.from(2)), ordinal(true)),
            englishMapper()
        )
        assertEquals("base.key_ordinal_two", key)
    }

    @Test
    fun `plural ordinal and context options are correctly appended to the base key`() {
        val key = TranslationProcessor.buildKey(
            "base.key",
            arrayOf(context("context"), count(2), ordinal(true)),
            englishMapper()
        )
        assertEquals("base.key_context_ordinal_two", key)
    }

    @Test
    fun `interpolate correctly replaces variables`() {
        val conf = ConfigBuilderImpl()
        val value = TranslationProcessor.translate(
            TranslatorData(
                Locale.US,
                mapOf(Locale.US to mapOf("hello" to "Hello {{name}}")),
                conf.interpolationDelimiter.inner,
                conf.pluralRuleProviderFactory,
                { conf.formatters[it]!! },
                conf.missingKeyPolicy,
                conf.localeSpecializationReduction
            ),
            "hello",
            arrayOf(variable("name", "John")),
            Locale.US,
        )
        assertEquals("Hello John", value)
    }

    @Test
    fun `translate returns null if the locale is not found`() {
        val conf = ConfigBuilderImpl()
        val value = TranslationProcessor.translate(
            TranslatorData(
                Locale.FRANCE,
                emptyMap(),
                conf.interpolationDelimiter.inner,
                conf.pluralRuleProviderFactory,
                { conf.formatters[it]!! },
                conf.missingKeyPolicy,
                conf.localeSpecializationReduction
            ),
            "foo",
            arrayOf(),
            Locale.FRANCE,
        )
        assertNull(value)
    }

    @Test
    fun `translate returns null if the key is not found`() {
        val conf = ConfigBuilderImpl()
        val value = TranslationProcessor.translate(
            TranslatorData(
                Locale.FRANCE,
                mapOf(Locale.FRANCE to mapOf()),
                conf.interpolationDelimiter.inner,
                conf.pluralRuleProviderFactory,
                { conf.formatters[it]!! },
                conf.missingKeyPolicy,
                conf.localeSpecializationReduction,
            ),
            "foo",
            arrayOf(),
            Locale.FRANCE,
        )
        assertNull(value)
    }

    @Test
    fun `translate returns the value if the key is found`() {
        val conf = ConfigBuilderImpl()
        val value = TranslationProcessor.translate(
            TranslatorData(
                Locale.FRANCE,
                mapOf(Locale.FRANCE to mapOf("foo" to "bar")),
                conf.interpolationDelimiter.inner,
                conf.pluralRuleProviderFactory,
                { conf.formatters[it]!! },
                conf.missingKeyPolicy,
                conf.localeSpecializationReduction,
            ),
            "foo",
            arrayOf(),
            Locale.FRANCE,
        )
        assertEquals("bar", value)
    }

    @Test
    fun `translate applies the options to the key`() {
        val conf = ConfigBuilderImpl()
        val value = TranslationProcessor.translate(
            TranslatorData(
                Locale.FRANCE,
                mapOf(Locale.FRANCE to mapOf("foo_context" to "bar")),
                conf.interpolationDelimiter.inner,
                conf.pluralRuleProviderFactory,
                { conf.formatters[it]!! },
                conf.missingKeyPolicy,
                conf.localeSpecializationReduction,
            ),
            "foo",
            arrayOf(context("context")),
            Locale.FRANCE,
        )
        assertEquals("bar", value)
    }

    @Test
    fun `translate throws if an interpolation variable is missing`() {
        val conf = ConfigBuilderImpl()
        assertThrows<IllegalArgumentException> {
            TranslationProcessor.translate(
                TranslatorData(
                    Locale.ENGLISH,
                    mapOf(Locale.ENGLISH to mapOf("foo" to "Hello {{name}}")),
                    conf.interpolationDelimiter.inner,
                    conf.pluralRuleProviderFactory,
                    { conf.formatters[it]!! },
                    conf.missingKeyPolicy,
                    conf.localeSpecializationReduction,
                ),
                "foo",
                arrayOf(),
                Locale.ENGLISH,
            )
        }
    }

    @Test
    fun `translate(String) throws an IAE if a variable is missing`() {
        val translator = Translator {
            translations {
                map(Locale.ENGLISH, mapOf("key" to "value {{missing}}"))
            }
        }
        assertThrows<IllegalArgumentException> {
            translator.t("key", selectedLocale(Locale.ENGLISH), variable("foo", 2))
        }
    }

    @Test
    fun `count throws an AssertionError if the count is not a valid type`() {
        val mapper = object : PluralRuleProvider {
            override fun cardinal(count: Double): Plural = Plural.OTHER
            override fun ordinal(count: Long): Plural = Plural.OTHER
        }
        assertThrows<AssertionError> {
            arrayOf(TranslationArgument.Companion.Count("")).countValue(false, mapper)
        }
    }

    @Test
    fun `TranslationProcessor VariableResolver coverage`() {
        val conf = ConfigBuilderImpl()
        assertThrows<AssertionError> {
            TranslationProcessor.customResolver.resolve(
                "foo",
                TranslationProcessor.ProcessingContext(
                    { conf.formatters[it]!!.unsafeCast() },
                    Locale.ENGLISH,
                    arrayOf()
                )
            )
        }
    }

    private fun englishMapper(): PluralRuleProvider = object : PluralRuleProvider {

        override fun cardinal(count: Double): Plural {
            require(count >= 0.0) { "count must be >= 0, but was $count" }
            return Plural.OTHER
        }

        override fun cardinal(count: Long): Plural {
            require(count >= 0L) { "count must be >= 0, but was $count" }
            return when (count) {
                1L -> Plural.ONE
                else -> Plural.OTHER
            }
        }

        override fun ordinal(count: Long): Plural {
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

