@file:JvmName("EndMarkerVariableSizedDecoders")
package com.kamelia.sprinkler.binary.decoder

import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset

fun UTF8StringDecoder(): Decoder<String> = StringDecoder(Charsets.UTF_8)

fun UTF8StringDecoderEM(): Decoder<String> = StringDecoderEM(Charsets.UTF_8)

fun ASCIIStringDecoder(): Decoder<String> = StringDecoder(Charsets.US_ASCII)

fun ASCIIStringDecoderEM(): Decoder<String> = StringDecoderEM(Charsets.US_ASCII)

@JvmOverloads
fun StringDecoder(
    charset: Charset,
    sizeDecoder: Decoder<Int> = IntDecoder(),
    endianness: ByteOrder = ByteOrder.BIG_ENDIAN,
): Decoder<String> = BatchPrefixedSizeVariableSizeDecoder(sizeDecoder, endianness) { charset.decode(this).toString() }

@JvmOverloads
fun StringDecoderEM(
    charset: Charset,
    endMarker: Byte = 0b0,
    endianness: ByteOrder = ByteOrder.BIG_ENDIAN,
): Decoder<String> = BatchEndMarkerVariableSizeDecoder(endMarker, endianness) { charset.decode(this).toString() }

private class BatchPrefixedSizeVariableSizeDecoder<E>(
    private val sizeDecoder: Decoder<Int> = IntDecoder(),
    private val endianness: ByteOrder = ByteOrder.BIG_ENDIAN,
    private val extractor: ByteBuffer.() -> E,
) : Decoder<E> {

    override fun decode(input: InputStream): E {
        val size = sizeDecoder.decode(input)
        checkDecoding(size > 0) { "Size must be positive, size: $size" }

        val array = ByteArray(size)
        val readBytes = input.read(array)
        checkDecoding(readBytes <= size) { "Not enough bytes to read, expected: $size, read: $readBytes" }

        val buffer = ByteBuffer.wrap(array).order(endianness)
        return extractor(buffer)
    }

}


private class BatchEndMarkerVariableSizeDecoder<E>(
    endMarker: Byte,
    private val endianness: ByteOrder = ByteOrder.BIG_ENDIAN,
    private val extractor: ByteBuffer.() -> E,
) : Decoder<E> {

    private val endMarker = endMarker.toInt()

    override fun decode(input: InputStream): E {
        val list = mutableListOf<Byte>()
        var byte = input.read()
        while (byte != endMarker && byte != -1) {
            list += byte.toByte()
            byte = input.read()
        }
        checkDecoding(byte != -1) { "End marker not found" }

        val buffer = ByteBuffer.wrap(list.toByteArray()).order(endianness)
        return extractor(buffer)
    }

}
