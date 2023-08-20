# Sprinkler: JVM Bridge

[![Maven Central](https://img.shields.io/maven-central/v/com.black-kamelia.sprinkler/jvm-bridge)](https://central.sonatype.com/artifact/com.black-kamelia.sprinkler/jvm-bridge)

## Summary

- [Intentions](#intentions)
- [KotlinDslAdapter](#kotlindsladapter)
- [Kotlin Lambda Adapters for Java](#kotlin-lambda-adapters-for-java)
  - [LambdaAdapters](#lambdaadapters)
  - [InvokeExtensions](#invokeextensions)
- [Collector Shorthands](#collector-shorthands)
- [Changelog](#changelog)

## Intentions

The purpose of this module is to provide helpers to simplify the creation of libraries that are meant to be used in
both Java and Kotlin. 

It is not meant to be used directly by end-users, but rather by library authors. Unlike other
modules of the Sprinkler project, this module is designed to be added as a dependency to a library, and to be shaded
into it.

## KotlinDslAdapter

One of the provided helpers is the `KotlinDslAdapter` interface. A library author simply needs to add it to another
class or interface as a super-type to get its benefits, that is to say, a method which returns `Unit`. It is useful
to easily adapt a Kotlin DSL to be easily used in Java.

For example:
```kt
// Builder.kt
class MyBuilder : KotlinDslAdapter {
    fun withA(i: Int): MyBuilder {
        // ...
    }
    // ...
}

fun dsl(block: MyBuilder.() -> Unit): MyObject {
    // ...
}
```

Can be used in Java like this:
```java
// Main.java
class Main {
  public static void main(String[] args) {
    var result = BuilderKt.dsl(builder -> builder
      .withA(5)
      .unit()
    );
  }
}
```

## Kotlin Lambda Adapters for Java

Kotlin's type system is very good, and the addition of the `Unit` type allows better type coherence in many cases.
However, while in Java, one can very easily implement lambdas for Kotlin's function types for the most part,
if the function is supposed to return `Unit`, then the lambda will have to explicitly return the `Unit.INSTANCE`
singleton, which is not convenient especially when the lambda is a one-liner, and when comparing it to Kotlin where it
is the equivalent of return void.

### LambdaAdapters

The `LambdaAdapters` class provides a few static methods to adapt lambdas to Kotlin's function types. That is to say,
they adapt the common Java functional interfaces which return `void` to Kotlin's function types which return `Unit`.
(`Runnable`, `Consumer`, `BiConsumer`)

It is important to note that the lambda is not wrapped in a Kotlin function type, it is actually a subtype of both the
Kotlin function type **and** the corresponding Java functional interface.

```kt
// Kotlin
fun foo(block: (String) -> Unit) {
    // ...
}
```

```java
// Java
import static com.kamelia.sprinkler.util.jvmlambda.LambdaAdapters.*; // static import the `a` (adapt) functions

class Main {
    public static void main(String[] args) {
        // you can write
        foo(a(arg -> System.out.println(arg)));
        
        // instead of
        foo(arg -> {
            System.out.println(arg);
            return Unit.INSTANCE;
        });
    }
}
```

No need to explicitly return `Unit.INSTANCE` anymore.

### InvokeExtensions

This file provides an `invoke` extension operator to **every single** Java functional interface from the standard library.

For example, on a `Consumer<T>`, it allows to call `consumer(value)` instead of `consumer.accept(value)`.

## Collector Shorthands

Calling the different functional interfaces composing a `Collector` is often redundant : you obtain the element thanks
to a getter, and then call the interface's method with the arguments. Sprinkler-utils provides a few shorthands to
simplify this. They are all extension functions on `Collector` and are all inlined.

- `supply` is a shorthand method for `Collector::supplier::get`
- `accumulate` is a shorthand method for `Collector::accumulator::accept`
- `combine` is a shorthand method for `Collector::combiner::apply`
- `finish` is a shorthand method for `Collector::finisher::apply`
- `characteristics` is a shorthand property for `Collector::characteristics`

## Changelog

[Changelog](CHANGELOG.md)
