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
val input: DecoderInput = MyDecoderInput()
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
val input1: DecoderInput = DecoderInput.from(ByteBuffer.allocate(10)) // from(ByteBuffer)
val input2: DecoderInput = DecoderInput.from(byteArrayOf(1, 2, 3)) // from(ByteArray)
```

Note that there is a third factory to create a `DecoderInput`, which is `DecoderInput::nullInput`. It returns a
`DecoderInput` which never reads from anything. It is a no-op, and is useful for testing purposes, for example.

## Provided Decoders

This library provides a lot of essential "atomic" decoders, which are used to decode most of the basic types, and some
more complex but common ones, as well as factories to create them easily.

### Core Decoders

The core decoders implement and factorize the different ways to interpret a sequence of bytes; that is to say, for
example, how to decode an arbitrary number of bytes from a sequence, a constant number of bytes from the sequence, etc.
This API offers relatively low-level decoders, which are used to create more complex ones. It is usually not necessary
to use them directly, but they are provided for completeness.

#### ConstantSizedDecoder

The `ConstantSizedDecoder` is used to decode a sequence of bytes of a constant size. It
is created from the number of bytes to read, and a function to convert the read bytes to the desired object.

Here is an example of a decoder that decodes a single byte:

```kt
val byteDecoder: Decoder<Byte> = ConstantSizedDecoder(byteSize = 1, converter = { it[0] })
```

## PrefixedSizeDecoder

The `PrefixedSizeDecoder` decoder is used to decode a sequence of bytes of a variable size. It  first reads a prefix of
a constant size which represents the size of the sequence to read, and then reads the sequence itself. It is created
from a decoder that decodes a `Number` and a function to convert the read bytes to the desired object.

The following example shows an implementation of a decoder of `String` objects using the `PrefixedSizeDecoder` (it first
decodes the size of the string represented as a `Byte`, and then reads the string itself).

```kt
val stringDecoder: Decoder<String> = PrefixedSizeDecoder(
    sizeDecoder = byteDecoder, // the previously defined byte decoder
    converter = { it.decodeToString() }
)
```

#### MarkerEndedItemDecoder

The `MarkerEndedItemDecoder` decoder is used to decode a sequence of bytes of a variable size, which is ended by a
specific byte. It is created from an end marker (a `ByteArray`) and a function to convert the read bytes to the desired
object.

The following example shows an implementation of a decoder of `String` objects using the `MarkerEndedItemDecoder` (it
stops decoding when it reads a `0` byte).

```kt
val stringDecoder: Decoder<String> = MarkerEndedItemDecoder(
    endMarker = byteArrayOf(0), // the end marker
    converter = { it.decodeToString() }
)
```

#### ConstantArityReductionDecoder

The `ConstantArityReductionDecoder` decoder is used to decode the same object a constant number of times. It is created
from the number of times to decode the object, a decoder that decodes the objects and a `Collector` to collect the
decoded objects.

```kt
val byteListDecoder: Decoder<List<Byte>> = ConstantArityReductionDecoder(
    arity = 3,
    elementDecoder = byteDecoder, // the previously defined byte decoder
    collector = Collectors.toList()
)
```

#### PrefixedArityReductionDecoder

The `PrefixedArityReductionDecoder` decoder is used to decode the same object a variable number of times. It is created
from a decoder that decodes the number of times to decode the objects, a decoder that decodes the object and a
`Collector` to collect the decoded objects.

```kt
val byteListDecoder: Decoder<List<Byte>> = PrefixedArityReductionDecoder(
    arityDecoder = byteDecoder,   // the previously defined byte decoder
    elementDecoder = byteDecoder, // the previously defined byte decoder
    collector = Collectors.toList()
)
```

#### MarkerEndedArityReductionDecoder

The `MarkerEndedArityReductionDecoder` decoder is used to decode the same object a variable number of times, until a
specific object is read. It is created from a decoder that decodes the objects, a function that tests the decoded
elements and returns whether the decoding should stop, a boolean that indicates whether the end marker should be
included in the decoded objects, and a `Collector` to collect the decoded objects.

The following example shows an implementation of a decoder of `List<Byte>` objects using the
`MarkerEndedArityReductionDecoder` (it stops decoding when it reads a `0` byte).

```kt 
val byteListDecoder: Decoder<List<Byte>> = MarkerEndedArityReductionDecoder(
    elementDecoder = byteDecoder, // the previously defined byte decoder
    endMarkerPredicate = { it == 0 }, // the end marker
    keepLast = false,
    collector = Collectors.toList()
)
```

#### NothingDecoder

The `NothingDecoder` decoder is a special decoder that always returns `Decoder.State.Error`. It is useful when one
needs a decoder of a specific type and does not have one. The class offers several constructors to create a decoder
using a specific error message, a specific exception or a factory function.

```kt
val nothingDecoder: Decoder<Nothing> = NothingDecoder("This does not work")

fun foo(decoder: Decoder<Int>) {
    // does some stuff
}

foo(nothingDecoder) // valid, we can like this test the behavior of the function without having an actual decoder
```

### Base Decoders

Base decoders are the most basic decoders, and decode all the primitive types, as well as enum variants, and even
`String`s from a chosen encoding.

#### Primitive Decoders

The most common types one will be decoding are the JVM primitive types.

Indeed, the provided factories primitive decoders are :
- `ByteDecoder`
- `ShortDecoder`
- `IntDecoder`
- `LongDecoder`
- `FloatDecoder`
- `DoubleDecoder`
- `BooleanDecoder`

All of those, except for `ByteDecoder` and `BooleanDecoder`, accept `ByteOrder` as an argument, which is used to
determine the byte order (endianness) of the encoded data. If you do not provide one, it will default to
`ByteOrder.BIG_ENDIAN`.

#### String Decoders

In binary encoding in general, there are two major ways to decode strings of text (aside from the encoding):
- Fixed-length encoding, where the length of the string is known beforehand, and is decoded before the string itself.
- Variable-length encoding, where the length of the string is not known beforehand, in which case, a terminator flag
  (or, end marker) is decoded after the string to indicate the end of the string, in the same vein as C-style strings,
  which are null-terminated.

To that effect, the two main string decoder factories are two overloads of `StringDecoder`, both of them accept a
`Charset`, and one also takes a `Decoder<Int>` to decode the length of the string, while the other one uses a
`ByteArray` corresponding to the end marker.

For example:

```kt
val utf8PrefixedLengthDecoder: Decoder<String> = StringDecoder(Charsets.UTF_8, IntDecoder())
val asciiNullTerminatedDecoder: Decoder<String> = StringDecoder(Charsets.US_ASCII, byteArrayOf(0))
```

Fortunately, there are also some predefined factories for the most common encodings, which are:
- `UTF8StringDecoder`
- `UTF16StringDecoder`
- `ASCIIStringDecoder`
- `Latin1StringDecoder`
- `UTF8StringDecoderEM` (EM stands for End Marker)
- `UTF16StringDecoderEM` (EM stands for End Marker)
- `ASCIIStringDecoderEM` (EM stands for End Marker)
- `Latin1StringDecoderEM` (EM stands for End Marker)

They have sensible defaults for the length decoder, and the end marker, and are the recommended way to decode strings.

#### Enum Decoders

Enum decoders are used to decode enum variants. They are very simple, and come in two flavors:
- `EnumDecoder` which decodes the ordinal of the enum variant.
- `EnumDecoderString` which decodes the name of the enum variant.

#### Constant Decoders

Constant decoders are used to decode a constant value. The two base factories to create them are accept whether the
constant value that will be decoded or a function that returns the constant value.

For example:

```kt
val constantDecoder: Decoder<Int> = ConstantDecoder(42)
val nowDecoder: Decoder<Long> = ConstantDecoder { System.currentTimeMillis() }
```

There also are two predefined factories for the most common constant decoders, which are:
- `NullDecoder` which always decodes `null` ;
- `UnitDecoder` which always decodes `Unit`.

### Common Decoders

Common decoders decode more complex types that are commonly used, and are composed of other decoders.

Here is a list of the provided common decoders:

- `UUIDDecoder` which decodes a `UUID` from two `Long`s.
- `UUIDStringDecoder` which decodes a `UUID` from a `String` using the `UUID.fromString()` method.
- `PairDecoder<T, U>` which composes two decoders (of `T` and `U`) to decode a `Pair<T, U>`.
- Time related decoders :
    - `InstantDecoder`
    - `LocalTimeDecoder`
    - `LocalDateDecoder`
    - `LocalDateTimeDecoder`
    - `DateDecoder`
    - `ZoneIdDecoder`
    - `ZonedDateTimeDecoder`

