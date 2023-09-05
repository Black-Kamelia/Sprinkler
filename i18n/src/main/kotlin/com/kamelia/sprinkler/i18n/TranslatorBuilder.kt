package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.i18n.TranslatorBuilder.DuplicatedKeyResolution
import com.kamelia.sprinkler.util.unsafeCast
import com.zwendo.restrikt.annotation.PackagePrivate
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.stream.Stream
import kotlin.collections.ArrayDeque
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.io.path.isDirectory

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

    private var optionsProcessor: OptionsProcessor = OptionsProcessor.noOp

    /**
     * Adds a path to the builder. If the path is a directory, all files in it will be loaded. If the path is a file, it
     * will be loaded.
     *
     * This method will throw an [IllegalArgumentException] if the path is already added.
     *
     * @param path the path to load
     * @param parser the parser to use to load the file
     * @return this builder
     * @throws IllegalArgumentException if the path is already added
     */
    fun addPath(path: Path, parser: I18nFileParser): TranslatorBuilder = apply {
        val isNew = addedPaths.add(path)
        require(isNew) { "Path $path already added" }
        translatorContent += FileInfo(path, parser)
    }

    /**
     * Adds a path to the builder. If the path is a directory, all files in it will be loaded. If the path is a file, it
     * will be loaded.
     *
     * This method will throw an [IllegalArgumentException] if the path is already added.
     *
     * This method is a shorthand for [addPath] with a [I18nFileParser.fromString] parser.
     *
     * @param path the path to load
     * @param mapper the mapper to use to load the file
     * @return this builder
     * @throws IllegalArgumentException if the path is already added
     * @see I18nFileParser.fromString
     */
    fun addPath(path: Path, mapper: (String) -> Map<String, TranslatorSourceData>): TranslatorBuilder =
        addPath(path, I18nFileParser.fromString { mapper(it) })

    /**
     * Adds a file to the builder. If the file is a directory, all files in it will be loaded. If the file is a file, it
     * will be loaded.
     *
     * This method will throw an [IllegalArgumentException] if the path is already added.
     *
     * @param file the file to load
     * @param parser the parser to use to load the file
     * @return this builder
     * @throws IllegalArgumentException if the file is already added
     */
    fun addFile(file: File, parser: I18nFileParser): TranslatorBuilder = addPath(file.toPath(), parser)

    /**
     * Adds a file to the builder. If the file is a directory, all files in it will be loaded. If the file is a file, it
     * will be loaded.
     *
     * This method will throw an [IllegalArgumentException] if the path is already added.
     *
     * This method is a shorthand for [addFile] with a [I18nFileParser.fromString] parser.
     *
     * @param file the file to load
     * @param mapper the mapper to use to load the file
     * @return this builder
     * @throws IllegalArgumentException if the file is already added
     * @see I18nFileParser.fromString
     */
    fun addFile(file: File, mapper: (String) -> Map<String, TranslatorSourceData>): TranslatorBuilder =
        addFile(file, I18nFileParser.fromString(mapper = mapper))

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
    fun addMap(locale: Locale, map: Map<String, TranslatorSourceData>): TranslatorBuilder = apply {
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
    fun addMaps(maps: Map<Locale, Map<String, TranslatorSourceData>>): TranslatorBuilder = apply {
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
     * @param optionsProcessor the options processor to set
     * @return this builder
     * @see OptionsProcessor
     */
    fun withOptionsProcessor(optionsProcessor: OptionsProcessor): TranslatorBuilder = apply {
        this.optionsProcessor = optionsProcessor
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
                    } catch (e: I18nFileParser.I18nParsingException) {
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

        return TranslatorImpl(defaultLocale, currentLocale, sortedMap, optionsProcessor)
    }

    private fun addToMap(finalMap: HashMap<Locale, HashMap<String, String>>, locale: Locale, map: Map<String, TranslatorSourceData>) {
        val localeMap = finalMap.computeIfAbsent(locale) { HashMap() }
        map.forEach { (key, value) ->
            // we must check the validity here in case the value is a leaf (string, number or boolean), because we do
            // not check the validity of the value nor the key in before adding it to the map
            checkKeyIsValid(key, currentLocale)
            checkValueIsValid(value, currentLocale)

            val toFlatten = ArrayDeque<Pair<String, TranslatorSourceData>>()
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

    private fun addValue(locale: Locale, finalMap: HashMap<String, String>, key: String, value: TranslatorSourceData) {
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

    private fun loadPath(info: FileInfo): List<Pair<Locale, Map<String, TranslatorSourceData>>> =
        if (info.path.isDirectory()) { // if the path is a directory, load all files in it and return the list
            Files.list(info.path)
                .map {
                    val (locale, map) = info.parser.parseFile(it) ?: return@map null
                    locale to map
                }
                .filter { it != null }
                // as we filtered null values, we can safely cast
                .unsafeCast<Stream<Pair<Locale, Map<String, TranslatorSourceData>>>>()
                .toList()
        } else { // if the path is a file, load it and store it in a one element list
            info.parser
                .parseFile(info.path)
                ?.let { listOf(it.locale to it.map) }
                ?: emptyList()
        }

}

private sealed interface TranslationResourceInformation

private class FileInfo(
    val path: Path,
    val parser: I18nFileParser,
) : TranslationResourceInformation

private class MapInfo(val locale: Locale, val map: Map<String, TranslatorSourceData>) : TranslationResourceInformation

private fun checkKeyIsValid(key: Any?, locale: Locale) {
    checkNotNull(key) {
        "Keys cannot be null. For more details about key syntax, see Translator interface documentation."
    }
    check(key is String) {
        "Invalid key type ${key::class.simpleName} for locale '$locale', expected String. For more details about keys, see Translator interface documentation."
    }
    check(FULL_KEY_REGEX.matches(key)) {
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
