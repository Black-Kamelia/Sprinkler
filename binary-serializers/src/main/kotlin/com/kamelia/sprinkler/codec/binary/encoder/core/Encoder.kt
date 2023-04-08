package com.kamelia.sprinkler.codec.binary.encoder.core

import java.io.File
import java.io.OutputStream
import java.nio.file.Path
import kotlin.io.path.outputStream

/**
 * Represents an object that can convert an object of type [T] to bytes.
 *
 * An encoder is stateless and encodes in a single operation, it can be instantiated once and reused multiple times to
 * encode different objects.
 *
 * &nbsp;
 *
 * This interface has only one abstract method and therefore can be implemented as a lambda, as shown below:
 *
 * &nbsp;
 *
 * ```
 * class MyBytePair(val first: Byte, val second: Byte)
 *
 * val myEncoder = Encoder<MyBytePair> { obj, output ->
 *     output.write(obj.first)
 *     output.write(obj.second)
 * }
 * ```
 *
 * @param T The type of the object to encode.
 * @see EncoderOutput
 */
fun interface Encoder<in T> {

    /**
     * Encodes the given [obj] to the given [output].
     *
     * @param obj the object to encode
     * @param output the output to write the encoded bytes to
     * @see EncoderOutput
     */
    fun encode(obj: T, output: EncoderOutput)

    /**
     * Encodes the given [obj] to a [ByteArray].
     *
     * @param obj the object to encode
     * @return the encoded bytes
     */
    fun encode(obj: T): ByteArray {
        val output = ArrayList<Byte>()
        encode(obj, output::add)
        return output.toByteArray()
    }

    /**
     * Encodes the given [obj] to the given [OutputStream].
     *
     * @param obj the object to encode
     * @param output the output to write the encoded bytes to
     */
    fun encode(obj: T, output: OutputStream): Unit = encode(obj, EncoderOutput.from(output))

    /**
     * Encodes the given [obj] to the given [Path].
     *
     * @param obj the object to encode
     * @param output the output to write the encoded bytes to
     */
    fun encode(obj: T, output: Path): Unit = output.outputStream().use { encode(obj, it) }

    /**
     * Encodes the given [obj] to the given [File].
     *
     * @param obj the object to encode
     * @param output the output to write the encoded bytes to
     */
    fun encode(obj: T, output: File): Unit = output.outputStream().use { encode(obj, it) }

}
