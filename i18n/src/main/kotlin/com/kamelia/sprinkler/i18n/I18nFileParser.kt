package com.kamelia.sprinkler.i18n

import java.nio.file.Path
import kotlin.io.path.readText

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
     * Parses the file at the given path and returns a map of the parsed data. The returned map must be structured as
     * stated in this [interface][I18nFileParser] documentation.
     *
     * @param path the path to the file to parse
     * @return the map of the parsed data
     */
    fun parseFile(path: Path): Map<String, Any>

    companion object {

        /**
         * Creates a new [I18nFileParser] that loads the whole file and parses it using the provided mapper. The mapper
         * is a simple function that takes the file content as a string and returns a map of the parsed data. The
         * returned map must be structured as stated in this [interface][I18nFileParser] documentation.
         *
         * @param mapper the mapper to use to parse the file
         * @return the created [I18nFileParser]
         */
        @JvmStatic
        fun from(mapper: (String) -> Map<String, Any>): I18nFileParser = I18nFileParser { mapper(it.readText()) }

    }

}
