package com.kamelia.benchmark.sprinkler.transcoder.binary

import com.kamelia.benchmark.sprinkler.transcoder.binary.`object`.BasicPerson
import com.kamelia.sprinkler.transcoder.binary.decoder.core.DecoderInput
import com.kamelia.sprinkler.transcoder.binary.encoder.composer.composedEncoder

private val _encoder = composedEncoder<BasicPerson> {
    encode(it.name)
    encode(it.age)
}

val ARRAY: ByteArray
    get() = _encoder.encode(BasicPerson("John Doe", 42))

fun inputProxy(decoderInput: DecoderInput): DecoderInput {
    var even = false
    return DecoderInput.from {
        even = !even
        if (even) {
            -1
        } else {
            decoderInput.read()
        }
    }
}
