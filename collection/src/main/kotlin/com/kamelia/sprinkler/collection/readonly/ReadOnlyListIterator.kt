package com.kamelia.sprinkler.collection.readonly

/**
 * Represents a read-only [ListIterator]. Like its super-interface [ReadOnlyIterator], this interface does not support
 * the [remove] operation.
 *
 * @param T the type of element being iterated over. The read-only list iterator is covariant in its element type
 * @see ListIterator
 * @see ReadOnlyIterator
 */
interface ReadOnlyListIterator<out T> : ReadOnlyIterator<T>, ListIterator<T>
