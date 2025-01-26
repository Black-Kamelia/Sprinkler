package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.i18n.formatting.VariableFormatter
import com.kamelia.sprinkler.i18n.pluralization.Plural
import com.kamelia.sprinkler.i18n.pluralization.PluralRuleProvider
import com.kamelia.sprinkler.i18n.pluralization.ScientificNotationNumber
import com.kamelia.sprinkler.util.VariableResolver
import com.kamelia.sprinkler.util.assertionFailed
import com.kamelia.sprinkler.util.illegalArgument
import com.kamelia.sprinkler.util.interpolate
import com.kamelia.sprinkler.util.unsafeCast
import com.zwendo.restrikt2.annotation.PackagePrivate
import java.util.Locale
import org.intellij.lang.annotations.Language

/**
 * Class in charge of processing the translations.
 *
 * During the processing, this class:
 * - create the actual key
 * - look for the translation in the map
 * - perform the interpolation of the values
 * - apply the formatters to the values
 * - return the final string
 */
@PackagePrivate
internal object TranslationProcessor {

    fun translate(data: TranslatorData, key: String, args: TranslationArgs, locale: Locale): String? {
        // The current behavior is to use the reduced locale ONLY for the lookup and the pluralization
        var lookupLocale: Locale? = null
        while (true) {
            var translations: Map<String, String>? = null
            // First, we try to find a map of translations for the current locale.
            while (translations == null) {
                // We need this awkward if assignment to ensure that each time we enter this loop, we generalize the
                // locale, except for the first time, where we use the provided locale.
                lookupLocale = if (lookupLocale != null) {
                    data.specializationReduction(lookupLocale) ?: return null
                } else {
                    locale
                }
                translations = data.translations[lookupLocale]
            }

            // Build the actual key with the options.
            val actualKey = buildKey(key, args, data.pluralRuleProvider(lookupLocale!!))

            // Get the value for the actual key or loop to the next locale.
            val value = translations[actualKey] ?: continue

            val context = ProcessingContext(data.formatters.unsafeCast(), locale, args)
            return value.interpolate(context, data.interpolationDelimiter, customResolver)
        }
    }

    fun buildKey(key: String, args: TranslationArgs, pluralRuleProvider: PluralRuleProvider): String {
        if (args.isEmpty()) return key

        val builder = StringBuilder(key)

        val interpolationContext = args.context()
        if (interpolationContext != null) {
            builder.append("_")
                .append(interpolationContext)
        }

        val ordinal = args.ordinal()
        val pluralValue = args.countValue(ordinal, pluralRuleProvider)
        if (pluralValue != null) {
            if (ordinal) {
                builder.append("_ordinal")
            }
            builder.append("_")
                .append(pluralValue.toString().lowercase())
        }

        return builder.toString()
    }

    private fun TranslationArgs.context(): String? = findKind<TranslationArgument.Companion.Context>()?.value

    private fun TranslationArgs.ordinal(): Boolean = findKind<TranslationArgument.Companion.Ordinal>()?.value ?: false

    fun TranslationArgs.countValue(ordinal: Boolean, pluralRuleProvider: PluralRuleProvider): Plural? {
        val count = findKind<TranslationArgument.Companion.Count>()?.value ?: return null
        return when (count) {
            is Number -> {
                if (ordinal) {
                    pluralRuleProvider.ordinal(count)
                } else {
                    pluralRuleProvider.cardinal(count)
                }
            }
            is ScientificNotationNumber -> {
                if (ordinal) {
                    pluralRuleProvider.ordinal(count)
                } else {
                    pluralRuleProvider.cardinal(count)
                }
            }
            else -> assertionFailed("Count must be a number but was ${count::class.java}")
        }
    }

    fun TranslationArgs.variable(name: String): TranslationArgument.Companion.Variable? {
        for (arg in this) {
            if (arg !is TranslationArgument.Companion.Variable) continue
            if (name != arg.name) continue
            return arg
        }
        return null
    }

    /**
     * This regex is globally the same as the translation value format check regex except that this regex actually
     * captures the information in groups, whereas the other one only checks if the format is valid.
     */
    private val generalSplit: Regex

    private val paramsSplit = """(?<!\\),""".toRegex()

    private val keyValueSplit = """(?<!\\):""".toRegex()

    internal class ProcessingContext(
        val formatters: (String) -> VariableFormatter<Any>,
        val locale: Locale,
        val args: TranslationArgs,
    )

    init {
        // Capture all the params in a single group.
        // Any char and ending with a non-escaped ')', no need further validation as the value has already been
        // validated on translator creation.
        @Language("RegExp")
        val formatParams = """\((.+(?<!\\))\)"""

        // Capture the format name.
        @Language("RegExp")
        val format = """\s*,\s*(${IDENTIFIER})\s*(?:$formatParams)?"""

        // Capture the variable name.
        generalSplit = """\s*(${IDENTIFIER})(?:$format)?\s*""".toRegex()
    }

    internal val customResolver = object : VariableResolver<ProcessingContext> {

        override fun resolveTo(builder: Appendable, name: String, context: ProcessingContext) {
            // '!!' is ok, because values are validated on translator creation.
            val (_, variableName, formatName, formatParams) = generalSplit.matchEntire(name)!!.groupValues

            val variableValue = context.args.variable(variableName)
                ?: illegalArgument("variable '$variableName' not found")

            if (formatName.isEmpty()) { // If there is no format, just append the value.
                builder.append(variableValue.value.toString())
                return
            }

            val formatPassedParams = variableValue.args
            // Otherwise, there is a format.
            val arguments = when {
                // Here we also handle the case where both are empty by returning an empty array implicitly.
                formatParams.isEmpty() -> formatPassedParams
                formatPassedParams.isEmpty() -> {
                    val split = formatParams.split(paramsSplit)
                    Array(split.size) {
                        val (k, v) = keyValueSplit.split(split[it], 2)
                        VariableFormatter.formatArgument(k, v)
                    }
                }
                else -> {
                    val split = formatParams.split(paramsSplit)
                    val finalArray = arrayOfNulls<VariableFormatter.Argument>(formatPassedParams.size + split.size)
                    System.arraycopy(formatPassedParams, 0, finalArray, split.size, formatPassedParams.size)
                    split.forEachIndexed { index, string ->
                        val (k, v) = keyValueSplit.split(string, 2)
                        finalArray[index] = VariableFormatter.formatArgument(k, v)
                    }
                    finalArray.unsafeCast()
                }
            }

            context.formatters(formatName).format(builder, variableValue.value, context.locale, *arguments)
        }


        override fun resolve(name: String, context: ProcessingContext): String =
            assertionFailed("This method should never be called")

    }

}
