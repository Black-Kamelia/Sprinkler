package com.kamelia.sprinkler.serializer.binary.deserializer

import com.kamelia.sprinkler.serializer.binary.AbstractStreamDeserializer
import com.kamelia.sprinkler.serializer.binary.BinaryBuffer
import com.kamelia.sprinkler.serializer.binary.ByteStream
import com.kamelia.sprinkler.serializer.binary.Deserializer
import com.kamelia.sprinkler.serializer.binary.StreamDeserializer
import com.kamelia.sprinkler.serializer.binary.StreamDeserializer.State.Done
import com.kamelia.sprinkler.serializer.binary.StreamDeserializer.State.Processing

sealed class NumberDeserializer<E : Number>(private val byteSize: Byte) : Deserializer<E> {

    init {
        require(byteSize > 0) { "Number of bytes must be greater than 0 got $byteSize" }
    }

    final override fun deserialize(bytes: ByteStream): E {
        require(bytes.remaining >= this.byteSize) { "Not enough bytes to deserialize" }
        return mapper(bytes)
    }

    protected abstract fun mapper(bytes: ByteStream): E

}

sealed class NumberStreamDeserializer<E : Number>(private val byteSize: Byte) : AbstractStreamDeserializer<E>() {

    init {
        require(byteSize > 0) { "Number of bytes must be greater than 0 got $byteSize" }
        buffer = TODO("BinaryBuffer(bytes)")
    }

    private val buffer: BinaryBuffer

    override fun process(bytes: BinaryBuffer): StreamDeserializer.State<E> {
        bytes.flip()
        buffer.transferTo(bytes)
        bytes.flip()

        state = if (buffer.remaining == 0) {
            buffer.flip()
            Done { createObject() }
        } else {
            Processing
        }
        return state
    }

    override fun reset() {
        super.reset()
        buffer.reset()
    }

    protected abstract fun createObject(): E

}
