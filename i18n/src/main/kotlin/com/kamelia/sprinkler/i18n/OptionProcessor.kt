package com.kamelia.sprinkler.i18n

import java.util.*

fun interface OptionsProcessor {

    fun translate(
        translator: Translator,
        key: String,
        options: Map<String, Any>,
        locale: Locale,
    ): String?

    companion object {

        @JvmField
        val noOp = OptionsProcessor { translator, key, _, locale ->
            translator.baseTranslateOrNull(key, locale)
        }

    }

}
