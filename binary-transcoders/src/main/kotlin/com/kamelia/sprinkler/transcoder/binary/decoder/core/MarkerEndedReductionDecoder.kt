package com.kamelia.sprinkler.transcoder.binary.decoder.core

import com.kamelia.sprinkler.util.accumulate
import com.kamelia.sprinkler.util.finish
import com.kamelia.sprinkler.util.supply
import java.util.stream.Collector


/**
 * A [Decoder] that decodes an object composed of a variable number of elements, by performing a
 * [variable polyadic (aka variadic)](https://en.wikipedia.org/wiki/Arity#Varying_arity) reduction on a sequence of
 * these elements.
 *
 * &nbsp;
 *
 * The number of elements to decode is undefined, and is determined by a [predicate][shouldStop] that tests each element
 * decoded by the [elementDecoder]. It accumulates them until the predicate returns `true`, and finally creates the
 * resulting object.
 *
 * &nbsp;
 *
 * This decoder works by using a [Collector] for the accumulation and creation of the resulting object. It effectively
 * does a reduction of the elements decoded by the [elementDecoder] into the resulting object.
 *
 * &nbsp;
 *
 * Here is an example of how to use this class:
 *
 * ```kotlin
 * fun decode(byteDecoder: Decoder<Byte>) {
 *     val decoder = MarkerEndedReductionDecoder(
 *         Collectors.toList(),
 *         byteDecoder,
 *     ) { it == 0 }
 *     val input = byteArrayOf(2, 5, 0)
 *     val result = decoder.decode(input).get()
 *     println(result) // prints [2, 3]
 * }
 * ```
 *
 * @param T the type of the elements to decode
 * @param C the type of the object used to accumulate the elements
 * @param R the type of the resulting object
 * @param collector a [Collector] used for the accumulation and creation of the resulting object
 * @param elementDecoder a [Decoder] used to decode the elements
 * @param keepLast whether the last element decoded by the [elementDecoder] should be kept in the resulting object.
 * @param shouldStop a predicate that tests each element decoded by the [elementDecoder]. It returns `true` when the
 * decoding should stop, and `false` otherwise.
 * @constructor Creates a new [MarkerEndedReductionDecoder].
 * @see Collector
 * @see ConstantArityReductionDecoder
 * @see PrefixedArityReductionDecoder
 */
class MarkerEndedReductionDecoder<T, C, R> @JvmOverloads constructor(
    private val collector: Collector<T, C, R>,
    private val elementDecoder: Decoder<T>,
    private val keepLast: Boolean = false,
    private val shouldStop: (T) -> Boolean,
) : Decoder<R> {

    private var collection: C? = null
    private var index = 0

    override fun decode(input: DecoderInput): Decoder.State<R> {
        val collection = collection ?: collector.supply().also { collection = it }

        while (true) {
            when (val elementState = elementDecoder.decode(input)) {
                is Decoder.State.Done -> {
                    val element = elementState.value
                    if (shouldStop(element)) {
                        if (keepLast) {
                            collector.accumulate(collection, element)
                        }

                        selfReset()
                        return Decoder.State.Done(collector.finish(collection))
                    } else {
                        collector.accumulate(collection, element)
                        index++
                    }
                }
                else -> return elementState.mapEmptyState()
            }
        }
    }

    override fun reset() {
        selfReset()
        elementDecoder.reset()
    }

    private fun selfReset() {
        collection = null
        index = 0
    }

}
