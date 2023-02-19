package com.kamelia.sprinkler.binary.decoder.core

import com.kamelia.sprinkler.util.accumulate
import com.kamelia.sprinkler.util.finish
import com.kamelia.sprinkler.util.supply
import java.util.stream.Collector

class PrefixedSizeCollectionDecoder<T, C, R>(
    private val collector: Collector<T, C, R>,
    private val elementDecoder: Decoder<T>,
    private val sizeDecoder: Decoder<Number>,
) : Decoder<R> {

    private var collection: C? = null
    private var size = -1
    private var index = 0

    override fun decode(input: DecoderDataInput): Decoder.State<R> {
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
