package com.kamelia.sprinkler.i18n.jar

import com.kamelia.sprinkler.i18n.Translator
import java.util.Locale
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

@Tag("jar-test")
class TranslatorBuilderTest {

    @Test
    fun `absolute resource file is correctly loaded from a jar`() {
        val t = Translator {
            translations {
                resource("/builder_test/en.yml")
            }
        }
        assertEquals("foo", t.t("test"))
    }

    @Test
    fun `relative resource file is correctly loaded from a jar`() {
        val t = Translator {
            translations {
                resource("en.yml")
            }
        }
        assertEquals("Hello!", t.t("greetings"))
    }

    @Test
    fun `folder resource file loads all files in the folder`() {
        val t = Translator {
            translations {
                resource("subfolder")
            }
        }
        val expect = mapOf(
            Locale.FRENCH to mapOf("my" to "mon"),
            Locale.ENGLISH to mapOf("my" to "my")
        )
        assertEquals(expect, t.toMap())
    }

    @Test
    fun `folder resource does not load files in subfolders`() {
        val t = Translator {
            translations {
                resource(".")
            }
        }
        val expect = mapOf(
            Locale.ENGLISH to mapOf("greetings" to "Hello!")
        )
        assertEquals(expect, t.toMap())
    }

    @Test
    fun `an IAE is thrown if the resource is not found`() {
        assertThrows<IllegalArgumentException> {
            Translator {
                translations {
                    resource("not_found")
                }
            }
        }
    }

    @Test
    fun `unrecognized file in folder is ignored if the ignoreUnrecognizedExtensionsInDirectory is set to true`() {
        assertDoesNotThrow {
            Translator {
                translations {
                    ignoreUnrecognizedExtensionsInDirectory = true
                    resource(".")
                }
            }
        }
    }

    @Test
    fun `unrecognized file in folder throws an IAE if the ignoreUnrecognizedExtensionsInDirectory is set to false`() {
        assertThrows<IllegalArgumentException> {
            Translator {
                translations {
                    ignoreUnrecognizedExtensionsInDirectory = false
                    resource(".")
                }
            }
        }
    }

    @Test
    fun `unrecognized explicit file always throws an IAE even if the ignoreUnrecognizedExtensionsInDirectory is set to true`() {
        assertThrows<IllegalArgumentException> {
            Translator {
                translations {
                    ignoreUnrecognizedExtensionsInDirectory = true
                    resource("ignored.ukn")
                }
            }
        }
    }

}
