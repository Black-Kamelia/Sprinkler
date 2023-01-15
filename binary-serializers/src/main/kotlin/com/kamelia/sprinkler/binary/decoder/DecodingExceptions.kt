package com.kamelia.sprinkler.binary.decoder

import com.kamelia.sprinkler.binary.CommunicationException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

open class DecodingException(message: String) : CommunicationException(message)

@OptIn(ExperimentalContracts::class)
inline fun checkDecoding(value: Boolean, lazyMessage: () -> Any) {
    contract {
        returns() implies value
    }
    if (!value) {
        val message = lazyMessage()
        throw DecodingException(message.toString())
    }
}

class MissingBytesException(message: String = "Not enough bytes to read.") : CommunicationException(message)
