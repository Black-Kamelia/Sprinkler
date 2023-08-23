@file:JvmName("Strings")

package com.kamelia.sprinkler.util


fun String.interpolate(resolver: NameResolver): String {
    val builder = StringBuilder()
    var state = State.DEFAULT
    var keyBuilder = StringBuilder()

    forEachIndexed { index, char ->
        when (state) {
            State.DEFAULT -> {
                when (char) {
                    '{' -> state = State.IN_CURLY
                    '\\' -> state = State.BACKSLASH
                    else -> builder.append(char)
                }
            }
            State.BACKSLASH -> {
                state = State.DEFAULT
                builder.append(char)
            }
            State.IN_CURLY -> {
                if ('}' == char) {
                    val key = keyBuilder.toString()
                    val value = try {
                        resolver.value(key)
                    } catch (e: NameResolver.NameResolutionException) {
                        illegalArgument("Error while resolving variable '$key': ${e.message!!}")
                    }
                    builder.append(value)
                    keyBuilder = StringBuilder()
                    state = State.DEFAULT
                } else {
                    if ('_' != char && '-' != char && !char.isLetterOrDigit()) {
                        illegalArgument("Unexpected character '$char' in interpolated value near character ${index + 1}")
                    } else {
                        keyBuilder.append(char)
                    }
                }
            }
        }
    }

    return builder.toString()
}

fun String.interpolate(vararg args: Any?): String = interpolate(NameResolver.create(*args))

fun String.interpolate(args: Map<String, Any>, fallback: String? = null): String =
    interpolate(NameResolver.create(args, fallback))

fun String.interpolate(vararg args: Pair<String, Any>, fallback: String? = null): String =
    interpolate(NameResolver.create(args.toMap(), fallback))

/**
 * Interface for resolving variable names during string interpolation. This interface maps variable names to their
 * values.
 *
 * @see [String.interpolate]
 * @see [NameResolver.create]
 */
fun interface NameResolver {

    /**
     * Returns the value of the variable with the given [name].
     *
     * Implementations may throw an [NameResolutionException] if the variable is unknown, or return a default value.
     *
     * @param name the name of the variable
     * @return the value of the variable
     * @throws NameResolutionException if the variable is unknown
     */
    fun value(name: String): String

    /**
     * Exception thrown by [NameResolver] implementations when a variable name cannot be resolved.
     *
     * @param message the exception message
     * @see NameResolver.value
     */
    class NameResolutionException(message: String) : IllegalArgumentException(message) {

        override fun fillInStackTrace(): Throwable = this // no need to fill in the stack trace

    }

    companion object {

        /**
         * Creates a [NameResolver] that resolves variables by their index in the given [array][args].
         *
         * The variable passed to [NameResolver.value] is parsed as an integer, and the value at the corresponding index
         * in the [array][args] is returned. If name does not represent a valid integer, or if the index is out of
         * bounds, an [NameResolutionException] is thrown.
         *
         * Example:
         * ```kt
         * val resolver = NameResolver.create("foo", "bar", "baz")
         * val result = "Hello {0}, {2}, {1}".interpolate(resolver)
         * println(result) // prints "Hello foo, baz, bar"
         * ```
         *
         * @param args the array of values
         * @return a [NameResolver] that resolves variables by their index in the given [array][args]
         */
        fun create(vararg args: Any?): NameResolver =
            NameResolver { name ->
                val index = name.toIntOrNull()
                    ?: throw NameResolutionException("index must be a parsable integer, but was'$name'")
                if (index !in args.indices) {
                    throw NameResolutionException("index must be in between 0 and ${args.size}, but was $index")
                }
                args[index].toString()
            }

        /**
         * Creates a [NameResolver] that resolves variables by their name in the given [map][args].
         *
         * The name of the variable passed to [NameResolver.value] is used as a key in the [map][args], and the value
         * associated with that key is returned. If a variable is unknown, the [fallback] value is returned. If the
         * [fallback] value is `null`, an [NameResolutionException] is thrown.
         *
         * Example:
         * ```kt
         * val resolver = NameResolver.create(mapOf("name" to "John", "age" to 42), fallback = "unknown")
         * val result = "Hello {name}, you are {age} years old, and you live in {city}".interpolate(resolver)
         * println(result) // prints "Hello John, you are 42 years old, and you live in unknown"
         * ```
         *
         * @param args the map of values
         * @param fallback the fallback value (defaults to `null`)
         * @return a [NameResolver] that resolves variables by their name in the given [map][args]
         */
        fun create(args: Map<String, Any>, fallback: String? = null): NameResolver =
            NameResolver { name ->
                args[name]?.toString() ?: fallback ?: throw NameResolutionException("unknown variable name '$name'")
            }

        /**
         * Creates a [NameResolver] that resolves variables by their name in the [Pair] array [args]. The array of pairs
         * is converted to a [map][Map].
         *
         * The name of the variable passed to [NameResolver.value] is used as a key in the map created from the
         * [array][args] and the value associated with that key is returned. If a variable is unknown, the [fallback]
         * value is returned. If the [fallback] value is `null`, an [NameResolutionException] is thrown.
         *
         * Example:
         * ```kt
         * val resolver = NameResolver.create("name" to "John", "age" to 42, fallback = "unknown")
         * val result = "Hello {name}, you are {age} years old, and you live in {city}".interpolate(resolver)
         * println(result) // prints "Hello John, you are 42 years old, and you live in unknown"
         * ```
         *
         * @param args the array of pairs
         * @param fallback the fallback value (defaults to `null`)
         * @return a [NameResolver] that resolves variables by their name in the [Pair] array [args]
         */
        fun create(vararg args: Pair<String, Any>, fallback: String? = null): NameResolver =
            create(args.toMap(), fallback)

    }

}


private enum class State {
    DEFAULT,
    BACKSLASH,
    IN_CURLY,
}
