@file:JvmName("OptionUtils")
package com.kamelia.sprinkler.i18n


import java.util.*

object Options {

    const val OPTIONS = "options"

    const val CONTEXT = "context"

    const val COUNT = "count"

    const val COUNT_MAPPER = "countMapper"

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
            fun defaultCountMapper(locale: Locale, count: Int): Plurals {
                require(count >= 0) { "count must be >= 0, but was $count" }
                return when (count) {
                    0 -> ZERO
                    1 -> ONE
                    else -> OTHER
                }
            }

        }

    }

}

@Suppress("NOTHING_TO_INLINE")
inline fun options(vararg pairs: Pair<String, Any>): Pair<String, Map<TranslationExtraArgs, Any>> =
    Options.OPTIONS to mapOf(*pairs)
