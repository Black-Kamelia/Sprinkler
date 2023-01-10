package com.kamelia.sprinkler.binary.encoder

import java.nio.ByteBuffer

fun interface Encoder<in T> {

    fun encode(obj: T): ByteBuffer

}
