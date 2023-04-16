package com.kamelia.sprinkler.codec.binary.encoder.composer

import com.kamelia.sprinkler.codec.binary.encoder.BooleanEncoder
import com.kamelia.sprinkler.codec.binary.encoder.ByteEncoder
import com.kamelia.sprinkler.codec.binary.encoder.DoubleEncoder
import com.kamelia.sprinkler.codec.binary.encoder.FloatEncoder
import com.kamelia.sprinkler.codec.binary.encoder.IntEncoder
import com.kamelia.sprinkler.codec.binary.encoder.LongEncoder
import com.kamelia.sprinkler.codec.binary.encoder.ShortEncoder
import com.kamelia.sprinkler.codec.binary.encoder.core.Encoder
import com.kamelia.sprinkler.codec.binary.encoder.core.EncoderOutput
import com.kamelia.sprinkler.codec.binary.encoder.toArray
import com.kamelia.sprinkler.codec.binary.encoder.toCollection
import com.kamelia.sprinkler.codec.binary.encoder.toIterable
import com.kamelia.sprinkler.codec.binary.encoder.toOptional
import com.zwendo.restrikt.annotation.PackagePrivate
import java.nio.ByteOrder


@PackagePrivate
internal class EncodingScopeImpl<E>(
    private val output: EncoderOutput,
    private val globalStack: ArrayList<() -> Unit>,
    private val recursionQueue: ArrayDeque<() -> Unit>,
    private val encoderMap: HashMap<Class<*>, Encoder<*>>,
    private val endianness: ByteOrder,
    inner: Encoder<E>,
) : EncodingScope<E> {

    private var selfQueue = ArrayList<() -> Unit>()

    private var recurse = false

    override val self: Encoder<E> = Encoder { obj, output ->
        recursionQueue.add { inner.encode(obj, output) } // add to recursion queue

        // after this point, recursion fields cannot be null
        if (recurse) return@Encoder

        recurse = true // set recursion flag to queue next encodings
        globalStack.add {
            for (i in selfQueue.size - 1 downTo 0) {
                globalStack += selfQueue[i]
            }
        }
    }

    @JvmName("encodeWith")
    @Suppress("INAPPLICABLE_JVM_NAME")
    override fun <T> encode(obj: T, encoder: Encoder<T>): EncodingScope<E> = apply {
        if (recurse) {
            selfQueue += { encoder.encode(obj, output) }
        } else {
            encoder.encode(obj, output)
        }
    }

    override fun encode(obj: Byte): EncodingScope<E> = encodeWithComputed(obj, ::ByteEncoder)

    override fun encode(obj: Short): EncodingScope<E> = encodeWithComputed(obj) { ShortEncoder(endianness) }

    override fun encode(obj: Int): EncodingScope<E> = encodeWithComputed(obj) { IntEncoder(endianness) }

    override fun encode(obj: Long): EncodingScope<E> = encodeWithComputed(obj) { LongEncoder(endianness) }

    override fun encode(obj: Float): EncodingScope<E> = encodeWithComputed(obj) { FloatEncoder(endianness) }

    override fun encode(obj: Double): EncodingScope<E> = encodeWithComputed(obj) { DoubleEncoder(endianness) }

    override fun encode(obj: Boolean): EncodingScope<E> = encodeWithComputed(obj, ::BooleanEncoder)

    override fun encode(obj: String): EncodingScope<E> = encodeWithComputed(obj) {
        throw AssertionError("A String encoder should always be present")
    }

    override fun encode(obj: Array<E>): EncodingScope<E> = encode(obj, computed { IntEncoder(endianness) })

    override fun encode(obj: Array<E>, sizeEncoder: Encoder<Int>): EncodingScope<E> =
        encode(obj, self.toArray(sizeEncoder))

    override fun encode(obj: Iterable<E>, endMarker: E): EncodingScope<E> =
        encode(obj, self.toIterable(endMarker))

    override fun encode(obj: Collection<E>): EncodingScope<E> = encode(obj, computed { IntEncoder(endianness) })

    override fun encode(obj: Collection<E>, sizeEncoder: Encoder<Int>): EncodingScope<E> =
        encode(obj, self.toCollection(sizeEncoder))

    override fun encode(obj: E?): EncodingScope<E> = encode(obj, computed(::BooleanEncoder))

    override fun encode(obj: E?, nullabilityEncoder: Encoder<Boolean>): EncodingScope<E> =
        encode(obj, self.toOptional(nullabilityEncoder))

    private inline fun <reified T> encodeWithComputed(obj: T, crossinline block: () -> Encoder<T>): EncodingScope<E> {
        @Suppress("UNCHECKED_CAST")
        val encoder = encoderMap.computeIfAbsent(T::class.java) { block() } as Encoder<T>
        return encode(obj, encoder)
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T> computed(crossinline block: () -> Encoder<T>): Encoder<T> =
        encoderMap.computeIfAbsent(T::class.java) { block() } as Encoder<T>

}
