package com.kamelia.sprinkler.i18n

import com.zwendo.restrikt2.annotation.PackagePrivate
import java.util.Locale

@PackagePrivate
internal class TranslatorData(
    val defaultLocale: Locale,
    val translations: Map<Locale, Map<String, String>>,
    val configuration: TranslatorConfiguration,
)
