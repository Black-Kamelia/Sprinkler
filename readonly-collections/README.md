# Sprinkler: ReadOnly Collections

[![Maven Central](https://img.shields.io/maven-central/v/com.black-kamelia.sprinkler/readonly-collections)](https://central.sonatype.com/artifact/com.black-kamelia.sprinkler/readonly-collections)

## Summary

- [Intentions](#intentions)
- [General Documentation](#general-documentation)
- [Interfaces](#interfaces)
  - [ReadOnlyCollection](#readonlycollection)
  - [ReadOnlyIterable](#readonlyiterable)
  - [ReadOnlyIterator](#readonlyiterator)
  - [ReadOnlyList](#readonlylist) 
  - [ReadOnlyListIterator](#readonlylistiterator)
  - [ReadOnlySet](#readonlyset)
  - [ReadOnlyMap](#readonlymap)
- [Changelog](#changelog)

## Intentions

The purpose of this module is to provide read-only collections to the user.

Then why not use [kotlinx.collections.immutable](https://github.com/Kotlin/kotlinx.collections.immutable) instead?

Because while that library offers a good API, it is not entirely cast-safe.
For example, casting an iterator obtained from an `ImmutableList` allows you to modify the list.
This module fixes this issue.

Moreover, for semantic reasons, the collections here are not called `Immutable...` but `ReadOnly...` instead.

## General Documentation

This library provides interfaces for read-only collections.

| Interface              | Bases                              | Functions                                            |
|------------------------|------------------------------------|------------------------------------------------------|
| `ReadOnlyCollection`   | `Collection`                       | `toReadOnlyCollection`, `asReadOnlyCollection`       |
| `ReadOnlyIterable`     | `Iterable`                         | `asReadonlyIterable`                                 |
| `ReadOnlyIterator`     | `Iterator`                         | `readOnlyIterator`                                   |
| `ReadOnlyList`         | `ReadOnlyCollection`, `List`       | `readOnlyListOf`, `toReadOnlyList`, `asReadOnlyList` |
| `ReadOnlyListIterator` | `ReadOnlyIterator`, `ListIterator` | `readOnlyListIterator`                               |
| `ReadOnlySet`          | `ReadOnlyCollection`, `Set`        | `readOnlySetOf`, `toReadOnlySet`, `asReadOnlySet`    |
| `ReadOnlyMap`          | `Map`                              | `readOnlyMapOf`, `toReadOnlyMap`, `asReadOnlyMap`    |

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

It is to be noted that `toReadOnlyX` methods create a copy of the collection, 
while `asReadOnlyX` methods wrap the collection when necessary 
(as in, they do nothing if the collection was already read-only).

## Interfaces

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

### ReadOnlyIterable

```kt
interface ReadOnlyIterable<out E> : Iterable<E>
```

Represents a read-only `Iterable`. 
This interface overrides the Iterable.iterator method to 
change the return type to a [ReadOnlyIterator](#readonlyiterator) which is a read-only `Iterator`.

One can obtain a read-only iterator from a read-only iterable by calling the `ReadOnlyIterable::iterator` method.

### ReadOnlyIterator

```kt
interface ReadOnlyIterator<out E> : Iterator<E>
```

Represents a read-only Iterator over a collection. 
This interface and all its sub-interfaces only allow read-only operations.

This cannot be down-cast to any implementation of `Iterator` that is mutable (one will never be able to down-cast it).
Moreover, one cannot `remove` from a read-only iterator.

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
Moreover, no write operations are allowed.

### ReadOnlyListIterator

```kt
interface ReadOnlyListIterator<out T> : ReadOnlyIterator<T>, ListIterator<T>
```

Represents a read-only `ListIterator`. 
Like its super-interface `ReadOnlyIterator`, this interface does not support the `remove` operation.

### ReadOnlySet

```kt
interface ReadOnlySet<out E> : Set<E>, ReadOnlyCollection<E>
```

Represents a read-only unordered `Collection` of elements that does not allow duplicate elements.
Implementations of this interface have the responsibility to be read-only.
However, read-only is not equivalent to immutable. 
If the implementation is a wrapper around a mutable set, it is still read-only but not immutable.

This cannot be down-cast to any implementation of `Set` that is mutable (one will never be able to down-cast it).
Moreover, no write operations are allowed.

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
Moreover, no write operations are allowed.

## Changelog

[Changelog](CHANGELOG.md)
