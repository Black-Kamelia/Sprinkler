package com.kamelia.miskl.collection.readonly

/**
 * Represents a read-only [Iterator] over a collection. This interface and all its sub-interfaces only allow read-only
 * operations.
 *
 * @param T the type of element being iterated over. The read-only iterator is covariant in its element type
 * @see Iterator
 * @see MutableIterator
 */
interface ReadOnlyIterator<out T> : Iterator<T>
