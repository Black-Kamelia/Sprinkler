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

    private val addedPaths = HashSet<Pair<Path, Boolean>>()

    private val translatorContent = ArrayList<LeTruc>()

    private var keyStoragePolicy = KeyStoragePolicy.FLAT

    private var duplicateKeyResolutionPolicy = DuplicateKeyResolutionPolicy.FAIL

    fun addPath(path: Path, parser: I18nFileParser, fromResources: Boolean = false): TranslatorBuilder = apply {
        val isNew = addedPaths.add(path to fromResources)
        require(isNew) { "Path $path (${if (fromResources) "from resources" else ""}) already added" }
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

    fun withKeyStoragePolicy(policy: KeyStoragePolicy): TranslatorBuilder = apply {
        keyStoragePolicy = policy
    }

    fun duplicateKeyResolutionPolicy(duplicateKeyResolutionPolicy: DuplicateKeyResolutionPolicy): TranslatorBuilder =
        apply {
            this.duplicateKeyResolutionPolicy = duplicateKeyResolutionPolicy
        }

    fun build(): Translator {
        val finalMap = HashMap<Locale, HashMap<String, Any>>()
        translatorContent.forEach {
            when (it) {
                is LoadedFileInfo -> loadPath(it).forEach { (locale, map) ->
                    addToMap(finalMap, locale, map)
                }

                is LoadedMap -> addToMap(finalMap, it.locale, it.map)
            }
        }
        return TranslatorImpl(defaultLocale, finalMap.unsafeCast())
    }

    enum class KeyStoragePolicy {
        FLAT,
        NESTED,
        FLAT_AND_NESTED,
        ;
    }

    enum class DuplicateKeyResolutionPolicy {
        FAIL,
        KEEP_FIRST,
        KEEP_LAST,
        ;
    }

    private fun addToMap(finalMap: HashMap<Locale, HashMap<String, Any>>, locale: Locale, map: Map<String, Any>) {
        val localeMap = finalMap.computeIfAbsent(locale) { HashMap() }
        map.forEach { (key, value) ->
            check(KEY_IDENTIFIER_REGEX.matches(key)) {
                "Invalid key $key" // TODO add more info about the key syntax
            }
            val old = localeMap[key]
            when (duplicateKeyResolutionPolicy) {
                DuplicateKeyResolutionPolicy.FAIL -> {
                    check(old == null) { "Duplicate key '$key' for locale '$locale'" }
                    addValue(localeMap, key, value)
                }

                DuplicateKeyResolutionPolicy.KEEP_FIRST -> old ?: addValue(localeMap, key, value)
                DuplicateKeyResolutionPolicy.KEEP_LAST -> addValue(localeMap, key, value)
            }
        }
    }

    private fun addValue(finalMap: HashMap<String, Any>, rootKey: String, element: Any) {
        when (keyStoragePolicy) {
            KeyStoragePolicy.FLAT -> addFlattenedValue(finalMap, rootKey, element)
            KeyStoragePolicy.NESTED -> TODO()
            KeyStoragePolicy.FLAT_AND_NESTED -> TODO()
        }
    }

    private fun addFlattenedValue(finalMap: HashMap<String, Any>, rootKey: String, element: Any) {
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
                else -> finalMap[key] = current.toString()
            }
        }
    }

    private fun loadPath(info: LoadedFileInfo): List<Pair<Locale, Map<String, Any>>> =
        if (info.path.isDirectory()) {
            Files.list(info.path)
                .map {
                    val locale = parseLocale(it.nameWithoutExtension)
                    locale to info.parser.parseFile(it, info.fromResources)
                }
                .toList()
        } else {
            val locale = parseLocale(info.path.nameWithoutExtension)
            listOf(locale to info.parser.parseFile(info.path, info.fromResources))
        }


}


private sealed interface LeTruc

private class LoadedFileInfo(val path: Path, val fromResources: Boolean, val parser: I18nFileParser) : LeTruc

private class LoadedMap(val locale: Locale, val map: Map<String, Any>) : LeTruc

private fun parseLocale(name: String): Locale = Locale
    .Builder()
    .setLanguageTag(name.replace('_', '-'))
    .build()

private val KEY_IDENTIFIER_REGEX = """[a-zA-Z0-9]([a-zA-Z0-9_-]*[a-zA-Z0-9])?""".toRegex()