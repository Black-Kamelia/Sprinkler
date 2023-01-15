//package com.kamelia.sprinkler.binary.decoder
//
//internal open class DecoderComposerImpl<T>(
//    private val decoder: Decoder<T>,
//) : DecoderComposer<T> {
//
//    override fun <R> then(
//        nextDecoder: Decoder<R>,
//        sideEffect: (R) -> Unit,
//    ): DecoderComposer.Intermediate = object : Decoder<Unit> {
//        private var currentDone = false
//
//        override fun decode(input: DecoderDataInput): Decoder.State<Unit> {
//            if (!currentDone) {
//                val state = decoder.decode(input)
//                @Suppress("UNCHECKED_CAST")
//                if (state.isNotDone()) {
//                    return state as Decoder.State<Unit>
//                }
//            }
//
//            return nextDecoder
//                .decode(input)
//                .map(sideEffect)
//                .also { currentDone = it.isDone() }
//        }
//
//        override fun reset() {
//            decoder.reset()
//            nextDecoder.reset()
//        }
//
//    }.let { IntermediateDecoderComposerImpl(it) }
//
//
//    override fun <R> then(
//        nextDecoder: () -> Decoder<R>,
//        sideEffect: (R) -> Unit,
//    ): IntermediateDecoderComposer = Decoder {
//        decoder.decode(it)
//        sideEffect(nextDecoder().decode(it))
//    }.let(::DecoderComposerImpl)
//
//    override fun <R> map(block: (T) -> Decoder<R>): DecoderComposer<R> = object : Decoder<R> {
//        var currentDone = false
//        lateinit var nextReader: Decoder<R>
//
//        override fun decode(input: DecoderDataInput): Decoder.State<R> {
//            if (!currentDone) {
//                val state = decoder.decode(input)
//                @Suppress("UNCHECKED_CAST")
//                if (state.isNotDone()) {
//                    return state as Decoder.State<R>
//                }
//                currentDone = true
//                val currentResult = (state as Decoder.State.Done<T>).value
//                nextReader = block(currentResult)
//            }
//
//            return nextReader.decode(input)
//        }
//
//        override fun reset() {
//            TODO("Not yet implemented")
//        }
//
//    }.let(::DecoderComposerImpl)
//
//    override fun <R> finally(resultMapper: (T) -> R): DecoderComposerImpl<R> = Decoder {
//        resultMapper(decoder.decode(it))
//    }.let(::DecoderComposerImpl)
//
//    override fun <C, R> repeat(
//        sizeReader: Decoder<Number>,
//        collector: DecoderCollector<C, T, R>,
//    ): DecoderComposerImpl<R> = Decoder { stream ->
//        val collection = collector.supplier()
//        val size = sizeReader.decode(stream).toInt()
//
//        repeat(size) {
//            val element = decoder.decode(stream)
//            collector.accumulator(collection, element)
//        }
//
//        collector.finisher(collection)
//    }.let(::DecoderComposerImpl)
//
//    override fun repeat(sizeReader: Decoder<Number>): DecoderComposerImpl<List<T>> =
//        repeat(sizeReader, DecoderCollector.list())
//
//    override fun <C, R> repeat(
//        amount: Number,
//        collector: DecoderCollector<C, T, R>,
//    ): DecoderComposerImpl<R> = Decoder { stream ->
//        check(amount !is Float && amount !is Double) { "Amount must be of an integer type" }
//        val collection = collector.supplier()
//
//        for (i in 0 until amount.toLong()) {
//            val element = decoder.decode(stream)
//            collector.accumulator(collection, element)
//        }
//        collector.finisher(collection)
//    }.let(::DecoderComposerImpl)
//
//    override fun skip(size: Int): IntermediateDecoderComposer = Decoder {
//        decoder.decode(it)
//        it.readNBytes(size)
//        Unit
//    }.let(::DecoderComposerImp)
//
//    override fun repeat(amount: Number): DecoderComposerImpl<List<T>> = repeat(amount, DecoderCollector.list())
//
//    override fun <C, R> repeatUntil(
//        collector: DecoderCollector<C, T, R>,
//        predicate: (T) -> Boolean,
//    ): DecoderComposerImpl<R> = Decoder {
//        val collection = collector.supplier()
//        while (true) {
//            val element = decoder.decode(it)
//
//            if (!predicate(element)) break
//
//            collector.accumulator(collection, element)
//        }
//        collector.finisher(collection)
//    }.let(::DecoderComposerImpl)
//
//    override fun repeatUntil(predicate: (T) -> Boolean): DecoderComposerImpl<List<T>> =
//        repeatUntil(DecoderCollector.list(), predicate)
//
//    override fun assemble(): Decoder<T> = decoder
//
//}
//
//private class IntermediateDecoderComposerImpl(
//    decoder: Decoder<Unit>,
//) : DecoderComposer.Intermediate, DecoderComposerImpl<Unit>(decoder)
//
//
//class Person(
//    val firstname: String,
//    val lastname: String,
//    val age: Int,
//)
//
//fun main() {
//    lateinit var firstname: String
//    lateinit var lastname: String
//    var age = 0
//
//    val decoder = UTF8StringDecoder()
//        .compose { firstname = it }
//        .then(UTF8StringDecoder()) { lastname = it }
//        .then(IntDecoder()) { age = it }
//        .finally { Person(firstname, lastname, age) }
//        .assemble()
//}
