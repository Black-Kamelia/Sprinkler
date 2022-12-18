package com.kamelia.miskl.collection.readonly

/**
 * Represents a read-only [Collection] of elements. This interface and all its sub-interfaces only allow read-only
 * operations.
 *
 * Implementations of this interface have the responsibility to be read-only. However, read-only is not equivalent to
 * immutable. If the implementation is a wrapper around a mutable collection, it is still read-only but not immutable.
 *
 * @param E the type of elements contained in the collection. The read-only collection is covariant in its element type
 * @see Collection
 */
interface ReadOnlyCollection<out E> : ReadOnlyIterable<E>, Collection<E>
