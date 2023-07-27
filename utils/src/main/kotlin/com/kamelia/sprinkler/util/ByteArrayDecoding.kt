@file:JvmName("ByteArrayDecoding")

package com.kamelia.sprinkler.util

import java.nio.ByteOrder
import java.nio.charset.Charset

/**
 * Read a [Byte] from a byte array.
 *
 * @receiver the byte array to read from
 * @param start the index to start reading from (defaults to 0)
 * @return the byte read
 * @throws IllegalArgumentException if [this.size] <= [start] || [start] < 0
 */
@JvmOverloads
fun ByteArray.readByte(start: Int = 0): Byte {
    require(start in indices) { "Index $start is out of bounds for array of size $size." }
    return this[start]
}

/**
 * Read a [Short] from a byte array.
 *
 * @receiver the byte array to read from
 * @param endianness the endianness of the short (defaults to [ByteOrder.BIG_ENDIAN])
 * @param start the index to start reading from (defaults to 0)
 * @return the short read
 * @throws IllegalArgumentException if [this.size] <= [start] + 1 || [start] < 0
 * @throws AssertionError if the endianness is unknown.
 */
@JvmOverloads
fun ByteArray.readShort(endianness: ByteOrder = ByteOrder.BIG_ENDIAN, start: Int = 0): Short {
    require(start + 1 < size && start >= 0) { "Index $start is out of bounds for array of size $size." }
    return if (ByteOrder.BIG_ENDIAN === endianness) {
        (this[start + 0].toInt() shl 8) or (this[start + 1].toInt() and 0xFF)
    } else {
        (this[start + 1].toInt() shl 8) or (this[start + 0].toInt() and 0xFF)
    }.toShort()
}

/**
 * Read an [Int] from a byte array.
 *
 * @receiver the byte array to read from
 * @param endianness the endianness of the int (defaults to [ByteOrder.BIG_ENDIAN])
 * @param start the index to start reading from (defaults to 0)
 * @return the int read
 * @throws IllegalArgumentException if [this.size] <= [start] + 3 || [start] < 0
 * @throws AssertionError if the endianness is unknown.
 */
@JvmOverloads
fun ByteArray.readInt(endianness: ByteOrder = ByteOrder.BIG_ENDIAN, start: Int = 0): Int {
    require(start + 3 < size && start >= 0) { "Index $start is out of bounds for array of size $size." }
    return if (ByteOrder.BIG_ENDIAN === endianness) {
        (this[start + 0].toInt() shl 24) or ((this[start + 1].toInt() and 0xFF) shl 16) or
                    ((this[start + 2].toInt() and 0xFF) shl 8) or (this[start + 3].toInt() and 0xFF)
    } else {
        (this[start + 3].toInt() shl 24) or ((this[start + 2].toInt() and 0xFF) shl 16) or
                    ((this[start + 1].toInt() and 0xFF) shl 8) or (this[start + 0].toInt() and 0xFF)
    }
}

/**
 * Read a [Long] from a byte array.
 *
 * @receiver the byte array to read from
 * @param endianness the endianness of the long (defaults to [ByteOrder.BIG_ENDIAN])
 * @param start the index to start reading from (defaults to 0)
 * @return the long read
 * @throws IllegalArgumentException if [this.size] <= [start] + 7 || [start] < 0
 * @throws AssertionError if the endianness is unknown.
 */
@JvmOverloads
fun ByteArray.readLong(endianness: ByteOrder = ByteOrder.BIG_ENDIAN, start: Int = 0): Long {
    require(start + 7 < size && start >= 0) { "Index $start is out of bounds for array of size $size." }
    return if (ByteOrder.BIG_ENDIAN === endianness) {
        (this[start + 0].toLong() shl 56) or ((this[start + 1].toLong() and 0xFF) shl 48) or
                    ((this[start + 2].toLong() and 0xFF) shl 40) or ((this[start + 3].toLong() and 0xFF) shl 32) or
                    ((this[start + 4].toLong() and 0xFF) shl 24) or ((this[start + 5].toLong() and 0xFF) shl 16) or
                    ((this[start + 6].toLong() and 0xFF) shl 8) or (this[start + 7].toLong() and 0xFF)
    } else {
        (this[start + 7].toLong() shl 56) or ((this[start + 6].toLong() and 0xFF) shl 48) or
                    ((this[start + 5].toLong() and 0xFF) shl 40) or ((this[start + 4].toLong() and 0xFF) shl 32) or
                    ((this[start + 3].toLong() and 0xFF) shl 24) or ((this[start + 2].toLong() and 0xFF) shl 16) or
                    ((this[start + 1].toLong() and 0xFF) shl 8) or (this[start + 0].toLong() and 0xFF)
    }
}

/**
 * Read a [Float] from a byte array.
 *
 * @receiver the byte array to read from
 * @param endianness the endianness of the float (defaults to [ByteOrder.BIG_ENDIAN])
 * @param start the index to start reading from (defaults to 0)
 * @return the float read
 * @throws IllegalArgumentException if [this.size] <= [start] + 3 || [start] < 0
 * @throws AssertionError if the endianness is unknown.
 */
@JvmOverloads
fun ByteArray.readFloat(endianness: ByteOrder = ByteOrder.BIG_ENDIAN, start: Int = 0): Float {
    require(start + 3 < size && start >= 0) { "Index $start is out of bounds for array of size $size." }
    return Float.fromBits(readInt(endianness, start))
}

/**
 * Read a [Double] from a byte array.
 *
 * @receiver the byte array to read from
 * @param endianness the endianness of the double (defaults to [ByteOrder.BIG_ENDIAN])
 * @param start the index to start reading from (defaults to 0)
 * @return the double read
 * @throws IllegalArgumentException if [this.size] <= [start] + 7 || [start] < 0
 * @throws AssertionError if the endianness is unknown.
 */
@JvmOverloads
fun ByteArray.readDouble(endianness: ByteOrder = ByteOrder.BIG_ENDIAN, start: Int = 0): Double {
    require(start + 7 < size && start >= 0) { "Index $start is out of bounds for array of size $size." }
    return Double.fromBits(readLong(endianness, start))
}

/**
 * Read a [Boolean] from a byte array.
 *
 * The boolean is read as a single byte, where 0 is `false` and any other value is `true`.
 *
 * @receiver the byte array to read from
 * @param start the index to start reading from (defaults to 0)
 * @return the boolean read
 * @throws IllegalArgumentException if [this.size] <= [start] || [start] < 0
 */
@JvmOverloads
fun ByteArray.readBoolean(start: Int = 0): Boolean {
    require(start in indices) { "Index $start is out of bounds for array of size $size." }
    return this[start] != 0.toByte()
}

/**
 * Read a [String] from a byte array.
 *
 * @receiver the byte array to read from
 * @param charset the charset to use to decode the string (defaults to [Charsets.UTF_8])
 * @param length the length of the string to read (defaults to the size of the array)
 * @param start the index to start reading from (defaults to 0)
 * @return the string read
 * @throws IllegalArgumentException if [this.size] < [start] + [length] || [start] < 0 || [length] < 0 || [start] + [length] < 0
 */
@JvmOverloads
fun ByteArray.readString(charset: Charset = Charsets.UTF_8, length: Int = size, start: Int = 0): String {
    require(start >= 0) { "Start must be non-negative, but was $start." }
    require(length >= 0) { "Length must be non-negative, but was $length." }
    require(start + length <= size) {
        "Start + length must be less than or equal to size, but was ${start + length} = ($start + $length) > $size."
    }
    return String(this, start, length, charset)
}
