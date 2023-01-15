package com.kamelia.sprinkler.binary.decoder

import java.io.InputStream
import java.nio.ByteBuffer


interface Decoder<out T> {

    fun decode(input: DecoderDataInput): State<T>

    fun decode(input: InputStream): State<T> = decode(DecoderDataInput.from(input))

    fun decode(input: ByteBuffer): State<T> = decode(DecoderDataInput.from(input))

    fun decode(input: ByteArray): State<T> = decode(DecoderDataInput.from(input))

    fun forceDecode(input: DecoderDataInput): T = when (val state = decode(input)) {
        is State.Done -> state.value
        is State.Error -> throw state.error
        is State.Processing -> throw MissingBytesException()
    }

    fun forceDecode(input: InputStream): T = forceDecode(DecoderDataInput.from(input))

    fun forceDecode(input: ByteBuffer): T = forceDecode(DecoderDataInput.from(input))

    fun forceDecode(input: ByteArray): T = forceDecode(DecoderDataInput.from(input))

    fun reset()

    fun compose(): DecoderComposer<T> = DecoderComposer(this)

    sealed class State<out T> {

        class Error(val error: Throwable) : State<Nothing>() {

            override fun toString(): String = "Error($error)"

        }

        class Processing(val reason: String = "Missing bytes") : State<Nothing>() {

            override fun toString(): String = "Processing: $reason"

        }

        data class Done<T>(val value: T) : State<T>()

        inline fun <R> map(block: (T) -> R): State<R> = when (this) {
            is Done -> Done(block(value))
            else -> mapEmptyState()
        }

        fun <R> mapEmptyState(): State<R> = if (this is Done) {
            throw IllegalStateException("Cannot map change type of Done state ($this).")
        } else {
            @Suppress("UNCHECKED_CAST")
            this as State<R>
        }

        fun isDone(): Boolean = this is Done<T>

        fun isNotDone(): Boolean = !isDone()

        fun get(): T {
            if (this !is Done) {
                throw IllegalStateException("Cannot get value from $this.")
            }
            return value
        }

        inline fun ifDone(block: (T) -> Unit): State<T> {
            if (this is Done) {
                block(value)
            }
            return this
        }
    }

}


