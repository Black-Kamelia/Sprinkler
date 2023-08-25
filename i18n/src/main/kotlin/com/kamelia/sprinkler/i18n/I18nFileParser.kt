package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.util.illegalArgument
import java.io.File
import java.nio.file.Path

interface I18nFileParser {

    fun parseFile(path: Path, fromResources: Boolean): Map<String, Any>

    fun parseFile(path: Path): Map<String, Any> = parseFile(path, true)

    companion object {

        @JvmStatic
        fun from(mapper: (String) -> Map<String, Any>): I18nFileParser = object : I18nFileParser {

            override fun parseFile(path: Path, fromResources: Boolean): Map<String, Any> {
                val file = if (fromResources) {
                    val uri = I18nFileParser::class.java.getResource(path.toString())?.toURI()
                        ?: illegalArgument("No file found in resources, at $path.")
                    File(uri)
                } else {
                    val f = path.toFile()
                    if (!f.exists()) {
                        illegalArgument("No file found at $path.")
                    }
                    f
                }
                if (!file.isFile) {
                    illegalArgument("Element at $path is not a file.")
                }
                return mapper(file.readText())
            }

        }

    }

}
