package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.util.entryOf
import com.kamelia.sprinkler.util.toUnmodifiableMap
import com.zwendo.restrikt2.annotation.PackagePrivate
import java.util.Locale
import java.util.stream.Collectors

@PackagePrivate
internal class TranslatorBuilderImpl(
    internal val caller: Class<*>,
) : TranslatorBuilder {

    private var configRedefined = false
    private var configBlock: TranslatorBuilder.Configuration.() -> Unit = { }

    private var contentRedefined = false
    private var contentBlock: TranslatorBuilder.Content.() -> Unit = { }

    private var built = false

    override fun configuration(block: TranslatorBuilder.Configuration.() -> Unit) {
        check(!built) { "TranslatorBuilder already built" }
        check(!configRedefined) { "Configuration block already defined" }
        configRedefined = true
        configBlock = block
    }

    override fun translations(block: TranslatorBuilder.Content.() -> Unit) {
        check(!built) { "TranslatorBuilder already built" }
        check(!contentRedefined) { "Content block already defined" }
        contentRedefined = true
        contentBlock = block
    }

    fun build(): Translator {
        built = true
        val configBuilder = ConfigBuilderImpl().apply(configBlock)
        val content = ContentBuilderImpl(caller, configBuilder).apply(contentBlock).run()

        val formatters = configBuilder.formatters
        val variableDelimiter =
            (configBuilder.interpolationDelimiter as TranslatorBuilder.Companion.InterpolationDelimiterImpl).inner
        val currentLocale: Locale = configBuilder.currentLocale ?: configBuilder.defaultLocale ?: Locale.ENGLISH

        val finalMap: Map<Locale, Map<String, String>> = content
            .entries
            .stream()
            .map { entryOf(it.key, it.value.toUnmodifiableMap()) }
            .collect(Collectors.toUnmodifiableMap({ it.key }, { it.value }))

        val pluralRuleProviderMap = finalMap.keys
            .stream()
            .map { entryOf(it, configBuilder.pluralRuleProviderFactory(it)) }
            .collect(Collectors.toUnmodifiableMap({ it.key }, { it.value }))

        val data = TranslatorData(
            configBuilder.defaultLocale,
            finalMap,
            variableDelimiter,
            MapAccessWrapper(pluralRuleProviderMap),
            MapAccessWrapper(formatters),
            configBuilder.missingKeyPolicy,
            configBuilder.localeSpecializationReduction
        )

        return TranslatorImpl(currentLocale, data)
    }

    /**
     * Simple wrapper to convert a map to a function with a toString.
     */
    private class MapAccessWrapper<K : Any, V : Any>(
        private val inner: Map<K, V>,
    ) : (K) -> V {

        override fun invoke(name: K): V = inner[name]!!

        override fun toString(): String = inner.toString()

    }

}
