package com.kamelia.sprinkler.transcoder.binary.decoder.core

/**
 * Thrown when there are not enough bytes to read.
 *
 * @constructor Creates a [MissingBytesException].
 */
class MissingBytesException : RuntimeException("Not enough bytes to read.")
