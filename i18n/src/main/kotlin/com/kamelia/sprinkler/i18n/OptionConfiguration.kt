package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.bridge.KotlinDslAdapter
import com.kamelia.sprinkler.util.VariableDelimiter
import com.zwendo.restrikt.annotation.PackagePrivate
import java.util.*

class OptionConfiguration @PackagePrivate internal constructor(
    internal val interpolationDelimiter: VariableDelimiter,
    internal val pluralMapper: (Locale, Int) -> Options.Plurals,
    internal val formats: Map<String, VariableFormatter>,
    internal val missingKeyPolicy: MissingKeyPolicy,
) {

    enum class MissingKeyPolicy {
        THROW_EXCEPTION,
        RETURN_KEY,
        ;
    }

    companion object {

        @JvmStatic
        inline fun create(block: Builder.() -> Unit): OptionConfiguration = Builder().apply(block).build()

    }

    class Builder @PublishedApi internal constructor() : KotlinDslAdapter {

        var interpolationDelimiter: VariableDelimiter = VariableDelimiter.DEFAULT

        var pluralMapper: (Locale, Int) -> Options.Plurals = Options.Plurals.Companion::defaultCountMapper

        var formats: Map<String, VariableFormatter> = mapOf(
            VariableFormatter.Builtins.Currency.NAME to VariableFormatter.Builtins.Currency,
            VariableFormatter.Builtins.Date.NAME to VariableFormatter.Builtins.Date,
            VariableFormatter.Builtins.Time.NAME to VariableFormatter.Builtins.Time,
            VariableFormatter.Builtins.DateTime.NAME to VariableFormatter.Builtins.DateTime,
            VariableFormatter.Builtins.Number.NAME to VariableFormatter.Builtins.Number,
        )

        var missingKeyPolicy: MissingKeyPolicy = MissingKeyPolicy.THROW_EXCEPTION

        @PublishedApi
        internal fun build(): OptionConfiguration = OptionConfiguration(
            interpolationDelimiter,
            pluralMapper,
            formats,
            missingKeyPolicy,
        )

    }

}
