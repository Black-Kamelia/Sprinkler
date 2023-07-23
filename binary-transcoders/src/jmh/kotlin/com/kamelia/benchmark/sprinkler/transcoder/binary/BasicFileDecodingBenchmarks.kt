package com.kamelia.benchmark.sprinkler.transcoder.binary

import com.kamelia.benchmark.sprinkler.transcoder.binary.`object`.BasicPerson
import com.kamelia.benchmark.sprinkler.transcoder.binary.`object`.BasicPersonDecoder
import com.kamelia.benchmark.sprinkler.transcoder.binary.`object`.basicPersonDecoder
import com.kamelia.sprinkler.transcoder.binary.decoder.core.Decoder
import com.kamelia.sprinkler.transcoder.binary.decoder.core.DecoderInput
import java.io.Closeable
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.TearDown

open class BasicFileDecodingBenchmarks {

    @Benchmark
    fun handmadeDecoderSingleStepDecoding(state: BasicFilePersonDecodingState) {
        state.handmade.decode(state.input)
    }

    @Benchmark
    fun compositionDecoderSingleStepDecoding(state: BasicFilePersonDecodingState) {
        state.composition.decode(state.input)
    }

    @Benchmark
    fun handmadeDecoderSeveralStepsDecoding(state: BasicFilePersonDecodingState) {
        var s: Decoder.State<BasicPerson>?
        do {
            s = state.handmade.decode(state.proxiedInput)
        } while (s != null && s.isNotDone())
    }

    @Benchmark
    fun compositionDecoderSeveralStepsDecoding(state: BasicFilePersonDecodingState) {
        var s: Decoder.State<BasicPerson>?
        do {
            s = state.composition.decode(state.proxiedInput)
        } while (s != null && s.isNotDone())
    }

}

@State(Scope.Benchmark)
open class BasicFilePersonDecodingState {

    @JvmField
    var handmade: Decoder<BasicPerson> = BasicPersonDecoder()

    @JvmField
    var composition: Decoder<BasicPerson> = basicPersonDecoder()

    private var streams: ArrayList<Closeable> = ArrayList()

    @JvmField
    var input: DecoderInput = DecoderInput.nullInput()

    @JvmField
    var proxiedInput: DecoderInput = DecoderInput.nullInput()

    @Setup(Level.Invocation)
    fun inputSetup() {
        // get resource from classpath
        val stream1 = javaClass.getResourceAsStream("/basic_person.bin")!!
        input = DecoderInput.from(stream1)
        streams.add(stream1)
        val stream2 = javaClass.getResourceAsStream("/basic_person.bin")!!
        proxiedInput = inputProxy(DecoderInput.from(stream2))
        streams.add(stream2)
    }

    @TearDown(Level.Invocation)
    fun inputTearDown() {
        streams.forEach(Closeable::close)
    }

}
