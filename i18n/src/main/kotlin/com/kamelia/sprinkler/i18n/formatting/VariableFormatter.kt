package com.kamelia.sprinkler.i18n.formatting

import com.kamelia.sprinkler.i18n.formatting.VariableFormatter.Argument
import com.kamelia.sprinkler.util.illegalArgument
import java.io.IOException
import java.math.RoundingMode
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.TemporalAccessor
import java.util.Locale

/**
 * Represents an object that can format specific values depending on the locale. Formatting is performed through the
 * [format] method, which writes the formatted value to an [Appendable] using the given locale and extra arguments.
 *
 * Formatter implementations should ignore any extra arguments that they do not recognize.
 *
 * Here is an example of how to create and use a custom formatter:
 *
 * ```
 * val markdownFormatter = VariableFormatter<String> { appendable, value, locale, args ->
 *     val bold = args.firstOrNull { it.name == "bold" }?.value as Boolean?
 *         ?: false
 *     val italic = args.firstOrNull { it.name == "italic" }?.value as Boolean?
 *         ?: false
 *     val strikethrough = args.firstOrNull { it.name == "strikethrough" }?.value as Boolean?
 *         ?: false
 *
 *     if (bold) appendable.append("**")
 *     if (italic) appendable.append("_")
 *     if (strikethrough) appendable.append("~~")
 *
 *     appendable.append(value)
 *
 *     if (strikethrough) appendable.append("~~")
 *     if (italic) appendable.append("_")
 *     if (bold) appendable.append("**")
 * }
 *
 * val appendable = StringBuilder()
 * val arguments = mapOf("bold" to true, "italic" to true)
 * markdownFormatter.format(appendable, "Hello, world!", Locale.ENGLISH, arguments)
 * println(appendable) // Output: **_Hello, world!_**
 *
 * ```
 *
 * As demonstrated in the example, the [format] method also accept an array of extra [arguments][Argument], which can
 * be used to customize the formatting process. Implementations should ignore any extra arguments that they do not
 * recognize and allow duplicate arguments by using the last occurrence of the argument.
 *
 * @param T the type of the value to format
 */
fun interface VariableFormatter<T> {

    /**
     * Formats the given [value] to an [Appendable] using the given [locale] and [args].
     *
     * **NOTE**: This method declares throwing an [IOException] because the [Appendable] interface declares it. It is
     * purely a simplification for java interop, to avoid having using try-catch blocks when writing custom formatters.
     *
     * @param appendable the appendable to write the formatted value to
     * @param value the value to format
     * @param locale the locale to use
     * @param args the extra arguments to use
     * @throws RuntimeException if an error occurs during formatting
     */
    @Throws(IOException::class)
    fun format(appendable: Appendable, value: T, locale: Locale, vararg args: Argument)

    /**
     * Represents an argument to be used in the formatting process. It is a simple key-value pair.
     *
     * @see VariableFormatter
     */
    sealed interface Argument {

        /**
         * The name of the argument.
         */
        val name: String

        /**
         * The value of the argument.
         */
        val value: Any

    }

    companion object {

        /**
         * Creates a [Argument] with the given [name] and [value].
         *
         * @param name the name of the parameter
         * @param value the value of the parameter
         * @return a new [Argument] with the given name and value
         */
        fun formatArgument(name: String, value: Any): Argument = VariableFormatterParameterImpl(name, value)

        /**
         * Returns a [VariableFormatter] that formats the variable as a readable number using the locale, following the
         * rules of the [NumberFormat.getInstance][java.text.NumberFormat.getInstance] method, including the thousands
         * and floating point symbols.
         *
         * For example, if the locale is `en_US`, the value `1234.56` will be formatted as `1,234.56`.
         *
         * The meaningful extra arguments are (with their valid types in parentheses):
         *
         * - `minIntDigits` ([`Int`][Int] or [`String`][String]): the minimum number of digits to use for the integer
         *   part of the number (will be padded with non-significant zeros if needed). For example, if the value is
         *   `1,234.56` and the parameter is `6`, the result will be `001,234.56`. The default is `1`.
         *
         *
         * - `maxIntDigits` ([`Int`][Int] or [`String`][String]): the maximum number of digits to use for the integer
         *   part of the number (will be truncated from the left if needed). For example, if the value is `1234.56` and
         *   the parameter is `2`, the result will be `34.56`.
         *
         *
         * - `minFracDigits` ([`Int`][Int] or [`String`][String]): the minimum number of digits to use for the
         *   fractional part of the number (will be padded with non-significant zeros if needed). For example, if the
         *   value is `1234.56` and the parameter is `4`, the result will be `1,234.5600`. The default is `0`.
         *
         *
         * - `maxFracDigits` ([`Int`][Int] or [`String`][String]): the maximum number of digits to use for the
         *   fractional part of the number (will be truncated from the right if needed). For example, if the value is
         *   `1234.5678` and the parameter is `2`, the result will be `1,234.56`.
         *
         *
         * - `groupingUsed` ([`Boolean`][Boolean] or [`String`][String]): whether to use the grouping separator (e.g.
         *   `,` in `1,234.56`) or not. The default is `true`.
         *
         *
         * - `roundingMode` ([`RoundingMode`][RoundingMode] or [`String`][String]): the rounding mode to use when
         *   truncating the number. The default is [`HALF_EVEN`][RoundingMode.HALF_EVEN].
         *
         * **NOTE**: Whenever a [String] parameter is used, for any of the above-described parameters, it will be parsed
         * as the corresponding type using traditional parsing methods (e.g. [Integer.parseInt],
         * [Enum.valueOf][java.lang.Enum.valueOf], [Boolean.parseBoolean][java.lang.Boolean.parseBoolean]).
         *
         * @return A [VariableFormatter] that formats the variable as a readable number using the locale.
         */
        @JvmStatic
        fun number(): VariableFormatter<Number> = VariableFormatter { appendable, value, locale, extraArgs ->
            val inner = NumberFormat.getInstance(locale)
            parseNumberFormatParams(inner, extraArgs)
            appendable.append(inner.format(value))
        }

        /**
         * Returns a [VariableFormatter] that formats a [Number] as a currency depending on the locale, using the
         * [NumberFormat.getCurrencyInstance][java.text.NumberFormat.getCurrencyInstance] method, including the currency
         * symbol, its position, and the thousands and floating point symbols.
         *
         * For example, if the locale is `en_US`, the value `1234.56` will be formatted as `$1,234.56`.
         *
         * The meaningful extra arguments are (with their valid types in parentheses):
         *
         * - see [number]
         *
         * @return A [VariableFormatter] that formats the variable as a currency using the locale.
         */
        @JvmStatic
        fun currency(): VariableFormatter<Number> = VariableFormatter { appendable, value, locale, extraArgs ->
            val inner = NumberFormat.getCurrencyInstance(locale)
            parseNumberFormatParams(inner, extraArgs)
            appendable.append(inner.format(value))
        }

        /**
         * Returns a [VariableFormatter] that formats the variable as a compact number using the locale, following the
         * rules of the [NumberFormat.getCompactNumberInstance][java.text.NumberFormat.getCompactNumberInstance] (with
         * a style of [`SHORT`][NumberFormat.Style.SHORT]) method.
         *
         * For example, if the locale is `en_US`, the value `1234` will be formatted as `1.2K`.
         *
         * The meaningful extra arguments are (with their valid types in parentheses):
         *
         * - see [number]
         *
         * @return A [VariableFormatter] that formats the variable as a compact number using the locale.
         */
        @JvmStatic
        fun compactNumber(): VariableFormatter<Number> = VariableFormatter { appendable, value, locale, extraArgs ->
            val inner = NumberFormat.getCompactNumberInstance(locale, NumberFormat.Style.SHORT)
            parseNumberFormatParams(inner, extraArgs)
            appendable.append(inner.format(value))
        }

        /**
         * Returns a [VariableFormatter] that formats the variable as a percentage using the locale, following the rules
         * of the [NumberFormat.getPercentInstance][java.text.NumberFormat.getPercentInstance] method.
         *
         * For example, if the locale is `en_US`, the value `0.12` will be formatted as `12%`.
         *
         * The meaningful extra arguments are (with their valid types in parentheses):
         *
         * - see [number]
         *
         * @return A [VariableFormatter] that formats the variable as a percentage using the locale.
         */
        @JvmStatic
        fun percent(): VariableFormatter<Number> = VariableFormatter { appendable, value, locale, extraArgs ->
            val inner = NumberFormat.getPercentInstance(locale)
            parseNumberFormatParams(inner, extraArgs)
            appendable.append(inner.format(value))
        }

        /**
         * Returns a [VariableFormatter] that formats the variable as a readable date using the locale, following the
         * rules of the [DateTimeFormatter.ofLocalizedDate][java.time.format.DateTimeFormatter.ofLocalizedDate] method.
         *
         * For example, if the locale is `en_US`, the value `2023-01-01` will be formatted as `Jan 1, 2023` by default.
         *
         * The meaningful extra arguments are (with their valid types in parentheses):
         *
         * - `dateStyle` ([`FormatStyle`][FormatStyle] or [`String`][String]): the style to use to format the date. The
         * default is [`MEDIUM`][FormatStyle.MEDIUM].
         *
         * **NOTE**: Whenever a [String] parameter is used, it will be parsed as the corresponding type using
         * traditional parsing methods (e.g. [Enum.valueOf][java.lang.Enum.valueOf]).
         *
         * @return A [VariableFormatter] that formats the variable as a readable date using the locale.
         */
        @JvmStatic
        fun date(): VariableFormatter<TemporalAccessor> = VariableFormatter { appendable, value, locale, extraArgs ->
            val inner = createDateTimeFormatParams(DateTimeFormatterKind.DATE, extraArgs).localizedBy(locale)
            inner.formatTo(value, appendable)
        }

        /**
         * Returns a [VariableFormatter] that formats the variable as a readable date using the locale, following the
         * rules of the [DateTimeFormatter.ofLocalizedTime][java.time.format.DateTimeFormatter.ofLocalizedTime] method.
         *
         * For example, if the locale is `en_US`, the value `12:34:56` will be formatted as `12:34:56 PM` by default.
         *
         * The meaningful extra arguments are (with their valid types in parentheses):
         *
         * - `timeStyle` ([`FormatStyle`][FormatStyle] or [`String`][String]): the style to use to format the time. The
         * default is [`MEDIUM`][FormatStyle.MEDIUM].
         *
         * **NOTE**: Whenever a [String] parameter is used, it will be parsed as the corresponding type using
         *
         * @return A [VariableFormatter] that formats the variable as a readable date using the locale.
         */
        @JvmStatic
        fun time(): VariableFormatter<TemporalAccessor> = VariableFormatter { appendable, value, locale, extraArgs ->
            val inner = createDateTimeFormatParams(DateTimeFormatterKind.TIME, extraArgs).localizedBy(locale)
            inner.formatTo(value, appendable)
        }

        /**
         * Returns a [VariableFormatter] that formats the variable as a readable date using the locale, following the
         * rules of the [DateTimeFormatter.ofLocalizedDateTime][java.time.format.DateTimeFormatter.ofLocalizedDateTime]
         * method.
         *
         * For example, if the locale is `en_US`, the value `2023-01-01T12:34:56` will be formatted as
         * `Jan 1, 2023, 12:34:56 PM` by default.
         *
         * The meaningful extra arguments are (with their valid types in parentheses):
         *
         * - `dateStyle` ([`FormatStyle`][FormatStyle] or [`String`][String]): the style to use to format the date. The
         * default is [`MEDIUM`][FormatStyle.MEDIUM].
         *
         *
         * - `timeStyle` ([`FormatStyle`][FormatStyle] or [`String`][String]): the style to use to format the time. The
         * default is [`MEDIUM`][FormatStyle.MEDIUM].
         *
         * **NOTE**: Whenever a [String] parameter is used, it will be parsed as the corresponding type using
         * traditional parsing methods (e.g. [Enum.valueOf][java.lang.Enum.valueOf]).
         *
         * @return A [VariableFormatter] that formats the variable as a readable date using the locale.
         */
        @JvmStatic
        fun datetime(): VariableFormatter<TemporalAccessor> =
            VariableFormatter { appendable, value, locale, extraArgs ->
                val inner = createDateTimeFormatParams(DateTimeFormatterKind.DATE_TIME, extraArgs).localizedBy(locale)
                inner.formatTo(value, appendable)
            }

        /**
         * Returns a map of all the built-in variable formatters, namely:
         *
         * - [number]
         * - [currency]
         * - [compactNumber]
         * - [percent]
         * - [date]
         * - [time]
         * - [datetime]
         *
         * The names of the formatters are assigned according to the name of their factory method, e.g., the name of the
         * [currency] formatter is `currency`.
         *
         * @return the map of all the built-in variable formatters
         */
        @JvmStatic
        fun builtins(): MutableMap<String, VariableFormatter<out Any>> = hashMapOf(
            Companion::currency.name to currency(),
            Companion::compactNumber.name to compactNumber(),
            Companion::percent.name to percent(),
            Companion::date.name to date(),
            Companion::time.name to time(),
            Companion::datetime.name to datetime(),
            Companion::number.name to number(),
        )

        private fun parseNumberFormatParams(formatter: NumberFormat, args: Array<out Argument>) {
            args.forEach {
                val key = it.name
                val value = it.value
                when (key) {
                    "minIntDigits" -> formatter.minimumIntegerDigits = value.toInt()
                    "maxIntDigits" -> formatter.maximumIntegerDigits = value.toInt()
                    "minFracDigits" -> formatter.minimumFractionDigits = value.toInt()
                    "maxFracDigits" -> formatter.maximumFractionDigits = value.toInt()
                    "groupingUsed" -> formatter.isGroupingUsed = value.toBooleanStrict()
                    "roundingMode" -> formatter.roundingMode = value.toEnum(RoundingMode::class.java)
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
            args: Array<out Argument>,
        ): DateTimeFormatter {
            var firstFormat: FormatStyle = DEFAULT_FORMAT_STYLE
            var secondFormat: FormatStyle = DEFAULT_FORMAT_STYLE
            args.forEach {
                val key = it.name
                val value = it.value
                if ("dateStyle" == key && (DateTimeFormatterKind.DATE == kind || DateTimeFormatterKind.DATE_TIME == kind)) {
                    firstFormat = value.toEnum(FormatStyle::class.java)
                } else if ("timeStyle" == key && (kind == DateTimeFormatterKind.TIME || kind == DateTimeFormatterKind.DATE_TIME)) {
                    if (DateTimeFormatterKind.DATE_TIME == kind) {
                        secondFormat = value.toEnum(FormatStyle::class.java)
                    } else {
                        firstFormat = value.toEnum(FormatStyle::class.java)
                    }
                }
            }

            return when (kind) {
                DateTimeFormatterKind.DATE -> DateTimeFormatter.ofLocalizedDate(firstFormat)
                DateTimeFormatterKind.TIME -> DateTimeFormatter.ofLocalizedTime(firstFormat)
                DateTimeFormatterKind.DATE_TIME -> DateTimeFormatter.ofLocalizedDateTime(firstFormat, secondFormat)
            }
        }

        private fun Any.toInt(): Int = when (this) {
            is Int -> this
            is String -> Integer.valueOf(this)
            else -> illegalArgument("Invalid integer value: $this, must be a number or a parsable string")
        }

        private fun Any.toBooleanStrict(): Boolean = when (this) {
            is Boolean -> this
            is String -> when (this.lowercase()) {
                "true" -> true
                "false" -> false
                else -> illegalArgument("Invalid boolean value: $this, must be 'true' or 'false'")
            }
            else -> illegalArgument("Invalid boolean value: $this, must be a boolean or a parsable string")
        }

        private fun <T : Enum<T>> Any.toEnum(cl: Class<T>): T =
            when {
                cl.isInstance(this) -> @Suppress("UNCHECKED_CAST") (this as T)
                this is String -> try {
                    java.lang.Enum.valueOf(cl, this.uppercase(Locale.ENGLISH))
                } catch (e: IllegalArgumentException) {
                    illegalArgument("Invalid enum value: $this")
                }
                else -> illegalArgument("Invalid enum value: $this, must be a ${cl.simpleName} label or a parsable string")
            }

        private val DEFAULT_FORMAT_STYLE = FormatStyle.MEDIUM

        private class VariableFormatterParameterImpl(
            override val name: String,
            override val value: Any,
        ) : Argument {

            override fun toString(): String = "$name: $value"
        }

    }

}
