package com.kamelia.sprinkler.transcoder.binary.decoder.composer

import com.kamelia.sprinkler.transcoder.binary.decoder.core.Decoder
import java.nio.ByteOrder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CoverageTests {

    @Test
    fun `ErrorStateHolder fillsInStacktrace`() {
        ErrorStateHolder(Decoder.State.Error("")).fillInStackTrace()
    }

    @Test
    fun `ProcessingMarker fillsInStacktrace`() {
        ProcessingMarker.fillInStackTrace()
    }

    @Test
    fun `RecursionMarker fillsInStacktrace`() {
        RecursionMarker.fillInStackTrace()
    }

    @Test
    fun `DecodingScope self decoder reset`() {
        DecodingScopeImpl<Any>(::ElementsAccumulator, HashMap(), ByteOrder.BIG_ENDIAN).self.reset()
    }

    @Test
    fun `DecodingScope string throws when missing`() {
        assertThrows<AssertionError> {
            DecodingScopeImpl<Any>(::ElementsAccumulator, HashMap(), ByteOrder.BIG_ENDIAN).string()
        }
    }

}
