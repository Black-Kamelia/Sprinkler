# Decoders

example which matches the encoder composer example
```kt
class Location(val coords: Pair<Double, Double>, val name: String)
class Person(val name: String, val age: Int, val children: List<Person>, val godParent: Person?, val location: Location?)

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
    val godParent = selfOrNull()
    val location = decode(optionalLocationDecoder)
    Person(name, age, children, godParent, location)
}
```