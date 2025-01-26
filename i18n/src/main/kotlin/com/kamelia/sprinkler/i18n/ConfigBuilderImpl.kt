package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.i18n.formatting.VariableFormatter
import com.kamelia.sprinkler.i18n.pluralization.PluralRuleProvider
import com.kamelia.sprinkler.util.VariableDelimiter
import com.kamelia.sprinkler.util.toUnmodifiableMap
import com.zwendo.restrikt2.annotation.PackagePrivate
import java.util.Locale
import java.util.function.Function

@PackagePrivate
@Suppress("INAPPLICABLE_JVM_NAME")
internal class ConfigBuilderImpl : TranslatorBuilder.Configuration {

    override var interpolationDelimiter: TranslatorBuilder.InterpolationDelimiter =
        TranslatorBuilder.Companion.InterpolationDelimiterImpl(VariableDelimiter.default)

    @get:JvmName("getPluralRuleProviderFactoryKt")
    @set:JvmName("setPluralRuleProviderFactoryKt")
    override var pluralRuleProviderFactory: (Locale) -> PluralRuleProvider
        get() = _pluralRuleProviderFactory
        set(value) {
            _pluralRuleProviderFactory = FunctionAdapter { value(it) }
        }

    @get:JvmName("getPluralRuleProviderFactory")
    @set:JvmName("setPluralRuleProviderFactory")
    override var pluralRuleProviderFactoryJava: Function<Locale, PluralRuleProvider>
        get() = _pluralRuleProviderFactory
        set(value) {
            _pluralRuleProviderFactory = FunctionAdapter { value.apply(it) }
        }

    private var _pluralRuleProviderFactory: FunctionAdapter<Locale, PluralRuleProvider> = PluralRuleProvider.internalBuiltins()

    override var formatters: Map<String, VariableFormatter<out Any>> = VariableFormatter.builtins()
        set(value) {
            field = value.toUnmodifiableMap()
        }

    override var missingKeyPolicy: TranslatorBuilder.MissingKeyPolicy =
        TranslatorBuilder.MissingKeyPolicy.THROW_EXCEPTION

    override var defaultLocale: Locale? = Locale.ENGLISH
    override var currentLocale: Locale? = null

    @get:JvmName("getLocaleSpecializationReduction")
    @set:JvmName("setLocaleSpecializationReduction")
    override var localeSpecializationReductionJava: Function<Locale, Locale?>
        get() = _localeSpecializationReduction
        set(value) {
            _localeSpecializationReduction = FunctionAdapter { value.apply(it) }
        }

    @get:JvmName("getLocaleSpecializationReductionKt")
    @set:JvmName("setLocaleSpecializationReductionKt")
    override var localeSpecializationReduction: (Locale) -> Locale?
        get() = _localeSpecializationReduction
        set(value) {
            _localeSpecializationReduction = FunctionAdapter { value(it) }
        }

    private var _localeSpecializationReduction: FunctionAdapter<Locale, Locale?> = FunctionAdapter {
        TranslatorBuilder.defaultLocaleSpecializationReduction(it)
    }

}
