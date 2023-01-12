package com.kamelia.sprinkler.binary.decoder.stream

import java.nio.ByteBuffer


sealed class NumberStreamDecoder<E : Number>(private val byteSize: Byte) : AbstractStreamDecoder<E>() {

    init {
        require(byteSize > 0) { "Number of bytes must be greater than 0 got $byteSize" }
        buffer = TODO("BinaryBuffer(bytes)")
    }

    private val buffer: ByteBuffer

    override fun process(bytes: ByteBuffer): StreamDecoder.State<E> {
        bytes.flip()
        //buffer.transferTo(bytes)
        bytes.flip()

        state = if (buffer.remaining() == 0) {
            buffer.flip()
            StreamDecoder.State.Done { createObject() }
        } else {
            StreamDecoder.State.Processing
        }
        return state
    }

    override fun reset() {
        super.reset()
        buffer.reset()
    }

    protected abstract fun createObject(): E

}
