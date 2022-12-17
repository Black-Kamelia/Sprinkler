package com.kamelia.miskl.collection.readonly

/**
 * Represents a read-only collection of pairs (key/value object). This interface and all its sub-interfaces only allow
 * read-only operations.
 *
 * Implementations of this interface are responsible to be read-only. However, read-only does is not equivalent to
 * immutable. If the implementation is a wrapper around a mutable map, it is still read-only but not immutable.
 *
 * @param K the type of map keys. The map is invariant on its key type
 * @param V the type of map values. The map is covariant on its value type
 * @see Map
 * @see ReadOnlyMap.Entry
 */
interface ReadOnlyMap<K, out V> : Map<K, V> {

    /**
     * Returns a [ReadOnlySet] of the keys contained in this map. The set is backed by the map, so changes to the map
     * are reflected in the set.
     *
     * @return a [ReadOnlySet] of the keys contained in this map
     */
    override val keys: ReadOnlySet<K>

    /**
     * Returns a [ReadOnlyCollection] of the values contained in this map. The collection is backed by the map, so
     * changes to the map are reflected in the collection.
     *
     * @return a [ReadOnlyCollection] of the values contained in this map
     */
    override val values: ReadOnlyCollection<V>

    /**
     * Returns a [ReadOnlySet] of the entries contained in this map. The set is backed by the map, so changes to the
     * map are reflected in the set.
     *
     * @return a [ReadOnlySet] of the entries contained in this map
     */
    override val entries: ReadOnlySet<Entry<K, V>>

    /**
     * Represents a read-only entry (key/value pair) in a [ReadOnlyMap]. This interface and all its sub-interfaces only
     * allow read-only operations.
     *
     * Implementations of this interface are responsible to be read-only. However, read-only does is not equivalent to
     * immutable. If the implementation is a wrapper around a mutable entry, it is still read-only but not immutable.
     *
     * @param K the type of the key. The entry is covariant on its key type
     * @param V the type of the value. The entry is covariant on its value type
     * @see Map.Entry
     */
    interface Entry<out K, out V> : Map.Entry<K, V> {

        /**
         * Returns the key corresponding to this entry.
         *
         * @return the key corresponding to this entry
         */
        override val key: K

        /**
         * Returns the value corresponding to this entry.
         *
         * @return the value corresponding to this entry
         */
        override val value: V

    }

}
