package com.kamelia.sprinkler.i18n

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class TranslatorTest {

    @Test
    fun `the root translator does not have a prefix`() {
        val translator = TranslatorBuilder(Locale.FRANCE).build()
        assertNull(translator.prefix)
    }

    @Test
    fun `the root translator is a root translator`() {
        val translator = TranslatorBuilder(Locale.FRANCE).build()
        assertTrue(translator.isRoot)
    }

    @Test
    fun `section throws if the key is invalid`() {
        val translator = TranslatorBuilder(Locale.FRANCE).build()
        assertThrows<IllegalArgumentException> {
            translator.section("(")
        }
    }

    @Test
    fun `section returns a new translator with the given prefix`() {
        val translator = TranslatorBuilder(Locale.FRANCE).build()
        val section = translator.section("foo")
        assertEquals("foo", section.prefix)
    }

    @Test
    fun `section is not a root translator`() {
        val translator = TranslatorBuilder(Locale.FRANCE).build()
        val section = translator.section("foo")
        assertFalse(section.isRoot)
    }

    @Test
    fun `section on a section returns a new translator with the given prefix appended to the previous prefix`() {
        val translator = TranslatorBuilder(Locale.FRANCE).build()
        val section = translator.section("foo").section("bar")
        assertEquals("foo.bar", section.prefix)
    }

    @Test
    fun `translateOrNull throws if the key is invalid`() {
        val translator = TranslatorBuilder(Locale.FRANCE).build()
        assertThrows<IllegalArgumentException> {
            translator.translateOrNull("(", Locale.FRANCE)
        }
    }

    @Test
    fun `translateOrNull returns the translation for the given locale if it exists`() {
        val translator = TranslatorBuilder(Locale.ENGLISH)
            .addMap(Locale.ENGLISH, mapOf("key" to "value"))
            .addMap(Locale.FRANCE, mapOf("key" to "valeur"))
            .build()
        assertEquals("valeur", translator.translateOrNull("key", Locale.FRANCE))
    }

    @Test
    fun `translateOrNull returns the translation for the default locale if the translation for the given locale does not exist`() {
        val translator = TranslatorBuilder(Locale.ENGLISH)
            .addMap(Locale.ENGLISH, mapOf("key" to "value"))
            .build()
        assertEquals("value", translator.translateOrNull("key", Locale.FRANCE))
    }

    @Test
    fun `translateOrNull returns null if the translation doesnt exist`() {
        val translator = TranslatorBuilder(Locale.ENGLISH).build()
        assertNull(translator.translateOrNull("key", Locale.FRANCE))
    }

    @Test
    fun `translateOrNull prepends the prefix to the key`() {
        val translator = TranslatorBuilder(Locale.ENGLISH)
            .addMap(Locale.ENGLISH, mapOf("foo" to mapOf("key" to "value")))
            .build()
        assertEquals("value", translator.section("foo").translateOrNull("key", Locale.ENGLISH))
    }

    @Test
    fun `translate(String, Locale) throws if the key is invalid`() {
        val translator = TranslatorBuilder(Locale.FRANCE).build()
        assertThrows<IllegalArgumentException> {
            translator.translate("(", Locale.FRANCE)
        }
    }

    @Test
    fun `translate(String, Locale) returns the translation for the given locale if it exists`() {
        val translator = TranslatorBuilder(Locale.ENGLISH)
            .addMap(Locale.ENGLISH, mapOf("key" to "value"))
            .addMap(Locale.FRANCE, mapOf("key" to "valeur"))
            .build()
        assertEquals("valeur", translator.translate("key", Locale.FRANCE))
    }

    @Test
    fun `translate(String, Locale) returns the translation for the default locale if the translation for the given locale does not exist`() {
        val translator = TranslatorBuilder(Locale.ENGLISH)
            .addMap(Locale.ENGLISH, mapOf("key" to "value"))
            .build()
        assertEquals("value", translator.translate("key", Locale.FRANCE))
    }

    @Test
    fun `translate(String, Locale) throws if the translation for the default locale does not exist`() {
        val translator = TranslatorBuilder(Locale.ENGLISH).build()
        assertThrows<IllegalArgumentException> {
            translator.translate("key", Locale.FRANCE)
        }
    }

    @Test
    fun `translate(String, Locale) prepends the prefix to the key`() {
        val translator = TranslatorBuilder(Locale.ENGLISH)
            .addMap(Locale.ENGLISH, mapOf("foo" to mapOf("key" to "value")))
            .build()
        assertEquals("value", translator.section("foo").translate("key", Locale.ENGLISH))
    }

    @Test
    fun `translate(String) uses the currentLocale`() {
        val translator = TranslatorBuilder(Locale.ENGLISH)
            .addMap(Locale.ENGLISH, mapOf("key" to "value"))
            .addMap(Locale.FRANCE, mapOf("key" to "valeur"))
            .withCurrentLocale(Locale.FRANCE)
            .build()
        assertEquals("valeur", translator.translate("key"))
        val currentEnglish = translator.withNewCurrentLocale(Locale.ENGLISH)
        assertEquals("value", currentEnglish.translate("key"))
    }

}
