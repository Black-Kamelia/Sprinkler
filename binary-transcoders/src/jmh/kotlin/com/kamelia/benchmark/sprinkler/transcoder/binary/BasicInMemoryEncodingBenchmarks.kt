package com.kamelia.benchmark.sprinkler.transcoder.binary

import com.kamelia.benchmark.sprinkler.transcoder.binary.`object`.BasicPerson
import com.kamelia.benchmark.sprinkler.transcoder.binary.`object`.BasicPersonEncoder
import com.kamelia.benchmark.sprinkler.transcoder.binary.`object`.basicPersonEncoder
import com.kamelia.sprinkler.transcoder.binary.encoder.core.Encoder
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State

open class BasicInMemoryEncodingBenchmarks {

//    @Benchmark
//    fun handmadeEncoderEncoding(state: BasicPersonEncodingState) {
//        state.handmade.encode(state.person)
//    }
//
//    @Benchmark
//    fun compositionEncoderEncoding(state: BasicPersonEncodingState) {
//        state.composition.encode(state.person)
//    }

}

@State(Scope.Benchmark)
open class BasicPersonEncodingState {

    @JvmField
    val handmade: Encoder<BasicPerson> = BasicPersonEncoder()

    @JvmField
    val composition: Encoder<BasicPerson> = basicPersonEncoder()

    @JvmField
    val person = BasicPerson("John Doe", 42)

}
