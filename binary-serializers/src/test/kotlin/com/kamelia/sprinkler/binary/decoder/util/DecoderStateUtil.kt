package com.kamelia.sprinkler.binary.decoder.util

import com.kamelia.sprinkler.binary.decoder.core.Decoder
import org.junit.jupiter.api.Assertions.assertInstanceOf

fun <T> Decoder.State<T>.assertDoneAndGet(): T {
    assertInstanceOf(Decoder.State.Done::class.java, this)
    return this.get()
}
