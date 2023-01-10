package com.kamelia.sprinkler.binary.decoder.stream

import java.nio.ByteBuffer


abstract class AbstractStreamDeserializer<E> : StreamDeserializer<E> {

    protected var state: StreamDeserializer.State<E> = StreamDeserializer.State.Processing

    final override fun deserialize(bytes: ByteBuffer): StreamDeserializer.State<E> {
        if (state.isDone() || state.isError()) return state
        return process(bytes)
    }

    override fun reset() {
        state = StreamDeserializer.State.Processing
    }

    protected abstract fun process(bytes: ByteBuffer): StreamDeserializer.State<E>

}
