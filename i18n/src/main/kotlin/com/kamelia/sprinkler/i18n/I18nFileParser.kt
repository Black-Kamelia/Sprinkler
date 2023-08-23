package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.util.illegalArgument
import java.nio.file.Path

interface I18nFileParser {

    fun parseFile(path: Path, fromResources: Boolean): Map<String, Any?>

    fun parseFile(path: Path): Map<String, Any?> = parseFile(path, false)

    companion object {

        @JvmStatic
        fun from(mapper: (String) -> Map<String, Any?>): I18nFileParser = object : I18nFileParser {

            override fun parseFile(path: Path, fromResources: Boolean): Map<String, Any?> {
                val content = if (fromResources) {
                    I18nFileParser::class.java.getResource(path.toString())?.readText()
                        ?: illegalArgument("No file found at $path")
                } else {
                    path.toFile().readText()
                }
                return mapper(content)
            }

        }

    }

}
