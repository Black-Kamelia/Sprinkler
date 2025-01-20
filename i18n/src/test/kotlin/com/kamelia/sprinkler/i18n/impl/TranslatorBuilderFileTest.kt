package com.kamelia.sprinkler.i18n.impl

import com.kamelia.sprinkler.i18n.absoluteResource
import java.util.Locale
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.io.path.Path


class TranslatorBuilderFileTest {

    @Test
    fun `file throws an IAE if the extension is invalid`() {
        assertThrows<IllegalArgumentException> {
            Translator {
                translations {
                    file(Path("invalid-extension.txt"))
                }
            }
        }
    }

    @Test
    fun `file does not throw with json extension`() {
        assertDoesNotThrow {
            Translator {
                translations {
                    file(absoluteResource(ROOT, "fr.json"))
                }
            }
        }
    }

    @Test
    fun `file does not throw with yaml extension`() {
        assertDoesNotThrow {
            Translator {
                translations {
                    file(absoluteResource(ROOT, "en.yaml"))
                }
            }
        }
    }

    @Test
    fun `file does not throw with yml extension`() {
        assertDoesNotThrow {
            Translator {
                translations {
                    file(absoluteResource(ROOT, "en.yml"))
                }
            }
        }
    }

    @Test
    fun `file throws an ISE on build call if the file name is not a valid locale`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {
                    file(absoluteResource(ROOT, "invalid-locale&.json"))
                }
            }
        }
    }

    @Test
    fun `file throws an ISE on build if the parser returns a map containing invalid key`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {
                    file(absoluteResource(ROOT, INVALID_CONTENT))
                }
            }
        }
    }

    @Test
    fun `file throws an ISE on build if a value is null`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {
                    file(absoluteResource(ROOT, NULL_VALUE))
                }
            }
        }
    }

    @Test
    fun `file throws an ISE on build if the map contains a map containing an invalid key`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {
                    file(absoluteResource(ROOT, INVALID_NESTED_KEY))
                }
            }
        }
    }

    @Test
    fun `file throws an ISE on build if the map contains a map containing a null value`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {
                    file(absoluteResource(ROOT, NESTED_NULL_VALUE))
                }
            }
        }
    }

    @Test
    fun `file works with list`() {
        val translator = Translator {
            translations {
                file(absoluteResource(ROOT, LIST))
            }
        }
        assertEquals("tesuto", translator.t("test.0", Locale.JAPANESE))
    }

    @Test
    fun `file works with nested value`() {
        val translator = Translator {
            translations {
                file(absoluteResource(ROOT, NESTED_VALUE))
            }
        }
        assertEquals("test", translator.t("test.test"))
    }

    @Test
    fun `file works with folder`() {
        val translator = Translator {
            translations {
                file(absoluteResource(ROOT, GROUP_FOLDER))
            }
        }
        assertEquals("this is a test", translator.t("test.test"))
    }

    @Test
    fun `file throws an IAE if the file does not exist`() {
        assertThrows<IllegalArgumentException> {
            Translator {
                translations {
                    file(Path("does-not-exist.json"))
                }
            }
        }
    }

    @Test
    fun `throws an exception if the content of the file of a json file is not a valid json`() {
        assertThrows<IllegalArgumentException> {
            Translator {
                translations {
                    file(absoluteResource(ROOT, INVALID_JSON))
                }
            }
        }
    }

    @Test
    fun `throws an exception if the content of the file of a yaml file is not a valid yaml`() {
        assertThrows<IllegalArgumentException> {
            Translator {
                translations {
                    file(absoluteResource(ROOT, INVALID_YAML))
                }
            }
        }
    }

    @Test
    fun `file with the same file more than once throws an ISE`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {
                    file(absoluteResource(ROOT, LIST))
                    file(absoluteResource(ROOT, LIST))
                }
            }
        }
    }

    @Test
    fun `trying to add files after the translator has been built throws an ISE`() {
        lateinit var content: TranslatorBuilder.Content
        Translator {
            translations {
                content = this
            }
        }
        assertThrows<IllegalStateException> {
            content.file(Path("."))
        }
    }

    @Test
    fun `unrecognized file in folder is ignored if the ignoreUnrecognizedExtensionsInDirectory is set to true`() {
        assertDoesNotThrow {
            Translator {
                translations {
                    ignoreUnrecognizedExtensionsInDirectory = true
                    file(absoluteResource(ROOT, INVALID))
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
                    file(absoluteResource(ROOT, INVALID))
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
                    file(absoluteResource(ROOT, "invalid-extension.txt"))
                }
            }
        }
    }

    private companion object {

        const val ROOT = "builder_test"

        const val GROUP_FOLDER = "group"

        const val INVALID_CONTENT = "fr_FR.json"

        const val NULL_VALUE = "fr_FR.yml"

        const val INVALID_NESTED_KEY = "fr_FR.json"

        const val NESTED_NULL_VALUE = "nlv.json"

        const val INVALID_JSON = "invalid.json"

        const val INVALID_YAML = "invalid.yaml"

        const val LIST = "ja.yml"

        const val NESTED_VALUE = "en.yaml"

        const val INVALID = "invalid"

    }

}
