package com.kamelia.miskl.collection.readonly

/**
 * Represents a read-only unordered [Collection] of elements that does not allow duplicate elements.
 *
 * Implementations of this interface are responsible to be read-only. However, read-only does is not equivalent to
 * immutable. If the implementation is a wrapper around a mutable set, it is still read-only but not immutable.
 *
 * @param E the type of elements contained in the set. The read-only set is covariant in its element type
 * @see Set
 * @see ReadOnlyCollection
 */
interface ReadOnlySet<out E> : Set<E>, ReadOnlyCollection<E>
