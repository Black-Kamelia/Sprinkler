package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.util.castOrNull
import com.kamelia.sprinkler.util.illegalArgument
import java.math.RoundingMode
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.TemporalAccessor
import java.util.Locale

/**
 * Represents an object that can format specific values depending on the locale. Formatting is performed through the
 * [format] method.
 *
 * Formatter implementations should throw an [IllegalArgumentException] when calling the [format] method with unexpected
 * `extraArgs`.
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
    fun format(value: Any, locale: Locale, extraArgs: List<Pair<String, String>>): String

    /**
     * Formats the given [value] using the given [locale]. This is a convenience method that calls the [format] method
     * with an [empty][emptyList] list of extra arguments.
     *
     * @param value the value to format
     * @param locale the locale to use
     * @return the formatted value
     * @throws IllegalArgumentException if some of the extra arguments are invalid or not recognized by the formatter
     * @throws Exception if an error occurs while formatting the value
     */
    fun format(value: Any, locale: Locale): String = format(value, locale, emptyList())

    companion object {

        /**
         * Returns a [VariableFormatter] that formats the variable as a currency using the locale, following the
         * rules of the [NumberFormat.getCurrencyInstance][java.text.NumberFormat.getCurrencyInstance] method, including
         * the currency symbol, its position, and the thousands and floating point symbols.
         *
         * For example, if the locale is `en_US`, the value `1234.56` will be formatted as `$1,234.56`.
         *
         * The extra arguments are the same as that of the [number] formatter.
         *
         * @return A [VariableFormatter] that formats the variable as a currency using the locale.
         */
        @JvmStatic
        fun currency(): VariableFormatter = VariableFormatter { value, locale, extraArgs ->
            val amount = value.castOrNull<Number>()?.toDouble()
                ?: throw ClassCastException("The value ($value) must be of type Number")

            val inner = NumberFormat.getCurrencyInstance(locale)
            parseNumberFormatParams(inner, extraArgs)
            inner.format(amount)
        }

        /**
         * Returns a [VariableFormatter] that formats the variable as a readable date using the locale, following the
         * rules of the [DateTimeFormatter.ofLocalizedDate][java.time.format.DateTimeFormatter.ofLocalizedDate] method.
         *
         * For example, if the locale is `en_US`, the value `2023-01-01` will be formatted as `Jan 1, 2023` by default.
         *
         * The only recognized extra argument is `dateStyle`, which specify the style to use to format the date. The
         * value must be a string that can be parsed by the [FormatStyle.valueOf][java.time.format.FormatStyle.valueOf]
         * method, and the default is `MEDIUM`.
         *
         * @return A [VariableFormatter] that formats the variable as a readable date using the locale.
         */
        @JvmStatic
        fun date(): VariableFormatter = VariableFormatter { value, locale, extraArgs ->
            val date = value.castOrNull<TemporalAccessor>()
                ?: throw ClassCastException("The value ($value) must be of type TemporalAccessor")

            val inner = createDateTimeFormatParams(DateTimeFormatterKind.DATE, extraArgs).localizedBy(locale)
            inner.format(date)
        }

        /**
         * Returns a [VariableFormatter] that formats the variable as a readable date using the locale, following the
         * rules of the [DateTimeFormatter.ofLocalizedTime][java.time.format.DateTimeFormatter.ofLocalizedTime] method.
         *
         * For example, if the locale is `en_US`, the value `12:34:56` will be formatted as `12:34:56 PM` by default.
         *
         * The only recognized extra argument is `timeStyle`, which specify the style to use to format the time. The
         * value must be a string that can be parsed by the [FormatStyle.valueOf][java.time.format.FormatStyle.valueOf]
         * method, and the default is `MEDIUM`.
         *
         * @return A [VariableFormatter] that formats the variable as a readable date using the locale.
         */
        @JvmStatic
        fun time(): VariableFormatter = VariableFormatter { value, locale, extraArgs ->
            val time = value.castOrNull<TemporalAccessor>()
                ?: throw ClassCastException("The value ($value) must be of type TemporalAccessor")

            val inner = createDateTimeFormatParams(DateTimeFormatterKind.TIME, extraArgs).localizedBy(locale)
            inner.format(time)
        }

        /**
         * Returns a [VariableFormatter] that formats the variable as a readable date using the locale, following the
         * rules of the [DateTimeFormatter.ofLocalizedDateTime][java.time.format.DateTimeFormatter.ofLocalizedDateTime]
         * method.
         *
         * For example, if the locale is `en_US`, the value `2023-01-01T12:34:56` will be formatted as
         * `Jan 1, 2023, 12:34:56 PM` by default.
         *
         * The extra arguments are the same as that of the [date] and [time] formatters.
         *
         * @return A [VariableFormatter] that formats the variable as a readable date using the locale.
         */
        @JvmStatic
        fun datetime(): VariableFormatter = VariableFormatter { value, locale, extraArgs ->
            val dateTime = value.castOrNull<TemporalAccessor>()
                ?: throw ClassCastException("The value ($value) must be of type TemporalAccessor")

            val inner = createDateTimeFormatParams(DateTimeFormatterKind.DATE_TIME, extraArgs).localizedBy(locale)
            inner.format(dateTime)
        }

        /**
         * Returns a [VariableFormatter] that formats the variable as a readable number using the locale, following the
         * rules of the [NumberFormat.getInstance][java.text.NumberFormat.getInstance] method, including the thousands
         * and floating point symbols.
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
         *   parsed by the [RoundingMode.valueOf][java.math.RoundingMode.valueOf] method, and the default is
         *   `HALF_EVEN`.
         *
         * @return A [VariableFormatter] that formats the variable as a readable number using the locale.
         */
        @JvmStatic
        fun number(): VariableFormatter = VariableFormatter { value, locale, extraArgs ->
            val number = value.castOrNull<Number>()
                ?: throw ClassCastException("The value ($value) must be of type Number")

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

        private fun parseNumberFormatParams(formatter: NumberFormat, params: List<Pair<String, String>>) {
            params.forEach {
                val (key, value) = it
                when (key) {
                    "minIntDigits" -> formatter.minimumIntegerDigits = value.toInt()
                    "maxIntDigits" -> formatter.maximumIntegerDigits = value.toInt()
                    "minFracDigits" -> formatter.minimumFractionDigits = value.toInt()
                    "maxFracDigits" -> formatter.maximumFractionDigits = value.toInt()
                    "groupingUsed" -> formatter.isGroupingUsed = value.toBooleanStrict()
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

        private fun createDateTimeFormatParams(
            kind: DateTimeFormatterKind,
            params: List<Pair<String, String>>,
        ): DateTimeFormatter {
            var firstFormat: FormatStyle = DEFAULT_FORMAT_STYLE
            var secondFormat: FormatStyle = DEFAULT_FORMAT_STYLE
            params.forEach {
                val (key, value) = it
                if ("dateStyle" == key && (DateTimeFormatterKind.DATE == kind || DateTimeFormatterKind.DATE_TIME == kind)) {
                    firstFormat = formatStyle(value)
                } else if ("timeStyle" == key && (kind == DateTimeFormatterKind.TIME || kind == DateTimeFormatterKind.DATE_TIME)) {
                    if (DateTimeFormatterKind.DATE_TIME == kind) {
                        secondFormat = formatStyle(value)
                    } else {
                        firstFormat = formatStyle(value)
                    }
                } else {
                    illegalArgument("Unknown parameter: $key")
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

        private val DEFAULT_FORMAT_STYLE = FormatStyle.MEDIUM

    }

}
