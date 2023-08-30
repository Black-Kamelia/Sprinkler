@file:PackagePrivate

package com.kamelia.sprinkler.i18n

import com.zwendo.restrikt.annotation.PackagePrivate
import org.intellij.lang.annotations.Language

@Language("RegExp")
private const val KEY_IDENTIFIER_REGEX_STRING = """[a-zA-Z0-9](?:[\w-]*[a-zA-Z0-9])?"""

internal val KEY_IDENTIFIER_REGEX = KEY_IDENTIFIER_REGEX_STRING.toRegex()

internal val FULL_KEY_REGEX = """$KEY_IDENTIFIER_REGEX_STRING(?:\.$KEY_IDENTIFIER_REGEX_STRING)*""".toRegex()
