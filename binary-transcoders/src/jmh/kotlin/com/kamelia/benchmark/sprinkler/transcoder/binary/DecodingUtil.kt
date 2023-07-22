package com.kamelia.benchmark.sprinkler.transcoder.binary

import com.kamelia.benchmark.sprinkler.transcoder.binary.`object`.BasicPerson
import com.kamelia.sprinkler.transcoder.binary.decoder.core.DecoderInput
import com.kamelia.sprinkler.transcoder.binary.encoder.composer.composedEncoder

val ARRAY: ByteArray
    get() {
        val person = BasicPerson("John Doe", 42)
        val encoder = composedEncoder<BasicPerson> {
            encode(it.name)
            encode(it.age)
        }
        return encoder.encode(person)
    }

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
