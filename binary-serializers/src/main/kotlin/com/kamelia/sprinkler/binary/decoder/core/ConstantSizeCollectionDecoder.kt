package com.kamelia.sprinkler.binary.decoder.core

class ConstantSizeCollectionDecoder<C, T, R>(
    private val collector: DecoderCollector<C, T, R>,
    private val elementDecoder: Decoder<T>,
    private val size: Int,
) : Decoder<R> {

    private var collection: C? = null
    private var index = 0

    override fun decode(input: DecoderDataInput): Decoder.State<R> {
        val collection = collection ?: collector.supplier(size).also { collection = it }

        while (index < size) {
            when (val elementState = elementDecoder.decode(input)) {
                is Decoder.State.Done -> {
                    collector.accumulator(collection, elementState.value, index)
                    index++
                }
                else -> return elementState.mapEmptyState()
            }
        }

        this.collection = null
        index = 0
        return Decoder.State.Done(collector.finisher(collection))
    }

    override fun reset() {
        collection = null
        index = 0
        elementDecoder.reset()
    }

}
