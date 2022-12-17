package com.kamelia.sprinkler.collection.readonly.internal

import com.kamelia.sprinkler.collection.readonly.ReadOnlyIterable
import com.kamelia.sprinkler.collection.readonly.ReadOnlyIterator
import com.kamelia.sprinkler.collection.readonly.readOnlyIterator

internal class ReadOnlyIterableImpl<T>(private val inner: Iterable<T>) : ReadOnlyIterable<T>, Iterable<T> {

    override fun iterator(): ReadOnlyIterator<T> = inner.readOnlyIterator()

}
