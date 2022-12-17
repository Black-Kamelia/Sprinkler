package com.kamelia.sprinkler.collection.readonly

/**
 * Represents a read-only [Iterator]. A read-only iterator is a read-only iterator, which means that it does not
 * support the [MutableIterator.remove] method (accessible via casting to [MutableIterator] in Kotlin or directly in
 * java because the [MutableIterator] interface only exists at compile-time).
 *
 * @param T the type of element being iterated over. The read-only iterator is covariant in its element type
 * @see Iterator
 * @see MutableIterator
 */
interface ReadOnlyIterator<out T> : Iterator<T>
