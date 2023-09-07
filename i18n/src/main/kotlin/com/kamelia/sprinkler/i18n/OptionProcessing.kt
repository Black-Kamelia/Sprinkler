package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.util.interpolate
import com.kamelia.sprinkler.util.unsafeCast
import java.util.*

internal object OptionProcessor {

    fun translate(
        translator: TranslatorImpl,
        key: String,
        options: Map<String, Any>,
        locale: Locale,
    ): String? {
        val actualKey = buildKey(key, options)
        val value = translator.baseTranslateOrNull(actualKey, locale)
        return value?.interpolate(options)
    }

    private fun buildKey(key: String, options: Map<String, Any>): String {
        val builder = StringBuilder(key)

        options.safeType<String>(Options.CONTEXT)?.let {
            builder.append("_")
            builder.append(it)
        }
        options.safeType<Int>(Options.COUNT)?.let {
            builder.append("_")
            builder.append(countMapper(it))
        }

        return builder.toString()
    }

    private inline fun <reified T> Map<TranslationOption, Any>.safeType(key: String): T? {
        val value = get(key) ?: return null
        require(value is T) {
            "Expected ${T::class.simpleName}, got ${value::class.simpleName}, if you want to use a reserved name for interpolation, use the '${Options.INTERPOLATION}' option."
        }
        return value.unsafeCast()
    }


}

val countMapper = { count: Int ->
    when (count) {
        0 -> "zero"
        1 -> "one"
        else -> "other"
    }
}


private fun main() {
    val translator = Translator.builder(Locale.ENGLISH)
        .addMap(
            Locale.ENGLISH,
            mapOf(
                "greetings" to "Hello {name}!",
                "child_male_one" to "He is my son",
                "child_male_other" to "They are my sons",
                "child_female_one" to "She is my daughter",
                "child_female_other" to "They are my daughters",
            )
        )
        .build()
    with(translator) {
        val value = t("child", mapOf("context" to "male", "count" to 2))
        println(value)
    }
}
