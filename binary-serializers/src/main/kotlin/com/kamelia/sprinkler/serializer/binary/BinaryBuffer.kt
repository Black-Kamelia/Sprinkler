package com.kamelia.sprinkler.serializer.binary

import java.nio.ByteBuffer

interface BinaryBuffer

@JvmInline
value class ByteBinaryBuffer(val value: ByteBuffer) : BinaryBuffer
