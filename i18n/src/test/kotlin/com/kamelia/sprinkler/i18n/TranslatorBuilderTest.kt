package com.kamelia.sprinkler.i18n

import java.util.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TranslatorBuilderTest {

    @Test
    fun `defaultLocale is the locale specifier in the constructor`() {
        val locale = Locale.FRANCE
        val translator = Translator.builder(locale).build()
        Assertions.assertEquals(locale, translator.defaultLocale)
    }

    @Test
    fun `currentLocale is set to the default locale by default`() {
        val locale = Locale.FRANCE
        val translator = Translator.builder(locale).build()
        Assertions.assertEquals(locale, translator.currentLocale)
    }

    @Test
    fun `addMap adds the map to the translator`() {
        val key = "test"
        val value = "this is a test"
        val locale = Locale.FRANCE

        val translator = Translator.builder(Locale.ENGLISH)
            .addMap(locale, mapOf(key to value))
            .build()
        Assertions.assertEquals(value, translator.t(key, locale))
    }

    @Test
    fun `addMaps adds the maps to the translator`() {
        val key = "test"
        val value = "this is a test"
        val locale = Locale.FRANCE

        val translator = Translator.builder(Locale.ENGLISH)
            .addMaps(mapOf(locale to mapOf(key to value)))
            .build()
        Assertions.assertEquals(value, translator.t(key, locale))
    }

    @Test
    fun `withDefaultLocale changes the final default locale`() {
        val translator = Translator.builder(Locale.ENGLISH)
            .withDefaultLocale(Locale.FRANCE)
            .build()
        Assertions.assertEquals(Locale.FRANCE, translator.defaultLocale)
    }

    @Test
    fun `withCurrentLocale changes the final current locale`() {
        val translator = Translator.builder(Locale.ENGLISH)
            .withCurrentLocale(Locale.FRANCE)
            .build()
        Assertions.assertEquals(Locale.FRANCE, translator.currentLocale)
    }

    @Test
    fun `FAIL duplicate policy throws if a key is duplicated`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .withDuplicatedKeyResolutionPolicy(TranslatorBuilder.DuplicatedKeyResolution.FAIL)
            .addMap(Locale.ENGLISH, mapOf("test" to "test"))
            .addMap(Locale.ENGLISH, mapOf("test" to "test"))
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `KEEP_FIRST duplicate policy keeps the first value`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .withDuplicatedKeyResolutionPolicy(TranslatorBuilder.DuplicatedKeyResolution.KEEP_FIRST)
            .addMap(Locale.ENGLISH, mapOf("test" to "test"))
            .addMap(Locale.ENGLISH, mapOf("test" to "test2"))
        val translator = builder.build()
        Assertions.assertEquals("test", translator.t("test"))
    }

    @Test
    fun `KEEP_LAST duplicate policy keeps the last value`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .withDuplicatedKeyResolutionPolicy(TranslatorBuilder.DuplicatedKeyResolution.KEEP_LAST)
            .addMap(Locale.ENGLISH, mapOf("test" to "test"))
            .addMap(Locale.ENGLISH, mapOf("test" to "test2"))
        val translator = builder.build()
        Assertions.assertEquals("test2", translator.t("test"))
    }

    @Test
    fun `stringListComparator works with empty lists`() {
        Assertions.assertEquals(0, stringListComparator(emptyList(), emptyList()))
    }

    @Test
    fun `stringListComparator works with lists of same size`() {
        Assertions.assertEquals(0, stringListComparator(listOf("test"), listOf("test")))
    }

    @Test
    fun `stringListComparator works with lists of different size (first is smaller)`() {
        Assertions.assertEquals(-1, stringListComparator(emptyList(), listOf("test")))
    }

    @Test
    fun `stringListComparator works with lists of different size (first is bigger)`() {
        Assertions.assertEquals(1, stringListComparator(listOf("test"), emptyList()))
    }

    @Test
    fun `stringListComparator works with lists of same size but different content`() {
        Assertions.assertEquals(-1, stringListComparator(listOf("test"), listOf("test2")))
    }

}
