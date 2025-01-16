package com.kamelia.sprinkler.i18n.impl

import com.kamelia.sprinkler.i18n.absoluteResource
import java.io.File
import java.util.Locale
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.io.path.Path


class TranslatorBuilderFileTest {

    @Test
    fun `addFile throws an IAE if the extension is invalid`() {
        assertThrows<IllegalArgumentException> {
            Translator {
                translations {
                    file(File("invalid-extension.txt"))
                }
            }
        }
    }

    @Test
    fun `addPath throws an IAE if the extension is invalid`() {
        assertThrows<IllegalArgumentException> {
            Translator {
                translations {
                    path(Path("invalid-extension.txt"))
                }
            }
        }
    }

    @Test
    fun `addURL throws an IAE if the extension is invalid`() {
        assertThrows<IllegalArgumentException> {
            Translator {
                translations {
                    url(absoluteResource(ROOT, "invalid-extension.txt").toUri().toURL())
                }
            }
        }
    }

    @Test
    fun `addURI throws an IAE if the extension is invalid`() {
        assertThrows<IllegalArgumentException> {
            Translator {
                translations {
                    uri(absoluteResource(ROOT, "invalid-extension.txt").toUri())
                }
            }
        }
    }

    @Test
    fun `addFile does not throw with json extension`() {
        assertDoesNotThrow {
            Translator {
                translations {
                    file(absoluteResource(ROOT, "fr.json").toFile())
                }
            }
        }
    }

    @Test
    fun `addPath does not throw with json extension`() {
        assertDoesNotThrow {
            Translator {
                translations {
                    path(absoluteResource(ROOT, "fr.json"))
                }
            }
        }
    }

    @Test
    fun `addURL does not throw with json extension`() {
        assertDoesNotThrow {
            Translator {
                translations {
                    url(absoluteResource(ROOT, "fr.json").toUri().toURL())
                }
            }
        }
    }

    @Test
    fun `addURI does not throw with json extension`() {
        assertDoesNotThrow {
            Translator {
                translations {
                    uri(absoluteResource(ROOT, "fr.json").toUri())
                }
            }
        }
    }

    @Test
    fun `addFile does not throw with yaml extension`() {
        assertDoesNotThrow {
            Translator {
                translations {
                    file(absoluteResource(ROOT, "en.yaml").toFile())
                }
            }
        }
    }

    @Test
    fun `addPath does not throw with yaml extension`() {
        assertDoesNotThrow {
            Translator {
                translations {
                    path(absoluteResource(ROOT, "en.yaml"))
                }
            }
        }
    }

    @Test
    fun `addURL does not throw with yaml extension`() {
        assertDoesNotThrow {
            Translator {
                translations {
                    url(absoluteResource(ROOT, "en.yaml").toUri().toURL())
                }
            }
        }
    }

    @Test
    fun `addURI does not throw with yaml extension`() {
        assertDoesNotThrow {
            Translator {
                translations {
                    uri(absoluteResource(ROOT, "en.yaml").toUri())
                }
            }
        }
    }

    @Test
    fun `addFile does not throw with yml extension`() {
        assertDoesNotThrow {
            Translator {
                translations {
                    file(absoluteResource(ROOT, "en.yml").toFile())
                }
            }
        }
    }

    @Test
    fun `addPath does not throw with yml extension`() {
        assertDoesNotThrow {
            Translator {
                translations {
                    path(absoluteResource(ROOT, "en.yml"))
                }
            }
        }
    }

    @Test
    fun `addURL does not throw with yml extension`() {
        assertDoesNotThrow {
            Translator {
                translations {
                    url(absoluteResource(ROOT, "en.yml").toUri().toURL())
                }
            }
        }
    }

    @Test
    fun `addURI does not throw with yml extension`() {
        assertDoesNotThrow {
            Translator {
                translations {
                    uri(absoluteResource(ROOT, "en.yml").toUri())
                }
            }
        }
    }

    @Test
    fun `addPath throws an ISE on build call if the file name is not a valid locale`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {
                    path(absoluteResource(ROOT, "invalid-locale&.json"))
                }
            }
        }
    }

    @Test
    fun `addFile throws an ISE on build call if the file name is not a valid locale`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {
                    file(absoluteResource(ROOT, "invalid-locale&.json").toFile())
                }
            }
        }
    }

    @Test
    fun `addPath throws an ISE on build if the parser returns a map containing invalid key`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {
                    path(absoluteResource(ROOT, INVALID_CONTENT))
                }
            }
        }
    }

    @Test
    fun `addFile throws an ISE on build if the parser returns a map containing invalid key`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {
                    file(absoluteResource(ROOT, INVALID_CONTENT).toFile())
                }
            }
        }
    }

    @Test
    fun `addPath throws an ISE on build if a value is null`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {
                    path(absoluteResource(ROOT, NULL_VALUE))
                }
            }
        }
    }

    @Test
    fun `addFile throws an ISE on build if a value is null`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {
                    file(absoluteResource(ROOT, NULL_VALUE).toFile())
                }
            }
        }
    }

    @Test
    fun `addPath throws an ISE on build if the map contains a map containing an invalid key`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {
                    path(absoluteResource(ROOT, INVALID_NESTED_KEY))
                }
            }
        }
    }

    @Test
    fun `addFile throws an ISE on build if the map contains a map containing an invalid key`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {
                    file(absoluteResource(ROOT, INVALID_NESTED_KEY).toFile())
                }
            }
        }
    }

    @Test
    fun `addPath throws an ISE on build if the map contains a map containing a null value`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {
                    path(absoluteResource(ROOT, NESTED_NULL_VALUE))
                }
            }
        }
    }

    @Test
    fun `addFile throws an ISE on build if the map contains a map containing a null value`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {
                    file(absoluteResource(ROOT, NESTED_NULL_VALUE).toFile())
                }
            }
        }
    }

    @Test
    fun `addPath works with list`() {
        val translator = Translator {
            translations {
                path(absoluteResource(ROOT, LIST))
            }
        }
        assertEquals("tesuto", translator.t("test.0", Locale.JAPANESE))
    }

    @Test
    fun `addFile works with list`() {
        val translator = Translator {
            translations {
                file(absoluteResource(ROOT, LIST).toFile())
            }
        }
        assertEquals("tesuto", translator.t("test.0", Locale.JAPANESE))
    }

    @Test
    fun `addPath works with nested value`() {
        val translator = Translator {
            translations {
                path(absoluteResource(ROOT, NESTED_VALUE))
            }
        }
        assertEquals("test", translator.t("test.test"))
    }

    @Test
    fun `addFile works with map`() {
        val translator = Translator {
            translations {
                file(absoluteResource(ROOT, NESTED_VALUE).toFile())
            }
        }
        assertEquals("test", translator.t("test.test"))
    }

    @Test
    fun `addFile works with folder`() {
        val translator = Translator {
            translations {
                file(absoluteResource(ROOT, GROUP_FOLDER).toFile())
            }
        }
        assertEquals("this is a test", translator.t("test.test"))
    }

    @Test
    fun `addPath works with folder`() {
        val translator = Translator {
            translations {
                path(absoluteResource(ROOT, GROUP_FOLDER))
            }
        }
        assertEquals("this is a test", translator.t("test.test"))
    }

    @Test
    fun `addFile throws an IAE if the file does not exist`() {
        assertThrows<IllegalArgumentException> {
            Translator {
                translations {
                    file(File("does-not-exist.json"))
                }
            }
        }
    }

    @Test
    fun `addPath throws an IAE if the file does not exist`() {
        assertThrows<IllegalArgumentException> {
            Translator {
                translations {
                    path(Path("does-not-exist.json"))
                }
            }
        }
    }

    @Test
    fun `throws an exception if the content of the file of a json file is not a valid json`() {
        assertThrows<IllegalArgumentException> {
            Translator {
                translations {
                    path(absoluteResource(ROOT, INVALID_JSON))
                }
            }
        }
    }

    @Test
    fun `throws an exception if the content of the file of a yaml file is not a valid yaml`() {
        assertThrows<IllegalArgumentException> {
            Translator {
                translations {
                    path(absoluteResource(ROOT, INVALID_YAML))
                }
            }
        }
    }

    @Test
    fun `addFile with the same file more than once throws an ISE`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {
                    file(absoluteResource(ROOT, LIST).toFile())
                    file(absoluteResource(ROOT, LIST).toFile())
                }
            }
        }
    }

    @Test
    fun `addURL with the same file more than once throws an ISE`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {
                    url(absoluteResource(ROOT, LIST).toUri().toURL())
                    url(absoluteResource(ROOT, LIST).toUri().toURL())
                }
            }
        }
    }

    @Test
    fun `addURI with the same file more than once throws an ISE`() {
        assertThrows<IllegalStateException> {
            Translator {
                translations {
                    uri(absoluteResource(ROOT, LIST).toUri())
                    uri(absoluteResource(ROOT, LIST).toUri())
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
            content.file(File("."))
        }
    }

    @Test
    fun `unrecognized file in folder is ignored if the ignoreUnrecognizedExtensionsInDirectory is set to true`() {
        assertDoesNotThrow {
            Translator {
                translations {
                    ignoreUnrecognizedExtensionsInDirectory = true
                    path(absoluteResource(ROOT, INVALID))
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
                    path(absoluteResource(ROOT, INVALID))
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
                    path(absoluteResource(ROOT, "invalid-extension.txt"))
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
