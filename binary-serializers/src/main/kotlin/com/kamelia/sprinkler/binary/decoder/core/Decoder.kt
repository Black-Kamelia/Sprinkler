package com.kamelia.sprinkler.binary.decoder.core

import java.io.InputStream
import java.nio.ByteBuffer


interface Decoder<out T> {

    fun decode(input: DecoderDataInput): State<T>

    fun decode(input: InputStream): State<T> = decode(DecoderDataInput.from(input))

    fun decode(input: ByteBuffer): State<T> = decode(DecoderDataInput.from(input))

    fun decode(input: ByteArray): State<T> = decode(DecoderDataInput.from(input))

    fun reset()

    sealed class State<out T> {

        class Error(val error: Throwable) : State<Nothing>() {

            override fun toString(): String = "Error($error)"

        }

        class Processing(val reason: String = "Missing bytes") : State<Nothing>() {

            override fun toString(): String = "Processing($reason)"

        }

        class Done<T>(factory: () -> T) : State<T>() {

            val value: T by lazy(factory)

            constructor(value: T) : this({ value })

            override fun toString(): String = "Done($value)"

        }

        inline fun <R> mapResult(block: (T) -> R): State<R> = when (this) {
            is Done -> Done(block(value))
            else -> mapEmptyState()
        }

        inline fun <R> mapState(block: (T) -> State<R>): State<R> = when (this) {
            is Done -> block(value)
            else -> mapEmptyState()
        }

        fun <R> mapEmptyState(): State<R> = if (this is Done) {
            throw IllegalStateException("Cannot map change type of Done state ($this).")
        } else {
            @Suppress("UNCHECKED_CAST")
            this as State<R>
        }

        fun isDone(): Boolean = this is Done<T>

        fun isNotDone(): Boolean = this !is Done<T>

        fun get(): T = when (this) {
            is Done -> value
            is Processing -> throw MissingBytesException(reason)
            is Error -> throw error
        }

        fun getOrNull(): T? = when (this) {
            is Done -> value
            else -> null
        }

        fun getOrElse(default: @UnsafeVariance T): T = when (this) {
            is Done -> value
            else -> default
        }

        inline fun getOrThrow(throwable: () -> Throwable): T = when (this) {
            is Done -> value
            else -> throw throwable()
        }

        inline fun ifDone(block: (T) -> Unit): State<T> = apply {
            if (this is Done) {
                block(value)
            }
        }

    }

}

