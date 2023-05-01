package com.kamelia.sprinkler.codec.binary.decoder.composer

import com.kamelia.sprinkler.codec.binary.decoder.BooleanDecoder
import com.kamelia.sprinkler.codec.binary.decoder.ByteDecoder
import com.kamelia.sprinkler.codec.binary.decoder.DoubleDecoder
import com.kamelia.sprinkler.codec.binary.decoder.FloatDecoder
import com.kamelia.sprinkler.codec.binary.decoder.IntDecoder
import com.kamelia.sprinkler.codec.binary.decoder.LongDecoder
import com.kamelia.sprinkler.codec.binary.decoder.ShortDecoder
import com.kamelia.sprinkler.codec.binary.decoder.core.Decoder
import com.kamelia.sprinkler.codec.binary.decoder.core.DecoderInput
import com.kamelia.sprinkler.codec.binary.decoder.toCollection
import com.zwendo.restrikt.annotation.PackagePrivate
import java.nio.ByteOrder
import java.util.stream.Collector

@PackagePrivate
internal class DecodingScopeImpl<E>(
    private val input: DecoderInput,
    private val accumulator: ElementsAccumulator,
    private val cache: HashMap<Class<*>, Decoder<*>>,
    private val endianness: ByteOrder,
    private var reset: Boolean,
) : DecodingScope<E> {

    private var currentIndex = 0

    lateinit var processingReason: String
        private set

    private lateinit var selfStateless: Decoder<E>
    private lateinit var selfStateful: Decoder<E>

    override fun self(usedInScope: Boolean): Decoder<E> = if (usedInScope) {
        if (!::selfStateless.isInitialized) {
            selfStateless = SelfDecoder(accumulator, false)
        }
        selfStateless
    } else {
        if (!::selfStateful.isInitialized) {
            selfStateful = SelfDecoder(accumulator, true)
        }
        selfStateful
    }

    override fun <T> decode(decoder: Decoder<T>): T = if (currentIndex < accumulator.size) { // already decoded
        accumulator[currentIndex++].tryCast()
    } else { // decode
        currentIndex++
        if (reset) {
            reset = false
            decoder.reset()
        }
        when (val value = decoder.decode(input)) {
            is Decoder.State.Done -> value.value.also(accumulator::add)
            is Decoder.State.Error -> throw value.error
            is Decoder.State.Processing -> {
                processingReason = "Near $decoder: ${value.reason}"
                throw ProcessingMarker
            }
        }
    }

    override fun <T> oncePerObject(block: () -> T): T = if (currentIndex < accumulator.size) { // already decoded
        accumulator[currentIndex++].tryCast()
    } else { // decode
        currentIndex++
        block().also(accumulator::add)
    }

    @JvmName("decodeByte")
    @Suppress("INAPPLICABLE_JVM_NAME")
    override fun byte(): Byte = decodeWithComputed(::ByteDecoder)

    @JvmName("decodeByte")
    @Suppress("INAPPLICABLE_JVM_NAME")
    override fun short(): Short = decodeWithComputed { ShortDecoder(endianness) }

    @JvmName("decodeByte")
    @Suppress("INAPPLICABLE_JVM_NAME")
    override fun int(): Int = decodeWithComputed { IntDecoder(endianness) }

    @JvmName("decodeByte")
    @Suppress("INAPPLICABLE_JVM_NAME")
    override fun long(): Long = decodeWithComputed { LongDecoder(endianness) }

    @JvmName("decodeByte")
    @Suppress("INAPPLICABLE_JVM_NAME")
    override fun float(): Float = decodeWithComputed { FloatDecoder(endianness) }

    @JvmName("decodeByte")
    @Suppress("INAPPLICABLE_JVM_NAME")
    override fun double(): Double = decodeWithComputed { DoubleDecoder(endianness) }

    @JvmName("decodeByte")
    @Suppress("INAPPLICABLE_JVM_NAME")
    override fun boolean(): Boolean = decodeWithComputed(::BooleanDecoder)

    @JvmName("decodeString")
    @Suppress("INAPPLICABLE_JVM_NAME")
    override fun string(): String = decodeWithComputed {
        throw AssertionError("A String decoder should always be present")
    }

    @JvmName("decodeSelfOrNull")
    @Suppress("INAPPLICABLE_JVM_NAME")
    override fun selfOrNull(nullabilityDecoder: Decoder<Boolean>): E? {
        val isPresent = decode(nullabilityDecoder)
        return if (isPresent) {
            decode(self())
        } else {
            null
        }
    }

    @JvmName("decodeSelfOrNull")
    @Suppress("INAPPLICABLE_JVM_NAME")
    override fun selfOrNull(): E? = selfOrNull(computed(::BooleanDecoder))

    @JvmName("decodeSelfCollection")
    @Suppress("INAPPLICABLE_JVM_NAME")
    override fun <R : Collection<E>> selfCollection(collector: Collector<E, *, R>): R {
        val decoder = oncePerObject { self().toCollection(collector) }
        return decode(decoder)
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T> decodeWithComputed(noinline block: () -> Decoder<T>): T {
        val decoder = cache.computeIfAbsent(T::class.java) { block() } as Decoder<T>
        return decode(decoder)
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T> computed(noinline block: () -> Decoder<T>): Decoder<T> =
        cache.computeIfAbsent(T::class.java) { block() } as Decoder<T>

    private class SelfDecoder<E>(
        private val accumulator: ElementsAccumulator,
        private var keepProgress: Boolean,
    ) : Decoder<E> {

        override fun decode(input: DecoderInput): Decoder.State<E> =
            if (keepProgress && accumulator.hasRecursionElement()) {
                Decoder.State.Done(accumulator.getFromRecursion().tryCast<E>())
            } else {
                throw RecursionMarker // always throw
            }

        override fun reset() = Unit

    }

}
