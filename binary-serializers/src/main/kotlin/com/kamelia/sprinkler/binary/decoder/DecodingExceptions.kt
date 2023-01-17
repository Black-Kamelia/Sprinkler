package com.kamelia.sprinkler.binary.decoder

import com.kamelia.sprinkler.binary.CommunicationException

class MissingBytesException(message: String = "Not enough bytes to read.") : CommunicationException(message)
