package com.kamelia.miskl.collection.readonly.internal

import com.kamelia.miskl.collection.readonly.ReadOnlyIterator

internal class ReadOnlyIteratorImpl<T>(inner: Iterator<T>) : ReadOnlyIterator<T>, Iterator<T> by inner
