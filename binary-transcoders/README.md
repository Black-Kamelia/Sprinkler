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

See [Encoders.md](Encoders.md)


## Decoders

See [Decoders.md](Decoders.md)
