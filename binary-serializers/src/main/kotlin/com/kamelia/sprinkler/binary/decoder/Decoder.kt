package com.kamelia.sprinkler.binary.decoder

import com.kamelia.sprinkler.binary.decoder.composer.Context0
import com.kamelia.sprinkler.binary.decoder.composer.DecoderComposer
import com.zwendo.restrikt.annotation.HideFromJava
import java.io.InputStream
import java.nio.ByteBuffer


interface Decoder<out T> {

    fun decode(input: DecoderDataInput): State<T>

    fun decode(input: InputStream): State<T> = decode(DecoderDataInput.from(input))

    fun decode(input: ByteBuffer): State<T> = decode(DecoderDataInput.from(input))

    fun decode(input: ByteArray): State<T> = decode(DecoderDataInput.from(input))

    fun reset()

    @HideFromJava
    @JvmName("composeWithContext")
    @Suppress("INAPPLICABLE_JVM_NAME")
    fun compose(): DecoderComposer<T, Context0> = DecoderComposer.create(this)

    @JvmName("compose")
    @Suppress("INAPPLICABLE_JVM_NAME")
    fun composeWithoutContext(): DecoderComposer<T, Nothing> = DecoderComposer.createWithoutContext(this)

    sealed class State<out T> {

        class Error(val error: Throwable) : State<Nothing>() {

            override fun toString(): String = "Error: $error"

        }

        class Processing(val reason: String = "Missing bytes") : State<Nothing>() {

            override fun toString(): String = "Processing: $reason"

        }

        class Done<T> : State<T> {

            private var valueField: T? = null

            private var factory: (() -> T)? = null

            val value: T
                get() = valueField ?: factory!!().also { valueField = it }

            constructor(value: T) : super() {
                valueField = value
            }

            constructor(factory: () -> T) : super() {
                this.factory = factory
            }

            override fun toString(): String = "Done: $value"

        }

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

        inline fun ifDone(block: (T) -> Unit): State<T> = apply {
            if (this is Done) {
                block(value)
            }
        }

    }

}


