package com.kamelia.sprinkler.i18n

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.nio.file.Path
import java.util.*

class TranslatorBuilderTest {

    @Test
    fun `adding several time the same path throws an IAE`() {
        val builder = TranslatorBuilder(Locale.ENGLISH)
        val path = Path.of("test", "test")
        builder.addPath(path) { emptyMap() }
        assertThrows<IllegalArgumentException> {
            builder.addPath(path) { emptyMap() }
        }
    }


    @Test
    fun `addPath LocaleMapper determines the locale of a file`() {
        val path = absoluteResource(ROOT, "empty.txt")
        val key = "test"
        val value = "this is a test"
        val locale = Locale.FRANCE

        val translator = TranslatorBuilder(Locale.ENGLISH)
            .addPath(path, { locale }) { mapOf(key to value) }
            .build()
        assertEquals(value, translator.translate(key, locale))
    }

    @Test
    fun `adding several time the same file throws an IAE`() {
        val builder = TranslatorBuilder(Locale.ENGLISH)
        val path = Path.of("test", "test").toFile()
        builder.addFile(path) { emptyMap() }
        assertThrows<IllegalArgumentException> {
            builder.addFile(path) { emptyMap() }
        }
    }

    @Test
    fun `addFile LocaleMapper determines the locale of a file`() {
        val path = absoluteResource(ROOT, "empty.txt").toFile()
        val key = "test"
        val value = "this is a test"
        val locale = Locale.FRANCE

        val translator = TranslatorBuilder(Locale.ENGLISH)
            .addFile(path, { locale }) { mapOf(key to value) }
            .build()
        assertEquals(value, translator.translate(key, locale))
    }

    @Test
    fun `addMap adds the map to the translator`() {
        val key = "test"
        val value = "this is a test"
        val locale = Locale.FRANCE

        val translator = TranslatorBuilder(Locale.ENGLISH)
            .addMap(locale, mapOf(key to value))
            .build()
        assertEquals(value, translator.translate(key, locale))
    }

    @Test
    fun `addMaps adds the maps to the translator`() {
        val key = "test"
        val value = "this is a test"
        val locale = Locale.FRANCE

        val translator = TranslatorBuilder(Locale.ENGLISH)
            .addMaps(mapOf(locale to mapOf(key to value)))
            .build()
        assertEquals(value, translator.translate(key, locale))
    }

    @Test
    fun `addTranslator adds the translator to the translator`() {
        val key = "test"
        val value = "this is a test"
        val locale = Locale.FRANCE
        val source = TranslatorBuilder(Locale.ENGLISH)
            .addMap(locale, mapOf(key to value))
            .build()

        val translator = TranslatorBuilder(Locale.ENGLISH)
            .addTranslator(source)
            .build()
        assertEquals(value, translator.translate(key, locale))
    }

    private companion object {

        const val ROOT = "builder_test"

    }

}
