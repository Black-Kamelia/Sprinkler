package com.kamelia.miskl.collection.readonly.internal

import com.kamelia.miskl.collection.readonly.ReadOnlyIterator
import com.kamelia.miskl.collection.readonly.ReadOnlyList
import com.kamelia.miskl.collection.readonly.ReadOnlyListIterator
import com.kamelia.miskl.collection.readonly.readOnlyIterator
import com.kamelia.miskl.collection.readonly.readOnlyListIterator

internal class ReadOnlyListImpl<E>(private val inner: List<E>) : ReadOnlyList<E>, List<E> by inner {

    override fun iterator(): ReadOnlyIterator<E> = inner.readOnlyIterator()

    override fun listIterator(): ReadOnlyListIterator<E> = inner.readOnlyListIterator()

    override fun subList(fromIndex: Int, toIndex: Int): ReadOnlyList<E> = super.subList(fromIndex, toIndex)

    override fun equals(other: Any?): Boolean = inner == other

    override fun hashCode(): Int = inner.hashCode()

    override fun toString(): String = inner.toString()

}
