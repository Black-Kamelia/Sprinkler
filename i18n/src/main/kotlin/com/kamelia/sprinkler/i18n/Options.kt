package com.kamelia.sprinkler.i18n


import java.util.*

object Options {

    const val CONTEXT = "context"

    const val INTERPOLATION = "interpolation"

    const val COUNT = "count"

    const val NESTING = "nesting"

    enum class Plurals {
        ZERO,
        ONE,
        TWO,
        FEW,
        MANY,
        OTHER,
        ;

        internal val representation: String = name.lowercase(Locale.ENGLISH)

    }

}
