package com.kamelia.sprinkler.decoder.util

import com.kamelia.sprinkler.binary.decoder.Decoder
import org.junit.jupiter.api.Assertions.assertEquals

fun <T> Decoder.State<T>.assertDoneAndGet(): T {
    println(this)
    assertEquals(Decoder.State.Done::class.java, javaClass)
    return (this as Decoder.State.Done).value
}
