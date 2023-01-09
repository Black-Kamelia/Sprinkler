package com.kamelia.sprinkler.serializer.binary

import java.nio.ByteBuffer

interface ByteStream {

    val remaining: Int

    fun nextByte(): Byte

    fun nextInt(): Int

}

interface BinaryBuffer : ByteStream {

    fun flip(): BinaryBuffer

    fun reset(): BinaryBuffer

    fun transferTo(array: BinaryBuffer): BinaryBuffer

    fun put(byte: Byte): BinaryBuffer

    fun putInt(int: Int): BinaryBuffer

    companion object {
        fun from(buffer: ByteBuffer) = object : BinaryBuffer by BinaryBufferByteBufferAdapter(buffer) {}
    }
}

internal class BinaryBufferByteBufferAdapter(private val inner: ByteBuffer) : BinaryBuffer {

    override fun flip() = apply {
        inner.flip()
    }

    override fun reset() = apply {
        inner.clear()
    }

    override val remaining: Int
        get() = inner.remaining()

    override fun nextByte(): Byte = inner.get()

    override fun nextInt(): Int = inner.int

    override fun put(byte: Byte) = apply {
        inner.put(byte)
    }

    override fun putInt(int: Int) = apply {
        inner.putInt(int)
    }

    override fun transferTo(array: BinaryBuffer) = apply {
        flip()
//        if (remaining <= array.remaining) {
//            array.put(from)
//        } else {
//            var oldLimit = from.limit();
//            from.limit(to.remaining());
//            to.put(from);
//            from.limit(oldLimit);
//        }
//        from.compact();
    }

}
