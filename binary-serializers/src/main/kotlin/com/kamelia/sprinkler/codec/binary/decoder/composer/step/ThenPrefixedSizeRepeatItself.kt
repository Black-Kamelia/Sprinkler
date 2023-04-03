package com.kamelia.sprinkler.codec.binary.decoder.composer.step

import com.kamelia.sprinkler.codec.binary.decoder.composer.ElementsAccumulator
import com.kamelia.sprinkler.codec.binary.decoder.core.Decoder
import com.kamelia.sprinkler.codec.binary.decoder.mapState
import com.kamelia.sprinkler.util.finish
import com.kamelia.sprinkler.util.supply
import com.zwendo.restrikt.annotation.PackagePrivate
import java.util.stream.Collector

@PackagePrivate
internal class ThenPrefixedSizeRepeatItself<T, C, R>(
    private val collector: Collector<T, C, R>,
    sizeDecoder: Decoder<Number>,
) : CompositionStep {

    private var collection: C? = null
    private var size = -1
    private var index = 0

    override val storeResult: Boolean
        get() = collection == null // true only if an error occurred

    private val proxy = sizeDecoder.mapState {
        val size = it.toInt()
        if (size < 0) {
            Decoder.State.Error("Self-repeating number of elements must be positive but was $size")
        } else {
            collection = collector.supply()
            Decoder.State.Done(it)
        }
    }

    override fun decoder(accumulator: ElementsAccumulator): Decoder<*> = proxy

    override fun onArrive(accumulator: ElementsAccumulator, currentIndex: Int): Int = if (index < size) {
        accumulator.addStep()
        currentIndex
    } else {
        accumulator.add(collector.finish(collection!!))
        currentIndex + 1
    }

}
