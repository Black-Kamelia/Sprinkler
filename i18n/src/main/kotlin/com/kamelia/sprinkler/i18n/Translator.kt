package com.kamelia.sprinkler.i18n

import com.fasterxml.jackson.databind.ObjectMapper
import com.kamelia.sprinkler.util.unsafeCast
import org.yaml.snakeyaml.Yaml
import java.nio.file.Path
import java.util.*

interface Translator {

    val defaultLocale: Locale

    fun translate(key: String, locale: Locale): String

    fun translate(key: String): String = translate(key, defaultLocale)

}


fun jsonParser(): I18nFileParser = I18nFileParser.from { content ->
    ObjectMapper().readValue(content, HashMap::class.java).unsafeCast()
}

fun yamlParser(): I18nFileParser = I18nFileParser.from { content ->
    Yaml().load<Map<String, Any>>(content)
}

fun main() {
    val parser = yamlParser()
    val str = parser.parseFile(Path.of("foo.json"), false)
    println(str)
}