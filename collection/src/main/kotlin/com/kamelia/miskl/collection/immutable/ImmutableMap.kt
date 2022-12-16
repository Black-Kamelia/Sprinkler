package com.kamelia.miskl.collection.immutable

interface ImmutableMap<K, out V> : Map<K, V> {

    override val keys: ImmutableSet<K>

    override val values: ImmutableCollection<V>

    override val entries: ImmutableSet<Entry<K, V>>

    interface Entry<out K, out V> : Map.Entry<K, V> {

        override val key: K

        override val value: V

    }

}
