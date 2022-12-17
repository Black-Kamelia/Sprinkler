package com.kamelia.miskl.collection.readonly.internal

import com.kamelia.miskl.collection.readonly.ReadOnlyIterator
import com.kamelia.miskl.collection.readonly.ReadOnlySet
import com.kamelia.miskl.collection.readonly.readOnlyIterator

internal class ReadOnlySetImpl<E>(
    private val inner: Set<E>,
) : ReadOnlySet<E>, Set<E> by inner {

    override fun iterator(): ReadOnlyIterator<E> = inner.readOnlyIterator()

    override fun equals(other: Any?): Boolean = inner == other

    override fun hashCode(): Int = inner.hashCode()

    override fun toString(): String = inner.toString()

}
