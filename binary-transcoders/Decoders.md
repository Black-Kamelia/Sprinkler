# Decoders

Decoders serve the purpose of deserializing bytes into specific objects. They are the opposite of Encoders, and differ
from them in the way that they are stateful. Indeed, they are intended to decode streams of bytes that are not 
necessarily given contiguously, as in, for example, on a network communication. Therefore, a Decoder should not be used
on different streams of bytes, as it will keep its state between calls.

We will see that the API provides building blocks to create decoders for any type of data, and compose them together
to create more complex decoders very easily.

For simplification and readability purposes, every class name referenced in the following sections will not be written
in their fqname form, but rather in their simple name form. For example, `com.kamelia.sprinkler.transcoder.binary.decoder.Decoder`
will be written as `Decoder`. The same goes for all classes in the package `com.kamelia.sprinkler.transcoder.binary.decoder`.

## Summary

...

## Main interfaces

The two main bricks of this API are the `Decoder` and `DecoderInput` interfaces.

They are used to define the behavior of the decoder and how to feed it the encoded data, respectively.
While they have several methods, only a few need to actually be implemented in each of them, because others have
default implementations depending on them.

Along with them, a [monadic](https://en.wikipedia.org/wiki/Monad_(functional_programming)) result type is also provided to ensure that partial decoding is possible, in the form
of `Decoder.State`.

### Decoder

A `Decoder<T>` should at least implement the following two methods: `decode(input: DecoderInput)` and `reset()`. The
`decode` method is the main brick of the API, and is used to define the behavior of the decoder. It should deserialize
the bytes coming from the `input` and return a `Decoder.State`. The `reset` method is used to reset the state of the
decoder; However, the decoder automatically resets itself whenever it fully decodes an object (the method is useful when
an error occurs, or when the decoding must be interrupted).

Here is an example of a decoder:

```kt
class MyBytePair(val first: Byte, val second: Byte)

fun myDecoder(): Decoder<MyBytePair> = object : Decoder<MyBytePair> {
    private var first: Byte? = null
    
    override fun decode(input: DecoderInput): Decoder.State<MyBytePair> {
        if (first == null) {                                          // if the first byte has not been read yet
            val byte: Int = input.read()                              // read the first byte
            if (byte == -1) return Decoder.State.Processing           // if the input is empty, return Processing
            first = byte.toByte()                                     // otherwise, store the byte
        }        
        
        val second: Int = input.read()                                // read the second byte
        if (second == -1) return Decoder.State.Processing             // if the input is empty, return Processing
        reset()                                                       // otherwise, reset the state of the decoder
        return Decoder.State.Done(MyBytePair(first, second.toByte())) // and return the decoded object
    }
    
    override fun reset() {
        first = null
    }
    
}
```

This decoder simply reads two bytes from the input and assembles them into a `MyBytePair` object. This example
illustrates the stateful aspect of decoders, as the first byte is stored in the decoder until the second byte is read.
It also shows the decoder automatically resets itself when it fully decodes an object. The `DecoderInput` is an
abstraction to represent the stream of bytes to decode (see the next section for more details).

The previously defined decoder can be used as follows:

```kt
val input: DecoderInput = ... // some input
val myPair: MyBytePair = myDecoder().decode(input).get() // decode the input and get the result
```

(We will expand on the `DecoderInput` interface in the next section)

You can notice the `get()` method call on the result of the decoding. This is because the `decode` method returns a
`Decoder.State`, which is a [monadic](https://en.wikipedia.org/wiki/Monad_(functional_programming)) type that represents
the result of the decoding.
It can be either:
- `Decoder.State.Done`, which means that the decoder has fully decoded the object and returns it (the instance contains
the decoded object) ;
- `Decoder.State.Processing`, which means that the decoder needs more bytes to fully decode the object ;
- `Decoder.State.Error`, which means that an error occurred during the decoding (the instance contains the error).

It offers several methods to perform different operations on the return object:
- Unwrap operations, through `get` , `getOrNull`, `getOrElse` or `getOrThrow` ;
- Bind operations, through `mapState`, `mapResult` or `mapEmptyState` ;
- Utility operations, through `isDone`, `isNotDone`, `ifDone`, `ifError`, etc.

### EncoderOutput

Basically, the `EncoderOutput` is an abstraction that serves to map the behavior of an object to that of something similar
to an `OutputStream`. It is used to output the encoded data, and is passed to the `Encoder` when encoding.

The methods that need to be implemented are `writeBit(bit: Int)` and `flush()`.

The `writeBit` method writes a single bit to the output. Only the least significant bit of the given `Int` is written,
and all other bits are ignored. The `flush` method flushes the output, to force any buffered bytes to be written.
This method is useful when the writing of byte is finished but the last byte is not full and therefore has not been
written yet. All the padding bits appended to the last byte are set to `0`.

```kt
val output: EncoderOutput = EncoderOutput.from { byte -> print(byte) }
output.writeBit(1)
output.writeBit(0)
output.writeBit(1)
output.flush() // should print the byte 1010_0000
```

However, often, one needs quite a bit more than just these two methods to output the encoded data in an efficient way
(writing whole bytes, or event groups of bytes). To that effect, there are several sensible factories to create
`EncoderOutput`s.

Indeed, say we want to output the encoded data to *stdout*, here's an example of how we could do it:

```kt
val stdoutEncoderOutput: EncoderOutput = EncoderOutput.from { byte -> print(byte) } // write byte implementation

stdoutEncoderOutput.write(42) // prints 42
stdoutEncoderOutput.write(byteArrayOf(1, 2, 3)) // prints 1, 2 and 3
```

(Obviously, this is not the most efficient way to do it, but it is just an example.)

In fact, the `from` function above is an overload of a function which takes in an `OutputStream` as an argument.

You can now use this `stdoutEncoderOutput` to output the encoded data to *stdout*.

```kt
UTF8StringEncoder().encode("Hello, World!", stdoutEncoderOutput) // prints the UTF-8 encoded bytes of "Hello, World!", prefixed with the size of the string
```

As stated before, the `Encoder::encode` function actually acts as if the given `EncoderOutput` writes to a
`ByteArray` by default, if you do not provide one.

There are also several helper `encode` methods, such as ones which take in an `OutputStream`, a `File`, or a
`java.nio.Path` as an argument, and automatically create an `EncoderOutput` for you behind the scenes.

```kt
val encoder: Encoder<String> = UTF8StringEncoder()
encoder.encode("Hello, World!", System.out)
encoder.encode("Hello, World!", File("hello.txt"))
encoder.encode("Hello, World!", Path.of("hello.txt"))
```

Note that there is a third factory to create an `EncoderOutput`, which is `EncoderOutput::nullOutput`. It returns an
`EncoderOutput` which never writes to anything. It is a no-op, and is useful for testing purposes, for example.

---

// get inspired by the text above to write the doc of DecoderInput

### DecoderInput

The `DecoderInput` is an abstraction that serves to map the behavior of an object to that of something similar
to an `InputStream`. It is used to feed the `Decoder` with the encoded data, and is passed to the `Decoder` when decoding.

The methods that need to be implemented are `read()` and `readBit()`.
The `read` method reads a single byte from the input and returns it as an `Int`. If the input is empty, it returns `-1`.
The `readBit` method reads a single bit from the input and returns it as an `Int`. If the input is empty, it returns `-1`.

However, often, one needs quite a bit more than just these two methods to feed the `Decoder` with the encoded data
in an efficient way (reading whole bytes, or even groups of bytes, instead of relying on the slow default
implementation). To that effect, there are several sensible factories to create `DecoderInput`s.

Indeed, say we want to read the encoded data from the natural integers sequence, here's an example of how we could do it:

```kt
var value: Byte = 0.toByte()
val stdinDecoderInput: DecoderInput = DecoderInput.from { value++ } // will return 0, 1, 2, ...

stdinDecoderInput.read() // returns 0
stdinDecoderInput.read() // returns 1
stdinDecoderInput.readBit() // returns 0 (the most significant bit of 2 = 0000_0010)
```

(Obviously, this is not the most realistic implementation, but it is just an example.)

The `from` function above takes in a function which returns a `Byte` as an argument. There are also several helper
`from` methods, such as ones which take in an `InputStream`, a `ByteBuffer`, or a `ByteArray` as an argument.

```kt
val input: DecoderInput = DecoderInput.from(System.`in`) // from(InputStream)
val input: DecoderInput = DecoderInput.from(ByteBuffer.allocate(10)) // from(ByteBuffer)
val input: DecoderInput = DecoderInput.from(byteArrayOf(1, 2, 3)) // from(ByteArray)
```

Note that there is a third factory to create a `DecoderInput`, which is `DecoderInput::nullInput`. It returns a
`DecoderInput` which never reads from anything. It is a no-op, and is useful for testing purposes, for example.

