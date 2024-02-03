package com.kamelia.sprinkler.util

/**
 * Casts this nullable object to the specified type [T]. This method can be used in the context of generic types to
 * cast.
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
 * @return the object cast as [T], or null if the cast fails or if the object is null
 */
inline fun <reified T> Any?.castOrNull(): T? = this as? T

/**
 * Tries to cast this nullable object to the specified type [T]. This method returns null if the object is null, or
 * throws a [ClassCastException] if the cast fails.
 *
 * @receiver the object to cast
 * @return the object cast as [T], or null if the object is null
 */
inline fun <reified T> Any?.castIfNotNull(): T? = this?.let { this as T }

/**
 * Tries to cast this object to the specified type [T]. This method throws a [ClassCastException] if the cast
 * fails, or a [NullPointerException] if the object is null.
 *
 * @receiver the object to cast
 * @return the object cast as [T]
 * @throws ClassCastException if the cast fails
 * @throws NullPointerException if the object is null
 */
inline fun <reified T> Any?.cast(): T = this as T
