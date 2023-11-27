@file:PackagePrivate

package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.util.illegalArgument
import com.zwendo.restrikt.annotation.PackagePrivate
import java.util.*
import org.intellij.lang.annotations.Language

@Language("RegExp")
private const val KEY_IDENTIFIER_REGEX_STRING = """[a-zA-Z\d]+(?:[-_][a-zA-Z\d]+)*"""

internal val KEY_REGEX = """$KEY_IDENTIFIER_REGEX_STRING(?:\.$KEY_IDENTIFIER_REGEX_STRING)*""".toRegex()

internal fun stringListComparator(first: List<String>, second: List<String>): Int {
    val it1 = first.iterator()
    val it2 = second.iterator()

    while (true) {
        val firstElement = if (it1.hasNext()) it1.next() else return if (it2.hasNext()) -1 else 0
        val secondElement = if (it2.hasNext()) it2.next() else return 1

        val comparison = firstElement.compareTo(secondElement)
        if (comparison != 0) return comparison
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

internal class I18nException(message: String) : Throwable(message, null, false, false)

internal const val KEY_DOCUMENTATION =
    "For more details about translation keys, see TranslationKey typealias documentation."

internal const val SOURCE_DATA_DOCUMENTATION =
    "For more details about translation source data, see TranslationSourceData typealias documentation."
