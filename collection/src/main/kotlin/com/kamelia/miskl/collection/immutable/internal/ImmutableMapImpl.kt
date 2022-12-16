package com.kamelia.miskl.collection.immutable.internal

import com.kamelia.miskl.collection.immutable.ImmutableCollection
import com.kamelia.miskl.collection.immutable.ImmutableIterator
import com.kamelia.miskl.collection.immutable.ImmutableMap
import com.kamelia.miskl.collection.immutable.ImmutableSet
import com.kamelia.miskl.collection.immutable.asImmutableCollection
import com.kamelia.miskl.collection.immutable.asImmutableSet


internal class ImmutableMapImpl<K, V>(private val inner: Map<K, V>) : ImmutableMap<K, V>, Map<K, V> by inner {

    override val keys: ImmutableSet<K>
        get() = inner.keys.asImmutableSet()

    override val values: ImmutableCollection<V>
        get() = inner.values.asImmutableCollection()

    override val entries: ImmutableSet<ImmutableMap.Entry<K, V>>
        get() = ImmutableMapEntries(inner.entries)

    class Entry<K, V>(private val inner: Map.Entry<K, V>) : ImmutableMap.Entry<K, V>, Map.Entry<K, V> by inner {

        override val key: K
            get() = inner.key

        override val value: V
            get() = inner.value

        override fun equals(other: Any?): Boolean = inner == other

        override fun hashCode(): Int = super.hashCode()

        override fun toString(): String = inner.toString()

    }

}
