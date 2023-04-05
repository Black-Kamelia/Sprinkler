package com.kamelia.sprinkler.codec.binary.encoder.core

import java.io.File
import java.io.OutputStream
import java.nio.file.Path

fun interface Encoder<in T> {

    fun encode(obj: T, output: EncoderOutput)

    fun encodeToByteArray(obj: T): ByteArray {
        val output = ArrayList<Byte>()
        encode(obj, output::add)
        return output.toByteArray()
    }

    fun encode(obj: T, output: OutputStream): Unit = encode(obj, EncoderOutput.OutputStreamOutput(output))

    fun encode(obj: T, output: Path): Unit = encode(obj, output.toFile())

    fun encode(obj: T, output: File): Unit = output.outputStream().use { encode(obj, it) }

}
