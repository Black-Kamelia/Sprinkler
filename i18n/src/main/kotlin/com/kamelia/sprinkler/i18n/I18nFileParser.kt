package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.i18n.I18nFileParser.Companion.from
import com.kamelia.sprinkler.i18n.I18nFileParser.ParsingResult
import java.nio.file.Path
import java.util.*
import org.json.JSONException
import org.json.JSONObject
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.error.YAMLException
import kotlin.io.path.extension
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
 * or when certain files must be ignored. If the implementation is generic, it is recommended to use [from] method to
 * create a new [I18nFileParser].
 *
 * @see I18nFileParser.from
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
    data class ParsingResult(val locale: Locale, val map: TranslationSourceMap)

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
         * is a simple function that takes the path of the file and returns a valid [TranslationSourceMap].
         *
         * This method also takes an optional parser to parse the locale from the path. By default, this method uses
         * [parseLocale] to parse the locale from the file name.
         *
         * @param localeParser the parser to use to parse the locale from the file name (defaults to [parseLocale])
         * @param fileLoader the mapper to use to load and parse the file
         * @return the created [I18nFileParser]
         */
        @JvmStatic
        @JvmOverloads
        fun from(
            localeParser: (Path) -> Locale = ::parseLocale,
            fileLoader: (Path) -> TranslationSourceMap,
        ): I18nFileParser = I18nFileParser {
            val locale = localeParser(it)
            val map = fileLoader(it)
            ParsingResult(locale, map)
        }

        /**
         * Creates a new [I18nFileParser] that parses JSON files. The returned [I18nFileParser] will load the whole file
         * and parse it into a valid map.
         *
         * This method also takes an optional parser to parse the locale from the path. By default, this method uses
         * [parseLocale] to parse the locale from the file name.
         *
         * @param localeParser the parser to use to parse the locale from the file name (defaults to [parseLocale])
         * @return the created [I18nFileParser]
         */
        @JvmStatic
        @JvmOverloads
        fun json(localeParser: (Path) -> Locale = ::parseLocale): I18nFileParser =
            from(localeParser) {
                try {
                    JSONObject(it.readText()).toMap()
                } catch (_: JSONException) {
                    throw I18nParsingException("Invalid JSON file.", it)
                }
            }

        /**
         * Creates a new [I18nFileParser] that parses YAML files. The returned [I18nFileParser] will load the whole file
         * and parse it into a valid map.
         *
         * This method also takes an optional parser to parse the locale from the path. By default, this method uses
         * [parseLocale] to parse the locale from the file name.
         *
         * @param localeParser the parser to use to parse the locale from the file name (defaults to [parseLocale])
         * @return the created [I18nFileParser]
         */
        @JvmStatic
        @JvmOverloads
        fun yaml(localeParser: (Path) -> Locale = ::parseLocale): I18nFileParser =
            from(localeParser) {
                try {
                    Yaml().load(it.readText())
                } catch (_: YAMLException) {
                    throw I18nParsingException("Invalid YAML file.", it)
                }
            }

        /**
         * Creates a new [I18nFileParser] that parses JSON and YAML files. The returned [I18nFileParser] will load the
         * whole file and parse it into a valid map.
         *
         * The parser will use the file extension to determine which to use. IT only supports JSON and YAML files
         * (`yaml` and `yml` extensions are both supported), and will throw an [I18nParsingException] if the file
         * extension is not supported.
         *
         * This method also takes an optional parser to parse the locale from the path. By default, this method uses
         * [parseLocale] to parse the locale from the file name.
         *
         * @param localeParser the parser to use to parse the locale from the file name (defaults to [parseLocale])
         * @return the created [I18nFileParser]
         */
        @JvmStatic
        @JvmOverloads
        fun basicParser(localeParser: (Path) -> Locale = ::parseLocale): I18nFileParser {
            val json = json(localeParser)
            val yaml = yaml(localeParser)
            return I18nFileParser {
                when (val extension = it.extension) {
                    "json" -> json.parseFile(it)
                    "yaml", "yml" -> yaml.parseFile(it)
                    else -> throw I18nParsingException("Unsupported file extension '$extension'.", it)
                }
            }
        }

        /**
         * Helper method to parse a locale from a path. This method relies on the [Locale.Builder.setLanguageTag]
         * method and the [Path.nameWithoutExtension] property to parse the locale. This method throws an
         * [I18nParsingException] if the provided string is not a valid language tag.
         *
         * For more information about language tags, see the [Locale] documentation.
         *
         * @param path the path to the file to parse
         * @return the parsed locale
         * @throws I18nParsingException if the provided string is not a valid language tag
         * @see Locale.Builder.setLanguageTag
         * @see Locale
         */
        @JvmStatic
        fun parseLocale(path: Path): Locale {
            val locale = path.nameWithoutExtension
            return try {
                Locale.Builder()
                    .setLanguageTag(locale.replace('_', '-'))
                    .build()
            } catch (_: IllformedLocaleException) {
                throw I18nParsingException(
                    "Invalid locale '$locale'. For more details about locale syntax, see java.util.Locale documentation.",
                    path
                )
            }
        }

    }

}
