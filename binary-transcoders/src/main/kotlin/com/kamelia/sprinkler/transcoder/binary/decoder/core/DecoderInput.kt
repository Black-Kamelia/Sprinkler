package com.kamelia.sprinkler.transcoder.binary.decoder.core

import com.kamelia.sprinkler.transcoder.binary.encoder.composer.composedEncoder
import com.kamelia.sprinkler.transcoder.binary.encoder.core.Encoder
import com.kamelia.sprinkler.util.bit
import java.io.InputStream
import java.nio.ByteBuffer
import java.util.*
import kotlin.math.ceil
import kotlin.math.log2
import kotlin.math.min

/**
 * Abstraction allowing [Decoders][Decoder] to read bytes from a source. This interface provides methods for reading
 * bytes in various ways, including reading a single byte, reading bytes into a [ByteArray], reading bytes into a
 * [MutableCollection], etc.
 *
 * [read] is the only method that must be implemented. All other methods are default implemented depending on this
 * method, meaning that this interface is actually a functional interface and can be implemented as a lambda, as shown
 * below:
 *
 * &nbsp;
 *
 * ```
 * var byte = 0.toByte()
 * val myInput = DecoderInput { byte++ } // myInput.read() will return 0, 1, 2, 3, ...
 * ```
 *
 * @see Decoder
 */
fun interface DecoderInput {

    fun readBit(): Int

    fun readBits(byte: Int, start: Int, length: Int): Int {
        Objects.checkFromIndexSize(start, length, 8)
        var result = 0
        repeat(length) {
            val bit = readBit()
            result = result or (bit shl (7 - start - it))
        }
        return result
    }

    fun readBits(byte: Int, length: Int) = readBits(byte, 0, length)

    fun readBits(byte: Byte, start: Int, length: Int) = readBits(byte.toInt(), start, length)

    fun readBits(bytes: ByteArray, start: Int, length: Int): Int {
        Objects.checkFromIndexSize(start, length, bytes.size * 8)
        val actualStart = start / 8

        // read the partial byte at the start
        val prefixPart = start - 8 * actualStart // start % 8
        val hasPrefix = prefixPart > 0
        if (hasPrefix) {
            val byte = readBits(prefixPart, prefixPart, 8 - prefixPart)
            bytes[actualStart] = byte.toByte()
        }

        val prefixOffset = if (hasPrefix) 1 else 0
        val bitLeft = length - prefixPart
    }

    /**
     * Reads a single byte from the source. Returns -1 if there are no more bytes to read.
     *
     * @return the byte read, or -1 if there are no more bytes to read
     */
    fun read(): Int {
        var byte = 0
        repeat(8) {
            val result = readBit()
            if (result == -1) { // end of stream
                return if (it == 0) { // no bits read
                    -1
                } else { // some bits read
                    byte
                }
            }
            byte = byte or (result shl (7 - it))
        }
        return byte
    }

    /**
     * Reads bytes from the source into the given [ByteArray] and returns the number of bytes read. The [start] and
     * [length] parameters specify the range of indices in the [ByteArray] to read into. The [start] parameter is
     * inclusive, and the [length] parameter is exclusive.
     *
     * @param bytes the [ByteArray] to read into
     * @param start the inclusive start index in the [ByteArray] to read into
     * @param length the exclusive end index in the [ByteArray] to read into
     * @return the number of bytes read
     * @throws IndexOutOfBoundsException if [start] < 0 or [length] < 0 or [start] + [length] > [ByteArray.size]
     */
    fun read(bytes: ByteArray, start: Int, length: Int): Int {
        Objects.checkFromIndexSize(start, length, bytes.size)
        var index = start
        val end = start + length
        while (index < end) {
            val read = read()
            if (read == -1) break
            bytes[index] = read.toByte()
            index++
        }
        return index - start
    }

    /**
     * Reads bytes from the source into the given [ByteArray] and returns the number of bytes read. The [start]
     * parameter specifies the start index in the [ByteArray] to read into. The method will read as many bytes as
     * possible, up to the end of the [ByteArray].
     *
     * @param bytes the [ByteArray] to read into
     * @param start the inclusive start index in the [ByteArray] to read into
     * @return the number of bytes read
     */
    fun read(bytes: ByteArray, start: Int): Int = read(bytes, start, bytes.size - start)

    /**
     * Reads bytes from the source into the given [ByteArray] and returns the number of bytes read. The method will
     * try to fill the entire [ByteArray].
     *
     * @param bytes the [ByteArray] to read into
     * @return the number of bytes read
     */
    fun read(bytes: ByteArray): Int = read(bytes, 0, bytes.size)

    /**
     * Reads bytes from the source into the given [MutableCollection] and returns the number of bytes read. The [length]
     * parameter specifies the maximum number of bytes to read.
     *
     * @param bytes the [MutableCollection] to read into
     * @param length the maximum number of bytes to read
     */
    fun read(bytes: MutableCollection<Byte>, length: Int): Int {
        var count = 0
        while (count < length && bytes.size < Int.MAX_VALUE) {
            val read = read()
            if (read == -1) break
            bytes += read.toByte()
            count++
        }
        return count
    }

    /**
     * Reads bytes from the source into the given [MutableCollection] and returns the number of bytes read. The method
     * will read as many bytes as possible, up to [Int.MAX_VALUE] element.
     *
     * @param bytes the [MutableCollection] to read into
     * @return the number of bytes read
     */
    fun read(bytes: MutableCollection<Byte>): Int = read(bytes, Int.MAX_VALUE)

    /**
     * Skips the given number of bytes. Returns the number of bytes actually skipped.
     *
     * @param n the number of bytes to skip
     * @return the number of bytes actually skipped
     */
    fun skip(n: Long): Long {
        var skipped = 0L
        while (skipped < n) {
            val read = read()
            if (read == -1) break
            skipped++
        }
        return skipped
    }

    companion object {

        /**
         * An empty [DecoderInput] that always returns -1.
         */
        @JvmField
        val EMPTY_INPUT = DecoderInput { -1 }

        /**
         * Creates a [DecoderInput] from the given [InputStream]. All changes to the [InputStream] will be reflected
         * in the [DecoderInput] and vice versa.
         *
         * @param inner the [InputStream] to read from
         * @return a [DecoderInput] that reads from the given [InputStream]
         */
        fun from(inner: InputStream): DecoderInput = TODO("change this")//DecoderInput(inner::read)

        /**
         * Creates a [DecoderInput] from the given [ByteBuffer]. All changes to the [ByteBuffer] will be reflected
         * in the [DecoderInput] and vice versa.
         *
         * All reading methods will expect the [ByteBuffer] to be in write mode before the method is called and will
         * leave it in write mode after the method is called. This means that the [ByteBuffer] will be flipped before
         * reading and compacted after reading. This implementation allows to keep the buffer in write mode without
         * having to flip it back and forth.
         *
         * @param inner the [ByteBuffer] to read from
         * @return a [DecoderInput] that reads from the given [ByteBuffer]
         */
        fun from(inner: ByteBuffer): DecoderInput = object : DecoderInput {
            override fun readBit(): Int {
                TODO("Not yet implemented")
            }

            override fun read(): Int {
                inner.flip()
                val byte = if (inner.hasRemaining()) {
                    inner.get().toInt() and 0xFF
                } else {
                    -1
                }
                inner.compact()
                return byte
            }

            override fun read(bytes: ByteArray, start: Int, length: Int): Int {
                Objects.checkFromIndexSize(start, length, bytes.size)
                if (inner.position() == 0) return 0
                inner.flip()

                val actualLength = min(length, inner.remaining())
                inner.get(bytes, start, actualLength)
                inner.compact()
                return actualLength
            }

        }

        /**
         * Creates a [DecoderInput] from the given [ByteArray]. The [ByteArray] will not be modified by the returned
         * [DecoderInput]. However, the [ByteArray] will not be copied, so any changes to the [ByteArray] will be
         * reflected in the [DecoderInput].
         *
         * @param inner the [ByteArray] to read from
         * @return a [DecoderInput] that reads from the given [ByteArray]
         */
        fun from(inner: ByteArray): DecoderInput = object : DecoderInput {

            private var buffer = 0
            private var bitLeft = 0
            private var index = 0


            override fun readBit(): Int {
                if (bitLeft == 0) {
                    val next = readFromArray()
                    if (next == -1) return -1
                    buffer = next
                    bitLeft = 8
                }
                return buffer.bit(--bitLeft)
            }

            override fun read(): Int = if (bitLeft == 0) {
                readFromArray()
            } else {
                super.read()
            }

            private fun readFromArray(): Int {
                return if (index < inner.size) {
                    inner[index++].toInt() and 0xFF
                } else {
                    -1
                }
            }

            override fun skip(n: Long): Long {
                val oldIndex = index
                index = min(index + n.toInt(), inner.size)
                return index - oldIndex.toLong()
            }
        }

    }

}

sealed interface Node {

    val id: Int

    val idSize: Int

    val nodeGroup: List<Node>

    interface Leaf : Node {

        val isBlack: Boolean

    }

    interface InnerNode : Node {

        val nw: Node

        val ne: Node

        val sw: Node

        val se: Node

    }

}


class CompressedPictureNodeFactory {

    private var currentId = 0

    private val idCurrentSize: Int
        get() = ceil(log2(currentId.toDouble())).toInt()

    private val nodesSet = ArrayList<Node>()

    val nodes: List<Node>
        get() = nodesSet.toList()

    fun leaf(isBlack: Boolean): Node.Leaf = object : Node.Leaf {
        override val idSize: Int
            get() = idCurrentSize
        override val nodeGroup: List<Node>
            get() = nodes
        override val id = currentId++
        override val isBlack = isBlack
        override fun toString(): String = "Leaf(id=$id, isBlack=$isBlack)"
    }.also(nodesSet::add)

    fun innerNode(nw: Node, ne: Node, sw: Node, se: Node): Node.InnerNode = object : Node.InnerNode {
        override val idSize: Int
            get() = idCurrentSize
        override val nodeGroup: List<Node>
            get() = nodes
        override val id = currentId++
        override val nw = nw
        override val ne = ne
        override val sw = sw
        override val se = se

        override fun toString(): String = "InnerNode(id=$id)"
    }.also(nodesSet::add)

}

fun main() {
    val factory = CompressedPictureNodeFactory()
    val black = factory.leaf(true)
    val white = factory.leaf(false)
    val inner1 = factory.innerNode(black, white, white, black)
    val inner2 = factory.innerNode(inner1, inner1, inner1, inner1)
    val root = factory.innerNode(inner2, inner2, inner2, inner2)

    val bitBooleanEncoder = Encoder<Boolean> { b, o -> o.writeBit(b) }
    val nodeIdEncoder = Encoder<Node> { n, o ->
        val start = 8 - n.idSize
        o.writeBits(n.id, start, n.idSize)
    }
    val nodeEncoder = composedEncoder<Node> {
        encode(it.idSize.toByte())
        it.nodeGroup.forEach { node ->
            println("encoding node $node")
            encode(node, nodeIdEncoder)
            when (node) {
                is Node.Leaf -> {
                    encode(true, bitBooleanEncoder)
                    encode(node.isBlack, bitBooleanEncoder)
                }
                is Node.InnerNode -> {
                    encode(false, bitBooleanEncoder)
                    encode(node.nw, nodeIdEncoder)
                    encode(node.ne, nodeIdEncoder)
                    encode(node.se, nodeIdEncoder)
                    encode(node.sw, nodeIdEncoder)
                }
            }
        }
    }

    val encoded = nodeEncoder.encode(root)
    println(black.idSize)
    encoded.forEach {
        repeat(8) { i ->
            print(it.bit(7 - i))
        }
        print("_")
    }
    println()
}
/*
00000011
000 1 1
001 1 0
010 0 000 001 000 001
011 0 010 010 010 010
100 0 011 011 011 011
000000
*/
