package com.kamelia.sprinkler.binary.encoder

class EncodingAccumulator {
    private val bytes = mutableListOf<Byte>()

    fun addByte(byte: Byte) {
        bytes.add(byte)
    }

    fun addBytes(bytes: ByteArray) {
        this.bytes.addAll(bytes.toList())
    }

    fun toByteArray(): ByteArray = bytes.toByteArray()

    override fun toString(): String = bytes.toString()
}
