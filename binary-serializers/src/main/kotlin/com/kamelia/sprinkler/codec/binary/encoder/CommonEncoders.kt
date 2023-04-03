@file:JvmName("Encoders")
@file:JvmMultifileClass

package com.kamelia.sprinkler.codec.binary.encoder

import com.kamelia.sprinkler.codec.binary.encoder.core.Encoder
import java.util.*


@JvmOverloads
fun UUIDEncoder(longEncoder: Encoder<Long> = LongEncoder()): Encoder<UUID> = TODO()
