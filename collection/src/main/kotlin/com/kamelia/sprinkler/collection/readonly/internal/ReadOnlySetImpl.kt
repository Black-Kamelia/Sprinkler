package com.kamelia.sprinkler.collection.readonly.internal

import com.kamelia.sprinkler.collection.readonly.ReadOnlyIterator
import com.kamelia.sprinkler.collection.readonly.ReadOnlySet
import com.kamelia.sprinkler.collection.readonly.readOnlyIterator

internal class ReadOnlySetImpl<E>(
    private val inner: Set<E>,
) : ReadOnlySet<E>, Set<E> by inner {

    override fun iterator(): ReadOnlyIterator<E> = inner.readOnlyIterator()

    override fun equals(other: Any?): Boolean = inner == other

    override fun hashCode(): Int = inner.hashCode()

    override fun toString(): String = inner.toString()

}
