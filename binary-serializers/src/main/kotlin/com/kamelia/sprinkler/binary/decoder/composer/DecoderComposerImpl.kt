package com.kamelia.sprinkler.binary.decoder.composer

import com.kamelia.sprinkler.binary.decoder.Decoder
import com.kamelia.sprinkler.binary.decoder.DecoderCollector
import com.kamelia.sprinkler.binary.decoder.DecoderDataInput
import com.zwendo.restrikt.annotation.PackagePrivate
import kotlin.collections.ArrayList

@PackagePrivate
internal class DecoderComposerImpl<T, D> private constructor(
    private val inner: Decoder<T>,
    internal val context: MutableList<Any>?,
) : DecoderComposer<T, D> {

    override fun <R> map(block: (T) -> Decoder<R>): DecoderComposerImpl<R, D> =
        DecoderComposerImpl(mapDecoder(block), context)

    override fun <C, R> repeat(
        collector: DecoderCollector<C, T, R>,
        sizeDecoder: Decoder<Number>,
    ): DecoderComposerImpl<R, D> = object : RepeatDecoder<C, T, R>(inner, collector), Decoder<R> {

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

    }.let { DecoderComposerImpl(it, context) }

    override fun <C, R> repeat(
        times: Int,
        collector: DecoderCollector<C, T, R>,
    ): DecoderComposerImpl<R, D> {
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

        }.let { DecoderComposerImpl(it, context) }
    }

    override fun <R> finally(block: (T) -> R): DecoderComposer<R, D> {
        TODO("Not yet implemented")
    }

    override fun skip(amount: Long): DecoderComposerImpl<T, D> {
        require(amount >= 0) { "Amount must be >= 0, but was $amount" }
        return SkipDecoder(inner, amount).let { DecoderComposerImpl(it, context) }
    }

    override fun <C, R> repeat(
        predicate: (T) -> Boolean,
        collector: DecoderCollector<C, T, R>,
        addLast: Boolean,
    ): DecoderComposerImpl<R, D> = object : Decoder<R> {
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

    }.let { DecoderComposerImpl(it, context) }

    override fun assemble(): Decoder<T> = inner

    fun <R> mapDecoder(block: (T) -> Decoder<R>): Decoder<R> = object : Decoder<R> {
        private var nextReader: Decoder<R>? = null

        override fun decode(input: DecoderDataInput): Decoder.State<R> {
            if (nextReader == null) {
                val state = inner.decode(input)
                if (state.isNotDone()) {
                    return state.mapEmptyState()
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

    fun <R> finallyDecoder(block: (T) -> R): Decoder<R> = object : Decoder<R> {

        override fun decode(input: DecoderDataInput): Decoder.State<R> = inner.decode(input).map(block)

        override fun reset() = inner.reset()

    }

    companion object {

        fun <T> create(decoder: Decoder<T>): DecoderComposerImpl<T, Nothing> = DecoderComposerImpl(decoder, null)

        fun <T> createWithContext(decoder: Decoder<T>): DecoderComposerImpl<T, Context0> =
            DecoderComposerImpl(decoder, ArrayList())

        fun <T, D> create(previous: DecoderComposerImpl<*, *>, decoder: Decoder<T>): DecoderComposerImpl<T, D> =
            DecoderComposerImpl(decoder, previous.context)

    }

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
