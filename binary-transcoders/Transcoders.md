# Transcoder

A `Transcoder` is an object that is both an `Encoder` and a `Decoder`, and as such, serve the purpose of serializing and
deserializing data in a binary format.

As of now, one can create a `Transcoder` using the `Transcoder.create(encoder, decoder)` function, or by implementing
the `Transcoder` interface directly.

> **Coming soon:** a way to create a `Transcoder` using the composition API (which will provide the `encode` and `decode`
> functions in one go).
