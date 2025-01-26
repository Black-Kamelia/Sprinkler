@file:PackagePrivate

package com.kamelia.sprinkler.i18n

import com.zwendo.restrikt2.annotation.PackagePrivate
import java.util.function.Function
import org.intellij.lang.annotations.Language

/**
 * Here we cannot use `\w` because it includes `_` and we don't want that.
 */
@Language("RegExp")
internal const val IDENTIFIER = """[a-zA-Z\d]+(?:[-_][a-zA-Z\d]+)*"""

internal const val KEY_DOCUMENTATION =
    "For more details about translation keys, see TranslationKey typealias documentation"

internal inline fun <reified T : TranslationArgument> TranslationArgs.findKind(): T? {
    for (arg in this) {
        if (arg is T) return arg
    }
    return null
}

internal fun interface FunctionAdapter<T, R> : Function<T, R>, (T) -> R {

    override fun apply(t: T): R = this(t)
}
