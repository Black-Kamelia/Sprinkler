package com.kamelia.sprinkler.collection.readonly.internal

import com.kamelia.sprinkler.collection.readonly.ReadOnlyIterator
import com.kamelia.sprinkler.collection.readonly.ReadOnlyMap
import com.kamelia.sprinkler.collection.readonly.ReadOnlySet


internal class ReadOnlyMapEntries<K, V>(
    private val inner: Collection<Map.Entry<K, V>>,
) : ReadOnlySet<ReadOnlyMap.Entry<K, V>> {

    override fun iterator() = object : ReadOnlyIterator<ReadOnlyMap.Entry<K, V>> {

        private var it = inner.iterator()

        override fun hasNext(): Boolean = it.hasNext()

        override fun next(): ReadOnlyMap.Entry<K, V> = ReadOnlyMapImpl.Entry(it.next())

    }

    override val size: Int
        get() = inner.size

    override fun isEmpty(): Boolean = inner.isEmpty()

    override fun containsAll(elements: Collection<ReadOnlyMap.Entry<K, V>>): Boolean = inner.containsAll(elements)

    override fun contains(element: ReadOnlyMap.Entry<K, V>): Boolean = inner.contains(element)

    override fun equals(other: Any?): Boolean = inner == other

    override fun hashCode(): Int = inner.hashCode()

    override fun toString(): String = inner.toString()

}
