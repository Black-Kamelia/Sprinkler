package com.kamelia.sprinkler.binary.decoder.util

import com.kamelia.sprinkler.binary.decoder.core.Decoder
import org.junit.jupiter.api.Assertions.assertEquals

fun <T> Decoder.State<T>.assertDoneAndGet(): T {
    assertEquals(Decoder.State.Done::class.java, javaClass) {
        "Expected Done state, but got $this"
    }
    return this.get()
}
