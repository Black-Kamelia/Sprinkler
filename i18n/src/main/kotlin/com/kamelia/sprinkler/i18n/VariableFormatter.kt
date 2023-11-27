package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.util.illegalArgument
import java.math.RoundingMode
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.TemporalAccessor
import java.util.*

/**
 * Represents an object that can format specific values depending on the locale.
 *
 * The syntax to use a variable formatter within a translation value is `{variable-name, formatter-name(arg1: value1,
 * arg2: value2, ...)}` where:
 * - `formatter-name` is the name of the formatter to user (e.g. `datetime`)
 * - `arg1`, `arg2`, etc. are the names of the extra parameters to pass to the formatter
 * - `value1`, `value2`, etc. are the values associated with those parameters.
 * The number of arguments and their types depend on the formatter used.
 *
 * Formatter implementations should throw an [IllegalArgumentException] when calling the [format] method with unexpected
 * `extraArgs`.
 *
 * If no arguments are needed or passed, the parentheses can be omitted (e.g. `{value, datetime}`). If the formatter is
 * not found, the [Translator.t] and [Translator.tn] methods will throw and [IllegalStateException].
 *
 * The following formatters are built-in:
 * - [VariableFormatter.currency]
 * - [VariableFormatter.date]
 * - [VariableFormatter.time]
 * - [VariableFormatter.datetime]
 * - [VariableFormatter.number]
 */
fun interface VariableFormatter {

    /**
     * Formats the given [value] using the given [locale] and [extraArgs].
     *
     * @param value the value to format
     * @param locale the locale to use
     * @param extraArgs the extra arguments to use
     * @return the formatted value
     * @throws IllegalArgumentException if some of the extra arguments are invalid or not recognized by the formatter
     * @throws Exception if an error occurs while formatting the value
     */
    fun format(value: Any, locale: Locale, extraArgs: List<String>): String

    companion object {

        /**
         * Returns a [VariableFormatter] that formats the variable as a currency using the locale, following the
         * rules of the [java.text.NumberFormat.getCurrencyInstance] method, including the currency symbol, its
         * position, and the thousands and floating point symbols.
         *
         * For example, if the locale is `en_US`, the value `1234.56` will be formatted as `$1,234.56`.
         *
         * The extra arguments are the same as that of the [number] formatter.
         *
         * @return A [VariableFormatter] that formats the variable as a currency using the locale.
         */
        @JvmStatic
        fun currency(): VariableFormatter = VariableFormatter { value, locale, extraArgs ->
            val amount = (value as? Number)?.toDouble() ?: castException(Number::class.java, value)

            val inner = NumberFormat.getCurrencyInstance(locale)
            parseNumberFormatParams(inner, extraArgs)
            inner.format(amount)
        }

        /**
         * Returns a [VariableFormatter] that formats the variable as a readable date using the locale, following the
         * rules of the [java.time.format.DateTimeFormatter.ofLocalizedDate] method.
         *
         * For example, if the locale is `en_US`, the value `2023-01-01` will be formatted as `Jan 1, 2023` by default.
         *
         * The extra argument represents the style to use to format the date, and is the same as that of the [datetime]
         * formatter.
         *
         * @return A [VariableFormatter] that formats the variable as a readable date using the locale.
         */
        @JvmStatic
        fun date(): VariableFormatter = VariableFormatter { value, locale, extraArgs ->
            val date = value as? TemporalAccessor ?: castException(TemporalAccessor::class.java, value)

            val inner = createDateTimeFormatParams(DateTimeFormatterKind.DATE, extraArgs).localizedBy(locale)
            inner.format(date)
        }

        /**
         * Returns a [VariableFormatter] that formats the variable as a readable date using the locale, following the
         * rules of the [java.time.format.DateTimeFormatter.ofLocalizedTime] method.
         *
         * For example, if the locale is `en_US`, the value `12:34:56` will be formatted as `12:34:56 PM` by default.
         *
         * The extra argument represents the style to use to format the time, and is the same as that of the [datetime]
         * formatter.
         *
         * @return A [VariableFormatter] that formats the variable as a readable date using the locale.
         */
        @JvmStatic
        fun time(): VariableFormatter = VariableFormatter { value, locale, extraArgs ->
            val time = value as? TemporalAccessor ?: castException(TemporalAccessor::class.java, value)

            val inner = createDateTimeFormatParams(DateTimeFormatterKind.TIME, extraArgs).localizedBy(locale)
            inner.format(time)
        }

        /**
         * Returns a [VariableFormatter] that formats the variable as a readable date using the locale, following the
         * rules of the [java.time.format.DateTimeFormatter.ofLocalizedDateTime] method.
         *
         * For example, if the locale is `en_US`, the value `2023-01-01T12:34:56` will be formatted as
         * `Jan 1, 2023, 12:34:56 PM` by default.
         *
         * The extra arguments represent the style to use to format the date and time respectively:
         * - `dateStyle`: the style to use to format the date. The value must be a string that can be parsed by the
         *   [java.time.format.FormatStyle.valueOf] method, and the default is `MEDIUM`.
         * - `timeStyle`: the style to use to format the time. . The value must be a string that can be parsed by the
         *   [java.time.format.FormatStyle.valueOf] method, and the default is `MEDIUM`.
         *
         * @return A [VariableFormatter] that formats the variable as a readable date using the locale.
         */
        @JvmStatic
        fun datetime(): VariableFormatter = VariableFormatter { value, locale, extraArgs ->
            val dateTime = value as? TemporalAccessor ?: castException(TemporalAccessor::class.java, value)

            val inner = createDateTimeFormatParams(DateTimeFormatterKind.DATE_TIME, extraArgs).localizedBy(locale)
            inner.format(dateTime)
        }

        /**
         * Returns a [VariableFormatter] that formats the variable as a readable number using the locale, following the
         * rules of the [java.text.NumberFormat.getInstance] method, including the thousands and floating point symbols.
         *
         * For example, if the locale is `en_US`, the value `1234.56` will be formatted as `1,234.56`.
         *
         * The extra arguments are restrictions to apply to the value:
         * - `minIntDigits`: the minimum number of digits to use for the integer part of the number (will be padded with
         *   non-significant zeros if needed). For example, if the value is `1,234.56` and the parameter is `6`, the
         *   result will be `001,234.56`.
         * - `maxIntDigits`: the maximum number of digits to use for the integer part of the number (will be truncated
         *   from the left if needed). For example, if the value is `1234.56` and the parameter is `2`, the result will
         *   be `34.56`.
         * - `minFracDigits`: the minimum number of digits to use for the fractional part of the number (will be padded
         *   with non-significant zeros if needed). For example, if the value is `1234.56` and the parameter is `4`, the
         *   result will be `1,234.5600`.
         * - `maxFracDigits`: the maximum number of digits to use for the fractional part of the number (will be
         *   truncated from the right if needed). For example, if the value is `1234.5678` and the parameter is `2`, the
         *   result will be `1,234.56`.
         * - `groupingUsed`: whether to use the grouping separator (e.g. `,` in `1,234.56`) or not. The value must be a
         *   boolean, and the default is `true`.
         * - `roundingMode`: the rounding mode to use when truncating the number. The value must be a string that can be
         *   parsed by the [java.math.RoundingMode.valueOf] method, and the default is `HALF_EVEN`.
         *
         * @return A [VariableFormatter] that formats the variable as a readable number using the locale.
         */
        @JvmStatic
        fun number(): VariableFormatter = VariableFormatter { value, locale, extraArgs ->
            val number = (value as? Number) ?: castException(Number::class.java, value)

            val inner = NumberFormat.getInstance(locale)
            parseNumberFormatParams(inner, extraArgs)
            inner.format(number)
        }

        /**
         * Returns a map of all the built-in variable formatters, with their name as the key.
         *
         * The names of the formatters are assigned according to the name of their factory method, e.g. the name of the
         * [currency] formatter is `currency`.
         *
         * @return the map of all the built-in variable formatters
         */
        @JvmStatic
        fun builtins(): Map<String, VariableFormatter> = mapOf(
            ::currency.name to currency(),
            ::date.name to date(),
            ::time.name to time(),
            ::datetime.name to datetime(),
            ::number.name to number(),
        )

    }

}

private fun parseNumberFormatParams(formatter: NumberFormat, params: List<String>) {
    params.forEach {
        val tokens = it.split(":")
        checkTokens(it, tokens.size)
        val (key, value) = tokens
        when (key) {
            "minIntDigits" -> formatter.minimumIntegerDigits = value.toIntOrException(it)
            "maxIntDigits" -> formatter.maximumIntegerDigits = value.toIntOrException(it)
            "minFracDigits" -> formatter.minimumFractionDigits = value.toIntOrException(it)
            "maxFracDigits" -> formatter.maximumFractionDigits = value.toIntOrException(it)
            "groupingUsed" -> formatter.isGroupingUsed = value.toBooleanStrictOrNull()
                ?: illegalArgument("Invalid parameter, expected 'true' or 'false', got '$value'.")
            "roundingMode" -> formatter.roundingMode = RoundingMode.valueOf(value)
            else -> illegalArgument("Unknown parameter: $key")
        }
    }
}

private enum class DateTimeFormatterKind {
    DATE,
    TIME,
    DATE_TIME,
}

private fun createDateTimeFormatParams(kind: DateTimeFormatterKind, params: List<String>): DateTimeFormatter {
    var firstFormat: FormatStyle = DEFAULT_FORMAT_STYLE
    var secondFormat: FormatStyle = DEFAULT_FORMAT_STYLE
    params.forEach {
        val tokens = it.split(":")
        checkTokens(it, tokens.size)
        val (key, value) = tokens
        if ("dateStyle" == key && (DateTimeFormatterKind.DATE == kind || DateTimeFormatterKind.DATE_TIME == kind)) {
            firstFormat = formatStyle(value)
        } else if ("timeStyle" == key && (kind == DateTimeFormatterKind.TIME || kind == DateTimeFormatterKind.DATE_TIME)) {
            if (DateTimeFormatterKind.DATE_TIME == kind) {
                secondFormat = formatStyle(value)
            } else {
                firstFormat = formatStyle(value)
            }
        } else {
            illegalArgument("Unsupported parameter: $key")
        }
    }

    return when (kind) {
        DateTimeFormatterKind.DATE -> DateTimeFormatter.ofLocalizedDate(firstFormat)
        DateTimeFormatterKind.TIME -> DateTimeFormatter.ofLocalizedTime(firstFormat)
        DateTimeFormatterKind.DATE_TIME -> DateTimeFormatter.ofLocalizedDateTime(firstFormat, secondFormat)
    }
}

private fun formatStyle(string: String): FormatStyle = try {
    FormatStyle.valueOf(string.uppercase(Locale.ENGLISH))
} catch (e: IllegalArgumentException) {
    illegalArgument("Invalid format style: $string")
}

@Suppress("NOTHING_TO_INLINE")
private inline fun String.toIntOrException(token: String): Int {
    return toIntOrNull() ?: illegalArgument("Invalid number format parameter: $token")
}

@Suppress("NOTHING_TO_INLINE")
private inline fun castException(expected: Class<*>, actual: Any?): Nothing {
    var message = "Expected ${expected.simpleName}, got '$actual'"
    if (actual != null) {
        message += " (${actual::class.simpleName})"
    }
    illegalArgument(message)
}

private fun checkTokens(root: String, size: Int) {
    require(size == 2) {
        "Invalid number format parameter, expected <key>:<value> but was '$root'"
    }
}

private val DEFAULT_FORMAT_STYLE = FormatStyle.MEDIUM
