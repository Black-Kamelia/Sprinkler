package com.kamelia.sprinkler.serializer.binary

interface Serializer<in T> {
    fun serialize(obj: T): BinaryBuffer
}
