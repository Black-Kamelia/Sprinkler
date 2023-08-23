# Sprinkler: Utils

[![Maven Central](https://img.shields.io/maven-central/v/com.black-kamelia.sprinkler/utils)](https://central.sonatype.com/artifact/com.black-kamelia.sprinkler/utils)

## Summary

- [Intentions](#intentions)
- [String interpolation](#string-interpolation)
  - [String syntax](#string-syntax)
  - [Interpolation](#interpolation)
  - [Custom Variable Resolvers](#custom-variable-resolvers)
- [CloseableScope](#closeablescope)
- [Box Delegate](#box-delegate)
- [Collector Factories](#collector-factories)
- [ByteArrayDecoding](#bytearraydecoding)
- [ByteAccess](#byteaccess)
- [unsafeCast](#unsafecast)
- [Changelog](#changelog)

## Intentions

The purpose of this module is to provide a set of utilities that are useful for any project, but are not complex 
enough to deserve their own module.

You may see it as a "stdlib++".

## String interpolation

Kotlin's string interpolation is great, but can only be used in the context of string literals. This module provides a
few extension functions to allow dynamic string interpolation with any object.

### String syntax

A string that can be interpolated is a string that contains variables, which are delimited by `{}`. The name of the
variable is the content of the braces and must be an alphanumeric string identifier that can contain underscores and
dashes. `"Hello {name}, I'm {my-name} and I'm {my_age} years old."` is a valid string that contains 3 variables.

Any opened brace must be closed, and opening braces can be escaped using a backslash (`\{`). For example, the string
`"Hello {name"` is invalid but `"Hello \{name"` is valid because the opening brace is escaped.

Failure to respect these rules will result in an exception being thrown.

### Interpolation

To interpolate a string, one must use the `interpolate` extension function on a `String`. It exists in several variants.

The first ones are `interpolate(vararg args: Pair<String, Any>)` and `interpolate(args: Map<String, Any>)`. They take a
list of pairs of variable names associated with their values or a map with the same semantics. The variable names must
be valid identifiers, and the values can be of any type. The function will replace all variables in the string with
their associated value.

```kt
val string = "Hello I'm {name}, and I'm {age} years old.".interpolate(
    "name" to "John",
    "age" to 42,
)
print(string) // prints "Hello I'm John, and I'm 42 years old."
```

Both of these functions accepts an optional `fallback` parameter, which is a string that will be used as a fallback
value if a variable is not found in the given arguments. If no fallback is provided, an exception will be thrown.

```kt
val string = "Hello I'm {name}, and I'm {age} years old.".interpolate(
    "name" to "John",
    fallback = "unknown"
)
print(string) // prints "Hello I'm John, and I'm unknown years old."
```

The second variant comes in two flavors: `interpolate(vararg args: Any)` and `interpolate(args: List<Any>)`. They take
a vararg or a list of values, and will replace the variables in the string using their index in the list. The variable
names in the string must be integers, and in the range of the list. If a variable name is not an integer, or is out of
bounds, an exception will be thrown.

```kt
val string = "'Hello I'm {0}, and I'm {1} years old.' said {0}.".interpolate(
    "John",
    42,
)
print(string) // prints "'Hello I'm John, and I'm 42 years old.' said John"
```

### Custom Variable Resolvers

The previously presented functions are all using under the hood the default interpolation function
`interpolate(VariableResolver)`. This function takes a `VariableResolver` as a parameter, which is a functional
interface representing a function that takes a variable name and returns its value.

Here is a dumb but simple example of how to use it:

```kt
val myResolver = VariableResolver { name -> name.reversed() }
val string = "Hello I'm {name}, and I'm {age} years old.".interpolate(myResolver)
print(string) // prints "Hello I'm eman, and I'm ega years old."
```

## CloseableScope

Similarly to Java's `try-with-resouces` statement, in Kotlin we can use the `Closeable::use` and `AutoCloseable::use` 
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

`closeableScope` may also accept one or several optional resources as parameters to add them to the scope (and close them at the end)

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
> - If an exception is thrown during the execution of the scope, all resources will be closed in the reverse order of their
>   declaration, and the exception will be caught and cached.
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
    var i by intBox
}

fun main() {
   val box = Box.singleWrite<Int>()
   val foo = Foo(box)
   runCatching {
     println(foo.i) // Throws an exception
   }
   foo.i = 1
   println(foo.i) // Prints 1
}
```

## Collector Factories

Java's standard library is missing a few very common `Collector` factories. To that effect, those are provided by
the `ExtendedCollectors` class.

- `ExtendedCollectors.toMap` returns a collector that collects elements to a map from pairs of keys and values.
- `ExtendedCollectors.toArray` returns a collector that collects elements to an array.
- `to[Primitive]Array` returns a collector that collects elements to a primitive array, where `[Primitive]` is the
  wanted primitive (e.g. `toIntArray`, `toDoubleArray`).

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
file allows. It provides a few extension functions to read bytes and bits from a `Number`.

Except for `Byte`, every `Number` type has two new extension functions:
- `bit(index: Int, endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Int` which returns the bit at the given index, and an optional endianness.
- `byte(index: Int, endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Byte` which returns the byte at the given index, and an optional endianness.

The `endianness` is used to signify if the `Number` should be interpreted as if it was written in big endian or not.

Of course, `Byte` has only the `bit` function, which doesn't accept an `endianness`, since it is already a byte.

## Typing extensions

To simplify casting and type inference, a few extensions are provided.

## unsafeCast

A simple extension function on `Any?`, that allows to cast it to any type without any check. It is defined as follows:

```kt
@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <T> Any?.unsafeCast(): T = this as T
```

It is useful when you know that a value is of a certain type, but the compiler doesn't, and you would need to add
a `Suppress` annotation to avoid a warning. It's also useful when chaining operations. It is mostly a convenience function
that should only be used in exceptional cases in library code.

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

fun countA(value: Any): Int = 
    ((value as Foo)
    .value as String)
    .count { 'a' == it }
```

## castOrNull

A simple extension function on `Any?`, that allows to cast it to any type, and returns `null` if the cast fails (this
function is the equivalent of `as?`). It is defined as follows:

```kt
@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <reified T> Any?.castOrNull(): T? = this as? T
```

In the same way as `unsafeCast`, it is useful when it comes to chaining operations to avoid nested `as?` calls.

## Changelog

[Changelog](CHANGELOG.md)
