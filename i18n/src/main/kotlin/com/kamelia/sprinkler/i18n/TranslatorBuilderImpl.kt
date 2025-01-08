package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.i18n.TranslatorBuilder.Companion.keyRegex
import com.kamelia.sprinkler.util.ExtendedCollectors
import com.kamelia.sprinkler.util.assertionFailed
import com.kamelia.sprinkler.util.entryOf
import com.kamelia.sprinkler.util.illegalArgument
import com.kamelia.sprinkler.util.toUnmodifiableMap
import com.kamelia.sprinkler.util.unsafeCast
import com.zwendo.restrikt2.annotation.PackagePrivate
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.util.IllformedLocaleException
import java.util.Locale
import java.util.TreeMap
import java.util.jar.JarFile
import java.util.stream.Collector
import java.util.stream.Collectors
import org.intellij.lang.annotations.Language
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.component3
import kotlin.collections.set
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.io.path.exists
import kotlin.io.path.extension
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.readText

@PackagePrivate
internal class TranslatorBuilderImpl private constructor(
    private val defaultLocale: Locale,
    internal val defaultCharset: Charset,
    private val configuration: TranslatorConfiguration,
    private val throwOnMissingKey: Boolean,
    private val duplicatedKeyResolution: TranslatorBuilder.DuplicatedKeyResolution,
    private val valueFormattingCheckRegex: Regex,
    private val variableExtractionRegex: Regex,
    private val contentLoaders: Map<String, () -> TranslatorBuilder.ContentLoader>,
) : TranslatorBuilder {

    /**
     * TreeMap used to normalize the order of the locales in the final map.
     */
    private val content = TreeMap<Locale, HashMap<String, String>> { a, b ->
        a.toLanguageTag().compareTo(b.toLanguageTag())
    }

    private val walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)

    override fun addPath(path: Path, charset: Charset): TranslatorBuilder = apply {
        val extension = path.extension
        checkValidExtension(extension, path)

        try {
            // we need to load the file, or, if it is a directory, load all files in it and add them to the final map.
            loadPath(path, charset).forEach { (locale, map) ->
                addToMap(locale, map)
            }
        } catch (e: IllegalStateException) {
            throw IllegalStateException("Error while loading path $path", e)
        } catch (e: Exception) { // catch and rethrow to add the path to the error message
            throw IllegalArgumentException("Error while loading file $path", e)
        }
    }

    private fun checkValidExtension(extension: String, path: Path) {
        require(extension in contentLoaders || path.isDirectory()) {
            buildString {
                append("Unsupported file extension '")
                append(extension)
                append("' for path '")
                append(path)
                append("'. Supported extensions are ")
                contentLoaders.keys.joinTo(this, "', '", "['", "']") { it }
                append(".")
            }
        }
    }

    override fun addResource(resourcePath: String, resourceClass: Class<*>, charset: Charset): TranslatorBuilder =
        apply {
            loadResources(resourcePath, resourceClass, charset)
        }

    override fun addResource(resourcePath: String, charset: Charset): TranslatorBuilder = apply {
        loadResources(resourcePath, walker.callerClass, charset)
    }

    override fun addResource(resourcePath: String, resourceClass: Class<*>): TranslatorBuilder = apply {
        loadResources(resourcePath, resourceClass, defaultCharset)
    }

    override fun addResource(resourcePath: String): TranslatorBuilder = apply {
        loadResources(resourcePath, walker.callerClass, defaultCharset)
    }

    private fun loadResources(resourcePath: String, resourceClass: Class<*>, charset: Charset) {
        var strPath = resourceClass
            .getProtectionDomain()
            .codeSource
            .location
            .path

        // fix for windows paths where the path starts with '/C:/...'
        if (strPath[2] == ':' && strPath[0] == '/') {
            strPath = strPath.substring(1)
        }

        val path = Path.of(strPath)
        if (!path.isRegularFile()) { // we are not in a jar
            val url = resourceClass.getResource(resourcePath) ?: illegalArgument("Resource $resourcePath not found")
            addURL(url, charset)
        } else { // we are in a jar
            walkJarDirectory(strPath, resourcePath, resourceClass).forEach {
                loadResourceFile(it, resourceClass)
            }
        }
    }

    override fun addMap(locale: Locale, map: TranslationSourceMap): TranslatorBuilder = apply { addToMap(locale, map) }

    override fun build(): Translator {
        if (content.isEmpty()) {
            val data = TranslatorData(
                defaultLocale,
                emptyMap(),
                configuration.interpolationDelimiter,
                { Plural.nullMapper() },
                { configuration.formatters.getValue(it) },
                configuration.missingKeyPolicy,
            )
            return TranslatorImpl(defaultLocale, data)
        }

        val finalMap = LinkedHashMap<Locale, MutableMap<String, String>>(content.size)

        val comparator = keyComparator()

        // firstEntry is never null because we checked that content is not empty
        val expectedKeys: Set<String> = content.firstEntry()
            .value
            .keys
            .stream()
            .map { it.substringBefore('_') }
            .collect(Collectors.toSet())
        val expectedKeysLocale = content.firstEntry().key

        content.entries.forEach { entry ->
            val (locale, translations) = entry
            if (throwOnMissingKey) {
                val cleaned = translations.keys
                    .stream()
                    .map { it.substringBefore('_') }
                    .collect(Collectors.toSet())
                check(cleaned == expectedKeys) {
                    val diff = (cleaned - expectedKeys) + (expectedKeys - cleaned)
                    "Error for locales '$expectedKeysLocale' and '$locale': Keys are not the same for both locales (different keys $diff). All maps must have the same keys. To disable this check, use the ignoreMissingKeysOnBuild parameter when creating the builder."
                }
            }

            // we sort translations and add them to the final map
            finalMap[locale] = translations.entries
                .stream()
                .sorted { (a, _), (b, _) -> comparator.compare(a, b) }
                .collect(ExtendedCollectors.toLinkedHashMapUsingEntries())
        }

        var i = 0
        val pluralMapperMap = finalMap.keys
            .stream()
            .map { entryOf(it, configuration.pluralMapperFactory(it)) }
            .collect(
                Collector.of(
                    {
                        arrayOfNulls<Map.Entry<Locale, Plural.Mapper>>(finalMap.keys.size)
                            .unsafeCast<Array<Map.Entry<Locale, Plural.Mapper>>>()
                    },
                    { acc, e -> acc[i++] = e },
                    { a, b -> a + b },
                    { a -> a.toUnmodifiableMap() }
                )
            )

        val data = TranslatorData(
            defaultLocale,
            finalMap,
            configuration.interpolationDelimiter,
            { pluralMapperMap[it]!! },
            { configuration.formatters[it]!! },
            configuration.missingKeyPolicy,
        )
        return TranslatorImpl(defaultLocale, data)
    }

    companion object {

        fun create(
            configuration: TranslatorConfiguration,
            ignoreMissingKeysOnBuild: Boolean,
            contentLoaders: Map<String, () -> TranslatorBuilder.ContentLoader>,
            duplicatedKeyResolution: TranslatorBuilder.DuplicatedKeyResolution,
            defaultLocale: Locale,
            defaultCharset: Charset,
        ): TranslatorBuilder {
            val delimiter = configuration.interpolationDelimiter
            val checkRegex = translationValueFormatCheckRegex(delimiter.startDelimiter, delimiter.endDelimiter)
            val extractionRegex =
                formatAndVariableNamesExtractionRegex(delimiter.startDelimiter, delimiter.endDelimiter)
            return TranslatorBuilderImpl(
                defaultLocale,
                defaultCharset,
                configuration,
                !ignoreMissingKeysOnBuild,
                duplicatedKeyResolution,
                checkRegex,
                extractionRegex,
                contentLoaders.toUnmodifiableMap(),
            )
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

        fun keyComparator(): Comparator<String> {
            val charComparator = Comparator { o1: Char, o2: Char ->
                when {
                    '.' == o1 -> -1
                    '.' == o2 -> 1
                    else -> o1 - o2
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

        private const val SOURCE_DATA_DOCUMENTATION =
            "For more details about translation source data, see TranslationSourceData typealias documentation"

        /**
         * Gets the list of files in the given directory of a jar file.
         *
         * @param path the path to the jar file
         * @param directory the directory to walk in the jar file
         * @param resourceClass the class used to get the resource
         */
        private fun walkJarDirectory(path: String, directory: String, resourceClass: Class<*>): List<String> {
            val isAbsolute = directory.startsWith('/')

            val root = if (isAbsolute) {
                directory.drop(1)
            } else {
                val bytes = resourceClass.packageName.split('.')
                val builder = StringBuilder(resourceClass.packageName.length + 1 + directory.length)
                bytes.forEach { builder.append(it).append('/') }
                builder.append(directory)
                    .toString()
            }
            val lastSlashIndex = root.lastIndexOf('/')
            return JarFile(path)
                .stream()
                .use { file ->
                    file
                        .filter { jarEntry ->
                            val name = jarEntry.name
                            if (jarEntry.isDirectory || !name.startsWith(root)) return@filter false
                            val lastSlash = name.lastIndexOf('/')
                            lastSlash == lastSlashIndex || lastSlash == root.length
                        }
                        .map { "/${it.name}" }
                        .toList()
                }
        }

    }

    private fun loadResourceFile(path: String, clazz: Class<*>) {
        val actualPath = Path.of(path)
        val extension = actualPath.extension
        checkValidExtension(extension, actualPath)

        val nameWithoutExtension = Path.of(path).nameWithoutExtension
        val locale = parseLocale(nameWithoutExtension)
        val content = clazz.getResourceAsStream(path)!!
            .bufferedReader()
            .use { it.lines().collect(Collectors.joining("\n")) }
        addToMap(locale, parseFile(content, extension))
    }

    private fun loadPath(path: Path, charset: Charset): List<Pair<Locale, TranslationSourceMap>> =
        when {
            !path.exists() -> illegalArgument("Path $path does not exist")
            path.isDirectory() -> { // if the path is a directory, load all files in it and return the list
                Files.list(path).use { stream ->
                    stream.filter { it.isRegularFile() }
                        .map {
                            val locale = parseLocale(it.nameWithoutExtension)
                            val map = parseFile(it.readText(charset), it.extension)
                            locale to map
                        }
                        .toList()
                }
            }

            else -> { // if the path is a file, load it and store it in a one element list
                val map = parseFile(path.readText(charset), path.extension)
                val locale = parseLocale(path.nameWithoutExtension)
                listOf(locale to map)
            }
        }

    private fun parseFile(content: String, extension: String): TranslationSourceMap {
        val loader = contentLoaders[extension] ?: assertionFailed()
        return loader().load(content)
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
        require(valueFormattingCheckRegex.matches(stringValue)) {
            "Invalid translation value '$stringValue' for locale '$locale', format is not valid. $SOURCE_DATA_DOCUMENTATION. Error occurred for key '$key' in map $value."
        }
        val existingFormats = configuration.formatters.keys
        variableExtractionRegex.findAll(stringValue).forEach {
            val (_, _, formatName) = it.groupValues
            require(formatName.isEmpty() || formatName in existingFormats) {
                "Invalid translation value '$stringValue' for locale '$locale', format '$formatName' is not defined for this translator. Existing formats are: $existingFormats. $SOURCE_DATA_DOCUMENTATION. Error occurred for key '$key' in map $value."
            }
        }

        when (duplicatedKeyResolution) {
            // if resolution is FAIL, we need to check that the key is not already present
            TranslatorBuilder.DuplicatedKeyResolution.FAIL -> {
                finalMap.compute(key) { _, old ->
                    check(old == null) {
                        "Duplicate key '$key' for locale '$locale'. To avoid this error, do not add translations with the same key several times, or use a different resolution policy."
                    }
                    stringValue
                }
            }
            // if resolution is KEEP_FIRST and old is null, we can add the value
            TranslatorBuilder.DuplicatedKeyResolution.KEEP_FIRST -> finalMap.computeIfAbsent(key) { stringValue }
            // if resolution is KEEP_LAST, we always add the value
            TranslatorBuilder.DuplicatedKeyResolution.KEEP_LAST -> finalMap[key] = stringValue
        }
    }

}
