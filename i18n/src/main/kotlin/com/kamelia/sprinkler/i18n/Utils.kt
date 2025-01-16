
package com.kamelia.sprinkler.i18n

import org.intellij.lang.annotations.Language


internal object Utils {

    /**
     * Here we cannot use `\w` because it includes `_` and we don't want that.
     */
    @Language("RegExp")
    const val IDENTIFIER = """[a-zA-Z\d]+(?:[-_][a-zA-Z\d]+)*"""

    const val KEY_DOCUMENTATION = "For more details about translation keys, see TranslationKey typealias documentation"

}
