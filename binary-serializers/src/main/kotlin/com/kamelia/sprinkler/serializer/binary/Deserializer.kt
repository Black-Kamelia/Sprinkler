package com.kamelia.sprinkler.serializer.binary

import com.kamelia.sprinkler.serializer.binary.StreamDeserializer.State
import com.kamelia.sprinkler.serializer.binary.StreamDeserializer.State.Processing
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

interface Deserializer<out T> {

    fun deserialize(bytes: ByteStream): T
}

interface StreamDeserializer<out T> {

    fun deserialize(bytes: BinaryBuffer): State<T>

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

abstract class AbstractStreamDeserializer<E> : StreamDeserializer<E> {

    protected var state: State<E> = Processing

    final override fun deserialize(bytes: BinaryBuffer): State<E> {
        if (state.isDone() || state.isError()) return state
        return process(bytes)
    }

    override fun reset() {
        state = Processing
    }

    protected abstract fun process(bytes: BinaryBuffer): State<E>

}
