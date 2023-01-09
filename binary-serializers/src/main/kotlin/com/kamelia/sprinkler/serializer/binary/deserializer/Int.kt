package com.kamelia.sprinkler.serializer.binary.deserializer

import com.kamelia.sprinkler.serializer.binary.ByteStream


object IntDeserializer : NumberDeserializer<Int>(Int.SIZE_BYTES.toByte()) {

    override fun mapper(bytes: ByteStream): Int = bytes.nextInt()

}
