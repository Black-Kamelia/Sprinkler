package com.kamelia.sprinkler.serializer.binary

import com.kamelia.sprinkler.serializer.binary.StreamDeserializer.State.Done
import com.kamelia.sprinkler.serializer.binary.StreamDeserializer.State.Error
import com.kamelia.sprinkler.serializer.binary.StreamDeserializer.State.Processing
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

interface Deserializer<out T> {
    fun deserialize(bytes: BinaryBuffer): T
}

interface StreamDeserializer<out T> : Deserializer<StreamDeserializer.State<T>> {

    override fun deserialize(bytes: BinaryBuffer): State<T>

    fun reset()

    sealed class State<out T> {

        class Error(val error: Throwable): State<Nothing>()

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


fun main() {
    val state: StreamDeserializer.State<Int> = Done { 1 }

    when (state) {
        is Done -> {
            val a = state.value
        }
        is Error -> {
            val e = state.error
        }
        is Processing -> {
            // yep
        }
    }
}