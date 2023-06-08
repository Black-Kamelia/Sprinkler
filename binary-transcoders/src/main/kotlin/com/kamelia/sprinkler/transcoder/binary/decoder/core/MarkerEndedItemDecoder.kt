package com.kamelia.sprinkler.transcoder.binary.decoder.core

/**
 * A [Decoder] decoding objects represented by a variable number of bytes. The number of bytes to read is undefined and
 * bytes are accumulated internally until an [endMarker] is found. Once the end marker is found, the [converter] function
 * is used to convert all the accumulated bytes to the decoded object.
 *
 * @param E the type of the decoded object
 * @param endMarker the bytes marking the end of the object
 * @param converter a function to convert the bytes (stored in a [ByteArray]) to the decoded object
 * @constructor Creates a new [MarkerEndedItemDecoder].
 * @throws IllegalArgumentException if [endMarker] is empty
 */
class MarkerEndedItemDecoder<E>(
    endMarker: ByteArray,
    private val converter: ByteArray.(Int) -> E,
) : Decoder<E> {

    private var accumulator: ByteArray? = null
    private var index = 0
    private var buffer: ArrayDeque<Byte>? = null

    init {
        require(endMarker.isNotEmpty()) { "endMarker must be greater than 0 (${endMarker.size})" }
    }

    private val endMarker = endMarker.copyOf()

    override fun decode(input: DecoderInput): Decoder.State<E> {
        val buffer = buffer ?: ArrayDeque<Byte>(endMarker.size).also { buffer = it }

        input.read(buffer, endMarker.size - buffer.size) // fill buffer
        if (buffer.size < endMarker.size) { // not enough data to fill buffer
            return Decoder.State.Processing
        }

        while (!bufferContentIsEndMarker()) {
            val byte = input.read()
            if (byte == -1) {
                return Decoder.State.Processing
            }
            addToArray(buffer.removeFirst())
            buffer.addLast(byte.toByte())
        }

        val result = (accumulator?: ByteArray(0)).converter(index) // can be null only if content is empty
        reset()
        return Decoder.State.Done(result)
    }

    override fun reset() {
        softReset()
        accumulator = null
    }

    private fun softReset() {
        index = 0
        buffer = null
    }

    private fun bufferContentIsEndMarker(): Boolean {
        val buffer = buffer!!
        repeat(endMarker.size) {
            if (buffer[it] != endMarker[it]) return false
        }
        return true
    }

    private fun addToArray(bytes: Byte) {
        val current = accumulator
        val array = when {
            current == null -> replaceArray { ByteArray(endMarker.size) }
            index == current.size -> replaceArray { current.copyOf(current.size * 2) }
            else -> current
        }
        array[index] = bytes
        index++
    }

    private inline fun replaceArray(factory: () -> ByteArray): ByteArray = factory().also { accumulator = it }

}
