@file:JvmName("StringConstants")
package com.kamelia.sprinkler.codec.binary.common

/**
 * The default [String] end marker used for the [ascii][Charsets.US_ASCII].
 */
@get:JvmName("asciiNull")
val ASCII_NULL = ByteArray(1)

/**
 * The default [String] end marker used for the [UTF-8][Charsets.UTF_8].
 */
@get:JvmName("utf8Null")
val UTF8_NULL = ASCII_NULL

/**
 * The default [String] end marker used for the [UTF-16][Charsets.UTF_16].
 */
@get:JvmName("utf16Null")
val UTF16_NULL = ByteArray(2)
