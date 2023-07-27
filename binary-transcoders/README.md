# Sprinkler: Binary Transcoders

[![Maven Central](https://img.shields.io/maven-central/v/com.black-kamelia.sprinkler/binary-transcoders)](https://central.sonatype.com/artifact/com.black-kamelia.sprinkler/binary-transcoders)

## Summary

- [Intentions](#intentions)
- [Getting Started](#getting-started)
- [Encoders](#encoders)
- [Decoders](#decoders)

## Intentions

The purpose of this module is to allow the user to serialize and deserialize data in a binary format. 
This is useful for sending data over the network, or storing data in a file.

The module is designed to permit the user to define the structure of the transcoders easily and with a
structured API. The user can define the structure of the transcoder using a builder pattern,
and add new types of transcoders by implementing simple interfaces.

## Getting Started

This module is a framework, in the sense that as long as one properly implement the provided interfaces,
everything goes into place accordingly, and provide a simple API to the end user.

Fortunately, the module also provides a few smart defaults and built-in transcoders.

Here is how to encoder an integer into a byte array:

```kt
val encoder: Encoder<Int> = IntEncoder()
val encoded: ByteArray = encoder.encode(5) // [0, 0, 0, 5]
```

The same goes for decoding:

```kt
val decoder: Decoder<Int> = IntDecoder()
val decoded: Int = decoder.decode(byteArrayOf(0, 0, 0, 5)).get() // 5
```

And you can combine them to create a transcoder

```kt
val encoder: Encoder<Int> = IntEncoder()
val decoder: Decoder<Int> = IntDecoder()
val transcoder: Transcoder<Int> = Transcoder.create(encoder, decoder)

val encoded: ByteArray = transcoder.encode(5)
val decoded: Int = transcoder.decode(encoded).get()
```

Moreover, one can easily create more complex transcoders using a composition API:

```kt
class IntPair(val first: Int, val second: Int)

val pairEncoder: Encoder<IntPair> = composedEncoder<IntPair> { // it: IntPair
  encode(it.first)
  encode(it.second)
}

val pairDecoder: Decoder<IntPair> = composedDecoder<IntPair> {
  val first: Int = int()
  val second: Int = int()
  IntPair(first, second)
}
```

## Encoders

Encoders are used to serialize data into a binary format. They are stateless, and can be reused multiple times. For
a complete guide on how to use encoders, see [Encoders.md](Encoders.md).

## Decoders

Decoders are used to deserialize data from a binary format. They are stateful, and should be created each time.
For a complete guide on how to use decoders, see [Decoders.md](Decoders.md).

## Transcoders

Transcoders are, by definition, both encoders and decoders. They are stateful, and should be created each time.
For a complete guide on how to use transcoders, see [Transcoders.md](Transcoders.md).
