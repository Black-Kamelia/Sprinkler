package com.kamelia.sprinkler.binary.common

enum class ByteEndianness {
    BIG_ENDIAN,
    LITTLE_ENDIAN
    ;

    val isBigEndian: Boolean
        get() = this == BIG_ENDIAN

}
