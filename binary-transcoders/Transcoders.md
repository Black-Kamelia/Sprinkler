# Transcoder

A `Transcoder` is an object that is both an `Encoder` and a `Decoder`, and as such, serve the purpose of serializing and
deserializing data in a binary format.

As of now, one can create a `Transcoder` using the `Transcoder.create(encoder, decoder)` function, or by implementing
the `Transcoder` interface directly.

```kotlin
val encoder: Encoder<Int> = IntEncoder()
val decoder: Decoder<Int> = IntDecoder()

val transcoder: Transcoder<Int> = Transcoder.create(encoder, decoder)
```
