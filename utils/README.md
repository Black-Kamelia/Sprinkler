# Sprinkler: Utils

[![Maven Central](https://img.shields.io/maven-central/v/com.black-kamelia.sprinkler/utils)](https://central.sonatype.com/artifact/com.black-kamelia.sprinkler/utils)

## Summary

- [Intentions](#intentions)
- [String interpolation](#string-interpolation)
  - [String syntax](#string-syntax)
  - [VariableDelimiter](#variabledelimiter)
  - [Interpolation](#interpolation)
    - [Map based overloads](#map-based-overloads)
    - [Indexed overloads](#indexed-overloads)
    - [Iterative overloads](#iterative-overloads)
  - [VariableResolvers](#variableresolvers)
- [CloseableScope](#closeablescope)
- [Box Delegate](#box-delegate)
- [Collector Factories](#collector-factories)
- [ByteArrayDecoding](#bytearraydecoding)
- [ByteAccess](#byteaccess)
- [Casts](#casts)
- [Exceptions](#exceptions)
- [Changelog](#changelog)

## Intentions

The purpose of this module is to provide a set of utilities that are useful for any project, but are not complex
enough to deserve their own module.

You may see it as a "stdlib++".

## String interpolation

Kotlin's string interpolation is great, but can only be used in the context of string literals. This module provides a
few extension functions to allow dynamic string interpolation with any object.

### String syntax

Strings that are valid for interpolation are defined as follows:

- String may contain zero, one or more variables delimited by a start sequence and an end sequence (these sequences
  can be specified, but for the examples, we will use curly braces `{{` and `}}` as start and end sequences) ;
- Escaping of start sequences is possible using a backslash (`\`), and only the start character needs to be escaped ;
- Any non-escaped start sequence is considered as the start of a variable and can be closed by the first non-escaped
  end sequence ;
- Any content between a non-escaped start sequence and the first non-escaped character of en end sequence is considered 
  as the variable name, and will be used to retrieve the value of the variable ;

Here are a few examples of valid strings:

- `"Hello {{Name}, I'm {{my-NAME}} and I'm {{myAge}} years old."`
- `"I like {{0}} and {{1}}."`
- `"I ate \{{something."`
- `"{{}} or {{}} ?"`
- `"See you next {{time_unit}}, at {{}}."`

In the next sections, some cases where variable names must follow specific rules will be presented.

### VariableDelimiter

Before going more in depth regarding the different interpolation functions overloads, let's first introduce the
`VariableDelimiter` class, which allows one to specify the start and end sequences delimiting the variables in the
string. Delimiters can be instantiated through the `VariableDelimiter.create(start: String, end: String)` factory
function.

Both start and end sequences can be composed of almost any character, but must follow the following rules:
- They cannot be blank (meaning empty or only composed of whitespace characters) ;
- They cannot contain a backslash (`'\'`) ;
- They cannot be the same.

There also exists a default delimiter `VariableDelimiter.default` which is composed of the double curly braces `"{{"`
and `"}}"`. This default implementation is used by default in all the interpolation functions.

### Interpolation

As stated previously, in the following examples all functions will use the default `VariableDelimiter` (`{{` and `}}`).

`interpolate` overloads can be divided in three categories which will be presented in the following sections.

#### Map based overloads

The first overload category uses the variable name as a key to retrieve the value of the variable from a map. In case
the variable name is not present in the map, an `IllegalArgumentException` will be thrown.

It exists in two variants, the first one being `interpolate(map: Map<String, Any>): String` using the provided map
directly.

```kt
val string: String = "Hello I'm {{name}}, and I'm {{age}} years old.".interpolate(
    mapOf(
        "name" to "John",
        "age" to 42,
    )
)
println(string) // prints "Hello I'm John, and I'm 42 years old."
```

And the second one, `interpolate(vararg args: Pair<String, Any>): String` converting the provided varargs to a map.

```kt
val string: String = "Hello I'm {{name}}, and I'm {{age}} years old.".interpolate(
    "name" to "John",
    "age" to 42,
)
println(string) // prints "Hello I'm John, and I'm 42 years old."
```

#### Indexed overloads

The second overload category uses the variable name as an index to retrieve the value of the variable from a list. These
overloads therefore require the variable names to be valid integers, and the values to be between 0 and the number of
provided arguments minus 1. If one of these overloads is used with a string that does not respect these rules, an
`IllegalArgumentException` will be thrown.

This overload category exists in two variants, the first one being `interpolate(list: List<Any>): String` using the
provided list to retrieve the values.

```kt
val string: String = "I like {{0}} and {{1}}.".interpolate(listOf("apples", "bananas"))
println(string) // prints "I like apples and bananas."
```

And the second one, `interpolateIdx(vararg args: Any): String` using the array of arguments to retrieve the values.

```kt
val string: String = "I like {{0}} and {{1}}.".interpolateIdx("apples", "bananas")
println(string) // prints "I like apples and bananas."
```

Note that this overload's name is different from the others, due to the `varargs Any` parameter causing signature
conflicts. It also exists as a variant `interpolateIdxD(delimiter: VariableDelimiter, args: Array<Any>): String` which
allows to specify a custom delimiter.

#### Iterative overloads

The third overload and last category uses an `Iterator` to retrieve the values of the variables. Each time a variable is
encountered, the iterator is called to retrieve the next value. If the iterator does not have a next value, an
`IllegalArgumentException` will be thrown. Note that this overload does not use the variable name at all, meaning that
it can be anything, even an empty string.

This overload category exists in two variants, the first one being `interpolate(iterator: Iterator<Any>): String` using
the provided iterator to retrieve the values.

```kt
val string: String = "I ate {{}} and {{}}.".interpolate(listOf("apples", "bananas").iterator())
println(string) // prints "I ate apples and bananas."
```

And the second one, `interpolateIt(vararg args: Any): String` using the array of arguments to retrieve the values.

```kt
val string: String = "I ate {{}} and {{}}.".interpolateIt("apples", "bananas")
println(string) // prints "I ate apples and bananas."
```

In the same way as the indexed overloads, this overload's name is different from the others, due to the `varargs Any` 
parameter causing signature conflicts. It also exists as a variant
`interpolateItD(delimiter: VariableDelimiter, args: Array<Any>): String` which allows to specify a custom delimiter.

### VariableResolvers

The previously introduced functions are all using the default interpolation function
`<T> interpolate(T, VariableDelimiter, VariableResolver<T>)` (where, again, the VariableDelimiter is optional) under the
hood. This function takes a `VariableResolver` as the first parameter, which is a functional interface representing a
function that takes a variable name, a context, and returns the value of the variable usually using the name and the
context.

This library provides a few implementations of this interface, which are used by the previously introduced functions.

Here are the provided implementations:
- `VariableResolver.fromMap(): VariableResolver<Map<String, Any>>` returns a resolver that uses a map to retrieve the
  values of the variables.
- `VariableResolver.fromArray(): VariableResolver<Array<out Any>>` returns a resolver that uses an array to retrieve
  the values of the variables.
- `VariableResolver.fromList(): VariableResolver<List<Any>>` returns a resolver that uses a list to retrieve the values
  of the variables.
- `VariableResolver.fromIterator(): VariableResolver<Iterator<Any>>` returns a resolver that uses an iterator to
  retrieve the values of the variables.

Users can also create their own `VariableResolver` implementations. Here is a dumb but simple example of how to create
and use a custom `VariableResolver`:

```kt
val myResolver: VariableResolver<Int> = VariableResolver<Int> { name: String, i: Int -> name + i }
val string: String = "Hello I'm {{name}}, and I'm {{age}} years old.".interpolate(0, resolver = myResolver)
println(string) // prints "Hello I'm name0, and I'm age1 years old."
```

## CloseableScope

Similarly to Java's `try-with-resources` statement, in Kotlin we can use the `Closeable::use` and `AutoCloseable::use`
methods to automatically handle the closing of a resource, even in case of exceptions during usage.

But one might see that they are not entirely symmetrical in their usage as soon as we want to use multiple resources:

```java
// java
class Main {
    public static void main(String[] args) {
        try (var resource1 = new Resource1(); var resource2 = new Resource2()) {
            // Use resources
        }
    }
}
```

```kt
// kotlin
Resource1().use { resource1 ->
    Resource2().use { resource2 ->
        // Use resources
    }
}
```

See how Kotlin's version nests the `use` calls, contrary to Java's version.

Another issue arises in Java's case when we want to use a new resource within the `try` block, but later than the
already opened ones:

```java
class Main {
    public static void main(String[] args) {
        try (var resource1 = new Resource1()) {
            resource1.doSomething();
            // Use resource1
            try (var resource2 = new Resource2()) {
                // Use resource2
            }
        }
    }
}
```

This also requires nesting the `try` blocks.

The `closeableScope` function solves both of these issues.

It accepts a lambda as a parameter that receives a `CloseableScope` as the receiver, which has a `using` method
that adds a new resource to the scope (and returns the resource itself).

```kt
closeableScope { // this: CloseableScope
    val resource1 = using(MyCloseable()) // will autoclose at the end of the scope
    val resource2 = using(MyOtherCloseable()) // will autoclose at the end of the scope

    resource1.doSomething()
    resource2.trySomething()
}
```

A scoped extension `AutoCloseable::usingSelf` is also provided for convenience while chaining calls.

```kt
closeableScope { // this: CloseableScope
    val resource = File("someFile.txt")
        .inputStream().usingSelf() // will autoclose at the end of the scope
        .buffered()

    println(resource.readAllBytes())
}
```

Note that the lambda may return a value, which will be the return value of the entire scope.

```kt
val content = closeableScope { // this: CloseableScope
    val resource = using(MyCloseable())
    resource.readContent() // the return value of the scope
}
```

`CloseableScope` is an inline class which only contains the list of resources to close. Coupled with the fact that
`closeableScope` is inlined too, this means that it is basically free of cost.

`closeableScope` may also accept one or several optional resources as parameters to add them to the scope (and close
them at the end)

```kt
val someCloseable = MyCloseable()
val someOtherCloseable = MyOtherCloseable()
closeableScope(someCloseable, someOtherCloseable) { // will autoclose these at the end of the scope
    someCloseable.doSomething()
    someOtherCloseable.trySomething()
    someCloseable.doSomethingElse()
}
```

> The semantics of `closeableScope` in regard to exceptions are the exact same as Java's `try-with-resources` statement:
> - If an exception is thrown during the execution of the scope, all resources will be closed in the reverse order of
    their declaration, and the exception will be caught and cached.
> - If the actual closing of one of the resources throws an exception, it will be added as a suppressed exception to the
>   original one.
> - The original exception will be rethrown.

## Box Delegate

There are situations where one might want to inject a value into a class, but the value is not available at the time of
the class' instantiation. The idea is similar to the `Lazy` delegate, or `lateinit` properties, but should work in the
case where said value needs to be injected in several places, in which case it is not possible to use `lateinit` without
losing a lot of control and encapsulation.

The `Box<T>` delegate is a property delegate that allows to do just that.

It is an interface with several methods and properties:

- the `value` property of type `T`. Trying to get it before it is set will throw an `IllegalStateException`.
- the `isFilled` property of type `Boolean`. It is `false` before the value is set, and `true` after.
- the `getValue` method which allows one to use a `Box` as a property delegate. Trying to read the delegated property
  before the value is set will throw an `IllegalStateException`.

Moreover, it has a sub-interface `Box.Mutable<T>` which adds a `fill` method that allows to set the inner value of the
box, and returns whether it was empty or not, or in other words, if the fill was successful or not (`true` if it was,
`false` if it wasn't). It also features a `setValue` method which allows one to use a `Box.Mutable` as a property.

One may want to implement them, but the real goal is to use the provided factories:

- `Box.empty<T>()` returns an empty box. It is not mutable, and cannot ever be filled.
- `Box.prefilled(value: T)` returns a box that is already filled with the given value. It is not mutable and will always
  be filled.
- `Box.singleWrite<T>()` returns a mutable box that can be filled once, and only once. Trying to fill it a second time
  will do nothing and yield `false`.
- `Box.rewritable<T>()` return a mutable box that can be filled multiple times and starts empty. The `fill` method will
  always return `true`.
- `Box.rewritable(value: T)` returns a mutable box that can be filled multiple times and starts filled with the given
  value. The `fill` method will always return `true`.

Here is a simple example:

```kt
class Foo(intBox: Box<Int>) {
    val i by intBox
}

fun main() {
    val box = Box.singleWrite<Int>()
    val foo = Foo(box)
    runCatching {
        println(foo.i) // Throws an exception
    }
    box.fill(1) // returns true
    println(foo.i) // Prints 1
    box.fill(2) // Does nothing, returns false
}
```

## Collector Factories

Java's standard library is missing a few very common `Collector` factories. To that effect, those are provided by
the `ExtendedCollectors` class.

- `ExtendedCollectors::toMap` returns a collector that collects elements to a map from pairs of keys and values.
- `ExtendedCollectors::toArray` returns a collector that collects elements to an array.
- `ExtendedCollectors::to[Primitive]Array` returns a collector that collects elements to a primitive array, where
  `[Primitive]` is the wanted primitive (e.g. `toIntArray`, `toDoubleArray`).

## ByteArrayDecoding

This file provides a few extension functions to decode and read a `ByteArray`'s content in a similar way to that of
a `ByteBuffer`'s.

The extensions are:

- `readByte` which requires a start index and returns the byte at that index.
- `readShort` which requires a start index and returns the short from that index, and an optional endianness.
- `readInt` which requires a start index and returns the int from that index, and an optional endianness.
- `readLong` which requires a start index and returns the long from that index, and an optional endianness.
- `readFloat` which requires a start index and returns the float from that index, and an optional endianness.
- `readDouble` which requires a start index and returns the double from that index, and an optional endianness.
- `readBoolean` which has the same behavior as `readByte` but returns `false` if the byte is 0, `true` otherwise.
- `readString` which requires a length to read, an optional `Charset` (UTF-8 by default), and a start index and returns
  the decoded string from that index.

## ByteAccess

Sometimes, we want to interpret a `Number` not as a number, but as a sequence of bytes. This is what the `ByteAccess`
helpers allow. They are a few extension functions to read bytes and bits from a `Number`.

Except for `Byte`, every `Number` type has two new extension functions:

- `bit(index: Int, endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Int` which returns the bit at the given index, and an
  optional endianness.
- `byte(index: Int, endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Byte` which returns the byte at the given index, and
  an optional endianness.

The `endianness` is used to signify if the `Number` should be interpreted as if it was written in big endian or not.

Of course, `Byte` has only the `bit` function, which doesn't accept an `endianness`, since it is already a byte.

## Typing extensions

To simplify casting and type inference, a few extensions are provided.

## Casts

Sprinkler offers several functions to cast values to other types.

### unsafeCast

A simple extension function on `Any?`, that allows to cast it to any type without any check. It is defined as follows:

```kt
@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <T> Any?.unsafeCast(): T = this as T
```

It is useful when you know that a value is of a certain type, but the compiler doesn't, and you would need to add
a `Suppress` annotation to avoid a warning. It's also useful when chaining operations. It is mostly a convenience
function that should only be used in exceptional cases in library code.

It also offers a cleaner syntax to chain operations on a value of an unknown type:

```kt
class Foo(val value: Any)

fun countAInFooString(value: Any): Int = value
    .unsafeCast<Foo>()
    .value
    .unsafeCast<String>()
    .count { 'a' == it }
```

instead of:

```kt
class Foo(val value: Any)

@Suppress("UNCHECKED_CAST")
fun countA(value: Any): Int =
    ((value as Foo)
        .value as String)
        .count { 'a' == it }
```

### castOrNull

A simple extension function on `Any?`, that allows to cast it to any type, and returns `null` if the cast fails (this
function is the equivalent of `as?`). It is defined as follows:

```kt
@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <reified T> Any?.castOrNull(): T? = this as? T
```

In the same way as `unsafeCast`, it is useful when it comes to chaining operations to avoid nested `as?` calls.

### castIfNotNull

A simple extension function on `Any?`, that allows to cast it to any type if it is not `null`, and returns `null` if the
object is `null`. It is defined as follows:

```kt
inline fun <reified T> Any?.castIfNotNull(): T? = if (this != null) this as T else null
```

### cast

A simple extension function on `Any?`, that allows to cast it to any type, and throws a `ClassCastException` if the cast
fails. It is defined as follows:

```kt
inline fun <reified T> Any?.cast(): T = this as T
```

## Exceptions

The library provides a few methods to throw exceptions with a message. The reason for this is that the standard library
provides the `error` function to throw an IllegalStateException, but nothing else. To complement this, the following
functions are provided:

- `illegalArgument(message: String): Nothing` throws an `IllegalArgumentException` with the given message.
- `assertionError(message: String): Nothing` throws an `AssertionError` with the given message.

## Changelog

[Changelog](CHANGELOG.md)
