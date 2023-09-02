package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.i18n.I18nFileParser.ParsingResult
import java.nio.file.Path
import java.util.*
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.readText

/**
 * Parser for i18n files. This interface serves as an abstraction layer between the library and the file format used, as
 * wel as the way of retrieving the locale from a file (usually the file name). This information was returned through
 * the [ParsingResult] class returned by the [parseFile] method.
 *
 * Implementations of this interface are responsible for parsing the file and returning:
 * - The locale of the file ;
 * - A map of the parsed data.
 *
 * Maps returned from [parseFile] must be structured as follows:
 * - Keys should be valid key identifiers, as defined in [Translator] ;
 * - Values cannot be null ;
 * - Values must be of type [String], [Number], [Boolean], [List] or [Map] ;
 * - Lists and maps can only contain the types listed above.
 *
 * [parseFile] method can also return null, to indicate that the file should be ignored.
 *
 * Implementing this interface can be useful when the file format is specific (e.g. only a subset of the file is used),
 * or when certain files must be ignored. When the whole file is used, it is recommended to use
 * [I18nFileParser.fromString] method instead, which loads the whole file and parses it using a provided mapper.
 *
 * @see I18nFileParser.fromString
 * @see Translator
 * @see TranslatorBuilder.addPath
 * @see TranslatorBuilder.addFile
 */
fun interface I18nFileParser {

    /**
     * Parses the provided file and returns the associated [ParsingResult], or null if the file should be ignored.
     *
     * @param path the path to the file to parse
     * @return the [ParsingResult] of the file, or null if the file should be ignored
     * @throws I18nParsingException if an error occurs while parsing the file
     */
    fun parseFile(path: Path): ParsingResult?

    /**
     * Represents the result of a file parsing. It contains the locale of the file, as well as the parsed data.
     *
     * @constructor Creates a new [ParsingResult] with the provided locale and map.
     * @param locale the locale of the file
     * @param map the parsed data
     * @property locale The locale of the file.
     * @property map The parsed data.
     * @see I18nFileParser.parseFile
     */
    @JvmRecord
    data class ParsingResult(val locale: Locale, val map: Map<String, Any>)

    /**
     * Exception thrown when an error occurs while parsing a file.
     *
     * @constructor Creates a new [I18nParsingException] with the provided message.
     * @param message the message of the exception
     * @param path the path to the file that caused the exception
     * @property path The path to the file that caused the exception.
     */
    class I18nParsingException(message: String, val path: Path) : IllegalArgumentException(message)

    companion object {

        /**
         * Creates a new [I18nFileParser] that loads the whole file and parses it using the provided mapper. The mapper
         * is a simple function that takes the file content as a string and returns a map of the parsed data. The
         * returned map must be structured as stated in this [interface][I18nFileParser] documentation.
         *
         * This method also takes an optional parser to parse the locale from the file name. This parser is a single
         * parameter function which accepts the file name (without its extension) as a string and returns the parsed
         * locale. By default, this method uses [parseLocale] to parse the locale from the file name.
         *
         * @param localeParser the parser to use to parse the locale from the file name (defaults to [parseLocale])
         * @param mapper the mapper to use to parse the file
         * @return the created [I18nFileParser]
         */
        @JvmStatic
        @JvmOverloads
        fun fromString(
            localeParser: (String) -> Locale = ::parseLocale,
            mapper: (String) -> Map<String, Any>,
        ): I18nFileParser = I18nFileParser {
            val locale = try {
                localeParser(it.nameWithoutExtension)
            } catch (e: IllformedLocaleException) {
                throw I18nParsingException("Invalid locale '${it.nameWithoutExtension}'. For more details about locale syntax, see java.util.Locale documentation.", it)
            }
            val map = mapper(it.readText())
            ParsingResult(locale, map)
        }

        /**
         * Helper method to parse a locale from a string. This method relies on the [Locale.Builder.setLanguageTag]
         * method, which throws an [IllformedLocaleException] if the provided string is not a valid language tag.
         *
         * For more information about language tags, see the [Locale] documentation.
         *
         * @param languageTag the language tag to parse
         * @return the parsed locale
         * @throws IllformedLocaleException if the provided string is not a valid language tag
         * @see Locale.Builder.setLanguageTag
         * @see Locale
         */
        @JvmStatic
        fun parseLocale(languageTag: String): Locale =
            Locale.Builder()
                .setLanguageTag(languageTag.replace('_', '-'))
                .build()

    }

}
