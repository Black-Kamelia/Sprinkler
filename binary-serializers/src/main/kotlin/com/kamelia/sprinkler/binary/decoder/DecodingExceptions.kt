package com.kamelia.sprinkler.binary.decoder

import com.kamelia.sprinkler.binary.CommunicationException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

open class DecodingException(message: String) : CommunicationException(message)

class MissingBytesException(message: String = "Not enough bytes to read.") : CommunicationException(message)
