@file:JvmName("StringConstants")
package com.kamelia.sprinkler.transcoder.binary.common

/**
 * The default [String] end marker used for the [ascii][Charsets.US_ASCII].
 */
@get:JvmName("asciiNull")
val ASCII_NULL: ByteArray
    get() = ByteArray(1)

/**
 * The default [String] end marker used for the [UTF-8][Charsets.UTF_8].
 */
@get:JvmName("utf8Null")
val UTF8_NULL: ByteArray
    get() = ASCII_NULL

/**
 * The default [String] end marker used for the [UTF-16][Charsets.UTF_16].
 */
@get:JvmName("utf16Null")
val UTF16_NULL: ByteArray
    get() = ByteArray(2)

/**
 * The default [String] end marker used for the [UTF-8][Charsets.ISO_8859_1].
 */
@get:JvmName("latin1Null")
val LATIN1_NULL: ByteArray
    get() = ASCII_NULL
