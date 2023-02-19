package com.kamelia.sprinkler.binary.decoder.core

import com.kamelia.sprinkler.util.accumulate
import com.kamelia.sprinkler.util.finish
import com.kamelia.sprinkler.util.supply
import java.util.stream.Collector

class ConstantSizeCollectionDecoder<T, C, R>(
    private val collector: Collector<T, C, R>,
    private val elementDecoder: Decoder<T>,
    private val size: Int,
) : Decoder<R> {

    private var collection: C? = null
    private var index = 0

    init {
        require(size >= 0) { "Size must be non-negative (was $size)" }
    }

    override fun decode(input: DecoderDataInput): Decoder.State<R> {
        val collection = collection ?: collector.supply().also { collection = it }

        while (index < size) {
            when (val elementState = elementDecoder.decode(input)) {
                is Decoder.State.Done -> {
                    collector.accumulate(collection, elementState.value)
                    index++
                }
                else -> return elementState.mapEmptyState()
            }
        }

        this.collection = null
        index = 0
        return Decoder.State.Done(collector.finish(collection))
    }

    override fun reset() {
        collection = null
        index = 0
        elementDecoder.reset()
    }

}
