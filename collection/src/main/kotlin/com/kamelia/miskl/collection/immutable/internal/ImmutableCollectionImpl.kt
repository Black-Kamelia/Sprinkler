package com.kamelia.miskl.collection.immutable.internal

import com.kamelia.miskl.collection.immutable.ImmutableCollection
import com.kamelia.miskl.collection.immutable.ImmutableIterator
import com.kamelia.miskl.collection.immutable.immutableIterator


internal open class ImmutableCollectionImpl<E>(
    private val inner: Collection<E>,
) : ImmutableCollection<E>, Collection<E> by inner {

    override fun iterator(): ImmutableIterator<E> = inner.immutableIterator()

    override fun equals(other: Any?): Boolean = inner == other

    override fun hashCode(): Int = inner.hashCode()

    override fun toString(): String = inner.toString()

}
