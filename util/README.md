# Sprinkler: Utils

## Summary

- [Intentions](#intentions)
- [CloseableScope](#closeablescope)
- [Box Delegate](#box-delegate)
- [Collector Utilities](#collector-utilities)
  - [Collector Shorthands](#collector-shorthands)
  - [Collector Factories](#collector-factories)

## Intentions

The purpose of this module is to provide a set of utilities that are useful for any project, but are not complex 
enough to deserve their own module.

You may see it as a "stdlib++".

## CloseableScope

Similarly to Java's `try-with-resouces` statement, in Kotlin we can use the `Closeable::use` and `AutoCloseable::use` 
methods to automatically handle the closing of a resource, even in case of exceptions during usage.

But one might see that they are not entirely symmetrical in their usage as soon as we want to use multiple resources:

```java
// java
try (var resource1 = new Resource1(); var resource2 = new Resource2()) {
    // Use resources
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
try (var resource1 = new Resource1()) {
	resource1.doSomething();
    // Use resource1
    try (var resource2 = new Resource2()) {
        // Use resource2
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
        .buffered().usingSelf() // will autoclose at the end of the scope
    
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

Moreover, it has sub-interface `Box.Mutable<T>` which adds a `fill` method that allows to set the inner value of the 
box, and returns whether it was empty or not, or in other words, if the fill was successful or not (`true` if it was,
`false` if it wasn't).

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
   box.fill(1)
   println(foo.i) // Prints 1
}
```

## Collector Utilities

Sprinkler-util brings a few utilities to simplify the creation of Java `Collector`s, and their usage which is sometimes
a bit clunky.

### Collector Shorthands

Calling the different functional interfaces composing a `Collector` is often redundant : you obtain the element thanks
to a getter, and then call the interface's method with the arguments. Sprinkler-util provides a few shorthands to
simplify this. They are all extension functions on `Collector` and are all inlined.

- `supply` is a shorthand method for `Collector::supplier::get`
- `accumulate` is a shorthand method for `Collector::accumulator::accept`
- `combine` is a shorthand method for `Collector::combiner::apply`
- `finish` is a shorthand method for `Collector::finisher::apply`
- `characteristics` is a shorthand property for `Collector::characteristics`

### Collector Factories

Java's standard library is missing a few very common `Collector` factories. To that effect, those are provided by
the `ExtendedCollectors` class.

- `ExtendedCollectors.toMap` returns a collector that collects elements to a map from pairs of keys and values.
- `ExtendedCollectors.toArray` returns a collector that collects elements to an array.
- `to[Primitive]Array` returns a collector that collects elements to a primitive array, where `[Primitive]` is the
  wanted primitive (e.g. `toIntArray`, `toDoubleArray`).
