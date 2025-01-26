@file:HideFromJava
@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package com.kamelia.sprinkler.util

import com.zwendo.restrikt2.annotation.HideFromJava
import java.util.List as JavaList
import java.util.Map as JavaMap
import java.util.Set as JavaSet

//region List

/**
 * Creates an unmodifiable list containing the elements of this collection.
 *
 * @receiver the collection to copy
 * @return an unmodifiable list containing the elements of this collection
 * @param T the type of elements in the collection
 * @see [java.util.List.copyOf]
 */
fun <T : Any> Collection<T>.toUnmodifiableList(): List<T> = JavaList.copyOf(this)

/**
 * Creates an unmodifiable list containing the elements of this array.
 *
 * @receiver the array to copy
 * @return an unmodifiable list containing the elements of this array
 * @param T the type of elements in the array
 * @see [java.util.List.copyOf]
 */
fun <T : Any> Array<T>.toUnmodifiableList(): List<T> = VarargCopyWorkaround.unmodifiableListOf(this)

/**
 * Creates an empty unmodifiable list.
 *
 * @return an empty unmodifiable list
 * @param T the type of elements in the list
 * @see [java.util.List.of]
 */
fun <T : Any> unmodifiableListOf(): List<T> = JavaList.of()

/**
 * Creates an unmodifiable list containing the given element.
 *
 * @param e1 the element to add to the list
 * @return an unmodifiable list containing the given element
 * @param T the type of elements in the list
 * @see [java.util.List.of]
 */
fun <T : Any> unmodifiableListOf(e1: T): List<T> = JavaList.of(e1)

/**
 * Creates an unmodifiable list containing the given elements.
 *
 * @param e1 the first element to add to the list
 * @param e2 the second element to add to the list
 * @return an unmodifiable list containing the given elements
 * @param T the type of elements in the list
 * @see [java.util.List.of]
 */
fun <T : Any> unmodifiableListOf(e1: T, e2: T): List<T> = JavaList.of(e1, e2)

/**
 * Creates an unmodifiable list containing the given elements.
 *
 * @param e1 the first element to add to the list
 * @param e2 the second element to add to the list
 * @param e3 the third element to add to the list
 * @return an unmodifiable list containing the given elements
 * @param T the type of elements in the list
 * @see [java.util.List.of]
 */
fun <T : Any> unmodifiableListOf(e1: T, e2: T, e3: T): List<T> = JavaList.of(e1, e2, e3)

/**
 * Creates an unmodifiable list containing the given elements.
 *
 * @param e1 the first element to add to the list
 * @param e2 the second element to add to the list
 * @param e3 the third element to add to the list
 * @param e4 the fourth element to add to the list
 * @return an unmodifiable list containing the given elements
 * @param T the type of elements in the list
 * @see [java.util.List.of]
 */
fun <T : Any> unmodifiableListOf(e1: T, e2: T, e3: T, e4: T): List<T> = JavaList.of(e1, e2, e3, e4)

/**
 * Creates an unmodifiable list containing the given elements.
 *
 * @param e1 the first element to add to the list
 * @param e2 the second element to add to the list
 * @param e3 the third element to add to the list
 * @param e4 the fourth element to add to the list
 * @param e5 the fifth element to add to the list
 * @return an unmodifiable list containing the given elements
 * @param T the type of elements in the list
 * @see [java.util.List.of]
 */
fun <T : Any> unmodifiableListOf(e1: T, e2: T, e3: T, e4: T, e5: T): List<T> = JavaList.of(e1, e2, e3, e4, e5)

/**
 * Creates an unmodifiable list containing the given elements.
 *
 * @param e1 the first element to add to the list
 * @param e2 the second element to add to the list
 * @param e3 the third element to add to the list
 * @param e4 the fourth element to add to the list
 * @param e5 the fifth element to add to the list
 * @param e6 the sixth element to add to the list
 * @return an unmodifiable list containing the given elements
 * @param T the type of elements in the list
 * @see [java.util.List.of]
 */
fun <T : Any> unmodifiableListOf(e1: T, e2: T, e3: T, e4: T, e5: T, e6: T): List<T> =
    JavaList.of(e1, e2, e3, e4, e5, e6)

/**
 * Creates an unmodifiable list containing the given elements.
 *
 * @param e1 the first element to add to the list
 * @param e2 the second element to add to the list
 * @param e3 the third element to add to the list
 * @param e4 the fourth element to add to the list
 * @param e5 the fifth element to add to the list
 * @param e6 the sixth element to add to the list
 * @param e7 the seventh element to add to the list
 * @return an unmodifiable list containing the given elements
 * @param T the type of elements in the list
 * @see [java.util.List.of]
 */
fun <T : Any> unmodifiableListOf(e1: T, e2: T, e3: T, e4: T, e5: T, e6: T, e7: T): List<T> =
    JavaList.of(e1, e2, e3, e4, e5, e6, e7)

/**
 * Creates an unmodifiable list containing the given elements.
 *
 * @param e1 the first element to add to the list
 * @param e2 the second element to add to the list
 * @param e3 the third element to add to the list
 * @param e4 the fourth element to add to the list
 * @param e5 the fifth element to add to the list
 * @param e6 the sixth element to add to the list
 * @param e7 the seventh element to add to the list
 * @param e8 the eighth element to add to the list
 * @return an unmodifiable list containing the given elements
 * @param T the type of elements in the list
 * @see [java.util.List.of]
 */
fun <T : Any> unmodifiableListOf(e1: T, e2: T, e3: T, e4: T, e5: T, e6: T, e7: T, e8: T): List<T> =
    JavaList.of(e1, e2, e3, e4, e5, e6, e7, e8)

/**
 * Creates an unmodifiable list containing the given elements.
 *
 * @param e1 the first element to add to the list
 * @param e2 the second element to add to the list
 * @param e3 the third element to add to the list
 * @param e4 the fourth element to add to the list
 * @param e5 the fifth element to add to the list
 * @param e6 the sixth element to add to the list
 * @param e7 the seventh element to add to the list
 * @param e8 the eighth element to add to the list
 * @param e9 the ninth element to add to the list
 * @return an unmodifiable list containing the given elements
 * @param T the type of elements in the list
 * @see [java.util.List.of]
 */
fun <T : Any> unmodifiableListOf(e1: T, e2: T, e3: T, e4: T, e5: T, e6: T, e7: T, e8: T, e9: T): List<T> =
    JavaList.of(e1, e2, e3, e4, e5, e6, e7, e8, e9)

/**
 * Creates an unmodifiable list containing the given elements.
 *
 * @param e1 the first element to add to the list
 * @param e2 the second element to add to the list
 * @param e3 the third element to add to the list
 * @param e4 the fourth element to add to the list
 * @param e5 the fifth element to add to the list
 * @param e6 the sixth element to add to the list
 * @param e7 the seventh element to add to the list
 * @param e8 the eighth element to add to the list
 * @param e9 the ninth element to add to the list
 * @param e10 the tenth element to add to the list
 * @return an unmodifiable list containing the given elements
 * @param T the type of elements in the list
 * @see [java.util.List.of]
 */
fun <T : Any> unmodifiableListOf(e1: T, e2: T, e3: T, e4: T, e5: T, e6: T, e7: T, e8: T, e9: T, e10: T): List<T> =
    JavaList.of(e1, e2, e3, e4, e5, e6, e7, e8, e9, e10)

/**
 * Creates an unmodifiable list containing the given elements.
 *
 * @param elements the elements to add to the list
 * @return an unmodifiable list containing the given elements
 * @param T the type of elements in the list
 * @see [java.util.List.of]
 */
fun <T : Any> unmodifiableListOf(vararg elements: T): List<T> = elements.toUnmodifiableList()

//endregion

//region Set

/**
 * Creates an unmodifiable set containing the elements of this collection.
 *
 * @receiver the collection to copy
 * @return an unmodifiable set containing the elements of this collection
 * @param T the type of elements in the collection
 * @see [java.util.Set.copyOf]
 */
fun <T : Any> Collection<T>.toUnmodifiableSet(): Set<T> = JavaSet.copyOf(this)

/**
 * Creates an unmodifiable set containing the elements of this array.
 *
 * @receiver the array to copy
 * @return an unmodifiable set containing the elements of this array
 * @param T the type of elements in the array
 * @see [java.util.Set.copyOf]
 */
fun <T : Any> Array<T>.toUnmodifiableSet(): Set<T> = VarargCopyWorkaround.unmodifiableSetOf(this)

/**
 * Creates an empty unmodifiable set.
 *
 * @return an empty unmodifiable set
 * @param T the type of elements in the set
 * @see [java.util.Set.of]
 */
fun <T : Any> unmodifiableSetOf(): Set<T> = JavaSet.of()

/**
 * Creates an unmodifiable set containing the given element.
 *
 * @param e1 the element to add to the set
 * @return an unmodifiable set containing the given element
 * @param T the type of elements in the set
 * @see [java.util.Set.of]
 */
fun <T : Any> unmodifiableSetOf(e1: T): Set<T> = JavaSet.of(e1)

/**
 * Creates an unmodifiable set containing the given elements.
 *
 * @param e1 the first element to add to the set
 * @param e2 the second element to add to the set
 * @return an unmodifiable set containing the given elements
 * @param T the type of elements in the set
 * @see [java.util.Set.of]
 */
fun <T : Any> unmodifiableSetOf(e1: T, e2: T): Set<T> = JavaSet.of(e1, e2)

/**
 * Creates an unmodifiable set containing the given elements.
 *
 * @param e1 the first element to add to the set
 * @param e2 the second element to add to the set
 * @param e3 the third element to add to the set
 * @return an unmodifiable set containing the given elements
 * @param T the type of elements in the set
 * @see [java.util.Set.of]
 */
fun <T : Any> unmodifiableSetOf(e1: T, e2: T, e3: T): Set<T> = JavaSet.of(e1, e2, e3)

/**
 * Creates an unmodifiable set containing the given elements.
 *
 * @param e1 the first element to add to the set
 * @param e2 the second element to add to the set
 * @param e3 the third element to add to the set
 * @param e4 the fourth element to add to the set
 * @return an unmodifiable set containing the given elements
 * @param T the type of elements in the set
 * @see [java.util.Set.of]
 */
fun <T : Any> unmodifiableSetOf(e1: T, e2: T, e3: T, e4: T): Set<T> = JavaSet.of(e1, e2, e3, e4)

/**
 * Creates an unmodifiable set containing the given elements.
 *
 * @param e1 the first element to add to the set
 * @param e2 the second element to add to the set
 * @param e3 the third element to add to the set
 * @param e4 the fourth element to add to the set
 * @param e5 the fifth element to add to the set
 * @return an unmodifiable set containing the given elements
 * @param T the type of elements in the set
 * @see [java.util.Set.of]
 */
fun <T : Any> unmodifiableSetOf(e1: T, e2: T, e3: T, e4: T, e5: T): Set<T> = JavaSet.of(e1, e2, e3, e4, e5)

/**
 * Creates an unmodifiable set containing the given elements.
 *
 * @param e1 the first element to add to the set
 * @param e2 the second element to add to the set
 * @param e3 the third element to add to the set
 * @param e4 the fourth element to add to the set
 * @param e5 the fifth element to add to the set
 * @param e6 the sixth element to add to the set
 * @return an unmodifiable set containing the given elements
 * @param T the type of elements in the set
 * @see [java.util.Set.of]
 */
fun <T : Any> unmodifiableSetOf(e1: T, e2: T, e3: T, e4: T, e5: T, e6: T): Set<T> = JavaSet.of(e1, e2, e3, e4, e5, e6)

/**
 * Creates an unmodifiable set containing the given elements.
 *
 * @param e1 the first element to add to the set
 * @param e2 the second element to add to the set
 * @param e3 the third element to add to the set
 * @param e4 the fourth element to add to the set
 * @param e5 the fifth element to add to the set
 * @param e6 the sixth element to add to the set
 * @param e7 the seventh element to add to the set
 * @return an unmodifiable set containing the given elements
 * @param T the type of elements in the set
 * @see [java.util.Set.of]
 */
fun <T : Any> unmodifiableSetOf(e1: T, e2: T, e3: T, e4: T, e5: T, e6: T, e7: T): Set<T> =
    JavaSet.of(e1, e2, e3, e4, e5, e6, e7)

/**
 * Creates an unmodifiable set containing the given elements.
 *
 * @param e1 the first element to add to the set
 * @param e2 the second element to add to the set
 * @param e3 the third element to add to the set
 * @param e4 the fourth element to add to the set
 * @param e5 the fifth element to add to the set
 * @param e6 the sixth element to add to the set
 * @param e7 the seventh element to add to the set
 * @param e8 the eighth element to add to the set
 * @return an unmodifiable set containing the given elements
 * @param T the type of elements in the set
 * @see [java.util.Set.of]
 */
fun <T : Any> unmodifiableSetOf(e1: T, e2: T, e3: T, e4: T, e5: T, e6: T, e7: T, e8: T): Set<T> =
    JavaSet.of(e1, e2, e3, e4, e5, e6, e7, e8)

/**
 * Creates an unmodifiable set containing the given elements.
 *
 * @param e1 the first element to add to the set
 * @param e2 the second element to add to the set
 * @param e3 the third element to add to the set
 * @param e4 the fourth element to add to the set
 * @param e5 the fifth element to add to the set
 * @param e6 the sixth element to add to the set
 * @param e7 the seventh element to add to the set
 * @param e8 the eighth element to add to the set
 * @param e9 the ninth element to add to the set
 * @return an unmodifiable set containing the given elements
 * @param T the type of elements in the set
 * @see [java.util.Set.of]
 */
fun <T : Any> unmodifiableSetOf(e1: T, e2: T, e3: T, e4: T, e5: T, e6: T, e7: T, e8: T, e9: T): Set<T> =
    JavaSet.of(e1, e2, e3, e4, e5, e6, e7, e8, e9)

/**
 * Creates an unmodifiable set containing the given elements.
 *
 * @param e1 the first element to add to the set
 * @param e2 the second element to add to the set
 * @param e3 the third element to add to the set
 * @param e4 the fourth element to add to the set
 * @param e5 the fifth element to add to the set
 * @param e6 the sixth element to add to the set
 * @param e7 the seventh element to add to the set
 * @param e8 the eighth element to add to the set
 * @param e9 the ninth element to add to the set
 * @param e10 the tenth element to add to the set
 * @return an unmodifiable set containing the given elements
 * @param T the type of elements in the set
 * @see [java.util.Set.of]
 */
fun <T : Any> unmodifiableSetOf(e1: T, e2: T, e3: T, e4: T, e5: T, e6: T, e7: T, e8: T, e9: T, e10: T): Set<T> =
    JavaSet.of(e1, e2, e3, e4, e5, e6, e7, e8, e9, e10)

/**
 * Creates an unmodifiable set containing the given elements.
 *
 * @param elements the elements to add to the set
 * @return an unmodifiable set containing the given elements
 * @param T the type of elements in the set
 * @see [java.util.Set.of]
 */
fun <T : Any> unmodifiableSetOf(vararg elements: T): Set<T> = elements.toUnmodifiableSet()

//endregion

//region Map

/**
 * Creates an [entry][Map.Entry] with the given key and value.
 *
 * @param key the key of the entry
 * @param value the value of the entry
 * @return an entry with the given key and value
 * @param K the type of the key
 * @param V the type of the value
 *
 * @see [java.util.Map.entry]
 */
fun <K : Any, V : Any> entryOf(key: K, value: V): Map.Entry<K, V> = JavaMap.entry(key, value)

/**
 * Creates an unmodifiable map containing the entries of this map.
 *
 * @receiver the map to copy
 * @return an unmodifiable map containing the entries of this map
 * @param K the type of keys in the map
 * @param V the type of values in the map
 * @see [java.util.Map.copyOf]
 */
fun <K : Any, V : Any> Map<K, V>.toUnmodifiableMap(): Map<K, V> = JavaMap.copyOf(this)

/**
 * Creates an unmodifiable map containing the entries of this collection of [pairs][Pair].
 *
 * @receiver the collection to transform into a map
 * @return an unmodifiable map containing the entries of this collection
 * @param K the type of keys in the map
 * @param V the type of values in the map
 */
fun <K : Any, V : Any> Collection<Pair<K, V>>.toUnmodifiableMap(): Map<K, V> = this.toTypedArray().toUnmodifiableMap()

/**
 * Creates an unmodifiable map containing the entries of this collection of [entries][Map.Entry].
 *
 * @receiver the collection to transform into a map
 * @return an unmodifiable map containing the entries of this collection
 * @param K the type of keys in the map
 * @param V the type of values in the map
 */
@JvmName("toUnmodifiableMapFromEntries")
fun <K : Any, V : Any> Collection<Map.Entry<K, V>>.toUnmodifiableMap(): Map<K, V> =
    this.toTypedArray().toUnmodifiableMap()

/**
 * Creates an unmodifiable map containing the entries of this array of [pairs][Pair].
 *
 * @receiver the array to transform into a map
 * @return an unmodifiable map containing the entries of this array
 * @param K the type of keys in the map
 * @param V the type of values in the map
 */
fun <K : Any, V : Any> Array<out Pair<K, V>>.toUnmodifiableMap(): Map<K, V> {
    val array = Array<Map.Entry<K, V>>(size) {
        val pair = this[it]
        JavaMap.entry(pair.first, pair.second)
    }
    return array.toUnmodifiableMap()
}

/**
 * Creates an unmodifiable map containing the entries of this array of [entries][Map.Entry].
 *
 * @receiver the array to transform into a map
 * @return an unmodifiable map containing the entries of this array
 * @param K the type of keys in the map
 * @param V the type of values in the map
 */
fun <K : Any, V : Any> Array<out Map.Entry<K, V>>.toUnmodifiableMap(): Map<K, V> =
    VarargCopyWorkaround.unmodifiableMapOf(this)

/**
 * Creates an empty unmodifiable map.
 *
 * @return an empty unmodifiable map
 * @param K the type of keys in the map
 * @param V the type of values in the map
 * @see [java.util.Map.of]
 */
fun <K : Any, V : Any> unmodifiableMapOf(): Map<K, V> = JavaMap.of()

/**
 * Creates an unmodifiable map containing the given entry.
 *
 * @param k1 the key of the entry
 * @param v1 the value of the entry
 * @return an unmodifiable map containing the given entry
 * @param K the type of keys in the map
 * @param V the type of values in the map
 * @see [java.util.Map.of]
 */
fun <K : Any, V : Any> unmodifiableMapOf(k1: K, v1: V): Map<K, V> = JavaMap.of(k1, v1)

/**
 * Creates an unmodifiable map containing the given entries.
 *
 * @param k1 the key of the first entry
 * @param v1 the value of the first entry
 * @param k2 the key of the second entry
 * @param v2 the value of the second entry
 * @return an unmodifiable map containing the given entries
 * @param K the type of keys in the map
 * @param V the type of values in the map
 * @see [java.util.Map.of]
 */
fun <K : Any, V : Any> unmodifiableMapOf(k1: K, v1: V, k2: K, v2: V): Map<K, V> = JavaMap.of(k1, v1, k2, v2)

/**
 * Creates an unmodifiable map containing the given entries.
 *
 * @param k1 the key of the first entry
 * @param v1 the value of the first entry
 * @param k2 the key of the second entry
 * @param v2 the value of the second entry
 * @param k3 the key of the third entry
 * @param v3 the value of the third entry
 * @return an unmodifiable map containing the given entries
 * @param K the type of keys in the map
 * @param V the type of values in the map
 * @see [java.util.Map.of]
 */
fun <K : Any, V : Any> unmodifiableMapOf(k1: K, v1: V, k2: K, v2: V, k3: K, v3: V): Map<K, V> =
    JavaMap.of(k1, v1, k2, v2, k3, v3)

/**
 * Creates an unmodifiable map containing the given entries.
 *
 * @param k1 the key of the first entry
 * @param v1 the value of the first entry
 * @param k2 the key of the second entry
 * @param v2 the value of the second entry
 * @param k3 the key of the third entry
 * @param v3 the value of the third entry
 * @param k4 the key of the fourth entry
 * @param v4 the value of the fourth entry
 * @return an unmodifiable map containing the given entries
 * @param K the type of keys in the map
 * @param V the type of values in the map
 * @see [java.util.Map.of]
 */
fun <K : Any, V : Any> unmodifiableMapOf(k1: K, v1: V, k2: K, v2: V, k3: K, v3: V, k4: K, v4: V): Map<K, V> =
    JavaMap.of(k1, v1, k2, v2, k3, v3, k4, v4)

/**
 * Creates an unmodifiable map containing the given entries.
 *
 * @param k1 the key of the first entry
 * @param v1 the value of the first entry
 * @param k2 the key of the second entry
 * @param v2 the value of the second entry
 * @param k3 the key of the third entry
 * @param v3 the value of the third entry
 * @param k4 the key of the fourth entry
 * @param v4 the value of the fourth entry
 * @param k5 the key of the fifth entry
 * @param v5 the value of the fifth entry
 * @return an unmodifiable map containing the given entries
 * @param K the type of keys in the map
 * @param V the type of values in the map
 */
fun <K : Any, V : Any> unmodifiableMapOf(
    k1: K,
    v1: V,
    k2: K,
    v2: V,
    k3: K,
    v3: V,
    k4: K,
    v4: V,
    k5: K,
    v5: V,
): Map<K, V> = JavaMap.of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5)

/**
 * Creates an unmodifiable map containing the given entries.
 *
 * @param k1 the key of the first entry
 * @param v1 the value of the first entry
 * @param k2 the key of the second entry
 * @param v2 the value of the second entry
 * @param k3 the key of the third entry
 * @param v3 the value of the third entry
 * @param k4 the key of the fourth entry
 * @param v4 the value of the fourth entry
 * @param k5 the key of the fifth entry
 * @param v5 the value of the fifth entry
 * @param k6 the key of the sixth entry
 * @param v6 the value of the sixth entry
 * @return an unmodifiable map containing the given entries
 * @param K the type of keys in the map
 * @param V the type of values in the map
 */
fun <K : Any, V : Any> unmodifiableMapOf(
    k1: K,
    v1: V,
    k2: K,
    v2: V,
    k3: K,
    v3: V,
    k4: K,
    v4: V,
    k5: K,
    v5: V,
    k6: K,
    v6: V,
): Map<K, V> = JavaMap.of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6)

/**
 * Creates an unmodifiable map containing the given entries.
 *
 * @param k1 the key of the first entry
 * @param v1 the value of the first entry
 * @param k2 the key of the second entry
 * @param v2 the value of the second entry
 * @param k3 the key of the third entry
 * @param v3 the value of the third entry
 * @param k4 the key of the fourth entry
 * @param v4 the value of the fourth entry
 * @param k5 the key of the fifth entry
 * @param v5 the value of the fifth entry
 * @param k6 the key of the sixth entry
 * @param v6 the value of the sixth entry
 * @param k7 the key of the seventh entry
 * @param v7 the value of the seventh entry
 * @return an unmodifiable map containing the given entries
 * @param K the type of keys in the map
 * @param V the type of values in the map
 */
fun <K : Any, V : Any> unmodifiableMapOf(
    k1: K,
    v1: V,
    k2: K,
    v2: V,
    k3: K,
    v3: V,
    k4: K,
    v4: V,
    k5: K,
    v5: V,
    k6: K,
    v6: V,
    k7: K,
    v7: V,
): Map<K, V> = JavaMap.of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7)

/**
 * Creates an unmodifiable map containing the given entries.
 *
 * @param k1 the key of the first entry
 * @param v1 the value of the first entry
 * @param k2 the key of the second entry
 * @param v2 the value of the second entry
 * @param k3 the key of the third entry
 * @param v3 the value of the third entry
 * @param k4 the key of the fourth entry
 * @param v4 the value of the fourth entry
 * @param k5 the key of the fifth entry
 * @param v5 the value of the fifth entry
 * @param k6 the key of the sixth entry
 * @param v6 the value of the sixth entry
 * @param k7 the key of the seventh entry
 * @param v7 the value of the seventh entry
 * @param k8 the key of the eighth entry
 * @param v8 the value of the eighth entry
 * @return an unmodifiable map containing the given entries
 * @param K the type of keys in the map
 * @param V the type of values in the map
 */
fun <K : Any, V : Any> unmodifiableMapOf(
    k1: K,
    v1: V,
    k2: K,
    v2: V,
    k3: K,
    v3: V,
    k4: K,
    v4: V,
    k5: K,
    v5: V,
    k6: K,
    v6: V,
    k7: K,
    v7: V,
    k8: K,
    v8: V,
): Map<K, V> = JavaMap.of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8)

/**
 * Creates an unmodifiable map containing the given entries.
 *
 * @param k1 the key of the first entry
 * @param v1 the value of the first entry
 * @param k2 the key of the second entry
 * @param v2 the value of the second entry
 * @param k3 the key of the third entry
 * @param v3 the value of the third entry
 * @param k4 the key of the fourth entry
 * @param v4 the value of the fourth entry
 * @param k5 the key of the fifth entry
 * @param v5 the value of the fifth entry
 * @param k6 the key of the sixth entry
 * @param v6 the value of the sixth entry
 * @param k7 the key of the seventh entry
 * @param v7 the value of the seventh entry
 * @param k8 the key of the eighth entry
 * @param v8 the value of the eighth entry
 * @param k9 the key of the ninth entry
 * @param v9 the value of the ninth entry
 * @return an unmodifiable map containing the given entries
 * @param K the type of keys in the map
 * @param V the type of values in the map
 */
fun <K : Any, V : Any> unmodifiableMapOf(
    k1: K,
    v1: V,
    k2: K,
    v2: V,
    k3: K,
    v3: V,
    k4: K,
    v4: V,
    k5: K,
    v5: V,
    k6: K,
    v6: V,
    k7: K,
    v7: V,
    k8: K,
    v8: V,
    k9: K,
    v9: V,
): Map<K, V> = JavaMap.of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8, k9, v9)

/**
 * Creates an unmodifiable map containing the given entries.
 *
 * @param k1 the key of the first entry
 * @param v1 the value of the first entry
 * @param k2 the key of the second entry
 * @param v2 the value of the second entry
 * @param k3 the key of the third entry
 * @param v3 the value of the third entry
 * @param k4 the key of the fourth entry
 * @param v4 the value of the fourth entry
 * @param k5 the key of the fifth entry
 * @param v5 the value of the fifth entry
 * @param k6 the key of the sixth entry
 * @param v6 the value of the sixth entry
 * @param k7 the key of the seventh entry
 * @param v7 the value of the seventh entry
 * @param k8 the key of the eighth entry
 * @param v8 the value of the eighth entry
 * @param k9 the key of the ninth entry
 * @param v9 the value of the ninth entry
 * @param k10 the key of the tenth entry
 * @param v10 the value of the tenth entry
 * @return an unmodifiable map containing the given entries
 * @param K the type of keys in the map
 * @param V the type of values in the map
 */
fun <K : Any, V : Any> unmodifiableMapOf(
    k1: K,
    v1: V,
    k2: K,
    v2: V,
    k3: K,
    v3: V,
    k4: K,
    v4: V,
    k5: K,
    v5: V,
    k6: K,
    v6: V,
    k7: K,
    v7: V,
    k8: K,
    v8: V,
    k9: K,
    v9: V,
    k10: K,
    v10: V,
): Map<K, V> = JavaMap.of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8, k9, v9, k10, v10)

/**
 * Creates an unmodifiable map containing the given entries.
 *
 * @param pairs the entries to add to the map
 * @return an unmodifiable map containing the given entries
 * @param K the type of keys in the map
 * @param V the type of values in the map
 */
fun <K : Any, V : Any> unmodifiableMapOf(vararg pairs: Pair<K, V>): Map<K, V> = pairs.toUnmodifiableMap()

/**
 * Creates an unmodifiable map containing the given entries.
 *
 * @param entries the entries to add to the map
 * @return an unmodifiable map containing the given entries
 * @param K the type of keys in the map
 * @param V the type of values in the map
 */
fun <K : Any, V : Any> unmodifiableMapOf(vararg entries: Map.Entry<K, V>): Map<K, V> =
    VarargCopyWorkaround.unmodifiableMapOf(entries)

//endregion
