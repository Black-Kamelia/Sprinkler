package com.kamelia.benchmark.sprinkler.transcoder.binary

import com.kamelia.benchmark.sprinkler.transcoder.binary.`object`.BasicPerson
import com.kamelia.benchmark.sprinkler.transcoder.binary.`object`.BasicPersonDecoder
import com.kamelia.benchmark.sprinkler.transcoder.binary.`object`.basicPersonDecoder
import com.kamelia.sprinkler.transcoder.binary.decoder.core.Decoder
import com.kamelia.sprinkler.transcoder.binary.decoder.core.DecoderInput
import com.kamelia.sprinkler.transcoder.binary.decoder.core.NothingDecoder
import com.kamelia.sprinkler.transcoder.binary.encoder.composer.composedEncoder
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import kotlin.math.min

open class BasicCompositionBenchmarks {

    @Benchmark
    fun handmadeDecoderSingleStepDecoding(state: BasicPersonState, blackhole: Blackhole) {
        val result = state.handmade.decode(state.input)
        blackhole.consume(result)
    }

    @Benchmark
    fun handmadeDecoderSeveralStepsDecoding(state: BasicPersonState, blackhole: Blackhole) {
        lateinit var s: Decoder.State<BasicPerson>
        do {
            s = state.handmade.decode(state.proxiedInput)
        } while (!s.isDone())
        blackhole.consume(s)
    }

    @Benchmark
    fun compositionDecoderSingleStepDecoding(state: BasicPersonState, blackhole: Blackhole) {
        val result = state.composition.decode(state.input)
        blackhole.consume(result)
    }

    @Benchmark
    fun compositionDecoderSeveralStepsDecoding(state: BasicPersonState, blackhole: Blackhole) {
        lateinit var s: Decoder.State<BasicPerson>
        do {
            s = state.composition.decode(state.proxiedInput)
            println(s)
        } while (!s.isDone())
        blackhole.consume(s)
    }



}

@State(Scope.Benchmark)
open class BasicPersonState {

    @JvmField
    var handmade: Decoder<BasicPerson> = NOTHING_DECODER

    @JvmField
    var composition: Decoder<BasicPerson> = NOTHING_DECODER

    @JvmField
    var input: DecoderInput = NULL_INPUT

    @JvmField
    var proxiedInput: DecoderInput = NULL_INPUT

    @Setup
    fun setup() {
        handmade = BasicPersonDecoder()
        composition = basicPersonDecoder()
        input = DecoderInput.from(ARRAY)
        proxiedInput = inputProxy(DecoderInput.from(ARRAY))
    }

}

fun inputProxy(decoderInput: DecoderInput): DecoderInput {
    var even = false
    return DecoderInput.from {
        even = !even
        if (even) {
            -1
        } else {
            decoderInput.read()
        }
    }
}


private val ARRAY: ByteArray = run {
    val person = BasicPerson("John Doe", 42)
    val encoder = composedEncoder<BasicPerson> {
        encode(it.name)
        encode(it.age)
    }
    encoder.encode(person)
}

private val NOTHING_DECODER = NothingDecoder()

private val NULL_INPUT = DecoderInput.nullInput()
