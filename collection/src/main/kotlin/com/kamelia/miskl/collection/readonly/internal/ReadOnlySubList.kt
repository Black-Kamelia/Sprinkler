package com.kamelia.miskl.collection.readonly.internal

import com.kamelia.miskl.collection.readonly.ReadOnlyIterator
import com.kamelia.miskl.collection.readonly.ReadOnlyList
import com.kamelia.miskl.collection.readonly.ReadOnlyListIterator
import com.kamelia.miskl.collection.readonly.readOnlyListIterator
import java.util.*
import kotlin.collections.AbstractList

internal class ReadOnlySubList<E>(
    private val source: ReadOnlyList<E>,
    private val fromIndex: Int,
    toIndex: Int,
) : ReadOnlyList<E>, AbstractList<E>() {

    override var size: Int = 0
        private set

    init {
        require(fromIndex <= toIndex) { "fromIndex($fromIndex) > toIndex($toIndex)" }
        Objects.checkFromToIndex(fromIndex, toIndex, source.size)
        size = toIndex - fromIndex
    }

    override fun get(index: Int): E {
        Objects.checkIndex(index, size)
        val i = fromIndex + index
        check(source.size >= i) {
            "The source list of this sublist has been modified and is now smaller than the sublist (index: $i, size: ${source.size})."
        }
        return source[i]
    }

    override fun iterator(): ReadOnlyIterator<E> = source.iterator()

    override fun listIterator(): ReadOnlyListIterator<E> = source.readOnlyListIterator()

    override fun subList(fromIndex: Int, toIndex: Int): ReadOnlyList<E> {
        Objects.checkFromToIndex(fromIndex, toIndex, size)
        return ReadOnlySubList(source, this.fromIndex + fromIndex, this.fromIndex + toIndex)
    }

}
