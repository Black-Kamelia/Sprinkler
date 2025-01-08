package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.i18n.TranslatorBuilder.Companion.defaultContentLoaders
import com.kamelia.sprinkler.i18n.TranslatorBuilder.ContentLoader
import com.kamelia.sprinkler.i18n.TranslatorBuilder.DuplicatedKeyResolution
import com.kamelia.sprinkler.util.unmodifiableMapOf
import java.io.File
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.nio.charset.Charset
import java.nio.file.FileSystemNotFoundException
import java.nio.file.Path
import java.util.Locale


/**
 * Builder class used to create a [Translator]. This class provides several methods to add data to the translator from
 * different sources.
 *
 * There are several points of attention to take into account when using this class:
 * - The order in which data is added is significant, as it will be used during key duplication resolution, depending on
 * the [DuplicatedKeyResolution] used.
 * - Values passed to this builder are all validated on building, to ensure that the potential variables used in the
 * string respect the [TranslationInterpolationVariable] rules. If a value does not respect these rules, an exception
 * will be thrown when adding the value to the builder.
 * - The content of the files is parsed using [ContentLoaders][ContentLoader]. Loaders are provided by a map associating
 * file extensions to content loaders. This map is passed to the builder when creating it. If a file extension is not
 * present in the map, an [IllegalArgumentException] will be thrown. Default content loaders exist but require
 * additional dependencies to be added to the project. For more information, see the [defaultContentLoaders] method.
 *
 * The translators created with this builder will have the following properties:
 *
 * - the created translators are immutable and therefore `thread-safe`.
 *
 *
 * - all methods returning a translator (e.g. [section][Translator.section],
[withNewCurrentLocale][Translator.withNewCurrentLocale]) returns a translator sharing common information with the
 * translator which created it. The methods do not copy the data, meaning that they do not have a significant
 * performance nor memory impact.
 *
 *
 * - the `extraArgs` argument passed to the [t][Translator.t] methods will be used to
 * [interpolate][com.kamelia.sprinkler.util.interpolate] the translation, all keys in the map will be replaced by their
 * corresponding values in the translation.
 *
 *
 * - options (keys starting with an underscore `_`) passed in the `extraArgs` will also be used to format values with
 * the same name after dropping the underscore (e.g. the option `_count` will be used to format the value of the
 * `count` variable in the translation). However, an argument with the exact name of the key is present in the map, it
 * will take precedence over the option (e.g. if the map contains a key `count` and the option `_count`, the value of
 * the `count` arg will be used).
 *
 *
 * - the [Translator.t] method will behave according to the [TranslatorConfiguration.missingKeyPolicy] chosen when
 * creating the translator, in case the key is not found.
 *
 *
 * - the returned map of [Translator.toMap] will be sorted according to the lexical order of the
 * [key parts][Identifier] of the keys (a new map is created every time the method is called).
 *
 *
 *
 * - To be interpolated on [t][Translator.t] method call, values stored in the translator must contain variable defined
 * inside [delimiters][TranslatorConfiguration.Builder.interpolationDelimiter] defined in the translator configuration. For more
 * details about interpolation, see [String.interpolate][com.kamelia.sprinkler.util.interpolate]. Variables' names must
 * follow a specific format, which is defined in the [TranslationInterpolationVariable] typealias documentation.
 *
 * @see Translator
 * @see TranslatorConfiguration
 * @see FormattedValueImpl
 */
sealed interface TranslatorBuilder {

    /**
     * Adds a path to the builder If the path is a directory, all files in it will be loaded (one level of depth, inner
     * directories are ignored). If the path is a file, it will be loaded.
     *
     * The locale of the file will be parsed from the file name, using the [Locale.forLanguageTag] method. If the file's
     * name is not a valid locale identifier, an [IllegalArgumentException] will be thrown.
     *
     * @param path the path to load
     * @param charset the charset to use when reading the file
     * @return this builder
     *
     * @throws IllegalArgumentException if the extension is not supported by any [ContentLoader]* @throws IllegalArgumentException if the extension is not supported by any [ContentLoader]
     * @throws IllegalArgumentException if the file name is not a valid locale identifier
     * @throws IOException if an I/O error occurs when trying to read the file
     * @throws IllegalStateException if the file contains a duplicated key and the [DuplicatedKeyResolution] is set to
     * [DuplicatedKeyResolution.FAIL]
     */
    fun addPath(path: Path, charset: Charset): TranslatorBuilder

    /**
     * Adds a path to the builder. If the path is a directory, all files in it will be loaded (one level of depth, inner
     * directories are ignored). If the path is a file, it will be loaded.
     *
     * The locale of the file will be parsed from the file name, using the [Locale.forLanguageTag] method. If the file's
     * name is not a valid locale identifier, an [IllegalArgumentException] will be thrown.
     *
     * This method uses the [defaultCharset] to read the file.
     *
     * @param path the path to load
     * @return this builder
     *
     * @throws IllegalArgumentException if the extension is not supported by any [ContentLoader]* @throws IllegalArgumentException if the extension is not supported by any [ContentLoader]
     * @throws IllegalArgumentException if the file name is not a valid locale identifier
     * @throws IOException if an I/O error occurs when trying to read the file
     * @throws IllegalStateException if the file contains a duplicated key and the [DuplicatedKeyResolution] is set to
     * [DuplicatedKeyResolution.FAIL]
     * @see addPath
     */
    fun addPath(path: Path): TranslatorBuilder = addPath(path, defaultCharset)

    /**
     * Adds a file to the builder. If the path is a directory, all files in it will be loaded (one level of depth, inner
     * directories are ignored). If the path is a file, it will be loaded.
     *
     * The locale of the file will be parsed from the file name, using the [Locale.forLanguageTag] method. If the file's
     * name is not a valid locale identifier, an [IllegalArgumentException] will be thrown.
     *
     * This method converts the [file] to a [Path] and calls [addPath].
     *
     * @param file the file to load
     * @param charset the charset to use when reading the file
     * @return this builder
     *
     * @throws IllegalArgumentException if the extension is not supported by any [ContentLoader]* @throws IllegalArgumentException if the extension is not supported by any [ContentLoader]
     * @throws IllegalArgumentException if the file name is not a valid locale identifier
     * @throws IOException if an I/O error occurs when trying to read the file
     * @throws IllegalStateException if the file contains a duplicated key and the [DuplicatedKeyResolution] is set to
     * [DuplicatedKeyResolution.FAIL]
     * @see addPath
     */
    fun addFile(file: File, charset: Charset): TranslatorBuilder = addPath(file.toPath(), charset)

    /**
     * Adds a file to the builder. If the path is a directory, all files in it will be loaded (one level of depth, inner
     * directories are ignored). If the path is a file, it will be loaded.
     *
     * The locale of the file will be parsed from the file name, using the [Locale.forLanguageTag] method. If the file's
     * name is not a valid locale identifier, an [IllegalArgumentException] will be thrown.
     *
     * This method uses the [defaultCharset] to read the file.
     *
     * This method converts the [file] to a [Path] and calls [addPath].
     *
     * @param file the file to load
     * @return this builder
     *
     * @throws IllegalArgumentException if the extension is not supported by any [ContentLoader]* @throws IllegalArgumentException if the extension is not supported by any [ContentLoader]
     * @throws IllegalArgumentException if the file name is not a valid locale identifier
     * @throws IOException if an I/O error occurs when trying to read the file
     * @throws IllegalStateException if the file contains a duplicated key and the [DuplicatedKeyResolution] is set to
     * [DuplicatedKeyResolution.FAIL]
     * @see addPath
     */
    fun addFile(file: File): TranslatorBuilder = addFile(file, defaultCharset)

    /**
     * Adds a URI to the builder. If the URI points to a directory, all files in it will be loaded (one level of depth,
     * inner directories are ignored). If the URI points to a file, it will be loaded.
     *
     * The locale of the file will be parsed from the file name, using the [Locale.forLanguageTag] method. If the file's
     * name is not a valid locale identifier, an [IllegalArgumentException] will be thrown.
     *
     * This method converts the [uri] to a [Path] and calls [addPath].
     *
     * @param uri the URI to load
     * @param charset the charset to use when reading the file
     * @return this builder
     *
     * @throws URISyntaxException if the URI is not formatted strictly according to RFC2396 and cannot be converted to a
     * URI
     * @throws FileSystemNotFoundException if an exception occurs when trying to convert the URI to a [Path]
     * @throws IllegalArgumentException if the extension is not supported by any [ContentLoader]
     * @throws IllegalArgumentException if the file name is not a valid locale identifier
     * @throws IOException if an I/O error occurs when trying to read the file
     * @throws IllegalStateException if the file contains a duplicated key and the [DuplicatedKeyResolution] is set to
     * [DuplicatedKeyResolution.FAIL]
     * @see addPath
     */
    fun addURI(uri: URI, charset: Charset): TranslatorBuilder = addPath(Path.of(uri), charset)

    /**
     * Adds a URI to the builder. If the URI points to a directory, all files in it will be loaded (one level of depth,
     * inner directories are ignored). If the URI points to a file, it will be loaded.
     *
     * The locale of the file will be parsed from the file name, using the [Locale.forLanguageTag] method. If the file's
     * name is not a valid locale identifier, an [IllegalArgumentException] will be thrown.
     *
     * This method uses the [defaultCharset] to read the file.
     *
     * This method converts the [uri] to a [Path] and calls [addPath].
     *
     * @param uri the URI to load
     * @return this builder
     *
     * @throws URISyntaxException if the URI is not formatted strictly according to RFC2396 and cannot be converted to a
     * URI
     * @throws FileSystemNotFoundException if an exception occurs when trying to convert the URI to a [Path]
     * @throws IllegalArgumentException if the extension is not supported by any [ContentLoader]
     * @throws IllegalArgumentException if the file name is not a valid locale identifier
     * @throws IOException if an I/O error occurs when trying to read the file
     * @throws IllegalStateException if the file contains a duplicated key and the [DuplicatedKeyResolution] is set to
     * [DuplicatedKeyResolution.FAIL]
     * @see addPath
     */
    fun addURI(uri: URI): TranslatorBuilder = addURI(uri, defaultCharset)

    /**
     * Adds a URL to the builder. If the URL points to a directory, all files in it will be loaded (one level of depth,
     * inner directories are ignored). If the URL points to a file, it will be loaded.
     *
     * The locale of the file will be parsed from the file name, using the [Locale.forLanguageTag] method. If the file's
     * name is not a valid locale identifier, an [IllegalArgumentException] will be thrown.
     *
     * This method converts the [url] to a [URI] and then to a [Path] and calls [addPath].
     *
     * @param url the URL to load
     * @param charset the charset to use when reading the file
     * @return this builder
     *
     * @throws URISyntaxException if the URL is not formatted strictly according to RFC2396 and cannot be converted to a
     * URI
     * @throws FileSystemNotFoundException if an exception occurs when trying to convert the URL to a [Path]
     * @throws IllegalArgumentException if the extension is not supported by any [ContentLoader]
     * @throws IllegalArgumentException if the file name is not a valid locale identifier
     * @throws IOException if an I/O error occurs when trying to read the file
     * @throws IllegalStateException if the file contains a duplicated key and the [DuplicatedKeyResolution] is set to
     * [DuplicatedKeyResolution.FAIL]
     * @see addPath
     */
    fun addURL(url: URL, charset: Charset): TranslatorBuilder = addPath(Path.of(url.toURI()), charset)

    /**
     * Adds a URL to the builder. If the URL points to a directory, all files in it will be loaded (one level of depth,
     * inner directories are ignored). If the URL points to a file, it will be loaded.
     *
     * The locale of the file will be parsed from the file name, using the [Locale.forLanguageTag] method. If the file's
     * name is not a valid locale identifier, an [IllegalArgumentException] will be thrown.
     *
     * This method uses the [defaultCharset] to read the file.
     *
     * This method converts the [url] to a [URI] and then to a [Path] and calls [addPath].
     *
     * @param url the URL to load
     * @return this builder
     *
     * @throws URISyntaxException if the URL is not formatted strictly according to RFC2396 and cannot be converted to a
     * URI
     * @throws FileSystemNotFoundException if an exception occurs when trying to convert the URL to a [Path]
     * @throws IllegalArgumentException if the extension is not supported by any [ContentLoader]
     * @throws IllegalArgumentException if the file name is not a valid locale identifier
     * @throws IOException if an I/O error occurs when trying to read the file
     * @throws IllegalStateException if the file contains a duplicated key and the [DuplicatedKeyResolution] is set to
     * [DuplicatedKeyResolution.FAIL]
     * @see addPath
     */
    fun addURL(url: URL): TranslatorBuilder = addURL(url, defaultCharset)

    /**
     * Adds a resource to the builder. The resource is loaded using the class loader of the [resourceClass] parameter
     * and the [resourcePath] parameter. If the resource is not found, an [IllegalArgumentException] will be thrown.
     * The [resourcePath] can represent a file or a directory. If the resource is a directory, all files in it will be
     * loaded (one level of depth, inner directories are ignored). If the resource is a file, it will be loaded.
     *
     * This method resolves the resource path in the same way as [getResourceAsStream][Class.getResourceAsStream]. If
     * the provided [resourcePath] is absolute (i.e. it starts with a `/`), it will be resolved as an absolute path
     * without further modification. If the provided [resourcePath] is relative, it will be resolved relative to the
     * package of the [resourceClass] parameter, with dots (`.`) replaced by slashes (`/`).
     *
     * Here is an example to illustrate the resolution:
     * ```
     * package com.example
     *
     * object Example {
     *     fun test() {
     *         // This will resolve to the resource `/com/example/example.yml`
     *         TranslatorBuilder.create().addResource("example.yml", Example::class.java, Charsets.UTF_8)
     *
     *         // This will resolve to the resource `/example.yml` because it is an absolute path
     *         TranslatorBuilder.create().addResource("/example.yml", Example::class.java, Charsets.UTF_8)
     *     }
     * }
     * ```
     *
     * The locale of the file will be parsed from the file name, using the [Locale.forLanguageTag] method. If the file's
     * name is not a valid locale identifier, an [IllegalStateException] will be thrown when building the translator.
     *
     * @param resourcePath the path of the resource to load
     * @param resourceClass the class to use to load the resource (defaults to [TranslatorBuilder] class)
     * @param charset the charset to use when reading the file
     * @return this builder
     *
     * @throws IllegalArgumentException if the extension is not supported by any [ContentLoader]
     * @throws IllegalArgumentException if the file name is not a valid locale identifier
     * @throws IOException if an I/O error occurs when trying to read the file
     * @throws IllegalStateException if the file contains a duplicated key and the [DuplicatedKeyResolution] is set to
     * [DuplicatedKeyResolution.FAIL]
     */
    fun addResource(resourcePath: String, resourceClass: Class<*>, charset: Charset): TranslatorBuilder

    /**
     * Adds a resource to the builder. The resource is loaded using the class loader of the [resourceClass] parameter
     * and the [resourcePath] parameter. If the resource is not found, an [IllegalArgumentException] will be thrown.
     * The [resourcePath] can represent a file or a directory. If the resource is a directory, all files in it will be
     * loaded (one level of depth, inner directories are ignored). If the resource is a file, it will be loaded.
     *
     * This method resolves the resource path in the same way as [getResourceAsStream][Class.getResourceAsStream]. If
     * the provided [resourcePath] is absolute (i.e. it starts with a `/`), it will be resolved as an absolute path
     * without further modification. If the provided [resourcePath] is relative, it will be resolved relative to the
     * package of the [resourceClass] parameter, with dots (`.`) replaced by slashes (`/`).
     *
     * Here is an example to illustrate the resolution:
     * ```
     * package com.example
     *
     * object Example {
     *     fun test() {
     *         // This will resolve to the resource `/com/example/example.yml`
     *         TranslatorBuilder.create().addResource("example.yml", Example::class.java)
     *
     *         // This will resolve to the resource `/example.yml` because it is an absolute path
     *         TranslatorBuilder.create().addResource("/example.yml", Example::class.java)
     *     }
     * }
     * ```
     *
     * The locale of the file will be parsed from the file name, using the [Locale.forLanguageTag] method. If the file's
     * name is not a valid locale identifier, an [IllegalStateException] will be thrown when building the translator.
     *
     * This method uses the [defaultCharset] to read the file.
     *
     * @param resourcePath the path of the resource to load
     * @param resourceClass the class to use to load the resource (defaults to [TranslatorBuilder] class)
     * @return this builder
     *
     * @throws IllegalArgumentException if the extension is not supported by any [ContentLoader]
     * @throws IllegalArgumentException if the file name is not a valid locale identifier
     * @throws IOException if an I/O error occurs when trying to read the file
     * @throws IllegalStateException if the file contains a duplicated key and the [DuplicatedKeyResolution] is set to
     * [DuplicatedKeyResolution.FAIL]
     * @see addResource
     */
    fun addResource(resourcePath: String, resourceClass: Class<*>): TranslatorBuilder

    /**
     * Adds a resource to the builder. The resource is loaded using the class loader of the class calling this method
     * and the [resourcePath] parameter. If the resource is not found, an [IllegalArgumentException] will be thrown. The
     * [resourcePath] can represent a file or a directory. If the resource is a directory, all files in it will be
     * loaded (one level of depth, inner directories are ignored). If the resource is a file, it will be loaded.
     *
     * This method resolves the resource path in the same way as [getResourceAsStream][Class.getResourceAsStream]. If
     * the provided [resourcePath] is absolute (i.e. it starts with a `/`), it will be resolved as an absolute
     * without further modification. If the provided [resourcePath] is relative, it will be resolved relative to the
     * package of the class calling this method, with dots (`.`) replaced by slashes (`/`).
     *
     * Here is an example to illustrate the resolution:
     * ```
     * package com.example
     *
     * object Example {
     *     fun test() {
     *         // This will resolve to the resource `/com/example/example.yml`
     *         TranslatorBuilder.create().addResource("example.yml", Charsets.UTF_8)
     *
     *         // This will resolve to the resource `/example.yml` because it is an absolute path
     *         TranslatorBuilder.create().addResource("/example.yml", Charsets.UTF_8)
     *     }
     * }
     * ```
     *
     * The locale of the file will be parsed from the file name, using the [Locale.forLanguageTag] method. If the file's
     * name is not a valid locale identifier, an [IllegalStateException] will be thrown when building the translator.
     *
     * @param resourcePath the path of the resource to load
     * @param charset the charset to use when reading the file
     * @return this builder
     *
     * @throws IllegalArgumentException if the extension is not supported by any [ContentLoader]
     * @throws IllegalArgumentException if the file name is not a valid locale identifier
     * @throws IOException if an I/O error occurs when trying to read the file
     * @throws IllegalStateException if the file contains a duplicated key and the [DuplicatedKeyResolution] is set to
     * [DuplicatedKeyResolution.FAIL]
     * @see addResource
     */
    fun addResource(resourcePath: String, charset: Charset): TranslatorBuilder

    /**
     * Adds a resource to the builder. The resource is loaded using the class loader of the class calling this method
     * and the [resourcePath] parameter. If the resource is not found, an [IllegalArgumentException] will be thrown. The
     * [resourcePath] can represent a file or a directory. If the resource is a directory, all files in it will be
     * loaded (one level of depth, inner directories are ignored). If the resource is a file, it will be loaded.
     *
     * This method resolves the resource path in the same way as [getResourceAsStream][Class.getResourceAsStream]. If
     * the provided [resourcePath] is absolute (i.e. it starts with a `/`), it will be resolved as an absolute path
     * without further modification. If the provided [resourcePath] is relative, it will be resolved relative to the
     * package of the class calling this method, with dots (`.`) replaced by slashes (`/`).
     *
     * Here is an example to illustrate the resolution:
     * ```
     * package com.example
     *
     * object Example {
     *     fun test() {
     *         // This will resolve to the resource `/com/example/example.yml`
     *         TranslatorBuilder.create().addResource("example.yml")
     *
     *         // This will resolve to the resource `/example.yml` because it is an absolute path
     *         TranslatorBuilder.create().addResource("/example.yml")
     *     }
     * }
     * ```
     *
     * The locale of the file will be parsed from the file name, using the [Locale.forLanguageTag] method. If the file's
     * name is not a valid locale identifier, an [IllegalStateException] will be thrown when building the translator.
     *
     * This method uses the [defaultCharset] to read the file.
     *
     * @param resourcePath the path of the resource to load
     * @return this builder
     *
     * @throws IllegalArgumentException if the extension is not supported by any [ContentLoader]
     * @throws IllegalArgumentException if the file name is not a valid locale identifier
     * @throws IOException if an I/O error occurs when trying to read the file
     * @throws IllegalStateException if the file contains a duplicated key and the [DuplicatedKeyResolution] is set to
     * [DuplicatedKeyResolution.FAIL]
     * @see addResource
     */
    fun addResource(resourcePath: String): TranslatorBuilder

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

    /**
     * Simple type representing a content loader. A content loader is a function that takes a string as input and
     * parses it into a [TranslationSourceMap].
     */
    fun interface ContentLoader {

        /**
         * Loads the content of a file into a [TranslationSourceMap].
         *
         * @param content the content of the file
         * @return the parsed content
         */
        fun load(content: String): TranslationSourceMap

    }

    companion object {

        /**
         * Creates a new [TranslatorBuilder].
         *
         * @param configuration the configuration to use when creating the translator (defaults to the default
         * configuration)
         * @param defaultLocale the default locale to use when no locale is specified (defaults to [Locale.ENGLISH])
         * @param ignoreMissingKeysOnBuild whether to ignore missing keys when building the translator (defaults to
         * `false`)
         * @param contentLoaderFactories a map mapping file extensions to content loader factories (defaults to the map
         * created by [defaultContentLoaders]). Each loaded file will be parsed using the content loader corresponding
         * to its extension.
         * @param duplicatedKeyResolution the resolution to use when a duplicated key is found (defaults to
         * [DuplicatedKeyResolution.FAIL])
         * @param defaultCharset the default charset to use when reading files (defaults to [Charsets.UTF_8])
         * @return the created builder
         */
        @JvmStatic
        @JvmOverloads
        fun create(
            configuration: TranslatorConfiguration = TranslatorConfiguration.builder().build(),
            defaultLocale: Locale = Locale.ENGLISH,
            ignoreMissingKeysOnBuild: Boolean = false,
            contentLoaderFactories: Map<String, () -> ContentLoader> = defaultContentLoaders(),
            duplicatedKeyResolution: DuplicatedKeyResolution = DuplicatedKeyResolution.FAIL,
            defaultCharset: Charset = Charsets.UTF_8,
        ): TranslatorBuilder =
            TranslatorBuilderImpl.create(
                configuration,
                ignoreMissingKeysOnBuild,
                contentLoaderFactories,
                duplicatedKeyResolution,
                defaultLocale,
                defaultCharset,
            )


        /**
         * The key regex used to validate keys.
         *
         * @return the key regex
         */
        @JvmStatic
        fun keyRegex(): Regex = KEY_REGEX

        /**
         * The default content loaders used when creating a translator.
         *
         * By default, this map contains loaders for:
         * - JSON (`.json` files)
         * - YAML (`.yaml` and `.yml` files)
         * - TOML (`.toml` files)
         *
         * All loaders in this map rely on optional external libraries which are not included by default. If you want to
         * use these loaders, you must include the corresponding dependencies in your project.
         *
         * Here are the possible dependencies for each loader:
         *
         * - JSON:
         *     - `com.fasterxml.jackson.core:jackson-databind:2.18.2`
         *     - `com.google.code.gson:gson:2.11.0`
         *     - `org.json:json:20241224`
         * - YAML:
         *     - `com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.18.2`
         *     - `org.yaml:snakeyaml:2.3`
         * - TOML:
         *     - `com.fasterxml.jackson.dataformat:jackson-dataformat-toml:2.18.2`
         *     - `io.hotmoka:toml4j:0.7.0`
         *
         * **NOTE**: Version numbers are indicative. They are the versions used in this library and may not be the
         * latest versions available at the time you read this. Moreover, for some of the listed libraries, older
         * version may also work.
         *
         * **NOTE**: You only need the dependencies for the loaders you want to use and only need to include one of
         * them. In case you include more than one, the order in which they are listed above is the order of precedence.
         *
         *
         *
         * @return the default content loaders
         */
        @JvmStatic
        fun defaultContentLoaders(): Map<String, () -> ContentLoader> {
            val yamlLoader = BuiltinContentLoaders.yamlLoader()
            return unmodifiableMapOf(
                "json" to BuiltinContentLoaders.jsonLoader(),
                "yaml" to yamlLoader,
                "yml" to yamlLoader,
                "toml" to BuiltinContentLoaders.tomlLoader(),
            )
        }

        private val KEY_REGEX = """$IDENTIFIER(?:\.$IDENTIFIER)*""".toRegex()

        /**
         * The default charset to use when reading files. It is defined when creating the builder.
         * @see TranslatorBuilder.create
         */
        private val TranslatorBuilder.defaultCharset: Charset
            get() = (this as TranslatorBuilderImpl).defaultCharset

    }

}
