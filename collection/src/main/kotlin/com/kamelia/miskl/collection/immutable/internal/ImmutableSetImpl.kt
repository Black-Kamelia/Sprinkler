package com.kamelia.miskl.collection.immutable.internal

import com.kamelia.miskl.collection.immutable.ImmutableIterator
import com.kamelia.miskl.collection.immutable.ImmutableSet
import com.kamelia.miskl.collection.immutable.immutableIterator

internal class ImmutableSetImpl<E>(
    private val inner: Set<E>,
) : ImmutableSet<E>, Set<E> by inner {

    override fun iterator(): ImmutableIterator<E> = inner.immutableIterator()

    override fun equals(other: Any?): Boolean = inner == other

    override fun hashCode(): Int = inner.hashCode()

    override fun toString(): String = inner.toString()

}
