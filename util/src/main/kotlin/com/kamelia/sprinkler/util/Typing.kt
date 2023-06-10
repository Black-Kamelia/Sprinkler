package com.kamelia.sprinkler.util

/**
 * Casts this nullable object to the specified type [T].
 *
 * @receiver the object to cast or null
 * @return the object cast as [T]
 */
@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <T> Any?.unsafeCast(): T = this as T



