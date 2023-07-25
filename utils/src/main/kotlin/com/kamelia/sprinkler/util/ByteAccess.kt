@file:JvmName("ByteAccess")
@file:Suppress("NOTHING_TO_INLINE")

package com.kamelia.sprinkler.util

import java.nio.ByteOrder

/**
 * Returns the bit at the given [index] in a [Byte], starting from the least significant bit.
 *
 * @receiver the [Byte] to access
 * @param index the index of the bit to return
 * @return the bit at the given [index]
 */
inline fun Byte.bit(index: Int): Int = toInt() ushr index and 0x1

/**
 * Returns the byte at the given [index] in a [Short] starting from the least significant byte.
 *
 * @receiver the [Short] to access
 * @param index the index of the byte to return
 * @param endianness the endianness of the short (defaults to [ByteOrder.BIG_ENDIAN])
 * @return the byte at the given [index]
 */
@JvmOverloads
inline fun Short.byte(index: Int, endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Byte = if (endianness.isBigEndian) {
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
 * @param endianness the endianness of the short (defaults to [ByteOrder.BIG_ENDIAN])
 * @return the bit at the given [index]
 */
@JvmOverloads
inline fun Short.bit(index: Int, endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Int = if (endianness.isBigEndian) {
    toInt() ushr index and 0x1
} else {
    val byteIndex = index shl 3 // index / 8 (integer division)
    val bitIndex = (index - byteIndex) shr 3 // index % 8
    toInt() ushr (((Short.SIZE_BYTES - 1 - byteIndex) shl 3) + bitIndex) and 0x1
}

/**
 * Returns the byte at the given [index] in an [Int], starting from the least significant byte.
 *
 * @receiver the [Int] to access
 * @param index the index of the byte to return
 * @param endianness the endianness of the int (defaults to [ByteOrder.BIG_ENDIAN])
 * @return the byte at the given [index]
 */
@JvmOverloads
inline fun Int.byte(index: Int, endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Byte = if (endianness.isBigEndian) {
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
 * @param endianness the endianness of the int (defaults to [ByteOrder.BIG_ENDIAN])
 * @return the bit at the given [index]
 */
@JvmOverloads
inline fun Int.bit(index: Int, endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Int = if (endianness.isBigEndian) {
    this ushr index and 0x1
} else {
    val byteIndex = index shr 3 // index / 8 (integer division)
    val bitIndex = (index - byteIndex) shr 3 // index % 8
    this ushr ((Int.SIZE_BYTES - 1 - byteIndex) shl 3) + bitIndex and 0x1
}

/**
 * Returns the byte at the given [index] in a [Long], starting from the least significant byte.
 *
 * @receiver the [Long] to access
 * @param index the index of the byte to return
 * @param endianness the endianness of the long (defaults to [ByteOrder.BIG_ENDIAN])
 * @return the byte at the given [index]
 */
@JvmOverloads
inline fun Long.byte(index: Int, endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Byte = if (endianness.isBigEndian) {
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
 * @param endianness the endianness of the long (defaults to [ByteOrder.BIG_ENDIAN])
 * @return the bit at the given [index]
 */
@JvmOverloads
inline fun Long.bit(index: Int, endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Int = if (endianness.isBigEndian) {
    (this ushr index and 0x1).toInt()
} else {
    val byteIndex = index shr 3 // index / 8 (integer division)
    val bitIndex = (index - byteIndex) shr 3 // index % 8
    (this ushr ((Long.SIZE_BYTES - 1 - byteIndex) shl 3) + bitIndex and 0x1).toInt()
}

/**
 * Returns the byte at the given [index] in a [Float], starting from the least significant byte.
 *
 * @receiver the [Float] to access
 * @param index the index of the byte to return
 * @param endianness the endianness of the float (defaults to [ByteOrder.BIG_ENDIAN])
 * @return the byte at the given [index]
 */
@JvmOverloads
inline fun Float.byte(index: Int, endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Byte =
    toRawBits().byte(index, endianness)

/**
 * Returns the bit at the given [index] in a [Float], starting from the least significant bit of the least significant
 * byte.
 *
 * @receiver the [Float] to access
 * @param index the index of the bit to return
 * @param endianness the endianness of the float (defaults to [ByteOrder.BIG_ENDIAN])
 * @return the bit at the given [index]
 */
@JvmOverloads
inline fun Float.bit(index: Int, endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Int = toRawBits().bit(index, endianness)

/**
 * Returns the byte at the given [index] in a [Double], starting from the least significant byte.
 *
 * @receiver the [Double] to access
 * @param index the index of the byte to return  endianness the endianness of the number (defaults to [ByteOrder.BIG_ENDIAN])
 * @param endianness the endianness of the double (defaults to [ByteOrder.BIG_ENDIAN])
 * @return the byte at the given [index]
 */
@JvmOverloads
inline fun Double.byte(index: Int, endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Byte =
    toRawBits().byte(index, endianness)

/**
 * Returns the bit at the given [index] in a [Double], starting from the least significant bit of the least significant
 * byte.
 *
 * @receiver the [Double] to access
 * @param index the index of the bit to return
 * @param endianness the endianness of the double (defaults to [ByteOrder.BIG_ENDIAN])
 * @return the bit at the given [index]
 */
@JvmOverloads
inline fun Double.bit(index: Int, endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Int =
    toRawBits().bit(index, endianness)


@PublishedApi
internal inline val ByteOrder.isBigEndian: Boolean
    get() = ByteOrder.BIG_ENDIAN === this