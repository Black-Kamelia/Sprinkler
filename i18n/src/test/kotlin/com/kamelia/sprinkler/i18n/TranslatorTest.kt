package com.kamelia.sprinkler.i18n

import java.util.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TranslatorTest {

    @Test
    fun `the root translator does not have a prefix`() {
        val translator = Translator.builder(Locale.FRANCE).build()
        assertNull(translator.prefix)
    }

    @Test
    fun `the root translator is a root translator`() {
        val translator = Translator.builder(Locale.FRANCE).build()
        assertTrue(translator.isRoot)
    }

    @Test
    fun `section throws if the key is invalid`() {
        val translator = Translator.builder(Locale.FRANCE).build()
        assertThrows<IllegalArgumentException> {
            translator.section("(")
        }
    }

    @Test
    fun `section returns a new translator with the given prefix`() {
        val translator = Translator.builder(Locale.FRANCE).build()
        val section = translator.section("foo")
        assertEquals("foo", section.prefix)
    }

    @Test
    fun `section is not a root translator`() {
        val translator = Translator.builder(Locale.FRANCE).build()
        val section = translator.section("foo")
        assertFalse(section.isRoot)
    }

    @Test
    fun `section on a section returns a new translator with the given prefix appended to the previous prefix`() {
        val translator = Translator.builder(Locale.FRANCE).build()
        val section = translator.section("foo").section("bar")
        assertEquals("foo.bar", section.prefix)
    }

    @Test
    fun `translateOrNull throws if the key is invalid`() {
        val translator = Translator.builder(Locale.FRANCE).build()
        assertThrows<IllegalArgumentException> {
            translator.translateOrNull("(", Locale.FRANCE)
        }
    }

    @Test
    fun `translateOrNull returns the translation for the given locale if it exists`() {
        val translator = Translator.builder(Locale.ENGLISH)
            .addMap(Locale.ENGLISH, mapOf("key" to "value"))
            .addMap(Locale.FRANCE, mapOf("key" to "valeur"))
            .build()
        assertEquals("valeur", translator.translateOrNull("key", Locale.FRANCE))
    }

    @Test
    fun `translateOrNull returns the translation for the default locale if the translation for the given locale does not exist`() {
        val translator = Translator.builder(Locale.ENGLISH)
            .addMap(Locale.ENGLISH, mapOf("key" to "value"))
            .build()
        assertEquals("value", translator.translateOrNull("key", Locale.FRANCE))
    }

    @Test
    fun `translateOrNull returns null if the translation doesnt exist`() {
        val translator = Translator.builder(Locale.ENGLISH).build()
        assertNull(translator.translateOrNull("key", Locale.FRANCE))
    }

    @Test
    fun `translateOrNull returns null if the translation doesnt exist and locale is default locale`() {
        val translator = Translator.builder(Locale.ENGLISH).build()
        assertNull(translator.translateOrNull("key", Locale.ENGLISH))
    }

    @Test
    fun `translateOrNull prepends the prefix to the key`() {
        val translator = Translator.builder(Locale.ENGLISH)
            .addMap(Locale.ENGLISH, mapOf("foo" to mapOf("key" to "value")))
            .build()
        assertEquals("value", translator.section("foo").translateOrNull("key", Locale.ENGLISH))
    }

    @Test
    fun `translate(String, Locale) throws if the key is invalid`() {
        val translator = Translator.builder(Locale.FRANCE).build()
        assertThrows<IllegalArgumentException> {
            translator.translate("(", Locale.FRANCE)
        }
    }

    @Test
    fun `translate(String, Locale) returns the translation for the given locale if it exists`() {
        val translator = Translator.builder(Locale.ENGLISH)
            .addMap(Locale.ENGLISH, mapOf("key" to "value"))
            .addMap(Locale.FRANCE, mapOf("key" to "valeur"))
            .build()
        assertEquals("valeur", translator.translate("key", Locale.FRANCE))
    }

    @Test
    fun `translate(String, Locale) returns the translation for the default locale if the translation for the given locale does not exist`() {
        val translator = Translator.builder(Locale.ENGLISH)
            .addMap(Locale.ENGLISH, mapOf("key" to "value"))
            .build()
        assertEquals("value", translator.translate("key", Locale.FRANCE))
    }

    @Test
    fun `translate(String, Locale) throws if the translation for the default locale does not exist`() {
        val translator = Translator.builder(Locale.ENGLISH).build()
        assertThrows<IllegalArgumentException> {
            translator.translate("key", Locale.FRANCE)
        }
    }

    @Test
    fun `translate(String, Locale) throws if the translation for the default locale does not exist and locale is default locale`() {
        val translator = Translator.builder(Locale.ENGLISH).build()
        assertThrows<IllegalArgumentException> {
            translator.translate("key", Locale.ENGLISH)
        }
    }

    @Test
    fun `translate(String, Locale) prepends the prefix to the key`() {
        val translator = Translator.builder(Locale.ENGLISH)
            .addMap(Locale.ENGLISH, mapOf("foo" to mapOf("key" to "value")))
            .build()
        assertEquals("value", translator.section("foo").translate("key", Locale.ENGLISH))
    }

    @Test
    fun `translate(String) throws if the key is invalid`() {
        val translator = Translator.builder(Locale.FRANCE).build()
        assertThrows<IllegalArgumentException> {
            translator.translate("(")
        }
    }

    @Test
    fun `translate(String) uses the currentLocale to return the translation if it exists`() {
        val translator = Translator.builder(Locale.ENGLISH)
            .addMap(Locale.ENGLISH, mapOf("key" to "value"))
            .addMap(Locale.FRANCE, mapOf("key" to "valeur"))
            .withCurrentLocale(Locale.FRANCE)
            .build()
        assertEquals("valeur", translator.translate("key"))
        val currentEnglish = translator.withNewCurrentLocale(Locale.ENGLISH)
        assertEquals("value", currentEnglish.translate("key"))
    }

    @Test
    fun `translate(String) throws if the translation for the currentLocale does not exist`() {
        val translator = Translator.builder(Locale.ENGLISH)
            .withCurrentLocale(Locale.FRANCE)
            .build()
        assertThrows<IllegalArgumentException> {
            translator.translate("key")
        }
    }

    @Test
    fun `translate(String) throws if the translation for the currentLocale does not exist and locale is defaultLocale`() {
        val translator = Translator.builder(Locale.ENGLISH).build()
        assertThrows<IllegalArgumentException> {
            translator.translate("key")
        }
    }

    @Test
    fun `toMap returns a map of all translations`() {
        val translator = Translator.builder(Locale.ENGLISH)
            .addMap(Locale.ENGLISH, mapOf("key.foo" to "value"))
            .addMap(Locale.FRANCE, mapOf("key" to "valeur"))
            .build()
        assertEquals(
            mapOf(
                Locale.ENGLISH to mapOf("key.foo" to "value"),
                Locale.FRANCE to mapOf("key" to "valeur")
            ),
            translator.toMap()
        )
    }

    @Test
    fun `toMap returns a map sorted on sub keys`() {
        val translator = Translator.builder(Locale.ENGLISH)
            .addMap(Locale.ENGLISH, mapOf("a.b.a" to "2", "a-b" to "4", "a.b" to "1", "a.b.c" to "3", "aaa" to "5"))
            .build()
        assertEquals(
            mapOf(
                Locale.ENGLISH to mapOf("a.b" to "1", "a.b.a" to "2", "a.b.c" to "3", "a-b" to "4", "aaa" to "5")
            ),
            translator.toMap()
        )
    }

    @Test
    fun `toMap on section returns a map of all translations under the section`() {
        val translator = Translator.builder(Locale.ENGLISH)
            .addMap(Locale.ENGLISH, mapOf("key.foo" to "value"))
            .addMap(Locale.FRANCE, mapOf("key" to "valeur"))
            .build()
        assertEquals(
            mapOf(
                Locale.ENGLISH to mapOf("foo" to "value"),
                Locale.FRANCE to mapOf()
            ),
            translator.section("key").toMap()
        )
    }

    @Test
    fun `toMap on section returns a map sorted on sub keys`() {
        val translator = Translator.builder(Locale.ENGLISH)
            .addMap(Locale.ENGLISH, mapOf("a-b" to "4", "a.b" to "2", "a.b.c" to "3", "a" to "1"))
            .build()
        println(translator.toMap())
        assertEquals(
            mapOf(
                Locale.ENGLISH to mapOf("b" to "2", "b.c" to "3")
            ),
            translator.section("a").toMap()
        )
    }

    @Test
    fun `toString contains the prefix`() {
        val translator = Translator.builder(Locale.ENGLISH).build().section("foo")
        assertTrue(translator.prefix!! in translator.toString())
    }

    @Test
    fun `toString contains the current locale`() {
        val translator = Translator.builder(Locale.ENGLISH)
            .withCurrentLocale(Locale.FRANCE)
            .build()
        assertTrue(Locale.FRANCE.toString() in translator.toString())
    }

    @Test
    fun `toString contains the default locale`() {
        val translator = Translator.builder(Locale.ENGLISH).build()
        assertTrue(Locale.ENGLISH.toString() in translator.toString())
    }

    @Test
    fun `toString contains the translations`() {
        val translator = Translator.builder(Locale.ENGLISH)
            .addMap(Locale.ENGLISH, mapOf("key" to "value"))
            .build()
        assertTrue(translator.toMap().toString() in translator.toString())
    }

}
