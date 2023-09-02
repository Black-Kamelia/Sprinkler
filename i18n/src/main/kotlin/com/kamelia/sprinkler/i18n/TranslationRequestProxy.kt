package com.kamelia.sprinkler.i18n

import java.util.*

fun interface TranslationRequestProxy {

    fun translate(translator: Translator, key: String, locale: Locale): String?

    companion object {

        fun default(): TranslationRequestProxy = TranslationRequestProxy(Translator::translate)

    }

}
