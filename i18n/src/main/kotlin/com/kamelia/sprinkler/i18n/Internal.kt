@file:PackagePrivate
package com.kamelia.sprinkler.i18n

import com.fasterxml.jackson.databind.ObjectMapper
import com.kamelia.sprinkler.util.unsafeCast
import com.zwendo.restrikt.annotation.PackagePrivate
import org.intellij.lang.annotations.Language
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.util.*

@Language("RegExp")
private const val KEY_IDENTIFIER_REGEX_STRING = """[a-zA-Z0-9](?:[\w-]*[a-zA-Z0-9])?"""

internal val KEY_IDENTIFIER_REGEX = KEY_IDENTIFIER_REGEX_STRING.toRegex()

internal val FULL_KEY_REGEX = """$KEY_IDENTIFIER_REGEX_STRING(?:\.$KEY_IDENTIFIER_REGEX_STRING)*""".toRegex()

fun jsonParser(): I18nFileParser = I18nFileParser.from { content ->
    ObjectMapper().readValue(content, HashMap::class.java).unsafeCast()
}

fun yamlParser(): I18nFileParser = I18nFileParser.from { Yaml().load(it) }

fun main() {
    val translator = Translator.builder(Locale.FRANCE)
        .addFile(File("translations"), yamlParser())
        .build()
    val translator2 = TranslatorBuilder(Locale.FRANCE)
        .addFile(File("foo.json"), jsonParser()) { Locale.ENGLISH }
        .build()
    val str = translator.section("pages.login").prettyDisplay(Locale.ENGLISH)
    val str2 = translator2.prettyDisplay(Locale.ENGLISH)
    println(translator.toMap() == translator2.toMap())
    println(str2)
}