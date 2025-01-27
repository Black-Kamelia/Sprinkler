@file:HideFromJava
@file:Suppress("NOTHING_TO_INLINE")
@file:JvmName("Exceptions")

package com.kamelia.sprinkler.util

import com.zwendo.restrikt2.annotation.HideFromJava
import java.io.IOException

/**
 * Throws an [IllegalArgumentException] with the given [message].
 *
 * @param message the message to use for the exception
 * @throws IllegalArgumentException always
 */
inline fun illegalArgument(message: Any?): Nothing = throw IllegalArgumentException(message.toString())

/**
 * Throws an [IllegalArgumentException].
 *
 * @throws IllegalArgumentException always
 */
inline fun illegalArgument(): Nothing = throw IllegalArgumentException()

/**
 * Throws an [AssertionError] with the given [message].
 *
 * @param message the message to use for the exception
 * @throws AssertionError always
 */
inline fun assertionFailed(message: Any?): Nothing = throw AssertionError(message.toString())

/**
 * Throws an [AssertionError].
 *
 * @throws AssertionError always
 */
inline fun assertionFailed(): Nothing = throw AssertionError()

/**
 * Throws an [IllegalStateException] with the given [message].
 *
 * @param message the message to use for the exception
 * @throws IllegalStateException always
 */
inline fun illegalState(message: Any?): Nothing = throw IllegalStateException(message.toString())

/**
 * Throws an [IllegalStateException].
 *
 * @throws IllegalStateException always
 */
inline fun illegalState(): Nothing = throw IllegalStateException()

/**
 * Throws an [UnsupportedOperationException] with the given [message].
 *
 * @param message the message to use for the exception
 * @throws UnsupportedOperationException always
 */
inline fun unsupportedOperation(message: Any?): Nothing = throw UnsupportedOperationException(message.toString())

/**
 * Throws an [UnsupportedOperationException].
 *
 * @throws UnsupportedOperationException always
 */
inline fun unsupportedOperation(): Nothing = throw UnsupportedOperationException()

/**
 * Throws an [NoSuchElementException] with the given [message].
 *
 * @param message the message to use for the exception
 * @throws NoSuchElementException always
 */
inline fun noSuchElement(message: Any?): Nothing = throw NoSuchElementException(message.toString())

/**
 * Throws an [NoSuchElementException].
 *
 * @throws NoSuchElementException always
 */
inline fun noSuchElement(): Nothing = throw NoSuchElementException()

/**
 * Throws an [IndexOutOfBoundsException] with the given [message].
 *
 * @param message the message to use for the exception
 * @throws IndexOutOfBoundsException always
 */
inline fun indexOutOfBounds(message: Any?): Nothing = throw IndexOutOfBoundsException(message.toString())

/**
 * Throws an [IndexOutOfBoundsException].
 *
 * @throws IndexOutOfBoundsException always
 */
inline fun indexOutOfBounds(): Nothing = throw IndexOutOfBoundsException()

/**
 * Throws an [IOException] with the given [message].
 *
 * @param message the message to use for the exception
 * @throws IOException always
 */
inline fun ioException(message: Any?): Nothing = throw IOException(message.toString())

/**
 * Throws an [IOException].
 *
 * @throws IOException always
 */
inline fun ioException(): Nothing = throw IOException()
