package com.kamelia.sprinkler.codec.binary.encoder.composer

import com.kamelia.sprinkler.codec.binary.encoder.EncoderPool
import com.kamelia.sprinkler.codec.binary.encoder.core.Encoder
import com.kamelia.sprinkler.codec.binary.encoder.core.EncoderOutput
import com.kamelia.sprinkler.codec.binary.util.PoolKey

class Composer<T>(
    private val pool: EncoderPool,
    private val output: EncoderOutput,
    private val selfEncoder: Encoder<T>,
) {

    fun <E> encodeWith(value: E, encoder: Encoder<E>): Composer<T> = apply {
        encoder.encode(value, output)
    }

//    fun encodeRecursivelyIfNotNull(value: T?): Composer<T> = encodeWith(value, selfEncoder)

    fun <E> encode(key: PoolKey<E>, value: E): Composer<T> = apply {
        val encoder = pool.encoder(key)
        encoder.encode(value, output)
    }

    fun <E> encode(clazz: Class<E>, value: E): Composer<T> = encode(PoolKey.of(clazz), value)

    fun encode(value: Byte): Composer<T> = encode(Byte::class.java, value)

    fun encode(value: Short): Composer<T> = encode(Short::class.java, value)

    fun encode(value: Int): Composer<T> = encode(Int::class.java, value)

    fun encode(value: Long): Composer<T> = encode(Long::class.java, value)

    fun encode(value: Float): Composer<T> = encode(Float::class.java, value)

    fun encode(value: Double): Composer<T> = encode(Double::class.java, value)

    fun encode(value: Boolean): Composer<T> = encode(Boolean::class.java, value)

    inline fun <reified E> encode(value: E): Composer<T> = encode(E::class.java, value)

}


fun <T> composedEncoder(
    pool: EncoderPool,
    block: Composer<T>.(T) -> Unit,
): Encoder<T> = object : Encoder<T> {

    override fun <O : EncoderOutput> encode(obj: T, output: O): O = output.also {
        Composer(pool, it, this).block(obj)
    }

}

data class Person(
    val name: String,
    val age: Int,
)

fun main() {
//    val pool = EncoderPool.create().apply {
//        addEncoder(UTF8StringEncoder())
//        addEncoder(IntEncoder())
//    }
//
//    val encoder = composedEncoder<Person>(pool) {
//        encode(it.name)
//
//        if (it.age < 18) {
//            encode(it.age)
//        } else {
//            encode(0)
//        }
//    }
//
//    val array = encoder.encodeToByteArray(Person("John", 16))
//    println(array.contentToString())
}
