package com.kamelia.sprinkler.util

/**
 * Throws an [IllegalArgumentException] with the given [message].
 *
 * @param message The message to use for the exception.
 * @see [error]
 */
@Suppress("NOTHING_TO_INLINE")
inline fun illegalArgument(message: Any): Nothing = throw IllegalArgumentException(message.toString())