package com.kamelia.sprinkler.collection.readonly

import com.zwendo.restrikt.annotation.PackagePrivate

@PackagePrivate
internal class ReadOnlyIteratorImpl<T>(inner: Iterator<T>) : ReadOnlyIterator<T>, Iterator<T> by inner
