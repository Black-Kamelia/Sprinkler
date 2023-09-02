@file:PackagePrivate

package com.kamelia.sprinkler.i18n

import com.zwendo.restrikt.annotation.PackagePrivate
import org.intellij.lang.annotations.Language

@Language("RegExp")
private const val KEY_IDENTIFIER_REGEX_STRING = """[a-zA-Z0-9](?:[\w-]*[a-zA-Z0-9])?"""

internal val FULL_KEY_REGEX = """$KEY_IDENTIFIER_REGEX_STRING(?:\.$KEY_IDENTIFIER_REGEX_STRING)*""".toRegex()

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
