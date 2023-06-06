@file:JvmName("ComposedDecoderFactory")

package com.kamelia.sprinkler.codec.binary.decoder.composer

import com.kamelia.sprinkler.codec.binary.decoder.UTF8StringDecoder
import com.kamelia.sprinkler.codec.binary.decoder.core.Decoder
import com.kamelia.sprinkler.codec.binary.decoder.core.DecoderInput
import java.nio.ByteOrder

/**
 * Creates a new encoder of type [T] using the given lambda [block]. The [block] parameter is a lambda accepting an
 * object of type [T] and a [DecodingScope]. The given [DecodingScope] will have the following properties:
 *
 * - All primitive objects will be decoded with the default decoders present in the `BaseDecoders` file, and with the
 * given [endianness].
 * - For [String] objects decoding, the [stringDecoder] parameter will be used.
 * - Each created decoder will be cached and reused for the same type (except for decoders composed with the
 * [self][DecodingScope.self] property).
 *
 * **NOTE**: The [DecodingScope] used in the lambda [block] is not designed to be used outside the lambda. Any use of
 * the scope outside the lambda may lead to unexpected results and can change the behaviour of the scope decoding
 * process.
 *
 * @param endianness the endianness of the decoder (defaults to [ByteOrder.BIG_ENDIAN])
 * @param stringDecoder the decoder to use for [String] objects (defaults to the default [UTF8StringDecoder])
 * @param block the block that will decode the object
 * @return the created decoder of type [T]
 * @see DecodingScope
 */
@JvmOverloads
fun <T> composedDecoder(
    endianness: ByteOrder = ByteOrder.BIG_ENDIAN,
    stringDecoder: Decoder<String> = UTF8StringDecoder(),
    block: DecodingScope<T>.() -> T,
): Decoder<T> {
    val cache = HashMap<Class<*>, Decoder<*>>().apply {
        put(String::class.java, stringDecoder)
    }
    return ComposedDecoderImpl(endianness, cache, block)
}

private class ComposedDecoderImpl<E>(
    endianness: ByteOrder,
    private val cache: HashMap<Class<*>, Decoder<*>>,
    private val block: DecodingScope<E>.() -> E,
) : Decoder<E> {

    private var elements = ElementsAccumulator()
    private val scope = DecodingScopeImpl<E>(elements, cache, endianness)

    override fun decode(input: DecoderInput): Decoder.State<E> {
        scope.input = input
        while (true) {
            scope.currentIndex = 0
            try {
                val result = scope.block()
                if (elements.isLastLayer) { // there is no recursion layer, we are done
                    elements = ElementsAccumulator()
                    return Decoder.State.Done(result)
                }

                elements.popRecursion() // else, we pop the recursion layer
                elements.addToRecursion(result) // and add the result to the previous layer
            } catch (_: ProcessingMarker) { // bytes are missing
                return Decoder.State.Processing
            } catch (_: RecursionMarker) { // recursion
                elements.recurse()
            } catch (e: Exception) { // an error occurred
                return Decoder.State.Error(e)
            }
        }
    }

    override fun reset() {
        elements = ElementsAccumulator()
        cache.values.forEach(Decoder<*>::reset)
    }

}
