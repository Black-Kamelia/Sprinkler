package com.kamelia.sprinkler.codec.binary.decoder.core

import com.kamelia.sprinkler.codec.binary.decoder.core.Decoder.State
import com.kamelia.sprinkler.codec.binary.decoder.core.Decoder.State.Done
import com.kamelia.sprinkler.codec.binary.decoder.core.Decoder.State.Error
import com.kamelia.sprinkler.codec.binary.decoder.core.Decoder.State.Processing
import java.io.InputStream
import java.nio.ByteBuffer

/**
 * Represents an object that can convert a stream of bytes into an object of type [T]. A decoder is stateful and can
 * therefore decode an object in multiple steps.
 *
 * Here is quick example of how to use a decoder:
 *
 * ```
 * fun myDecoding(decoder: Decoder<Byte>) {
 *     val input = FileInputStream("file") // a binary file containing the byte 5
 *     val value = decoder.decode(input).get()
 *     println(value) // prints 5
 * }
 * ```
 *
 * The [decode] method is the entry point for decoding. It takes a [DecoderInput] and returns a [State] object.
 * The [State] object aims to represent the state of the decoding process. It can be one of the following:
 *
 * - [State.Done] - the decoding process is complete and the decoded object can be accessed via the
 * [value][State.Done.value] property.
 * - [State.Processing] - the decoding process is not complete and more bytes are needed. The [reason][State.Processing.reason]
 * property can be used to know why more bytes are needed.
 * - [State.Error] - the decoding process has failed and the [error][State.Error.error] property can be used to access
 * the exception that caused the failure.
 *
 * The internal state of the decoder is automatically reset when a [State.Done] object is returned.
 * However, this reset may be partial. For example, a decoder accumulating bytes in an internal buffer will clear the contents of the
 * buffer but the reference will remain the same, meaning that the decoder might contain a reference to a buffer with a
 * large capacity even if it is not needed anymore. This is behavior done to avoid unnecessary allocations.
 *
 * To completely reset the decoder, the [reset] method can be called. It can be called at any time, even if the decoder
 * is not in a [State.Done] state (e.g. an error occurred and the decoder must be reset).
 *
 * @param T the type of the object to be decoded.
 * @see DecoderInput
 * @see State
 */
interface Decoder<out T> {

    /**
     * Tries to decode an object of type [T] from the given [DecoderInput] [input].
     *
     * @param input the input from which to decode the object
     * @return a [State] object representing the state of the decoding process
     */
    fun decode(input: DecoderInput): State<T>

    /**
     * Tries to decode an object of type [T] from the given [InputStream] [input].
     *
     * @param input the input from which to decode the object
     * @return a [State] object representing the state of the decoding process
     */
    fun decode(input: InputStream): State<T> = decode(DecoderInput.from(input))

    /**
     * Tries to decode an object of type [T] from the given [ByteBuffer] [input].
     *
     * @param input the input from which to decode the object
     * @return a [State] object representing the state of the decoding process
     */
    fun decode(input: ByteBuffer): State<T> = decode(DecoderInput.from(input))

    /**
     * Tries to decode an object of type [T] from the given [ByteArray] [input].
     *
     * @param input the input from which to decode the object
     * @return a [State] object representing the state of the decoding process
     */
    fun decode(input: ByteArray): State<T> = decode(DecoderInput.from(input))

    /**
     * Resets the internal state of the decoder. This method can be called at any time, even if the decoder is not in a
     * [State.Done] state.
     */
    fun reset()

    /**
     * Represents the state of the decoding process of a [Decoder], returned by the [decode] method, which can be:
     * - [Done] when the decoding process has completed successfully
     * - [Processing] when the decoding process is not complete and more bytes are needed
     * - [Error] when the decoding process has failed
     *
     * For more information about the decoding process, see [Decoder] documentation.
     *
     * @see Decoder
     * @see Done
     * @see Processing
     * @see Error
     */
    sealed class State<out T> {

        /**
         * State returned when the decoding process has been completed successfully. The decoded object can be accessed
         * via the [value] property.
         *
         * @param T the type of the decoded object
         * @constructor Creates a new [Done] state with the given factory function.
         * @param factory a function that returns the decoded object
         */
        class Done<T>(factory: () -> T) : State<T>() {

            private val lazyField = lazy(LazyThreadSafetyMode.NONE, factory)

            /**
             * The decoded object.
             */
            val value: T
                get() = lazyField.value

            /**
             * Creates a new [Done] state with the given [value].
             *
             * @param value the decoded object
             */
            constructor(value: T) : this({ value }) {
                lazyField.value // force initialization
            }

            override fun toString(): String {
                val v = if (lazyField.isInitialized()) {
                    lazyField.value.toString()
                } else {
                    "not initialized"
                }
                return "Done($v)"
            }

        }

        /**
         * State returned when the decoding process has not been completed yet. More bytes are needed.
         */
        object Processing : State<Nothing>() {

            override fun toString(): String = "Processing"

        }

        /**
         * State returned when the decoding process has failed. The exception that caused the failure can be accessed
         * via the [error] property.
         *
         * @constructor Creates a new [Error] state with the given [error].
         * @param error the [Throwable] that caused the failure
         * @property error The [Throwable] that caused the failure.
         */
        class Error(val error: Throwable) : State<Nothing>() {

            /**
             * Creates a new [Error] state with the given [message]. The exception will be an [IllegalStateException].
             *
             * @param message the message of the exception
             */
            constructor(message: String) : this(IllegalStateException(message))

            override fun toString(): String = "Error(${error.message})"

        }

        /**
         * Maps the value of this [State] to a new [State] using the given [block] function. The [block] function is
         * invoked only if this [State] is a [Done] state.
         *
         * @param block the function to map the value of this [State]
         * @return a new [State] with the mapped value
         * @param R the type of the new [State]
         */
        inline fun <R> mapState(block: (T) -> State<R>): State<R> = when (this) {
            is Done -> block(value)
            else -> mapEmptyState()
        }

        /**
         * Maps the value of this [State] to another value using the given [block] function. The [block] function is
         * invoked only if this [State] is a [Done] state.
         *
         * @param block the function to map the value of this [State]
         * @return a new [State] with the mapped value
         * @param R the type of the new [State]
         */
        inline fun <R> mapResult(block: (T) -> R): State<R> = mapState { Done(block(it)) }

        /**
         * Casts this [State] to a [State] of type [R]. This method is useful to propagate an [Error] or [Processing]
         * state without having to cast it manually.
         *
         * @return this [State], cast to a [State] of type [R]
         * @param R the type of the new [State]
         * @throws IllegalStateException if this [State] is a [Done] state
         */
        fun <R> mapEmptyState(): State<R> = if (this is Done) {
            throw IllegalStateException("Cannot map change type of Done state ($this).")
        } else {
            @Suppress("UNCHECKED_CAST")
            this as State<R>
        }

        /**
         * Returns `true` if this [State] is a [Done] state, `false` otherwise.
         */
        fun isDone(): Boolean = this is Done<T>

        /**
         * Returns `true` if this [State] is not a [Done] state, `false` otherwise.
         */
        fun isNotDone(): Boolean = this !is Done<T>

        /**
         * Returns the decoded value if this [State] is a [Done] state, otherwise throws an exception.
         *
         * @return the decoded value if this [State] is a [Done] state
         * @throws MissingBytesException if this [State] is a [Processing] state
         * @throws Throwable if this [State] is an [Error] state, the exception that caused the failure
         */
        fun get(): T = when (this) {
            is Done -> value
            is Processing -> throw MissingBytesException()
            is Error -> throw error
        }

        /**
         * Returns the decoded value if this [State] is a [Done] state, otherwise returns `null`.
         *
         * @return the decoded value if this [State] is a [Done] state, `null` otherwise
         */
        fun getOrNull(): T? = when (this) {
            is Done -> value
            else -> null
        }

        /**
         * Returns the decoded value if this [State] is a [Done] state, otherwise returns the given [default].
         *
         * @param default the default value to return if this [State] is not a [Done] state
         * @return the decoded value if this [State] is a [Done] state, otherwise returns the given [default]
         */
        fun getOrElse(default: @UnsafeVariance T): T = when (this) {
            is Done -> value
            else -> default
        }

        /**
         * Returns the decoded value if this [State] is a [Done] state, otherwise returns the value returned by the
         * given [default] factory.
         *
         * @param default the factory to return the default value if this [State] is not a [Done] state
         * @return the decoded value if this [State] is a [Done] state, otherwise returns the value returned by the
         * given [default] factory
         */
        fun getOrElse(default: () -> @UnsafeVariance T): T = when (this) {
            is Done -> value
            else -> default()
        }

        /**
         * Returns the decoded value if this [State] is a [Done] state, otherwise throws the exception returned by the
         * given [throwable] factory.
         *
         * @param throwable the factory to return the exception to throw if this [State] is not a [Done] state
         * @return the decoded value if this [State] is a [Done] state
         */
        fun getOrThrow(throwable: () -> Throwable): T = when (this) {
            is Done -> value
            else -> throw throwable()
        }

        /**
         * Executes the given [block] function if this [State] is a [Done] state.
         *
         * @param block the function to execute if this [State] is a [Done] state
         * @return this [State]
         */
        fun ifDone(block: (T) -> Unit): State<T> = apply {
            if (this is Done) {
                block(value)
            }
        }

    }

}
