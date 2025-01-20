package com.kamelia.sprinkler.i18n.impl

import com.kamelia.sprinkler.i18n.FunctionAdapter
import com.kamelia.sprinkler.i18n.TranslationKey
import com.kamelia.sprinkler.i18n.Translator
import com.kamelia.sprinkler.i18n.Utils
import com.kamelia.sprinkler.i18n.Utils.KEY_DOCUMENTATION
import com.kamelia.sprinkler.i18n.formatting.VariableFormatter
import com.kamelia.sprinkler.i18n.impl.TranslatorBuilder.Companion.defaultContentParsers
import com.kamelia.sprinkler.util.illegalArgument
import com.zwendo.restrikt2.annotation.PackagePrivate
import java.io.BufferedReader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.util.IllformedLocaleException
import java.util.Locale
import java.util.TreeMap
import java.util.function.Function
import java.util.jar.JarFile
import java.util.stream.Collectors
import org.intellij.lang.annotations.Language
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.io.path.exists
import kotlin.io.path.extension
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.readText
import kotlin.io.path.toPath

@PackagePrivate
@Suppress("INAPPLICABLE_JVM_NAME")
internal class ContentImpl(
    val caller: Class<*>,
    configuration: TranslatorBuilder.Configuration,
) : TranslatorBuilder.Content {

    override var contentParsers = defaultContentParsers()
    override var duplicatedKeyResolution: TranslatorBuilder.DuplicatedKeyResolution =
        TranslatorBuilder.DuplicatedKeyResolution.FAIL
    override var defaultCharset: Charset = Charset.defaultCharset()

    @get:JvmName("getLocaleParserKt")
    @set:JvmName("setLocaleParserKt")
    override var localeParser: (String) -> Locale
        get() = _localeParser
        set(value) {
            _localeParser = FunctionAdapter(value::invoke)
        }

    @get:JvmName("getLocaleParser")
    @set:JvmName("setLocaleParser")
    override var localeParserJava: Function<String, Locale>
        get() = _localeParser
        set(value) {
            _localeParser = FunctionAdapter(value::apply)
        }

    private var _localeParser: FunctionAdapter<String, Locale> = FunctionAdapter {
        try {
            Locale.Builder().setLanguageTag(it.replace('_', '-')).build()
        } catch (e: IllformedLocaleException) {
            throw IllegalStateException(
                "Invalid locale '$it'. For more details about locale syntax, see java.util.Locale documentation.",
                e
            )
        }
    }

    override var ignoreUnrecognizedExtensionsInDirectory: Boolean = true

    private val valueFormattingCheckRegex: Regex
    private val variableExtractionRegex: Regex
    private val formatters: Map<String, VariableFormatter<out Any>> = configuration.formatters

    private var hasRun = false

    init {
        val delimiter =
            (configuration.interpolationDelimiter as TranslatorBuilder.Companion.InterpolationDelimiterImpl).inner
        valueFormattingCheckRegex = translationValueFormatCheckRegex(delimiter.startDelimiter, delimiter.endDelimiter)
        variableExtractionRegex =
            formatAndVariableNamesExtractionRegex(delimiter.startDelimiter, delimiter.endDelimiter)
    }

    /**
     * TreeMap used to normalize the order of the locales in the final map.
     */
    private val content = TreeMap<Locale, HashMap<String, String>> { a, b ->
        a.toLanguageTag().compareTo(b.toLanguageTag())
    }

    override fun file(path: Path, charset: Charset) {
        check(!hasRun) { "Cannot add content after the Translator has been built." }
        val extension = path.extension
        val loader = contentParsers[extension]

        try {
            // we need to load the file, or, if it is a directory, load all files in it and add them to the final map.
            loadPath(path, charset, loader)
        } catch (e: IllegalStateException) {
            throw IllegalStateException("Error while loading path $path", e)
        } catch (e: Exception) { // catch and rethrow to add the path to the error message
            throw IllegalArgumentException("Error while loading file $path", e)
        }
    }

    override fun resource(path: String, resourceClass: Class<*>, charset: Charset) {
        check(!hasRun) { "Cannot add content after the Translator has been built." }
        require(".." !in path) {
            "Resource path '$path' is invalid. It cannot contain references to parent directories ('..')."
        }

        var strPath = resourceClass
            .getProtectionDomain()
            .codeSource
            .location
            .path

        // fix for windows paths where the path starts with '/C:/...'
        if (strPath[2] == ':' && strPath[0] == '/') {
            strPath = strPath.substring(1)
        }

        if (!Path.of(strPath).isRegularFile()) { // we are not in a jar
            val url = resourceClass.getResource(path) ?: illegalArgument("Resource $path not found")
            file(url.toURI().toPath(), charset)
        } else { // we are in a jar
            val (isDirectory, resources) = walkJar(strPath, path, resourceClass)
            resources.forEach { loadResourceFile(it, resourceClass, isDirectory) }
        }
    }


    override fun map(locale: Locale, map: TranslationSourceMap) {
        check(!hasRun) { "Cannot add content after the Translator has been built." }
        addToMap(locale, map)
    }

    fun run(): Map<Locale, Map<String, String>> {
        hasRun = true
        return content
    }

    companion object {

        private fun checkKeyIsValid(key: Any?, locale: Locale, map: Map<*, *>) {
            check(key != null) {
                "Error in map $map:\nInvalid translation key for locale '$locale', key cannot be null. $KEY_DOCUMENTATION."

            }
            check(key is String) {
                "Error in map $map:\nInvalid translation key '$key' of type ${key::class.simpleName} for locale '$locale', expected String. $KEY_DOCUMENTATION."
            }

            check(Translator.keyRegex().matches(key)) {
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
            val format = """\s*,\s*${Utils.IDENTIFIER}\s*(?:$formatParams)?"""

            // and finally we can build the variable content regex
            @Language("RegExp")
            val variableContent = """\s*${Utils.IDENTIFIER}\s*(?:$format)?"""

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

    }

    /**
     * Gets the list of files in the given directory of a jar file.
     *
     * @param jarPath the path to the jar file
     * @param resourcePath the directory to walk in the jar file
     * @param resourceClass the class used to get the resource
     * @return a pair consisting of a boolean indicating if the resource is a directory and a list of all files in the
     * directory
     */
    private fun walkJar(
        jarPath: String,
        resourcePath: String,
        resourceClass: Class<*>,
    ): Pair<Boolean, List<String>> {
        val actualResourcePath = if (resourcePath == ".") { // specific case for the '.' path.
            ""
        } else { // otherwise, we remove all redundant './'
            resourcePath.replace("./", "")
        }

        val isAbsolute = actualResourcePath.startsWith('/')
        val root = if (isAbsolute) {
            actualResourcePath.drop(1)
        } else {
            val packages = resourceClass.packageName.split('.')
            val builder = StringBuilder(resourceClass.packageName.length + 1 + actualResourcePath.length)
            packages.forEach { builder.append(it).append('/') }
            builder.append(actualResourcePath)
                .toString()
        }

        val jarFile = JarFile(jarPath)

        val resource = jarFile.getJarEntry(root) ?: illegalArgument("Resource '$root' does not exist.")

        // if the resource is not a directory it means that it is a file, we return it
        if (!resource.isDirectory) return false to listOf("/$root")

        // we use the jar entry to have a final '/'
        val finalRoot = resource.name
        val lastSlashIndex = finalRoot.lastIndexOf('/')
        // otherwise, it might be a folder, we need to walk the jar file to find all files in the folder

        return true to JarFile(jarPath)
            .stream()
            .use { file ->
                file
                    .filter { jarEntry ->
                        val name = jarEntry.name

                        // skip classes, directories and entries that are not inside the root directory
                        if (jarEntry.isDirectory || name.endsWith(".class") || !name.startsWith(finalRoot)) return@filter false

                        // ensure that the depth level is 1 by checking that the last slash is the same index as the
                        // index of the last slash in the root
                        val lastSlash = name.lastIndexOf('/')
                        lastSlash == lastSlashIndex
                    }
                    .map { "/" + it.name }
                    .toList()
            }
    }

    private fun loadResourceFile(path: String, clazz: Class<*>, isDirectory: Boolean) {
        val actualPath = Path.of(path)
        val extension = actualPath.extension
        val loader = contentParsers[extension]
            ?: if (isDirectory && ignoreUnrecognizedExtensionsInDirectory) {
                return
            } else illegalArgument(
                "Unsupported file extension '$extension' for path '$path'."
            )

        val nameWithoutExtension = Path.of(path).nameWithoutExtension
        val locale = parseLocale(nameWithoutExtension)
        val content = clazz.getResourceAsStream(path)!!
            .reader()
            .run(::BufferedReader)
            .use { it.lines().collect(Collectors.joining("\n")) }
        addToMap(locale, loader.parse(content))
    }

    private fun loadPath(
        path: Path,
        charset: Charset,
        loader: TranslatorBuilder.ContentParser?,
    ) {
        when {
            !path.exists() -> illegalArgument("Path '$path' does not exist.")
            path.isDirectory() -> { // if the path is a directory, load all files in it and return the list
                Files.list(path).use { stream ->
                    stream.filter { it.isRegularFile() }
                        .forEach {
                            val locale = parseLocale(it.nameWithoutExtension)
                            val fileLoader = contentParsers[it.extension]
                                ?: if (ignoreUnrecognizedExtensionsInDirectory) {
                                    return@forEach
                                } else {
                                    illegalArgument("Unsupported file extension '${it.extension}' for path '$it'.")
                                }
                            val map = fileLoader.parse(it.readText(charset))
                            addToMap(locale, map)
                        }
                }
            }
            else -> { // if the path is a file, load it and store it in a one element list
                require(loader != null) {
                    "Unsupported file extension '${path.nameWithoutExtension}' for path '$path'."
                }
                val map = loader.parse(path.readText(charset))
                val locale = parseLocale(path.nameWithoutExtension)
                addToMap(locale, map)
            }
        }
    }

    private fun parseLocale(fileName: String): Locale = localeParser(fileName)

    private fun addToMap(locale: Locale, map: Map<*, *>) {
        if (map.isEmpty()) return // shortcut to avoid creating a Locale map
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
        val existingFormats = formatters.keys
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
