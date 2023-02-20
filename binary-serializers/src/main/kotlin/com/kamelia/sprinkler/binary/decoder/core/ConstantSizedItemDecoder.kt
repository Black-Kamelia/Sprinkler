package com.kamelia.sprinkler.binary.decoder.core

/**
 * A [Decoder] decoding objects represented by a fixed number of bytes. This decoder accumulates the bytes read and
 * calls a [converter] function to convert these bytes to the decoded object.
 *
 * @param E the type of the decoded object
 * @param byteSize the number of bytes to read
 * @param converter a function to convert the bytes (stored in a [ByteArray]) to the decoded object
 * @constructor Creates a new [ConstantSizedItemDecoder].
 * @throws IllegalArgumentException if [byteSize] is negative
 */
class ConstantSizedItemDecoder<E>(
    private val byteSize: Int = 0,
    private val converter: ByteArray.() -> E,
) : Decoder<E> {

    init {
        require(byteSize >= 0) { "Number of bytes must positive or zero (was $byteSize)" }
    }

    private val array: ByteArray = ByteArray(byteSize)
    private var index = 0

    override fun decode(input: DecoderInputData): Decoder.State<E> {
        if (byteSize == 0) return Decoder.State.Done(array.converter()) // shortcut

        index += input.read(array, index)
        return if (index == byteSize) {
            Decoder.State.Done(array.converter()).also { index = 0 }
        } else {
            Decoder.State.Processing(
                "(${ConstantSizedItemDecoder::class.simpleName}) $index / $byteSize bytes read."
            )
        }
    }

    override fun reset() {
        index = 0
    }

}
