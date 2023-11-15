package com.kamelia.sprinkler.i18n

import kotlin.Number as KotlinNumber
import com.kamelia.sprinkler.util.illegalArgument
import java.math.RoundingMode
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.TemporalAccessor
import java.util.*

/**
 * Represents an object that can format specific values depending on the locale.
 */
fun interface VariableFormatter {

    /**
     * Formats the given [value] using the given [locale] and [extraArgs].
     *
     * **NOTE**: any unrecognized [extraArgs] is ignored.
     *
     * @param value the value to format
     * @param locale the locale to use
     * @param extraArgs the extra arguments to use
     * @return the formatted value
     */
    fun format(value: Any, locale: Locale, extraArgs: List<String>): String

    /**
     * The built-in [VariableFormatters][VariableFormatter].
     */
    object Builtins {

        object Currency : VariableFormatter {

            const val NAME = "currency"

            override fun format(value: Any, locale: Locale, extraArgs: List<String>): String {
                val amount = (value as? KotlinNumber)?.toDouble() ?: castException(KotlinNumber::class.java, value)
                val inner = NumberFormat.getCurrencyInstance(locale)
                return inner.format(amount)
            }

        }

        object Date : VariableFormatter {

            const val NAME = "date"

            override fun format(value: Any, locale: Locale, extraArgs: List<String>): String {
                val date = value as? TemporalAccessor ?: castException(TemporalAccessor::class.java, value)

                val formatStyle = if (extraArgs.isEmpty()) {
                    DEFAULT_FORMAT_STYLE
                } else {
                    formatStyle(extraArgs[0])
                }

                val inner = DateTimeFormatter.ofLocalizedDate(formatStyle).localizedBy(locale)
                return inner.format(date)
            }

        }

        object Time : VariableFormatter {

            const val NAME = "time"

            override fun format(value: Any, locale: Locale, extraArgs: List<String>): String {
                val time = value as? TemporalAccessor ?: castException(TemporalAccessor::class.java, value)

                val formatStyle = if (extraArgs.isEmpty()) {
                    DEFAULT_FORMAT_STYLE
                } else {
                    formatStyle(extraArgs[0])
                }

                val inner = DateTimeFormatter.ofLocalizedTime(formatStyle).localizedBy(locale)
                return inner.format(time)
            }

        }

        object DateTime : VariableFormatter {

            const val NAME = "datetime"

            override fun format(value: Any, locale: Locale, extraArgs: List<String>): String {
                val dateTime = value as? TemporalAccessor ?: castException(TemporalAccessor::class.java, value)

                val dateStyle = if (extraArgs.isEmpty()) {
                    DEFAULT_FORMAT_STYLE
                } else {
                    formatStyle(extraArgs[0])
                }
                val timeStyle = if (extraArgs.size < 2) {
                    DEFAULT_FORMAT_STYLE
                } else {
                    formatStyle(extraArgs[1])
                }

                val inner = DateTimeFormatter.ofLocalizedDateTime(dateStyle, timeStyle).localizedBy(locale)
                return inner.format(dateTime)
            }

        }

        object Number : VariableFormatter {

            const val NAME = "number"

            override fun format(value: Any, locale: Locale, extraArgs: List<String>): String {
                val number = (value as? KotlinNumber) ?: castException(KotlinNumber::class.java, value)

                // please do the trick
                val inner = NumberFormat.getInstance(locale)
                parseNumberFormatParams(inner, extraArgs)
                return inner.format(number)
            }

        }

        private fun parseNumberFormatParams(formatter: NumberFormat, params: List<String>) {
            params.forEach {
                val tokens = it.split(":")
                require(tokens.size == 2) { "Invalid number format parameter: $it" }
                val (key, value) = tokens
                when (key) {
                    "minIntDigits" -> formatter.minimumIntegerDigits = value.toIntOrException(it)
                    "maxIntDigits" -> formatter.maximumIntegerDigits = value.toIntOrException(it)
                    "minFracDigits" -> formatter.minimumFractionDigits = value.toIntOrException(it)
                    "maxFracDigits" -> formatter.maximumFractionDigits = value.toIntOrException(it)
                    "groupingUsed" -> formatter.isGroupingUsed = value.toBooleanStrictOrNull()
                        ?: illegalArgument("Invalid parameter, expected 'true' or 'false', got '$value'.")
                    "roundingMode" -> formatter.roundingMode = RoundingMode.valueOf(value)
                    // ignore unknown parameters
                }
            }
        }

        @Suppress("NOTHING_TO_INLINE")
        private inline fun String.toIntOrException(token: String): Int {
            return toIntOrNull() ?: illegalArgument("Invalid number format parameter: $token")
        }

        @Suppress("NOTHING_TO_INLINE")
        private inline fun castException(expected: Class<*>, actual: Any?): Nothing {
            throw IllegalArgumentException("Expected ${expected.simpleName}, got '$actual'.")
        }

        private fun formatStyle(string: String): FormatStyle = try {
            FormatStyle.valueOf(string.uppercase(Locale.ENGLISH))
        } catch (e: IllegalArgumentException) {
            illegalArgument("Invalid format style: $string")
        }

        private val DEFAULT_FORMAT_STYLE = FormatStyle.MEDIUM

    }

}
