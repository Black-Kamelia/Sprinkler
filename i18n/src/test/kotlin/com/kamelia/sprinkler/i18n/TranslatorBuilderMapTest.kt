package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.i18n.TranslationArgument.Companion.selectedLocale
import com.kamelia.sprinkler.util.unsafeCast
import java.util.Locale
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class TranslatorBuilderMapTest {

    @Test
    fun `map throws an ISE on build if the map contains invalid key`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {
                    map(Locale.FRANCE, mapOf("invalid#" to 5))
                }
            }
        }
    }

    @Test
    fun `maps throws an ISE on build if one of the maps contains invalid key`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {
                    maps(mapOf(Locale.FRANCE to mapOf("invalid#" to 5)))
                }
            }
        }
    }

    @Test
    fun `map throws an ISE on build if a key is null`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {
                    map(Locale.FRANCE, mapOf(null to 5).unsafeCast())
                }
            }
        }
    }

    @Test
    fun `maps throws an ISE on build if a key is null`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {
                    maps(mapOf(Locale.FRANCE to mapOf(null to 5).unsafeCast()))
                }
            }
        }
    }

    @Test
    fun `map throws an ISE on build if a value is null`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {
                    map(Locale.FRANCE, mapOf("test" to null).unsafeCast())
                }
            }
        }
    }

    @Test
    fun `maps throws an ISE on build if a value is null`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {
                    maps(mapOf(Locale.FRANCE to mapOf("test" to null).unsafeCast()))
                }
            }
        }
    }

    @Test
    fun `map throws an ISE on build if the map contains a map containing an invalid key`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {
                    map(Locale.FRANCE, mapOf("test" to mapOf("invalid#" to 5)))
                }
            }
        }
    }

    @Test
    fun `maps throws an ISE on build if the map contains a map containing an invalid key`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {
                    maps(mapOf(Locale.FRANCE to mapOf("test" to mapOf("invalid#" to 5))))
                }
            }
        }
    }

    @Test
    fun `map throws an ISE on build if the map contains a map containing a null key`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {
                    map(Locale.FRANCE, mapOf("test" to mapOf(null to 5)).unsafeCast())
                }
            }
        }
    }

    @Test
    fun `maps throws an ISE on build if the map contains a map containing a null key`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {
                    maps(mapOf(Locale.FRANCE to mapOf("test" to mapOf(null to 5)).unsafeCast()))
                }
            }
        }
    }

    @Test
    fun `map throws an ISE on build if the map contains a map containing a null value`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {
                    map(Locale.FRANCE, mapOf("test" to mapOf("test" to null)))
                }
            }
        }
    }

    @Test
    fun `maps throws an ISE on build if the map contains a map containing a null value`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {
                    maps(mapOf(Locale.FRANCE to mapOf("test" to mapOf("test" to null))))
                }
            }
        }
    }

    @Test
    fun `map throws an ISE on build if the map contains a list containing an invalid value`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {
                    map(Locale.FRANCE, mapOf("test" to listOf(Any())))
                }
            }
        }
    }

    @Test
    fun `maps throws an ISE on build if the map contains a list containing an invalid value`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {
                    maps(mapOf(Locale.FRANCE to mapOf("test" to listOf(Any()))))
                }
            }
        }
    }

    @Test
    fun `map throws an ISE on build if the map contains a map containing a key that is not a string`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {
                    map(Locale.FRANCE, mapOf("test" to mapOf(5 to "test")))
                }
            }
        }
    }

    @Test
    fun `maps throws an ISE on build if the map contains a map containing a key that is not a string`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {
                    maps(mapOf(Locale.FRANCE to mapOf("test" to mapOf(5 to "test"))))
                }
            }
        }
    }

    @Test
    fun `map works with list`() {
        val translator = Translator {
            translations {
                map(Locale.ENGLISH, mapOf("test" to listOf("test")))
            }
        }
        Assertions.assertEquals("test", translator.t("test.0"))
    }

    @Test
    fun `maps works with list`() {
        val translator = Translator {
            translations {
                maps(mapOf(Locale.ENGLISH to mapOf("test" to listOf("test"))))
            }
        }
        Assertions.assertEquals("test", translator.t("test.0"))
    }

    @Test
    fun `map works with map`() {
        val translator = Translator {
            translations {
                map(Locale.ENGLISH, mapOf("test" to mapOf("test" to "test")))
            }
        }
        Assertions.assertEquals("test", translator.t("test.test"))
    }

    @Test
    fun `maps works with map`() {
        val translator = Translator {
            translations {
                maps(mapOf(Locale.ENGLISH to mapOf("test" to mapOf("test" to "test"))))
            }
        }
        Assertions.assertEquals("test", translator.t("test.test"))
    }

    @Test
    fun `map does not throw for valid types`() {
        assertDoesNotThrow {
            Translator {
                translations {
                    map(
                        Locale.ENGLISH, mapOf(
                            "test" to "test",
                            "test2" to 0.toByte(),
                            "test3" to 0.toShort(),
                            "test4" to 0,
                            "test5" to 0L,
                            "test6" to 0f,
                            "test7" to .0,
                            "test8" to true,
                            "test9" to listOf("test"),
                            "test10" to mapOf("test" to "test")
                        )
                    )
                }
            }
        }
    }

    @Test
    fun `maps does not throw for valid types`() {
        assertDoesNotThrow {
            Translator {
                translations {
                    maps(
                        mapOf(
                            Locale.ENGLISH to mapOf(
                                "test" to "test",
                                "test2" to 0.toByte(),
                                "test3" to 0.toShort(),
                                "test4" to 0,
                                "test5" to 0L,
                                "test6" to 0f,
                                "test7" to .0,
                                "test8" to true,
                                "test9" to listOf("test"),
                                "test10" to mapOf("test" to "test")
                            )
                        )
                    )
                }
            }
        }
    }

    @Test
    fun `map with dotted key correctly nest the value in the translator`() {
        val translator = Translator {
            translations {
                map(Locale.FRANCE, mapOf("test.test" to "test"))
            }
        }
        Assertions.assertEquals("test", translator.t("test.test", selectedLocale(Locale.FRANCE)))
    }

    @Test
    fun `maps with dotted key correctly nest the value in the translator`() {
        val translator = Translator {
            translations {
                maps(mapOf(Locale.FRANCE to mapOf("test.test" to "test")))
            }
        }
        Assertions.assertEquals("test", translator.t("test.test", selectedLocale(Locale.FRANCE)))
    }

    @Test
    fun `trying to add maps after the translator has been built throws an ISE`() {
        lateinit var content: TranslatorBuilder.Content
        Translator {
            translations {
                content = this
            }
        }
        assertThrows<IllegalStateException> {
            content.map(Locale.ENGLISH, mapOf("test" to "test"))
        }
    }

}
