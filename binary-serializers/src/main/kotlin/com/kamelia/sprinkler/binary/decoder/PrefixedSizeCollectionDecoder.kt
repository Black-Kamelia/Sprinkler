package com.kamelia.sprinkler.binary.decoder

class PrefixedSizeCollectionDecoder<C, T, R> @JvmOverloads constructor(
    private val collector: DecoderCollector<C, T, R>,
    private val elementDecoder: Decoder<T>,
    private val sizeDecoder: Decoder<Number> = IntDecoder(),
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

            if (size == 0) { // short circuit for empty collection
                size = -1
                index = 0
                return Decoder.State.Done(collector.finisher(collector.supplier(0)))
            }
            collection = collector.supplier(size)
        }

        val collection = collection!!
        while (index < size) {
            when (val elementState = elementDecoder.decode(input)) {
                is Decoder.State.Done -> {
                    collector.accumulator(collection, elementState.value, index)
                    index++
                }
                else -> return elementState.mapEmptyState()
            }
        }

        size = -1
        index = 0
        this.collection = null
        return Decoder.State.Done(collector.finisher(collection))
    }

    override fun reset() {
        collection = null
        size = -1
        index = 0
        elementDecoder.reset()
        sizeDecoder.reset()
    }

}
