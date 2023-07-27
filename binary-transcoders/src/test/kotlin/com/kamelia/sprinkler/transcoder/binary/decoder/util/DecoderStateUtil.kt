package com.kamelia.sprinkler.transcoder.binary.decoder.util

import com.kamelia.sprinkler.transcoder.binary.decoder.core.Decoder
import org.junit.jupiter.api.Assertions.assertInstanceOf

fun <T> Decoder.State<T>.assertDoneAndGet(): T {
    assertInstanceOf(Decoder.State.Done::class.java, this)
    return this.get()
}
