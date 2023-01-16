package com.kamelia.sprinkler.binary.decoder.composer

import com.kamelia.sprinkler.binary.decoder.Decoder
import com.kamelia.sprinkler.binary.decoder.DecoderCollector
import com.kamelia.sprinkler.binary.decoder.DecoderDataInput
import com.kamelia.sprinkler.binary.decoder.IntDecoder
import com.zwendo.restrikt.annotation.PackagePrivate
import java.util.*
import kotlin.collections.ArrayDeque

abstract class DecoderComposer<out T, D : DecoderComposer<*, D>> private constructor(
    private val inner: Decoder<T>,
    private val context: ArrayDeque<Any>,
) {

    constructor(inner: Decoder<T>, previous: DecoderComposer<*, *>) : this(inner, previous.context)

    @PackagePrivate
    internal constructor(inner: Decoder<T>) : this(inner, ArrayDeque())

    fun <R> map(block: (T) -> Decoder<R>): DecoderComposer<R, D> = mapDecoder(block).let(::factory)

    abstract fun <R> then(nextDecoder: Decoder<R>): DecoderComposer<R, *>

    abstract fun <R> then(nextDecoder: () -> Decoder<R>): DecoderComposer<R, *>

    @JvmOverloads
    fun <C, R> repeat(
        collector: DecoderCollector<C, T, R>,
        sizeDecoder: Decoder<Number> = IntDecoder(),
    ): DecoderComposer<R, D> = object : RepeatDecoder<C, T, R>(inner, collector), Decoder<R> {

        override fun decode(input: DecoderDataInput): Decoder.State<R> {
            if (size == -1) {
                when (val state = sizeDecoder.decode(input)) {
                    is Decoder.State.Done -> init(state.value.toInt())
                    else -> return state.mapEmptyState()
                }
            }

            return accumulate(input)
        }

        override fun reset() {
            inner.reset()
            sizeDecoder.reset()
            clear()
        }

    }.let(::factory)

    @JvmOverloads
    fun repeat(sizeDecoder: Decoder<Number> = IntDecoder()): DecoderComposer<List<T>, D> =
        repeat(DecoderCollector.toList(), sizeDecoder)

    fun <C, R> repeat(
        times: Int,
        collector: DecoderCollector<C, T, R>,
    ): DecoderComposer<R, D> {
        require(times >= 0) { "Times must be >= 0, but was $times" }
        return object : RepeatDecoder<C, T, R>(inner, collector), Decoder<R> {

            override fun decode(input: DecoderDataInput): Decoder.State<R> {
                if (size == -1) {
                    init(times)
                }

                return accumulate(input).mapEmptyState()
            }

            override fun reset() {
                inner.reset()
                clear()
            }

        }.let(::factory)
    }

    fun repeat(times: Int): DecoderComposer<List<T>, D> {
        require(times >= 0) { "Times must be >= 0, but was $times" }
        return repeat(times, DecoderCollector.toList())
    }

    fun skip(amount: Long): DecoderComposer<T, D> {
        require(amount >= 0) { "Amount must be >= 0, but was $amount" }
        return SkipDecoder(inner, amount).let(::factory)
    }

    @JvmOverloads
    fun <C, R> repeat(
        collector: DecoderCollector<C, T, R>,
        predicate: (T) -> Boolean,
        addLast: Boolean = false,
    ): DecoderComposer<R, D> = object : Decoder<R> {
        private var container: C? = null
        private var index = 0

        override fun decode(input: DecoderDataInput): Decoder.State<R> {
            if (container == null) {
                container = collector.supplier(-1)
            }

            val container = container!!
            while (index != -1) {
                when (val state = inner.decode(input)) {
                    is Decoder.State.Done -> {
                        val value = state.value
                        if (predicate(value)) {
                            collector.accumulator(container, value, index)
                            index++
                            continue
                        }

                        if (addLast) {
                            collector.accumulator(container, value, index)
                        }
                        index = -1
                    }
                    else -> return state.mapEmptyState()
                }
            }

            return Decoder.State.Done(collector.finisher(container)).also { this.container = null }
        }

        override fun reset() {
            inner.reset()
            container = null
            index = 0
        }

    }.let(::factory)

    @JvmOverloads
    fun repeat(predicate: (T) -> Boolean, addLast: Boolean = true): DecoderComposer<List<T>, D> =
        repeat(DecoderCollector.toList(), predicate, addLast)

    fun assemble(): Decoder<T> = inner

    private inline fun <T> applyOnImpl(block: (D) -> T): T {
        @Suppress("UNCHECKED_CAST")
        return block(this as D)
    }

    protected abstract fun <R> factory(decoder: Decoder<R>): DecoderComposer<R, D>

    private fun <R> mapDecoder(block: (T) -> Decoder<R>): Decoder<R> = object : Decoder<R> {
        private var nextReader: Decoder<R>? = null

        override fun decode(input: DecoderDataInput): Decoder.State<R> {
            if (nextReader == null) {
                val state = inner.decode(input)
                @Suppress("UNCHECKED_CAST")
                if (state.isNotDone()) {
                    return state as Decoder.State<R>
                }
                val currentResult = (state as Decoder.State.Done<T>).value
                nextReader = block(currentResult)
            }

            return nextReader!!.decode(input).ifDone { nextReader = null }
        }

        override fun reset() {
            inner.reset()
            nextReader?.let {
                it.reset()
                nextReader = null
            }
        }

    }

    protected fun <R> thenDecoder(nextDecoder: Decoder<R>): Decoder<R> = mapDecoder {
        context += it as Any
        nextDecoder
    }

    protected fun <R> thenDecoder(nextDecoder: () -> Decoder<R>): Decoder<R> = mapDecoder {
        context += it as Any
        nextDecoder()
    }

    protected fun <R> finallyDecoder(block: (T) -> R): Decoder<R> = object : Decoder<R> {

        override fun decode(input: DecoderDataInput): Decoder.State<R> = inner.decode(input).map(block)

        override fun reset() = inner.reset()

    }

    protected fun next(): Any = context.removeFirst()

}

private abstract class RepeatDecoder<C, T, R>(
    private val decoder: Decoder<T>,
    private val collector: DecoderCollector<C, T, R>,
) {

    protected var size = -1
        private set
    private var index = 0
    private var container: C? = null

    protected fun accumulate(input: DecoderDataInput): Decoder.State<R> {
        while (index < size) {
            when (val state = decoder.decode(input)) {
                is Decoder.State.Done -> collector.accumulator(container!!, state.value, index++)
                else -> return state.mapEmptyState()
            }
        }

        return Decoder.State.Done(collector.finisher(container!!))
    }

    protected fun init(size: Int) {
        this.size = size
        this.index = 0
        this.container = collector.supplier(size)
    }

    protected fun clear() {
        container = null
        index = 0
        size = -1
    }

}

private class SkipDecoder<T>(private val inner: Decoder<T>, private val size: Long) : Decoder<T> {

    override fun decode(input: DecoderDataInput): Decoder.State<T> =
        inner.decode(input).ifDone { input.skip(size) }

    override fun reset() {
        if (inner !is SkipDecoder) {
            inner.reset()
        }
    }

}
