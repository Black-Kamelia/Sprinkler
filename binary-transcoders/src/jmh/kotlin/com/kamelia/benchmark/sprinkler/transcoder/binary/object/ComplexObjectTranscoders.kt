package com.kamelia.benchmark.sprinkler.transcoder.binary.`object`

import com.kamelia.sprinkler.transcoder.binary.decoder.composer.composedDecoder
import com.kamelia.sprinkler.transcoder.binary.decoder.core.Decoder
import com.kamelia.sprinkler.transcoder.binary.encoder.composer.composedEncoder
import com.kamelia.sprinkler.transcoder.binary.encoder.core.Encoder

class ComplexPerson(
    val firstname: String,
    val lastname: String,
    val age: Int,
    val height: Float,
    val weight: Float,
    val isMarried: Boolean,
    val address: Address,
    val phoneNumber: String,
)

class Address(
    val streetNumber: Int,
    val streetName: String,
    val city: String,
    val zipcode: String,
    val country: String,
    val extraInformation: String?,
)

fun addressDecoder(): Decoder<Address> = composedDecoder {
    val streetNumber = int()
    val streetName = string()
    val city = string()
    val zipcode = string()
    val country = string()

    val extraInformation = if (boolean()) {
        string()
    } else {
        null
    }

    Address(streetNumber, streetName, city, zipcode, country, extraInformation)
}

fun complexPersonDecoder(): Decoder<ComplexPerson> {
    val addressDecoder = addressDecoder()
    return composedDecoder {
        val firstname = string()
        val lastname = string()
        val age = int()
        val height = float()
        val weight = float()
        val isMarried = boolean()
        val address = decode(addressDecoder)
        val phoneNumber = string()

        ComplexPerson(firstname, lastname, age, height, weight, isMarried, address, phoneNumber)
    }
}

val addressEncoder: Encoder<Address> = composedEncoder {
    encode(it.streetNumber)
    encode(it.streetName)
    encode(it.city)
    encode(it.zipcode)
    encode(it.country)
    if (it.extraInformation != null) {
        encode(true)
        encode(it.extraInformation)
    } else {
        encode(false)
    }
}

val complexPersonEncoder: Encoder<ComplexPerson> = composedEncoder {
    encode(it.firstname)
    encode(it.lastname)
    encode(it.age)
    encode(it.height)
    encode(it.weight)
    encode(it.isMarried)
    encode(it.address, addressEncoder)
    encode(it.phoneNumber)
}
