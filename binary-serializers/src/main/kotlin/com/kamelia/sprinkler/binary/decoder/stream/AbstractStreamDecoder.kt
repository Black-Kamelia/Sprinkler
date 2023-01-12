package com.kamelia.sprinkler.binary.decoder.stream

import java.nio.ByteBuffer


abstract class AbstractStreamDecoder<E> : StreamDecoder<E> {

    protected var state: StreamDecoder.State<E> = StreamDecoder.State.Processing

    final override fun decode(input: ByteBuffer): StreamDecoder.State<E> {
        if (state.isDone() || state.isError()) return state
        return process(input)
    }

    override fun reset() {
        state = StreamDecoder.State.Processing
    }

    protected abstract fun process(bytes: ByteBuffer): StreamDecoder.State<E>

}
