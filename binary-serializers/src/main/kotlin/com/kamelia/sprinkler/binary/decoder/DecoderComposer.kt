package com.kamelia.sprinkler.binary.decoder

class DecoderComposer<out T>(
    private val inner: Decoder<T>,
) {

    fun <R> map(block: (T) -> Decoder<R>): DecoderComposer<R> = object : Decoder<R> {
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

    }.let(::DecoderComposer)

    inline fun <R> then(nextDecoder: Decoder<R>, crossinline sideEffect: (T) -> Unit): DecoderComposer<R> = map {
        sideEffect(it)
        nextDecoder
    }

    inline fun <R> then(
        crossinline nextDecoder: () -> Decoder<R>,
        crossinline sideEffect: (T) -> Unit,
    ): DecoderComposer<R> = map {
        sideEffect(it)
        nextDecoder()
    }

    fun <R> finally(resultMapper: (T) -> R): DecoderComposer<R> = object : Decoder<R> {

        override fun decode(input: DecoderDataInput): Decoder.State<R> = inner.decode(input).map(resultMapper)

        override fun reset() = inner.reset()

    }.let(::DecoderComposer)

    @JvmOverloads
    fun <C, R> repeat(
        collector: DecoderCollector<C, T, R>,
        sizeDecoder: Decoder<Number> = IntDecoder(),
    ): DecoderComposer<R> = object : RepeatDecoder<C, T, R>(inner, collector), Decoder<R> {

        override fun decode(input: DecoderDataInput): Decoder.State<R> {
            if (size == -1) {
                when (val state = sizeDecoder.decode(input)) {
                    is Decoder.State.Done -> init(state.value.toInt())
                    else -> return state.mapEmptyState()
                }
            }

            return accumulate(input).mapEmptyState()
        }

        override fun reset() {
            inner.reset()
            sizeDecoder.reset()
            clear()
        }

    }.let(::DecoderComposer)

    @JvmOverloads
    fun repeat(sizeDecoder: Decoder<Number> = IntDecoder()): DecoderComposer<List<T>> =
        repeat(DecoderCollector.toList(), sizeDecoder)

    fun <C, R> repeat(
        times: Int,
        collector: DecoderCollector<C, T, R>,
    ): DecoderComposer<R> {
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

        }.let(::DecoderComposer)
    }

    fun repeat(times: Int): DecoderComposer<List<T>> {
        require(times >= 0) { "Times must be >= 0, but was $times" }
        return repeat(times, DecoderCollector.toList())
    }

    fun skip(amount: Long): DecoderComposer<T> {
        require(amount >= 0) { "Amount must be >= 0, but was $amount" }
        return SkipDecoder(inner, amount).let(::DecoderComposer)
    }

    @JvmOverloads
    fun <C, R> repeat(
        collector: DecoderCollector<C, T, R>,
        predicate: (T) -> Boolean,
        addLast: Boolean = false,
    ): DecoderComposer<R> = object : Decoder<R> {
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

                        if(addLast) {
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

    }.let(::DecoderComposer)

    @JvmOverloads
    fun repeat(predicate: (T) -> Boolean, addLast: Boolean = true): DecoderComposer<List<T>> =
        repeat(DecoderCollector.toList(), predicate, addLast)

    fun assemble(): Decoder<T> = inner

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
