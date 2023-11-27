package com.kamelia.sprinkler.i18n

import java.util.*

/**
 * Represents an object that can format specific values depending on the locale.
 */
fun interface VariableFormatter {

    /**
     * Formats the given [value] using the given [locale] and [extraArgs].
     *
     * @param value the value to format
     * @param locale the locale to use
     * @param extraArgs the extra arguments to use
     * @return the formatted value
     */
    fun format(value: Any, locale: Locale, extraArgs: List<String>): String

    companion object {

        @JvmStatic
        fun currency(): VariableFormatter = BuiltinVariableFormatters.Currency

        @JvmStatic
        fun date(): VariableFormatter = BuiltinVariableFormatters.Date

        @JvmStatic
        fun time(): VariableFormatter = BuiltinVariableFormatters.Time

        @JvmStatic
        fun datetime(): VariableFormatter = BuiltinVariableFormatters.DateTime

        @JvmStatic
        fun number(): VariableFormatter = BuiltinVariableFormatters.Number

        @JvmStatic
        fun builtins(): List<VariableFormatter> = listOf(
            currency(),
            date(),
            time(),
            datetime(),
            number(),
        )

    }

}
