package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.i18n.TranslatorBuilder.DuplicatedKeyResolution
import java.io.File
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.nio.file.FileSystemNotFoundException
import java.nio.file.Path
import java.util.Locale


/**
 * Builder class used to create a [Translator]. This class provides several methods to add data to the translator from
 * different sources.
 *
 * TODO
 * There are several points of attention to take into account when using this class:
 * - The sources which are added will only be queried upon the creation of the translator, when calling [build]. That is
 * to say that the construction is lazy and that all the checks and logic is done when calling [build];
 * - The order in which data is added is significant, as it will be used during key duplication resolution, depending on
 * the [DuplicatedKeyResolution] used.
 * - Values passed to this builder are all validated on building, to ensure that the potential variables used in the
 * string respect the [TranslationInterpolationVariable] rules. If a value does not respect these rules, an exception
 * will be thrown when building the translator;
 *
 * The translators created with this builder will have the following properties:
 * - the created translators are immutable and therefore `thread-safe`.
 * - all methods (of the created translator) returning a translator (e.g. [section][Translator.section],
 * [withNewCurrentLocale][Translator.withNewCurrentLocale]) returns a translator sharing common information with the
 * translator which created it. The methods do not copy the data, meaning that they do not have a significant
 * performance nor memory impact.
 * - the `extraArgs` argument passed to the [t][Translator.t] methods will be used to
 * [interpolate][com.kamelia.sprinkler.util.interpolate] the translation, all keys in the map will be replaced by their
 * corresponding values in the translation, except for the [Options.OPTIONS] key, which will contain a map of options as
 * defined in the [Options] class.
 * - the [Translator.t] method will behave according to the [TranslatorConfiguration.missingKeyPolicy] chosen when
 * creating the translator, in case the key is not found.
 * - the returned map of [Translator.toMap] will be sorted according to the lexical order of the
 * [key parts][Identifier] of the keys (the map is created every time the method is called).
 * - [toString] will use [toMap] under the hood to create the string representation of the translator.
 * - To be interpolated on [t][Translator.t] method call, values stored in the translator must contain variable defined
 * inside [delimiters][TranslatorConfiguration.Builder.interpolationDelimiter] defined in the translator configuration. For more
 * details about interpolation, see [String.interpolate][com.kamelia.sprinkler.util.interpolate]. Variables' names must
 * follow a specific format, which is defined in the [TranslationInterpolationVariable] typealias documentation.
 *
 * @see Translator
 * @see TranslatorConfiguration
 */
sealed interface TranslatorBuilder {

    /**
     * Adds a path to the builder If the path is a directory, all files in it will be loaded (one level of depth, inner
     * directories are ignored). If the path is a file, it will be loaded.
     *
     * Supported formats are JSON and YAML, with the following extensions: `json`, `yaml`, and `yml`.
     *
     * The locale of the file will be parsed from the file name, using the [Locale.forLanguageTag] method. If the file's
     * name is not a valid locale identifier, an [IllegalArgumentException] will be thrown.
     *
     * @param path the path to load
     * @return this builder
     *
     * @throws IllegalArgumentException if the file extension is not supported
     * @throws IllegalArgumentException if the file name is not a valid locale identifier
     * @throws IOException if an I/O error occurs when trying to read the file
     * @throws IllegalStateException if the file contains a duplicated key and the [DuplicatedKeyResolution] is set to
     * [DuplicatedKeyResolution.FAIL]
     */
    fun addPath(path: Path): TranslatorBuilder

    /**
     * Adds a file to the builder. If the path is a directory, all files in it will be loaded (one level of depth, inner
     * directories are ignored). If the path is a file, it will be loaded.
     *
     * This method converts the [file] to a [Path] and calls [addPath].
     *
     * @param file the file to load
     * @return this builder
     *
     * @throws IllegalArgumentException if the file extension is not supported
     * @throws IllegalArgumentException if the file name is not a valid locale identifier
     * @throws IOException if an I/O error occurs when trying to read the file
     * @throws IllegalStateException if the file contains a duplicated key and the [DuplicatedKeyResolution] is set to
     * [DuplicatedKeyResolution.FAIL]
     * @see addPath
     */
    fun addFile(file: File): TranslatorBuilder = addPath(file.toPath())

    /**
     * Adds a URI to the builder. If the URI points to a directory, all files in it will be loaded (one level of depth,
     * inner directories are ignored). If the URI points to a file, it will be loaded.
     *
     * This method converts the [uri] to a [Path] and calls [addPath].
     *
     * @param uri the URI to load
     * @return this builder
     *
     * @throws FileSystemNotFoundException if an exception occurs when trying to convert the URI to a [Path]
     * @throws IllegalArgumentException if the file extension is not supported
     * @throws IllegalArgumentException if the file name is not a valid locale identifier
     * @throws IOException if an I/O error occurs when trying to read the file
     * @throws IllegalStateException if the file contains a duplicated key and the [DuplicatedKeyResolution] is set to
     * [DuplicatedKeyResolution.FAIL]
     * @see addPath
     */
    fun addURI(uri: URI): TranslatorBuilder = addPath(Path.of(uri))

    /**
     * Adds a URL to the builder. If the URL points to a directory, all files in it will be loaded (one level of depth,
     * inner directories are ignored). If the URL points to a file, it will be loaded.
     *
     * This method converts the [url] to a [URI] and then to a [Path] and calls [addPath].
     *
     * @param url the URL to load
     * @return this builder
     *
     * @throws URISyntaxException if the URL is not formatted strictly according to RFC2396 and cannot be converted to a
     * URI
     * @throws FileSystemNotFoundException if an exception occurs when trying to convert the URL to a [Path]
     * @throws IllegalArgumentException if the file extension is not supported
     * @throws IllegalArgumentException if the file name is not a valid locale identifier
     * @throws IOException if an I/O error occurs when trying to read the file
     * @throws IllegalStateException if the file contains a duplicated key and the [DuplicatedKeyResolution] is set to
     * [DuplicatedKeyResolution.FAIL]
     * @see addPath
     */
    fun addURL(url: URL): TranslatorBuilder = addPath(Path.of(url.toURI()))

    /**
     * Adds a resource to the builder. The resource is loaded using the class loader of the [resourceClass] parameter
     * and the [resourcePath] parameter. If the resource is not found, an [IllegalArgumentException] will be thrown.
     * By default, the class loader of the [TranslatorBuilder] class is used. If the resource is a directory, all files
     * in it will be loaded (one level of depth, inner directories are ignored). If the resource is a file, it will be
     * loaded.
     *
     * Supported formats are JSON and YAML, with the following extensions: `json`, `yaml`, and `yml`.
     *
     * The locale of the file will be parsed from the file name, using the [Locale.forLanguageTag] method. If the file's
     * name is not a valid locale identifier, an [IllegalStateException] will be thrown when building the translator.
     *
     * This method will throw an [IllegalArgumentException] if the file extension is not supported.
     *
     * @param resourcePath the path of the resource to load
     * @param resourceClass the class to use to load the resource (defaults to [TranslatorBuilder] class)
     * @return this builder
     * @throws IllegalArgumentException if the extension is not supported
     */
    fun addResource(resourcePath: String, resourceClass: Class<*>): TranslatorBuilder

    fun addResource(resourcePath: String): TranslatorBuilder = addResource(resourcePath, TranslatorBuilder::class.java)

    /**
     * Adds a map for a locale to the builder. The content of the map will be added to the final translator. The
     * [locale] parameter will be used as the locale of the map.
     *
     * **NOTE**: The [map] should follow rules defined in the [TranslationSourceMap] documentation. Any map that does
     * not follow these rules will result in an [IllegalArgumentException] being thrown.
     *
     * @param locale the locale of the map
     * @param map the map to add
     * @return this builder
     *
     * @throws IllegalArgumentException if the map does not follow the rules defined in the [TranslationSourceMap]
     * @throws IllegalStateException if the map contains a duplicated key and the [DuplicatedKeyResolution] is set to
     * [DuplicatedKeyResolution.FAIL]
     */
    fun addMap(locale: Locale, map: TranslationSourceMap): TranslatorBuilder

    /**
     * Adds a map of locales to the builder. The content of the maps will be added to the final translator. The keys of
     * the [maps] will be used as the locales of the maps. The values of the [maps] will be used as the content of the
     * translation maps for the corresponding locales.
     *
     * **NOTE**: The [maps] should follow rules defined in the [TranslationSourceMap] documentation. Any map that does
     * not follow these rules will result in an [IllegalArgumentException] being thrown.
     *
     * @param maps the maps to add
     * @return this builder
     *
     * @throws IllegalArgumentException if any map does not follow the rules defined in the [TranslationSourceMap]
     * @throws IllegalStateException if any map contains a duplicated key and the [DuplicatedKeyResolution] is set to
     * [DuplicatedKeyResolution.FAIL]
     * @see addMap
     */
    fun addMaps(maps: Map<Locale, TranslationSourceMap>): TranslatorBuilder = apply {
        maps.forEach { (locale, map) -> addMap(locale, map) }
    }

    /**
     * Builds the translator using the data added to the builder.
     *
     * @return the created translator
     * @throws IllegalStateException if `ignoreMissingKeyOnBuild` is `false` and at least two locales have different
     * does not have the same translation keys
     */
    fun build(): Translator

    /**
     * Defines how to handle duplicated keys when creating a translator.
     */
    enum class DuplicatedKeyResolution {

         /**
         * If a duplicated key is found, the build will fail. This policy will cause an [IllegalStateException] to be
         * thrown when a duplicated key is found.
         */
        FAIL,

        /**
         * If a duplicated key is found, the first value will be kept.
         */
        KEEP_FIRST,

        /**
         * If a duplicated key is found, the last value will be kept.
         */
        KEEP_LAST,

        ;

    }

    companion object {

        @JvmStatic
        @JvmOverloads
        fun create(
            configuration: TranslatorConfiguration = TranslatorConfiguration.builder().build(),
            ignoreMissingKeysOnBuild: Boolean = false,
            duplicatedKeyResolution: DuplicatedKeyResolution = DuplicatedKeyResolution.FAIL,
            defaultLocale: Locale = Locale.ENGLISH,
        ): TranslatorBuilder = TranslatorBuilderImpl.create(
            configuration,
            ignoreMissingKeysOnBuild,
            duplicatedKeyResolution,
            defaultLocale,
        )

        /**
         * The key regex used to validate keys.
         *
         * @return the key regex
         */
        @JvmStatic
        fun keyRegex(): Regex = KEY_REGEX

        private val KEY_REGEX = """$IDENTIFIER(?:\.$IDENTIFIER)*""".toRegex()

    }

}
