package com.kamelia.sprinkler.i18n

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.isDirectory
import kotlin.io.path.nameWithoutExtension

class TranslatorBuilder(
    private val defaultLocale: Locale,
) {

    private val addedPaths = HashSet<Pair<Path, Boolean>>()

    private val translatorContent = ArrayList<LeTruc>()

    private var keyStoragePolicy = KeyStoragePolicy.FLAT

    fun addPath(path: Path, parser: I18nFileParser, fromResources: Boolean = false): TranslatorBuilder = apply {
        val isNew = addedPaths.add(path to fromResources)
        require(isNew) { "Path $path (${if (fromResources) "from resources" else ""}) already added" }
        translatorContent += LoadedFileInfo(path, fromResources, parser)
    }

    fun addFile(file: File, parser: I18nFileParser, fromResources: Boolean = false): TranslatorBuilder = apply {
        addPath(file.toPath(), parser, fromResources)
    }

    fun addMap(locale: Locale, map: Map<String, Any?>): TranslatorBuilder = apply {
        translatorContent += LoadedMap(locale, map)
    }

    fun addMaps(maps: Map<Locale, Map<String, Any?>>): TranslatorBuilder = apply {
        maps.forEach { (locale, map) ->
            addMap(locale, map)
        }
    }

    fun withKeyStoragePolicy(policy: KeyStoragePolicy): TranslatorBuilder = apply {
        keyStoragePolicy = policy
    }

    fun duplicateKeyResolutionPolicy(): TranslatorBuilder = apply {
        TODO()
    }

    fun build(): Translator {
        return when (keyStoragePolicy) {
            KeyStoragePolicy.NESTED -> createNestedKeysTranslator()
            else -> TODO()
        }
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

    private fun createNestedKeysTranslator(): Translator {
        val finalMap = HashMap<Locale, HashMap<String, Any?>>()
        val mapList = buildList {
            translatorContent.forEach {
                when (it) {
                    is LoadedFileInfo -> {
                        //val maps = loadPath(it)
                    }

                    is LoadedMap -> add(it.map)
                }
            }
        }
        TODO()
    }

    private fun loadPath(info: LoadedFileInfo): List<Map<String, Any?>> =
        if (info.path.isDirectory()) {
            Files.list(info.path)
                .map {
                    val locale = parseLocale(it.nameWithoutExtension)
                    info.parser.parseFile(it, info.fromResources)
                }
                .toList()
        } else {
            listOf(info.parser.parseFile(info.path, info.fromResources))
        }


}


private sealed interface LeTruc

private class LoadedFileInfo(val path: Path, val fromResources: Boolean, val parser: I18nFileParser) : LeTruc

private class LoadedMap(val locale: Locale, val map: Map<String, Any?>) : LeTruc

private fun parseLocale(name: String): Locale = Locale
    .Builder()
    .setLanguageTag(name.replace('_', '-'))
    .build()