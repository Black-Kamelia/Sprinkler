package com.kamelia.sprinkler.i18n

import java.io.File
import java.util.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.io.path.Path


class TranslatorBuilderFileTest {

    @Test
    fun `addFile throws an IAE if the extension is invalid`() {
        assertThrows<IllegalArgumentException> {
            Translator.builder(Locale.ENGLISH)
                .addFile(File("invalid-extension.txt"))
        }
    }

    @Test
    fun `addPath throws an IAE if the extension is invalid`() {
        assertThrows<IllegalArgumentException> {
            Translator.builder(Locale.ENGLISH)
                .addFile(Path("invalid-extension.txt"))
        }
    }

    @Test
    fun `addFile does not throw with json extension`() {
        assertDoesNotThrow {
            Translator.builder(Locale.ENGLISH)
                .addFile(File("valid-extension.json"))
        }
    }

    @Test
    fun `addPath does not throw with json extension`() {
        assertDoesNotThrow {
            Translator.builder(Locale.ENGLISH)
                .addFile(Path("valid-extension.json"))
        }
    }

    @Test
    fun `addFile does not throw with yaml extension`() {
        assertDoesNotThrow {
            Translator.builder(Locale.ENGLISH)
                .addFile(File("valid-extension.yaml"))
        }
    }

    @Test
    fun `addPath does not throw with yaml extension`() {
        assertDoesNotThrow {
            Translator.builder(Locale.ENGLISH)
                .addFile(Path("valid-extension.yaml"))
        }
    }

    @Test
    fun `addFile does not throw with yml extension`() {
        assertDoesNotThrow {
            Translator.builder(Locale.ENGLISH)
                .addFile(File("valid-extension.yml"))
        }
    }

    @Test
    fun `addPath does not throw with yml extension`() {
        assertDoesNotThrow {
            Translator.builder(Locale.ENGLISH)
                .addFile(Path("valid-extension.yml"))
        }
    }

    @Test
    fun `addPath throws an ISE on build call if the default localeParser is used and the file name is not a valid locale`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addFile(absoluteResource(ROOT, "invalid-locale&.json"))
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addFile throws an ISE on build call if the default localeParser is used and the file name is not a valid locale`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addFile(absoluteResource(ROOT, "invalid-locale&.json").toFile())
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addPath throws an ISE on build if the parser returns a map containing invalid key`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addFile(absoluteResource(ROOT, INVALID_CONTENT))
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addFile throws an ISE on build if the parser returns a map containing invalid key`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addFile(absoluteResource(ROOT, INVALID_CONTENT).toFile())
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addPath throws an ISE on build if a value is null`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addFile(absoluteResource(ROOT, NULL_VALUE))
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addFile throws an ISE on build if a value is null`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addFile(absoluteResource(ROOT, NULL_VALUE).toFile())
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addPath throws an ISE on build if the map contains a map containing an invalid key`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addFile(absoluteResource(ROOT, INVALID_NESTED_KEY))
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addFile throws an ISE on build if the map contains a map containing an invalid key`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addFile(absoluteResource(ROOT, "fr_FR.json").toFile())
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addPath throws an ISE on build if the map contains a map containing a null value`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addFile(absoluteResource(ROOT, NESTED_NULL_VALUE))
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addFile throws an ISE on build if the map contains a map containing a null value`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addFile(absoluteResource(ROOT, NESTED_NULL_VALUE).toFile())
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addPath works with list`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addFile(absoluteResource(ROOT, LIST))
        val translator = builder.build()
        assertEquals("test", translator.t("test.0"))
    }

    @Test
    fun `addFile works with list`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addFile(absoluteResource(ROOT, LIST).toFile())
        val translator = builder.build()
        assertEquals("test", translator.t("test.0"))
    }

    @Test
    fun `addPath works with nested value`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addFile(absoluteResource(ROOT, NESTED_VALUE))
        val translator = builder.build()
        assertEquals("test", translator.t("test.test"))
    }

    @Test
    fun `addFile works with map`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addFile(absoluteResource(ROOT, NESTED_VALUE).toFile())
        val translator = builder.build()
        assertEquals("test", translator.t("test.test"))
    }

    private companion object {

        const val ROOT = "builder_test"

        const val INVALID_CONTENT = "fr_FR.json"

        const val NULL_VALUE = "fr_FR.yml"

        const val INVALID_NESTED_KEY = "fr_FR.json"

        const val NESTED_NULL_VALUE = "fr.json"

        const val LIST = "en.yml"

        const val NESTED_VALUE = "en.yaml"

    }

}
