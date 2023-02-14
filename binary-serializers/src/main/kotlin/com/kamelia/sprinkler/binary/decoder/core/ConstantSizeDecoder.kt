package com.kamelia.sprinkler.binary.decoder.core

class ConstantSizeDecoder<E>(
    private val byteSize: Int,
    private val extractor: ByteArray.() -> E,
) : Decoder<E> {

    init {
        require(byteSize > 0) { "Number of bytes must positive (was $byteSize)" }
    }

    private val array: ByteArray = ByteArray(byteSize)
    private var index = 0

    override fun decode(input: DecoderDataInput): Decoder.State<E> {
        index += input.read(array, index)
        return if (index == byteSize) {
            Decoder.State.Done(array.extractor()).also { index = 0 }
        } else {
            Decoder.State.Processing(
                "(${ConstantSizeDecoder::class.simpleName}) $index / $byteSize bytes read."
            )
        }
    }

    override fun reset() {
        index = 0
    }

}
