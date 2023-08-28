@file:PackagePrivate
package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.util.unsafeCast
import com.zwendo.restrikt.annotation.PackagePrivate
import org.intellij.lang.annotations.Language
import java.util.*

@Language("RegExp")
private const val KEY_IDENTIFIER_REGEX_STRING = """[a-zA-Z0-9](?:[\w-]*[a-zA-Z0-9])?"""

internal val KEY_IDENTIFIER_REGEX = KEY_IDENTIFIER_REGEX_STRING.toRegex()

internal val FULL_KEY_REGEX = """$KEY_IDENTIFIER_REGEX_STRING(?:\.$KEY_IDENTIFIER_REGEX_STRING)*""".toRegex()

internal fun Map<*, *>.prettyPrint(): String = buildString {
    append("{\n")
    val queue = ArrayDeque<Triple<Int, String, Map<*, *>>>()
    queue.addFirst(Triple(0, "", this@prettyPrint))
    while (queue.isNotEmpty()) {
        val (indentLevel, name, map) = queue.removeFirst()
        prettyPrint(map, queue, indentLevel, name)
    }
    append("\n}")
}

private fun StringBuilder.prettyPrint(
    map: Map<*, *>,
    queue: ArrayDeque<Triple<Int, String, Map<*, *>>>,
    indentLevel: Int,
    keyName: String,
) {
    val notEmptyKey = keyName.isNotEmpty()
    if (notEmptyKey) {
        repeat(indentLevel) { append("    ") }
        append("\"")
        append(keyName)
        append("\": {\n")
    }

    map.entries.forEachIndexed { index, (key, value) ->
        if (value is Map<*, *>) {
            queue.addFirst(Triple(indentLevel + 1, key.toString(), value.unsafeCast()))
            return@forEachIndexed
        }
        repeat(indentLevel + 1) { append("    ") }
        append("\"")
        append(key)
        append("\": \"")
        append(value)
        append("\"")
        if (index < map.size - 1 || queue.peekFirst()?.first == indentLevel + 1) {
            append(",")
        }
        append("\n")
    }

    if (notEmptyKey) {
        val nextIndentation = queue.peekFirst()?.first ?: 1
        val difference = indentLevel - (nextIndentation - 1)
        val notLast = queue.isNotEmpty()
        for (i in 0 until difference) {
            repeat(indentLevel - i) { append("    ") }
            append("}")
            if (i == difference - 1 && queue.isNotEmpty()) {
                append(",")
            }
            if (notLast || i < difference - 1) {
                append("\n")
            }
        }
    }
}
