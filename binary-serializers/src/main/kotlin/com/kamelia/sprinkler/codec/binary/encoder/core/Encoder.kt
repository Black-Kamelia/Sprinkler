package com.kamelia.sprinkler.codec.binary.encoder.core

import java.io.File
import java.io.OutputStream
import java.nio.file.Path
import kotlin.io.path.outputStream

fun interface Encoder<in T> {

    fun encode(obj: T, output: EncoderOutput)

    fun encode(obj: T): ByteArray {
        val output = ArrayList<Byte>()
        encode(obj, output::add)
        return output.toByteArray()
    }

    fun encode(obj: T, output: OutputStream): Unit = encode(obj, EncoderOutput.from(output))

    fun encode(obj: T, output: Path): Unit = output.outputStream().use { encode(obj, it) }

    fun encode(obj: T, output: File): Unit = output.outputStream().use { encode(obj, it) }

}
