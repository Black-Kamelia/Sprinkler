package com.kamelia.miskl.collection.immutable.internal

import com.kamelia.miskl.collection.immutable.ImmutableIterator

internal class ImmutableIteratorImpl<T>(inner: Iterator<T>) : ImmutableIterator<T>, Iterator<T> by inner
