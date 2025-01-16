package com.kamelia.sprinkler.i18n.impl

import com.kamelia.sprinkler.i18n.Translator
import com.kamelia.sprinkler.i18n.formatting.VariableFormatter
import com.kamelia.sprinkler.i18n.impl.TranslatorBuilder.Companion.defaultContentLoaders
import com.kamelia.sprinkler.i18n.impl.TranslatorBuilder.Configuration
import com.kamelia.sprinkler.i18n.impl.TranslatorBuilder.ContentParser
import com.kamelia.sprinkler.i18n.impl.TranslatorBuilder.DuplicatedKeyResolution
import com.kamelia.sprinkler.i18n.pluralization.PluralMapper
import com.kamelia.sprinkler.util.VariableDelimiter
import com.kamelia.sprinkler.util.unmodifiableMapOf
import com.zwendo.restrikt2.annotation.HideFromJava
import com.zwendo.restrikt2.annotation.HideFromKotlin
import com.zwendo.restrikt2.annotation.PackagePrivate
import java.io.File
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.nio.charset.Charset
import java.nio.file.FileSystemNotFoundException
import java.nio.file.Path
import java.util.Locale
import java.util.function.Consumer


/**
 * Builder class used to create a [Translator]. This class provides several methods to add data to the translator from
 * different sources.
 *
 * It can be used in a dsl through the [Translator][com.kamelia.sprinkler.i18n.impl.Translator] method, to create a new
 * [Translator] configure with the provided dsl content. The dsl is divided in three parts:
 *
 * - The configuration available directly through the [TranslatorBuilder] interface, which allows to configure general
 * aspects of the translator.
 * - The [configuration] block, which allows to configure the behavior of the built [Translator], like for example the
 * [interpolationDelimiter][Configuration.interpolationDelimiter] defining the delimiters used for interpolation
 * variables in strings (e.g. `{` and `}` in the string `"Hello mister {name}!"`).
 * - The [translations] block, which allows to load the content of the built [Translator]. Content can be loaded from
 * [maps][Map], any class representing a file ([File], [Path], [URL], [URI]) or a resource (including jar resources).
 *
 * Here is a simple example of how to use the builder:
 *
 * ```
 * val translator = Translator {
 *     configuration {
 *         interpolationDelimiter = TranslatorBuilder.InterpolationDelimiter.create("{", "}")
 *     }
 *
 *     translations {
 *         defaultCharset = Charsets.US_ASCII
 *         path(Path.of("path/to/file.yml"))
 *     }
 * }
 * ```
 *
 * There are several points of attention to take into account when using this class:
 * - The order in which data is added is significant, as it will be used during key duplication resolution, depending on
 * the [DuplicatedKeyResolution] used.
 * - Values passed to this builder are all validated on building, to ensure that the potential variables used in the
 * string respect the [TranslationInterpolationVariable] rules. If a value does not respect these rules, an exception
 * will be thrown when adding the value to the builder.
 * - The content of the files is parsed using [ContentLoaders][ContentParser]. Loaders are provided by a map associating
 * file extensions to content parsers. This map is passed to the builder when creating it. If a file extension is not
 * present in the map, an [IllegalArgumentException] will be thrown. Default content parsers exist but require
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
 * - the [Translator.t] method will behave according to the
 * [missingKeyPolicy][TranslatorBuilder.Configuration.missingKeyPolicy] chosen when creating the translator, in case
 * the key is not found.
 *
 *
 * - the returned map of [Translator.toMap] will be sorted according to the lexical order of the
 * [key parts][com.kamelia.sprinkler.i18n.Identifier] of the keys (a new map is created every time the method is
 * called).
 *
 *
 *
 * - To be interpolated on [t][Translator.t] method call, values stored in the translator must contain variable defined
 * inside [delimiters][TranslatorBuilder.Configuration.interpolationDelimiter] defined in the translator configuration.
 * For more details about interpolation, see [String.interpolate][com.kamelia.sprinkler.util.interpolate]. Variables'
 * names must follow a specific format, which is defined in the [TranslationInterpolationVariable] typealias
 * documentation.
 *
 * @see Translator
 * @see [com.kamelia.sprinkler.i18n.impl.Translator]
 */
@TranslatorBuilder.TranslatorBuilderDsl
sealed interface TranslatorBuilder {

    /**
     * Whether to ignore missing keys when building the translator.
     *
     * default: `false`
     *
     * @return whether to ignore missing keys
     */
    var ignoreMissingKeysOnBuild: Boolean

    /**
     * Starts the configuration block of the builder. This block is used to configure the behavior of the built
     * [Translator].
     *
     * @param block the code to apply to the configuration
     * @throws IllegalStateException if the block is called more than once
     * @see Configuration
     */
    @HideFromJava
    fun configuration(block: Configuration.() -> Unit)

    /**
     * Starts the configuration block of the builder. This block is used to configure the behavior of the built
     * [Translator].
     *
     * @param block the code to apply to the configuration
     * @return this builder
     * @throws IllegalStateException if the block is called more than once
     * @see Configuration
     */
    @HideFromKotlin
    fun configuration(block: Consumer<Configuration>): TranslatorBuilder = apply {
        configuration { block.accept(this) }
    }

    /**
     * Starts the content block of the builder. This block is used to load the content of the built [Translator].
     *
     * @param block the code to apply to the content
     * @throws IllegalStateException if the block is called more than once
     * @see Content
     */
    @HideFromJava
    fun translations(block: Content.() -> Unit)

    /**
     * Starts the content block of the builder. This block is used to load the content of the built [Translator].
     *
     * @param block the code to apply to the content
     * @return this builder
     * @throws IllegalStateException if the block is called more than once
     * @see Content
     */
    @HideFromKotlin
    fun translations(block: Consumer<Content>): TranslatorBuilder = apply {
        translations { block.accept(this) }
    }

    /**
     * Type defining the configuration of the built [Translator]. Parameters of this type only affect the behavior of
     * the built [Translator] and aren't involved in the loading of the content.
     */
    @TranslatorBuilderDsl
    sealed interface Configuration {

        /**
         * The delimiter to use for interpolation in translations.
         *
         * default: '{{' and '}}'
         */
        var interpolationDelimiter: InterpolationDelimiter

        /**
         * The function providing the [PluralMapper]s used by the created [Translator].
         *
         * default: [PluralMapper.builtins]
         */
        var pluralMapperFactory: (Locale) -> PluralMapper

        /**
         * Map used to find formatters using their name during variable interpolation.
         *
         * default: [VariableFormatter.builtins]
         *
         * @see VariableFormatter
         */
        var formatters: Map<String, VariableFormatter<out Any>>

        /**
         * The policy to use when a key is not found.
         *
         * default: [MissingKeyPolicy.THROW_EXCEPTION]
         *
         * @see MissingKeyPolicy
         */
        var missingKeyPolicy: MissingKeyPolicy

        /**
         * The default locale to use when no locale is specified when calling the [Translator.t] method.
         *
         * default: [Locale.ENGLISH]
         *
         * @return the default locale
         */
        var defaultLocale: Locale

    }

    /**
     * Type defining the content to load into the built [Translator]. This type provides several methods to load file
     * contents into the built [Translator].
     *
     * The files are loaded using the two properties [contentParserFactory] and [localeParser]:
     * - When a file is provided, the [Locale] it represents is determined by the [localeParser] function, which is called
     * with the filename without extension.
     *
     * - The content of the file is then parsed using the [ContentParser] associated with the file extension. The parser
     * is determined by the [contentParserFactory] function, which is called with the file extension (for more details
     * about how an unrecognized extension is handled, see [contentParserFactory] documentation).
     *
     * It is important to note that in the context of the [translations] method, the content of the dsl is executed
     * in the order it is written, meaning that altering properties after calling a method that loads content will not
     * affect the content loading. You can find an illustration of this behavior below:
     *
     * ```
     * Translator {
     *     translations {
     *         path(Path.of(...))
     *         defaultCharset = Charsets.US_ASCII // this will not affect the content loading above
     *     }
     * }
     * ```
     */
    @TranslatorBuilderDsl
    sealed interface Content {

        /**
         * The function providing the [ContentParser] corresponding to the file extension (e.g. `json` should return a
         * json content parser).
         *
         * If the extension is not supported by any [ContentParser] of this factory, here are the possible cases:
         *
         * - If the missing extension has been required due to a file being present in a loaded folder (e.g.
         * `path(Path.of("myFolder/"))` has been called in the ds, and it contains an `en_US.unknown` file), if the
         * [ignoreUnrecognizedExtensionsInDirectory] property is set to `true`, the file will be ignored. If it is set
         * to `false`, an [IllegalArgumentException] will be thrown.
         *
         *
         * - If the missing extension has been required due to a specific file being explicitly loaded (e.g.
         * `path(Path.of("en_US.unknown"))` has been called in the dsl), an [IllegalArgumentException] will always be
         * thrown.
         *
         * default: [defaultContentLoaders]
         *
         * @return the content parser factory
         */
        var contentParserFactory: (String) -> ContentParser?

        /**
         * The resolution to use when a duplicated key is found.
         *
         * default: [DuplicatedKeyResolution.FAIL]
         *
         * @return the resolution to use when a duplicated key is found
         */
        var duplicatedKeyResolution: DuplicatedKeyResolution

        /**
         * The default charset to use when reading files.
         *
         * default: [Charset.defaultCharset]
         *
         * @return the default charset
         */
        var defaultCharset: Charset

        /**
         * The function to use to parse a locale from a string.
         *
         * Each file loaded by [Content] will have its filename parsed by this function to determine the locale of the file.
         * Only the **filename without extension** is passed to this function (e.g. `path/to/en_US.yml` will pass `en_US`).
         *
         * default: [Locale.Builder.setLanguageTag]
         *
         * @return the locale parser
         */
        var localeParser: (String) -> Locale

        /**
         * Whether to ignore unrecognized extensions in a directory. If a file extension is not present in the
         * [contentParserFactory], if this property is set to `true`, the file will be ignored. If it is set to `false`, an
         * [IllegalArgumentException] will be thrown.
         *
         * **NOTE:** This property only affects directories, not files, trying to directly load an unsupported file will
         * always throw an [IllegalArgumentException] (e.g. `path("file.unknown")`).
         *
         * default: `true`
         *
         * @return whether to ignore unrecognized extensions in a directory
         */
        var ignoreUnrecognizedExtensionsInDirectory: Boolean

        /**
         * Adds a path to the builder If the path is a directory, all files in it will be loaded (one level of depth,
         * inner directories are ignored). If the path is a file, it will be loaded.
         *
         * The locale of the file will be parsed from the file name, using the [Locale.forLanguageTag] method. If the
         * file's name is not a valid locale identifier, an [IllegalArgumentException] will be thrown.
         *
         * @param path the path to load
         * @param charset the charset to use when reading the file
         * @return this builder
         *
         * @throws IllegalArgumentException if the [path] does not exist
         * @throws IllegalArgumentException if the extension is not supported by any [ContentParser]
         * @throws IllegalArgumentException if the file name is not a valid locale identifier
         * @throws IOException if an I/O error occurs when trying to read the file
         * @throws IllegalStateException if the file contains a duplicated key and the [DuplicatedKeyResolution] is set
         * to [DuplicatedKeyResolution.FAIL]
         */
        fun path(path: Path, charset: Charset): Content

        /**
         * Adds a path to the builder. If the path is a directory, all files in it will be loaded (one level of depth,
         * inner directories are ignored). If the path is a file, it will be loaded.
         *
         * The locale of the file will be parsed from the file name, using the [Locale.forLanguageTag] method. If the
         * file's name is not a valid locale identifier, an [IllegalArgumentException] will be thrown.
         *
         * This method uses the [defaultCharset] to read the file.
         *
         * @param path the path to load
         * @return this builder
         *
         * @throws IllegalArgumentException if the [path] does not exist
         * @throws IllegalArgumentException if the extension is not supported by any [ContentParser]
         * @throws IllegalArgumentException if the file name is not a valid locale identifier
         * @throws IOException if an I/O error occurs when trying to read the file
         * @throws IllegalStateException if the file contains a duplicated key and the [DuplicatedKeyResolution] is set
         * to [DuplicatedKeyResolution.FAIL]
         * @see path
         */
        fun path(path: Path): Content = path(path, defaultCharset)

        /**
         * Adds a file to the builder. If the path is a directory, all files in it will be loaded (one level of depth,
         * inner directories are ignored). If the path is a file, it will be loaded.
         *
         * The locale of the file will be parsed from the file name, using the [Locale.forLanguageTag] method. If the
         * file's name is not a valid locale identifier, an [IllegalArgumentException] will be thrown.
         *
         * This method converts the [file] to a [Path] and calls [path].
         *
         * @param file the file to load
         * @param charset the charset to use when reading the file
         * @return this builder
         *
         * @throws IllegalArgumentException if the [file] does not exist
         * @throws IllegalArgumentException if the extension is not supported by any [ContentParser]
         * @throws IllegalArgumentException if the file name is not a valid locale identifier
         * @throws IOException if an I/O error occurs when trying to read the file
         * @throws IllegalStateException if the file contains a duplicated key and the [DuplicatedKeyResolution] is set
         * to [DuplicatedKeyResolution.FAIL]
         * @see path
         */
        fun file(file: File, charset: Charset): Content = path(file.toPath(), charset)

        /**
         * Adds a file to the builder. If the path is a directory, all files in it will be loaded (one level of depth,
         * inner directories are ignored). If the path is a file, it will be loaded.
         *
         * The locale of the file will be parsed from the file name, using the [Locale.forLanguageTag] method. If the
         * file's name is not a valid locale identifier, an [IllegalArgumentException] will be thrown.
         *
         * This method uses the [defaultCharset] to read the file.
         *
         * This method converts the [file] to a [Path] and calls [path].
         *
         * @param file the file to load
         * @return this builder
         *
         * @throws IllegalArgumentException if the [file] does not exist
         * @throws IllegalArgumentException if the extension is not supported by any [ContentParser]
         * @throws IllegalArgumentException if the file name is not a valid locale identifier
         * @throws IOException if an I/O error occurs when trying to read the file
         * @throws IllegalStateException if the file contains a duplicated key and the [DuplicatedKeyResolution] is set
         * to [DuplicatedKeyResolution.FAIL]
         * @see path
         */
        fun file(file: File): Content = file(file, defaultCharset)

        /**
         * Adds a URI to the builder. If the URI points to a directory, all files in it will be loaded (one level of
         * depth, inner directories are ignored). If the URI points to a file, it will be loaded.
         *
         * The locale of the file will be parsed from the file name, using the [Locale.forLanguageTag] method. If the
         * file's name is not a valid locale identifier, an [IllegalArgumentException] will be thrown.
         *
         * This method converts the [uri] to a [Path] and calls [path].
         *
         * @param uri the URI to load
         * @param charset the charset to use when reading the file
         * @return this builder
         *
         * @throws URISyntaxException if the URI is not formatted strictly according to RFC2396 and cannot be converted
         * to a URI
         * @throws FileSystemNotFoundException if an exception occurs when trying to convert the URI to a [Path]
         * @throws IllegalArgumentException if the [uri] does not exist
         * @throws IllegalArgumentException if the extension is not supported by any [ContentParser]
         * @throws IllegalArgumentException if the file name is not a valid locale identifier
         * @throws IOException if an I/O error occurs when trying to read the file
         * @throws IllegalStateException if the file contains a duplicated key and the [DuplicatedKeyResolution] is set
         * to [DuplicatedKeyResolution.FAIL]
         * @see path
         */
        fun uri(uri: URI, charset: Charset): Content = path(Path.of(uri), charset)

        /**
         * Adds a URI to the builder. If the URI points to a directory, all files in it will be loaded (one level of
         * depth, inner directories are ignored). If the URI points to a file, it will be loaded.
         *
         * The locale of the file will be parsed from the file name, using the [Locale.forLanguageTag] method. If the
         * file's name is not a valid locale identifier, an [IllegalArgumentException] will be thrown.
         *
         * This method uses the [defaultCharset] to read the file.
         *
         * This method converts the [uri] to a [Path] and calls [path].
         *
         * @param uri the URI to load
         * @return this builder
         *
         * @throws URISyntaxException if the URI is not formatted strictly according to RFC2396 and cannot be converted
         * to a URI
         * @throws FileSystemNotFoundException if an exception occurs when trying to convert the URI to a [Path]
         * @throws IllegalArgumentException if the [uri] does not exist
         * @throws IllegalArgumentException if the extension is not supported by any [ContentParser]
         * @throws IllegalArgumentException if the file name is not a valid locale identifier
         * @throws IOException if an I/O error occurs when trying to read the file
         * @throws IllegalStateException if the file contains a duplicated key and the [DuplicatedKeyResolution] is set
         * to [DuplicatedKeyResolution.FAIL]
         * @see path
         */
        fun uri(uri: URI): Content = uri(uri, defaultCharset)

        /**
         * Adds a URL to the builder. If the URL points to a directory, all files in it will be loaded (one level of
         * depth, inner directories are ignored). If the URL points to a file, it will be loaded.
         *
         * The locale of the file will be parsed from the file name, using the [Locale.forLanguageTag] method. If the
         * file's name is not a valid locale identifier, an [IllegalArgumentException] will be thrown.
         *
         * This method converts the [url] to a [URI] and then to a [Path] and calls [path].
         *
         * @param url the URL to load
         * @param charset the charset to use when reading the file
         * @return this builder
         *
         * @throws URISyntaxException if the URL is not formatted strictly according to RFC2396 and cannot be converted
         * to a URI
         * @throws FileSystemNotFoundException if an exception occurs when trying to convert the URL to a [Path]
         * @throws IllegalArgumentException if the [url] does not exist
         * @throws IllegalArgumentException if the extension is not supported by any [ContentParser]
         * @throws IllegalArgumentException if the file name is not a valid locale identifier
         * @throws IOException if an I/O error occurs when trying to read the file
         * @throws IllegalStateException if the file contains a duplicated key and the [DuplicatedKeyResolution] is set
         * to [DuplicatedKeyResolution.FAIL]
         * @see path
         */
        fun url(url: URL, charset: Charset): Content = path(Path.of(url.toURI()), charset)

        /**
         * Adds a URL to the builder. If the URL points to a directory, all files in it will be loaded (one level of
         * depth, inner directories are ignored). If the URL points to a file, it will be loaded.
         *
         * The locale of the file will be parsed from the file name, using the [Locale.forLanguageTag] method. If the
         * file's name is not a valid locale identifier, an [IllegalArgumentException] will be thrown.
         *
         * This method uses the [defaultCharset] to read the file.
         *
         * This method converts the [url] to a [URI] and then to a [Path] and calls [path].
         *
         * @param url the URL to load
         * @return this builder
         *
         * @throws URISyntaxException if the URL is not formatted strictly according to RFC2396 and cannot be converted
         * to a URI
         * @throws FileSystemNotFoundException if an exception occurs when trying to convert the URL to a [Path]
         * @throws IllegalArgumentException if the [url] does not exist
         * @throws IllegalArgumentException if the extension is not supported by any [ContentParser]
         * @throws IllegalArgumentException if the file name is not a valid locale identifier
         * @throws IOException if an I/O error occurs when trying to read the file
         * @throws IllegalStateException if the file contains a duplicated key and the [DuplicatedKeyResolution] is set
         * to [DuplicatedKeyResolution.FAIL]
         * @see path
         */
        fun url(url: URL): Content = url(url, defaultCharset)

        /**
         * Adds a resource to the builder. The resource is loaded using the class loader of the [resourceClass]
         * parameter and the [resourcePath] parameter. If the resource is not found, an [IllegalArgumentException] will
         * be thrown. It cannot contain a reference to a parent directory (`..`) or an [IllegalArgumentException] will
         * be thrown.
         *
         * The [resourcePath] can represent a file or a directory. If the resource is a directory, all files in it will
         * be loaded (one level of depth, inner directories are ignored). If the resource is a file, it will be loaded.
         *
         * This method resolves the resource path in the same way as [getResourceAsStream][Class.getResourceAsStream].
         * If the provided [resourcePath] is absolute (i.e. it starts with a `/`), it will be resolved as an absolute
         * path without further modification. If the provided [resourcePath] is relative, it will be resolved relative
         * to the package of the [resourceClass] parameter, with dots (`.`) replaced by slashes (`/`).
         *
         * Here is an example to illustrate the resolution:
         * ```
         * package com.example
         *
         * object Example {
         *     fun test() {
         *         // This will resolve to the resource `/com/example/example.yml`
         *         Translator {
         *             translations {
         *                 resource("example.yml", Example::class.java, Charsets.UTF_8)
         *             }
         *         }
         *
         *         // This will resolve to the resource `/example.yml` because it is an absolute path
         *         Translator {
         *             translations {
         *                 resource("/example.yml", Example::class.java, Charsets.UTF_8)
         *             }
         *         }
         *     }
         * }
         * ```
         *
         * The locale of the file will be parsed from the file name, using the [Locale.forLanguageTag] method. If the
         * file's name is not a valid locale identifier, an [IllegalStateException] will be thrown when building the
         * translator.
         *
         * @param resourcePath the path of the resource to load
         * @param resourceClass the class to use to load the resource (defaults to [TranslatorBuilder] class)
         * @param charset the charset to use when reading the file
         * @return this builder
         *
         * @throws IllegalArgumentException if the [resourcePath] does not exist
         * @throws IllegalArgumentException if the [resourcePath] contains a parent directory reference (`..`)
         * @throws IllegalArgumentException if the extension is not supported by any [ContentParser]
         * @throws IllegalArgumentException if the file name is not a valid locale identifier
         * @throws IOException if an I/O error occurs when trying to read the file
         * @throws IllegalStateException if the file contains a duplicated key and the [DuplicatedKeyResolution] is set
         * to [DuplicatedKeyResolution.FAIL]
         */
        fun resource(resourcePath: String, resourceClass: Class<*>, charset: Charset): Content

        /**
         * Adds a resource to the builder. The resource is loaded using the class loader of the [resourceClass]
         * parameter and the [resourcePath] parameter. If the resource is not found, an [IllegalArgumentException] will
         * be thrown. It cannot contain a reference to a parent directory (`..`) or an [IllegalArgumentException] will
         * be thrown.
         *
         * The [resourcePath] can represent a file or a directory. If the resource is a directory, all files in it will
         * be loaded (one level of depth, inner directories are ignored). If the resource is a file, it will be loaded.
         *
         * This method resolves the resource path in the same way as [getResourceAsStream][Class.getResourceAsStream].
         * If the provided [resourcePath] is absolute (i.e. it starts with a `/`), it will be resolved as an absolute
         * path without further modification. If the provided [resourcePath] is relative, it will be resolved relative
         * to the package of the [resourceClass] parameter, with dots (`.`) replaced by slashes (`/`).
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
         * The locale of the file will be parsed from the file name, using the [Locale.forLanguageTag] method. If the
         * file's name is not a valid locale identifier, an [IllegalStateException] will be thrown when building the
         * translator.
         *
         * This method uses the [defaultCharset] to read the file.
         *
         * @param resourcePath the path of the resource to load
         * @param resourceClass the class to use to load the resource (defaults to [TranslatorBuilder] class)
         * @return this builder
         *
         * @throws IllegalArgumentException if the [resourcePath] does not exist
         * @throws IllegalArgumentException if the [resourcePath] contains a parent directory reference (`..`)
         * @throws IllegalArgumentException if the extension is not supported by any [ContentParser]
         * @throws IllegalArgumentException if the file name is not a valid locale identifier
         * @throws IOException if an I/O error occurs when trying to read the file
         * @throws IllegalStateException if the file contains a duplicated key and the [DuplicatedKeyResolution] is set
         * to [DuplicatedKeyResolution.FAIL]
         * @see resource
         */
        fun resource(resourcePath: String, resourceClass: Class<*>): Content =
            resource(resourcePath, resourceClass, defaultCharset)

        /**
         * Adds a resource to the builder. The resource is loaded using the class loader of the class calling this
         * method and the [resourcePath] parameter. If the resource is not found, an [IllegalArgumentException] will be
         * thrown. It cannot contain a reference to a parent directory (`..`) or an [IllegalArgumentException] will be
         * thrown.
         *
         * The [resourcePath] can represent a file or a directory. If the resource is a directory, all files in it will
         * be loaded (one level of depth, inner directories are ignored). If the resource is a file, it will be loaded.
         *
         * This method resolves the resource path in the same way as [getResourceAsStream][Class.getResourceAsStream].
         * If the provided [resourcePath] is absolute (i.e. it starts with a `/`), it will be resolved as an absolute
         * without further modification. If the provided [resourcePath] is relative, it will be resolved relative to the
         * package of the **class calling this method**, with dots (`.`) replaced by slashes (`/`).
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
         * The locale of the file will be parsed from the file name, using the [Locale.forLanguageTag] method. If the
         * file's name is not a valid locale identifier, an [IllegalStateException] will be thrown when building the
         * translator.
         *
         * @param resourcePath the path of the resource to load
         * @param charset the charset to use when reading the file
         * @return this builder
         *
         * @throws IllegalArgumentException if the [resourcePath] does not exist
         * @throws IllegalArgumentException if the extension is not supported by any [ContentParser]
         * @throws IllegalArgumentException if the file name is not a valid locale identifier
         * @throws IOException if an I/O error occurs when trying to read the file
         * @throws IllegalStateException if the file contains a duplicated key and the [DuplicatedKeyResolution] is set
         * to [DuplicatedKeyResolution.FAIL]
         * @see resource
         */
        fun resource(resourcePath: String, charset: Charset): Content = resource(resourcePath, caller, charset)

        /**
         * Adds a resource to the builder. The resource is loaded using the class loader of the class calling this
         * method and the [resourcePath] parameter. If the resource is not found, an [IllegalArgumentException] will be
         * thrown. It cannot contain a reference to a parent directory (`..`) or an [IllegalArgumentException] will be
         * thrown.
         *
         * The [resourcePath] can represent a file or a directory. If the resource is a directory, all files in it will
         * be loaded (one level of depth, inner directories are ignored). If the resource is a file, it will be loaded.
         *
         * This method resolves the resource path in the same way as [getResourceAsStream][Class.getResourceAsStream].
         * If the provided [resourcePath] is absolute (i.e. it starts with a `/`), it will be resolved as an absolute
         * path without further modification. If the provided [resourcePath] is relative, it will be resolved relative
         * to the package of the **class calling this method**, with dots (`.`) replaced by slashes (`/`).
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
         * The locale of the file will be parsed from the file name, using the [Locale.forLanguageTag] method. If the
         * file's name is not a valid locale identifier, an [IllegalStateException] will be thrown when building the
         * translator.
         *
         * This method uses the [defaultCharset] to read the file.
         *
         * @param resourcePath the path of the resource to load
         * @return this builder
         *
         * @throws IllegalArgumentException if the [resourcePath] does not exist
         * @throws IllegalArgumentException if the extension is not supported by any [ContentParser]
         * @throws IllegalArgumentException if the file name is not a valid locale identifier
         * @throws IOException if an I/O error occurs when trying to read the file
         * @throws IllegalStateException if the file contains a duplicated key and the [DuplicatedKeyResolution] is set
         * to [DuplicatedKeyResolution.FAIL]
         * @see resource
         */
        fun resource(resourcePath: String): Content = resource(resourcePath, caller, defaultCharset)

        /**
         * Adds a map for a locale to the builder. The content of the map will be added to the final translator. The
         * [locale] parameter will be used as the locale of the map.
         *
         * **NOTE**: The [map] should follow rules defined in the [TranslationSourceMap] documentation. Any map that
         * does not follow these rules will result in an [IllegalArgumentException] being thrown.
         *
         * @param locale the locale of the map
         * @param map the map to add
         * @return this builder
         *
         * @throws IllegalArgumentException if the map does not follow the rules defined in the [TranslationSourceMap]
         * @throws IllegalStateException if the map contains a duplicated key and the [DuplicatedKeyResolution] is set
         * to [DuplicatedKeyResolution.FAIL]
         */
        fun map(locale: Locale, map: TranslationSourceMap): Content

        /**
         * Adds a map of locales to the builder. The content of the maps will be added to the final translator. The keys
         * of the [maps] will be used as the locales of the maps. The values of the [maps] will be used as the content
         * of the translation maps for the corresponding locales.
         *
         * **NOTE**: The [maps] should follow rules defined in the [TranslationSourceMap] documentation. Any map that
         * does not follow these rules will result in an [IllegalArgumentException] being thrown.
         *
         * @param maps the maps to add
         * @return this builder
         *
         * @throws IllegalArgumentException if any map does not follow the rules defined in the [TranslationSourceMap]
         * @throws IllegalStateException if any map contains a duplicated key and the [DuplicatedKeyResolution] is set
         * to [DuplicatedKeyResolution.FAIL]
         * @see map
         */
        fun maps(maps: Map<Locale, TranslationSourceMap>): Content = apply {
            maps.forEach { (locale, map) -> map(locale, map) }
        }

    }

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
     * Simple type representing a content parser. A content parser is a function that takes a string as input and
     * parses it into a [TranslationSourceMap]. It is usually used to parse the content of a file into a map
     * [TranslationSourceMap]
     */
    fun interface ContentParser {

        /**
         * Loads the content of a file into a [TranslationSourceMap].
         *
         * @param content the content of the file
         * @return the parsed content
         */
        fun load(content: String): TranslationSourceMap

    }

    /**
     * Policy to use when a key is not found when using a [Translator.t] method.
     */
    enum class MissingKeyPolicy {

        /**
         * Throw an [IllegalArgumentException] when a key is not found.
         */
        THROW_EXCEPTION,

        /**
         * Return the given key itself when a key is not found.
         */
        RETURN_KEY,

        ;

    }

    /**
     * Delimiter to use for interpolation in translations.
     */
    class InterpolationDelimiter @PackagePrivate internal constructor(
        internal val inner: VariableDelimiter,
    ) {

        companion object {

            /**
             * Creates an [InterpolationDelimiter] using the given [start] and [end] delimiters.
             *
             * The delimiters cannot contain the following characters: `\`, `(`, `)`, `:`. Trying to create a delimiter
             * containing one of these characters will throw an [IllegalStateException].
             *
             * @param start the start delimiter
             * @param end the end delimiter
             * @return the created [InterpolationDelimiter]
             * @throws IllegalStateException if the delimiters contain forbidden characters
             */
            @JvmStatic
            fun create(start: String, end: String): InterpolationDelimiter {
                val inner = VariableDelimiter.create(start, end)
                val ch = forbiddenChars()
                val forbiddenChars = ch.joinToString("", "[^", "]*") { it.toString() }
                    .toRegex()
                check(forbiddenChars.matches(inner.startDelimiter)) {
                    "Start delimiter cannot contain the following characters: ${ch.contentToString()}, but was '${inner.startDelimiter}'"
                }
                check(forbiddenChars.matches(inner.endDelimiter)) {
                    "End delimiter cannot contain the following characters: ${ch.contentToString()}, but was '${inner.endDelimiter}'"
                }
                return InterpolationDelimiter(inner)
            }

            private fun forbiddenChars(): CharArray = charArrayOf('\\', '(', ')', ':')

        }
    }

    companion object {

        /**
         * The default content parsers used when creating a translator.
         *
         * By default, this factory contains loaders for:
         * - JSON (`.json` files)
         * - YAML (`.yaml` and `.yml` files)
         * - TOML (`.toml` files)
         *
         * All loaders in this factory rely on optional external libraries which are not included by default. If you
         * want to use these loaders, you must include the corresponding dependencies in your project.
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
         * @return the default content parsers factory
         */
        @JvmStatic
        fun defaultContentLoaders(): (String) -> ContentParser? {
            val yamlLoader = BuiltinContentParsers.yamlParser()
            val map = unmodifiableMapOf(
                "json" to BuiltinContentParsers.jsonParser(),
                "yaml" to yamlLoader,
                "yml" to yamlLoader,
                "toml" to BuiltinContentParsers.tomlParser(),
            )
            return { map[it]?.invoke() }
        }

        private val Content.caller: Class<*>
            get() = (this as ContentImpl).caller

    }

    /**
     * Marker annotation for the DSL of the [TranslatorBuilder].
     *
     * @see TranslatorBuilder
     */
    @DslMarker
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.BINARY)
    annotation class TranslatorBuilderDsl

}
