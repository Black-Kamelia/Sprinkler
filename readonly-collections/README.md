# Sprinkler: Collections

## Summary

- [Intentions](#intentions)
- [General Documentation](#general-documentation)
- [API Documentation](#api-documentation)

## Intentions

The purpose of this module is to provide read-only collections to the user.

Then why not use [kotlinx.collections.immutable](https://github.com/Kotlin/kotlinx.collections.immutable) instead?

Because while that library offers a good API, it is not entirely cast-safe.
For example, casting an iterator obtained from an `ImmutableList` allows you to modify the list.
This module fixes this issue.

Moreover, for semantic reasons, the collections here are not called `Immutable...` but `ReadOnly...` instead.

## General Documentation

This library provides interfaces for read-only collections.

| Interface              | Bases                              |
|------------------------|------------------------------------|
| `ReadOnlyCollection`   | `Collection`                       |
| `ReadOnlyIterable`     | `Iterable`                         |
| `ReadOnlyIterator`     | `Iterator`                         |
| `ReadOnlyList`         | `ReadOnlyCollection`, `List`       |
| `ReadOnlyListIterator` | `ReadOnlyIterator`, `ListIterator` |
| `ReadOnlySet`          | `ReadOnlyCollection`, `Set`        |
| `ReadOnlyMap`          | `Map`                              |

One can obtain them in the same ways as standard collections, for example:

```kt
val readOnlyList = readOnlyListOf(1, 2, 3)
```

Or by copy or wrapping:

```kt
val list = listOf(1, 2, 3)
val readOnlyListCopy = list.toReadOnlyList()
val readOnlyListWrapped = list.asReadOnlyList()
```

This applies to the other interfaces as well.

It is to be noted that `toReadOnlyX` methods creates a copy of the collection, 
while `asReadOnlyX` methods wrap the collection when necessary 
(as in, it does nothing if it already was read-only).

## API Documentation

### Interfaces

- [ReadOnlyCollection](#readonlycollection)
- [ReadOnlyIterable](#readonlyiterable)
- [ReadOnlyIterator](#readonlyiterator)
- [ReadOnlyList](#ReadOnlyList)
- [ReadOnlyListIterator](#readonlylistiterator)
- [ReadOnlySet](#readonlyset)
- [ReadOnlyMap](#readonlymap)

### ReadOnlyCollection

```kt
interface ReadOnlyCollection<out E> : ReadOnlyIterable<E>, Collection<E>
```

Represents a read-only Collection of elements. 
This interface and all its sub-interfaces only allow read-only operations.
Implementations of this interface have the responsibility to be read-only. 
However, read-only is not equivalent to immutable. 
If the implementation is a wrapper around a mutable collection, it is still read-only but not immutable.

A read-only collection is iterable through its [ReadOnlyIterator](#readonlyiterator) 
(because it implements [ReadOnlyIterable](#readonlyiterable)).

```kt
/**
 * Creates a [ReadOnlyCollection] copy of the given [Iterable].
 *
 * @receiver the [Iterable] to copy
 * @return a [ReadOnlyCollection] copy of the given [Iterable]
 * @param T the type of the elements in the [Iterable]
 */
fun <T> Iterable<T>.toReadOnlyCollection(): ReadOnlyCollection<T>
```

```kt
/**
 * Wraps the given [Collection] into a [ReadOnlyCollection].
 *
 * @receiver the [Collection] to wrap
 * @return a [ReadOnlyCollection] wrapping the given [Collection]
 * @param T the type of the elements in the [Collection]
 */
fun <T> Collection<T>.asReadOnlyCollection(): ReadOnlyCollection<T>
```

### ReadOnlyIterable

```kt
interface ReadOnlyIterable<out E> : Iterable<E>
```

Represents a read-only `Iterable`. 
This interface overrides the Iterable.iterator method to 
change the return type to a [ReadOnlyIterator](#readonlyiterator) which is a read-only `Iterator`.

One can obtain a read-only iterator from a read-only iterable by calling the `ReadOnlyIterable::iterator` method.

```kt
/**
 * Wraps the given [Iterable] into a [ReadOnlyIterable].
 *
 * @receiver the [Iterable] to wrap
 * @return a [ReadOnlyIterable] wrapping the given [Iterable]
 * @param T the type of the elements in the [Iterable]
 */
fun <T> Iterable<T>.asReadOnlyIterable(): ReadOnlyIterable<T>
```

### ReadOnlyIterator

```kt
interface ReadOnlyIterator<out E> : Iterator<E>
```

Represents a read-only Iterator over a collection. 
This interface and all its sub-interfaces only allow read-only operations.

This cannot be down-cast to any implementation of `Iterator` that is mutable (one will never be able to down-cast it).
Moreover, one cannot `remove` from a read-only iterator.

```kt
/**
 * Returns a [ReadOnlyIterator] over the elements of this iterable.
 *
 * @receiver the [Iterable] to iterate over
 * @return a [ReadOnlyIterator] over the elements of this iterable
 * @param T the type of the elements in the [Iterable]
 */
fun <T> Iterable<T>.readOnlyIterator(): ReadOnlyIterator<T>
```

### ReadOnlyList

```kt
interface ReadOnlyList<out E> : List<E>, ReadOnlyCollection<E>
```

Represents a read-only ordered `Collection` of elements. 
This interface and all its sub-interfaces only allow read-only operations.
Implementations of this interface have the responsibility to be read-only. 
However, read-only is not equivalent to immutable.
If the implementation is a wrapper around a mutable list, it is still read-only but not immutable.

This cannot be down-cast to any implementation of `List` that is mutable (one will never be able to down-cast it).
Moreover, not write operations are allowed.

```kt
/**
 * Creates a [ReadOnlyList] from the given vararg [elements].
 *
 * @param elements the elements to create the [ReadOnlyList] from
 * @return a [ReadOnlyList] from the given vararg [elements]
 */
fun <T> readOnlyListOf(vararg elements: T): ReadOnlyList<T>
```

```kt
/**
 * Creates a [ReadOnlyList] copy of the given [Iterable].
 *
 * @receiver the [Iterable] to copy
 * @return a [ReadOnlyList] copy of the given [Iterable]
 * @param T the type of the elements in the [Iterable]
 */
fun <T> Iterable<T>.toReadOnlyList(): ReadOnlyList<T>
```

```kt
/**
 * Wraps the given [List] into a [ReadOnlyList].
 *
 * @receiver the [List] to wrap
 * @return a [ReadOnlyList] wrapping the given [List]
 * @param T the type of the elements in the [List]
 */
fun <T> List<T>.asReadOnlyList(): ReadOnlyList<T>
```

```kt
/**
 * Creates a [ReadOnlyList] copy of the given [Array].
 *
 * @receiver the [Array] to copy
 * @return a [ReadOnlyList] copy of the given [Array]
 * @param T the type of the elements in the [Array]
 */
fun <T> Array<T>.toReadOnlyList(): ReadOnlyList<T>
```

```kt
/**
 * Wraps the given [Array] into a [ReadOnlyList].
 *
 * @receiver the [Array] to wrap
 * @return a [ReadOnlyList] wrapping the given [Array]
 * @param T the type of the elements in the [Array]
 */
fun <T> Array<T>.asReadOnlyList(): ReadOnlyList<T>
```

### ReadOnlyListIterator

```kt
interface ReadOnlyListIterator<out T> : ReadOnlyIterator<T>, ListIterator<T>
```

Represents a read-only `ListIterator`. 
Like its super-interface `ReadOnlyIterator`, this interface does not support the `remove` operation.

```kt
/**
 * Creates a [ReadOnlyListIterator] over the elements of this list.
 *
 * @receiver the [List] to iterate over
 * @return a [ReadOnlyListIterator] over the elements of this list
 * @param T the type of the elements in the [List]
 */
fun <T> List<T>.readOnlyListIterator(): ReadOnlyListIterator<T>
```

### ReadOnlySet

```kt
interface ReadOnlySet<out E> : Set<E>, ReadOnlyCollection<E>
```

Represents a read-only unordered `Collection` of elements that does not allow duplicate elements.
Implementations of this interface have the responsibility to be read-only.
However, read-only is not equivalent to immutable. 
If the implementation is a wrapper around a mutable set, it is still read-only but not immutable.

This cannot be down-cast to any implementation of `Set` that is mutable (one will never be able to down-cast it).
Moreover, not write operations are allowed.

```kt
/**
 * Creates a [ReadOnlySet] from the given vararg [elements].
 *
 * @param elements the elements to create the [ReadOnlySet] from
 * @return a [ReadOnlySet] from the given vararg [elements]
 * @param T the type of the elements in the [Array]
 */
fun <T> readOnlySetOf(vararg elements: T): ReadOnlySet<T>
```

```kt
/**
 * Creates a [ReadOnlySet] copy of the given [Iterable].
 *
 * @receiver the [Iterable] to copy
 * @return a [ReadOnlySet] copy of the given [Iterable]
 * @param T the type of the elements in the [Iterable]
 */
fun <T> Iterable<T>.toReadOnlySet(): ReadOnlySet<T>
```

```kt
/**
 * Wraps the given [Set] into a [ReadOnlySet].
 *
 * @receiver the [Set] to wrap
 * @return a [ReadOnlySet] wrapping the given [Set]
 * @param T the type of the elements in the [Set]
 */
fun <T> Set<T>.asReadOnlySet(): ReadOnlySet<T>
```

```kt
/**
 * Creates a [ReadOnlySet] copy of the given [Array].
 *
 * @receiver the [Array] to copy
 * @return a [ReadOnlySet] copy of the given [Array]
 * @param T the type of the elements in the [Array]
 */
fun <T> Array<T>.toReadOnlySet(): ReadOnlySet<T>
```

### ReadOnlyMap

```kt
interface ReadOnlyMap<K, out V> : Map<K, V>
```

Represents a read-only collection of pairs (key/value object).
This interface and all its sub-interfaces only allow read-only operations.
Implementations of this interface have the responsibility to be read-only. 
However, read-only is not equivalent to immutable. 
If the implementation is a wrapper around a mutable map, it is still read-only but not immutable.

This cannot be down-cast to any implementation of `Map` that is mutable (one will never be able to down-cast it).
Moreover, not write operations are allowed.

```kt
/**
 * Creates a [ReadOnlyMap] from the given vararg [pairs].
 *
 * @param pairs the pairs to create the [ReadOnlyMap] from
 * @return a [ReadOnlyMap] from the given vararg [pairs]
 * @param K the type of the keys in the [Array]
 * @param V the type of the values in the [Array]
 */
fun <K, V> readOnlyMapOf(vararg pairs: Pair<K, V>): ReadOnlyMap<K, V>
```

```kt
/**
 * Creates a [ReadOnlyMap] copy from the given [Iterable] of [Pair]s.
 *
 * @receiver the [Iterable] to copy
 * @return a [ReadOnlyMap] copy from the given [Iterable]
 * @param K the type of the keys in the [Iterable]
 * @param V the type of the values in the [Iterable]
 */
fun <K, V> Iterable<Pair<K, V>>.toReadOnlyMap(): ReadOnlyMap<K, V>
```

```kt
/**
 * Creates a [ReadOnlyMap] copy from the given [Map].
 *
 * @receiver the [Map] to copy
 * @return a [ReadOnlyMap] copy from the given [Map]
 * @param K the type of the keys in the [Map]
 * @param V the type of the values in the [Map]
 */
fun <K, V> Map<K, V>.toReadOnlyMap(): ReadOnlyMap<K, V>
```

```kt
/**
 * Wraps the given [Map] into a [ReadOnlyMap].
 *
 * @receiver the [Map] to wrap
 * @return a [ReadOnlyMap] wrapping the given [Map]
 * @param K the type of the keys in the [Map]
 * @param V the type of the values in the [Map]
 */
fun <K, V> Map<K, V>.asReadOnlyMap(): ReadOnlyMap<K, V>
```

```kt
/**
 * Creates a [ReadOnlyMap] copy from the given [Array] of [Pair]s.
 *
 * @receiver the [Array] to copy
 * @return a [ReadOnlyMap] copy from the given [Array]
 * @param K the type of the keys in the [Array]
 * @param V the type of the values in the [Array]
 */
fun <K, V> Array<Pair<K, V>>.toReadOnlyMap(): ReadOnlyMap<K, V>
```
