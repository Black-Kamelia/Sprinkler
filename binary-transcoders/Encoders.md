# Encoders

Encoders serve the purpose of serializing data into a binary format. They are stateless, and can be reused anytime, and
should serialize the data in both a deterministic and predictable manner, in one go (one call the `encode` function).

We will see that the API provides building blocks to create encoders for any type of data, and compose them together
to create more complex encoders very easily.

For simplification and readability purposes, every class name referenced in the following sections will not be written
in their fqname form, but rather in their simple name form. For example, `com.kamelia.sprinkler.transcoder.binary.encoder.Encoder`
will be written as `Encoder`. The same goes for all classes in the package `com.kamelia.sprinkler.transcoder.binary.encoder`.

## Summary

- [Main Interfaces](#main-interfaces)
  - [Encoder](#encoder)
  - [EncoderOutput](#encoderoutput)
- [Provided Encoders](#provided-encoders)
  - [Base Encoders](#base-encoders)
    - [Primitive Encoders](#primitive-encoders)
    - [String Encoders](#string-encoders)
    - [Enum Encoders](#enum-encoders)
    - [NoOp Encoder](#noop-encoder)
  - [Common Encoders](#common-encoders)
- [Encoder Mappers](#encoder-mappers)
  - [Encoder::withMappedInput](#encoderwithmappedinput)
  - [Iterables](#iterables)
  - [Nullable](#nullable)
- [Encoder Composition](#encoder-composition)
  - [EncodingScope Interface](#encodingscope-interface)
  - [Scope Usage](#scope-usage)

## Main Interfaces

The two main bricks of this API are the `Encoder` and `EncoderOutput` interfaces.

They are used to define the behavior of the encoder and how to output the encoded data,
respectively. While they have several methods, only a few need to actually be implemented in each of them, because the
others have default implementations depending on them.

### Encoder

An `Encoder<T>` should implement at least the `encode(obj: T, output: EncoderOutput)` method. It is the main brick of
the API, and is used to define the behavior of the encoder. It should serialize the object `obj` to the `output`.

From this, there are default implementations to write to other common outputs, such as a `ByteArray`, an `OutputStream`,
a `File`, or a `Path`. You can also implement your own `EncoderOutput` to write to something else 
(see [EncoderOutput](#encoderoutput)).

As stated before, an Encoder's implementation should be stateless, and deterministic. This means that it should always
output the same data for the same input.

Here is a simple example of an encoder:

```kt
class MyBytePair(val first: Byte, val second: Byte)

val myEncoder: Encoder<MyBytePair> = Encoder<MyBytePair> { obj, output ->
    output.write(obj.first)
    output.write(obj.second)
}
```

This encoder simply writes the first and second bytes of the `MyBytePair` object to the `EncoderOutput` sequentially.
Note that the `EncoderOutput` passed as an argument in the `Encoder`'s implementation is the only way one should output
the encoded data.

That way, we can use this encoder to encode a `MyBytePair` object to a `ByteArray`:

```kt
val myBytePair: Encoder<MyBytePair> = MyBytePair(1, 2)
val encoded: ByteArray = myEncoder.encode(myBytePair)
```

Here, we can see that, by default, if no `EncoderOutput` is given, the encoder will create an `EncoderOutput` that
writes to a `ByteArray` and returns it. We will see in the next section how to use a custom `EncoderOutput`.

> **WARNING**
> ```kt
> fun myEncoder(file: File): Encoder<MyBytePair> = Encoder<MyBytePair> { obj, _ ->
>     file.writeBytes(byteArrayOf(obj.first, obj.second))
> }
> ```
> Here, we are writing to something that is NOT the provided output. This is highly unadvised, and should NEVER be done.
> If done anyway, the behavior of the encoder becomes completely undefined.

### EncoderOutput

Basically, the `EncoderOutput` is an abstraction that serves to map the behavior of an object to that of something similar
to an `OutputStream`. It is used to output the encoded data, and is passed to the `Encoder` when encoding. 

The methods that need to be implemented are `writeBit(bit: Int)` and `flush()`. 

The `writeBit` method writes a single bit to the output. Only the least significant bit of the given `Int` is written,
and all other bits are ignored. The `flush` method flushes the output, to force any buffered bytes to be written. 
This method is useful when the writing of byte is finished but the last byte is not full and therefore has not been 
written yet. All the padding bits appended to the last byte are set to `0`.

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

## Provided Encoders

This library provides a lot of essential "atomic" encoders, which are used to encode most of the basic types, and some
more complex but common ones, as well as factories to create them easily.

### Base Encoders

#### Primitive Encoders

Base encoders are the most basic encoders, and encode all the primitive types, as well as enum variants, and even `String`s
to a chosen encoding.

Indeed, the provided factories primitive encoders are :
- `ByteEncoder`
- `ShortEncoder`
- `IntEncoder`
- `LongEncoder`
- `FloatEncoder`
- `DoubleEncoder`
- `BooleanEncoder`

All of those, except for `ByteEncoder` and `BooleanEncoder`, accept `ByteOrder` as an argument, which is used to
determine the byte order (endianness) of the encoded data.
If you do not provide one, it will default to `ByteOrder.BIG_ENDIAN`.

#### String Encoders

In binary encoding in general, there are two major ways to encode strings of text (aside from the encoding):
- Fixed-length encoding, where the length of the string is known beforehand, and is encoded before the string itself.
- Variable-length encoding, where the length of the string is not known beforehand, in which case, a terminator flag
  (or, end marker) is encoded after the string to indicate the end of the string, in the same vein as C-style strings, 
  which are null-terminated.

To that effect, the two main string encoder factories are two overloads of `StringEncoder`, both of them accept a
`Charset`, but also an `Encoder<Int>` to encode the length of the string, or a `ByteArray` corresponding to the end
marker, respectively.

For example:

```kt
val utf8PrefixedLengthEncoder: Encoder<String> = StringEncoder(Charsets.UTF_8, IntEncoder())
val asciiNullTerminatedEncoder: Encoder<String> = StringEncoder(Charsets.US_ASCII, byteArrayOf(0))
```

Fortunately, there are also some predefined factories for the most common encodings, which are:
- `UTF8StringEncoder`
- `UTF16StringEncoder`
- `ASCIIStringEncoder`
- `Latin1StringEncoder`
- `UTF8StringEncoderEM` (EM stands for End Marker)
- `UTF16StringEncoderEM` (EM stands for End Marker)
- `ASCIIStringEncoderEM` (EM stands for End Marker)
- `Latin1StringEncoderEM` (EM stands for End Marker)

They have sensible defaults for the length encoder, and the end marker, and are the recommended way to encode strings.

#### Enum Encoders

Enum encoders are used to encode enum variants. They are very simple, and come in two flavors:
- `EnumEncoder` which encodes the ordinal of the enum variant.
- `EnumEncoderString` which encodes the name of the enum variant.

#### NoOp Encoder

It's an encoder which... does nothing ! It is useful to swallow some data when encoding it.

### Common Encoders

Common encoders encode more complex types that are commonly used, and are composed of other encoders.

Here is a list of the provided common encoders:
- `UUIDEncoder` which encodes a `UUID` as two `Long`s.
- `UUIDStringEncoder` which encodes a `UUID` as a `String` using the `UUID.toString()` method.
- `PairEncoder<T, U>` which composes two encoders (of `T` and `U`) to encode a `Pair<T, U>`.
- Time related encoders :
    - `InstantDecoder`
    - `LocalTimeEncoder`
    - `LocalDateEncoder`
    - `LocalDateTimeEncoder`
    - `DateEncoder`
    - `ZoneIdEncoder`
    - `ZonedDateTimeEncoder`

## Encoder Mappers

Often times, one will want to encode a derived type from a base type, or a collection of a type we already have an
encoder for. To that effect, there are several encoder mappers that can be used to map an encoder to another type.

### Encoder::withMappedInput

The first one is `Encoder<T>.withMappedInput(mapper: (R) -> T): Encoder<R>`, and simply transforms an encoder of `T` to
an encoder of `R` by mapping the input of the encoder to `T` to an input of `R` using the given mapper.

For example :

```kt
val longEncoder: Encoder<Long> = LongEncoder()
val instantEncoder: Encoder<Instant> = longEncoder.withMappedInput(Instant::toEpochMilli)
```

The above code creates an encoder that encodes `Instant` objects by first converting them to a
long value using `Instant::toEpochMilli`, and then encoding the long value using the longEncoder.

### Iterables

In the exact same vein as string encoders, there are two ways to encode iterables:
- Fixed-length encoding, where the length of the iterable is known beforehand, and is encoded before the iterable itself.
- Variable-length encoding, where the length of the iterable is not known beforehand, in which case, a terminator flag
  (or, end marker) is encoded after the iterable to indicate the end of the iterable.

In the real world, `Iterable`s are often encoded using variable-length encoding, and to that effect,
`toIterable` transforms an encoder of `T` to an encoder of `Iterable<T>` using variable-length encoding.

```kt
val intEncoder: Encoder<Int> = IntEncoder()
val intIterableEncoder: Encoder<Iterable<Int>> = intEncoder.toIterable()
```

By opposition, `toCollection` transforms an encoder of `T` to an encoder of `Collection<T>` using fixed-length encoding.

```kt
val intEncoder: Encoder<Int> = IntEncoder()
val intIterableEncoder: Encoder<Collection<Int>> = intEncoder.toCollection()
```

`toArray` comes in the two variations, and transforms an encoder of `T` to an encoder of `Array<T>`.

```kt
val intEncoder: Encoder<Int> = IntEncoder()
val intIterableEncoder: Encoder<Array<Int>> = intEncoder.toArray()
```

Similarly, `toMap` also comes in the two variations, and transforms an encoder of `Pair<T, U>` to an encoder of 
`Map<T, U>`, OR an encoder of `K` to an encoder of `Map<K, V>` using an encoder of `V` as an argument.

```kt
val stringEncoder: Encoder<String> = UTF8StringEncoder()
val intEncoder: Encoder<Int> = IntEncoder()

val stringToIntEncoder: Encoder<Map<String, Int>> = PairEncoder(stringEncoder, intEncoder).toMap()
// OR
val stringToIntEncoder: Encoder<Map<String, Int>> = stringEncoder.toMap(intEncoder)
```

### Nullable

`toOptional` transforms an encoder of `T` to an encoder of `T?` using a prefixed encoded boolean to determine the
presence of the value.

```kt
val booleanEncoder: Encoder<Boolean> = BooleanEncoder()
val nullableBooleanEncoder: Encoder<Boolean?> = booleanEncoder.toOptional()
```

## Encoder Composition

The heart and goal of this library is to provide a way to compose atomic encoders together to create more complex
encoders, which can in turn be used to compose even more complex encoders, and so on.

For that, the API provides a neat DSL to compose encoders together, and even allow easy encoding of recursive data
structures (a data structure which contains a nullable reference to its own type, or a collection of objects of its own
type).

### EncodingScope Interface

To manipulate the composition API, one must use the `EncodingScope` interface, which is a receiver interface, and is
provided via the `composedEncoder` top level factory function. One should not implement this interface directly, but
rather use provided implementations of it.

The `EncodingScope<E>` interface provides the following methods:
- `<T>encode(obj: T, encoder: Encoder<T>)` which encodes the given object using the given encoder.
- `encode(obj: Byte)`, `encode(obj: Short)`, `encode(obj: Int)`, `encode(obj: Long)`, `encode(obj: Float)`,
  `encode(obj: Double)`, and `encode(obj: Boolean)`, which encode the given object using the appropriate primitive 
  encoder (the encoder is automatically provided).
- Recursive encoding functions, which recursively encodes an object of the same type as the scope, that is to say, 
  the type of the encoder to create in the end:
  - `encode(obj: E?)` which recursively encodes itself or null (null is the recursion end marker).
  - `encode(obj: Collection<E>)` and `encode(obj: Array<E>)` which recursively encodes a collection or array of itself.

The recursive magic happens because the `EncodingScope` is aware of the resulting encoder even before it is built. 
This mocked encoder is provided through the `self` property.

> **NOTE**: The `self` encoder should only be used in the current scope. Any use of this encoder outside the current 
> scope may lead to unexpected results and can change the behaviour of the scope itself.

### Scope Usage

The `composedEncoder` top level factory function which uses the aforementioned `EncodingScope` as its receiver, 
and is used to create a composed encoder, and takes a lambda as an argument, which receives the object it will have to 
encode, and all of its properties. The user of the API only has to tell the composed encoder which properties to encoder
sequentially (and potentially conditionally but deterministic way), with which encoder. To help clarity, one does not
need to provide an explicit encoder for primitive types, strings, or when recursively encoding.

Here is a simple example of creation of a composed encoder:

```kt
class Person(val name: String, val age: Int)

val personEncoder: Encoder<Person> = composedEncoder<Person> {
    encode(it.name)
    if (it.age < 0) { // conditional encoding
        encode(0)
    } else {
        encode(it.age)
    }
}
```

Here is a more complex example of creation of several encoders using only encoder composition, the provided factories,
and recursive encoding:

```kt
class Location(val coords: Pair<Double, Double>, val name: String)
class Person(
  val name: String, 
  val age: Int, 
  val children: List<Person> = emptyList(), 
  val godParent: Person? = null, 
  val location: Location? = null,
)

val doubleEncoder: Encoder<Double> = DoubleEncoder()
val coordsEncoder: Encoder<Pair<Double, Double>> = doubleEncoder and doubleEncoder // `and` is an infix shorthand for PairEncoder
val locationEncoder: Encoder<Location> = composedEncoder<Location> {
    encode(it.coords, coordsEncoder)
    encode(it.name)
}
val optionalLocationEncoder: Encoder<Location?> = locationEncoder.toOptional()
val personEncoder: Encoder<Person> = composedEncoder<Person> {
    encode(it.name)
    encode(it.age)
    encode(it.children) // recursively encode a collection of itself
    encode(it.godParent) // recursively encode itself, or null
    encode(it.location, optionalLocationEncoder)
}

val person = Person(
    name = "John",
    age = 42,
    children = listOf(
        Person(name = "Jane", age = 12, godParent = Person(name = "Alice", age = 35)),
        Person(name = "Jack", age = 8)
    ),
    godParent = Person(name = "Jamesus", age = 2023),
    location = Location(coords = 42.0 to 42.0, name = "Paris")
)

val encodedPerson: ByteArray = personEncoder.encode(person)
```
