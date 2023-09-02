@file:JvmName("Exceptions")

package com.kamelia.sprinkler.util

/**
 * Throws an [IllegalArgumentException] with the given [message].
 *
 * @param message the message to use for the exception
 * @throws IllegalArgumentException always
 * @see [error]
 */
@Suppress("NOTHING_TO_INLINE")
inline fun illegalArgument(message: Any): Nothing = throw IllegalArgumentException(message.toString())

/**
 * Throws an [AssertionError] with the given [message].
 *
 * @param message the message to use for the exception
 * @throws AssertionError always
 * @see [error]
 */
@Suppress("NOTHING_TO_INLINE")
inline fun assertionFailed(message: Any): Nothing = throw AssertionError(message.toString())
