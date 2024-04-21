package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.i18n.TranslatorBuilder.DuplicatedKeyResolution
import com.kamelia.sprinkler.util.ExtendedCollectors
import com.kamelia.sprinkler.util.assertionFailed
import java.io.File
import java.net.URI
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.util.IllformedLocaleException
import java.util.Locale
import java.util.TreeMap
import java.util.stream.Collectors
import org.intellij.lang.annotations.Language
import org.json.JSONException
import org.json.JSONObject
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.error.YAMLException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.io.path.exists
import kotlin.io.path.extension
import kotlin.io.path.isDirectory
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.readText

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
class TranslatorBuilder private constructor(
    private val defaultLocale: Locale,
    private val configuration: TranslatorConfiguration,
    private val throwOnMissingKey: Boolean,
    private val duplicatedKeyResolution: DuplicatedKeyResolution,
    private val valueFormattingCheckRegex: Regex,
    private val variableExtractionRegex: Regex,
) {

    private val content = TreeMap<Locale, HashMap<String, String>>(::compareLocale)

    /**
     * Adds a path to the builder.
     * If the path is a directory, all files in it will be loaded (one level of depth, inner directories are ignored).
     * If the path is a file, it will be loaded.
     *
     * Supported formats are JSON and YAML, with the following extensions: `json`, `yaml`, and `yml`.
     *
     * The locale of the file will be parsed from the file name, using the [Locale.forLanguageTag] method. If the file's
     * name is not a valid locale identifier, an [IllegalStateException] will be thrown when building the translator.
     *
     * This method will throw an [IllegalArgumentException] if the file extension is not supported.
     *
     * @param path the path to load
     * @throws IllegalArgumentException if the file extension is not supported
     */
    fun addFile(path: Path): TranslatorBuilder = apply {
        val extension = path.extension
        require("json" == extension || "yaml" == extension || "yml" == extension || path.isDirectory()) {
            "Unsupported file extension '$extension' for path '$path'. Supported extensions are 'json', 'yaml' and 'yml'."
        }

        try {
            // we need to load the file, or, if it is a directory, load all files in it and add them to the final map.
            loadPath(path).forEach { (locale, map) ->
                addToMap(locale, map)
            }
        } catch (e: Exception) { // catch and rethrow to add the path to the error message
            throw IllegalArgumentException("Error while loading file $path", e)
        }
    }

    /**
     * Adds a path to the builder.
     * If the path is a directory, all files in it will be loaded (one level of depth, inner directories are ignored).
     * If the path is a file, it will be loaded.
     *
     * Supported formats are JSON and YAML, with the following extensions: `json`, `yaml`, and `yml`.
     *
     * The locale of the file will be parsed from the file name, using the [Locale.forLanguageTag] method. If the file's
     * name is not a valid locale identifier, an [IllegalStateException] will be thrown when building the translator.
     *
     * This method will throw an [IllegalArgumentException] if the file extension is not supported.
     *
     * @param file the file to load
     * @return this builder
     * @throws IllegalArgumentException if the file extension is not supported
     */
    fun addFile(file: File): TranslatorBuilder = addFile(file.toPath())

    /**
     * Adds a URL to the builder. If the URL points to a directory, all files in it will be loaded (one level of depth,
     * inner directories are ignored). If the URL points to a file, it will be loaded.
     *
     * Supported formats are JSON and YAML, with the following extensions: `json`, `yaml`, and `yml`.
     *
     * The locale of the file will be parsed from the file name, using the [Locale.forLanguageTag] method. If the file's
     * name is not a valid locale identifier, an [IllegalStateException] will be thrown when building the translator.
     *
     * This method will throw an [IllegalArgumentException] if the file extension is not supported.
     *
     * @param url the URL to load
     * @return this builder
     * @throws IllegalArgumentException if the extension is not supported
     */
    fun addURL(url: URL): TranslatorBuilder = addFile(Path.of(url.toURI()))

    /**
     * Adds a URI to the builder. If the URI points to a directory, all files in it will be loaded (one level of depth,
     * inner directories are ignored). If the URI points to a file, it will be loaded.
     *
     * Supported formats are JSON and YAML, with the following extensions: `json`, `yaml`, and `yml`.
     *
     * The locale of the file will be parsed from the file name, using the [Locale.forLanguageTag] method. If the file's
     * name is not a valid locale identifier, an [IllegalStateException] will be thrown when building the translator.
     *
     * This method will throw an [IllegalArgumentException] if the file extension is not supported.
     *
     * @param uri the URI to load
     * @return this builder
     * @throws IllegalArgumentException if the extension is not supported
     */
    fun addURI(uri: URI): TranslatorBuilder = addFile(Path.of(uri))

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
    @JvmOverloads
    fun addResource(resourcePath: String, resourceClass: Class<*>? = null): TranslatorBuilder = apply {
        val clazz = resourceClass ?: javaClass
        // we first try to load the resource as a directory, if it is not a directory, we load it as a file
        val endsWithSlash = resourcePath.endsWith('/')
        val directoryPath = if (!endsWithSlash) {
            "$resourcePath/"
        } else {
            resourcePath
        }
        val dir = clazz.getResource(directoryPath)
        if (dir == null) { // the resource is not a directory
            // if the name was already a directory, it means that the user intended to load a directory, so we throw an
            // exception
            require(!endsWithSlash) { "Resource directory '$directoryPath' not found." }
            loadResourceFile(resourcePath, clazz, null)
            return@apply
        }
        // the resource is a directory, we load all files in it
        dir.openStream()
            .bufferedReader()
            .use { stream -> stream.lines().forEach { loadResourceFile(it, clazz, directoryPath) } }
    }

    /**
     * Adds a map for a locale to the builder. The content of the map will be added to the final translator. The
     * [locale] parameter will be used as the locale of the map.
     *
     * **NOTE**: The [map] should follow rules defined in the [TranslationSourceMap] documentation. Any map that does
     * not follow these rules will result in an [IllegalArgumentException] being thrown when building the translator.
     *
     * @param locale the locale of the map
     * @param map the map to add
     * @return this builder
     */
    fun addMap(locale: Locale, map: TranslationSourceMap): TranslatorBuilder = apply { addToMap(locale, map) }

    /**
     * Adds a map of locales to the builder. The content of the maps will be added to the final translator. The keys of
     * the [maps] will be used as the locales of the maps. The values of the [maps] will be used as the content of the
     * translation maps for the corresponding locales.
     *
     * **NOTE**: The [maps] should follow rules defined in the [TranslationSourceMap] documentation. Any map that does
     * not follow these rules will result in an [IllegalArgumentException] being thrown when building the translator.
     *
     * @param maps the maps to add
     * @return this builder
     */
    fun addMaps(maps: Map<Locale, TranslationSourceMap>): TranslatorBuilder = apply {
        maps.forEach { (locale, map) ->
            addMap(locale, map)
        }
    }

    /**
     * Defines how to handle duplicated keys when creating a translator.
     */
    enum class DuplicatedKeyResolution {

        /**
         * If a duplicated key is found, the build will fail. This policy will cause an [IllegalStateException] to be
         * thrown when [building][build] the [Translator].
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
     * Builds the translator using the data added to the builder.
     *
     * @return the created translator
     * TODO
     * @throws IllegalStateException if a duplicate key is found and the duplicate key resolution policy is set to
     * [DuplicatedKeyResolution.FAIL]
     * @throws IllegalStateException if a source has been added without following the rules defined in the method
     * documentation
     * @throws IllegalStateException if a source contains a key or a value that does not respect the rules defined in
     * [TranslationKey] (for the keys) and [TranslationSourceData] (for the values) documentation
     * @throws IllegalStateException if an error occurs while loading a file
     */
    fun build(): Translator {
        if (content.isEmpty()) {
            return TranslatorImpl(defaultLocale, TranslatorData(defaultLocale, emptyMap(), configuration))
        }
        val finalMap = LinkedHashMap<Locale, MutableMap<String, String>>(content.size)

        val comparator = keyComparator()
        val expectedKeys: Set<String> =
            content.firstEntry().value.keys // possible because we checked that content is not empty

        content.entries.forEach { entry ->
            val (locale, translations) = entry
            if (throwOnMissingKey) {
                check(expectedKeys == translations.keys) {
                    "Error in map ${translations.keys}:\nKeys are not the same as the first map added to the builder. All maps must have the same keys. To disable this check, use the ignoreMissingKeysOnBuild parameter when creating the builder."
                }
            }

            // we sort translations and add them to the final map
            finalMap[locale] = translations.entries
                .stream()
                .sorted { (a, _), (b, _) -> comparator.compare(a, b) }
                .collect(
                    ExtendedCollectors.toLinkedHashMapUsingEntries { _, _ ->
                        assertionFailed("Collisions should have been resolved")
                    }
                )
        }

        val data = TranslatorData(defaultLocale, finalMap, configuration)
        return TranslatorImpl(defaultLocale, data)
    }

    companion object {

        @JvmStatic
        @JvmOverloads
        fun create(
            defaultLocale: Locale = Locale.ENGLISH,
            configuration: TranslatorConfiguration = TranslatorConfiguration.builder().build(),
            ignoreMissingKeysOnBuild: Boolean = true,
            duplicatedKeyResolution: DuplicatedKeyResolution = DuplicatedKeyResolution.FAIL,
        ): TranslatorBuilder {
            val delimiter = configuration.interpolationDelimiter
            val checkRegex = translationValueFormatCheckRegex(delimiter.startDelimiter, delimiter.endDelimiter)
            val extractionRegex =
                formatAndVariableNamesExtractionRegex(delimiter.startDelimiter, delimiter.endDelimiter)
            return TranslatorBuilder(
                defaultLocale,
                configuration,
                !ignoreMissingKeysOnBuild,
                duplicatedKeyResolution,
                checkRegex,
                extractionRegex,
            )
        }

        /**
         * The key regex used to validate keys.
         *
         * @return the key regex
         */
        @JvmStatic
        fun keyRegex(): Regex = KEY_REGEX

        private val KEY_REGEX = """$IDENTIFIER(?:\.$IDENTIFIER)*""".toRegex()

        private fun loadPath(path: Path): List<Pair<Locale, TranslationSourceMap>> =
            when {
                !path.exists() -> error("Path $path does not exist")
                path.isDirectory() -> { // if the path is a directory, load all files in it and return the list
                    Files.list(path)
                        .map {
                            val locale = parseLocale(it.nameWithoutExtension)
                            val map = parseFile(it.readText(), it.extension)
                            locale to map
                        }
                        .toList()
                }
                else -> { // if the path is a file, load it and store it in a one element list
                    val map = parseFile(path.readText(), path.extension)
                    val locale = parseLocale(path.nameWithoutExtension)
                    listOf(locale to map)
                }
            }

        private fun parseFile(content: String, extension: String): TranslationSourceMap =
            when (extension) {
                "json" -> try {
                    JSONObject(content).toMap()
                } catch (e: JSONException) {
                    throw IllegalStateException("Invalid JSON file.", e)
                }
                "yaml", "yml" -> try {
                    Yaml().load<Map<TranslationKey, TranslationSourceData>>(content)
                } catch (e: YAMLException) {
                    throw IllegalStateException("Invalid YAML file.", e)
                }
                else -> assertionFailed("File extension '$extension' should have been checked before.")
            }

        private fun checkKeyIsValid(key: Any?, locale: Locale, map: Map<*, *>) {
            check(key != null) {
                "Error in map $map:\nInvalid translation key for locale '$locale', key cannot be null. $KEY_DOCUMENTATION."

            }
            check(key is String) {
                "Error in map $map:\nInvalid translation key '$key' of type ${key::class.simpleName} for locale '$locale', expected String. $KEY_DOCUMENTATION."
            }

            check(keyRegex().matches(key)) {
                "Error in map $map:\nInvalid translation key '$key' for locale '$locale', format is not valid. $KEY_DOCUMENTATION."
            }
        }

        @OptIn(ExperimentalContracts::class)
        private fun checkValueIsValid(value: Any?, locale: Locale, map: Map<*, *>) {
            contract {
                returns() implies (value != null)
            }
            check(value != null) {
                "Error in map $map:\nInvalid translation value for locale '$locale', value cannot be null. $SOURCE_DATA_DOCUMENTATION."

            }
            check(value is String || value is Number || value is Boolean || value is Map<*, *> || value is List<*>) {
                "Error in map $map:\nInvalid translation value '$value' of type ${value::class.simpleName} for locale '$locale'. For more details about supported types, see TranslationSourceData typealias documentation."
            }
        }

        private fun formatAndVariableNamesExtractionRegex(start: String, end: String): Regex {
            @Language("RegExp")
            val nbs = """(?<!\\)"""

            @Language("RegExp")
            val s = """$nbs${Regex.escape(start)}"""

            @Language("RegExp")
            val e = """$nbs${Regex.escape(end)}"""

            @Language("RegExp")
            val variable = """\s*([^ ]+)\s*"""

            @Language("RegExp")
            val postFormat = """(?:\(.+?$nbs\)\s*)?"""

            @Language("RegExp")
            val format = """(?:,\s*([^ ]+?)\s*$postFormat)?"""

            return """$s$variable$format$e""".toRegex()
        }

        private fun translationValueFormatCheckRegex(start: String, end: String): Regex {
            // first we define the param key regex
            @Language("RegExp")
            val notCommaOrColon = """[^:,]"""

            @Language("RegExp")
            val escapedCommaOrColon = """(?<=\\)[,:]"""

            @Language("RegExp")
            val formatParamKey = """(?:$notCommaOrColon|$escapedCommaOrColon)*"""

            // then we define the param value regex
            @Language("RegExp")
            val notCommaOrParenthesis = """[^,)]"""

            @Language("RegExp")
            val escapedCommaOrParenthesis = """(?<=\\)[,)]"""

            @Language("RegExp")
            val formatParamValue = """(?:$notCommaOrParenthesis|$escapedCommaOrParenthesis)*"""

            // then we define the not escaped colon regex which will be used to separate the key and value
            @Language("RegExp")
            val notEscapedColon = """(?<!\\):"""

            // now we combine the key and value regexes to build the param regex
            @Language("RegExp")
            val formatParam = """$formatParamKey$notEscapedColon$formatParamValue"""

            // once we have the param regex, we can build the params regex
            @Language("RegExp") // negative lookbehind to avoid escaping the closing parenthesis
            val formatParams = """\(($formatParam,)*$formatParam(?<!\\)\)\s*"""

            // which allows us to build the format regex
            @Language("RegExp")
            val format = """\s*,\s*$IDENTIFIER\s*(?:$formatParams)?"""

            // and finally we can build the variable content regex
            @Language("RegExp")
            val variableContent = """\s*$IDENTIFIER\s*(?:$format)?"""

            val s = Regex.escape(start)
            val e = Regex.escape(end)

            @Language("RegExp")
            val validVariable = """$s$variableContent(?<!\\)$e"""

            // the last step is to combine all the regexes to build the final regex
            val notStartSequence = if (start.length > 1) { // in case the start sequence is more than one character long
                val lastStartChar = Regex.escape(start.last().toString())
                val startPrefix = Regex.escape(start.dropLast(1))

                @Language("RegExp")
                val r = """[^$lastStartChar]|(?<!$startPrefix)$lastStartChar"""
                r
            } else {
                @Language("RegExp")
                val r = """[^$s]"""
                r
            }

            @Language("RegExp")
            val escapedStartSequence = """\\$s""""

            return """(?:$notStartSequence|$escapedStartSequence|$validVariable)*""".toRegex()
        }

        private fun parseLocale(fileName: String): Locale {
            return try {
                Locale.Builder()
                    .setLanguageTag(fileName.replace('_', '-'))
                    .build()
            } catch (e: IllformedLocaleException) {
                throw IllegalStateException(
                    "Invalid locale '$fileName'. For more details about locale syntax, see java.util.Locale documentation.",
                    e
                )
            }
        }

        private fun keyComparator(): Comparator<String> {
            val charComparator = Comparator { o1: Char, o2: Char ->
                when {
                    o1 == o2 -> 0
                    '.' == o1 -> -1
                    '.' == o2 -> 1
                    else -> o1.compareTo(o2)
                }
            }
            return Comparator { o1: String, o2: String ->
                val firstIt = o1.iterator()
                val secondIt = o2.iterator()

                while (firstIt.hasNext() && secondIt.hasNext()) {
                    val first = firstIt.nextChar()
                    val second = secondIt.nextChar()
                    val result = charComparator.compare(first, second)
                    if (result != 0) return@Comparator result
                }

                if (firstIt.hasNext()) {
                    1
                } else if (secondIt.hasNext()) {
                    -1
                } else {
                    0
                }
            }
        }

        private fun compareLocale(a: Locale, b: Locale): Int = a.toLanguageTag().compareTo(b.toLanguageTag())

        private const val SOURCE_DATA_DOCUMENTATION =
            "For more details about translation source data, see TranslationSourceData typealias documentation"

    }

    private fun loadResourceFile(path: String, clazz: Class<*>, directoryPath: String?) {
        val extension = path.substringAfterLast('.')
        val actualPath = if (directoryPath != null) "$directoryPath$path" else path
        require("json" == extension || "yaml" == extension || "yml" == extension) {
            "Unsupported file extension '$extension' for path '$actualPath'. Supported extensions are 'json', 'yaml' and 'yml'."
        }
        val nameWithoutExtension = path.substring(0, path.length - extension.length - 1)
        val locale = parseLocale(nameWithoutExtension)
        val content = clazz.getResourceAsStream(actualPath)!!
            .bufferedReader()
            .use { it.lines().collect(Collectors.joining("\n")) }
        addToMap(locale, parseFile(content, extension))
    }

    private fun addToMap(locale: Locale, map: Map<*, *>) {
        val localeMap = content.computeIfAbsent(locale) { HashMap() }
        map.forEach { (k, value) ->
            // we must check the validity here in case the value is a leaf (string, number or boolean), because we do
            // not check the validity of the value nor the key in before adding it to the map
            checkKeyIsValid(k, locale, map)
            checkValueIsValid(value, locale, map)
            val key = k as TranslationKey

            val toFlatten = ArrayDeque<Pair<String, TranslationSourceData>>()
            toFlatten.addLast(key to value)

            while (toFlatten.isNotEmpty()) {
                val (currentKey, currentValue) = toFlatten.removeFirst()
                when (currentValue) {
                    is Map<*, *> -> currentValue.forEach { (subKey, subValue) ->
                        checkKeyIsValid(subKey, locale, map)
                        checkValueIsValid(subValue, locale, map)
                        toFlatten.addLast("$currentKey.$subKey" to subValue)
                    }
                    is List<*> -> currentValue.forEachIndexed { index, subValue ->
                        checkValueIsValid(subValue, locale, map)
                        toFlatten.addLast("$currentKey.$index" to subValue)
                    }
                    // type of value is always valid here (string, number or boolean), because of previous checks
                    else -> addValue(
                        locale,
                        localeMap,
                        currentKey,
                        currentValue,
                    )
                }
            }
        }
    }

    private fun addValue(
        locale: Locale,
        finalMap: MutableMap<String, String>,
        key: String,
        value: TranslationSourceData,
    ) {
        val stringValue = value.toString()
        check(valueFormattingCheckRegex.matches(stringValue)) {
            "Invalid translation value '$stringValue' for locale '$locale', format is not valid. $SOURCE_DATA_DOCUMENTATION. Error occurred for key '$key' in map $value."
        }
        val existingFormats = configuration.formatters.keys
        variableExtractionRegex.findAll(stringValue).forEach {
            val (_, variableName, formatName) = it.groupValues
            check(Options.OPTIONS != variableName) {
                "The '${Options.OPTIONS}' variable name is reserved for the options map, use another name."
            }
            check(formatName.isEmpty() || formatName in existingFormats) {
                "Invalid translation value '$stringValue' for locale '$locale', format '$formatName' is not defined for this translator. Existing formats are: $existingFormats. $SOURCE_DATA_DOCUMENTATION. Error occurred for key '$key' in map $value."
            }
        }

        when (duplicatedKeyResolution) {
            // if resolution is FAIL, we need to check that the key is not already present
            DuplicatedKeyResolution.FAIL -> {
                finalMap.compute(key) { _, old ->
                    check(old == null) {
                        "Duplicate key '$key' for locale '$locale'. To avoid this error, do not add translations with the same key several times, or use a different resolution policy."
                    }
                    stringValue
                }
            }
            // if resolution is KEEP_FIRST and old is null, we can add the value
            DuplicatedKeyResolution.KEEP_FIRST -> finalMap.computeIfAbsent(key) { stringValue }
            // if resolution is KEEP_LAST, we always add the value
            DuplicatedKeyResolution.KEEP_LAST -> finalMap[key] = stringValue
        }
    }

}
