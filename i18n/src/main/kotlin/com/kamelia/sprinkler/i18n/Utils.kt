
package com.kamelia.sprinkler.i18n

import java.util.function.Function
import org.intellij.lang.annotations.Language


internal object Utils {

    /**
     * Here we cannot use `\w` because it includes `_` and we don't want that.
     */
    @Language("RegExp")
    const val IDENTIFIER = """[a-zA-Z\d]+(?:[-_][a-zA-Z\d]+)*"""

    const val KEY_DOCUMENTATION = "For more details about translation keys, see TranslationKey typealias documentation"

}

internal fun interface FunctionAdapter<T, R> : Function<T, R>, (T) -> R {
    override fun apply(t: T): R = this(t)
}
