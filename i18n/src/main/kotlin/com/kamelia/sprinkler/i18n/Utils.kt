@file:PackagePrivate

package com.kamelia.sprinkler.i18n

import com.zwendo.restrikt.annotation.PackagePrivate
import org.intellij.lang.annotations.Language

/**
 * Here we cannot use `\w` because it includes `_` and we don't want that.
 */
@Language("RegExp")
internal const val IDENTIFIER = """[a-zA-Z\d]+(?:[-_][a-zA-Z\d]+)*"""

internal val KEY_REGEX = """$IDENTIFIER(?:\.$IDENTIFIER)*""".toRegex()
