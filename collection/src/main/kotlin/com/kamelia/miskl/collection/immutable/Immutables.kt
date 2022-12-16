@file:JvmName("Immutables")

package com.kamelia.miskl.collection.immutable

import com.kamelia.miskl.collection.immutable.internal.ImmutableCollectionImpl
import com.kamelia.miskl.collection.immutable.internal.ImmutableIteratorImpl
import com.kamelia.miskl.collection.immutable.internal.ImmutableListImpl
import com.kamelia.miskl.collection.immutable.internal.ImmutableListIteratorImpl
import com.kamelia.miskl.collection.immutable.internal.ImmutableMapImpl
import com.kamelia.miskl.collection.immutable.internal.ImmutableSetImpl

//region Iterables

fun <T> Iterable<T>.immutableIterator(): ImmutableIterator<T> = when (this) {
    is ImmutableIterable<T> -> iterator()
    else -> ImmutableIteratorImpl(iterator())
}

//endregion


//region ImmutableCollections

fun <T> Iterable<T>.toImmutableCollection(): ImmutableCollection<T> = when(this) {
    is ImmutableCollection -> this
    else -> ImmutableCollectionImpl(toList())
}

fun <T> Collection<T>.asImmutableCollection(): ImmutableCollection<T> = when (this) {
    is ImmutableCollection -> this
    else -> ImmutableCollectionImpl(this)
}

//endregion


//region ImmutableLists

fun <T> List<T>.immutableListIterator(): ImmutableListIterator<T> = ImmutableListIteratorImpl(listIterator())

fun <T> Iterable<T>.toImmutableList(): ImmutableList<T> = when (this) {
    is ImmutableList -> this
    else -> ImmutableListImpl(toList())
}

fun <T> Array<T>.toImmutableList(): ImmutableList<T> = toList().toImmutableList()

fun <T> List<T>.asImmutableList(): ImmutableList<T> = when (this) {
    is ImmutableList -> this
    else -> ImmutableListImpl(this)
}

fun <T> immutableListOf(vararg elements: T): ImmutableList<T> = ImmutableListImpl(elements.asList())

//endregion


//region ImmutableSets

fun <T> Iterable<T>.toImmutableSet(): ImmutableSet<T> = when(this) {
    is ImmutableSet<T> -> this
    else -> ImmutableSetImpl(toSet())
}

fun <T> Array<T>.toImmutableSet(): ImmutableSet<T> = toSet().toImmutableSet()

fun <T> Set<T>.asImmutableSet(): ImmutableSet<T> = when(this) {
    is ImmutableSet<T> -> this
    else -> ImmutableSetImpl(this)
}

fun <T> immutableSetOf(vararg elements: T): ImmutableSet<T> = elements.toImmutableSet()

//endregion


//region ImmutableMaps

fun <K, V> Iterable<Pair<K, V>>.toImmutableMap(): ImmutableMap<K, V> = toMap().toImmutableMap()

fun <K, V> Map<K, V>.toImmutableMap(): ImmutableMap<K, V> = when (this) {
    is ImmutableMap -> this
    else -> ImmutableMapImpl(toMap())
}

fun <K, V> Array<Pair<K, V>>.toImmutableMap(): ImmutableMap<K, V> = toMap().toImmutableMap()

fun <K, V> Map<K, V>.asImmutableMap(): ImmutableMap<K, V> = when (this) {
    is ImmutableMap -> this
    else -> ImmutableMapImpl(this)
}

fun <K, V> immutableMapOf(vararg pairs: Pair<K, V>): ImmutableMap<K, V> = pairs.toMap().toImmutableMap()

//endregion
