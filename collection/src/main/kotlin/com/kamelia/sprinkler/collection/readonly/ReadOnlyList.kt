package com.kamelia.sprinkler.collection.readonly

import com.kamelia.sprinkler.collection.readonly.internal.ReadOnlySubList

/**
 * Represents a read-only ordered [Collection] of elements. This interface and all its sub-interfaces only allow
 * read-only operations.
 *
 * Implementations of this interface have the responsibility to be read-only. However, read-only is not equivalent to
 * immutable. If the implementation is a wrapper around a mutable list, it is still read-only but not immutable.
 *
 * @param E the type of elements contained in the list. The read-only list is covariant in its element type
 * @see List
 * @see ReadOnlyCollection
 */
interface ReadOnlyList<out E> : List<E>, ReadOnlyCollection<E> {

    /**
     * Returns a [ReadOnlyListIterator] over the elements of this list.
     *
     * @return a [ReadOnlyListIterator] over the elements of this list
     * @see List.listIterator
     */
    override fun listIterator(): ReadOnlyListIterator<E>

    /**
     * Returns a view of the portion of this list between the specified [fromIndex] (inclusive) and [toIndex]
     * (exclusive). This view is represented as an [ReadOnlyList].
     *
     * @param fromIndex the low endpoint (inclusive) of the subList
     * @param toIndex the high endpoint (exclusive) of the subList
     * @return a [ReadOnlyList] containing the specified range of elements from this list
     * @throws IndexOutOfBoundsException if [fromIndex] < 0 or [toIndex] > size
     * @throws IllegalArgumentException if [fromIndex] > [toIndex]
     * @see List.subList
     */
    override fun subList(fromIndex: Int, toIndex: Int): ReadOnlyList<E> =
        ReadOnlySubList(this, fromIndex, toIndex)

}
