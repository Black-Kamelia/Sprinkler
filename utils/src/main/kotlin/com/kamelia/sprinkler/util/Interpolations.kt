@file:JvmName("Interpolations")

package com.kamelia.sprinkler.util

/**
 * Interpolates variables in this string using the given [resolver]. This function replaces all occurrences of
 * `{variable}` with the value of the variable returned by [VariableResolver.value].
 *
 * Names must be alphanumeric, and may contain underscores and dashes.
 *
 * &nbsp;
 *
 * This function can be used as follows:
 *
 * ```kt
 * val resolver: VariableResolver = ...
 * val result = "Hello {user_name}, you are {user-age} years old".interpolate(resolver)
 * ```
 *
 * @param resolver the [VariableResolver] to use for resolving variable names
 * @return the interpolated string
 * @throws IllegalArgumentException if a variable has an invalid name, or if an error occurs while resolving a variable
 * @see [VariableResolver]
 */
fun String.interpolate(resolver: VariableResolver): String {
    val builder = StringBuilder() // final string builder
    var state = State.DEFAULT // current state of the parser
    var keyBuilder = StringBuilder() // builder used to build the key of the variable

    forEachIndexed { index, char ->
        when (state) {
            State.DEFAULT -> {
                when (char) {
                    '{' -> state = State.IN_CURLY // start of a variable
                    '\\' -> state = State.BACKSLASH // start of an escape sequence
                    else -> builder.append(char) // any other character is appended to the final string
                }
            }
            State.BACKSLASH -> { // in this state the '{' is escaped
                builder.append(char)
                state = State.DEFAULT
            }
            State.IN_CURLY -> {
                if ('}' == char) { // end of the variable
                    val key = keyBuilder.toString()
                    val value = try { // try to resolve the variable
                        resolver.value(key)
                    } catch (e: VariableResolver.ResolutionException) {
                        illegalArgument("Error while resolving variable '$key': ${e.message!!}")
                    }
                    builder.append(value)
                    keyBuilder = StringBuilder()
                    state = State.DEFAULT
                } else { // append the current character to the key
                    require(char.isLetterOrDigit() || '_' == char || '-' == char) {
                        "Unexpected character '$char' in interpolated value near character ${index + 1}"
                    }
                    keyBuilder.append(char)
                }
            }
        }
    }
    require(State.IN_CURLY !== state) { // ensure that the string does not end in the middle of a variable
        "Unexpected end of string in interpolated value"
    }

    return builder.toString()
}

/**
 * Interpolates variables in this string using the given vararg [args].
 *
 * Variable are resolved by their index in the given [args]. The variable passed to is parsed as an integer, and the
 * value at the corresponding index in the [array][args] is returned. If name does not represent a valid integer, or if
 * the index is not in between 0 and the number of arguments, an [IllegalArgumentException] is thrown.
 *
 * &nbsp;
 *
 * It can be used as follows:
 *
 * ```kt
 * val result = "Hello {0}, you are {1} years old".interpolateIndexed("John", 42)
 * ```
 *
 * @param args the vararg of values
 * @return the interpolated string
 * @throws IllegalArgumentException if a variable has an invalid name, or if a variable name is not a valid integer or
 * is if the index is out of bounds
 * @see [VariableResolver]
 */
fun String.interpolateIndexed(vararg args: Any): String = interpolate(VariableResolver.fromVararg(*args))

/**
 * Interpolates variables in this string using the given list [args].
 *
 * Variable are resolved by their index in the given [args]. The variable passed to is parsed as an integer, and the
 * value at the corresponding index in the [list][args] is returned. If name does not represent a valid integer, or if
 * the index is not in between 0 and the number of arguments, an [IllegalArgumentException] is thrown.
 *
 * &nbsp;
 *
 * It can be used as follows:
 *
 * ```kt
 * val args = listOf("John", 42)
 * val result = "Hello {0}, you are {1} years old".interpolateIndexed(args)
 * ```
 *
 * @param args the list of values
 * @return the interpolated string
 * @throws IllegalArgumentException if a variable has an invalid name, or if a variable name is not a valid integer or
 * is if the index is out of bounds
 * @see [VariableResolver]
 */
fun String.interpolateIndexed(args: List<Any>): String = interpolate(VariableResolver.fromList(args))

/**
 * Interpolates variables in this string using the given map of [args].
 *
 * Variable are resolved by their name in the given [map][args]. The name of the variable passed to is used as a key in
 * the [map][args], and the value associated with that key is returned. If a variable is unknown, the [fallback] value
 * is returned. If the [fallback] value is `null`, an [IllegalArgumentException] is thrown.
 *
 * &nbsp;
 *
 * It can be used as follows:
 *
 * ```kt
 * val result = "Hello {name}, you are {age} years old".interpolate(mapOf("name" to "John", "age" to 42))
 * ```
 *
 * @param args the map of values
 * @param fallback the fallback value (defaults to `null`)
 * @return the interpolated string
 * @throws IllegalArgumentException if a variable has an invalid name, or if a variable name is unknown and the
 * [fallback] value is `null`
 * @see [VariableResolver]
 */
fun String.interpolate(args: Map<String, Any>, fallback: String? = null): String =
    interpolate(VariableResolver.fromMap(args, fallback))

/**
 * Interpolates variables in this string using the given [Pair] array [args]. The array of pairs is converted to a
 * [map][Map].
 *
 * Variable are resolved by their name in the [Pair] array [args]. The name of the variable passed to is used as a key
 * in the map created from the [array][args] and the value associated with that key is returned. If a variable is
 * unknown, the [fallback] value is returned. If the [fallback] value is `null`, an [IllegalArgumentException] is
 * thrown.
 *
 * &nbsp;
 *
 * It can be used as follows:
 *
 * ```kt
 * val result = "Hello {name}, you are {age} years old".interpolate("name" to "John", "age" to 42)
 * ```
 *
 * @param args the array of pairs
 * @param fallback the fallback value (defaults to `null`)
 * @return the interpolated string
 * @throws IllegalArgumentException if a variable has an invalid name, or if a variable name is unknown and the
 * [fallback] value is `null`
 * @see [VariableResolver]
 */
fun String.interpolate(vararg args: Pair<String, Any>, fallback: String? = null): String =
    interpolate(VariableResolver.fromMap(args.toMap(), fallback))

/**
 * Interface for resolving variables during string interpolation. This interface maps variable names to their
 * values.
 */
fun interface VariableResolver {

    /**
     * Returns the value of the variable with the given [name].
     *
     * Implementations may throw an [ResolutionException] if the variable is unknown, or return a default value.
     *
     * @param name the name of the variable
     * @return the value of the variable
     * @throws ResolutionException if the variable is unknown
     */
    fun value(name: String): String

    /**
     * Exception thrown by [VariableResolver] implementations when a variable name cannot be resolved.
     *
     * @param message the exception message
     * @see VariableResolver.value
     */
    class ResolutionException(message: String) : IllegalArgumentException(message) {

        override fun fillInStackTrace(): Throwable = this // no need to fill in the stack trace

    }

    companion object {

        /**
         * Creates a [VariableResolver] that resolves variables by their index in the given [list][args].
         *
         * The variable passed to [VariableResolver.value] is parsed as an integer, and the value at the corresponding index
         * in the [list][args] is returned. If name does not represent a valid integer, or if the index is out of
         * bounds, an [ResolutionException] is thrown.
         *
         * Example:
         * ```kt
         * val args = listOf("foo", "bar", "baz")
         * val resolver = VariableResolver.fromList(args)
         * val result = "Hello {0}, {2}, {1}".interpolate(resolver)
         * println(result) // prints "Hello foo, baz, bar"
         * ```
         *
         * @param args the list of values
         * @return a [VariableResolver] that resolves variables by their index in the given [list][args]
         */
        fun fromList(args: List<Any>): VariableResolver =
            VariableResolver { name ->
                val index = name.toIntOrNull()
                    ?: throw ResolutionException("index must be a parsable integer, but was'$name'")
                if (index !in args.indices) {
                    throw ResolutionException("index must be in between 0 and ${args.size}, but was $index")
                }
                args[index].toString()
            }

        /**
         * Creates a [VariableResolver] that resolves variables by their index in the given [vararg][args].
         *
         * The variable passed to [VariableResolver.value] is parsed as an integer, and the value at the corresponding index
         * in the [vararg][args] is returned. If name does not represent a valid integer, or if the index is out of
         * bounds, an [ResolutionException] is thrown.
         *
         * Example:
         * ```kt
         * val resolver = VariableResolver.fromVararg("foo", "bar", "baz")
         * val result = "Hello {0}, {2}, {1}".interpolate(resolver)
         * println(result) // prints "Hello foo, baz, bar"
         * ```
         *
         * @param args the vararg of values
         * @return a [VariableResolver] that resolves variables by their index in the given [vararg][args]
         */
        fun fromVararg(vararg args: Any): VariableResolver = fromList(args.asList())

        /**
         * Creates a [VariableResolver] that resolves variables by their name in the given [map][args].
         *
         * The name of the variable passed to [VariableResolver.value] is used as a key in the [map][args], and the value
         * associated with that key is returned. If a variable is unknown, the [fallback] value is returned. If the
         * [fallback] value is `null`, an [ResolutionException] is thrown.
         *
         * Example:
         * ```kt
         * val resolver = VariableResolver.fromMap(mapOf("name" to "John", "age" to 42), fallback = "unknown")
         * val result = "Hello {name}, you are {age} years old, and you live in {city}".interpolate(resolver)
         * println(result) // prints "Hello John, you are 42 years old, and you live in unknown"
         * ```
         *
         * @param args the map of values
         * @param fallback the fallback value (defaults to `null`)
         * @return a [VariableResolver] that resolves variables by their name in the given [map][args]
         */
        fun fromMap(args: Map<String, Any>, fallback: String? = null): VariableResolver =
            VariableResolver { name ->
                args[name]?.toString() ?: fallback ?: throw ResolutionException("unknown variable name '$name'")
            }

        /**
         * Creates a [VariableResolver] that resolves variables by their name in the [Pair] array [args]. The array of pairs
         * is converted to a [map][Map].
         *
         * The name of the variable passed to [VariableResolver.value] is used as a key in the map created from the
         * [array][args] and the value associated with that key is returned. If a variable is unknown, the [fallback]
         * value is returned. If the [fallback] value is `null`, an [ResolutionException] is thrown.
         *
         * Example:
         * ```kt
         * val resolver = VariableResolver.fromPairs("name" to "John", "age" to 42, fallback = "unknown")
         * val result = "Hello {name}, you are {age} years old, and you live in {city}".interpolate(resolver)
         * println(result) // prints "Hello John, you are 42 years old, and you live in unknown"
         * ```
         *
         * @param args the array of pairs
         * @param fallback the fallback value (defaults to `null`)
         * @return a [VariableResolver] that resolves variables by their name in the [Pair] array [args]
         */
        fun fromPairs(vararg args: Pair<String, Any>, fallback: String? = null): VariableResolver =
            fromMap(args.toMap(), fallback)

    }

}


private enum class State {
    DEFAULT,
    BACKSLASH,
    IN_CURLY,
}
