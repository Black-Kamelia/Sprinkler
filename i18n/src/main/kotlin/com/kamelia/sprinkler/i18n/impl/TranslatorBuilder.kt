package com.kamelia.sprinkler.i18n.impl

import com.kamelia.sprinkler.i18n.FunctionAdapter
import com.kamelia.sprinkler.i18n.Translator
import com.kamelia.sprinkler.i18n.formatting.VariableFormatter
import com.kamelia.sprinkler.i18n.impl.TranslatorBuilder.Companion.defaultContentParsers
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
import java.net.URL
import java.nio.charset.Charset
import java.nio.file.Path
import java.util.Locale
import java.util.function.Consumer
import java.util.function.Function


/**
 * Builder class used to create a [Translator]. This class provides several methods to add data to the translator from
 * different sources.
 *
 * It can be used in a DSL through the [Translator][com.kamelia.sprinkler.i18n.impl.Translator] method, to create a new
 * [Translator] configure with the provided DSL content. The DSL is divided in three parts:
 *
 * - The configuration available directly through the [TranslatorBuilder] interface, which allows to configure general
 * aspects of the translator.
 *
 *
 * - The [configuration] block, which allows to configure the behavior of the built [Translator], like for example the
 * [interpolationDelimiter][Configuration.interpolationDelimiter] defining the delimiters used for interpolation
 * variables in strings (e.g. `{` and `}` in the string `"Hello mister {name}!"`).
 *
 *
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
 *
 *
 * - Values passed to this builder are all validated on building, to ensure that the potential variables used in the
 * string respect the [TranslationInterpolationVariable] rules. If a value does not respect these rules, an exception
 * will be thrown when adding the value to the builder.
 *
 *
 * - The content of the files is parsed using [ContentLoaders][ContentParser]. Loaders are provided by a map associating
 * file extensions to content parsers. This map is passed to the builder when creating it. If a file extension is not
 * present in the map, an [IllegalArgumentException] will be thrown. Default content parsers exist but require
 * additional dependencies to be added to the project. For more information, see the [defaultContentParsers] method.
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
@Suppress("INAPPLICABLE_JVM_NAME")
sealed interface TranslatorBuilder {

    /**
     * Whether to check that all locales have the same keys when building the translator. If set to `true`, and at least
     * two locales have different keys, an [IllegalStateException] will be thrown when building the translator.
     *
     * default: `false`
     */
    var checkMissingKeysOnBuild: Boolean

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
    fun configuration(block: Consumer<Configuration>): Unit = configuration { block.accept(this) }

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
    fun translations(block: Consumer<Content>): Unit = translations { block.accept(this) }

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
         * The function associating a locale to a [PluralMapper] used by the created [Translator].
         *
         * default: [PluralMapper.builtins]
         */
        @HideFromJava
        @get:JvmName("getPluralMapperFactoryKt")
        @set:JvmName("setPluralMapperFactoryKt")
        var pluralMapperFactory: (Locale) -> PluralMapper

        /**
         * The function associating a locale to a [PluralMapper] used by the created [Translator].
         *
         * default: [PluralMapper.builtins][PluralMapper.builtinsJava]
         */
        @HideFromKotlin
        @get:JvmName("getPluralMapperFactory")
        @set:JvmName("setPluralMapperFactory")
        var pluralMapperFactoryJava: Function<Locale, PluralMapper>

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
         * The default locale to use when no locale is specified when calling the [Translator.t] method. It can be set
         * to `null` to prevent the use of a default locale.
         *
         * default: [Locale.ENGLISH]
         */
        var defaultLocale: Locale?

        /**
         * The [currentLocale][Translator.currentLocale] of the built [Translator]. If set to `null`, the
         * [defaultLocale] will be used, and if it is also `null`, the [Locale.ENGLISH] will be used.
         *
         * default: `null`
         */
        var currentLocale: Locale?

        /**
         * The function to use to reduce the specialization of a locale. This function is used to reduce the specificity
         * of a locale to a more general one. For example, if the locale is `en_US`, the function could return `en` to
         * use the general English translation. If a locale is already the most general one, the function should return
         * `null`.
         *
         * This function is used when a translation is not found for a specific locale, to try to find a translation for
         * a more general locale. If this function returns `null` before finding a translation, the translator will then
         * try to use the [defaultLocale].
         *
         * default: [defaultLocaleSpecializationReduction]
         */
        @HideFromJava
        @get:JvmName("getLocaleSpecializationReductionKt")
        @set:JvmName("setLocaleSpecializationReductionKt")
        var localeSpecializationReduction: (Locale) -> Locale?

        /**
         * The function to use to reduce the specialization of a locale. This function is used to reduce the specificity
         * of a locale to a more general one. For example, if the locale is `en_US`, the function could return `en` to
         * use the general English translation. If a locale is already the most general one, the function should return
         * `null`.
         *
         * This function is used when a translation is not found for a specific locale, to try to find a translation for
         * a more general locale. If this function returns `null` before finding a translation, the translator will then
         * try to use the [defaultLocale].
         *
         * default: [defaultLocaleSpecializationReductionJava]
         */
        @HideFromKotlin
        @get:JvmName("getLocaleSpecializationReduction")
        @set:JvmName("setLocaleSpecializationReduction")
        var localeSpecializationReductionJava: Function<Locale, Locale?>

    }

    /**
     * Type defining the content to load into the built [Translator]. This type provides several methods to load file
     * contents into the built [Translator].
     *
     * The files are loaded using the two properties [contentParsers] and [localeParser]:
     * - When a file is provided, the [Locale] it represents is determined by the [localeParser] function, which is
     * called with the filename without extension.
     *
     * - The content of the file is then parsed using the [ContentParser] associated with the file extension. The parser
     * is determined by the [contentParsers] function, which is called with the file extension (for more details
     * about how an unrecognized extension is handled, see [contentParsers] documentation).
     *
     * It is important to note that in the context of the [translations] method, the content of the DSL is executed
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
         * The map a type of file (using its extension) to a [ContentParser] used to parse the content of the file
         * (e.g. the `json` key is associated with a [ContentParser] that parses JSON files).
         *
         * If an extension is not present in this map, here are the possible cases:
         *
         * - If the missing extension has been required due to a file being present in a loaded folder (e.g.
         * `path(Path.of("myFolder/"))` has been called in the ds, and it contains an `en_US.unknown` file), if the
         * [ignoreUnrecognizedExtensionsInDirectory] property is set to `true`, the file will be ignored. If it is set
         * to `false`, an [IllegalArgumentException] will be thrown.
         *
         *
         * - If the missing extension has been required due to a specific file being explicitly loaded (e.g.
         * `path(Path.of("en_US.unknown"))` has been called in the DSL), an [IllegalArgumentException] will always be
         * thrown.
         *
         * default: [defaultContentParsers]
         */
        var contentParsers: Map<String, ContentParser>

        /**
         * The resolution to use when a duplicated key is found.
         *
         * default: [DuplicatedKeyResolution.FAIL]
         */
        var duplicatedKeyResolution: DuplicatedKeyResolution

        /**
         * The default charset to use when reading files.
         *
         * default: [Charset.defaultCharset]
         */
        var defaultCharset: Charset

        /**
         * The function to use to parse a locale from a string.
         *
         * Each file loaded by [Content] will have its filename parsed by this function to determine the locale of the
         * file. cOnly the **filename without extension** is passed to this function (e.g. `path/to/en_US.yml` will pass
         * `en_US`).
         *
         * default: [Locale.Builder.setLanguageTag]
         */
        @HideFromJava
        @get:JvmName("getLocaleParserKt")
        @set:JvmName("setLocaleParserKt")
        var localeParser: (String) -> Locale

        /**
         * The function to use to parse a locale from a string.
         *
         * Each file loaded by [Content] will have its filename parsed by this function to determine the locale of the
         * file. Only the **filename without extension** is passed to this function (e.g. `path/to/en_US.yml` will pass
         * `en_US`).
         *
         * default: [Locale.Builder.setLanguageTag]
         */
        @HideFromKotlin
        @get:JvmName("getLocaleParser")
        @set:JvmName("setLocaleParser")
        var localeParserJava: Function<String, Locale>

        /**
         * Whether to ignore unrecognized extensions in a directory. If a file extension is not present in the
         * [contentParsers], if this property is set to `true`, the file will be ignored. If it is set to `false`, an
         * [IllegalArgumentException] will be thrown.
         *
         * **NOTE:** This property only affects directories, not files, trying to directly load an unsupported file will
         * always throw an [IllegalArgumentException] (e.g. `path("file.unknown")`).
         *
         * default: `true`
         */
        var ignoreUnrecognizedExtensionsInDirectory: Boolean

        /**
         * Adds a file to the builder If the path is a directory, all files in it will be loaded (one level of depth,
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
        fun file(path: Path, charset: Charset)

        /**
         * Adds a file to the builder. If the path is a directory, all files in it will be loaded (one level of depth,
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
        fun file(path: Path): Unit = file(path, defaultCharset)

        /**
         * Adds a resource to the builder. The resource is loaded using the class loader of the [resourceClass]
         * parameter and the [path] parameter. If the resource is not found, an [IllegalArgumentException] will
         * be thrown. It cannot contain a reference to a parent directory (`..`) or an [IllegalArgumentException] will
         * be thrown.
         *
         * The [path] can represent a file or a directory. If the resource is a directory, all files in it will
         * be loaded (one level of depth, inner directories are ignored). If the resource is a file, it will be loaded.
         *
         * This method resolves the resource path in the same way as [getResourceAsStream][Class.getResourceAsStream].
         * If the provided [path] is absolute (i.e. it starts with a `/`), it will be resolved as an absolute
         * path without further modification. If the provided [path] is relative, it will be resolved relative
         * to the package of the [resourceClass] parameter, with dots (`.`) replaced by slashes (`/`).
         *
         * Here is an example to illustrate the resolution:
         * ```
         * package com.example
         *
         * object Example {
         *     fun test() {
         *         Translator {
         *             translations {
         *                 // This will resolve to the resource `/com/example/example.yml`
         *                 resource("example.yml", Example::class.java, Charsets.UTF_8)
         *
         *                 // This will resolve to the resource `/example.yml` because it is an absolute path
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
         * @param path the path of the resource to load
         * @param resourceClass the class to use to load the resource (defaults to [TranslatorBuilder] class)
         * @param charset the charset to use when reading the file
         * @return this builder
         *
         * @throws IllegalArgumentException if the [path] does not exist
         * @throws IllegalArgumentException if the [path] contains a parent directory reference (`..`)
         * @throws IllegalArgumentException if the extension is not supported by any [ContentParser]
         * @throws IllegalArgumentException if the file name is not a valid locale identifier
         * @throws IOException if an I/O error occurs when trying to read the file
         * @throws IllegalStateException if the file contains a duplicated key and the [DuplicatedKeyResolution] is set
         * to [DuplicatedKeyResolution.FAIL]
         */
        fun resource(path: String, resourceClass: Class<*>, charset: Charset)

        /**
         * Adds a resource to the builder. The resource is loaded using the class loader of the [resourceClass]
         * parameter and the [path] parameter. If the resource is not found, an [IllegalArgumentException] will
         * be thrown. It cannot contain a reference to a parent directory (`..`) or an [IllegalArgumentException] will
         * be thrown.
         *
         * The [path] can represent a file or a directory. If the resource is a directory, all files in it will
         * be loaded (one level of depth, inner directories are ignored). If the resource is a file, it will be loaded.
         *
         * This method resolves the resource path in the same way as [getResourceAsStream][Class.getResourceAsStream].
         * If the provided [path] is absolute (i.e. it starts with a `/`), it will be resolved as an absolute
         * path without further modification. If the provided [path] is relative, it will be resolved relative
         * to the package of the [resourceClass] parameter, with dots (`.`) replaced by slashes (`/`).
         *
         * Here is an example to illustrate the resolution:
         * ```
         * package com.example
         *
         * object Example {
         *     fun test() {
         *         Translator {
         *             translations {
         *                 // This will resolve to the resource `/com/example/example.yml`
         *                 resource("example.yml", Example::class.java)
         *
         *                 // This will resolve to the resource `/example.yml` because it is an absolute path
         *                 resource("/example.yml", Example::class.java)
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
         * This method uses the [defaultCharset] to read the file.
         *
         * @param path the path of the resource to load
         * @param resourceClass the class to use to load the resource (defaults to [TranslatorBuilder] class)
         * @return this builder
         *
         * @throws IllegalArgumentException if the [path] does not exist
         * @throws IllegalArgumentException if the [path] contains a parent directory reference (`..`)
         * @throws IllegalArgumentException if the extension is not supported by any [ContentParser]
         * @throws IllegalArgumentException if the file name is not a valid locale identifier
         * @throws IOException if an I/O error occurs when trying to read the file
         * @throws IllegalStateException if the file contains a duplicated key and the [DuplicatedKeyResolution] is set
         * to [DuplicatedKeyResolution.FAIL]
         * @see resource
         */
        fun resource(path: String, resourceClass: Class<*>): Unit =
            resource(path, resourceClass, defaultCharset)

        /**
         * Adds a resource to the builder. The resource is loaded using the class loader of the class calling this
         * method and the [path] parameter. If the resource is not found, an [IllegalArgumentException] will be
         * thrown. It cannot contain a reference to a parent directory (`..`) or an [IllegalArgumentException] will be
         * thrown.
         *
         * The [path] can represent a file or a directory. If the resource is a directory, all files in it will
         * be loaded (one level of depth, inner directories are ignored). If the resource is a file, it will be loaded.
         *
         * This method resolves the resource path in the same way as [getResourceAsStream][Class.getResourceAsStream].
         * If the provided [path] is absolute (i.e. it starts with a `/`), it will be resolved as an absolute
         * without further modification. If the provided [path] is relative, it will be resolved relative to the
         * package of the **class calling this method**, with dots (`.`) replaced by slashes (`/`).
         *
         * Here is an example to illustrate the resolution:
         * ```
         * package com.example
         *
         * object Example {
         *     fun test() {
         *         Translator {
         *             translations {
         *                 // This will resolve to the resource `/com/example/example.yml`
         *                 resource("example.yml", Charsets.UTF_8)
         *
         *                 // This will resolve to the resource `/example.yml` because it is an absolute path
         *                 resource("/example.yml", Charsets.UTF_8)
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
         * @param path the path of the resource to load
         * @param charset the charset to use when reading the file
         * @return this builder
         *
         * @throws IllegalArgumentException if the [path] does not exist
         * @throws IllegalArgumentException if the extension is not supported by any [ContentParser]
         * @throws IllegalArgumentException if the file name is not a valid locale identifier
         * @throws IOException if an I/O error occurs when trying to read the file
         * @throws IllegalStateException if the file contains a duplicated key and the [DuplicatedKeyResolution] is set
         * to [DuplicatedKeyResolution.FAIL]
         * @see resource
         */
        fun resource(path: String, charset: Charset): Unit = resource(path, caller, charset)

        /**
         * Adds a resource to the builder. The resource is loaded using the class loader of the class calling this
         * method and the [path] parameter. If the resource is not found, an [IllegalArgumentException] will be
         * thrown. It cannot contain a reference to a parent directory (`..`) or an [IllegalArgumentException] will be
         * thrown.
         *
         * The [path] can represent a file or a directory. If the resource is a directory, all files in it will
         * be loaded (one level of depth, inner directories are ignored). If the resource is a file, it will be loaded.
         *
         * This method resolves the resource path in the same way as [getResourceAsStream][Class.getResourceAsStream].
         * If the provided [path] is absolute (i.e. it starts with a `/`), it will be resolved as an absolute
         * path without further modification. If the provided [path] is relative, it will be resolved relative
         * to the package of the **class calling this method**, with dots (`.`) replaced by slashes (`/`).
         *
         * Here is an example to illustrate the resolution:
         * ```
         * package com.example
         *
         * object Example {
         *     fun test() {
         *         Translator {
         *             translations {
         *                 // This will resolve to the resource `/com/example/example.yml`
         *                 resource("example.yml")
         *
         *                 // This will resolve to the resource `/example.yml` because it is an absolute path
         *                 resource("/example.yml")
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
         * This method uses the [defaultCharset] to read the file.
         *
         * @param path the path of the resource to load
         * @return this builder
         *
         * @throws IllegalArgumentException if the [path] does not exist
         * @throws IllegalArgumentException if the extension is not supported by any [ContentParser]
         * @throws IllegalArgumentException if the file name is not a valid locale identifier
         * @throws IOException if an I/O error occurs when trying to read the file
         * @throws IllegalStateException if the file contains a duplicated key and the [DuplicatedKeyResolution] is set
         * to [DuplicatedKeyResolution.FAIL]
         * @see resource
         */
        fun resource(path: String): Unit = resource(path, caller, defaultCharset)

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
        fun map(locale: Locale, map: TranslationSourceMap)

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
        fun maps(maps: Map<Locale, TranslationSourceMap>): Unit = maps.forEach { (locale, map) -> map(locale, map) }

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
         * Parses the content of a file into a [TranslationSourceMap].
         *
         * @param content the content of the file
         * @return the parsed content
         */
        fun parse(content: String): TranslationSourceMap

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
     *
     * @see interpolationDelimiter
     * @see Configuration.interpolationDelimiter
     */
    sealed interface InterpolationDelimiter

    companion object {

        /**
         * The default content parsers used when creating a translator.
         *
         * By default, this map contains loaders for:
         * - JSON (`.json` files)
         * - YAML (`.yaml` and `.yml` files)
         * - TOML (`.toml` files)
         *
         * All loaders in this map rely on optional external libraries which are not included by default. If you
         * want to use these loaders, you must include the corresponding dependencies in your project.
         *
         * Here are the possible dependencies for each parser:
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
         * **NOTE**: You only need the dependencies for the parsers you want to use and only need to include one of
         * them. In case you include more than one, the order in which they are listed above is the order of precedence.
         *
         * **NOTE**: If you add some dependencies only to be able to use these parsers, you can set these dependencies
         * to `runtimeOnly` to avoid using them by mistake in your code and reduce the size of your compile classpath.
         *
         * @return the default [ContentParser]s map
         */
        @JvmStatic
        fun defaultContentParsers(): Map<String, ContentParser> {
            val yamlLoader = BuiltinContentParsers.yamlParser()
            return unmodifiableMapOf(
                "json", BuiltinContentParsers.jsonParser(),
                "yaml", yamlLoader,
                "yml", yamlLoader,
                "toml", BuiltinContentParsers.tomlParser(),
            )
        }

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
        fun interpolationDelimiter(start: String, end: String): InterpolationDelimiter {
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
            return InterpolationDelimiterImpl(inner)
        }

        /**
         * The default locale specialization reduction function. This function is used to reduce the specificity of a
         * locale to a more general one.
         *
         * The implementation of this function reduces the locale by removing parts of it in the following order:
         *
         * - Extensions
         * - Variant
         * - Country/Region
         * - Script
         *
         * Once all parts have been removed the locale should only contain the language. A call with a locale solely
         * containing the language will return `null`.
         *
         * @return the default locale specialization reduction function
         * @see [Configuration.localeSpecializationReduction]
         */
        @HideFromJava
        @JvmName("defaultPluralMapperFactoryKt")
        fun defaultLocaleSpecializationReduction(): (Locale) -> Locale? = internalLocaleSpecializationReduction()

        /**
         * The default locale specialization reduction function. This function is used to reduce the specificity of a
         * locale to a more general one.
         *
         * The implementation of this function reduces the locale by removing parts of it in the following order:
         *
         * - Extensions
         * - Variant
         * - Country/Region
         * - Script
         *
         * Once all parts have been removed the locale should only contain the language. A call with a locale solely
         * containing the language will return `null`.
         *
         * @return the default locale specialization reduction function
         * @see [Configuration.localeSpecializationReductionJava]
         */
        @HideFromKotlin
        @JvmName("defaultPluralMapperFactory")
        fun defaultLocaleSpecializationReductionJava(): Function<Locale, Locale?> =
            internalLocaleSpecializationReduction()

        @PackagePrivate
        internal fun internalLocaleSpecializationReduction(): FunctionAdapter<Locale, Locale?> =
            FunctionAdapter { locale ->
                when {
                    locale.hasExtensions() -> locale.stripExtensions()
                    locale.variant.isNotEmpty() -> Locale.Builder().setLocale(locale).setVariant("").build()
                    locale.country.isNotEmpty() -> Locale.Builder().setLocale(locale).setRegion("").build()
                    locale.script.isNotEmpty() -> Locale.Builder().setLocale(locale).setScript("").build()
                    else -> null
                }
            }

        private val Content.caller: Class<*>
            get() = (this as ContentImpl).caller

        private fun forbiddenChars(): CharArray = charArrayOf('\\', '(', ')', ':')

        @PackagePrivate
        internal class InterpolationDelimiterImpl(internal val inner: VariableDelimiter) : InterpolationDelimiter

        @PackagePrivate
        internal val InterpolationDelimiter.inner: VariableDelimiter
            get() = (this as InterpolationDelimiterImpl).inner

    }

    /**
     * Marker annotation for the DSL of the [TranslatorBuilder]. Any type annotated with this annotation is involved in
     * the DSL of the [TranslatorBuilder] and should only be used in this context. Any usage outside the
     * [TranslatorBuilder] DSL is not supported and may lead to unexpected behavior.
     *
     * @see TranslatorBuilder
     */
    @DslMarker
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.BINARY)
    annotation class TranslatorBuilderDsl

}
