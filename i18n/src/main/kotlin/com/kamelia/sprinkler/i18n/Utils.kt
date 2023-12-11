@file:PackagePrivate

package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.util.illegalArgument
import com.zwendo.restrikt.annotation.PackagePrivate
import java.util.*
import org.intellij.lang.annotations.Language

@Language("RegExp")
internal const val IDENTIFIER = """[a-zA-Z\d]+(?:[-_][a-zA-Z\d]+)*"""

@Language("RegExp")
internal const val FORMAT_REGEX = """\s*,\s*$IDENTIFIER(?:\([^,()]+(?:\s*,\s*[^,()]+)*\))?"""

private val VARIABLE_REGEX = """$IDENTIFIER(?:$FORMAT_REGEX)?""".toRegex()

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

private enum class State(
    val acceptMultipleDashOrUnderscore: Boolean,
    val acceptBackslash: Boolean,
) {

    NORMAL(true, true),
    VARIABLE_NAME(false, false),
    VARIABLE_FORMAT_NAME(false, false),
    VARIABLE_FORMAT_NAME_LEADING_SPACES(false, false),
    VARIABLE_FORMAT_PARAMETERS_KEY(true, true),
    VARIABLE_FORMAT_PARAMETERS_VALUE(true, true),
    VARIABLE_FORMAT_AFTER(false, false),
}

private fun isValidChar(char: Char): Boolean = char.isLetterOrDigit() || char == '_' || char == '-'

private fun Char.isDashOrUnderscore(): Boolean = this == '-' || this == '_'

// TODO someday try to use a regex instead of this big boi
internal fun checkValueFormat(value: String, start: Char, end: Char) {
    var state = State.NORMAL
    var escaping = false
    var count = 0
    var lastChar = ' ' // any char that is not a dash or an underscore is fine

    value.forEachIndexed { index, char ->
        // check for consecutive dashes or underscores
        check(
            state.acceptMultipleDashOrUnderscore
                        || !char.isDashOrUnderscore()
                        || !lastChar.isDashOrUnderscore()
        ) {
            "Invalid character '$char' at index $index: $value"
        }

        val startsEscaping = if (!escaping && char == '\\') { // start escaping
            check(state.acceptBackslash) {
                "Unexpected character '$char' at index $index: $value"
            }
            escaping = true
            true
        } else {
            false
        }

        when (state) {
            State.NORMAL -> {
                if (!escaping && start == char) { // only transition is to variable name
                    state = State.VARIABLE_NAME
                }
            }
            State.VARIABLE_NAME -> {
                if (end == char || ',' == char) { // end of variable or start of format
                    check(count > 0) { // check for empty variable name
                        "Empty variable name at index $index: $value"
                    }
                    check(!lastChar.isDashOrUnderscore()) { // check for trailing dash or underscore
                        "Invalid character '$char' at index $index: $value"
                    }
                    count = 0 // reset count
                    state = if (end == char) { // update state
                        State.NORMAL // end of variable
                    } else {
                        State.VARIABLE_FORMAT_NAME_LEADING_SPACES // start of format
                    }
                } else { // otherwise, just check for valid char
                    check(isValidChar(char)) {
                        "Invalid character '$char' at index $index: $value"
                    }
                    count++
                }
            }
            State.VARIABLE_FORMAT_NAME_LEADING_SPACES -> {
                if (' ' != char) { // only transition is to variable format name
                    check(char.isLetterOrDigit()) { // check for valid char, this check disallows dashes and underscores
                        "Invalid character '$char' at index $index: $value"
                    }
                    state = State.VARIABLE_FORMAT_NAME
                    count = 1 // because we already have one valid char
                }
            }
            State.VARIABLE_FORMAT_NAME -> {
                if (end == char || '(' == char) { // end of format or start of format parameters
                    check(count > 0) { // check for empty format name
                        "Empty format name at index $index: $value"
                    }
                    check(!lastChar.isDashOrUnderscore()) { // check for trailing dash or underscore
                        "Invalid character '$char' at index $index: $value"
                    }
                    count = 0
                    state = if (end == char) { // update state
                        State.NORMAL // end of format
                    } else {
                        State.VARIABLE_FORMAT_PARAMETERS_KEY // start of format parameters
                    }
                } else { // otherwise, just check for valid char
                    check(isValidChar(char)) {
                        "Invalid character '$char' at index $index: $value"
                    }
                    count++
                }
            }
            State.VARIABLE_FORMAT_PARAMETERS_KEY -> {
                if (escaping) {
                    count++
                } else {
                    when (char) {
                        ':' -> { // only transition is to variable format parameters value
                            if (count == 0) { // check for empty format parameter key
                                throw IllegalStateException("Empty format parameter key at index $index: $value")
                            }
                            count = 0
                            state = State.VARIABLE_FORMAT_PARAMETERS_VALUE
                        }
                        // end of variable or start of another format parameter is not allowed
                        ')', ',', end -> throw IllegalStateException("Missing format parameter value at index $index: $value")
                        else -> count++
                    }
                }
            }
            State.VARIABLE_FORMAT_PARAMETERS_VALUE -> {
                if (escaping) {
                    count++
                } else {
                    when (char) {
                        ')', ',' -> { // only transition is to variable format parameters key or end of format
                            if (count == 0) { // check for empty format parameter value
                                throw IllegalStateException("Empty format parameter value at index $index: $value")
                            }
                            count = 0
                            state = if (',' == char) { // update state
                                State.VARIABLE_FORMAT_PARAMETERS_KEY // start of another format parameter
                            } else {
                                State.VARIABLE_FORMAT_AFTER // end of format
                            }
                        }
                        // end of variable or start of another format parameter is not allowed
                        end -> throw IllegalStateException("Missing closing parenthesis for format parameter value at index $index: $value")
                        else -> count++
                    }
                }
            }
            State.VARIABLE_FORMAT_AFTER -> {
                check(end == char) { // only transition is to end of variable
                    "Unexpected character '$char' at index $index: $value"
                }
                state = State.NORMAL
            }
        }

        if (!startsEscaping && escaping) { // stop escaping
            escaping = false
        }
        lastChar = char
    }

    check(State.NORMAL == state) {
        "Missing closing delimiter for variable at index ${value.length - 1}: $value"
    }
}

internal const val KEY_DOCUMENTATION =
    "For more details about translation keys, see TranslationKey typealias documentation."

internal const val SOURCE_DATA_DOCUMENTATION =
    "For more details about translation source data, see TranslationSourceData typealias documentation."
