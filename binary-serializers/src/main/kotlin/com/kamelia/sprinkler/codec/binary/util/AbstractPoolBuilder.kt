package com.kamelia.sprinkler.codec.binary.util

import java.nio.ByteOrder

abstract class AbstractPoolBuilder<T, B : AbstractPoolBuilder<T, B>> {

    private var stringCharset = Charsets.UTF_8
    private var sizePrefixed = true // false = end marker

    private var endianness: ByteOrder = ByteOrder.BIG_ENDIAN




    abstract fun build(): T

    @Suppress("UNCHECKED_CAST")
    private inline fun applyToImpl(block: () -> Unit): B {
        block()
        return this as B
    }

}
