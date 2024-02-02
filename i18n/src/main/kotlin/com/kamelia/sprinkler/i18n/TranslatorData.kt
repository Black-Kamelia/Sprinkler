package com.kamelia.sprinkler.i18n

import com.zwendo.restrikt.annotation.PackagePrivate
import java.util.Locale

@PackagePrivate
internal class TranslatorData(
    val defaultLocale: Locale,
    val translations: Map<Locale, Map<String, String>>,
    val configuration: TranslatorConfiguration,
)
