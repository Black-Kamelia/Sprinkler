@file:JvmName("ByteAccess")
@file:Suppress("NOTHING_TO_INLINE")
package com.kamelia.sprinkler.util

/**
 * Returns the bit at the given [index] in a [Byte], starting from the least significant bit.
 *
 * @receiver the [Byte] to access
 * @param index the index of the bit to return
 * @return the bit at the given [index]
 */
inline fun Byte.bit(index: Int): Byte = (toInt() ushr index and 0x1).toByte()

/**
 * Returns the byte at the given [index] in a [Short] starting from the least significant byte.
 *
 * @receiver the [Short] to access
 * @param index the index of the byte to return
 * @param bigEndian whether to access the byte in big-endian order
 * @return the byte at the given [index]
 */
@JvmOverloads
inline fun Short.byte(index: Int, bigEndian: Boolean = true): Byte = if (bigEndian) {
    (toInt() ushr (index shl 3) and 0xFF).toByte()
} else {
    (toInt() ushr ((Short.SIZE_BYTES - 1 - index) shl 3) and 0xFF).toByte()
}

/**
 * Returns the bit at the given [index] in a [Short], starting from the least significant bit of the least significant
 * byte.
 *
 * @receiver the [Short] to access
 * @param index the index of the bit to return
 * @param bigEndian whether to access the bit in big-endian order
 * @return the bit at the given [index]
 */
@JvmOverloads
inline fun Short.bit(index: Int, bigEndian: Boolean = true): Byte = if (bigEndian) {
    (toInt() ushr index and 0x1).toByte()
} else {
    val byteIndex = index shl 3 // index / 8 (integer division)
    val bitIndex = (index - byteIndex) shr 3 // index % 8
    (toInt() ushr (((Short.SIZE_BYTES - 1 - byteIndex) shl 3) + bitIndex) and 0x1).toByte()
}

/**
 * Returns the byte at the given [index] in an [Int], starting from the least significant byte.
 *
 * @receiver the [Int] to access
 * @param index the index of the byte to return
 * @param bigEndian whether to access the byte in big-endian order
 * @return the byte at the given [index]
 */
@JvmOverloads
inline fun Int.byte(index: Int, bigEndian: Boolean = true): Byte = if (bigEndian) {
    (this ushr (index shl 3) and 0xFF).toByte()
} else {
    (this ushr ((Int.SIZE_BYTES - 1 - index) shl 3) and 0xFF).toByte()
}

/**
 * Returns the bit at the given [index] in an [Int], starting from the least significant bit of the least significant
 * byte.
 *
 * @receiver the [Int] to access
 * @param index the index of the bit to return
 * @param bigEndian whether to access the bit in big-endian order
 * @return the bit at the given [index]
 */
@JvmOverloads
inline fun Int.bit(index: Int, bigEndian: Boolean = true): Byte = if (bigEndian) {
    (this ushr index and 0x1).toByte()
} else {
    val byteIndex = index shr 3 // index / 8 (integer division)
    val bitIndex = (index - byteIndex) shr 3 // index % 8
    println("byteIndex: $byteIndex, bitIndex: $bitIndex")
    (this ushr ((Int.SIZE_BYTES - 1 - byteIndex) shl 3) + bitIndex and 0x1).toByte()
}

/**
 * Returns the byte at the given [index] in a [Long], starting from the least significant byte.
 *
 * @receiver the [Long] to access
 * @param index the index of the byte to return
 * @param bigEndian whether to access the byte in big-endian order
 * @return the byte at the given [index]
 */
@JvmOverloads
fun Long.byte(index: Int, bigEndian: Boolean = true): Byte = if (bigEndian) {
    (this ushr (index shl 3) and 0xFF).toByte()
} else {
    (this ushr ((Long.SIZE_BYTES - 1 - index) shl 3) and 0xFF).toByte()
}

/**
 * Returns the bit at the given [index] in a [Long], starting from the least significant bit of the least significant
 * byte.
 *
 * @receiver the [Long] to access
 * @param index the index of the bit to return
 * @param bigEndian whether to access the bit in big-endian order
 * @return the bit at the given [index]
 */
@JvmOverloads
fun Long.bit(index: Int, bigEndian: Boolean = true): Byte = if (bigEndian) {
    (this ushr index and 0x1).toByte()
} else {
    val byteIndex = index shr 3 // index / 8 (integer division)
    val bitIndex = (index - byteIndex) shr 3 // index % 8
    (this ushr ((Long.SIZE_BYTES - 1 - byteIndex) shl 3) + bitIndex and 0x1).toByte()
}

/**
 * Returns the byte at the given [index] in a [Float], starting from the least significant byte.
 *
 * @receiver the [Float] to access
 * @param index the index of the byte to return
 * @param bigEndian whether to access the byte in big-endian order
 * @return the byte at the given [index]
 */
@JvmOverloads
fun Float.byte(index: Int, bigEndian: Boolean = true): Byte = toRawBits().byte(index, bigEndian)

/**
 * Returns the bit at the given [index] in a [Float], starting from the least significant bit of the least significant
 * byte.
 *
 * @receiver the [Float] to access
 * @param index the index of the bit to return
 * @param bigEndian whether to access the bit in big-endian order
 * @return the bit at the given [index]
 */
@JvmOverloads
fun Float.bit(index: Int, bigEndian: Boolean = true): Byte = toRawBits().bit(index, bigEndian)

/**
 * Returns the byte at the given [index] in a [Double], starting from the least significant byte.
 *
 * @receiver the [Double] to access
 * @param index the index of the byte to return
 * @param bigEndian whether to access the byte in big-endian order
 * @return the byte at the given [index]
 */
@JvmOverloads
fun Double.byte(index: Int, bigEndian: Boolean = true): Byte = toRawBits().byte(index, bigEndian)

/**
 * Returns the bit at the given [index] in a [Double], starting from the least significant bit of the least significant
 * byte.
 *
 * @receiver the [Double] to access
 * @param index the index of the bit to return
 * @param bigEndian whether to access the bit in big-endian order
 * @return the bit at the given [index]
 */
@JvmOverloads
fun Double.bit(index: Int, bigEndian: Boolean = true): Byte = toRawBits().bit(index, bigEndian)
