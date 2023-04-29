package com.kamelia.sprinkler.codec.binary.decoder.core

/**
 * A [Decoder] that decode an object with a variable size. The number of bytes to read are prefixed to the actual bytes
 * of the object. This size is decoded by a [sizeDecoder] and then the n bytes are accumulated internally. Once all
 * bytes have been collected, the [converter] function is used to convert these bytes to the decoded object.
 *
 * **NOTE**:
 * The created decoder will return an [error][Decoder.State.Error] if the size is negative.
 *
 * @param E the type of the decoded object
 * @param sizeDecoder a [Decoder] to decode the number of bytes to read
 * @param converter a function to convert the bytes (stored in a [ByteArray]) to the decoded object
 * @constructor Creates a new [PrefixedSizeItemDecoder].
 */
class PrefixedSizeItemDecoder<E>(
    private val sizeDecoder: Decoder<Number>,
    private val converter: ByteArray.(Int) -> E,
) : Decoder<E> {

    private var array: ByteArray? = null
    private var index = 0
    private var bytesToRead = -1

    override fun decode(input: DecoderInput): Decoder.State<E> {
        if (bytesToRead == -1) {
            val state = decodeSize(input)
            if (state != null) {
                return state
            }
        }

        return decodeContent(input)
    }

    private fun decodeSize(input: DecoderInput): Decoder.State<E>? {
        when (val sizeState = sizeDecoder.decode(input)) {
            is Decoder.State.Done -> {
                val size = sizeState.value.toInt()
                if (size < 0) {
                    return Decoder.State.Error("Size must be positive, but was $size")
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

    private fun decodeContent(input: DecoderInput): Decoder.State<E> {
        val array = array!!
        if (index < bytesToRead) {
            index += input.read(array, index, bytesToRead - index)
        }

        return if (index == bytesToRead) {
            val finalSize = bytesToRead
            softReset()
            Decoder.State.Done(array.converter(finalSize))
        } else {
            Decoder.State.Processing(
                "(${PrefixedSizeItemDecoder::class.simpleName}) $index / $bytesToRead bytes read."
            )
        }
    }

    override fun reset() {
        softReset()
        array = null
    }

    private fun softReset() {
        index = 0
        bytesToRead = -1
    }

}

