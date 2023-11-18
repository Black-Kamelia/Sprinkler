package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.bridge.KotlinDslAdapter
import com.kamelia.sprinkler.util.VariableDelimiter
import com.zwendo.restrikt.annotation.PackagePrivate
import java.util.*

/**
 * Configuration of a [Translator].
 *
 * @see OptionConfiguration.Builder
 * @see Translator
 * @see TranslatorBuilder
 */
class OptionConfiguration @PackagePrivate internal constructor(
    internal val interpolationDelimiter: VariableDelimiter,
    internal val pluralMapper: (Locale, Int) -> Options.Plurals,
    internal val formats: Map<String, VariableFormatter>,
    internal val missingKeyPolicy: MissingKeyPolicy,
) {

    /**
     * Policy to use when a key is not found.
     */
    enum class MissingKeyPolicy {

        /**
         * Throw an exception when a key is not found.
          */
        THROW_EXCEPTION,

        /**
         * Return the key itself when a key is not found.
         */
        RETURN_KEY,

        ;

    }

    companion object {

        /**
         * Create an [OptionConfiguration] using the given [block] applied to a [Builder].
         *
         * @param block the block to apply to the [Builder]
         * @return the created [OptionConfiguration]
         */
        @JvmStatic
        inline fun create(block: Builder.() -> Unit): OptionConfiguration = Builder().apply(block).build()

    }

    class Builder @PublishedApi internal constructor() : KotlinDslAdapter {

        /**
         * The delimiter to use for interpolation in translations.
         */
        var interpolationDelimiter: VariableDelimiter = VariableDelimiter.DEFAULT

        /**
         * The mapper function to use for pluralization.
         */
        var pluralMapper: (locale: Locale, count: Int) -> Options.Plurals = Options.Plurals.Companion::defaultCountMapper

        /**
         * Map used to find formatters using their name during variable interpolation.
         *
         * @see VariableFormatter
         */
        var formats: Map<String, VariableFormatter> = mapOf(
            VariableFormatter.Builtins.Currency.NAME to VariableFormatter.Builtins.Currency,
            VariableFormatter.Builtins.Date.NAME to VariableFormatter.Builtins.Date,
            VariableFormatter.Builtins.Time.NAME to VariableFormatter.Builtins.Time,
            VariableFormatter.Builtins.DateTime.NAME to VariableFormatter.Builtins.DateTime,
            VariableFormatter.Builtins.Number.NAME to VariableFormatter.Builtins.Number,
        )

        /**
         * The policy to use when a key is not found.
         *
         * @see MissingKeyPolicy
         */
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
