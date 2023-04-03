package com.kamelia.sprinkler.codec.binary.encoder.core

import java.io.File
import java.io.OutputStream
import java.nio.file.Path

interface Encoder<in T> {

    fun <O : EncoderOutput> encode(obj: T, output: O): O

    fun encodeToByteArray(obj: T): ByteArray = encode(obj, EncoderOutput.ByteArrayOutput()).toByteArray()

    fun encodeToOutputStream(obj: T, output: OutputStream): EncoderOutput.OutputStreamOutput =
        encode(obj, EncoderOutput.OutputStreamOutput(output))

    fun encodeToPath(obj: T, path: Path): EncoderOutput.OutputStreamOutput = encodeToFile(obj, path.toFile())

    fun encodeToFile(obj: T, file: File): EncoderOutput.OutputStreamOutput =
        file.outputStream().use { encodeToOutputStream(obj, it) }

}
