package com.kamelia.sprinkler.binary.decoder

import com.kamelia.sprinkler.binary.common.ByteEndianness

class ConstantSizeDecoder<E> @JvmOverloads constructor(
    private val byteSize: Int,
    private val endianness: ByteEndianness = ByteEndianness.BIG_ENDIAN,
    private val extractor: ByteArray.(ByteEndianness) -> E,
) : Decoder<E> {

    init {
        require(byteSize > 0) { "Number of bytes must positive, number of bytes: $byteSize" }
    }

    private val array: ByteArray = ByteArray(byteSize)
    private var index = 0

    override fun decode(input: DecoderDataInput): Decoder.State<E> {
        index += input.read(array, index)
        return if (index == byteSize) {
            Decoder.State.Done(array.extractor(endianness)).also { index = 0 }
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
