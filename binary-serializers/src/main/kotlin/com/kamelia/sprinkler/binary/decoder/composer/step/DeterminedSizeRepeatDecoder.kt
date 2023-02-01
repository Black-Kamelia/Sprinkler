package com.kamelia.sprinkler.binary.decoder.composer.step

import com.kamelia.sprinkler.binary.decoder.Decoder
import com.kamelia.sprinkler.binary.decoder.DecoderCollector
import com.kamelia.sprinkler.binary.decoder.DecoderDataInput
import com.zwendo.restrikt.annotation.PackagePrivate

/**
 * Repeat decoder where the size is known in advance:
 * - the number of elements is fixed
 * - the size of the collection is prepended to the elements
 */
@PackagePrivate
internal abstract class DeterminedSizeRepeatDecoder<C, E, R>(
    private val collector: DecoderCollector<C, E, R>,
) : RepeatDecoder<E, R> {

    var times: Int = -1
        protected set

    private var size: Int = 0

    private var collection: C? = null

    final override fun addElement(element: E) {
        val collection = collection ?: collector.supplier(times).also { this.collection = it }
        collector.accumulator(collection, element, size)
        size++
    }

    override val isFull: Boolean
        get() = size == times

    final override fun decode(input: DecoderDataInput): Decoder.State<R> = if (collection == null) { // first encounter
        // not stored because the step call to store value
        Decoder.State.Done { throw AssertionError("Dummy return should never be read.") }
    } else { // second and last encounter
        Decoder.State.Done(collector.finisher(collection!!))
    }

    override fun reset() {
        collection = null
        size = 0
    }

}
