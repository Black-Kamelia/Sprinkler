package com.kamelia.miskl.collection.readonly.internal

import com.kamelia.miskl.collection.readonly.ReadOnlyListIterator

internal class ReadOnlyListIteratorImpl<T>(inner: ListIterator<T>) : ReadOnlyListIterator<T>, ListIterator<T> by inner
