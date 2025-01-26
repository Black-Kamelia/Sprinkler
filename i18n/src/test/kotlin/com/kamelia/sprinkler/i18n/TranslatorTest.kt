package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.i18n.TranslationArgument.Companion.context
import com.kamelia.sprinkler.i18n.TranslationArgument.Companion.count
import com.kamelia.sprinkler.i18n.TranslationArgument.Companion.fallbackLocale
import com.kamelia.sprinkler.i18n.TranslationArgument.Companion.fallbacks
import com.kamelia.sprinkler.i18n.TranslationArgument.Companion.ordinal
import com.kamelia.sprinkler.i18n.TranslationArgument.Companion.selectedLocale
import com.kamelia.sprinkler.i18n.TranslationArgument.Companion.variable
import com.kamelia.sprinkler.i18n.formatting.VariableFormatter.Companion.formatArgument
import java.util.Locale
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class TranslatorTest {

    @Test
    fun `the root translator does not have a prefix`() {
        val translator = Translator { }
        assertNull(translator.prefix)
    }

    @Test
    fun `the root translator is a root translator`() {
        val translator = Translator { }
        assertTrue(translator.isRoot)
    }

    @Test
    fun `section throws if the key is invalid`() {
        val translator = Translator { }
        assertThrows<IllegalArgumentException> {
            translator.section("(")
        }
    }

    @Test
    fun `section returns a new translator with the given prefix`() {
        val translator = Translator { }
        val section = translator.section("foo")
        assertEquals("foo", section.prefix)
    }

    @Test
    fun `section is not a root translator`() {
        val translator = Translator { }
        val section = translator.section("foo")
        assertFalse(section.isRoot)
    }

    @Test
    fun `section on a section returns a new translator with the given prefix appended to the previous prefix`() {
        val translator = Translator { }
        val section = translator.section("foo").section("bar")
        assertEquals("foo.bar", section.prefix)
    }

    @Test
    fun `section prepend the section prefix before the key`() {
        val translator = Translator {
            configuration {
                defaultLocale = Locale.FRANCE
            }
            translations {
                map(Locale.FRANCE, mapOf("foo" to mapOf("key" to "value")))
            }
        }
        val section = translator.section("foo")
        assertDoesNotThrow {
            assertEquals("value", section.t("key"))
        }
    }

    @Test
    fun `translateOrNull throws if the key is invalid`() {
        val translator = Translator { }
        assertThrows<IllegalArgumentException> {
            translator.tn("(", selectedLocale(Locale.ENGLISH))
        }
    }

    @Test
    fun `translateOrNull returns the translation for the given locale if it exists`() {
        val translator = Translator {
            translations {
                map(Locale.ENGLISH, mapOf("key" to "value"))
                map(Locale.FRANCE, mapOf("key" to "valeur"))
            }
        }
        assertEquals("valeur", translator.tn("key", selectedLocale(Locale.FRANCE)))
    }

    @Test
    fun `translateOrNull returns the translation for the default locale if the translation for the given locale does not exist`() {
        val translator = Translator {
            translations {
                map(Locale.ENGLISH, mapOf("key" to "value"))
            }
        }
        assertEquals("value", translator.tn("key", selectedLocale(Locale.FRANCE)))
    }

    @Test
    fun `translateOrNull returns null if the translation doesnt exist`() {
        val translator = Translator {}
        assertNull(translator.tn("key", selectedLocale(Locale.FRANCE)))
    }

    @Test
    fun `translateOrNull returns null if the translation doesnt exist and locale is default locale`() {
        val translator = Translator { }
        assertNull(translator.tn("key", selectedLocale(Locale.ENGLISH)))
    }

    @Test
    fun `translateOrNull returns null if the translation doesnt exist and default locale is null`() {
        val translator = Translator { }
        assertNull(translator.tn("key", fallbackLocale(null)))
    }

    @Test
    fun `translateOrNull prepends the prefix to the key`() {
        val translator = Translator {
            configuration {
                defaultLocale = Locale.ENGLISH
            }
            translations {
                map(Locale.ENGLISH, mapOf("foo" to mapOf("key" to "value")))
            }
        }
        assertEquals("value", translator.section("foo").tn("key", selectedLocale(Locale.ENGLISH)))
    }

    @Test
    fun `translate(String, Locale) throws if the key is invalid`() {
        val translator = Translator {
            configuration {
                defaultLocale = Locale.FRANCE
            }
        }
        assertThrows<IllegalArgumentException> {
            translator.t("(", selectedLocale(Locale.FRANCE))
        }
    }

    @Test
    fun `translate(String, Locale) returns the translation for the given locale if it exists`() {
        val translator = Translator {
            translations {
                map(Locale.ENGLISH, mapOf("key" to "value"))
                map(Locale.FRANCE, mapOf("key" to "valeur"))
            }
        }
        assertEquals("valeur", translator.t("key", selectedLocale(Locale.FRANCE)))
    }

    @Test
    fun `translate(String, Locale) returns the translation for the default locale if the translation for the given locale does not exist`() {
        val translator = Translator {
            translations {
                map(Locale.ENGLISH, mapOf("key" to "value"))
            }
        }
        assertEquals("value", translator.t("key", selectedLocale(Locale.FRANCE)))
    }

    @Test
    fun `translate(String, Locale) throws if the translation for the default locale does not exist`() {
        val translator = Translator { }
        assertThrows<IllegalArgumentException> {
            translator.t("key", selectedLocale(Locale.FRANCE))
        }
    }

    @Test
    fun `translate(String, Locale) throws if the translation for the default locale does not exist and locale is default locale`() {
        val translator = Translator { }
        assertThrows<IllegalArgumentException> {
            translator.t("key", selectedLocale(Locale.ENGLISH))
        }
    }

    @Test
    fun `translate(String, Locale) prepends the prefix to the key`() {
        val translator = Translator {
            translations {
                map(Locale.ENGLISH, mapOf("foo" to mapOf("key" to "value")))
            }
        }
        assertEquals("value", translator.section("foo").t("key", selectedLocale(Locale.ENGLISH)))
    }

    @Test
    fun `translate(String) throws if the key is invalid`() {
        val translator = Translator { }
        assertThrows<IllegalArgumentException> {
            translator.t("(")
        }
    }

    @Test
    fun `translate(String) uses the currentLocale to return the translation if it exists`() {
        val translator = Translator {
            configuration {
                defaultLocale = Locale.FRANCE
            }
            translations {
                map(Locale.ENGLISH, mapOf("key" to "value"))
                map(Locale.FRANCE, mapOf("key" to "valeur"))
            }
        }
        assertEquals("valeur", translator.t("key"))
        val currentEnglish = translator.withNewCurrentLocale(Locale.ENGLISH)
        assertEquals("value", currentEnglish.t("key"))
    }

    @Test
    fun `translate(String) throws if the translation for the currentLocale does not exist`() {
        val translator = Translator { }
        assertThrows<IllegalArgumentException> {
            translator.t("key")
        }
    }

    @Test
    fun `translate(String) throws if the translation for the currentLocale does not exist and locale is defaultLocale`() {
        val translator = Translator { }
        assertThrows<IllegalArgumentException> {
            translator.t("key")
        }
    }

    @Test
    fun `translate(String) throws if the translation for the currentLocale does not exist and locale is defaultLocale with a more detailed message if the locale exists in the translator`() {
        val translator = Translator {
            translations {
                map(Locale.ENGLISH, mapOf("key" to "value"))
            }
        }
        val exception = assertThrows<IllegalArgumentException> {
            translator.t("key", count(5), context("male"))
        }
        assertTrue("male" in exception.message!!)
        assertTrue("other" in exception.message!!)
    }

    @Test
    fun `toMap returns a map of all translations`() {
        val translator = Translator {
            translations {
                map(Locale.ENGLISH, mapOf("key.foo" to "value"))
                map(Locale.FRANCE, mapOf("key.foo" to "valeur"))
            }
        }
        assertEquals(
            mapOf(
                Locale.ENGLISH to mapOf("key.foo" to "value"),
                Locale.FRANCE to mapOf("key.foo" to "valeur")
            ),
            translator.toMap()
        )
    }

    @Test
    fun `toMap returns a map sorted on sub keys`() {
        val translator = Translator {
            translations {
                map(Locale.ENGLISH, mapOf("a.b" to "1", "a.b.a" to "2", "a.b.c" to "3", "a-b" to "4", "aaa" to "5"))
            }
        }
        assertEquals(
            mapOf(
                Locale.ENGLISH to mapOf("a.b" to "1", "a.b.a" to "2", "a.b.c" to "3", "a-b" to "4", "aaa" to "5")
            ),
            translator.toMap()
        )
    }

    @Test
    fun `toMap on section returns a map of all translations under the section`() {
        val translator = Translator {
            translations {
                map(Locale.ENGLISH, mapOf("key.foo" to "value", "key" to "va"))
                map(Locale.FRANCE, mapOf("key" to "valeur", "key.foo" to "va"))
            }
        }
        assertEquals(
            mapOf(
                Locale.ENGLISH to mapOf("foo" to "value"),
                Locale.FRANCE to mapOf("foo" to "va")
            ),
            translator.section("key").toMap()
        )
    }

    @Test
    fun `toMap on section returns a map sorted on sub keys`() {
        val translator = Translator {
            translations {
                map(Locale.ENGLISH, mapOf("a.b" to "2", "a.b.c" to "3", "b" to "2", "b.c" to "3"))
            }
        }
        assertEquals(
            mapOf(
                Locale.ENGLISH to mapOf("b" to "2", "b.c" to "3")
            ),
            translator.section("a").toMap()
        )
    }

    @Test
    fun `toString contains the prefix`() {
        val translator = Translator { }.section("foo")
        assertTrue(translator.prefix!! in translator.toString())
    }

    @Test
    fun `toString contains the current locale`() {
        val translator = Translator {
            configuration {
                defaultLocale = Locale.FRANCE
            }
        }
        assertTrue(Locale.FRANCE.toString() in translator.toString())
    }

    @Test
    fun `toString contains the default locale`() {
        val translator = Translator { }
        assertTrue(Locale.ENGLISH.toString() in translator.toString())
    }

    @Test
    fun `asRoot return this if the translator is a root`() {
        val translator = Translator { }
        assertSame(translator, translator.asRoot())
    }

    @Test
    fun `asRoot return a new translator with no prefix if the translator is not a root`() {
        val translator = Translator { }.section("foo")
        val root = translator.asRoot()
        assertNull(root.prefix)
    }

    @Test
    fun `withCurrentLocale returns this if the current locale is the same`() {
        val translator = Translator { }
        assertSame(translator, translator.withNewCurrentLocale(Locale.ENGLISH))
    }

    @Test
    fun `withCurrentLocale returns a new translator with the new current locale`() {
        val translator = Translator { }
        val newTranslator = translator.withNewCurrentLocale(Locale.FRANCE)
        assertEquals(Locale.FRANCE, newTranslator.currentLocale)
    }

    @Test
    fun `tn uses fallback keys if the key does not exist`() {
        val translator = Translator {
            translations {
                map(Locale.ENGLISH, mapOf("key" to "value"))
            }
        }
        assertEquals("value", translator.tn("foo", fallbacks("key")))
    }

    @Test
    fun `tn returns null if fallback keys do not exist`() {
        val translator = Translator {
            translations {
                map(Locale.ENGLISH, mapOf("key" to "value"))
            }
        }
        assertNull(translator.tn("foo", fallbacks("bar")))
    }

    @Test
    fun `formatters are applied to the translation`() {
        val translator = Translator {
            configuration {
                defaultLocale = Locale.US
            }
            translations {
                map(Locale.US, mapOf("key" to "value {{currency, currency}}"))
            }
        }
        assertEquals("value $1.00", translator.t("key", variable("currency", 1)))
    }

    @Test
    fun `formatters arguments present in translation value are passed to the formatter`() {
        val translator = Translator {
            configuration {
                defaultLocale = Locale.US
            }
            translations {
                map(Locale.US, mapOf("key" to "value {{currency, currency(maxFracDigits:1)}}"))
            }
        }
        assertEquals("value $1.0", translator.t("key", variable("currency", 1)))
    }

    @Test
    fun `formatters arguments passed in through t are passed to the formatter`() {
        val translator = Translator {
            configuration {
                defaultLocale = Locale.US
            }
            translations {
                map(Locale.US, mapOf("key" to "value {{currency, currency}}"))
            }
        }
        assertEquals("value $1.0", translator.t("key", variable("currency", 1, formatArgument("maxFracDigits", 1))))
    }

    @Test
    fun `formatters arguments passed in through t override the ones in the translation value`() {
        val translator = Translator {
            configuration {
                defaultLocale = Locale.US
            }
            translations {
                map(Locale.US, mapOf("key" to "value {{currency, currency(maxFracDigits:1)}}"))
            }
        }
        assertEquals(
            "value $1", translator.tn("key", variable("currency", 1, formatArgument("maxFracDigits", 0)))
        )
    }

    @Test
    fun `asParent returns this if the translator is a root`() {
        val translator = Translator { }
        assertSame(translator, translator.asParent())
    }

    @Test
    fun `asParent returns a new translator with the parent prefix if the translator is not a root`() {
        val translator = Translator { }.section("foo").section("bar")
        val parent = translator.asParent()
        assertEquals("foo", parent.prefix)
    }

    @Test
    fun `selectedLocale toString is redefined`() {
        val variable = selectedLocale(Locale.US)
        assertEquals("selectedLocale=en_US", variable.toString())
    }

    @Test
    fun `fallbackLocale toString is redefined`() {
        val variable = fallbackLocale(Locale.US)
        assertEquals("fallbackLocale=en_US", variable.toString())
    }

    @Test
    fun `fallbacks toString is redefined`() {
        val variable = fallbacks("key")
        assertEquals("fallbacks=['key']", variable.toString())
    }

    @Test
    fun `context toString is redefined`() {
        val variable = context("foo")
        assertEquals("context='foo'", variable.toString())
    }

    @Test
    fun `count toString is redefined`() {
        val variable = count(5)
        assertEquals("count=5", variable.toString())
    }

    @Test
    fun `ordinal toString is redefined`() {
        val variable = ordinal(true)
        assertEquals("ordinal=true", variable.toString())
    }

    @Test
    fun `variable toString is redefined`() {
        val variable = variable("foo", "bar", formatArgument("maxFracDigits", 1))
        assertEquals("variable='foo' -> 'bar' (args=['maxFracDigits: 1'])", variable.toString())
    }

    @Test
    fun `fallbackLocale is used instead of default locale when provided`() {
        val translator = Translator {
            configuration {
                defaultLocale = Locale.US
            }
            translations {
                map(Locale.ENGLISH, mapOf("key" to "value"))
                map(Locale.ITALIAN, mapOf("key" to "valore"))
            }
        }
        assertEquals("valore", translator.t("key", selectedLocale(Locale.FRANCE), fallbackLocale(Locale.ITALIAN)))
    }

    @Test
    fun `function adapter coverage`() {
        val f = FunctionAdapter<String, String> { it }
        assertEquals("foo", f("foo"))
        assertEquals("foo", f.apply("foo"))
    }

}
