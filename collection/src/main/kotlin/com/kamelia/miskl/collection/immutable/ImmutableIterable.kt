package com.kamelia.miskl.collection.immutable

interface ImmutableIterable<out T> : Iterable<T> {

    override fun iterator(): ImmutableIterator<T>

}
