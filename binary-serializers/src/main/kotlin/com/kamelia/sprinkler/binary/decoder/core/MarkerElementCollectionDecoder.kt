package com.kamelia.sprinkler.binary.decoder.core

class MarkerElementCollectionDecoder<C, T, R>(
    private val collector: DecoderCollector<C, T, R>,
    private val elementDecoder: Decoder<T>,
    private val keepLast: Boolean = false,
    private val predicate: (T) -> Boolean,
) : Decoder<R> {

    private var collection: C? = null
    private var index = 0

    override fun decode(input: DecoderDataInput): Decoder.State<R> {
        val collection = collection ?: collector.supplier().also { collection = it }

        while (true) {
            when (val elementState = elementDecoder.decode(input)) {
                is Decoder.State.Done -> {
                    val element = elementState.value
                    if (predicate(element)) {
                        if (keepLast) {
                            collector.accumulator(collection, element, index)
                            index++
                        }

                        index = 0
                        this.collection = null
                        return Decoder.State.Done(collector.finisher(collection))
                    } else {
                        collector.accumulator(collection, element, index)
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
