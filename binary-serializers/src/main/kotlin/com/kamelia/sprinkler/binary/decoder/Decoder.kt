package com.kamelia.sprinkler.binary.decoder

import java.io.InputStream

fun interface Decoder<out T> {

    fun decode(input: InputStream): T

    fun compose(consumer: (T) -> Unit): IntermediateDecoderComposer<Decoder<@UnsafeVariance T>> =
        DecoderComposer
            .new(NoOpDecoder)
            .then(this) { consumer(it) }

}
