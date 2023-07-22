package com.kamelia.benchmark.sprinkler.transcoder.binary

import com.kamelia.benchmark.sprinkler.transcoder.binary.`object`.BasicPerson
import com.kamelia.benchmark.sprinkler.transcoder.binary.`object`.BasicPersonDecoder
import com.kamelia.benchmark.sprinkler.transcoder.binary.`object`.basicPersonDecoder
import com.kamelia.sprinkler.transcoder.binary.decoder.core.Decoder
import com.kamelia.sprinkler.transcoder.binary.decoder.core.DecoderInput
import org.openjdk.jmh.annotations.*

open class BasicInMemoryDecodingBenchmarks {

//    @Benchmark
//    fun handmadeDecoderSingleStepDecoding(state: BasicInMemoryPersonDecodingState) {
//        state.handmade.decode(state.input)
//    }
//
//    @Benchmark
//    fun compositionDecoderSingleStepDecoding(state: BasicInMemoryPersonDecodingState) {
//        state.composition.decode(state.input)
//    }
//
//    @Benchmark
//    fun handmadeDecoderSeveralStepsDecoding(state: BasicInMemoryPersonDecodingState) {
//        var s: Decoder.State<BasicPerson>?
//        do {
//            s = state.handmade.decode(state.proxiedInput)
//        } while (s != null && s.isNotDone())
//    }
//
//    @Benchmark
//    fun compositionDecoderSeveralStepsDecoding(state: BasicInMemoryPersonDecodingState) {
//        var s: Decoder.State<BasicPerson>?
//        do {
//            s = state.composition.decode(state.proxiedInput)
//        } while (s != null && s.isNotDone())
//    }

}

@State(Scope.Benchmark)
open class BasicInMemoryPersonDecodingState {

    @JvmField
    var handmade: Decoder<BasicPerson> = BasicPersonDecoder()

    @JvmField
    var composition: Decoder<BasicPerson> = basicPersonDecoder()

    @JvmField
    var input: DecoderInput = DecoderInput.nullInput()

    @JvmField
    var proxiedInput: DecoderInput = DecoderInput.nullInput()

    @Setup(Level.Invocation)
    fun inputSetup() {
        input = DecoderInput.from(ARRAY)
        proxiedInput = inputProxy(DecoderInput.from(ARRAY))
    }

}
