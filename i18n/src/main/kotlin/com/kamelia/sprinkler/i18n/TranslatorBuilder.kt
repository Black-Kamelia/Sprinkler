package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.i18n.TranslatorBuilder.DuplicatedKeyResolution
import com.kamelia.sprinkler.util.assertionFailed
import com.kamelia.sprinkler.util.unsafeCast
import com.zwendo.restrikt.annotation.PackagePrivate
import org.json.JSONException
import org.json.JSONObject
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.error.YAMLException
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.stream.Stream
import kotlin.collections.ArrayDeque
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.io.path.extension
import kotlin.io.path.isDirectory
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.readText

/**
 * Builder class used to create a [Translator]. This class provides several methods to add data to the translator from
 * different sources.
 *
 * There is several attention points to take into account when using this class:
 * - Different added sources will only be queried upon the creation of the translator, when calling [build] ;
 * - The order in which data is added is important, as it will be used during key duplication resolution, depending on
 * the [DuplicatedKeyResolution] used.
 *
 * **NOTE**: [translators][Translator] created with this builder are immutable and therefore thread-safe.
 *
 * @see Translator
 */
class TranslatorBuilder @PackagePrivate internal constructor(
    private var defaultLocale: Locale,
) {

    /**
     * Set of already added path to avoid duplicates.
     */
    private val addedPaths = HashSet<Path>()

    /**
     * List of all data that will be used to build the translator.
     */
    private val translatorContent = ArrayList<TranslationResourceInformation>()

    /**
     * How to handle duplicate keys.
     */
    private var duplicatedKeyResolution = DuplicatedKeyResolution.FAIL

    /**
     * The current locale that will be used to create the translator.
     */
    private var currentLocale = defaultLocale

    private var optionProcessor: OptionProcessor = OptionProcessor.noOp

    /**
     * Adds a path to the builder. If the path is a directory, all files in it will be loaded. If the path is a file, it
     * will be loaded.
     *
     * Supported formats are JSON and YAML, with the following extensions: `json`, `yaml` and `yml`.
     *
     * The locale of the file will be parsed from the file name, using the [Locale.forLanguageTag] method. If the file's
     * name is not a valid locale, an [IllegalStateException] will be thrown when building the translator.
     *
     * This method will throw an [IllegalArgumentException] if the path is already added or if the file extension is not
     * supported.
     *
     * @param path the path to load
     * @throws IllegalArgumentException if the path is already added or if the file extension is not supported
     */
    fun addPath(path: Path): TranslatorBuilder = apply {
        val extension = path.extension
        require(extension == "json" || extension == "yaml" || extension == "yml") {
            "Unsupported file extension '$extension' for path '$path'. Supported extensions are 'json', 'yaml' and 'yml'."
        }
        val isNew = addedPaths.add(path)
        require(isNew) { "Path $path already added" }
        translatorContent += FileInfo(path)
    }

    /**
     * Adds a file to the builder. If the file is a directory, all files in it will be loaded. If the file is a file, it
     * will be loaded.
     *
     * Supported formats are JSON and YAML, with the following extensions: `json`, `yaml` and `yml`.
     *
     * The locale of the file will be parsed from the file name, using the [Locale.forLanguageTag] method. If the file's
     * name is not a valid locale, an [IllegalStateException] will be thrown when building the translator.
     *
     * This method will throw an [IllegalArgumentException] if the file is already added or if the file extension is not
     * supported.
     *
     * @param file the file to load
     * @return this builder
     * @throws IllegalArgumentException if the file is already added
     */
    fun addFile(file: File): TranslatorBuilder = addPath(file.toPath())

    /**
     * Adds a map for a locale to the builder. The content of the map will be added to the final translator. The
     * [locale] parameter will be used as the locale of the map.
     *
     * **NOTE**: The [map] should follow rules defined in the [I18nFileParser] documentation.
     *
     * @param locale the locale of the map
     * @param map the map to add
     * @return this builder
     */
    fun addMap(locale: Locale, map: Map<String, TranslationSourceData>): TranslatorBuilder = apply {
        translatorContent += MapInfo(locale, map)
    }

    /**
     * Adds a map of locales to the builder. The content of the maps will be added to the final translator. The keys of
     * the [maps] will be used as the locales of the maps. The values of the [maps] will be used as the content of the
     *
     * **NOTE**: The [maps] should follow rules defined in the [I18nFileParser] documentation.
     *
     * @param maps the maps to add
     * @return this builder
     */
    fun addMaps(maps: Map<Locale, Map<String, TranslationSourceData>>): TranslatorBuilder = apply {
        maps.forEach { (locale, map) ->
            addMap(locale, map)
        }
    }

    /**
     * Adds a translator to the builder. The content of the translator will be added to the final translator.
     *
     * @param translator the translator to add
     * @return this builder
     * @see Translator.toMap
     */
    fun addTranslator(translator: Translator): TranslatorBuilder = apply {
        translator.toMap().forEach { (locale, map) ->
            addMap(locale, map)
        }
    }

    /**
     * Sets the duplicated key resolution policy to use when adding data to the builder. By default, the policy is set
     * to [DuplicatedKeyResolution.FAIL].
     *
     * @param duplicatedKeyResolution the duplicated key resolution policy to use
     * @return this builder
     * @see DuplicatedKeyResolution
     */
    fun withDuplicatedKeyResolutionPolicy(duplicatedKeyResolution: DuplicatedKeyResolution): TranslatorBuilder = apply {
        this.duplicatedKeyResolution = duplicatedKeyResolution
    }

    /**
     * Sets the default locale that will be set to the translator upon creation.
     *
     * @param locale the default locale to set
     * @return this builder
     */
    fun withDefaultLocale(locale: Locale): TranslatorBuilder = apply {
        defaultLocale = locale
    }

    /**
     * Sets the current locale that will be set to the translator upon creation.
     *
     * @param locale the current locale to set
     * @return this builder
     */
    fun withCurrentLocale(locale: Locale): TranslatorBuilder = apply {
        currentLocale = locale
    }

    /**
     * Sets the options processor that will be set to the translator upon creation.
     *
     * @param optionProcessor the options processor to set
     * @return this builder
     * @see OptionProcessor
     */
    fun withOptionsProcessor(optionProcessor: OptionProcessor): TranslatorBuilder = apply {
        this.optionProcessor = optionProcessor
    }

    /**
     * Defines how to handle duplicated keys when creating a translator.
     */
    enum class DuplicatedKeyResolution {

        /**
         * If a duplicated key is found, the build will fail.
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
     * @throws IllegalArgumentException if a duplicate key is found and the duplicate key resolution policy is set to
     * [DuplicatedKeyResolution.FAIL], or if a source has been added without following the rules defined in the method
     * documentation
     */
    fun build(): Translator {
        val finalMap = HashMap<Locale, HashMap<String, String>>()
        translatorContent.forEach {
            when (it) { // switch on different types of TranslationResourceInformation
                // if it is a FileInfo, we need to load the file or if it is a directory, load all files in it
                // and add them to the final map
                is FileInfo -> {
                    try {
                        loadPath(it).forEach { (locale, map) ->
                            addToMap(finalMap, locale, map)
                        }
                    } catch (e: I18nParsingException) {
                        error("Error while parsing file ${e.path}: ${e.message}")
                    }
                }
                // if it is a MapInfo, we can directly add it to the final map
                is MapInfo -> addToMap(finalMap, it.locale, it.map)
            }
        }

        // once all data is added to the final map, we need to sort it
        val sortedMap = finalMap.mapValues { (_, map) ->
            map.asSequence()
                .map { (key, value) -> key.split('.') to value }
                .sortedWith { (first, _), (second, _) ->
                    stringListComparator(first, second)
                }
                .map { (key, value) -> key.joinToString(".") to value }
                .toMap()
        }

        return TranslatorImpl(defaultLocale, currentLocale, sortedMap, optionProcessor)
    }

    private fun addToMap(
        finalMap: HashMap<Locale, HashMap<String, String>>,
        locale: Locale,
        map: Map<String, TranslationSourceData>,
    ) {
        val localeMap = finalMap.computeIfAbsent(locale) { HashMap() }
        map.forEach { (key, value) ->
            // we must check the validity here in case the value is a leaf (string, number or boolean), because we do
            // not check the validity of the value nor the key in before adding it to the map
            checkKeyIsValid(key, currentLocale)
            checkValueIsValid(value, currentLocale)

            val toFlatten = ArrayDeque<Pair<String, TranslationSourceData>>()
            toFlatten.addLast(key to value)

            while (toFlatten.isNotEmpty()) {
                val (currentKey, currentValue) = toFlatten.removeFirst()
                when (currentValue) {
                    is Map<*, *> -> currentValue.forEach { (subKey, subValue) ->
                        checkKeyIsValid(subKey, currentLocale)
                        checkValueIsValid(subValue, currentLocale)
                        toFlatten.addLast("$currentKey.$subKey" to subValue)
                    }
                    is List<*> -> currentValue.forEachIndexed { index, subValue ->
                        checkValueIsValid(subValue, currentLocale)
                        toFlatten.addLast("$currentKey.$index" to subValue)
                    }
                    // type of value is always valid here (string, number or boolean), because of previous checks
                    else -> addValue(locale, localeMap, currentKey, currentValue)
                }
            }
        }
    }

    private fun addValue(locale: Locale, finalMap: HashMap<String, String>, key: String, value: TranslationSourceData) {
        when (duplicatedKeyResolution) {
            // if resolution is FAIL, we need to check that the key is not already present
            DuplicatedKeyResolution.FAIL -> {
                finalMap.compute(key) { _, old ->
                    check(old == null) { "Duplicate key '$key' for locale '$locale'" }
                    value.toString()
                }
            }
            // if resolution is KEEP_FIRST and old is null, we can add the value
            DuplicatedKeyResolution.KEEP_FIRST -> finalMap.computeIfAbsent(key) { value.toString() }
            // if resolution is KEEP_LAST, we always add the value
            DuplicatedKeyResolution.KEEP_LAST -> finalMap[key] = value.toString()
        }
    }

    private fun loadPath(info: FileInfo): List<Pair<Locale, Map<String, TranslationSourceData>>> =
        if (info.path.isDirectory()) { // if the path is a directory, load all files in it and return the list
            Files.list(info.path)
                .map {
                    val map = parseFile(it)
                    val locale = parseLocale(it)
                    locale to map
                }
                .filter { it != null }
                // as we filtered null values, we can safely cast
                .unsafeCast<Stream<Pair<Locale, Map<String, TranslationSourceData>>>>()
                .toList()
        } else { // if the path is a file, load it and store it in a one element list
            val map = parseFile(info.path)
            val locale = parseLocale(info.path)
            listOf(locale to map)
        }

}

private sealed interface TranslationResourceInformation

private class FileInfo(val path: Path) : TranslationResourceInformation

private class MapInfo(val locale: Locale, val map: Map<String, TranslationSourceData>) : TranslationResourceInformation

private fun checkKeyIsValid(key: Any?, locale: Locale) {
    checkNotNull(key) {
        "Keys cannot be null. For more details about key syntax, see Translator interface documentation."
    }
    check(key is String) {
        "Invalid key type ${key::class.simpleName} for locale '$locale', expected String. For more details about keys, see Translator interface documentation."
    }
    check(KEY_REGEX.matches(key)) {
        "Invalid key '$key' for locale '$locale'. For more details about key syntax, see Translator interface documentation."
    }
}

@OptIn(ExperimentalContracts::class)
private fun checkValueIsValid(value: Any?, locale: Locale) {
    contract {
        returns() implies (value != null)
    }
    checkNotNull(value) {
        "Values cannot be null. For more details about supported types, see I18nFileParser interface documentation."
    }
    check(value is String || value is Number || value is Boolean || value is Map<*, *> || value is List<*>) {
        "Invalid value '$value' of type ${value::class.simpleName} for locale '$locale'. For more details about supported types, see I18nFileParser interface documentation."
    }
}

private fun parseFile(path: Path): TranslationSourceMap =
    when (val extension = path.extension) {
        "json" -> {
            try {
                JSONObject(path.readText()).toMap()
            } catch (_: JSONException) {
                throw I18nParsingException("Invalid JSON file.", path)
            }
        }
        "yaml", "yml" -> {
            try {
                Yaml().load<Map<TranslationKeyPart, TranslationSourceData>>(path.readText())
            } catch (_: YAMLException) {
                throw I18nParsingException("Invalid YAML file.", path)
            }
        }
        else -> assertionFailed("File extension '$extension' should have been checked before.")
    }

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

private class I18nParsingException(message: String, val path: Path) : Throwable(message, null, false, false)
