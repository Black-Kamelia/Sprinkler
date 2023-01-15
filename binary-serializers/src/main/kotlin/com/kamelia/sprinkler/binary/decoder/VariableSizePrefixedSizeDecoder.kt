package com.kamelia.sprinkler.binary.decoder

class VariableSizePrefixedSizeDecoder<E> @JvmOverloads constructor(
    private val sizeDecoder: Decoder<Int> = IntDecoder(),
    private val endianness: ByteEndianness = ByteEndianness.BIG_ENDIAN,
    private val extractor: ByteArray.(ByteEndianness, Int) -> E,
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
                val size = sizeState.value
                if (size < 0) {
                    return Decoder.State.Error(IllegalStateException("Size must be positive, but was $size"))
                }
                bytesToRead = size
            }
            else -> @Suppress("UNCHECKED_CAST") return sizeState as Decoder.State<E>
        }

        if (bytesToRead == 0) { // short circuit for empty array
            bytesToRead = -1
            return Decoder.State.Done(ByteArray(0).extractor(endianness, 0))
        }

        if (bytesToRead > (array?.size ?: 0)) { // allocate new array if needed
            array = ByteArray(bytesToRead)
        }

        return null // continue decoding
    }

    private fun decodeContent(input: DecoderDataInput): Decoder.State<E> {
        val array = array!!
        if (index < array.size) {
            index += input.read(array, index, bytesToRead - index)
        }

        return if (index == array.size) {
            val finalSize = bytesToRead
            index = 0
            bytesToRead = -1
            Decoder.State.Done(array.extractor(endianness, finalSize))
        } else {
            Decoder.State.Processing
        }
    }

    override fun reset() {
        array = null // free memory
        index = 0
        bytesToRead = -1
    }

}

