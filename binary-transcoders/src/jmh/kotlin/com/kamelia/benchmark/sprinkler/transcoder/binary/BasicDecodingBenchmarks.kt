package com.kamelia.benchmark.sprinkler.transcoder.binary

import com.kamelia.benchmark.sprinkler.transcoder.binary.`object`.BasicPerson
import com.kamelia.benchmark.sprinkler.transcoder.binary.`object`.BasicPersonDecoder
import com.kamelia.benchmark.sprinkler.transcoder.binary.`object`.basicPersonDecoder
import com.kamelia.sprinkler.transcoder.binary.decoder.core.Decoder
import com.kamelia.sprinkler.transcoder.binary.decoder.core.NothingDecoder
import org.openjdk.jmh.annotations.*

open class BasicCompositionBenchmarks {

    @Benchmark
    fun handmadeDecoderSingleStepDecoding(state: BasicPersonDecodingState): Any {
        return state.handmade.decode(state.input)
    }

    @Benchmark
    fun compositionDecoderSingleStepDecoding(state: BasicPersonDecodingState): Any {
        return state.composition.decode(state.input)
    }

    @Benchmark
    fun handmadeDecoderSeveralStepsDecoding(state: BasicPersonDecodingState): Any? {
        var s: Decoder.State<BasicPerson>?
        do {
            s = state.handmade.decode(state.proxiedInput)
        } while (s != null && s.isNotDone())
        return s
    }

    @Benchmark
    fun compositionDecoderSeveralStepsDecoding(state: BasicPersonDecodingState): Any? {
        var s: Decoder.State<BasicPerson>?
        do {
            s = state.composition.decode(state.proxiedInput)
        } while (s != null && s.isNotDone())
        return s
    }

}

@State(Scope.Benchmark)
open class BasicPersonDecodingState : AbstractDecoderBenchmarkState() {

    @JvmField
    var handmade: Decoder<BasicPerson> = BasicPersonDecoder()

    @JvmField
    var composition: Decoder<BasicPerson> = basicPersonDecoder()

}
