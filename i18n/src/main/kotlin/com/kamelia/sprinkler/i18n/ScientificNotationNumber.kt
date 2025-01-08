package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.i18n.ScientificNotationNumber.Companion.from
import java.math.RoundingMode
import java.util.Objects
import kotlin.math.log10
import kotlin.math.pow

/**
 * A simple type that represents a number in the scientific notation (e.g. `15e8` = `15 * 10^8` = `1 500 000 000`). This
 * type is required to have a way to represent number in the scientific notation, because several languages treat them
 * differently when it comes to pluralization.
 *
 * @see Plural
 * @see from
 */
interface ScientificNotationNumber {

    /**
     * The actual value of the number after the exponentiation (e.g. `1e2` = `1 * 10^2` = `100`).
     *
     * @return the value of the number
     */
    val value: Number

    /**
     * The significand of the number (e.g. the significand of `1e2` is `1`).
     *
     * @return the significand of the number
     */
    val significand: Double

    /**
     * The exponent of the number (e.g. the exponent of `1e2` is `2`).
     *
     * @return the exponent of the number
     */
    val exponent: Int

    /**
     * Returns whether the number is an integer or not.
     *
     * @return `true` if the number is an integer, `false` otherwise
     */
    val isInteger: Boolean

    /**
     * Converts the number after the exponentiation to a [Double].
     *
     * @return the value of the number
     * @see value
     */
    fun toDouble(): Double = (significand * 10.0.pow(exponent))

    /**
     * Converts the number after the exponentiation to a [Long].
     *
     * @return the long value of the number
     * @see value
     */
    fun toLong(): Long = toDouble().toLong()

    companion object {

        /**
         * Creates a new [ScientificNotationNumber] from the given [value].
         *
         * **NOTE**: For the value `0` the returned number will be `0e0`.
         *
         * @param value the value to convert to the scientific notation
         * @return the scientific notation number
         */
        @JvmStatic
        fun from(value: Long): ScientificNotationNumber {
            if (value == 0L) return IntegerSci(0.0, 0)
            val exponent = log10(value.toDouble()).toInt()
            // scale is set to 20 because a long cane be represented by at most 19 digits. We ensure like this that we
            // will always store the exact value of the long.
            val significand = value.toBigDecimal()
                .divide(10.0.pow(exponent).toBigDecimal(), 20, RoundingMode.HALF_UP)
                .toDouble()
            return IntegerSci(significand, exponent)
        }

        /**
         * Creates a new [ScientificNotationNumber] from the given [value].
         *
         * **NOTE**: For the value `0` the returned number will be `0e0`.
         *
         * @param value the value to convert to the scientific notation
         * @return the scientific notation number
         */
        @JvmStatic
        fun from(value: Double): ScientificNotationNumber {
            require(!value.isNaN()) { "The value must not be NaN" }
            require(!value.isInfinite()) { "The value must be finite" }
            if (value == 0.0) return DecimalSci(0.0, 0)
            val exponent = log10(value).toInt()
            if (exponent == 0 && value.toLong() == 0L) { // value is between -1 and 1 (both exclusive)
                return DecimalSci(value * 10, -1)
            }
            val significand = value / 10.0.pow(exponent)
            return DecimalSci(significand, exponent)
        }

        /**
         * Creates a new [ScientificNotationNumber] from the given [significand] and [exponent].
         *
         * @param significand the significand of the number
         * @param exponent the exponent of the number
         * @return the scientific notation number
         */
        @JvmStatic
        fun create(significand: Double, exponent: Int): ScientificNotationNumber {
            val value = significand * 10.0.pow(exponent)
            return if (value == value.toLong().toDouble()) {
                IntegerSci(significand, exponent)
            } else {
                DecimalSci(significand, exponent)
            }
        }

    }

}

private class DecimalSci(
    override val significand: Double,
    override val exponent: Int,
) : ScientificNotationNumber {

    override val value: Number
        get() = toDouble()

    override val isInteger: Boolean
        get() = false

    override fun equals(other: Any?): Boolean {
        if (other !is ScientificNotationNumber) return false
        return significand == other.significand && exponent == other.exponent
    }

    override fun hashCode(): Int = Objects.hash(significand, exponent)

    override fun toString(): String = "${significand}e$exponent"

}

private class IntegerSci(
    override val significand: Double,
    override val exponent: Int,
) : ScientificNotationNumber {

    override val value: Number
        get() = toLong()

    override val isInteger: Boolean
        get() = true

    override fun equals(other: Any?): Boolean {
        if (other !is ScientificNotationNumber) return false
        return significand == other.significand && exponent == other.exponent
    }

    override fun hashCode(): Int = Objects.hash(significand, exponent)

    override fun toString(): String {
        val v = significand.toLong()
        return if (v.toDouble() == significand) {
            "${v}e$exponent"
        } else {
            "${significand}e$exponent"
        }
    }
}
