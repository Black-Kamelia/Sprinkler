# Sprinkler: Binary Transcoders

## Summary

- [Intentions](#intentions)
- [Quick Examples](#quick-examples)
  - [Basic Usage](#basic-usage)
  - [Recursive Structure](#recursive-structure)
- [Encoders](#encoders)
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
      
    
    

## Intentions

The purpose of this module is to allow the user to serialize and deserialize data in a binary format. 
This is useful for sending data over the network, or storing data in a file.

The module is designed to permit the user to define the structure of the transcoders easily and with a
structured API. The user can define the structure of the transcoder using a builder pattern,
and add new types of transcoders by implementing simple interfaces.

## Quick Examples

### Basic Usage

```kt
data class Person(val name: String, val age: Int) {
    
    companion object {
        val encoder = composedEncoder<Person> {
            encode(it.name)
            encode(it.age)
        }
        
        fun decoder() = composedDecoder<Person> {
            val name = string()
            val age = int() 
            Person(name, age)
        }
    }
}

fun main() {
    val person = Person("John", 42)
    val encoder = Person.encoder
    val decoder = Person.decoder()
    
    val encodedPerson: ByteArray = encoder.encode(person)
    val decodedPerson: Decoder.State<Person> = decoder.decode(encodedPerson)
    
    println(decodedPerson.get())
}
```

In this configuration, we define both an encoder and a decoder for the `Person` class.
As you can see, both of them are created thanks to a simple DSL builder, in a sequential manner.

In the case of the encoder (which is stateless), we simply tell the builder to first encode the name,
and then the age. Behind the scenes, the builder will also create primitive encoders 
for the `String` and `Int` types automatically.

In the case of the decoder (which is stateful), we tell the builder to first decode a `String`,
thanks to the scoped `string()` function, and then to decode an `Int`, thanks to the scoped `int()` function.
To finalize the decoder, we create and return a `Person` instance with the decoded values.

Note that because the decoder is stateful, we should create a new instance of the decoder each time we want to use it.

### Recursive Structure

```kt
class Node(val value: Int, val children: List<Node>) {
    
    companion object {
        val encoder = composedEncoder<Node> {
            encode(it.value)
            encode(it.children) // recursive encoding
        }
        
        fun decoder() = composedDecoder<Node> {
            val value = int()
            val children = selfList() // recursive decoding
            Node(value, children)
        }
    }
}

class Person(val name: String, val lover: Person?) {
    
    companion object {
        val encoder = composedEncoder<Person> {
            encode(it.name)
            encode(it.lover) // recursive encoding
        }
        
        fun decoder() = composedDecoder<Person> {
            val name = string()
            val lover = selfOrNull() // recursive decoding
            Person(name, lover)
        }
    }
}
```

It is also possible to transcode recursive structures, as shown in the example above. 
Recursive encoding happens when the structure contains a reference to itself, be it
as a collection of elements of its own type (like in the Node example above), as a collection of nullable elements
of its own type, or as a nullable field of its own type (like in the Person example above).

To perform recursive encoding, the API is seamless: the user simply has to call the `encode` function
as usual, and the encoder will take care of the rest.

Recursive decoding is a bit more complex because of predictability issues. In this case, the API provides
several scoped functions. Two of these are `selfList` and `selfOrNull`, which are used to decode a list of elements
of the same type as the one being decoded, and a nullable element of the same type as the one being decoded, 
respectively.

## Encoders

Encoders serve the purpose of serializing data into a binary format. They are stateless, and can be reused anytime, and
should serialize the data in both a deterministic and predictable manner, in one go (one call the `encode` function).

We will see that the API provides building blocks to create encoders for any type of data, and compose them together
to create more complex encoders very easily.

For simplification and readability purposes, every class name referenced in the following sections will not be written
in their fqname form, but rather in their simple name form. For example, `com.kamelia.sprinkler.transcoder.binary.encoder.Encoder`
will be written as `Encoder`. The same goes for all classes in the package `com.kamelia.sprinkler.transcoder.binary.encoder`.

### Main Interfaces

The two main building blocks of this API are the `Encoder` and `EncoderOutput` interfaces.

Both are functional interfaces, and are used to define the behavior of the encoder and how to output the encoded data,
respectively. While they have several methods, you only *need* to implement only one in each of them, because the others
have default implementations depending on it.

#### Encoder

An `Encoder<T>` is an object which must at least accept an object of type `T` to serialize and write to an injected
`EncoderOutput`. It is the main building block of the API, and is used to define the behavior of the encoder.

From this, there are default implementations to write to either a `ByteArray`, an `OutputStream`, a `File`, or a
`Path`. You can also implement your own `EncoderOutput` to write to something else (see [EncoderOutput](#encoderoutput)).

As stated before, an Encoder's implementation should be stateless, and deterministic. This means that it should always
output the same data for the same input, and that it should not have any side effects (or at least limit them in a
predictable manner).

Here is a simple example of an encoder

```kt
class MyBytePair(val first: Byte, val second: Byte)

val myEncoder = Encoder<MyBytePair> { obj, output ->
    output.write(obj.first)
    output.write(obj.second)
}
```

This encoder simply writes the first and second bytes of the `MyBytePair` object to the `EncoderOutput` sequentially.
Note that the `EncoderOutput` is injected in the `Encoder`'s implementation, and that it is the only way to output the
encoded data.

That way, we can use this encoder to encode a `MyBytePair` object to a `ByteArray`:

```kt
val myBytePair = MyBytePair(1, 2)
val encoded: ByteArray = myEncoder.encode(myBytePair)
```

Here, we can see that, by default, if no `EncoderOutput` is injected, the encoder will create an `EncoderOutput` that
writes to a `ByteArray` to return it.

#### EncoderOutput

Basically, the `EncoderOutput` is an adapter that serves to map the behavior of an object to that of something similar
to an `OutputStream`. It is used to output the encoded data, and is passed to the `Encoder` when encoding. Indeed,
say we want to output the encoded to *stdout*, here's an example of how we would do it:

```kt
val stdoutEncoderOutput = EncoderOutput { byte -> print(byte) }

stdoutEncoderOutput.write(42) // prints 42
stdoutEncoderOutput.write(byteArrayOf(1, 2, 3)) // prints 1, 2 and 3
```

(Obviously, this is not the most efficient way to do it, but it is just an example. A more efficient way would be to
also implement the `write(bytes: ByteArray)` method, and use a `ByteArrayOutputStream`.)

You can now use this `stdoutEncoderOutput` to output the encoded data to *stdout*.

```kt
UTF8StringEncoder().encode("Hello, World!", stdoutEncoderOutput) // prints the UTF-8 encoded bytes of "Hello, World!"
```

It is to be noted that the `Encoder::encode` function actually acts as if the given `EncoderOutput` writes to a
`ByteArray` by default, if you do not provide one.

There are also several helpers `encode` method which take in an `OutputStream`, a `File`, or a `java.nio.Path` 
as an argument, and automatically create an `EncoderOutput` for you behind the scenes.

```kt
val encoder = UTF8StringEncoder()
encoder.encode("Hello, World!", System.out)
encoder.encode("Hello, World!", File("hello.txt"))
encoder.encode("Hello, World!", Path.of("hello.txt"))
```

### Provided Encoders

Fortunately for you, this library provides a lot of essential "atomic" encoders, which are used to encode most of the
basic types, and some more complex but common ones, as well as factories to create them easily.

#### Base Encoders

##### Primitive Encoders

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

##### String Encoders

In binary encoding in general, there are two major ways to encode strings of text (aside from the encoding):
- Fixed-length encoding, where the length of the string is known beforehand, and is encoded before the string itself.
- Variable-length encoding, where the length of the string is not known beforehand, in which case, a terminator flag
  (or, end marker) is encoded after the string to indicate the end of the string. You can see the similarities with the
  C-style strings, which are null-terminated.

To that effect, the two main string encoder factories are two overloads of `StringEncoder`, both of them accept a
`Charset`, but also an `Encoder<Int>` to encode the length of the string, or a `ByteArray` corresponding to the end marker,
respectively.

For example:

```kt
val utf8PrefixedLengthEncoder = StringEncoder(Charsets.UTF_8, IntEncoder())
val asciiNullTerminatedEncoder = StringEncoder(Charsets.US_ASCII, byteArrayOf(0))
```

Fortunately, there are also some predefined factories for the most common encodings, which are:
- `UTF8StringEncoder`
- `UTF16StringEncoder`
- `ASCIIStringEncoder`
- `UTF8StringEncoderEM` (EM stands for End Marker)
- `UTF16StringEncoderEM` (EM stands for End Marker)
- `ASCIIStringEncoderEM` (EM stands for End Marker)

They have sensible defaults for the length encoder, and the end marker, and are the recommended way to encode strings.

##### Enum Encoders

Enum encoders are used to encode enum variants. They are very simple, and come in two flavors:
- `EnumEncoder` which encodes the ordinal of the enum variant.
- `EnumEncoderString` which encodes the name of the enum variant.

##### NoOp Encoder

It's an encoder which... does nothing ! It is useful to swallow some data when encoding it.

#### Common Encoders

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

### Encoder Mappers

#### Encoder::withMappedInput

Often times, you will want to encode a derived type from a base type, or a collection of a type you already have an
encoder for. To that effect, there are several encoder mappers that can be used to map an encoder to another type.

The first one is `Encoder<T>.withMappedInput(mapper: (R) -> T): Encoder<R>`, and simply transforms an encoder of `T` to
an encoder of `R` by mapping the input of the encoder to `T` to an input of `R` using the given mapper.

For example :
```kt
val longEncoder = LongEncoder()
val instantEncoder: Encoder<Instant> = longEncoder.withMappedInput(Instant::toEpochMilli)
```

The above code creates an encoder that encodes `Instant` objects by first converting them to a
long value using `Instant::toEpochMilli`, and then encoding the long value using the longEncoder.

#### Iterables

In the exact same vein as string encoders, there are two ways to encode iterables:
- Fixed-length encoding, where the length of the iterable is known beforehand, and is encoded before the iterable itself.
- Variable-length encoding, where the length of the iterable is not known beforehand, in which case, a terminator flag
  (or, end marker) is encoded after the iterable to indicate the end of the iterable.

In the real world, `Iterable`s are often encoded using variable-length encoding, and to that effect,
`toIterable` transforms an encoder of `T` to an encoder of `Iterable<T>` using variable-length encoding.

By opposition, `toCollection` transforms an encoder of `T` to an encoder of `Collection<T>` using fixed-length encoding.

`toArray` comes in the two variations, and transforms an encoder of `T` to an encoder of `Array<T>`.

Similarly, `toMap` transforms an encoder of `Pair<T, U>` to an encoder of `Map<T, U>` using variable-length encoding.

#### Nullable

`toOptional` transforms an encoder of `T` to an encoder of `T?` using a prefixed encoded boolean to determine the
presence of the value.

### Encoder Composition

The heart and goal of this library is to provide a way to compose atomic encoders together to create more complex encoders,
which can in turn be used to compose even more complex encoders, and so on.

For that, the API provides a neat DSL to compose encoders together, and even allow easy encoding of recursive data
structures (a data structure which contains a nullable reference to its own type, or a collection of objects of its own type).

The `composedEncoder` top level factory function is used to create a composed encoder, and takes a lambda as an argument,
which receives an image of the object it will have to encode, and all of its properties. The user of the API only has to
tell the composed encoder which properties to encoder sequentially (and potentially conditionally but deterministic way),
with which encoder. To help clarity, one does not need to provide an explicit decoder for primitive types, strings, or
when recursively encoding.

Here is a complex example of creation of several encoders using only encoder composition and the provided factories:

```kt
class Location(val coords: Pair<Double, Double>, val name: String)
class Person(val name: String, val age: Int, val children: List<Person>, val location: Location?)

val doubleEncoder = DoubleEncoder()
val coordsEncoder = PairEncoder(doubleEncoder, doubleEncoder)
val locationEncoder = composedEncoder<Location> {
    encode(it.coords, coordsEncoder)
    encode(it.name)
}
val optionalLocationEncoder = locationEncoder.toOptional()
val personEncoder = composedEncoder<Person> {
    encode(it.name)
    encode(it.age)
    encode(it.children) // recursive encoding
    encode(it.location, optionalLocationEncoder)
}

val person = Person(
    "John",
    42,
    listOf(
        Person("Jane", 12, emptyList(), null),
        Person("Jack", 8, emptyList(), null)
    ),
    Location(Pair(42.0, 42.0), "Paris")
)

val encodedPerson = personEncoder.encode(person)
```






## Decoder

example which matches the encoder composer example
```kt
class Location(val coords: Pair<Double, Double>, val name: String)
class Person(val name: String, val age: Int, val children: List<Person>, val location: Location?)

val doubleDecoder = DoubleDecoder()
val coordsDecoder = PairDecoder(doubleDecoder, doubleDecoder)
val locationDecoder = composedDecoder {
    val coords = decode(coordsDecoder)
    val name = string()
    Location(coords, name)
}
val optionalLocationDecoder = locationDecoder.toOptional()
val personDecoder = composedDecoder {
    val name = string()
    val age = int()
    val children = selfList()
    val location = decode(optionalLocationDecoder)
    Person(name, age, children, location)
}
```
