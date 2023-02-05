package com.kamelia.sprinkler.binary.decoder.composer

import com.kamelia.sprinkler.binary.decoder.BooleanDecoder
import com.kamelia.sprinkler.binary.decoder.ByteDecoder
import com.kamelia.sprinkler.binary.decoder.Decoder
import com.kamelia.sprinkler.binary.decoder.DecoderCollector
import com.kamelia.sprinkler.binary.decoder.IntDecoder
import com.kamelia.sprinkler.binary.decoder.UTF8StringDecoder
import com.kamelia.sprinkler.binary.encoder.EncoderBuilder
import com.zwendo.restrikt.annotation.PackagePrivate

class DecoderComposer0<B> {

    fun <R> beginWith(decoder: Decoder<R>): DecoderComposer1<B, R> = DecoderComposer1<B, R>(decoder)

}

class DecoderComposer1<B, T> : DecoderComposer<B, T, DecoderComposer1<B, T>> {

    @PublishedApi
    internal var decoder: Decoder<T>?

    internal constructor(decoder: Decoder<T>) : super(decoder) {
        this.decoder = decoder
    }

    constructor(previous: DecoderComposer<B, *, *>) : super(previous) {
        decoder = null
    }

    fun <R> map(block: (T) -> Decoder<R>): DecoderComposer1<B, R> = casted { mapStep(block) }

    fun <R> then(decoder: Decoder<R>): DecoderComposer2<B, T, R> = DecoderComposer2(this, decoder)

    fun <C, R> repeat(times: Int, collector: DecoderCollector<C, T, R>): DecoderComposer1<B, R> = thisCasted {
        repeatStep(times, collector)
    }

    fun <C, R> repeat(collector: DecoderCollector<C, T, R>, sizeDecoder: Decoder<Int>): DecoderComposer1<B, R> =
        casted { repeatStep(collector, sizeDecoder) }

    @JvmOverloads
    fun <C, R> until(
        collector: DecoderCollector<C, T, R>,
        addLast: Boolean = false,
        predicate: (T) -> Boolean,
    ): DecoderComposer1<B, R> = casted { untilStep(collector, addLast, predicate) }

    fun optional(nullabilityDecoder: Decoder<Boolean>): DecoderComposer1<B, T?> = casted {
        optionalStep(nullabilityDecoder)
    }

    fun optionalRecursion(nullabilityDecoder: Decoder<Boolean>): DecoderComposer2<B, T, B?> {
        return DecoderComposer2(this, optionalRecursionStep(nullabilityDecoder))
    }

    private inline fun <R> casted(block: DecoderComposer1<B, T>.() -> Unit): DecoderComposer1<B, R> {
        block()
        decoder = null
        @Suppress("UNCHECKED_CAST")
        return this as DecoderComposer1<B, R>
    }

}

class DecoderComposer2<B, T1, T2> @PackagePrivate internal constructor(
    previous: DecoderComposer<B, *, *>,
    decoder: Decoder<T2>,
) : DecoderComposer<B, T2, DecoderComposer2<B, T1, T2>>(previous, decoder) {

    fun <R> map(block: (T2) -> Decoder<R>): DecoderComposer2<B, T1, R> = thisCasted { mapStep(block) }

    fun <R> then(decoder: Decoder<R>): DecoderComposer3<B, T1, T2, R> = DecoderComposer3(this, decoder)

    fun <R> reduce(reducer: (T1, T2) -> R): DecoderComposer1<B, R> {
        reduceStep { reducer(next(), next()) }
        return DecoderComposer1(this)
    }

    fun optionalRecursion(nullabilityDecoder: Decoder<Boolean>): DecoderComposer3<B, T1, T2, B?> {
        return DecoderComposer3(this, optionalRecursionStep(nullabilityDecoder))
    }

}

class DecoderComposer3<B, T1, T2, T3> @PackagePrivate internal constructor(
    previous: DecoderComposer<B, *, *>,
    decoder: Decoder<T3>,
) : DecoderComposer<B, T3, DecoderComposer3<B, T1, T2, T3>>(previous, decoder) {

    fun <R> map(block: (T3) -> Decoder<R>): DecoderComposer3<B, T1, T2, R> = thisCasted { mapStep(block) }

    fun <R> then(decoder: Decoder<R>): DecoderComposer4<B, T1, T2, T3, R> = DecoderComposer4(this, decoder)

    fun <R> reduce(reducer: (T1, T2, T3) -> R): DecoderComposer1<B, R> {
        reduceStep { reducer(next(), next(), next()) }
        return DecoderComposer1(this)
    }

    fun optionalRecursion(nullabilityDecoder: Decoder<Boolean>): DecoderComposer4<B, T1, T2, T3, B?> {
        return DecoderComposer4(this, optionalRecursionStep(nullabilityDecoder))
    }

}

class DecoderComposer4<B, T1, T2, T3, T4> @PackagePrivate internal constructor(
    previous: DecoderComposer<B, *, *>,
    decoder: Decoder<T4>,
) : DecoderComposer<B, T4, DecoderComposer4<B, T1, T2, T3, T4>>(previous, decoder) {

    fun <R> map(block: (T4) -> Decoder<R>): DecoderComposer4<B, T1, T2, T3, R> = thisCasted { mapStep(block) }

    fun <R> reduce(reducer: (T1, T2, T3, T4) -> R): DecoderComposer1<B, R> {
        reduceStep { reducer(next(), next(), next(), next()) }
        return DecoderComposer1(this)
    }

}

val personEncoder = EncoderBuilder<Person>()
    .encode(Person::firstname)
    .encode(Person::lastname)
    .encode(Person::age)
    //.encodeRecursivelyWith(Person::father)
    .build()

data class Person(val firstname: String, val lastname: String, val age: Int, val father: Person? = null)


fun main() {
    val decoder: Decoder<List<Person>> = composedDecoder {
        beginWith(UTF8StringDecoder())
            .then(UTF8StringDecoder())
            .then(IntDecoder())
//            .optionalRecursion(BooleanDecoder())
            .reduce(::Person)
            .repeat(0, DecoderCollector.toList())
//            .repeat(DecoderCollector.toList(), IntDecoder())
    }
    val john = Person("John", "Doe", 42, null)
    val jane = Person("Jane", "Doe", 42, john)
    val jack = Person("Jack", "Doe", 42, jane)

    val bytes1 = personEncoder.encode(john)
    val bytes2 = personEncoder.encode(jane)
    val bytes3 = personEncoder.encode(jack)

    val data = byteArrayOf(
//        0, 0, 0, 2,
        *bytes1,
        *bytes2
    )

    val result = decoder.decode(data)
    println(result.get())
//    treeTest()

}


data class Node(val value: Byte, val left: Node?, val right: Node?)

val treeEncoder = EncoderBuilder<Node>()
    .encode(Node::value)
    .encodeRecursivelyWith(Node::left)
    .encodeRecursivelyWith(Node::right)
    .build()

fun treeTest() {
    val treeDecoder: Decoder<Node> = composedDecoder {
        beginWith(ByteDecoder())
            .optionalRecursion(BooleanDecoder())
            .optionalRecursion(BooleanDecoder())
            .reduce(::Node)
    }

    val root = Node(
        1,
        Node(
            2,
            Node(3, null, null),
            Node(4, null, null)
        ),
        Node(
            5,
            Node(6, null, null),
            Node(7, null, null)
        )
    )

    val treeBytes = treeEncoder.encode(root)

    val treeResult = treeDecoder.decode(treeBytes)
    println(treeResult.get())
}
