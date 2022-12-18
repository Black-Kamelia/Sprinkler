package com.kamelia.miskl.collection.readonly.internal

import com.kamelia.miskl.collection.readonly.ReadOnlyIterable
import com.kamelia.miskl.collection.readonly.ReadOnlyIterator
import com.kamelia.miskl.collection.readonly.readOnlyIterator

internal class ReadOnlyIterableImpl<T>(private val inner: Iterable<T>) : ReadOnlyIterable<T>, Iterable<T> {

    override fun iterator(): ReadOnlyIterator<T> = inner.readOnlyIterator()

}
