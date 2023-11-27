package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.bridge.KotlinDslAdapter
import com.kamelia.sprinkler.i18n.TranslatorConfiguration.Companion.create
import com.kamelia.sprinkler.util.VariableDelimiter
import java.util.*

/**
 * Configuration of a [Translator]. This class defines rules applied to a [Translator] and all [Translator]s created
 * from it.
 *
 * @see create
 * @see TranslatorConfiguration.Builder
 * @see TranslatorBuilder
 * @see Translator
 */
class TranslatorConfiguration internal constructor(
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
         * Return the given key itself when a key is not found.
         */
        RETURN_KEY,

        ;

    }

    companion object {

        /**
         * Creates a [TranslatorConfiguration] using the given [block] applied to a [Builder].
         *
         * @param block the block to apply to the [Builder]
         * @return the created [TranslatorConfiguration]
         */
        @JvmStatic
        inline fun create(block: Builder.() -> Unit): TranslatorConfiguration = Builder().apply(block).build()

    }

    class Builder @PublishedApi internal constructor() : KotlinDslAdapter {

        /**
         * The delimiter to use for interpolation in translations.
         */
        var interpolationDelimiter: VariableDelimiter = VariableDelimiter.DEFAULT

        /**
         * The mapper function to use for the pluralization strategy.
         * Said strategy can use a given [Locale] and count to return a [Options.Plurals] value.
         */
        var pluralMapper: (locale: Locale, count: Int) -> Options.Plurals = Options.Plurals.Companion::defaultCountMapper

        /**
         * Map used to find formatters using their name during variable interpolation.
         *
         * @see VariableFormatter
         */
        var formats: Map<String, VariableFormatter> = BuiltinVariableFormatters.builtins()
            .associateBy(BuiltinVariableFormatters::name)

        /**
         * The policy to use when a key is not found.
         *
         * @see MissingKeyPolicy
         */
        var missingKeyPolicy: MissingKeyPolicy = MissingKeyPolicy.THROW_EXCEPTION

        @PublishedApi
        internal fun build(): TranslatorConfiguration = TranslatorConfiguration(
            interpolationDelimiter,
            pluralMapper,
            formats,
            missingKeyPolicy,
        )

    }

}
