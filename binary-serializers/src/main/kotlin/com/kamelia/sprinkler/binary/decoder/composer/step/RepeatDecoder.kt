package com.kamelia.sprinkler.binary.decoder.composer.step

import com.kamelia.sprinkler.binary.decoder.Decoder
import com.zwendo.restrikt.annotation.PackagePrivate

@PackagePrivate
internal interface RepeatDecoder<E, R> : Decoder<R> {

    val isFull: Boolean

    fun addElement(element: E)

}
