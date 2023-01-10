package com.kamelia.sprinkler.binary.decoder.stream

import java.nio.ByteBuffer
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract


interface StreamDeserializer<out T> {

    fun deserialize(bytes: ByteBuffer): State<T>

    fun reset()

    sealed class State<out T> {

        class Error(val error: Throwable) : State<Nothing>()

        object Processing : State<Nothing>()

        class Done<T>(provider: () -> T) : State<T>() {

            val value: T by lazy { provider() }
        }

        @OptIn(ExperimentalContracts::class)
        fun isDone(): Boolean {
            contract {
                returns(true) implies (this@State is Done)
            }
            return this is Done
        }

        @OptIn(ExperimentalContracts::class)
        fun isError(): Boolean {
            contract {
                returns(true) implies (this@State is Error)
            }
            return this is Error
        }
    }
}
