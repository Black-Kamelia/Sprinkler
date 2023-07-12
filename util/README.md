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