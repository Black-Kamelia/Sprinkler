package com.kamelia.sprinkler.binary.decoder.core

/**
 * A [Decoder] that decode an object with a variable size. The number of bytes to read are prefixed to the bytes of the
 * object. This size is decoded by a [sizeDecoder] and then the n bytes are accumulated in a [ByteArray]. Once the
 * [ByteArray] is full, the [converter] function is used to convert the [ByteArray] to the final object.
 *
 * @param E the type of the decoded object
 * @param sizeDecoder a [Decoder] to decode the number of bytes to read
 * @param converter a function to convert the [ByteArray] to the decoded object
 * @constructor Creates a new [VariableSizePrefixedSizeDecoder].
 */
class VariableSizePrefixedSizeDecoder<E>(
    private val sizeDecoder: Decoder<Number>,
    private val converter: ByteArray.(Int) -> E,
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

        val array = array
        if (array == null || bytesToRead > (array.size)) { // allocate new array if needed
            this.array = ByteArray(bytesToRead)
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
            Decoder.State.Done(array.converter(finalSize))
        } else {
            Decoder.State.Processing(
                "(${VariableSizePrefixedSizeDecoder::class.simpleName}) $index / $bytesToRead bytes read."
            )
        }
    }

    override fun reset() {
        array = null
        index = 0
        bytesToRead = -1
    }

}

