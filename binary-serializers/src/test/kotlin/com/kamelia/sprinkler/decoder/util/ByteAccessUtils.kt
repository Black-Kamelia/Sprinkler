package com.kamelia.sprinkler.decoder.util

operator fun Int.get(int: Int): Byte {
    require(int in 0..3) { "Index $int is out of bounds ([0, 4[)." }
    return (this ushr (int * 8) and 0xFF).toByte()
}
