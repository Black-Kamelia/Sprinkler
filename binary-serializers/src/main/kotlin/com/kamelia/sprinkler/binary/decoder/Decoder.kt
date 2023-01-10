package com.kamelia.sprinkler.binary.decoder

import java.io.InputStream

fun interface Decoder<out T> {

    fun decode(stream: InputStream): T

}
