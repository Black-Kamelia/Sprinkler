package com.kamelia.sprinkler.binary.decoder.core

class VariableSizePrefixedSizeDecoder<E>(
    private val sizeDecoder: Decoder<Number>,
    private val extractor: ByteArray.(Int) -> E,
) : Decoder<E> {

    private var array: ByteArray? = null
    private var index = 0
    private var bytesToRead = -1

    override fun decode(input: DecoderDataInput): Decoder.State<E> {
        if (bytesToRead == -1) {
            val state = decodeSize(input)
            if (state != null) {
                return state
            }
        }

        return decodeContent(input)
    }

    private fun decodeSize(input: DecoderDataInput): Decoder.State<E>? {
        when (val sizeState = sizeDecoder.decode(input)) {
            is Decoder.State.Done -> {
                val size = sizeState.value.toInt()
                if (size < 0) {
                    return Decoder.State.Error(IllegalStateException("Size must be positive, but was $size"))
                }
                bytesToRead = size
            }
            else -> return sizeState.mapEmptyState()
        }

        if (bytesToRead == 0) { // short circuit for empty array
            bytesToRead = -1
            return Decoder.State.Done(ByteArray(0).extractor(0))
        }

        if (bytesToRead > (array?.size ?: 0)) { // allocate new array if needed
            array = ByteArray(bytesToRead)
        }

        return null // continue decoding
    }

    private fun decodeContent(input: DecoderDataInput): Decoder.State<E> {
        val array = array!!
        if (index < bytesToRead) {
            index += input.read(array, index, bytesToRead - index)
        }

        return if (index == bytesToRead) {
            val finalSize = bytesToRead
            index = 0
            bytesToRead = -1
            Decoder.State.Done(array.extractor(finalSize))
        } else {
            Decoder.State.Processing(
                "(${VariableSizePrefixedSizeDecoder::class.simpleName}) $index / $bytesToRead bytes read."
            )
        }
    }

    override fun reset() {
        array = null // free memory
        index = 0
        bytesToRead = -1
    }

}

