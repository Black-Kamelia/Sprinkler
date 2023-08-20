package com.kamelia.sprinkler.transcoder.binary.decoder.core

import com.kamelia.sprinkler.bridge.accumulate
import com.kamelia.sprinkler.bridge.finish
import com.kamelia.sprinkler.bridge.supply
import java.util.stream.Collector

/**
 * A [Decoder] that decodes an object composed of a constant number of elements, by performing a
 * [constant n-ary (aka polyadic)](https://en.wikipedia.org/wiki/Arity#n-ary) reduction on a sequence of these elements.
 * It accumulates them until they are all decoded, and finally creates the resulting object and returns it.
 *
 * This decoder works by using a [Collector] for the accumulation and creation of the resulting object. It effectively
 * does a reduction of the elements decoded by the [elementDecoder] into the resulting object.
 *
 * Here is an example of how to use this class:
 *
 * ```kotlin
 * fun decode(byteDecoder: Decoder<Byte>) {
 *     val decoder = ConstantArityReductionDecoder(
 *         Collectors.toList(),
 *         byteDecoder,
 *         3
 *     )
 *     val input = byteArrayOf(1, 2, 3)
 *     val result = decoder.decode(input).get()
 *     println(result) // prints [1, 2, 3]
 * }
 * ```
 *
 * @param T the type of the elements to decode
 * @param C the type of the object used to accumulate the elements
 * @param R the type of the resulting object
 * @param collector a [Collector] used for the accumulation and creation of the resulting object
 * @param elementDecoder a [Decoder] used to decode the elements
 * @param arity the number of elements to decode
 * @constructor Creates a new [ConstantArityReductionDecoder].
 * @see Collector
 * @see PrefixedArityReductionDecoder
 * @see MarkerEndedReductionDecoder
 */
class ConstantArityReductionDecoder<T, C, R>(
    private val collector: Collector<T, C, R>,
    private val elementDecoder: Decoder<T>,
    private val arity: Int,
) : Decoder<R> {

    private var collection: C? = null
    private var index = 0

    init {
        require(arity >= 0) { "Arity must be non-negative (was $arity)" }
    }

    override fun decode(input: DecoderInput): Decoder.State<R> {
        val collection = collection ?: collector.supply().also { collection = it }

        while (index < arity) {
            when (val elementState = elementDecoder.decode(input)) {
                is Decoder.State.Done -> {
                    collector.accumulate(collection, elementState.value)
                    index++
                }

                else -> return elementState.mapEmptyState()
            }
        }

        selfReset()
        return Decoder.State.Done(collector.finish(collection))
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
