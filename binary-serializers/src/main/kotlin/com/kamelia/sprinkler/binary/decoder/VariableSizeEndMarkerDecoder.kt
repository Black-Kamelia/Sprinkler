package com.kamelia.sprinkler.binary.decoder

class VariableSizeEndMarkerDecoder<E> @JvmOverloads constructor(
    endMarker: Byte,
    private val endianness: ByteEndianness = ByteEndianness.BIG_ENDIAN,
    private val extractor: ByteArray.(ByteEndianness, Int) -> E,
) : Decoder<E> {

    private val endMarker = endMarker.toInt()
    private var array: ByteArray? = null
    private var index = 0

    override fun decode(input: DecoderDataInput): Decoder.State<E> {
        while (true) {
            when (val byte = input.read()) {
                -1 -> return Decoder.State.Processing(
                    "(${VariableSizeEndMarkerDecoder::class.simpleName}) missing end marker ($index bytes read)."
                )
                endMarker -> break
                else -> addToArray(byte)
            }
        }

        val result = array!!.extractor(endianness, index)
        index = 0
        return Decoder.State.Done(result)
    }

    override fun reset() {
        index = 0
        array = null
    }

    private fun addToArray(byte: Int) {
        val current = array!!
        val array = when {
            this.array == null -> replaceArray { ByteArray(16) }
            index == current.size -> replaceArray { current.copyOf(current.size * 2) }
            else -> current
        }
        array[index] = byte.toByte()
        index++
    }

    private inline fun replaceArray(factory: () -> ByteArray): ByteArray = factory().also { array = it }

}
