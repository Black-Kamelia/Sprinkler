package com.kamelia.sprinkler.binary.decoder.core

class MissingBytesException(message: String = "Not enough bytes to read.") : RuntimeException(message)
