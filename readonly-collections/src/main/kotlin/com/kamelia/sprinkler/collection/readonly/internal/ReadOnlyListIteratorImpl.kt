package com.kamelia.sprinkler.collection.readonly.internal

import com.kamelia.sprinkler.collection.readonly.ReadOnlyListIterator

internal class ReadOnlyListIteratorImpl<T>(inner: ListIterator<T>) : ReadOnlyListIterator<T>, ListIterator<T> by inner
