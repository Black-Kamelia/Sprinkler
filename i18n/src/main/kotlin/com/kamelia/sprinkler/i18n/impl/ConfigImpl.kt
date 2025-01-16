package com.kamelia.sprinkler.i18n.impl

import com.kamelia.sprinkler.i18n.formatting.VariableFormatter
import com.kamelia.sprinkler.i18n.pluralization.PluralMapper
import com.kamelia.sprinkler.util.VariableDelimiter
import com.zwendo.restrikt2.annotation.PackagePrivate
import java.util.Locale

@PackagePrivate
internal class ConfigImpl : TranslatorBuilder.Configuration {

    override var interpolationDelimiter: TranslatorBuilder.InterpolationDelimiter =
        TranslatorBuilder.InterpolationDelimiter(VariableDelimiter.default)

    override var pluralMapperFactory: (Locale) -> PluralMapper = PluralMapper.builtins()

    override var formatters: Map<String, VariableFormatter<out Any>> = VariableFormatter.builtins()

    override var missingKeyPolicy: TranslatorBuilder.MissingKeyPolicy =
        TranslatorBuilder.MissingKeyPolicy.THROW_EXCEPTION

    override var defaultLocale: Locale = Locale.ENGLISH

}
