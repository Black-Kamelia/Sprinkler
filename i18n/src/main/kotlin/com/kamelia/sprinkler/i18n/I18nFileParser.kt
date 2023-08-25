package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.util.illegalArgument
import java.io.File
import java.nio.file.Path

/**
 * Parser for i18n files. This interface serves as an abstraction layer between the library and the file format used.
 * Implementations of this interface are responsible for parsing the file and returning a map of the parsed data. The
 * map must be structured as follows:
 *
 * TODO
 *
 * Implementing this interface can be useful when the file format is specific (e.g. only a subset of the file is used).
 * When the whole file is used, it is recommended to use [I18nFileParser.from] instead, which loads the whole file and
 * parses it using a provided mapper.
 *
 * @see I18nFileParser.from
 * @see Translator
 * @see TranslatorBuilder.addPath
 */
fun interface I18nFileParser {

    /**
     * Parses the file at the given path and returns a map of the parsed data.
     */
    fun parseFile(path: Path, fromResources: Boolean): Map<String, Any>

    fun parseFile(path: Path): Map<String, Any> = parseFile(path, true)

    companion object {

        @JvmStatic
        fun from(mapper: (String) -> Map<String, Any>): I18nFileParser =
            I18nFileParser { path, fromResources ->
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
                mapper(file.readText())
            }

    }

}
