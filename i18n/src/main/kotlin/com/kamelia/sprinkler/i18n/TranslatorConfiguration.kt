package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.bridge.KotlinDslAdapter
import com.kamelia.sprinkler.i18n.TranslatorConfiguration.Companion.create
import com.kamelia.sprinkler.util.VariableDelimiter
import com.zwendo.restrikt.annotation.HideFromJava
import com.zwendo.restrikt.annotation.HideFromKotlin
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
class TranslatorConfiguration @PackagePrivate internal constructor(
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

    /**
     * Delimiter to use for interpolation in translations.
     */
    class InterpolationDelimiter @PackagePrivate internal constructor(
        internal val inner: VariableDelimiter,
    ) {

        companion object {

            /**
             * Creates an [InterpolationDelimiter] using the given [start] and [end] delimiters.
             *
             * The delimiters cannot contain the following characters: `\`, `(`, `)`, `:`. Trying to create a delimiter
             * containing one of these characters will throw an [IllegalStateException].
             *
             * @param start the start delimiter
             * @param end the end delimiter
             * @return the created [InterpolationDelimiter]
             * @throws IllegalStateException if the delimiters contain forbidden characters
             */
            @JvmStatic
            fun create(start: String, end: String): InterpolationDelimiter {
                val inner = VariableDelimiter.create(start, end)
                val ch = forbiddenChars()
                val forbiddenChars = ch.joinToString("", "[^", "]*") { it.toString() }.toRegex()
                check(forbiddenChars.matches(inner.startDelimiter)) {
                    "Start delimiter cannot contain the following characters: ${ch.contentToString()}, but was '${inner.startDelimiter}'"
                }
                check(forbiddenChars.matches(inner.endDelimiter)) {
                    "End delimiter cannot contain the following characters: ${ch.contentToString()}, but was '${inner.endDelimiter}'"
                }
                return InterpolationDelimiter(inner)
            }

            private fun forbiddenChars(): CharArray = charArrayOf('\\', '(', ')', ':')

        }

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

    }

    /**
     * Builder for [TranslatorConfiguration].
     */
    class Builder @PackagePrivate internal constructor() : KotlinDslAdapter {

        /**
         * The delimiter to use for interpolation in translations.
         *
         * default: '{{' and '}}'
         */
        @set:HideFromJava
        var interpolationDelimiter: InterpolationDelimiter = InterpolationDelimiter(VariableDelimiter.default)

        /**
         * The mapper function to use for the pluralization strategy.
         * Said strategy can use a given [Locale] and count to return a [Plural] value.
         *
         * default: [Plural.defaultMapper]
         */
        @set:HideFromJava
        var pluralMapper: Plural.Mapper = Plural.defaultMapper()

        /**
         * Map used to find formatters using their name during variable interpolation.
         *
         * default: [VariableFormatter.builtins]
         *
         * @see VariableFormatter
         */
        @set:HideFromJava
        var formats: Map<String, VariableFormatter> = VariableFormatter.builtins()

        /**
         * The policy to use when a key is not found.
         *
         * default: [MissingKeyPolicy.THROW_EXCEPTION]
         *
         * @see MissingKeyPolicy
         */
        @set:HideFromJava
        var missingKeyPolicy: MissingKeyPolicy = MissingKeyPolicy.THROW_EXCEPTION

        /**
         * Sets the delimiter to use for interpolation in translations.
         *
         * The delimiter cannot contain the following characters: `\`, `(`, `)`, `:`. Any delimiter containing one of
         * these characters will throw an [IllegalStateException] when [build] is called.
         *
         * default: '{{' and '}}'
         *
         * @param value the delimiter to use
         * @return this [Builder]
         */
        @HideFromKotlin
        fun setInterpolationDelimiter(value: InterpolationDelimiter): Builder = apply { interpolationDelimiter = value }

        /**
         * Sets the mapper function to use for the pluralization strategy.
         * Said strategy can use a given [Locale] and count to return a [Plural] value.
         *
         * default: [Plural.defaultMapper]
         *
         * @param value the mapper function to use
         * @return this [Builder]
         */
        @HideFromKotlin
        fun setPluralMapper(value: Plural.Mapper): Builder = apply { pluralMapper = value }

        /**
         * Sets the map used to find formatters using their name during variable interpolation.
         *
         * default: [VariableFormatter.builtins]
         *
         * @param value the map to use
         * @return this [Builder]
         */
        @HideFromKotlin
        fun setFormats(value: Map<String, VariableFormatter>): Builder = apply { formats = value }

        /**
         * Sets the policy to use when a key is not found.
         *
         * default: [MissingKeyPolicy.THROW_EXCEPTION]
         *
         * @param value the policy to use
         * @return this [Builder]
         */
        @HideFromKotlin
        fun setMissingKeyPolicy(value: MissingKeyPolicy): Builder = apply { missingKeyPolicy = value }

        @PackagePrivate
        internal fun build(): TranslatorConfiguration = TranslatorConfiguration(
            interpolationDelimiter.inner,
            pluralMapper,
            formats.toMap(),
            missingKeyPolicy,
        )

    }

    override fun toString(): String =
        "TranslatorConfiguration(interpolationDelimiter=$interpolationDelimiter, pluralMapper=$pluralMapper, formats=$formats, missingKeyPolicy=$missingKeyPolicy)"

}
