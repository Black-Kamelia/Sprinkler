package com.kamelia.sprinkler.collection.readonly.internal

import com.kamelia.sprinkler.collection.readonly.ReadOnlyIterator
import com.kamelia.sprinkler.collection.readonly.ReadOnlyList
import com.kamelia.sprinkler.collection.readonly.ReadOnlyListIterator
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
            "The source list of this sublist has been modified and is now smaller than the sublist (index: $i, size: " +
                        "${source.size})."
        }
        return source[i]
    }

    override fun iterator(): ReadOnlyIterator<E> = listIterator()

    override fun listIterator(): ReadOnlyListIterator<E> = object : ReadOnlyListIterator<E> {
        private var index = 0

        override fun hasNext(): Boolean = index < size

        override fun next(): E {
            if (!hasNext()) {
                throw NoSuchElementException("No more elements in this list.")
            }
            val result = get(index)
            index++
            return result
        }

        override fun hasPrevious(): Boolean = index > 0

        override fun previous(): E {
            if (!hasPrevious()) {
                throw NoSuchElementException("No more elements in this list.")
            }
            index--
            return get(index)
        }

        override fun nextIndex(): Int = index

        override fun previousIndex(): Int = index - 1

    }

    override fun subList(fromIndex: Int, toIndex: Int): ReadOnlyList<E> {
        Objects.checkFromToIndex(fromIndex, toIndex, size)
        return ReadOnlySubList(source, this.fromIndex + fromIndex, this.fromIndex + toIndex)
    }

}
