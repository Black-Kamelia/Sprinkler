package com.kamelia.sprinkler.i18n.impl

import com.kamelia.sprinkler.i18n.FunctionAdapter
import com.kamelia.sprinkler.i18n.formatting.VariableFormatter
import com.kamelia.sprinkler.i18n.pluralization.PluralMapper
import com.kamelia.sprinkler.util.VariableDelimiter
import com.zwendo.restrikt2.annotation.PackagePrivate
import java.util.Locale
import java.util.function.Function

@PackagePrivate
@Suppress("INAPPLICABLE_JVM_NAME")
internal class ConfigImpl : TranslatorBuilder.Configuration {

    override var interpolationDelimiter: TranslatorBuilder.InterpolationDelimiter =
        TranslatorBuilder.Companion.InterpolationDelimiterImpl(VariableDelimiter.default)

    @get:JvmName("getPluralMapperFactoryKt")
    @set:JvmName("setPluralMapperFactoryKt")
    override var pluralMapperFactory: (Locale) -> PluralMapper
        get() = _pluralMapperFactory
        set(value) {
            _pluralMapperFactory = FunctionAdapter(value::invoke)
        }

    @get:JvmName("getPluralMapperFactory")
    @set:JvmName("setPluralMapperFactory")
    override var pluralMapperFactoryJava: Function<Locale, PluralMapper>
        get() = _pluralMapperFactory
        set(value) {
            _pluralMapperFactory = FunctionAdapter(value::apply)
        }

    private var _pluralMapperFactory: FunctionAdapter<Locale, PluralMapper> = PluralMapper.internalBuiltins()

    override var formatters: Map<String, VariableFormatter<out Any>> = VariableFormatter.builtins()

    override var missingKeyPolicy: TranslatorBuilder.MissingKeyPolicy =
        TranslatorBuilder.MissingKeyPolicy.THROW_EXCEPTION

    override var defaultLocale: Locale? = Locale.ENGLISH
    override var currentLocale: Locale? = null

    @get:JvmName("getLocaleSpecializationReduction")
    @set:JvmName("setLocaleSpecializationReduction")
    override var localeSpecializationReductionJava: Function<Locale, Locale?>
        get() = _localeSpecializationReduction
        set(value) {
            _localeSpecializationReduction = FunctionAdapter(value::apply)
        }

    @get:JvmName("getLocaleSpecializationReductionKt")
    @set:JvmName("setLocaleSpecializationReductionKt")
    override var localeSpecializationReduction: (Locale) -> Locale?
        get() = _localeSpecializationReduction
        set(value) {
            _localeSpecializationReduction = FunctionAdapter(value::invoke)
        }

    private var _localeSpecializationReduction: FunctionAdapter<Locale, Locale?> = TranslatorBuilder.internalLocaleSpecializationReduction()

}
