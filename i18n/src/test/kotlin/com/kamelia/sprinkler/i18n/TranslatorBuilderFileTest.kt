package com.kamelia.sprinkler.i18n

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
    fun `addURL throws an IAE if the extension is invalid`() {
        assertThrows<IllegalArgumentException> {
            Translator.builder(Locale.ENGLISH)
                .addURL(absoluteResource(ROOT, "invalid-extension.txt").toUri().toURL())
        }
    }

    @Test
    fun `addURI throws an IAE if the extension is invalid`() {
        assertThrows<IllegalArgumentException> {
            Translator.builder(Locale.ENGLISH)
                .addURI(absoluteResource(ROOT, "invalid-extension.txt").toUri())
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
    fun `addURL does not throw with json extension`() {
        assertDoesNotThrow {
            Translator.builder(Locale.ENGLISH)
                .addURL(Path("valid-extension.json").toUri().toURL())
        }
    }

    @Test
    fun `addURI does not throw with json extension`() {
        assertDoesNotThrow {
            Translator.builder(Locale.ENGLISH)
                .addURI(Path( "valid-extension.json").toUri())
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
    fun `addURL does not throw with yaml extension`() {
        assertDoesNotThrow {
            Translator.builder(Locale.ENGLISH)
                .addURL(Path("valid-extension.yaml").toUri().toURL())
        }
    }

    @Test
    fun `addURI does not throw with yaml extension`() {
        assertDoesNotThrow {
            Translator.builder(Locale.ENGLISH)
                .addURI(Path("valid-extension.yaml").toUri())
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
    fun `addURL does not throw with yml extension`() {
        assertDoesNotThrow {
            Translator.builder(Locale.ENGLISH)
                .addURL(Path("valid-extension.yml").toUri().toURL())
        }
    }

    @Test
    fun `addURI does not throw with yml extension`() {
        assertDoesNotThrow {
            Translator.builder(Locale.ENGLISH)
                .addURI(Path("valid-extension.yml").toUri())
        }
    }

    @Test
    fun `addPath throws an ISE on build call if the file name is not a valid locale`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addFile(absoluteResource(ROOT, "invalid-locale&.json"))
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addFile throws an ISE on build call if the file name is not a valid locale`() {
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

    @Test
    fun `addFile works with folder`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addFile(absoluteResource(ROOT, GROUP_FOLDER).toFile())
        val translator = builder.build()
        assertEquals("this is a test", translator.t("test.test"))
    }

    @Test
    fun `addPath works with folder`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addFile(absoluteResource(ROOT, GROUP_FOLDER))
        val translator = builder.build()
        assertEquals("this is a test", translator.t("test.test"))
    }

    @Test
    fun `addFile throws an ISE if the file does not exist`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addFile(File("does-not-exist.json"))
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addPath throws an ISE if the file does not exist`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addFile(Path("does-not-exist.json"))
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `throws an exception if the content of the file of a json file is not a valid json`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addFile(absoluteResource(ROOT, INVALID_JSON))
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `throws an exception if the content of the file of a yaml file is not a valid yaml`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addFile(absoluteResource(ROOT, INVALID_YAML))
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addFile with the same file more than once does not add the file more than once`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addFile(absoluteResource(ROOT, LIST).toFile())
            .addFile(absoluteResource(ROOT, LIST).toFile())
            // to ensure that it should fail in case of the file is added more than once
            .withDuplicatedKeyResolutionPolicy(TranslatorBuilder.DuplicatedKeyResolution.FAIL)
        assertDoesNotThrow {
            builder.build()
        }
    }

    @Test
    fun `addURL with the same file more than once does not add the file more than once`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addFile(absoluteResource(ROOT, LIST))
            .addFile(absoluteResource(ROOT, LIST))
            // to ensure that it should fail in case of the file is added more than once
            .withDuplicatedKeyResolutionPolicy(TranslatorBuilder.DuplicatedKeyResolution.FAIL)
        assertDoesNotThrow {
            builder.build()
        }
    }

    @Test
    fun `addURI with the same file more than once does not add the file more than once`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addURI(absoluteResource(ROOT, LIST).toUri())
            .addURI(absoluteResource(ROOT, LIST).toUri())
            .withDuplicatedKeyResolutionPolicy(TranslatorBuilder.DuplicatedKeyResolution.FAIL)
        assertDoesNotThrow {
            builder.build()
        }
    }

    private companion object {

        const val ROOT = "builder_test"

        const val GROUP_FOLDER = "group"

        const val INVALID_CONTENT = "fr_FR.json"

        const val NULL_VALUE = "fr_FR.yml"

        const val INVALID_NESTED_KEY = "fr_FR.json"

        const val NESTED_NULL_VALUE = "fr.json"

        const val INVALID_JSON = "invalid.json"

        const val INVALID_YAML = "invalid.yaml"

        const val LIST = "en.yml"

        const val NESTED_VALUE = "en.yaml"

    }

}
