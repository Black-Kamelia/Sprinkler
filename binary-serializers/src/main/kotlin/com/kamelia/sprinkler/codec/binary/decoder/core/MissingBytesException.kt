package com.kamelia.sprinkler.codec.binary.decoder.core

/**
 * Thrown when there are not enough bytes to read.
 *
 * @constructor Creates a [MissingBytesException] with the given [message].
 * @param message the message to use in the [RuntimeException]
 */
class MissingBytesException(message: String = "Not enough bytes to read.") : RuntimeException(message)
