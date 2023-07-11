# Sprinkler: Utils

## Summary

- [Intentions](#intentions)
- [CloseableScope](#closeablescope)

## Intentions

The purpose of this module is to provide a set of utilities that are useful for any project, but are not complex 
enough to deserve their own module.

You may see it as a "stdlib++".

## CloseableScope

Similarly to Java's `try-with-resouces` statement, in Kotlin we can use the `Closeable::use` and `AutoCloseable::use` 
methods to automatically handle the closing of a resource, even in case of exceptions during usage.

But one might see that they are not entirely symmetrical in their usage as soon as we want to use multiple resources:

```java
try (var resource1 = new Resource1(); var resource2 = new Resource2()) {
    // Use resources
}
```

```kt
Resource1().use { resource1 ->
    Resource2().use { resource2 ->
        // Use resources
    }
}
```

See how Kotlin's version nests the `use` calls, contrary to Java's version.

Another issue arises in Java's case when we want to use a new resource within the `try` block, but later that the
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

The `closeableScope` function solves both of these issues, while also being basically free of cost.
It accepts a lambda as a parameter that receives a `CloseableScope` as the receiver, which has a `using` method
that adds a new resource to the scope (and returns the resource itself). A scoped extension `AutoCloseable::using` is
also provided for convenience while chaining calls.

It may also accept one or several optional resources as parameters to add them to the scope (and close them at the end)

Here is an example of how to use it:

```kt
val someCloseable = MyCloseable()
val ok = closeableScope(someCloseable) { // will autoclose someCloseable at the end of the scope
    val sin = using(System.`in`) // will autoclose the input stream at the end of the scope
    val f = File("someFile")
        .inputStream().usingSelf() // will autoclose the input stream at the end of the scope
        .buffered().usingSelf() // will autoclose the buffered input stream at the end of the scope
    
    // Do stuff with sin and f
    
    true // return value of the scope
}
```