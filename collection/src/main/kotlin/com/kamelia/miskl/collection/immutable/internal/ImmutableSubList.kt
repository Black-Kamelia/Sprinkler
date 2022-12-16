package com.kamelia.miskl.collection.immutable.internal

import com.kamelia.miskl.collection.immutable.ImmutableIterator
import com.kamelia.miskl.collection.immutable.ImmutableList
import com.kamelia.miskl.collection.immutable.ImmutableListIterator
import com.kamelia.miskl.collection.immutable.immutableListIterator
import java.util.*
import kotlin.collections.AbstractList

internal class ImmutableSubList<E>(
    private val source: ImmutableList<E>,
    private val fromIndex: Int,
    toIndex: Int,
) : ImmutableList<E>, AbstractList<E>() {

    override var size: Int = 0
        private set

    init {
        Objects.checkFromToIndex(fromIndex, toIndex, source.size)
        size = toIndex - fromIndex
    }

    override fun get(index: Int): E {
        Objects.checkIndex(index, size)
        return source[fromIndex + index]
    }

    override fun iterator(): ImmutableIterator<E> = source.iterator()

    override fun listIterator(): ImmutableListIterator<E> = source.immutableListIterator()

    override fun subList(fromIndex: Int, toIndex: Int): ImmutableList<E> {
        Objects.checkFromToIndex(fromIndex, toIndex, size)
        return ImmutableSubList(source, this.fromIndex + fromIndex, this.fromIndex + toIndex)
    }

}
