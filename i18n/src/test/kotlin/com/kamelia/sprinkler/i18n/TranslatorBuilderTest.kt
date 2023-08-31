package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.util.unsafeCast
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.nio.file.Path
import java.util.*

class TranslatorBuilderTest {

    @Test
    fun `defaultLocale is the locale specifier in the constructor`() {
        val locale = Locale.FRANCE
        val translator = Translator.builder(locale).build()
        assertEquals(locale, translator.defaultLocale)
    }

    @Test
    fun `currentLocale is set to the default locale by default`() {
        val locale = Locale.FRANCE
        val translator = Translator.builder(locale).build()
        assertEquals(locale, translator.currentLocale)
    }

    @Test
    fun `adding several time the same path throws an IAE`() {
        val builder = Translator.builder(Locale.ENGLISH)
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

        val translator = Translator.builder(Locale.ENGLISH)
            .addPath(path, { locale }) { mapOf(key to value) }
            .build()
        assertEquals(value, translator.translate(key, locale))
    }

    @Test
    fun `adding several time the same file throws an IAE`() {
        val builder = Translator.builder(Locale.ENGLISH)
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

        val translator = Translator.builder(Locale.ENGLISH)
            .addFile(path, { locale }) { mapOf(key to value) }
            .build()
        assertEquals(value, translator.translate(key, locale))
    }

    @Test
    fun `addMap adds the map to the translator`() {
        val key = "test"
        val value = "this is a test"
        val locale = Locale.FRANCE

        val translator = Translator.builder(Locale.ENGLISH)
            .addMap(locale, mapOf(key to value))
            .build()
        assertEquals(value, translator.translate(key, locale))
    }

    @Test
    fun `addMaps adds the maps to the translator`() {
        val key = "test"
        val value = "this is a test"
        val locale = Locale.FRANCE

        val translator = Translator.builder(Locale.ENGLISH)
            .addMaps(mapOf(locale to mapOf(key to value)))
            .build()
        assertEquals(value, translator.translate(key, locale))
    }

    @Test
    fun `addTranslator adds the translator to the translator`() {
        val key = "test"
        val value = "this is a test"
        val locale = Locale.FRANCE
        val source = Translator.builder(Locale.ENGLISH)
            .addMap(locale, mapOf(key to value))
            .build()

        val translator = Translator.builder(Locale.ENGLISH)
            .addTranslator(source)
            .build()
        assertEquals(value, translator.translate(key, locale))
    }

    @Test
    fun `withDefaultLocale changes the final default locale`() {
        val translator = Translator.builder(Locale.ENGLISH)
            .withDefaultLocale(Locale.FRANCE)
            .build()
        assertEquals(Locale.FRANCE, translator.defaultLocale)
    }

    @Test
    fun `withCurrentLocale changes the final current locale`() {
        val translator = Translator.builder(Locale.ENGLISH)
            .withCurrentLocale(Locale.FRANCE)
            .build()
        assertEquals(Locale.FRANCE, translator.currentLocale)
    }

    @Test
    fun `addPath throws an ISE on build call if the default localeParser is used and the file name is not a valid locale`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addPath(absoluteResource("builder_test", "invalid-locale&.txt")) { emptyMap() }
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addFile throws an ISE on build call if the default localeParser is used and the file name is not a valid locale`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addFile(absoluteResource("builder_test", "invalid-locale&.txt").toFile()) { emptyMap() }
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addPath throws an ISE on build if the parser returns a map containing invalid key`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addPath(absoluteResource("builder_test", "empty.txt")) { mapOf("invalid#" to 5) }
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addFile throws an ISE on build if the parser returns a map containing invalid key`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addFile(absoluteResource("builder_test", "empty.txt").toFile()) { mapOf("invalid#" to 5) }
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addMap throws an ISE on build if the map contains invalid key`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addMap(Locale.FRANCE, mapOf("invalid#" to 5))
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addMaps throws an ISE on build if one of the maps contains invalid key`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addMaps(mapOf(Locale.FRANCE to mapOf("invalid#" to 5)))
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addTranslator throws an ISE on build if the translator contains invalid key`() {
        val customImpl = object : Translator by Translator.builder(Locale.ENGLISH).build() {
            override fun toMap(): Map<Locale, Map<String, String>> = mapOf(Locale.FRANCE to mapOf("invalid#" to "5"))
        }
        val builder = Translator.builder(Locale.ENGLISH)
            .addTranslator(customImpl)
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addPath throws an ISE on build if a key is null`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addPath(absoluteResource("builder_test", "empty.txt")) { mapOf(null to 5).unsafeCast() }
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addFile throws an ISE on build if a key is null`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addFile(absoluteResource("builder_test", "empty.txt").toFile()) { mapOf(null to 5).unsafeCast() }
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addMap throws an ISE on build if a key is null`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addMap(Locale.FRANCE, mapOf(null to 5).unsafeCast())
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addMaps throws an ISE on build if a key is null`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addMaps(mapOf(Locale.FRANCE to mapOf(null to 5).unsafeCast()))
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addTranslator throws an ISE on build if a key is null`() {
        val customImpl = object : Translator by Translator.builder(Locale.ENGLISH).build() {
            override fun toMap(): Map<Locale, Map<String, String>> =
                mapOf(Locale.FRANCE to mapOf(null to "5").unsafeCast())
        }
        val builder = Translator.builder(Locale.ENGLISH)
            .addTranslator(customImpl)
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addPath throws an ISE on build if a value is null`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addPath(absoluteResource("builder_test", "empty.txt")) { mapOf("test" to null).unsafeCast() }
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addFile throws an ISE on build if a value is null`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addFile(absoluteResource("builder_test", "empty.txt").toFile()) { mapOf("test" to null).unsafeCast() }
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addMap throws an ISE on build if a value is null`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addMap(Locale.FRANCE, mapOf("test" to null).unsafeCast())
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addMaps throws an ISE on build if a value is null`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addMaps(mapOf(Locale.FRANCE to mapOf("test" to null).unsafeCast()))
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addTranslator throws an ISE on build if a value is null`() {
        val customImpl = object : Translator by Translator.builder(Locale.ENGLISH).build() {
            override fun toMap(): Map<Locale, Map<String, String>> =
                mapOf(Locale.FRANCE to mapOf("test" to null).unsafeCast())
        }
        val builder = Translator.builder(Locale.ENGLISH)
            .addTranslator(customImpl)
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addPath throws an ISE on build if the map contains a map containing an invalid key`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addPath(absoluteResource("builder_test", "empty.txt")) { mapOf("test" to mapOf("invalid#" to 5)) }
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addFile throws an ISE on build if the map contains a map containing an invalid key`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addFile(absoluteResource("builder_test", "empty.txt").toFile()) { mapOf("test" to mapOf("invalid#" to 5)) }
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addMap throws an ISE on build if the map contains a map containing an invalid key`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addMap(Locale.FRANCE, mapOf("test" to mapOf("invalid#" to 5)))
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addMaps throws an ISE on build if the map contains a map containing an invalid key`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addMaps(mapOf(Locale.FRANCE to mapOf("test" to mapOf("invalid#" to 5))))
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addTranslator throws an ISE on build if the map contains a map containing an invalid key`() {
        val customImpl = object : Translator by Translator.builder(Locale.ENGLISH).build() {
            override fun toMap(): Map<Locale, Map<String, String>> =
                mapOf(Locale.FRANCE to mapOf("test" to mapOf("invalid#" to "5"))).unsafeCast()
        }
        val builder = Translator.builder(Locale.ENGLISH)
            .addTranslator(customImpl)
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addPath throws an ISE on build if the map contains a map containing a null key`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addPath(absoluteResource("builder_test", "empty.txt")) { mapOf("test" to mapOf(null to 5)) }
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addFile throws an ISE on build if the map contains a map containing a null key`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addFile(absoluteResource("builder_test", "empty.txt").toFile()) { mapOf("test" to mapOf(null to 5)) }
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addMap throws an ISE on build if the map contains a map containing a null key`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addMap(Locale.FRANCE, mapOf("test" to mapOf(null to 5)).unsafeCast())
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addMaps throws an ISE on build if the map contains a map containing a null key`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addMaps(mapOf(Locale.FRANCE to mapOf("test" to mapOf(null to 5)).unsafeCast()))
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addTranslator throws an ISE on build if the map contains a map containing a null key`() {
        val customImpl = object : Translator by Translator.builder(Locale.ENGLISH).build() {
            override fun toMap(): Map<Locale, Map<String, String>> =
                mapOf(Locale.FRANCE to mapOf("test" to mapOf(null to "5")).unsafeCast())
        }
        val builder = Translator.builder(Locale.ENGLISH)
            .addTranslator(customImpl)
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addPath throws an ISE on build if the map contains a map containing a null value`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addPath(absoluteResource("builder_test", "empty.txt")) { mapOf("test" to mapOf("test" to null)) }
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addFile throws an ISE on build if the map contains a map containing a null value`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addFile(absoluteResource("builder_test", "empty.txt").toFile()) { mapOf("test" to mapOf("test" to null)) }
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addMap throws an ISE on build if the map contains a map containing a null value`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addMap(Locale.FRANCE, mapOf("test" to mapOf("test" to null)))
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addMaps throws an ISE on build if the map contains a map containing a null value`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addMaps(mapOf(Locale.FRANCE to mapOf("test" to mapOf("test" to null))))
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addTranslator throws an ISE on build if the map contains a map containing a null value`() {
        val customImpl = object : Translator by Translator.builder(Locale.ENGLISH).build() {
            override fun toMap(): Map<Locale, Map<String, String>> =
                mapOf(Locale.FRANCE to mapOf("test" to mapOf("test" to null)).unsafeCast())
        }
        val builder = Translator.builder(Locale.ENGLISH)
            .addTranslator(customImpl)
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addPath throws an ISE on build if the map contains a list containing an invalid value`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addPath(absoluteResource("builder_test", "empty.txt")) { mapOf("test" to listOf(Any())) }
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addFile throws an ISE on build if the map contains a list containing an invalid value`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addFile(absoluteResource("builder_test", "empty.txt").toFile()) { mapOf("test" to listOf(Any())) }
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addMap throws an ISE on build if the map contains a list containing an invalid value`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addMap(Locale.FRANCE, mapOf("test" to listOf(Any())))
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addMaps throws an ISE on build if the map contains a list containing an invalid value`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addMaps(mapOf(Locale.FRANCE to mapOf("test" to listOf(Any()))))
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addTranslator throws an ISE on build if the map contains a list containing an invalid value`() {
        val customImpl = object : Translator by Translator.builder(Locale.ENGLISH).build() {
            override fun toMap(): Map<Locale, Map<String, String>> =
                mapOf(Locale.FRANCE to mapOf("test" to listOf(Any())).unsafeCast())
        }
        val builder = Translator.builder(Locale.ENGLISH)
            .addTranslator(customImpl)
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addPath throws an ISE on build if the map contains a map containing a key that is not a string`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addPath(absoluteResource("builder_test", "empty.txt")) { mapOf("test" to mapOf(5 to "test")) }
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addFile throws an ISE on build if the map contains a map containing a key that is not a string`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addFile(absoluteResource("builder_test", "empty.txt").toFile()) { mapOf("test" to mapOf(5 to "test")) }
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addMap throws an ISE on build if the map contains a map containing a key that is not a string`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addMap(Locale.FRANCE, mapOf("test" to mapOf(5 to "test")))
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addMaps throws an ISE on build if the map contains a map containing a key that is not a string`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addMaps(mapOf(Locale.FRANCE to mapOf("test" to mapOf(5 to "test"))))
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addTranslator throws an ISE on build if the map contains a map containing a key that is not a string`() {
        val customImpl = object : Translator by Translator.builder(Locale.ENGLISH).build() {
            override fun toMap(): Map<Locale, Map<String, String>> =
                mapOf(Locale.FRANCE to mapOf("test" to mapOf(5 to "test")).unsafeCast())
        }
        val builder = Translator.builder(Locale.ENGLISH)
            .addTranslator(customImpl)
        assertThrows<IllegalStateException> {
            builder.build()
        }
    }

    @Test
    fun `addPath works with list`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addPath(
                absoluteResource("builder_test", "empty.txt"),
                { Locale.ENGLISH }) { mapOf("test" to listOf("test")) }
        val translator = builder.build()
        assertEquals("test", translator.translate("test.0"))
    }

    @Test
    fun `addFile works with list`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addFile(
                absoluteResource("builder_test", "empty.txt").toFile(),
                { Locale.ENGLISH }) { mapOf("test" to listOf("test")) }
        val translator = builder.build()
        assertEquals("test", translator.translate("test.0"))
    }

    @Test
    fun `addMap works with list`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addMap(Locale.ENGLISH, mapOf("test" to listOf("test")))
        val translator = builder.build()
        assertEquals("test", translator.translate("test.0"))
    }

    @Test
    fun `addMaps works with list`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addMaps(mapOf(Locale.ENGLISH to mapOf("test" to listOf("test"))))
        val translator = builder.build()
        assertEquals("test", translator.translate("test.0"))
    }

    @Test
    fun `addTranslator works with list`() {
        val customImpl = object : Translator by Translator.builder(Locale.ENGLISH).build() {
            override fun toMap(): Map<Locale, Map<String, String>> =
                mapOf(Locale.ENGLISH to mapOf("test" to listOf("test")).unsafeCast())
        }
        val builder = Translator.builder(Locale.ENGLISH)
            .addTranslator(customImpl)
        val translator = builder.build()
        assertEquals("test", translator.translate("test.0"))
    }

    @Test
    fun `addPath works with map`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addPath(
                absoluteResource("builder_test", "empty.txt"),
                { Locale.ENGLISH }) { mapOf("test" to mapOf("test" to "test")) }
        val translator = builder.build()
        assertEquals("test", translator.translate("test.test"))
    }

    @Test
    fun `addFile works with map`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addFile(
                absoluteResource("builder_test", "empty.txt").toFile(),
                { Locale.ENGLISH }) { mapOf("test" to mapOf("test" to "test")) }
        val translator = builder.build()
        assertEquals("test", translator.translate("test.test"))
    }

    @Test
    fun `addMap works with map`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addMap(Locale.ENGLISH, mapOf("test" to mapOf("test" to "test")))
        val translator = builder.build()
        assertEquals("test", translator.translate("test.test"))
    }

    @Test
    fun `addMaps works with map`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addMaps(mapOf(Locale.ENGLISH to mapOf("test" to mapOf("test" to "test"))))
        val translator = builder.build()
        assertEquals("test", translator.translate("test.test"))
    }

    @Test
    fun `addTranslator works with map`() {
        val customImpl = object : Translator by Translator.builder(Locale.ENGLISH).build() {
            override fun toMap(): Map<Locale, Map<String, String>> =
                mapOf(Locale.ENGLISH to mapOf("test" to mapOf("test" to "test")).unsafeCast())
        }
        val builder = Translator.builder(Locale.ENGLISH)
            .addTranslator(customImpl)
        val translator = builder.build()
        assertEquals("test", translator.translate("test.test"))
    }

    @Test
    fun `addPath does not throw for valid types`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addPath(
                absoluteResource("builder_test", "empty.txt"),
                { Locale.ENGLISH }) { mapOf(
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
            ) }
        assertDoesNotThrow {
            builder.build()
        }
    }

    @Test
    fun `addFile does not throw for valid types`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addFile(
                absoluteResource("builder_test", "empty.txt").toFile(),
                { Locale.ENGLISH }) { mapOf(
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
            ) }
        assertDoesNotThrow {
            builder.build()
        }
    }

    @Test
    fun `addMap does not throw for valid types`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addMap(Locale.ENGLISH, mapOf(
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
            ))
        assertDoesNotThrow {
            builder.build()
        }
    }

    @Test
    fun `addMaps does not throw for valid types`() {
        val builder = Translator.builder(Locale.ENGLISH)
            .addMaps(mapOf(Locale.ENGLISH to mapOf(
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
            )))
        assertDoesNotThrow {
            builder.build()
        }
    }

    @Test
    fun `addTranslator does not throw for valid types`() {
        val customImpl = object : Translator by Translator.builder(Locale.ENGLISH).build() {
            override fun toMap(): Map<Locale, Map<String, String>> =
                mapOf(Locale.ENGLISH to mapOf(
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
                ).unsafeCast())
        }
        val builder = Translator.builder(Locale.ENGLISH)
            .addTranslator(customImpl)
        assertDoesNotThrow {
            builder.build()
        }
    }


    private companion object {

        const val ROOT = "builder_test"

    }

}
