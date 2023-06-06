package com.kamelia.sprinkler.codec.binary.encoder.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.io.path.createTempFile
import kotlin.io.path.deleteIfExists

class EncoderTest {

    @Test
    fun `encode to byte array works correctly`() {
        val encoder = Encoder<Byte> { obj, o -> o.write(obj) }
        val value = 5.toByte()
        val result = encoder.encode(value)
        assertEquals(1, result.size)
        assertEquals(value, result[0])
    }

    @Test
    fun `encode to path works correctly`() {
        val path = createTempFile()

        val encoder = Encoder<Byte> { obj, o -> o.write(obj) }
        val value = 5.toByte()
        encoder.encode(value, path)
        assertEquals(1, path.toFile().length())
        assertEquals(value, path.toFile().readBytes()[0])

        path.deleteIfExists()
    }

    @Test
    fun `encode to file works correctly`() {
        val file = createTempFile().toFile()

        val encoder = Encoder<Byte> { obj, o -> o.write(obj) }
        val value = 5.toByte()
        encoder.encode(value, file)
        assertEquals(1, file.length())
        assertEquals(value, file.readBytes()[0])

        file.delete()
    }

}
