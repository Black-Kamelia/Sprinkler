package com.kamelia.sprinkler.i18n


import java.util.*

object Options {

    const val CONTEXT = "context"

    const val INTERPOLATION = "interpolation"

    const val COUNT = "count"

    const val COUNT_MAPPER = "countMapper"

    const val FORMAT = "format"

    enum class Plurals {
        ZERO,
        ONE,
        TWO,
        FEW,
        MANY,
        OTHER,
        ;

        internal val representation: String = name.lowercase(Locale.ENGLISH)

        companion object {

            @JvmStatic
            fun defaultCountMapper(locale: Locale, count: Int): Plurals =
                when (count) {
                    0 -> ZERO
                    1 -> ONE
                    else -> OTHER
                }

        }

    }

}
