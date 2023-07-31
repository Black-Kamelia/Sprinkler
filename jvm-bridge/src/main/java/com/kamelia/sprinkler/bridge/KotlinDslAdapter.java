package com.kamelia.sprinkler.bridge;

import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Unit;

/**
 * Interface for adapting Kotlin DSLs to Java. As Kotlin DSLs usually use lambdas that return {@link Unit}, it is not
 * practical to use them in Java, because Java lambdas have to explicitly return {@link Unit}. To solve this problem,
 * this interface simply provides a method that returns {@link Unit} by default and can be implemented on classes
 * involved with Kotlin DSLs to provide a Java-friendly API.
 * <p>
 * Example:
 * <pre>
 * // Builder.kt
 * class MyBuilder : KotlinDslAdapter {
 *
 *     fun withA(i: Int): MyBuilder {
 *         // ...
 *     }
 *
 *     // ...
 * }
 *
 * fun dsl(block: MyBuilder.() -> Unit): MyObject {
 *   // ...
 * }
 * </pre>
 * <p>
 * Can be used in Java like this:
 * <pre>
 * // Main.java
 * class Main {
 *
 *   public static void main(String[] args) {
 *      var result = BuilderKt.dsl(builder -> builder
 *              .withA(5)
 *              // ...
 *              .unit()
 *      );
 *   }
 *
 * }
 * </pre>
 */
public interface KotlinDslAdapter {

    /**
     * Method to finish a chain call for a Kotlin DSL. It just returns {@link Unit}. There is no need to override it.
     *
     * @return {@link Unit} instance
     * @see KotlinDslAdapter
     */
    @Deprecated(
        message = "this method only exists to simplify Kotlin DSL usage from Java",
        level = DeprecationLevel.HIDDEN
    )
    default Unit unit() {
        return Unit.INSTANCE;
    }

}
