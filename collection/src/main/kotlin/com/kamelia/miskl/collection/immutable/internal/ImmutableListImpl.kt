package com.kamelia.miskl.collection.immutable.internal

import com.kamelia.miskl.collection.immutable.ImmutableIterator
import com.kamelia.miskl.collection.immutable.ImmutableList
import com.kamelia.miskl.collection.immutable.immutableIterator
import com.kamelia.miskl.collection.immutable.ImmutableListIterator
import com.kamelia.miskl.collection.immutable.immutableListIterator

internal class ImmutableListImpl<E>(private val inner: List<E>) : ImmutableList<E>, List<E> by inner {

    override fun iterator(): ImmutableIterator<E> = inner.immutableIterator()

    override fun listIterator(): ImmutableListIterator<E> = inner.immutableListIterator()

    override fun subList(fromIndex: Int, toIndex: Int): ImmutableList<E> = super.subList(fromIndex, toIndex)

    override fun equals(other: Any?): Boolean = inner == other

    override fun hashCode(): Int = inner.hashCode()

    override fun toString(): String = inner.toString()

}
