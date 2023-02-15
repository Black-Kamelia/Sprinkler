package com.kamelia.sprinkler.binary.decoder.core

/**
 * A [Decoder] decoding objects represented by a fixed number of bytes. This decoder accumulates the bytes read in a
 * [ByteArray] and calls a [converter] function to convert the [ByteArray] to the decoded object.
 *
 * @param E the type of the decoded object
 * @param byteSize the number of bytes to read
 * @param converter a function to convert the [ByteArray] to the decoded object
 * @constructor Creates a new [ConstantSizeDecoder].
 * @throws IllegalArgumentException if [byteSize] is negative
 */
class ConstantSizeDecoder<E>(
    private val byteSize: Int = 0,
    private val converter: ByteArray.() -> E,
) : Decoder<E> {

    init {
        require(byteSize >= 0) { "Number of bytes must positive or zero (was $byteSize)" }
    }

    private val array: ByteArray = ByteArray(byteSize)
    private var index = 0

    override fun decode(input: DecoderDataInput): Decoder.State<E> {
        if (byteSize == 0) return Decoder.State.Done(array.converter()) // shortcut

        index += input.read(array, index)
        return if (index == byteSize) {
            Decoder.State.Done(array.converter()).also { index = 0 }
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
