package com.kamelia.sprinkler.collection.readonly.internal

import com.kamelia.sprinkler.collection.readonly.ReadOnlyCollection
import com.kamelia.sprinkler.collection.readonly.ReadOnlyMap
import com.kamelia.sprinkler.collection.readonly.ReadOnlySet
import com.kamelia.sprinkler.collection.readonly.asReadOnlyCollection
import com.kamelia.sprinkler.collection.readonly.asReadOnlySet


internal class ReadOnlyMapImpl<K, V>(private val inner: Map<K, V>) : ReadOnlyMap<K, V>, Map<K, V> by inner {

    override val keys: ReadOnlySet<K>
        get() = inner.keys.asReadOnlySet()

    override val values: ReadOnlyCollection<V>
        get() = inner.values.asReadOnlyCollection()

    override val entries: ReadOnlySet<ReadOnlyMap.Entry<K, V>>
        get() = ReadOnlyMapEntries(inner.entries)

    class Entry<K, V>(private val inner: Map.Entry<K, V>) : ReadOnlyMap.Entry<K, V>, Map.Entry<K, V> by inner {

        override val key: K
            get() = inner.key

        override val value: V
            get() = inner.value

        override fun equals(other: Any?): Boolean = inner == other

        override fun hashCode(): Int = super.hashCode()

        override fun toString(): String = inner.toString()

    }

}
