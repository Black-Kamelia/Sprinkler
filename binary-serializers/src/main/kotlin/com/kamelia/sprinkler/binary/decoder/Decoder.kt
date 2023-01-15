package com.kamelia.sprinkler.binary.decoder

import java.io.InputStream
import java.nio.ByteBuffer
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract


interface Decoder<out T> {

    fun decode(input: DecoderDataInput): State<T>

    fun decode(input: InputStream): State<T> = decode(DecoderDataInput.from(input))

    fun decode(input: ByteBuffer): State<T> = decode(DecoderDataInput.from(input))

    fun forceDecode(input: DecoderDataInput): T = when (val state = decode(input)) {
        is State.Done -> state.value
        is State.Error -> throw state.error
        is State.Processing -> throw MissingBytesException()
    }

    fun forceDecode(input: InputStream): T = forceDecode(DecoderDataInput.from(input))

    fun forceDecode(input: ByteBuffer): T = forceDecode(DecoderDataInput.from(input))

    fun reset()

    fun compose(sideEffect: (T) -> Unit): DecoderComposer.Intermediate = DecoderComposer
        .new(NoOpDecoder)
        .then(this, sideEffect)

    sealed class State<out T> {

        class Error(val error: Throwable) : State<Nothing>()

        object Processing : State<Nothing>()

        class Done<T>(val value: T) : State<T>()

        inline fun <R> map(block: (T) -> R): State<R> = when (this) {
            is Done -> Done(block(value))
            else -> @Suppress("UNCHECKED_CAST") (this as State<R>)
        }

        inline fun <R> mapValueOrNull(action: (T) -> R): R? =
            if (this is Done) {
                action(value)
            } else {
                null
            }

        @OptIn(ExperimentalContracts::class)
        fun isDone(): Boolean {
            contract {
                returns(true) implies (this@State is Done<T>)
            }
            return this is Done<T>
        }

        @OptIn(ExperimentalContracts::class)
        fun isError(): Boolean {
            contract {
                returns(true) implies (this@State is Error)
            }
            return this is Error
        }

        fun isNotDone(): Boolean = !isDone()
    }

}


