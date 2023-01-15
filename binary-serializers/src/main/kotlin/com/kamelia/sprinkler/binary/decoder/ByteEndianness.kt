package com.kamelia.sprinkler.binary.decoder

enum class ByteEndianness {
    BIG_ENDIAN,
    LITTLE_ENDIAN
    ;

    val isBigEndian: Boolean
        get() = this == BIG_ENDIAN

}
