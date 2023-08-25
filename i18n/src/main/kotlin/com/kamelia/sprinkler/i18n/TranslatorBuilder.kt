package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.util.unsafeCast
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.io.path.isDirectory
import kotlin.io.path.nameWithoutExtension

class TranslatorBuilder internal constructor(
    private val defaultLocale: Locale,
) {

    /**
     * Set of already added path to avoid duplicates.
     */
    private val addedPaths = HashSet<Pair<Path, Boolean>>()

    /**
     * List of all data that will be used to build the translator.
     */
    private val translatorContent = ArrayList<TranslationResourceInformation>()

    /**
     * How to handle duplicate keys.
     */
    private var duplicateKeyResolution = DuplicateKeyResolution.FAIL

    fun addPath(path: Path, parser: I18nFileParser, fromResources: Boolean = false): TranslatorBuilder = apply {
        val isNew = addedPaths.add(path to fromResources)
        require(isNew) { "Path $path ${if (fromResources) "(from resources)" else ""} already added" }
        translatorContent += LoadedFileInfo(path, fromResources, parser)
    }

    fun addFile(file: File, parser: I18nFileParser, fromResources: Boolean = false): TranslatorBuilder = apply {
        addPath(file.toPath(), parser, fromResources)
    }

    fun addMap(locale: Locale, map: Map<String, Any>): TranslatorBuilder = apply {
        translatorContent += LoadedMap(locale, map)
    }

    fun addMaps(maps: Map<Locale, Map<String, Any>>): TranslatorBuilder = apply {
        maps.forEach { (locale, map) ->
            addMap(locale, map)
        }
    }

    fun duplicateKeyResolutionPolicy(duplicateKeyResolution: DuplicateKeyResolution): TranslatorBuilder = apply {
        this.duplicateKeyResolution = duplicateKeyResolution
    }

    enum class DuplicateKeyResolution {
        FAIL,
        KEEP_FIRST,
        KEEP_LAST,
        ;
    }

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
        return TranslatorImpl(defaultLocale, finalMap.unsafeCast())
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
                is Map<*, *> -> current.unsafeCast<Map<String, Any>>().forEach { (subKey, subValue) ->
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
                    val locale = parseLocale(it.nameWithoutExtension)
                    locale to info.parser.parseFile(it, info.fromResources)
                }
                .toList()
        } else { // if the path is a file, load it and store it in a one element list
            val locale = parseLocale(info.path.nameWithoutExtension)
            listOf(locale to info.parser.parseFile(info.path, info.fromResources))
        }

}


private sealed interface TranslationResourceInformation

private class LoadedFileInfo(
    val path: Path,
    val fromResources: Boolean,
    val parser: I18nFileParser,
) : TranslationResourceInformation

private class LoadedMap(val locale: Locale, val map: Map<String, Any>) : TranslationResourceInformation

private fun parseLocale(name: String): Locale = Locale
    .Builder()
    .setLanguageTag(name.replace('_', '-'))
    .build()
