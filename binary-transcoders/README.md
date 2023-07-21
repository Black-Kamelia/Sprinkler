# Sprinkler: Binary Transcoders

## Summary

- [Intentions](#intentions)
- [Quick Examples](#quick-examples)
  - [Basic Usage](#basic-usage)
  - [Recursive Structure](#recursive-structure)
- [Encoders](#encoders)
- [Decoders](#decoders)

## Intentions

The purpose of this module is to allow the user to serialize and deserialize data in a binary format. 
This is useful for sending data over the network, or storing data in a file.

The module is designed to permit the user to define the structure of the transcoders easily and with a
structured API. The user can define the structure of the transcoder using a builder pattern,
and add new types of transcoders by implementing simple interfaces.

## Getting Started

Here is a very simple example of how to use the module:

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

For more details on how to use the module, please refer to the following sections.

## Encoders

Encoders are used to serialize data into a binary format. They are stateless, and can be reused multiple times. For
a complete guide on how to use encoders, see [Encoders.md](Encoders.md).

## Decoders

Decoders are used to deserialize data from a binary format. They are stateful, and should be created each time.
For a complete guide on how to use decoders, see [Decoders.md](Decoders.md).
