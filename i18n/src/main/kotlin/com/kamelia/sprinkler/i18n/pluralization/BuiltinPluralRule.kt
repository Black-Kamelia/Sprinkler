package com.kamelia.sprinkler.i18n.pluralization

import com.kamelia.sprinkler.i18n.FunctionAdapter
import com.kamelia.sprinkler.util.illegalArgument
import com.kamelia.sprinkler.util.unsafeCast
import com.kamelia.sprinkler.util.unsupportedOperation
import com.zwendo.restrikt2.annotation.PackagePrivate
import java.io.BufferedReader
import java.util.Locale
import java.util.stream.Collectors
import kotlin.math.abs

/**
 * Default implementation of the [PluralRuleProvider] that uses the builtin plural rules defined by
 * [unicode.org](https://www.unicode.org/cldr/charts/45/supplemental/language_plural_rules.html). This implementation
 * relies on a csv file located in the resources that contains the rules from Unicode in a simplified format. Each line
 * represents a locale and is parsed and loaded into a [PluralRuleProvider].
 *
 * This object contains the logic to parse the tiny grammar representing the rules in the csv file.
 */
@PackagePrivate
internal object BuiltinPluralRule {

    /**
     * The builtin [PluralRuleProvider] factory.
     */
    fun factory(): FunctionAdapter<Locale, PluralRuleProvider> {
        val map = providersAsStrings()
        return FunctionAdapter {
            try {
                loadRule(map, it)
            } catch (e: LocaleNotFoundException) {
                illegalArgument("Locale not supported: ${e.locale}")
            }
        }
    }

    private fun providersAsStrings(): Map<String, String> =
        Plural::class.java
            .getResourceAsStream("plural_rules.csv")!!
            .reader()
            .run(::BufferedReader)
            .use { reader ->
                reader
                    .lines()
                    .skip(1)
                    .map {
                        val (code, rest) = it.split(";", limit = 2)
                        code to rest
                    }
                    .collect(Collectors.toUnmodifiableMap({ it.first }, { it.second }))
            }

    private fun loadRule(content: Map<String, String>, locale: Locale): PluralRuleProvider {
        // we first look for the whole tag, then for the language
        val rules = content[locale.toLanguageTag()]
            ?: content[locale.language]
            ?: throw LocaleNotFoundException(locale)
        return loadedProvider(locale, rules)
    }

    class LocaleNotFoundException(val locale: Locale) : RuntimeException(null, null, false, false)

    private class BuiltinPluralRuleProvider(
        private val cardinal: IV2Plural,
        private val ordinal: IV2Plural,
    ) : PluralRuleProvider {

        override fun cardinal(count: Double): Plural = cardinal(InputValue.from(count))

        override fun cardinal(count: Long): Plural = cardinal(InputValue.from(count))

        override fun cardinal(count: ScientificNotationNumber): Plural = cardinal(InputValue.from(count))

        override fun ordinal(count: Long): Plural = ordinal(InputValue.from(count))

        override fun ordinal(count: Double): Plural = ordinal(InputValue.from(count))

        override fun ordinal(count: ScientificNotationNumber): Plural = ordinal(InputValue.from(count))

        override fun toString(): String = "(cardinal=[$cardinal], ordinal=[$ordinal])"

    }

    /**
     * Simple wrapper around a floating point or integer value. It is used to pass the value to the provider.
     */
    internal sealed interface InputValue {

        /**
         * Unicode `i`
         */
        fun toLong(): InputValue

        /**
         * Unicode `f` (it should normally include the trailing zeros)
         */
        fun toFloatingPart(): InputValue

        /**
         * Unicode `v`
         */
        fun toFloatingPartDigitsCount(): InputValue

        /**
         * Unicode `e`
         */
        fun exponent(): InputValue

        operator fun rem(divisor: Long): InputValue

        fun isInRange(start: Long, end: Long): Boolean

        fun isEqualsToValue(value: Long): Boolean

        companion object {

            fun from(value: Long): InputValue = ILong(value)

            fun from(value: Double): InputValue = IDouble(value)

            fun from(value: ScientificNotationNumber): InputValue = ISci(value)

            private fun doubleFloatingPart(value: Double): Long {
                val absoluteValue = abs(value)
                val floatingValue = absoluteValue.toBigDecimal() - absoluteValue.toLong().toBigDecimal()
                val exponent = floatingValue.toString().length - 2
                return (floatingValue * (10.0.toBigDecimal().pow(exponent))).toLong()
            }

            private fun doubleFloatingPartDigits(value: Double): Long {
                val absoluteValue = abs(value)
                val floatingValue = absoluteValue.toBigDecimal() - absoluteValue.toLong().toBigDecimal()
                return (floatingValue.toString().length - 2).toLong()
            }

        }

        private class ILong(val long: Long) : InputValue {

            override fun toLong(): ILong = this
            override fun toFloatingPart(): ILong = ILong(0)
            override fun toFloatingPartDigitsCount(): ILong = ILong(0)
            override fun exponent(): ILong = ILong(0)
            override fun rem(divisor: Long): ILong = ILong(long % divisor)
            override fun isInRange(start: Long, end: Long): Boolean = long in start..end
            override fun isEqualsToValue(value: Long): Boolean = long == value
        }

        private class IDouble(val double: Double) : InputValue {

            override fun toLong(): ILong = ILong(double.toLong())
            override fun toFloatingPart(): ILong = ILong(doubleFloatingPart(double))
            override fun toFloatingPartDigitsCount(): ILong = ILong(doubleFloatingPartDigits(double))
            override fun rem(divisor: Long): ILong =
                unsupportedOperation("Cannot apply modulo to a floating point number")

            override fun isInRange(start: Long, end: Long): Boolean = double in start.toDouble()..end.toDouble()
            override fun isEqualsToValue(value: Long): Boolean = double == value.toDouble()
            override fun exponent(): ILong = ILong(0)
        }

        private class ISci(val sci: ScientificNotationNumber) : InputValue {

            override fun toLong(): ILong = ILong(sci.toLong())
            override fun toFloatingPart(): ILong = ILong(doubleFloatingPart(sci.toDouble()))
            override fun toFloatingPartDigitsCount(): ILong = ILong(doubleFloatingPartDigits(sci.toDouble()))
            override fun rem(divisor: Long): ILong {
                if (!sci.isInteger) {
                    unsupportedOperation("Cannot apply modulo to a floating point number")
                }
                return ILong(sci.toLong() % divisor)
            }

            override fun isInRange(start: Long, end: Long): Boolean =
                if (sci.isInteger) {
                    sci.toLong() in start..end
                } else {
                    sci.toDouble() in start.toDouble()..end.toDouble()
                }

            override fun exponent(): ILong = ILong(sci.exponent.toLong())
            override fun isEqualsToValue(value: Long): Boolean = sci.toDouble() == value.toDouble()
        }

    }

    fun loadedProvider(locale: Locale, rules: String): PluralRuleProvider {
        return try {
            val (cardinal, ordinal) = rules.split(';')
            val cardinalRule = parseRule(true, cardinal)
            val ordinalRule = parseRule(false, ordinal)
            BuiltinPluralRuleProvider(cardinalRule, ordinalRule)
        } catch (e: Exception) {
            throw IllegalArgumentException("Error parsing the rules '$rules' for locale '$locale'.", e)
        }
    }

    private fun parseRule(isCardinal: Boolean, rule: String): IV2Plural =
        when (rule) {
            "" -> object : IV2Plural {
                override fun invoke(value: InputValue): Plural {
                    val kind = if (isCardinal) "Cardinal" else "Ordinal"
                    throw UnsupportedOperationException("$kind not supported by this provider")
                }

                override fun toString(): String {
                    val kind = if (isCardinal) "Cardinal" else "Ordinal"
                    return "Unsupported $kind"
                }
            }

            "*" -> object : IV2Plural {
                override fun invoke(value: InputValue): Plural = Plural.OTHER
                override fun toString(): String = "*"
            }

            else -> parseRuleContent(rule)
        }

    fun parseRuleContent(rule: String): IV2Plural {
        val parts = rule.split("//")
            .stream()
            .map {
                val (partName, partRule) = it.split(":")
                parseOrParts(partRule) to Plural.valueOf(partName.uppercase())
            }
            .toArray { arrayOfNulls<Pair<IV2Boolean, Plural>>(it) }
            .unsafeCast<Array<Pair<IV2Boolean, Plural>>>()
        return object : IV2Plural {
            override fun invoke(value: InputValue): Plural {
                for ((part, plural) in parts) {
                    if (part(value)) return plural
                }
                return Plural.OTHER
            }

            override fun toString(): String = parts.joinToString(", ") { (part, plural) -> "$plural: '$part'" }
        }
    }

    private fun parseOrParts(part: String): IV2Boolean {
        val orParts = part.split("||")
        if (orParts.size == 1) return parseAndParts(orParts[0])
        val parts = orParts.map { parseAndParts(it) }.toTypedArray()
        return object : IV2Boolean {
            override fun invoke(value: InputValue): Boolean {
                return parts.any { it(value) }
            }

            override fun toString(): String = parts.joinToString(" || ")
        }
    }

    private fun parseAndParts(part: String): IV2Boolean {
        val andParts = part.split("&&")
        if (andParts.size == 1) return parseComparisonParts(andParts[0])
        val parts = andParts.map { parseComparisonParts(it) }.toTypedArray()
        return object : IV2Boolean {
            override fun invoke(value: InputValue): Boolean = parts.all { it(value) }
            override fun toString(): String = parts.joinToString(" && ")
        }
    }

    private fun parseComparisonParts(part: String): IV2Boolean {
        var isDifferent = true
        var comparisonParts = part.split("!=")
        if (comparisonParts.size == 1) {
            comparisonParts = part.split("=")
            isDifferent = false
        }
        val leftPart = parseComparisonLeftPart(comparisonParts[0])
        val rightPart = parseComparisonRightPart(comparisonParts[1])
        return object : IV2Boolean {
            private val isDiff = isDifferent
            override fun invoke(value: InputValue): Boolean {
                val left = leftPart(value)
                val validForRight = rightPart(left)
                return if (isDiff) !validForRight else validForRight
            }

            override fun toString(): String = "$leftPart ${if (isDiff) "!=" else "=="} $rightPart"
        }
    }

    /**
     * Comparisons are always in the form `leftPart =/!= rightPart`, where leftPart != rightPart and leftPart is
     * `conversion (% modulo)?`, `conversion` being the variable used (e.g. `i`, `n`, ...).
     */
    private fun parseComparisonLeftPart(part: String): IV2IV {
        val parts = part.split("%")
        val adapter = parseInputValueAdapter(parts[0])
        if (parts.size == 1) return adapter
        val modulo = parts[1].toLong()
        return object : IV2IV {
            override fun invoke(value: InputValue): InputValue {
                val result = adapter(value)
                return result % modulo
            }

            override fun toString(): String = "$adapter % $modulo"
        }
    }

    private fun parseInputValueAdapter(part: String): IV2IV {
        val conversion = try {
            IVConversions.valueOf(part.uppercase())
        } catch (e: IllegalArgumentException) {
            throw AssertionError("Invalid conversion part: $part")
        }
        return object : IV2IV {
            override fun invoke(value: InputValue): InputValue =
                when (conversion) {
                    // the value directly
                    IVConversions.N -> value // 63.8 -> 63.8

                    // the value of the exponent of 10 in scientific notation
                    IVConversions.E -> value.exponent() // 63e8 -> 8

                    // the value of the integer part of the number
                    IVConversions.I -> value.toLong() // 63.8 -> 63

                    // the value of the decimal part of the number
                    IVConversions.F -> value.toFloatingPart() // 63.8 -> 8

                    // the number of digits in the decimal part of the number
                    IVConversions.V -> value.toFloatingPartDigitsCount() // 63.8 -> 1

                }

            override fun toString(): String = conversion.toString().lowercase()
        }
    }

    /**
     * Comparisons are always in the form `leftPart =/!= rightPart`, where leftPart != rightPart and rightPart is
     * `range | enumeration`, `range` being `start..end` and `enumeration` being a list of ranges separated by commas.
     * Note that a simple comparison is an enumeration with a single element.
     */
    private fun parseComparisonRightPart(part: String): IV2Boolean {
        val enumeration = part.split(",")
        if (enumeration.size == 1) return parseRangePart(enumeration[0])
        val parts = enumeration.map { parseRangePart(it) }.toTypedArray()
        return object : IV2Boolean {
            override fun invoke(value: InputValue): Boolean = parts.any { it(value) }
            override fun toString(): String = enumeration.joinToString(",")
        }
    }

    private fun parseRangePart(part: String): IV2Boolean {
        val range = part.split("..")
        if (range.size == 1) return parseValuePart(range[0])
        val start = range[0].toLong()
        val end = range[1].toLong()
        return object : IV2Boolean {
            override fun invoke(value: InputValue): Boolean = value.isInRange(start, end)
            override fun toString(): String = "$start..$end"
        }
    }

    /**
     * Simple value like `5` in `i = 5`.
     */
    private fun parseValuePart(part: String): IV2Boolean =
        object : IV2Boolean {
            private val value = part.toLong()
            override fun invoke(value: InputValue): Boolean = value.isEqualsToValue(this.value)
            override fun toString(): String = value.toString()
        }

    private interface IV2Boolean {

        operator fun invoke(value: InputValue): Boolean
    }


    interface IV2Plural {

        operator fun invoke(value: InputValue): Plural
    }

    private interface IV2IV {

        operator fun invoke(value: InputValue): InputValue
    }

    private enum class IVConversions {
        N, E, I, F, V
    }

}
