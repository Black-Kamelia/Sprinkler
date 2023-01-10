package com.kamelia.sprinkler.binary.decoder.stream

import java.nio.ByteBuffer


sealed class NumberStreamDeserializer<E : Number>(private val byteSize: Byte) : AbstractStreamDeserializer<E>() {

    init {
        require(byteSize > 0) { "Number of bytes must be greater than 0 got $byteSize" }
        buffer = TODO("BinaryBuffer(bytes)")
    }

    private val buffer: ByteBuffer

    override fun process(bytes: ByteBuffer): StreamDeserializer.State<E> {
        bytes.flip()
        //buffer.transferTo(bytes)
        bytes.flip()

        state = if (buffer.remaining() == 0) {
            buffer.flip()
            StreamDeserializer.State.Done { createObject() }
        } else {
            StreamDeserializer.State.Processing
        }
        return state
    }

    override fun reset() {
        super.reset()
        buffer.reset()
    }

    protected abstract fun createObject(): E

}
