@file:JvmName("ByteArrays")

package com.kamelia.sprinkler.binary.decoder

import java.nio.charset.Charset

fun ByteArray.readByte(): Byte = this[0]

fun ByteArray.readShort(endianness: ByteEndianness = ByteEndianness.BIG_ENDIAN): Short =
    when (endianness) {
        ByteEndianness.BIG_ENDIAN -> (this[0].toInt() shl 8) or (this[1].toInt() and 0xFF)
        ByteEndianness.LITTLE_ENDIAN -> (this[1].toInt() shl 8) or (this[0].toInt() and 0xFF)
    }.toShort()

fun ByteArray.readInt(endianness: ByteEndianness = ByteEndianness.BIG_ENDIAN): Int =
    when (endianness) {
        ByteEndianness.BIG_ENDIAN -> (this[0].toInt() shl 24) or ((this[1].toInt() and 0xFF) shl 16) or
                    ((this[2].toInt() and 0xFF) shl 8) or (this[3].toInt() and 0xFF)
        ByteEndianness.LITTLE_ENDIAN -> (this[3].toInt() shl 24) or ((this[2].toInt() and 0xFF) shl 16) or
                    ((this[1].toInt() and 0xFF) shl 8) or (this[0].toInt() and 0xFF)
    }

fun ByteArray.readLong(endianness: ByteEndianness = ByteEndianness.BIG_ENDIAN): Long =
    when (endianness) {
        ByteEndianness.BIG_ENDIAN -> (this[0].toLong() shl 56) or ((this[1].toLong() and 0xFF) shl 48) or
                    ((this[2].toLong() and 0xFF) shl 40) or ((this[3].toLong() and 0xFF) shl 32) or
                    ((this[4].toLong() and 0xFF) shl 24) or ((this[5].toLong() and 0xFF) shl 16) or
                    ((this[6].toLong() and 0xFF) shl 8) or (this[7].toLong() and 0xFF)
        ByteEndianness.LITTLE_ENDIAN -> (this[7].toLong() shl 56) or ((this[6].toLong() and 0xFF) shl 48) or
                    ((this[5].toLong() and 0xFF) shl 40) or ((this[4].toLong() and 0xFF) shl 32) or
                    ((this[3].toLong() and 0xFF) shl 24) or ((this[2].toLong() and 0xFF) shl 16) or
                    ((this[1].toLong() and 0xFF) shl 8) or (this[0].toLong() and 0xFF)
    }

fun ByteArray.readFloat(endianness: ByteEndianness = ByteEndianness.BIG_ENDIAN): Float =
    Float.fromBits(readInt(endianness))

fun ByteArray.readDouble(endianness: ByteEndianness = ByteEndianness.BIG_ENDIAN): Double =
    Double.fromBits(readLong(endianness))

fun ByteArray.readBoolean(): Boolean = this[0] != 0.toByte()

fun ByteArray.readString(charset: Charset, length: Int = 0): String = String(this,0, length, charset)
