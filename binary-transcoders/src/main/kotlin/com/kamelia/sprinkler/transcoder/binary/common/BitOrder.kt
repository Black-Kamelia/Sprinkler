package com.kamelia.sprinkler.transcoder.binary.common

/**
 * The order in which bits are read from a byte.
 */
enum class BitOrder {

    /**
     * Most significant bit first.
     */
    MSB_FIRST,

    /**
     * Least significant bit first.
     */
//    LSB_FIRST,
    ;

    /**
     * Returns `true` if this is [MSB_FIRST].
     *
     * @return `true` if this is [MSB_FIRST]
     */
//    val isMsbFirst: Boolean
//        get() = this === MSB_FIRST

}
