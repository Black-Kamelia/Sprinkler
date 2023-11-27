package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.util.illegalArgument
import com.zwendo.restrikt.annotation.PackagePrivate
import java.math.RoundingMode
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.TemporalAccessor
import java.util.*

@PackagePrivate
internal sealed interface BuiltinVariableFormatters : VariableFormatter {

    val name: String

    companion object {

        fun builtins(): List<BuiltinVariableFormatters> = listOf(
            Currency,
            Date,
            Time,
            DateTime,
            Number,
        )

    }

    object Currency : BuiltinVariableFormatters {

        override val name = "currency"

        override fun format(value: Any, locale: Locale, extraArgs: List<String>): String {
            val amount = (value as? kotlin.Number)?.toDouble() ?: castException(kotlin.Number::class.java, value)
            val inner = NumberFormat.getCurrencyInstance(locale)
            parseNumberFormatParams(inner, extraArgs)
            return inner.format(amount)
        }

    }

    object Date : BuiltinVariableFormatters {

        override val name = "date"

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

    object Time : BuiltinVariableFormatters {

        override val name = "time"

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

    object DateTime : BuiltinVariableFormatters {

        override val name = "datetime"

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

    object Number : BuiltinVariableFormatters {

        override val name = "number"

        override fun format(value: Any, locale: Locale, extraArgs: List<String>): String {
            val number = (value as? kotlin.Number) ?: castException(kotlin.Number::class.java, value)

            // please do the trick
            val inner = NumberFormat.getInstance(locale)
            parseNumberFormatParams(inner, extraArgs)
            return inner.format(number)
        }

    }

}

private fun parseNumberFormatParams(formatter: NumberFormat, params: List<String>) {
    params.forEach {
        val tokens = it.split(":")
        if (tokens.size != 2) return
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
