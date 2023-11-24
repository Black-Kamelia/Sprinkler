package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.i18n.TranslatorBuilder.DuplicatedKeyResolution
import com.kamelia.sprinkler.util.assertionFailed
import com.kamelia.sprinkler.util.unsafeCast
import com.zwendo.restrikt.annotation.PackagePrivate
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.stream.Stream
import org.json.JSONException
import org.json.JSONObject
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.error.YAMLException
import kotlin.collections.ArrayDeque
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

    private var options: OptionConfiguration = OptionConfiguration.create { }

    /**
     * Adds a path to the builder. If the path is a directory, all files in it will be loaded. If the path is a file, it
     * will be loaded.
     *
     * Supported formats are JSON and YAML, with the following extensions: `json`, `yaml` and `yml`.
     *
     * The locale of the file will be parsed from the file name, using the [Locale.forLanguageTag] method. If the file's
     * name is not a valid locale, an [IllegalStateException] will be thrown when building the translator.
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
        val isNew = addedPaths.add(path)
        if (!isNew) return@apply // if the path is already added, we do not need to append it to the list
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
     * This method will throw an [IllegalArgumentException] if the file extension is not supported.
     *
     * @param file the file to load
     * @return this builder
     * @throws IllegalArgumentException if the file extension is not supported
     */
    fun addFile(file: File): TranslatorBuilder = addFile(file.toPath())

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
    fun addMap(locale: Locale, map: TranslationSourceMap): TranslatorBuilder = apply {
        translatorContent += MapInfo(locale, map)
    }

    /**
     * Adds a map of locales to the builder. The content of the maps will be added to the final translator. The keys of
     * the [maps] will be used as the locales of the maps. The values of the [maps] will be used as the content of the
     *
     * **NOTE**: The [maps] should follow rules defined in the [TranslationSourceMap] documentation. Any map that does
     * not follow these rules will result in an [IllegalArgumentException] being thrown when building the translator.
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
     * Sets the options that will be used for the created translator.
     *
     * @param options the options to set
     * @return this builder
     */
    fun withOptions(options: OptionConfiguration): TranslatorBuilder = apply {
        this.options = options
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
     * @throws IllegalStateException if a duplicate key is found and the duplicate key resolution policy is set to
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
                    } catch (e: I18nException) { // catch and rethrow to add the path to the error message
                        throw IllegalStateException("Error while loading file ${it.path}", e)
                    }
                }
                // if it is a MapInfo, we can directly add it to the final map
                is MapInfo -> {
                    try {
                        addToMap(finalMap, it.locale, it.map)
                    } catch (e: I18nException) {
                        throw IllegalStateException(e)
                    }
                }
            }
        }

        // once all data is added to the final map, we need to sort it
        val sortedMap = finalMap.mapValues { (_, map) ->
            map.asSequence()
                // the sort is done on each key part, so we need to split the key
                .map { (key, value) -> key.split('.') to value }
                // TODO try to sort without the split
                .sortedWith { (first, _), (second, _) ->
                    stringListComparator(first, second)
                }
                .map { (key, value) -> key.joinToString(".") to value }
                .toMap()
        }

        val data = TranslatorData(defaultLocale, sortedMap, options)
        return TranslatorImpl(currentLocale, data)
    }

    private fun addToMap(
        finalMap: HashMap<Locale, HashMap<String, String>>,
        locale: Locale,
        map: Map<*, *>,
    ) {
        val localeMap = finalMap.computeIfAbsent(locale) { HashMap() }
        map.forEach { (k, value) ->
            // we must check the validity here in case the value is a leaf (string, number or boolean), because we do
            // not check the validity of the value nor the key in before adding it to the map
            checkKeyIsValid(k, currentLocale, map)
            checkValueIsValid(value, currentLocale, map)
            val key = k as TranslationKey

            val toFlatten = ArrayDeque<Pair<String, TranslationSourceData>>()
            toFlatten.addLast(key to value)

            while (toFlatten.isNotEmpty()) {
                val (currentKey, currentValue) = toFlatten.removeFirst()
                when (currentValue) {
                    is Map<*, *> -> currentValue.forEach { (subKey, subValue) ->
                        checkKeyIsValid(subKey, currentLocale, map)
                        checkValueIsValid(subValue, currentLocale, map)
                        toFlatten.addLast("$currentKey.$subKey" to subValue)
                    }
                    is List<*> -> currentValue.forEachIndexed { index, subValue ->
                        checkValueIsValid(subValue, currentLocale, map)
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
        when {
            !info.path.exists() -> throw I18nException("Path ${info.path} does not exist")
            info.path.isDirectory() -> { // if the path is a directory, load all files in it and return the list
                Files.list(info.path)
                    .map {
                        val locale = parseLocale(it)
                        val map = parseFile(it)
                        locale to map
                    }
                    .unsafeCast<Stream<Pair<Locale, Map<String, TranslationSourceData>>>>()
                    .toList()
            }
            else -> { // if the path is a file, load it and store it in a one element list
                val map = parseFile(info.path)
                val locale = parseLocale(info.path)
                listOf(locale to map)
            }
        }

    private fun checkKeyIsValid(key: Any?, locale: Locale, map: Map<*, *>) {
        if (key == null) {
            throw I18nException(
                "Error in map $map:\nInvalid translation key for locale '$locale', key cannot be null. $KEY_DOCUMENTATION"
            )
        }
        if (key !is String) {
            throw I18nException(
                "Error in map $map:\nInvalid translation key '$key' of type ${key::class.simpleName} for locale '$locale', expected String. $KEY_DOCUMENTATION"
            )
        }

        if (!KEY_REGEX.matches(key)) {
            throw I18nException(
                "Error in map $map:\nInvalid translation key '$key' for locale '$locale', format is not valid. $KEY_DOCUMENTATION"
            )
        }
    }

    @OptIn(ExperimentalContracts::class)
    private fun checkValueIsValid(value: Any?, locale: Locale, map: Map<*, *>) {
        contract {
            returns() implies (value != null)
        }
        if (value == null) {
            throw I18nException(
                "Error in map $map:\nInvalid translation value for locale '$locale', value cannot be null. $SOURCE_DATA_DOCUMENTATION"
            )
        }
        if (value !is String && value !is Number && value !is Boolean && value !is Map<*, *> && value !is List<*>) {
            throw I18nException(
                "Error in map $map:\nInvalid translation value '$value' of type ${value::class.simpleName} for locale '$locale'. For more details about supported types, see TranslationSourceData typealias documentation."
            )
        }
    }

    private fun parseFile(path: Path): TranslationSourceMap =
        when (val extension = path.extension) {
            "json" -> {
                try {
                    JSONObject(path.readText()).toMap()
                } catch (e: JSONException) {
                    throw I18nException("Invalid JSON file: ${e.message}")
                }
            }
            "yaml", "yml" -> {
                try {
                    Yaml().load<Map<TranslationKeyPart, TranslationSourceData>>(path.readText())
                } catch (e: YAMLException) {
                    throw I18nException("Invalid YAML file: ${e.message}")
                }
            }
            else -> assertionFailed("File extension '$extension' should have been checked before.")
        }

    private fun parseLocale(path: Path): Locale {
        val locale = path.nameWithoutExtension
        return try {
            Locale.Builder()
                .setLanguageTag(locale.replace('_', '-'))
                .build()
        } catch (_: IllformedLocaleException) {
            throw I18nException(
                "Invalid locale '$locale'. For more details about locale syntax, see java.util.Locale documentation.",
            )
        }
    }

    private sealed interface TranslationResourceInformation

    private class FileInfo(val path: Path) : TranslationResourceInformation

    private class MapInfo(
        val locale: Locale,
        val map: Map<String, TranslationSourceData>,
    ) : TranslationResourceInformation

}
