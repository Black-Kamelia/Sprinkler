package com.kamelia.sprinkler.binary.decoder

class VariableSizeEndMarkerDecoder<E>(
    private val endMarker: ByteArray,
    private val extractor: ByteArray.(Int) -> E,
) : Decoder<E> {

    init {
        require(endMarker.isNotEmpty()) { "endMarker must be greater than 0 (${endMarker.size})" }
    }

    private var accumulator: ByteArray? = null
    private var index = 0
    private var buffer: ArrayDeque<Byte>? = null

    override fun decode(input: DecoderDataInput): Decoder.State<E> {
        val buffer = buffer ?: ArrayDeque<Byte>(endMarker.size).also { buffer = it }

        input.read(buffer, endMarker.size - buffer.size) // fill buffer
        if (buffer.size < endMarker.size) { // not enough data to fill buffer
            return Decoder.State.Processing(MISSING_BYTES_MESSAGE)
        }

        while (!bufferIsEndMarker()) {
            val byte = input.read()
            if (byte == -1) {
                return Decoder.State.Processing(MISSING_BYTES_MESSAGE)
            }
            addToArray(buffer.removeFirst())
            buffer.addLast(byte.toByte())
        }

        val result = accumulator!!.extractor(index)
        index = 0
        return Decoder.State.Done(result)
    }

    override fun reset() {
        index = 0
        accumulator = null
    }

    override fun createNew(): Decoder<E> = VariableSizeEndMarkerDecoder(endMarker, extractor)

    private fun bufferIsEndMarker(): Boolean {
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

    private companion object {

        const val MISSING_BYTES_MESSAGE = "End marker still not found"

    }

}
