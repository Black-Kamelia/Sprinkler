package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.i18n.TranslatorBuilder.DuplicateKeyResolution
import com.kamelia.sprinkler.util.unsafeCast
import com.zwendo.restrikt.annotation.PackagePrivate
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.io.path.isDirectory
import kotlin.io.path.nameWithoutExtension

/**
 * Builder class used to create a [Translator]. This class provides several methods to add data to the translator from
 * different sources.
 *
 * There is several attention points to take into account when using this class:
 * - Different added sources will only be queried upon the creation of the translator, when calling [build] ;
 * - The order in which data is added is important, as it will be used during key duplication resolution, depending on
 * the [DuplicateKeyResolution] used.
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
    private var duplicateKeyResolution = DuplicateKeyResolution.FAIL

    /**
     * The current locale that will be used to create the translator.
     */
    private var currentLocale = defaultLocale

    /**
     * Adds a path to the builder. If the path is a directory, all files in it will be loaded. If the path is a file, it
     * will be loaded.
     *
     * Content will be converted to a map using the given [parser], and the locale of the file will be determined using
     * the given [localeMapper]. By default, the file name is parsed using [Locale.forLanguageTag].
     *
     * This method will throw an [IllegalArgumentException] if the path is already added.
     *
     * @param path the path to load
     * @param parser the parser to use to load the file
     * @param localeMapper the mapper to use to map the file name to a locale (by default, the file name is parsed using
     * [Locale.forLanguageTag])
     * @return this builder
     * @throws IllegalArgumentException if the path is already added
     */
    @JvmOverloads
    fun addPath(
        path: Path,
        parser: I18nFileParser,
        localeMapper: (String) -> Locale = ::parseLocale,
    ): TranslatorBuilder = apply {
        val isNew = addedPaths.add(path)
        require(isNew) { "Path $path already added" }
        translatorContent += LoadedFileInfo(path, parser, localeMapper)
    }

    /**
     * Adds a file to the builder. If the file is a directory, all files in it will be loaded. If the file is a file, it
     * will be loaded.
     *
     * Content will be converted to a map using the given [parser], and the locale of the file will be determined using
     * the given [localeMapper]. By default, the file name is parsed using [Locale.forLanguageTag].
     *
     * This method will throw an [IllegalArgumentException] if the path is already added.
     *
     * @param file the file to load
     * @param parser the parser to use to load the file
     * @param localeMapper the mapper to use to map the file name to a locale (by default, the file name is parsed using
     * [Locale.forLanguageTag])
     * @return this builder
     * @throws IllegalArgumentException if the file is already added
     */
    @JvmOverloads
    fun addFile(
        file: File,
        parser: I18nFileParser,
        localeMapper: (String) -> Locale = ::parseLocale,
    ): TranslatorBuilder = apply {
        addPath(file.toPath(), parser, localeMapper)
    }

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
    fun addMap(locale: Locale, map: Map<String, Any>): TranslatorBuilder = apply {
        translatorContent += LoadedMap(locale, map)
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
    fun addMaps(maps: Map<Locale, Map<String, Any>>): TranslatorBuilder = apply {
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
     * Sets the duplicate key resolution policy to use when adding data to the builder. By default, the policy is set to
     * [DuplicateKeyResolution.FAIL].
     *
     * @param duplicateKeyResolution the duplicate key resolution policy to use
     * @return this builder
     * @see DuplicateKeyResolution
     */
    fun duplicateKeyResolutionPolicy(duplicateKeyResolution: DuplicateKeyResolution): TranslatorBuilder = apply {
        this.duplicateKeyResolution = duplicateKeyResolution
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
     * Defines how to handle duplicate keys when creating a translator.
     */
    enum class DuplicateKeyResolution {
        /**
         * If a duplicate key is found, the build will fail.
         */
        FAIL,
        /**
         * If a duplicate key is found, the first value will be kept.
         */
        KEEP_FIRST,
        /**
         * If a duplicate key is found, the last value will be kept.
         */
        KEEP_LAST,
        ;
    }

    /**
     * Builds the translator using the data added to the builder.
     *
     * @return the created translator
     * @throws IllegalArgumentException if a duplicate key is found and the duplicate key resolution policy is set to
     * [DuplicateKeyResolution.FAIL], or if a source has been added without following the rules defined in the method
     * documentation
     */
    fun build(): Translator {
        val finalMap = HashMap<Locale, HashMap<String, Any>>()
        translatorContent.forEach {
            when (it) { // switch on different types of TranslationResourceInformation
                // if it is a LoadedFileInfo, we need to load the file or if it is a directory, load all files in it
                // and add them to the final map
                is LoadedFileInfo -> {
                    try {
                        loadPath(it).forEach { (locale, map) ->
                            addToMap(finalMap, locale, map)
                        }
                    } catch (e: IllformedLocaleException) {
                        error("Invalid locale for file '${it.path}'. File name must be a valid locale.")
                    }
                }
                // if it is a LoadedMap, we can directly add it to the final map
                is LoadedMap -> addToMap(finalMap, it.locale, it.map)
            }
        }
        return TranslatorImpl(defaultLocale, currentLocale, finalMap.unsafeCast())
    }

    private fun addToMap(finalMap: HashMap<Locale, HashMap<String, Any>>, locale: Locale, map: Map<String, Any>) {
        val localeMap = finalMap.computeIfAbsent(locale) { HashMap() }
        map.forEach { (key, value) ->
            // first check if the key is valid, if not we can already stop here and throw an exception
            check(KEY_IDENTIFIER_REGEX.matches(key)) {
                "Invalid key $key for locale '$locale'. For more details about key syntax, see Translator interface documentation."
            }
            when (duplicateKeyResolution) {
                // if resolution is fail, we need to check that the key is not already present
                DuplicateKeyResolution.FAIL -> {
                    check(key !in localeMap) { "Duplicate key '$key' for locale '$locale'" }
                    addValue(localeMap, key, value)
                }
                // if resolution is keep first and old is null, we can add the value
                DuplicateKeyResolution.KEEP_FIRST -> if (key !in localeMap) addValue(localeMap, key, value)
                // if resolution is keep last, we always add the value
                DuplicateKeyResolution.KEEP_LAST -> addValue(localeMap, key, value)
            }
        }
    }

    private fun addValue(finalMap: HashMap<String, Any>, rootKey: String, element: Any) {
        val toFlatten = ArrayDeque<Pair<String, Any>>()
        toFlatten.addLast(rootKey to element)

        while (toFlatten.isNotEmpty()) {
            val (key, current) = toFlatten.removeFirst()
            when (current) {
                is Map<*, *> -> current.unsafeCast<Map<Any, Any>>().forEach { (subKey, subValue) ->
                    toFlatten.addLast("$key.$subKey" to subValue)
                }

                is List<*> -> current.unsafeCast<List<Any>>().forEachIndexed { index, it ->
                    toFlatten.addLast("$key.$index" to it)
                }

                is String, is Number, is Boolean -> finalMap[key] = current.toString()
                else -> error("Unsupported type ${current::class.simpleName}. For more details about supported types, see Translator interface documentation.")
            }
        }
    }

    private fun loadPath(info: LoadedFileInfo): List<Pair<Locale, Map<String, Any>>> =
        if (info.path.isDirectory()) { // if the path is a directory, load all files in it and return the list
            Files.list(info.path)
                .map {
                    val locale = info.localeMapper(it.nameWithoutExtension)
                    locale to info.parser.parseFile(it)
                }
                .toList()
        } else { // if the path is a file, load it and store it in a one element list
            val locale = info.localeMapper(info.path.nameWithoutExtension)
            listOf(locale to info.parser.parseFile(info.path))
        }

}

private sealed interface TranslationResourceInformation

private class LoadedFileInfo(
    val path: Path,
    val parser: I18nFileParser,
    val localeMapper: (String) -> Locale,
) : TranslationResourceInformation

private class LoadedMap(val locale: Locale, val map: Map<String, Any>) : TranslationResourceInformation

private fun parseLocale(name: String): Locale = Locale
    .Builder()
    .setLanguageTag(name.replace('_', '-'))
    .build()
