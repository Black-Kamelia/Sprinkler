package com.kamelia.sprinkler.i18n

object TranslationRequestProxies {

    inline fun mapKey(crossinline mapper: (String) -> String): TranslationRequestProxy = TranslationRequestProxy { _, key, _ ->
        mapper(key)
    }

    inline fun mapValue(crossinline mapper: (String) -> String): TranslationRequestProxy = TranslationRequestProxy { translator, key, locale ->
        mapper(translator.translate(key, locale))
    }

}
