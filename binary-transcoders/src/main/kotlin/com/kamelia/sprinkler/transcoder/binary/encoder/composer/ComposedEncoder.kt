@file:JvmName("ComposedEncoderFactories")

package com.kamelia.sprinkler.transcoder.binary.encoder.composer

import com.kamelia.sprinkler.transcoder.binary.encoder.UTF8StringEncoder
import com.kamelia.sprinkler.transcoder.binary.encoder.core.Encoder
import com.kamelia.sprinkler.transcoder.binary.encoder.core.EncoderOutput
import java.nio.ByteOrder

/**
 * Creates a new encoder of type [T] using the given function [block]. The [block] parameter is a lambda accepting an
 * object of type [T] and an [EncodingScope]. The given [EncodingScope] will have the following properties:
 *
 * - All primitive objects will be encoded with the default encoders present in the `BaseEncoders` file, and with the
 *  given [endianness].
 * - For [String] objects encoding, the [stringEncoder] parameter will be used.
 * - Each created encoder will be cached and reused for the same type (except for encoders composed with the
 * [self][EncodingScope.self] property).
 * - Nullable objects encoding will be encoded assuming that the object is prefixed with a [Boolean] (represented as a
 * single byte) that indicates if the object is present or not (`true` if present, `false` if not).
 * - All collection encoding will be encoded assuming that the collection is prefixed with its size represented by an
 * [Int].
 * - All nullable collections encoding will be encoded assuming that the collection is prefixed with a [Boolean] that
 * indicates if the collection is present or not (`true` if present, `false` if not). Then, if the collection is
 * present, the size of the collection will be encoded as an [Int]. Finally, if the collection is present and its size
 * is greater than 0, the elements of the collection will be encoded.
 *
 * **NOTE**: The [EncodingScope] used in the lambda [block] is not designed to be used outside the lambda. Any use of
 * the scope outside the lambda may lead to unexpected results and can change the behaviour of the scope encoding
 * process. The scope is designed to be used only inside the lambda. In the same way, the encoder returned by the
 * [self][EncodingScope.self] property should also only be used inside the lambda.
 *
 * **NOTE**: The returned encoder is not thread-safe. Unexpected behaviours may occur if the internal cache of the
 * decoder is modified concurrently.
 *
 * @param endianness the endianness of the encoder (defaults to [ByteOrder.BIG_ENDIAN])
 * @param stringEncoder the encoder to use for [String] objects (defaults to the default [UTF8StringEncoder])
 * @param block the block that will encode the object
 * @return the created encoder of type [T]
 * @see EncodingScope
 */
@JvmOverloads
fun <T> composedEncoder(
    endianness: ByteOrder = ByteOrder.BIG_ENDIAN,
    stringEncoder: Encoder<String> = UTF8StringEncoder(),
    block: EncodingScope<T>.(T) -> Unit,
): Encoder<T> {
    val encodersCache = HashMap<Class<*>, Encoder<*>>().apply {
        put(String::class.java, stringEncoder)
    }

    return Encoder { obj, output ->
        var encoder: Encoder<T>? = null
        var top = true

        val recursionQueue = ArrayDeque<() -> Unit>()
        val globalStack = ArrayList<() -> Unit>()

        encoder = Encoder self@{ t: T, o: EncoderOutput ->
            // base case
            val scope = EncodingScopeImpl(o, globalStack, recursionQueue, encodersCache, endianness, encoder!!)
            scope.block(t)

            if (!top) return@self // true only for the first call in the recursion stack
            top = false

            while (recursionQueue.isNotEmpty()) { // while there are encodings to be done
                // while there are recursive encodings (we must loop because new recursive encodings may be added when a
                // lambda is executed).
                while (recursionQueue.isNotEmpty()) {
                    recursionQueue.removeFirst()()
                }

                // while there are non-recursive encodings (we must loop because new encoding may be added when a lambda is
                // executed). We must also check that the recursion queue is empty because we want to execute all recursive
                // as soon as a new one is added.
                while (recursionQueue.isEmpty() && globalStack.isNotEmpty()) {
                    globalStack.removeLast()()
                }
            }
        }

        encoder.encode(obj, output)
    }
}
