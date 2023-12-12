@file:JvmName("Interpolations")

package com.kamelia.sprinkler.util

/**
 * Interpolates variables in this string using the given [resolver]. This function replaces all occurrences of
 * `{variable}` with the value of the variable returned by [VariableResolver.value].
 *
 * Strings that are valid for interpolation are defined as follows:
 * - String may contain **zero**, **one** or **more** variables delimited by a start character and an end character
 * defined by the [delimiters] parameter ;
 * - Variable names can contain any character except the end character, which is reserved for closing the variable ;
 * - **Escaping** [delimiters] characters is possible using a **backslash** (`\`), and **only the opening character**
 * can be escaped ;
 * - Any **non-escaped start character** is considered as the start of a variable and **must be closed** before the end
 * of the string ;
 * - If a variable **name is empty**, the variable is resolved by using the **count of variables** encountered so far.
 * The first variable has index 0, the second index 1, and so on. Note that even variables with a **non-empty name are**
 * **also counted** (e.g. with '{' and '}' as start and end characters, `"Hello {name}, I'm {}"`, the `{}` has the index
 * 1 because the variable `name` is counted).
 *
 * Any string that does not conform to these rules is considered invalid, a call to this function with an invalid string
 * will result in an [IllegalArgumentException] being thrown.
 *
 * **NOTE**: Depending on the implementation of [VariableResolver] used, this function may also throw an
 * [IllegalArgumentException] if a variable name is invalid for the given [VariableResolver].
 *
 * This function can be used as follows:
 *
 * ```kt
 * val resolver: VariableResolver = ...
 * val result = "Hello {user_name}, you are {user-age} years old".interpolate(resolver)
 * ```
 *
 * @param resolver the [VariableResolver] to use for resolving variable names
 * @param delimiters the delimitation of the variable (defaults to [VariableDelimiter.DEFAULT])
 * @return the interpolated string
 * @throws IllegalArgumentException if the string is invalid, or if a variable name is invalid for the given
 * [VariableResolver]
 * @see [VariableResolver]
 */
fun String.interpolate(
    resolver: VariableResolver,
    delimiters: VariableDelimiter = VariableDelimiter.DEFAULT,
): String {
    val builder = StringBuilder() // final string builder
    var state = State.DEFAULT // current state of the parser
    val keyBuilder = StringBuilder() // builder used to build the key of the variable
    var variableCount = 0 // number of variables encountered to resolve empty names

    forEach { char ->
        when (state) {
            State.DEFAULT -> {
                when (char) {
                    delimiters.variableStart -> state = State.IN_VARIABLE // start of a variable
                    '\\' -> state = State.BACKSLASH // start of an escape sequence
                    else -> builder.append(char) // any other character is appended to the final string
                }
            }
            State.BACKSLASH -> { // in this state the variableStart is escaped
                builder.append(char)
                state = State.DEFAULT
            }
            State.IN_VARIABLE -> {
                if (delimiters.variableEnd != char) { // append the current character to the key
                    keyBuilder.append(char)
                    return@forEach
                }

                // end of the variable
                val key = if (keyBuilder.isEmpty()) { // if the key is empty, use the variable count
                    variableCount.toString()
                } else {
                    keyBuilder.toString()
                }

                val value = try { // try to resolve the variable
                    resolver.value(key, delimiters)
                } catch (e: VariableResolver.ResolutionException) {
                    illegalArgument("Error while resolving variable '$key': ${e.message!!}")
                }

                builder.append(value)
                // clear instead of creating a new one, because the complexity is O(1) and not O(n)
                // it simply sets the length of the builder to 0
                keyBuilder.clear()
                state = State.DEFAULT
                variableCount++
            }
        }
    }
    require(State.IN_VARIABLE !== state) { // ensure that the string does not end in the middle of a variable
        "Unexpected end of string in interpolated value"
    }

    return builder.toString()
}

/**
 * Interpolates variables in this string using the given vararg [args].
 *
 * Variable are resolved by their index in the given [args]. The variable passed to is parsed as an integer, and the
 * value at the corresponding index in the [array][args] is returned.
 *
 * Strings must follow the same rules as defined in [String.interpolate]. In addition, the following rules apply to
 * variable names:
 * - Name must be a **valid integer** ;
 * - The **index** specified in the name must be in between **0** and the **number of arguments in [args]** - 1.
 *
 * **NOTE**: As empty variable names are resolved by using the count of variables encountered so far, they are totally
 * valid for this function, as long as the resulting index is in bounds.
 *
 * Any string that does not conform to these rules is considered invalid, a call to this function with an invalid string
 * will result in an [IllegalArgumentException] being thrown.
 *
 * This function can be used as follows:
 *
 * ```kt
 * val result = "Hello {0}, you are {1} years old".interpolateIndexed("John", 42)
 * ```
 *
 * @param args the vararg of values
 * @return the interpolated string
 * @throws IllegalArgumentException if the string is invalid, or if a variable name does not conform to the rules
 * defined above
 * @see [VariableResolver.fromVararg]
 */
fun String.interpolateIndexed(vararg args: Any): String = interpolate(VariableResolver.fromVararg(*args))

/**
 * Interpolates variables in this string using the given list [args].
 *
 * Variable are resolved by their index in the given [args]. The variable passed to is parsed as an integer, and the
 * value at the corresponding index in the [list][args] is returned.
 *
 * Strings must follow the same rules as defined in [String.interpolate]. In addition, the following rules apply to
 * variable names:
 * - Name must be a **valid integer** ;
 * - The **index** specified in the name must be in between **0** and the **number of arguments in [args]** - 1.
 *
 * **NOTE**: As empty variable names are resolved by using the count of variables encountered so far, they are totally
 * valid for this function, as long as the resulting index is in bounds.
 *
 * Any string that does not conform to these rules is considered invalid, a call to this function with an invalid string
 * will result in an [IllegalArgumentException] being thrown.
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
 * @param delimiters the delimitation of the variable (defaults to [VariableDelimiter.DEFAULT])
 * @return the interpolated string
 * @throws IllegalArgumentException if the string is invalid, or if a variable name does not conform to the rules
 * defined above
 * @see [VariableResolver.fromList]
 */
@JvmOverloads
fun String.interpolateIndexed(
    args: List<Any>,
    delimiters: VariableDelimiter = VariableDelimiter.DEFAULT,
): String =
    interpolate(VariableResolver.fromList(args), delimiters)

/**
 * Interpolates variables in this string using the given map of [args].
 *
 * Variable are resolved by their name in the given [map][args]. The name of the variable passed to is used as a key in
 * the [map][args], and the value associated with that key is returned. If a variable is unknown, the [fallback] value
 * is returned. If the [fallback] value is `null`, an [IllegalArgumentException] is thrown.
 *
 * Strings must follow the same rules as defined in [String.interpolate].
 *
 * It can be used as follows:
 *
 * ```kt
 * val result = "Hello {name}, you are {age} years old".interpolate(mapOf("name" to "John", "age" to 42))
 * ```
 *
 * @param args the map of values
 * @param fallback the fallback value (defaults to `null`)
 * @param delimiters the delimitation of the variable (defaults to [VariableDelimiter.DEFAULT])
 * @return the interpolated string
 * @throws IllegalArgumentException if the string is invalid, or if a variable name is unknown and the [fallback] value
 * is `null`
 * @see [VariableResolver.fromMap]
 */
@JvmOverloads
fun String.interpolate(
    args: Map<String, Any>,
    fallback: String? = null,
    delimiters: VariableDelimiter = VariableDelimiter.DEFAULT,
): String =
    interpolate(VariableResolver.fromMap(args, fallback), delimiters)

/**
 * Interpolates variables in this string using the given [Pair] array [args]. The array of pairs is converted to a
 * [map][Map].
 *
 * Variable are resolved by their name in the [Pair] array [args]. The name of the variable passed to is used as a key
 * in the map created from the [array][args] and the value associated with that key is returned. If a variable is
 * unknown, the [fallback] value is returned. If the [fallback] value is `null`, an [IllegalArgumentException] is
 * thrown.
 *
 * Strings must follow the same rules as defined in [String.interpolate].
 *
 * It can be used as follows:
 *
 * ```kt
 * val result = "Hello {name}, you are {age} years old".interpolate("name" to "John", "age" to 42)
 * ```
 *
 * @param args the array of pairs
 * @param fallback the fallback value (defaults to `null`)
 * @param delimiters the delimitation of the variable (defaults to [VariableDelimiter.DEFAULT])
 * @return the interpolated string
 * @throws IllegalArgumentException if the string is invalid, or if a variable name is unknown and the [fallback] value
 * is `null`
 * @see [VariableResolver.fromPairs]
 */
@JvmOverloads
fun String.interpolate(
    vararg args: Pair<String, Any>,
    fallback: String? = null,
    delimiters: VariableDelimiter = VariableDelimiter.DEFAULT,
): String =
    interpolate(VariableResolver.fromMap(args.toMap(), fallback), delimiters)

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
     * @param delimiter the delimitation of the variable, can be useful for some implementations
     * @return the value of the variable
     * @throws ResolutionException if the variable is unknown
     */
    fun value(name: String, delimiter: VariableDelimiter): String

    /**
     * Exception thrown by [VariableResolver] implementations when a variable name cannot be resolved.
     *
     * @param message the exception message
     * @see VariableResolver.value
     */
    class ResolutionException(message: String) : IllegalArgumentException(message)

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
        @JvmStatic
        fun fromList(args: List<Any>): VariableResolver =
            VariableResolver { name, _ ->
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
        @JvmStatic
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
        @JvmStatic
        @JvmOverloads
        fun fromMap(args: Map<String, Any>, fallback: String? = null): VariableResolver =
            VariableResolver { name, _ ->
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
        @JvmStatic
        @JvmOverloads
        fun fromPairs(vararg args: Pair<String, Any>, fallback: String? = null): VariableResolver =
            fromMap(args.toMap(), fallback)

    }

}

/**
 * Represents the delimitation of a variable in an interpolated string.
 *
 * @property variableStart The start character of a variable.
 * @property variableEnd The end character of a variable.
 * @constructor Creates a new [VariableDelimiter] with the given [variableStart] and [variableEnd].
 * @param variableStart the start character of a variable
 * @param variableEnd the end character of a variable
 * @throws IllegalArgumentException if [variableStart] and [variableEnd] are equal, or if [variableStart] or
 * [variableEnd] is equal to `'\'`
 * @see VariableResolver
 */
class VariableDelimiter(
    val variableStart: Char,
    val variableEnd: Char,
) {

    init {
        require(variableStart != variableEnd) { "variableStart and variableEnd must not be equal" }
        require(variableStart != '\\') { "variableStart must not be equal to '\\'" }
        require(variableEnd != '\\') { "variableEnd must not be equal to '\\'" }
    }

    companion object {

        /**
         * The default [VariableDelimiter] used by [String.interpolate]. It uses the characters `'{'` and `'}'` as
         * delimiters.
         */
        val DEFAULT = VariableDelimiter('{', '}')

    }

}

private enum class State {
    DEFAULT,
    BACKSLASH,
    IN_VARIABLE,
}
