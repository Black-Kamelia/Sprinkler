package com.kamelia.sprinkler.binary.decoder.composer

import com.kamelia.sprinkler.binary.decoder.Decoder
import com.kamelia.sprinkler.binary.decoder.DecoderCollector
import com.kamelia.sprinkler.binary.decoder.DecoderDataInput
import com.zwendo.restrikt.annotation.PackagePrivate

@PackagePrivate
internal abstract class RepeatDecoder<C, T, R>(
    private val decoder: Decoder<T>,
    private val collector: DecoderCollector<C, T, R>,
) {

    protected var size = -1
        private set
    private var index = 0
    private var container: C? = null

    protected fun accumulate(input: DecoderDataInput): Decoder.State<R> {
        while (index < size) {
            when (val state = decoder.decode(input)) {
                is Decoder.State.Done -> collector.accumulator(container!!, state.value, index++)
                else -> return state.mapEmptyState()
            }
        }

        size = -1
        return Decoder.State.Done(collector.finisher(container!!))
    }

    protected fun init(size: Int) {
        this.size = size
        this.index = 0
        this.container = collector.supplier(size)
    }

    protected fun clear() {
        container = null
        index = 0
        size = -1
    }

}
