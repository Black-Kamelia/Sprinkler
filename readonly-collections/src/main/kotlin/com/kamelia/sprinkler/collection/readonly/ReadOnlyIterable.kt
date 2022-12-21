package com.kamelia.sprinkler.collection.readonly

/**
 * Represents a read-only [Iterable]. This interface overrides the [Iterable.iterator] method to change the return type
 * to a [ReadOnlyIterator] which is a read-only [Iterator].
 *
 * @param T the type of element being iterated over. The read-only iterable is covariant in its element type
 * @see Iterable
 * @see ReadOnlyIterator
 */
interface ReadOnlyIterable<out T> : Iterable<T> {

    /**
     * Returns a [ReadOnlyIterator] over the elements of this object.
     *
     * @return a [ReadOnlyIterator] over the elements of this object
     * @see Iterable.iterator
     */
    override fun iterator(): ReadOnlyIterator<T>

}
