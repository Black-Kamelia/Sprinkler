@file:JvmName("Interpolations")

package com.kamelia.sprinkler.util


fun String.interpolate(resolver: NameResolver): String {
    val builder = StringBuilder()
    var state = State.DEFAULT
    var keyBuilder = StringBuilder()

    forEach { char ->
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
                val actualChar = ESCAPED_CHARS[char] ?: char
                builder.append(actualChar)
            }

            State.IN_CURLY -> {
                when (char) {
                    '\\' -> error("Unexpected '\\' in interpolated value")
                    '{' -> error("Unexpected '{' in interpolated value")
                    '}' -> {
                        val key = keyBuilder.toString()
                        val value = resolver.value(key)
                        builder.append(value)
                        keyBuilder = StringBuilder()
                        state = State.DEFAULT
                    }

                    else -> keyBuilder.append(char)
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

fun interface NameResolver {

    fun value(name: String): String

    companion object {

        fun create(vararg args: Any?): NameResolver = NameResolver { name ->
            val index = name.toIntOrNull() ?: error("Invalid index '$name'")
            if (index !in args.indices) {
                throw IllegalArgumentException("index must be in between 0 and ${args.size}, but was $index")
            }
            args[index].toString()
        }

        fun create(args: Map<String, Any>, fallback: String? = null): NameResolver =
            NameResolver {
                args[it]?.toString() ?: fallback ?: throw IllegalArgumentException("Unknown variable '$it'")
            }

        fun create(vararg args: Pair<String, Any>, fallback: String? = null): NameResolver =
            create(args.toMap(), fallback = fallback)

    }

}


private val ESCAPED_CHARS = mapOf(
    'b' to '\b',
    't' to '\t',
    'n' to '\n',
    'r' to '\r',
    'f' to '\u000C',
    '\'' to '\'',
    '"' to '"',
    '\\' to '\\',
)

private enum class State {
    DEFAULT,
    BACKSLASH,
    IN_CURLY,
}
