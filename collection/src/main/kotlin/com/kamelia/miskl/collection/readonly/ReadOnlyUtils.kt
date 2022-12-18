@file:JvmName("ReadOnlyUtils")

package com.kamelia.miskl.collection.readonly

import com.kamelia.miskl.collection.readonly.internal.ReadOnlyCollectionImpl
import com.kamelia.miskl.collection.readonly.internal.ReadOnlyIterableImpl
import com.kamelia.miskl.collection.readonly.internal.ReadOnlyIteratorImpl
import com.kamelia.miskl.collection.readonly.internal.ReadOnlyListImpl
import com.kamelia.miskl.collection.readonly.internal.ReadOnlyListIteratorImpl
import com.kamelia.miskl.collection.readonly.internal.ReadOnlyMapImpl
import com.kamelia.miskl.collection.readonly.internal.ReadOnlySetImpl

//region ReadOnlyIterables

/**
 * Wraps the given [Iterable] into a [ReadOnlyIterable].
 *
 * @receiver the [Iterable] to wrap
 * @return a [ReadOnlyIterable] wrapping the given [Iterable]
 * @param T the type of the elements in the [Iterable]
 */
fun <T> Iterable<T>.asReadOnlyIterable(): ReadOnlyIterable<T> = when (this) {
    is ReadOnlyIterable -> this
    else -> ReadOnlyIterableImpl(this)
}

/**
 * Returns a [ReadOnlyIterator] over the elements of this iterable.
 *
 * @receiver the [Iterable] to iterate over
 * @return a [ReadOnlyIterator] over the elements of this iterable
 * @param T the type of the elements in the [Iterable]
 */
fun <T> Iterable<T>.readOnlyIterator(): ReadOnlyIterator<T> = when (this) {
    is ReadOnlyIterable -> iterator()
    else -> ReadOnlyIteratorImpl(iterator())
}

//endregion

//region ReadOnlyCollections

/**
 * Creates a [ReadOnlyCollection] copy of the given [Iterable].
 *
 * @receiver the [Iterable] to copy
 * @return a [ReadOnlyCollection] copy of the given [Iterable]
 * @param T the type of the elements in the [Iterable]
 */
fun <T> Iterable<T>.toReadOnlyCollection(): ReadOnlyCollection<T> = ReadOnlyCollectionImpl(toList())

/**
 * Wraps the given [Collection] into a [ReadOnlyCollection].
 *
 * @receiver the [Collection] to wrap
 * @return a [ReadOnlyCollection] wrapping the given [Collection]
 * @param T the type of the elements in the [Collection]
 */
fun <T> Collection<T>.asReadOnlyCollection(): ReadOnlyCollection<T> = when (this) {
    is ReadOnlyCollection -> this
    else -> ReadOnlyCollectionImpl(this)
}

//endregion

//region ReadOnlyLists

/**
 * Creates a [ReadOnlyListIterator] over the elements of this list.
 *
 * @receiver the [List] to iterate over
 * @return a [ReadOnlyListIterator] over the elements of this list
 * @param T the type of the elements in the [List]
 */
fun <T> List<T>.readOnlyListIterator(): ReadOnlyListIterator<T> = ReadOnlyListIteratorImpl(listIterator())

/**
 * Creates a [ReadOnlyList] copy of the given [Iterable].
 *
 * @receiver the [Iterable] to copy
 * @return a [ReadOnlyList] copy of the given [Iterable]
 * @param T the type of the elements in the [Iterable]
 */
fun <T> Iterable<T>.toReadOnlyList(): ReadOnlyList<T> = ReadOnlyListImpl(toList())

/**
 * Wraps the given [List] into a [ReadOnlyList].
 *
 * @receiver the [List] to wrap
 * @return a [ReadOnlyList] wrapping the given [List]
 * @param T the type of the elements in the [List]
 */
fun <T> List<T>.asReadOnlyList(): ReadOnlyList<T> = when (this) {
    is ReadOnlyList -> this
    else -> ReadOnlyListImpl(this)
}

/**
 * Creates a [ReadOnlyList] copy of the given [Array].
 *
 * @receiver the [Array] to copy
 * @return a [ReadOnlyList] copy of the given [Array]
 * @param T the type of the elements in the [Array]
 */
fun <T> Array<T>.toReadOnlyList(): ReadOnlyList<T> = ReadOnlyListImpl(toList())

/**
 * Wraps the given [Array] into a [ReadOnlyList].
 *
 * @receiver the [Array] to wrap
 * @return a [ReadOnlyList] wrapping the given [Array]
 * @param T the type of the elements in the [Array]
 */
fun <T> Array<T>.asReadOnlyList(): ReadOnlyList<T> = ReadOnlyListImpl(asList())

/**
 * Creates a [ReadOnlyList] from the given vararg [elements].
 *
 * @param elements the elements to create the [ReadOnlyList] from
 * @return a [ReadOnlyList] from the given vararg [elements]
 */
fun <T> readOnlyListOf(vararg elements: T): ReadOnlyList<T> = ReadOnlyListImpl(elements.asList())

//endregion

//region ReadOnlySets

/**
 * Creates a [ReadOnlySet] copy of the given [Iterable].
 *
 * @receiver the [Iterable] to copy
 * @return a [ReadOnlySet] copy of the given [Iterable]
 * @param T the type of the elements in the [Iterable]
 */
fun <T> Iterable<T>.toReadOnlySet(): ReadOnlySet<T> = ReadOnlySetImpl(toSet())

/**
 * Wraps the given [Set] into a [ReadOnlySet].
 *
 * @receiver the [Set] to wrap
 * @return a [ReadOnlySet] wrapping the given [Set]
 * @param T the type of the elements in the [Set]
 */
fun <T> Set<T>.asReadOnlySet(): ReadOnlySet<T> = when (this) {
    is ReadOnlySet<T> -> this
    else -> ReadOnlySetImpl(this)
}

/**
 * Creates a [ReadOnlySet] copy of the given [Array].
 *
 * @receiver the [Array] to copy
 * @return a [ReadOnlySet] copy of the given [Array]
 * @param T the type of the elements in the [Array]
 */
fun <T> Array<T>.toReadOnlySet(): ReadOnlySet<T> = toSet().asReadOnlySet()

/**
 * Creates a [ReadOnlySet] from the given vararg [elements].
 *
 * @param elements the elements to create the [ReadOnlySet] from
 * @return a [ReadOnlySet] from the given vararg [elements]
 * @param T the type of the elements in the [Array]
 */
fun <T> readOnlySetOf(vararg elements: T): ReadOnlySet<T> = elements.toReadOnlySet()

//endregion

//region ReadOnlyMaps

/**
 * Creates a [ReadOnlyMap] copy from the given [Iterable] of [Pair]s.
 *
 * @receiver the [Iterable] to copy
 * @return a [ReadOnlyMap] copy from the given [Iterable]
 * @param K the type of the keys in the [Iterable]
 * @param V the type of the values in the [Iterable]
 */
fun <K, V> Iterable<Pair<K, V>>.toReadOnlyMap(): ReadOnlyMap<K, V> = toMap().asReadOnlyMap()

/**
 * Creates a [ReadOnlyMap] copy from the given [Map].
 *
 * @receiver the [Map] to copy
 * @return a [ReadOnlyMap] copy from the given [Map]
 * @param K the type of the keys in the [Map]
 * @param V the type of the values in the [Map]
 */
fun <K, V> Map<K, V>.toReadOnlyMap(): ReadOnlyMap<K, V> = ReadOnlyMapImpl(toMap())

/**
 * Wraps the given [Map] into a [ReadOnlyMap].
 *
 * @receiver the [Map] to wrap
 * @return a [ReadOnlyMap] wrapping the given [Map]
 * @param K the type of the keys in the [Map]
 * @param V the type of the values in the [Map]
 */
fun <K, V> Map<K, V>.asReadOnlyMap(): ReadOnlyMap<K, V> = when (this) {
    is ReadOnlyMap -> this
    else -> ReadOnlyMapImpl(this)
}

/**
 * Creates a [ReadOnlyMap] copy from the given [Array] of [Pair]s.
 *
 * @receiver the [Array] to copy
 * @return a [ReadOnlyMap] copy from the given [Array]
 * @param K the type of the keys in the [Array]
 * @param V the type of the values in the [Array]
 */
fun <K, V> Array<Pair<K, V>>.toReadOnlyMap(): ReadOnlyMap<K, V> = toMap().asReadOnlyMap()

/**
 * Creates a [ReadOnlyMap] from the given vararg [pairs].
 *
 * @param pairs the pairs to create the [ReadOnlyMap] from
 * @return a [ReadOnlyMap] from the given vararg [pairs]
 * @param K the type of the keys in the [Array]
 * @param V the type of the values in the [Array]
 */
fun <K, V> readOnlyMapOf(vararg pairs: Pair<K, V>): ReadOnlyMap<K, V> = pairs.toMap().asReadOnlyMap()

//endregion
