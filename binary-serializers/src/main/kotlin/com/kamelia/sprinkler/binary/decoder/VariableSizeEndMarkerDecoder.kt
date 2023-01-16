package com.kamelia.sprinkler.binary.decoder

import com.kamelia.sprinkler.binary.common.ByteEndianness

class VariableSizeEndMarkerDecoder<E> @JvmOverloads constructor(
    private val endMarker: ByteArray,
    private val elementsSize: Int,
    private val endianness: ByteEndianness = ByteEndianness.BIG_ENDIAN,
    private val extractor: ByteArray.(ByteEndianness, Int) -> E,
) : Decoder<E> {

    init {
        require(elementsSize > 0) { "elementsSize must be greater than 0 ($elementsSize)" }
        require(endMarker.size == elementsSize) {
            "endMarker must be the same size as elementsSize, expected ${elementsSize}, got ${endMarker.size}"
        }
    }

    private var array: ByteArray? = null
    private var index = 0
    private val inner = ConstantSizeDecoder(elementsSize, endianness) { this.copyOf() }

    override fun decode(input: DecoderDataInput): Decoder.State<E> {
        while (true) {
            when (val state = inner.decode(input)) {
                is Decoder.State.Done -> {
                    val element = state.value
                    if (endMarker.contentEquals(element)) {
                        break
                    }
                    addToArray(element)
                }
                else -> return state.mapEmptyState()
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

    private fun addToArray(bytes: ByteArray) {
        val current = array!!
        val array = when {
            this.array == null -> replaceArray { ByteArray(elementsSize) }
            index == current.size -> replaceArray { current.copyOf(current.size * 2) }
            else -> current
        }
        bytes.copyInto(array, index * elementsSize)
        index++
    }

    private inline fun replaceArray(factory: () -> ByteArray): ByteArray = factory().also { array = it }

}
