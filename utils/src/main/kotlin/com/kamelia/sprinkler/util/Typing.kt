package com.kamelia.sprinkler.util

/**
 * Casts this nullable object to the specified type [T].
 *
 * @receiver the object to cast or null
 * @return the object cast as [T]
 */
@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <T> Any?.unsafeCast(): T = this as T

/**
 * Tries to cast this nullable object to the specified type [T].
 * If the cast fails, returns null.
 *
 * @receiver the object to cast or null
 * @return the object cast as [T] or null if the cast fails
 */
inline fun <reified T> Any?.castOrNull(): T? = this as? T
