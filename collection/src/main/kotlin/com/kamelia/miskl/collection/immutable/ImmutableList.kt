package com.kamelia.miskl.collection.immutable

import com.kamelia.miskl.collection.immutable.internal.ImmutableSubList

interface ImmutableList<out E> : List<E>, ImmutableCollection<E> {

    override fun listIterator(): ImmutableListIterator<E>
    
    /**
     * Returns a view of the portion of this list between the specified [fromIndex] (inclusive) and [toIndex] (exclusive).
     *
     * The returned list is backed by this list.
     *
     * @throws IndexOutOfBoundsException if [fromIndex] is less than zero or [toIndex] is greater than the size of this list.
     * @throws IllegalArgumentException if [fromIndex] is greater than [toIndex].
     */
    override fun subList(fromIndex: Int, toIndex: Int): ImmutableList<E> =
        ImmutableSubList(this, fromIndex, toIndex)

}
