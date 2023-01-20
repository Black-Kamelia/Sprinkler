package com.kamelia.sprinkler.binary.decoder.composer

import com.kamelia.sprinkler.binary.decoder.Decoder
import com.kamelia.sprinkler.binary.decoder.DecoderCollector
import com.kamelia.sprinkler.binary.decoder.DecoderDataInput
import com.zwendo.restrikt.annotation.PackagePrivate
import kotlin.jvm.internal.Ref.ObjectRef

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

                return accumulate(input)
            }

            override fun reset() {
                inner.reset()
                clear()
            }

        }.let { DecoderComposerImpl(it, context) }
    }

    override fun <R> andFinally(block: (T) -> R): DecoderComposer<R, D> =
        DecoderComposerImpl(finallyDecoder(block), context)

    override fun skip(amount: Long): DecoderComposerImpl<T, D> {
        require(amount >= 0) { "Amount must be >= 0, but was $amount" }
        return object : Decoder<T> {
            private var leftToSkip = amount
            private var result: ObjectRef<T>? = null

            override fun decode(input: DecoderDataInput): Decoder.State<T> {
                if (result == null) {
                    when (val state = inner.decode(input)) {
                        is Decoder.State.Done -> {
                            result = ObjectRef<T>()
                            result!!.element = state.value
                            leftToSkip = amount
                        }
                        else -> return state.mapEmptyState()
                    }
                }

                if (leftToSkip > 0) {
                    leftToSkip -= input.skip(leftToSkip)
                }
                return if (leftToSkip == 0L) {
                    Decoder.State.Done(result!!.element).also { result = null }
                } else {
                    Decoder.State.Processing()
                }
            }

            override fun reset() {
                inner.reset()
                leftToSkip = amount
                result = null
            }

        }.let { DecoderComposerImpl(it, context) }
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
                when (val state = inner.decode(input)) {
                    is Decoder.State.Done -> nextReader = block(state.value)
                    else -> return state.mapEmptyState()
                }
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

    inline fun <R> finallyDecoder(crossinline block: (T) -> R): Decoder<R> = object : Decoder<R> {

        override fun decode(input: DecoderDataInput): Decoder.State<R> = inner
            .decode(input)
            .map(block)
            .ifDone { context?.clear() }

        override fun reset() = inner.reset()

    }

    companion object {

        fun <T> create(decoder: Decoder<T>): DecoderComposerImpl<T, Unit> = DecoderComposerImpl(decoder, null)

        fun <T> createWithContext(decoder: Decoder<T>): DecoderComposerImpl<T, Context0> =
            DecoderComposerImpl(decoder, ArrayList())

        fun <T, D> create(previous: DecoderComposerImpl<*, *>, decoder: Decoder<T>): DecoderComposerImpl<T, D> =
            DecoderComposerImpl(decoder, previous.context)

    }

}

