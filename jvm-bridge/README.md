# Sprinkler: JVM Bridge

[![Maven Central](https://img.shields.io/maven-central/v/com.black-kamelia.sprinkler/jvm-bridge)](https://central.sonatype.com/artifact/com.black-kamelia.sprinkler/jvm-bridge)

## Summary

- [Intentions](#intentions)
- [KotlinDslAdapter](#kotlindsladapter)

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
