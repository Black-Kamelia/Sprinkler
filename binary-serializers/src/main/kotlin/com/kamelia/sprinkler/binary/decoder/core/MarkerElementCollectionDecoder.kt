package com.kamelia.sprinkler.binary.decoder.core

import com.kamelia.sprinkler.util.accumulate
import com.kamelia.sprinkler.util.finish
import com.kamelia.sprinkler.util.supply
import java.util.stream.Collector

class MarkerElementCollectionDecoder<T, C, R> @JvmOverloads constructor(
    private val collector: Collector<T, C, R>,
    private val elementDecoder: Decoder<T>,
    private val keepLast: Boolean = false,
    private val predicate: (T) -> Boolean,
) : Decoder<R> {

    private var collection: C? = null
    private var index = 0

    override fun decode(input: DecoderDataInput): Decoder.State<R> {
        val collection = collection ?: collector.supply().also { collection = it }

        while (true) {
            when (val elementState = elementDecoder.decode(input)) {
                is Decoder.State.Done -> {
                    val element = elementState.value
                    if (predicate(element)) {
                        if (keepLast) {
                            collector.accumulate(collection, element)
                            index++
                        }

                        index = 0
                        this.collection = null
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
        collection = null
        index = 0
        elementDecoder.reset()
    }

}
