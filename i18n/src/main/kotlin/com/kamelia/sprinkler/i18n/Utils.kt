@file:PackagePrivate

package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.util.illegalArgument
import com.zwendo.restrikt.annotation.PackagePrivate
import java.util.*
import org.intellij.lang.annotations.Language

@Language("RegExp")
internal const val IDENTIFIER = """[a-zA-Z\d]+(?:[-_][a-zA-Z\d]+)*"""

internal val KEY_REGEX = """$IDENTIFIER(?:\.$IDENTIFIER)*""".toRegex()

internal fun keyComparator(): Comparator<String> {
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

internal fun keyNotFound(
    key: TranslationKey,
    options: Map<String, Any>,
    locale: Locale,
    fallbackLocale: Locale?,
    fallbacks: Array<out String>,
): Nothing {
    val builder = StringBuilder()
    builder.append("No translation found for parameters: key='")
        .append(key)
        .append("', locale='")
        .append(locale)
        .append("', fallbackLocale='")
        .append(fallbackLocale)
        .append("', fallbacks='")

    fallbacks.joinTo(builder, ", ", "[", "]")

    builder.append("', extraArgs='")
        .append(options)
        .append("'. ")

    illegalArgument(builder.toString())
}

internal const val KEY_DOCUMENTATION =
    "For more details about translation keys, see TranslationKey typealias documentation."

internal const val SOURCE_DATA_DOCUMENTATION =
    "For more details about translation source data, see TranslationSourceData typealias documentation."

private val VARIABLE = run {
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
    @Language("RegExp")
    val formatParams = """\(($formatParam,)*$formatParam(?<!\\)\)""" // negative lookbehind to avoid escaping the closing parenthesis

    // which allows us to build the format regex
    @Language("RegExp")
    val format = """\s*,\s*$IDENTIFIER\s*(?:$formatParams)?"""

    // and finally we can build the variable regex
    @Language("RegExp")
    val finalRegex = """\s*$IDENTIFIER(?:$format)?"""

    finalRegex
}

fun translationValueFormatRegex(start: Char, end: Char): Regex {
    @Language("RegExp")
    val notStartChar = """[^$start]"""
    @Language("RegExp")
    val escapedStartChar = """(?<=\\)[$start]"""
    @Language("RegExp")
    val validVariable = """[$start]$VARIABLE[$end]"""
    return """(?:$notStartChar|$escapedStartChar|$validVariable)*""".toRegex()
}
