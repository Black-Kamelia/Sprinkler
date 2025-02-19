package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.i18n.TranslatorBuilder.MissingKeyPolicy
import com.kamelia.sprinkler.i18n.formatting.VariableFormatter
import com.kamelia.sprinkler.i18n.pluralization.PluralRuleProvider
import com.kamelia.sprinkler.util.VariableDelimiter
import com.zwendo.restrikt2.annotation.PackagePrivate
import java.util.Locale

@PackagePrivate
internal class TranslatorData(
    val defaultLocale: Locale?,
    val translations: Map<Locale, Map<String, String>>,
    val interpolationDelimiter: VariableDelimiter,
    val pluralRuleProvider: (Locale) -> PluralRuleProvider,
    val formatters: (String) -> VariableFormatter<out Any>,
    val missingKeyPolicy: MissingKeyPolicy,
    val specializationReduction: (Locale) -> Locale?,
) {

    override fun toString(): String =
        "defaultLocale=$defaultLocale, translations=use toMap(), interpolationDelimiter=$interpolationDelimiter, pluralRuleProvider=$pluralRuleProvider, formatters=$formatters, missingKeyPolicy=$missingKeyPolicy, specializationReduction=$specializationReduction"

}
