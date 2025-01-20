package com.kamelia.sprinkler.i18n.impl

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class BuiltinContentParsersTest {

    @Test
    fun `jsonLoaders all return the same map`() {
        val loaders = BuiltinContentParsers.jsonParsers()
        val content = """{"key1": "value1", "key2": "value2"}"""
        val expected = mapOf("key1" to "value1", "key2" to "value2")
        loaders.forEach { loader ->
            val actual = loader.parse(content)
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `yamlLoaders all return the same map`() {
        val loaders = BuiltinContentParsers.yamlParsers()
        val content = """
            key1: value1
            key2: value2
        """.trimIndent()
        val expected = mapOf("key1" to "value1", "key2" to "value2")
        loaders.forEach { loader ->
            val actual = loader.parse(content)
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `tomlLoaders all return the same map`() {
        val loaders = BuiltinContentParsers.tomlParsers()
        val content = """
            key1 = "value1"
            key2 = "value2"
        """.trimIndent()
        val expected = mapOf("key1" to "value1", "key2" to "value2")
        loaders.forEach { loader ->
            val actual = loader.parse(content)
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `compositeLoader correctly stops at the first valid loader encountered`() {
        val notPresent: () -> TranslatorBuilder.ContentParser = { throw NoClassDefFoundError() }
        val valid = { BuiltinContentParsers.jsonParsers().first() }
        val shouldNotReach: () -> TranslatorBuilder.ContentParser = { throw AssertionError() }

        val loader = BuiltinContentParsers.compositeParser(listOf(notPresent, valid, shouldNotReach), "json")

        assertDoesNotThrow {
            val content = """{"key1": "value1", "key2": "value2"}"""
            val expected = mapOf("key1" to "value1", "key2" to "value2")
            val actual = loader.parse(content)
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `compositeLoader throws an ISE if no valid loader is found`() {
        val notPresent: () -> TranslatorBuilder.ContentParser = { throw NoClassDefFoundError() }

        val loader = BuiltinContentParsers.compositeParser(listOf(notPresent), "json")
        assertThrows<IllegalStateException> {
            val content = """{"key1": "value1", "key2": "value2"}"""
            loader.parse(content)
        }
    }

}
