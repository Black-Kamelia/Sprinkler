package com.kamelia.sprinkler.binary.decoder.util

operator fun Short.get(index: Int): Byte {
    require(index in 0..1) { "Index $index is out of bounds ([0, 2[)." }
    return if (index == 0) {
        (this.toInt() and 0xFF).toByte()
    } else {
        (this.toInt() ushr 8 and 0xFF).toByte()
    }
}

operator fun Int.get(index: Int): Byte {
    require(index in 0..3) { "Index $index is out of bounds ([0, 4[)." }
    return (this ushr (index * 8) and 0xFF).toByte()
}

operator fun Long.get(index: Int): Byte {
    require(index in 0..7) { "Index $index is out of bounds ([0, 8[)." }
    return (this ushr (index * 8) and 0xFF).toByte()
}

operator fun Float.get(index: Int): Byte {
    require(index in 0..3) { "Index $index is out of bounds ([0, 4[)." }
    return (this.toRawBits() ushr (index * 8) and 0xFF).toByte()
}

operator fun Double.get(index: Int): Byte {
    require(index in 0..7) { "Index $index is out of bounds ([0, 8[)." }
    return (this.toRawBits() ushr (index * 8) and 0xFF).toByte()
}
