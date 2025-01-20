package com.kamelia.sprinkler.i18n.impl

import com.kamelia.sprinkler.i18n.absoluteResource
import com.kamelia.sprinkler.i18n.formatting.VariableFormatter
import com.kamelia.sprinkler.i18n.pluralization.Plural
import com.kamelia.sprinkler.i18n.pluralization.PluralMapper
import java.util.Locale
import java.util.function.Consumer
import java.util.function.Function
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class TranslatorBuilderTest {

    @Test
    fun `defaultLocale is the locale specifier in the constructor`() {
        val locale = Locale.FRANCE
        val translator = Translator {
            configuration {
                defaultLocale = locale
            }
        }
        Assertions.assertEquals(locale, translator.defaultLocale)
    }

    @Test
    fun `currentLocale is set to the default locale by default`() {
        val locale = Locale.FRANCE
        val translator = Translator {
            configuration {
                defaultLocale = locale
            }
        }
        Assertions.assertEquals(locale, translator.currentLocale)
    }

    @Test
    fun `addMap adds the map to the translator`() {
        val key = "test"
        val value = "this is a test"
        val locale = Locale.FRANCE

        val translator = Translator {
            translations {
                map(locale, mapOf(key to value))
            }
        }
        Assertions.assertEquals(value, translator.t(key, locale))
    }

    @Test
    fun `addMaps adds the maps to the translator`() {
        val key = "test"
        val value = "this is a test"
        val locale = Locale.FRANCE

        val translator = Translator {
            translations {
                maps(mapOf(locale to mapOf(key to value)))
            }
        }
        Assertions.assertEquals(value, translator.t(key, locale))
    }

    @Test
    fun `FAIL duplicate policy throws if a key is duplicated`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {
                    duplicatedKeyResolution = TranslatorBuilder.DuplicatedKeyResolution.FAIL
                    map(Locale.ENGLISH, mapOf("test" to "test"))
                    map(Locale.ENGLISH, mapOf("test" to "test2"))
                }
            }
        }
    }

    @Test
    fun `KEEP_FIRST duplicate policy keeps the first value`() {
        val translator = Translator {
            translations {
                duplicatedKeyResolution = TranslatorBuilder.DuplicatedKeyResolution.KEEP_FIRST
                map(Locale.ENGLISH, mapOf("test" to "test"))
                map(Locale.ENGLISH, mapOf("test" to "test2"))
            }
        }
        Assertions.assertEquals("test", translator.t("test"))
    }

    @Test
    fun `KEEP_LAST duplicate policy keeps the last value`() {
        val translator = Translator {
            translations {
                duplicatedKeyResolution = TranslatorBuilder.DuplicatedKeyResolution.KEEP_LAST
                map(Locale.ENGLISH, mapOf("test" to "test"))
                map(Locale.ENGLISH, mapOf("test" to "test2"))
            }
        }
        Assertions.assertEquals("test2", translator.t("test"))
    }

    @Test
    fun `addMap throws an IAE if a value contains a format that is not present in the configuration`() {
        assertThrows<IllegalArgumentException> {
            Translator {
                translations {
                    map(Locale.ENGLISH, mapOf("test" to "test {{name, unknown}}"))
                }
            }
        }
    }

    @Test
    fun `addMap does not throw if a value contains a format that is present in the configuration`() {
        assertDoesNotThrow {
            Translator {
                translations {
                    map(Locale.ENGLISH, mapOf("test" to "test {{name, date}}"))
                }
            }
        }
    }

    @Test
    fun `addMap throws an IAE if a value contains a variable that is does not respect the format (illegal name)`() {
        assertThrows<IllegalArgumentException> {
            Translator {
                translations {
                    map(Locale.ENGLISH, mapOf("test" to "test {{name#}}"))
                }
            }
        }
    }

    @Test
    fun `addMap throws an IAE if a value contains a variable that is does not respect the format (illegal format)`() {
        assertThrows<IllegalArgumentException> {
            Translator {
                configuration {
                    formatters += "12#" to VariableFormatter.date()
                }

                translations {
                    map(Locale.ENGLISH, mapOf("test" to "test {{name, 12#}}"))
                }
            }
        }
    }

    @Test
    fun `addMap throws an IAE if a value contains a variable that is does not respect the format (format with parenthesis and no param)`() {
        assertThrows<IllegalArgumentException> {
            Translator {
                translations {
                    map(Locale.ENGLISH, mapOf("test" to "test {{name, date()}}"))
                }
            }
        }
    }

    @Test
    fun `build throws an ISE if at least two locals does not have the same keys`() {
        assertThrows<IllegalStateException> {
            Translator {
                checkMissingKeysOnBuild = true

                translations {
                    map(Locale.ENGLISH, mapOf("test" to "test"))
                    map(Locale.FRANCE, mapOf("toto" to "toto"))
                }
            }
        }
    }

    @Test
    fun `build does not throw an ISE if throwOnMissingKey is false and at least two locals does not have the same keys`() {
        assertDoesNotThrow {
            Translator {
                checkMissingKeysOnBuild = false

                translations {
                    map(Locale.ENGLISH, mapOf("test" to "test"))
                    map(Locale.FRANCE, mapOf())
                }
            }
        }
    }

    @Test
    fun `keyComparator correctly compares (a lt b)`() {
        val keyComparator = ContentImpl.keyComparator()
        val a = "a"
        val b = "b"
        assertTrue(keyComparator.compare(a, b) < 0)
    }

    @Test
    fun `keyComparator correctly compares (a eq a)`() {
        val keyComparator = ContentImpl.keyComparator()
        val a = "a"
        assertTrue(keyComparator.compare(a, a) == 0)
    }

    @Test
    fun `keyComparator correctly compares (b gt a)`() {
        val keyComparator = ContentImpl.keyComparator()
        val a = "a"
        val b = "b"
        assertTrue(keyComparator.compare(b, a) > 0)
    }

    @Test
    fun `keyComparator correctly handles dots`() {
        val keyComparator = ContentImpl.keyComparator()
        val a = "a.b"
        val b = "a.c"
        assertTrue(keyComparator.compare(a, b) < 0)
    }

    @Test
    fun `keyComparator correctly handles dots when a key is the prefix`() {
        val keyComparator = ContentImpl.keyComparator()
        val a = "a"
        val b = "a.b"
        assertTrue(keyComparator.compare(a, b) < 0)
    }

    @Test
    fun `calling configuration more than once throws an ISE`() {
        assertThrows<IllegalStateException> {
            Translator {
                configuration {}
                configuration {}
            }
        }
    }

    @Test
    fun `calling translations more than once throws an ISE`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {}
                translations {}
            }
        }
    }

    @Test
    fun `trying to call a block on an already built translator builder throws an ISE`() {
        lateinit var translator: TranslatorBuilder
        Translator {
            translator = this
        }
        assertThrows<IllegalStateException> {
            translator.translations {}
        }
        assertThrows<IllegalStateException> {
            translator.configuration {}
        }
    }

    @Test
    fun `localeSpecializationReduction correctly defaults in the described order`() {
        val reduction = TranslatorBuilder.defaultLocaleSpecializationReduction()
        assertEquals(Locale.ENGLISH, reduction(Locale.US))
        val full = Locale.forLanguageTag("en-test-US-POSIX-x-foo-bar")
        assertEquals(full.stripExtensions(), reduction(full))
        assertEquals(Locale.forLanguageTag("en-test"), reduction(Locale.forLanguageTag("en-test-US")))
        assertEquals(Locale.forLanguageTag("en-US"), reduction(Locale.forLanguageTag("en-US-POSIX")))
        assertEquals(Locale.ENGLISH, reduction(Locale.forLanguageTag("en-test")))
        assertNull(reduction(Locale.ENGLISH))
    }

    @Test
    fun `custom localeSpecializationReduction is correctly applied`() {
        val translator = Translator {
            configuration {
                localeSpecializationReduction = { if (it == Locale.JAPAN) null else Locale.JAPAN }
            }
            translations {
                map(Locale.JAPAN, mapOf("test" to "tesuto"))
                map(Locale.ENGLISH, mapOf("test" to "test", "test2" to "test2"))
                map(Locale.FRENCH, mapOf("test3" to "test3"))
            }
        }

        assertEquals("test3", translator.t("test3", Locale.FRENCH))
        assertEquals("test2", translator.t("test2", Locale.FRENCH))
        assertEquals("tesuto", translator.t("test", Locale.FRENCH))
    }

    @Test
    fun `custom localeParser is correctly applied`() {
        val translator = Translator {
            translations {
                localeParser = { Locale.CHINESE }
                file(absoluteResource("builder_test/fr.json"))
            }
        }

        assertEquals(setOf(Locale.CHINESE), translator.toMap().keys)
    }

    @Test
    fun `java api coverage`() {
        Translator(Consumer { })
        TranslatorBuilder.defaultLocaleSpecializationReductionJava()
        val t = Translator {
            configuration(Consumer {
                it.localeSpecializationReductionJava = Function { null }
                it.localeSpecializationReductionJava
                it.pluralMapperFactoryJava = Function {
                    object : PluralMapper {
                        override fun mapCardinal(count: Double): Plural = Plural.OTHER
                        override fun mapOrdinal(count: Long): Plural = Plural.OTHER
                    }
                }
                it.pluralMapperFactoryJava
            })
            translations(Consumer {
                it.localeParserJava
                it.localeParserJava = Function { Locale.ENGLISH }
                it.map(Locale.FRANCE, mapOf("test" to "test"))
                it.file(absoluteResource("builder_test/fr.json"))
            })
        }
        assertThrows<IllegalArgumentException> {
            t.t("test", mapOf(count(5)), Locale.FRANCE)
        }
    }

}

