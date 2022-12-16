package com.kamelia.miskl.collection.immutable.internal

import com.kamelia.miskl.collection.immutable.ImmutableListIterator

internal class ImmutableListIteratorImpl<T>(inner: ListIterator<T>) : ImmutableListIterator<T>, ListIterator<T> by inner
