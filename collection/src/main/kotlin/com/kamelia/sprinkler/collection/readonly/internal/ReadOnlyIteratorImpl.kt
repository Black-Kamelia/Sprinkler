package com.kamelia.sprinkler.collection.readonly.internal

import com.kamelia.sprinkler.collection.readonly.ReadOnlyIterator

internal class ReadOnlyIteratorImpl<T>(inner: Iterator<T>) : ReadOnlyIterator<T>, Iterator<T> by inner
