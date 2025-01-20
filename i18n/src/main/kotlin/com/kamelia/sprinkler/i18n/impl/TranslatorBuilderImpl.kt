package com.kamelia.sprinkler.i18n.impl

import com.kamelia.sprinkler.i18n.Translator
import com.kamelia.sprinkler.i18n.impl.ContentImpl.Companion.keyComparator
import com.kamelia.sprinkler.i18n.pluralization.PluralMapper
import com.kamelia.sprinkler.util.ExtendedCollectors
import com.kamelia.sprinkler.util.entryOf
import com.kamelia.sprinkler.util.toUnmodifiableMap
import com.kamelia.sprinkler.util.unsafeCast
import com.zwendo.restrikt2.annotation.PackagePrivate
import java.util.Locale
import java.util.stream.Collector
import java.util.stream.Collectors
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

@PackagePrivate
internal class TranslatorBuilderImpl(
    private val caller: Class<*>,
) : TranslatorBuilder {

    override var checkMissingKeysOnBuild: Boolean = false

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
        val configBuilder = ConfigImpl().apply(configBlock)
        val content = ContentImpl(caller, configBuilder).apply(contentBlock).run()

        val formatters = configBuilder.formatters
        val variableDelimiter =
            (configBuilder.interpolationDelimiter as TranslatorBuilder.Companion.InterpolationDelimiterImpl).inner
        val currentLocale: Locale = configBuilder.currentLocale ?: configBuilder.defaultLocale ?: Locale.ENGLISH

        if (content.isEmpty()) {
            val data = TranslatorData(
                configBuilder.defaultLocale,
                emptyMap(),
                variableDelimiter,
                MapAccessWrapper(emptyMap()),
                MapAccessWrapper(formatters),
                configBuilder.missingKeyPolicy,
                configBuilder.localeSpecializationReduction
            )

            return TranslatorImpl(currentLocale, data)
        }

        val finalMap = LinkedHashMap<Locale, MutableMap<String, String>>(content.size)

        val comparator = keyComparator()

        // firstEntry is never null because we checked that content is not empty
        val firstEntry = content.entries.first()
        val expectedKeys: Set<String> = firstEntry
            .value
            .keys
            .stream()
            .map { it.substringBefore('_') }
            .collect(Collectors.toSet())
        val expectedKeysLocale = firstEntry.key

        content.entries.forEach { entry ->
            val (locale, translations) = entry
            if (checkMissingKeysOnBuild) {
                val cleaned = translations.keys
                    .stream()
                    .map { it.substringBefore('_') }
                    .collect(Collectors.toSet())
                check(cleaned == expectedKeys) {
                    val diff = (cleaned - expectedKeys) + (expectedKeys - cleaned)
                    "Error for locales '$expectedKeysLocale' and '$locale': Keys are not the same for both locales (different keys $diff). All maps must have the same keys. To disable this check, use the ignoreMissingKeysOnBuild parameter when creating the builder."
                }
            }

            // we sort translations and add them to the final map
            finalMap[locale] = translations.entries
                .stream()
                .sorted { (a, _), (b, _) -> comparator.compare(a, b) }
                .collect(ExtendedCollectors.toLinkedHashMapUsingEntries())
        }

        var i = 0
        val pluralMapperMap = finalMap.keys
            .stream()
            .map { entryOf(it, configBuilder.pluralMapperFactory(it)) }
            .collect(
                Collector.of(
                    {
                        arrayOfNulls<Map.Entry<Locale, PluralMapper>>(finalMap.keys.size)
                            .unsafeCast<Array<Map.Entry<Locale, PluralMapper>>>()
                    },
                    { acc, e -> acc[i++] = e },
                    { a, b -> a + b },
                    { a -> a.toUnmodifiableMap() }
                )
            )

        val data = TranslatorData(
            configBuilder.defaultLocale,
            finalMap,
            variableDelimiter,
            MapAccessWrapper(pluralMapperMap),
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
