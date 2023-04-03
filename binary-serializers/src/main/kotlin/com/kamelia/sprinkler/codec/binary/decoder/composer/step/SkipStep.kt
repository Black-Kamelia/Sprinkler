package com.kamelia.sprinkler.codec.binary.decoder.composer.step

import com.kamelia.sprinkler.codec.binary.decoder.composer.ElementsAccumulator
import com.kamelia.sprinkler.codec.binary.decoder.core.Decoder
import com.kamelia.sprinkler.codec.binary.decoder.core.DecoderInput
import com.zwendo.restrikt.annotation.PackagePrivate

@PackagePrivate
internal class SkipStep(size: Long) : CompositionStep {

    private val decoder = SkipDecoder(size)

    init {
        require(size >= 0) { "Size must be >= 0, but was $size" }
    }

    override fun decoder(accumulator: ElementsAccumulator): Decoder<*> = decoder

    override val storeResult: Boolean
        get() = false

    private class SkipDecoder(private val size: Long) : Decoder<Nothing> {

        private var left = size

        override fun decode(input: DecoderInput): Decoder.State<Nothing> {
            left -= input.skip(left)
            return if (left > 0) {
                Decoder.State.Processing()
            } else {
                Decoder.State.Done { throw AssertionError("Should never read") }
            }
        }

        override fun reset() {
            left = size
        }

    }

}
