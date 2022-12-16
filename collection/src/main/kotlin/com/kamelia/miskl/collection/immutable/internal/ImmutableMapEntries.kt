package com.kamelia.miskl.collection.immutable.internal

import com.kamelia.miskl.collection.immutable.ImmutableIterator
import com.kamelia.miskl.collection.immutable.ImmutableMap
import com.kamelia.miskl.collection.immutable.ImmutableSet


internal class ImmutableMapEntries<K, V>(
    private val inner: Collection<Map.Entry<K, V>>,
) : ImmutableSet<ImmutableMap.Entry<K, V>> {

    override fun iterator() = object : ImmutableIterator<ImmutableMap.Entry<K, V>> {

        private var it = inner.iterator()

        override fun hasNext(): Boolean = it.hasNext()

        override fun next(): ImmutableMap.Entry<K, V> = ImmutableMapImpl.Entry(it.next())

    }

    override val size: Int
        get() = inner.size

    override fun isEmpty(): Boolean = inner.isEmpty()

    override fun containsAll(elements: Collection<ImmutableMap.Entry<K, V>>): Boolean = inner.containsAll(elements)

    override fun contains(element: ImmutableMap.Entry<K, V>): Boolean = inner.contains(element)

}
