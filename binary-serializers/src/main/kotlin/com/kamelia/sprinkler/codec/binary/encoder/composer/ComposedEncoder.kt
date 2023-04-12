@file:JvmName("ComposedEncoder")

package com.kamelia.sprinkler.codec.binary.encoder.composer

import com.kamelia.sprinkler.codec.binary.encoder.core.Encoder
import com.kamelia.sprinkler.codec.binary.encoder.core.EncoderOutput

@JvmName("create")
fun <T> composedEncoder(
    block: EncodingScope<T>.(T) -> Unit,
): Encoder<T> = Encoder { obj, output ->
    lateinit var encoder: Encoder<T>
    var top = true
    val recursionQueue = ArrayDeque<() -> Unit>()
    val globalStack = ArrayList<() -> Unit>()

    encoder = Encoder(fun(t: T, o: EncoderOutput) {
        EncodingScopeImpl(o, globalStack, recursionQueue, encoder).block(t)

        if (!top) return
        top = false

        while (recursionQueue.isNotEmpty() || globalStack.isNotEmpty()) {
            while (recursionQueue.isNotEmpty()) {
                recursionQueue.removeFirst()()
            }
            while (recursionQueue.isEmpty() && globalStack.isNotEmpty()) {
                globalStack.removeLast()()
            }
        }
    })

    encoder.encode(obj, output)
}
