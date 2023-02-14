@file:JvmName("ByteArrays")

package com.kamelia.sprinkler.binary.decoder.util

import com.kamelia.sprinkler.binary.common.ByteEndianness
import java.nio.charset.Charset

fun ByteArray.readByte(): Byte {
    check(isNotEmpty()) { "Not enough bytes to read a byte ($size)." }
    return this[0]
}

fun ByteArray.readShort(endianness: ByteEndianness = ByteEndianness.BIG_ENDIAN): Short {
    check(size >= 2) { "Not enough bytes to read a short ($size)." }
    return when (endianness) {
        ByteEndianness.BIG_ENDIAN -> (this[0].toInt() shl 8) or (this[1].toInt() and 0xFF)
        ByteEndianness.LITTLE_ENDIAN -> (this[1].toInt() shl 8) or (this[0].toInt() and 0xFF)
    }.toShort()
}

fun ByteArray.readInt(endianness: ByteEndianness = ByteEndianness.BIG_ENDIAN): Int {
    check(size >= 4) { "Not enough bytes to read an int ($size)." }
    return when (endianness) {
        ByteEndianness.BIG_ENDIAN -> (this[0].toInt() shl 24) or ((this[1].toInt() and 0xFF) shl 16) or
                    ((this[2].toInt() and 0xFF) shl 8) or (this[3].toInt() and 0xFF)
        ByteEndianness.LITTLE_ENDIAN -> (this[3].toInt() shl 24) or ((this[2].toInt() and 0xFF) shl 16) or
                    ((this[1].toInt() and 0xFF) shl 8) or (this[0].toInt() and 0xFF)
    }
}

fun ByteArray.readLong(endianness: ByteEndianness = ByteEndianness.BIG_ENDIAN): Long {
    check(size >= 8) { "Not enough bytes to read a long ($size)." }
    return when (endianness) {
        ByteEndianness.BIG_ENDIAN -> (this[0].toLong() shl 56) or ((this[1].toLong() and 0xFF) shl 48) or
                    ((this[2].toLong() and 0xFF) shl 40) or ((this[3].toLong() and 0xFF) shl 32) or
                    ((this[4].toLong() and 0xFF) shl 24) or ((this[5].toLong() and 0xFF) shl 16) or
                    ((this[6].toLong() and 0xFF) shl 8) or (this[7].toLong() and 0xFF)
        ByteEndianness.LITTLE_ENDIAN -> (this[7].toLong() shl 56) or ((this[6].toLong() and 0xFF) shl 48) or
                    ((this[5].toLong() and 0xFF) shl 40) or ((this[4].toLong() and 0xFF) shl 32) or
                    ((this[3].toLong() and 0xFF) shl 24) or ((this[2].toLong() and 0xFF) shl 16) or
                    ((this[1].toLong() and 0xFF) shl 8) or (this[0].toLong() and 0xFF)
    }
}

fun ByteArray.readFloat(endianness: ByteEndianness = ByteEndianness.BIG_ENDIAN): Float {
    check(size >= 4) { "Not enough bytes to read a float ($size)." }
    return Float.fromBits(readInt(endianness))
}

fun ByteArray.readDouble(endianness: ByteEndianness = ByteEndianness.BIG_ENDIAN): Double {
    check(size >= 8) { "Not enough bytes to read a double ($size)." }
    return Double.fromBits(readLong(endianness))
}

fun ByteArray.readBoolean(): Boolean {
    check(isNotEmpty()) { "Not enough bytes to read a boolean ($size)." }
    return this[0] != 0.toByte()
}

fun ByteArray.readString(charset: Charset, length: Int = 0): String = String(this,0, length, charset)
