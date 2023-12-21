package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.bridge.KotlinDslAdapter
import com.kamelia.sprinkler.i18n.TranslatorConfiguration.Companion.create
import com.kamelia.sprinkler.util.VariableDelimiter
import com.zwendo.restrikt.annotation.PackagePrivate
import java.util.Locale

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
    internal val pluralMapper: Plural.Mapper,
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
        fun create(block: Builder.() -> Unit): TranslatorConfiguration = Builder().apply(block).build()

        private fun forbiddenChars(): CharArray = charArrayOf('\\', '(', ')', ':')

    }

    class Builder @PublishedApi internal constructor() : KotlinDslAdapter {

        /**
         * The delimiter to use for interpolation in translations.
         *
         * The delimiter cannot contain the following characters: `\`, `(`, `)`, `:`. Any delimiter containing one of
         * these characters will throw an [IllegalStateException] when [build] is called.
         *
         * default: [VariableDelimiter.default]
         */
        var interpolationDelimiter: VariableDelimiter = VariableDelimiter.default

        /**
         * The mapper function to use for the pluralization strategy.
         * Said strategy can use a given [Locale] and count to return a [Plural] value.
         *
         * default: [Plural.defaultMapper]
         */
        var pluralMapper: Plural.Mapper = Plural.defaultMapper()

        /**
         * Map used to find formatters using their name during variable interpolation.
         *
         * default: [VariableFormatter.builtins]
         *
         * @see VariableFormatter
         */
        var formats: Map<String, VariableFormatter> = VariableFormatter.builtins()

        /**
         * The policy to use when a key is not found.
         *
         * default: [MissingKeyPolicy.THROW_EXCEPTION]
         *
         * @see MissingKeyPolicy
         */
        var missingKeyPolicy: MissingKeyPolicy = MissingKeyPolicy.THROW_EXCEPTION

        @PackagePrivate
        internal fun build(): TranslatorConfiguration {
            val ch = forbiddenChars()
            val forbiddenChars = ch.joinToString("", "[^", "]*") { it.toString() }.toRegex()
            check(forbiddenChars.matches(interpolationDelimiter.startDelimiter)) {
                "Start delimiter cannot contain the following characters: ${ch.contentToString()}, but was '${interpolationDelimiter.startDelimiter}'"
            }
            check(forbiddenChars.matches(interpolationDelimiter.endDelimiter)) {
                "End delimiter cannot contain the following characters: ${ch.contentToString()}, but was '${interpolationDelimiter.endDelimiter}'"
            }

            return TranslatorConfiguration(
                interpolationDelimiter,
                pluralMapper,
                formats.toMap(),
                missingKeyPolicy,
            )
        }

    }

    override fun toString(): String =
        "TranslatorConfiguration(interpolationDelimiter=$interpolationDelimiter, pluralMapper=$pluralMapper, formats=$formats, missingKeyPolicy=$missingKeyPolicy)"
}

