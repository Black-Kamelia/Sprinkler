package com.kamelia.miskl.collection.immutable

import com.kamelia.miskl.collection.immutable.internal.ImmutableSubList

interface ImmutableList<out E> : List<E>, ImmutableCollection<E> {

    override fun listIterator(): ImmutableListIterator<E>

    override fun subList(fromIndex: Int, toIndex: Int): ImmutableList<E> =
        ImmutableSubList(this, fromIndex, toIndex)

}
