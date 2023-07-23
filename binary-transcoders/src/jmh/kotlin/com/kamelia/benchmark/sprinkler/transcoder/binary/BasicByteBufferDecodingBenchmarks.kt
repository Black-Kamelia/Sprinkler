package com.kamelia.benchmark.sprinkler.transcoder.binary

import com.kamelia.benchmark.sprinkler.transcoder.binary.`object`.BasicPerson
import com.kamelia.benchmark.sprinkler.transcoder.binary.`object`.BasicPersonDecoder
import com.kamelia.benchmark.sprinkler.transcoder.binary.`object`.basicPersonDecoder
import com.kamelia.sprinkler.transcoder.binary.decoder.core.Decoder
import com.kamelia.sprinkler.transcoder.binary.decoder.core.DecoderInput
import org.openjdk.jmh.annotations.*
import java.nio.ByteBuffer


open class BasicByteBufferDecodingBenchmarks {

    @Benchmark
    fun handmadeDecoderSingleStepDecoding(state: BasicByteBufferPersonDecodingState) {
        state.handmade.decode(state.input)
    }

    @Benchmark
    fun compositionDecoderSingleStepDecoding(state: BasicByteBufferPersonDecodingState) {
        state.composition.decode(state.input)
    }

    @Benchmark
    fun handmadeDecoderSeveralStepsDecoding(state: BasicByteBufferPersonDecodingState) {
        var s: Decoder.State<BasicPerson>?
        do {
            s = state.handmade.decode(state.proxiedInput)
        } while (s != null && s.isNotDone())
    }

    @Benchmark
    fun compositionDecoderSeveralStepsDecoding(state: BasicByteBufferPersonDecodingState) {
        var s: Decoder.State<BasicPerson>?
        do {
            s = state.composition.decode(state.proxiedInput)
        } while (s != null && s.isNotDone())
    }


}

@State(Scope.Benchmark)
open class BasicByteBufferPersonDecodingState {

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
        val array = ARRAY
        input = DecoderInput.from(ByteBuffer.wrap(array).position(array.size))
        proxiedInput = inputProxy(DecoderInput.from(ByteBuffer.wrap(array).position(array.size)))
    }

}
