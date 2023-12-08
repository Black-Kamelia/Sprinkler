@file:PackagePrivate

package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.util.illegalArgument
import com.zwendo.restrikt.annotation.PackagePrivate
import java.util.*
import org.intellij.lang.annotations.Language

@Language("RegExp")
private const val KEY_IDENTIFIER_REGEX_STRING = """[a-zA-Z\d]+(?:[-_][a-zA-Z\d]+)*"""

internal val KEY_REGEX = """$KEY_IDENTIFIER_REGEX_STRING(?:\.$KEY_IDENTIFIER_REGEX_STRING)*""".toRegex()

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
