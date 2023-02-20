package com.kamelia.sprinkler.binary.decoder.core

import com.kamelia.sprinkler.util.accumulate
import com.kamelia.sprinkler.util.finish
import com.kamelia.sprinkler.util.supply
import java.util.stream.Collector

/**
 * A [Decoder] that decodes an object composed of a variable number of elements, by performing a
 * [variable polyadic (aka variadic)](https://en.wikipedia.org/wiki/Arity#Varying_arity) reduction on a sequence of
 * these elements.
 *
 * The element's byte sequence is prefixed by the number of elements to decode. It accumulates them until they are all
 * decoded, and finally creates the resulting object and returns it.
 *
 * This decoder works by using a [Collector] for the accumulation and creation of the resulting object. It effectively
 * does a reduction of the elements decoded by the [elementDecoder] into the resulting object.
 *
 * Here is an example of how to use this class:
 *
 * ```kotlin
 * fun decode(byteDecoder: Decoder<Byte>) {
 *     val decoder = PrefixedArityReductionDecoder(
 *         Collectors.toList(),
 *         byteDecoder,
 *         byteDecoder
 *     )
 *     val input = byteArrayOf(2, 5, 3)
 *     val result = decoder.decode(input).get()
 *     println(result) // prints [5, 3]
 * }
 * ```
 *
 * @param T the type of the elements to decode
 * @param C the type of the object used to accumulate the elements
 * @param R the type of the resulting object
 * @param collector a [Collector] used for the accumulation and creation of the resulting object
 * @param elementDecoder a [Decoder] used to decode the elements
 * @param sizeDecoder the [Decoder] used to decode the number of elements of the sequence
 * @constructor Creates a new [PrefixedArityReductionDecoder].
 * @see Collector
 * @see ConstantArityReductionDecoder
 * @see MarkerEndedReductionDecoder
 */
class PrefixedArityReductionDecoder<T, C, R>(
    private val collector: Collector<T, C, R>,
    private val elementDecoder: Decoder<T>,
    private val sizeDecoder: Decoder<Number>,
) : Decoder<R> {

    private var collection: C? = null
    private var size = -1
    private var index = 0

    override fun decode(input: DecoderInputData): Decoder.State<R> {
        if (size == -1) {
            when (val sizeState = sizeDecoder.decode(input)) {
                is Decoder.State.Done -> {
                    val size = sizeState.value.toInt()
                    if (size < 0) {
                        reset()
                        return Decoder.State.Error(IllegalStateException("Size must be positive, but was $size"))
                    }
                    this.size = size
                }
                else -> return sizeState.mapEmptyState()
            }
            collection = collector.supply()
        }

        val collection = collection!!
        while (index < size) {
            when (val elementState = elementDecoder.decode(input)) {
                is Decoder.State.Done -> {
                    collector.accumulate(collection, elementState.value)
                    index++
                }
                else -> return elementState.mapEmptyState()
            }
        }

        size = -1
        index = 0
        this.collection = null
        return Decoder.State.Done(collector.finish(collection))
    }

    override fun reset() {
        collection = null
        size = -1
        index = 0
        elementDecoder.reset()
        sizeDecoder.reset()
    }

}
