@file:JvmName("ByteAccess")
package com.kamelia.sprinkler.util

/**
 * Returns the bit at the given [index] in a [Byte].
 *
 * @receiver the [Byte] to access
 * @param index the index of the bit to return
 * @return the bit at the given [index]
 * @throws IllegalArgumentException if [index] is not in between 0 and [Byte.SIZE_BITS] (exclusive)
 */
fun Byte.bit(index: Int): Byte {
    require(index in 0..7) { "Index $index is out of bounds ([0, ${Byte.SIZE_BITS}[)." }
    return (this.toInt() ushr index and 0x1).toByte()
}

/**
 * Returns the byte at the given [index] in a [Short].
 *
 * @receiver the [Short] to access
 * @param index the index of the byte to return
 * @return the byte at the given [index]
 * @throws IllegalArgumentException if [index] is not in between 0 and [Short.SIZE_BYTES] (exclusive)
 */
fun Short.byte(index: Int): Byte {
    require(index in 0..1) { "Index $index is out of bounds ([0, ${Short.SIZE_BYTES}[)." }
    return (this.toInt() ushr (index * 8) and 0xFF).toByte()
}

/**
 * Returns the bit at the given [index] in a [Short].
 *
 * @receiver the [Short] to access
 * @param index the index of the bit to return
 * @return the bit at the given [index]
 * @throws IllegalArgumentException if [index] is not in between 0 and [Short.SIZE_BITS] (exclusive)
 */
fun Short.bit(index: Int): Byte {
    require(index in 0..15) { "Index $index is out of bounds ([0, ${Short.SIZE_BITS}[)." }
    return (this.toInt() ushr index and 0x1).toByte()
}

/**
 * Returns the byte at the given [index] in an [Int].
 *
 * @receiver the [Int] to access
 * @param index the index of the byte to return
 * @return the byte at the given [index]
 * @throws IllegalArgumentException if [index] is not in between 0 and [Int.SIZE_BYTES] (exclusive)
 */
fun Int.byte(index: Int): Byte {
    require(index in 0..3) { "Index $index is out of bounds ([0, ${Int.SIZE_BYTES}[)." }
    return (this ushr (index * 8) and 0xFF).toByte()
}

/**
 * Returns the bit at the given [index] in an [Int].
 *
 * @receiver the [Int] to access
 * @param index the index of the bit to return
 * @return the bit at the given [index]
 * @throws IllegalArgumentException if [index] is not in between 0 and [Int.SIZE_BITS] (exclusive)
 */
fun Int.bit(index: Int): Byte {
    require(index in 0..31) { "Index $index is out of bounds ([0, ${Int.SIZE_BITS}[)." }
    return (this ushr index and 0x1).toByte()
}

/**
 * Returns the byte at the given [index] in a [Long].
 *
 * @receiver the [Long] to access
 * @param index the index of the byte to return
 * @return the byte at the given [index]
 * @throws IllegalArgumentException if [index] is not in between 0 and [Long.SIZE_BYTES] (exclusive)
 */
fun Long.byte(index: Int): Byte {
    require(index in 0..7) { "Index $index is out of bounds ([0, ${Long.SIZE_BYTES}[)." }
    return (this ushr (index * 8) and 0xFF).toByte()
}

/**
 * Returns the bit at the given [index] in a [Long].
 *
 * @receiver the [Long] to access
 * @param index the index of the bit to return
 * @return the bit at the given [index]
 * @throws IllegalArgumentException if [index] is not in between 0 and [Long.SIZE_BITS] (exclusive)
 */
fun Long.bit(index: Int): Byte {
    require(index in 0..63) { "Index $index is out of bounds ([0, ${Long.SIZE_BITS}[)." }
    return (this ushr index and 0x1).toByte()
}

/**
 * Returns the byte at the given [index] in a [Float].
 *
 * @receiver the [Float] to access
 * @param index the index of the byte to return
 * @return the byte at the given [index]
 * @throws IllegalArgumentException if [index] is not in between 0 and [Float.SIZE_BYTES] (exclusive)
 */
fun Float.byte(index: Int): Byte {
    require(index in 0..3) { "Index $index is out of bounds ([0, ${Float.SIZE_BYTES}[)." }
    return (this.toRawBits() ushr (index * 8) and 0xFF).toByte()
}

/**
 * Returns the bit at the given [index] in a [Float].
 *
 * @receiver the [Float] to access
 * @param index the index of the bit to return
 * @return the bit at the given [index]
 * @throws IllegalArgumentException if [index] is not in between 0 and [Float.SIZE_BITS] (exclusive)
 */
fun Float.bit(index: Int): Byte {
    require(index in 0..31) { "Index $index is out of bounds ([0, ${Float.SIZE_BITS}[)." }
    return (this.toRawBits() ushr index and 0x1).toByte()
}

/**
 * Returns the byte at the given [index] in a [Double].
 *
 * @receiver the [Double] to access
 * @param index the index of the byte to return
 * @return the byte at the given [index]
 * @throws IllegalArgumentException if [index] is not in between 0 and [Double.SIZE_BYTES] (exclusive)
 */
fun Double.byte(index: Int): Byte {
    require(index in 0..7) { "Index $index is out of bounds ([0, ${Double.SIZE_BYTES}[)." }
    return (this.toRawBits() ushr (index * 8) and 0xFF).toByte()
}

/**
 * Returns the bit at the given [index] in a [Double].
 *
 * @receiver the [Double] to access
 * @param index the index of the bit to return
 * @return the bit at the given [index]
 * @throws IllegalArgumentException if [index] is not in between 0 and [Double.SIZE_BITS] (exclusive)
 */
fun Double.bit(index: Int): Byte {
    require(index in 0..63) { "Index $index is out of bounds ([0, ${Double.SIZE_BITS}[)." }
    return (this.toRawBits() ushr index and 0x1).toByte()
}
