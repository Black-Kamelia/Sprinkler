@file:JvmName("ComposedDecoder")

package com.kamelia.sprinkler.codec.binary.decoder.composer

import com.kamelia.sprinkler.codec.binary.decoder.UTF8StringDecoder
import com.kamelia.sprinkler.codec.binary.decoder.core.Decoder
import com.kamelia.sprinkler.codec.binary.decoder.core.DecoderInput
import com.kamelia.sprinkler.codec.binary.encoder.composer.composedEncoder
import com.zwendo.restrikt.annotation.PackagePrivate
import java.nio.ByteOrder
import java.util.stream.Collectors

/**
 *
 */
@JvmOverloads
@JvmName("create")
fun <T> composedDecoder(
    endianness: ByteOrder = ByteOrder.BIG_ENDIAN,
    stringDecoder: Decoder<String> = UTF8StringDecoder(),
    block: DecodingScope<T>.() -> T,
): Decoder<T> {
    val cache = HashMap<Class<*>, Decoder<*>>().apply {
        put(String::class.java, stringDecoder)
    }
    return ComposedDecoderImpl(endianness, cache, block)
}

private class ComposedDecoderImpl<E>(
    private val endianness: ByteOrder,
    private val cache: HashMap<Class<*>, Decoder<*>>,
    private val block: DecodingScope<E>.() -> E,
) : Decoder<E> {

    private var elements = ElementsAccumulator()

    private var resetOnNextCall = false

    override fun decode(input: DecoderInput): Decoder.State<E> {
        while (true) {
            val scope = DecodingScopeImpl<E>(input, elements, cache, endianness, resetOnNextCall)
            resetOnNextCall = false
            try {
                val result = scope.block()
                if (elements.isLastLayer) { // there is no recursion layer, we are done
                    return Decoder.State.Done(result)
                }

                elements.popRecursion() // else, we pop the recursion layer
                elements.addToRecursion(result) // and add the result to the previous layer
            } catch (_: ProcessingMarker) { // bytes are missing
                return Decoder.State.Processing(scope.processingReason)
            } catch (_: RecursionMarker) { // recursion
                elements.recurse()
            } catch (e: Exception) { // an error occurred
                return Decoder.State.Error(e)
            }
        }
    }

    override fun reset() {
        elements = ElementsAccumulator()
        resetOnNextCall = true
    }

}

data class Person(
    val name: String,
    val age: Int,
    val height: Float,
    val weight: Float,
    val isMarried: Boolean,
    val children: List<Person>,
)

fun main() {
    val encoder = composedEncoder<Person> {
        encode(it.name)
        encode(it.age)
        encode(it.height)
        encode(it.weight)
        encode(it.isMarried)
        encode(it.children)
    }

    val decoder = composedDecoder<Person> {
        val name = string()
        val age = int()
        val height = float()
        val weight = float()
        val isMarried = boolean()
        val children = selfCollection(Collectors.toList())

        Person(name, age, height, weight, isMarried, children)
    }

    val jack = Person("Jack", 12, 1.5f, 40f, false, emptyList())
    val john = Person("John", 42, 1.8f, 80f, true, listOf(jack))

    val encoded = encoder.encode(john)
    val part1 = encoded.sliceArray(0 until 18)
    val part2 = encoded.sliceArray(18 until encoded.size)

    decoder.decode(part1)
    val decoded = decoder.decode(part2)
    println(decoded)
}

@PackagePrivate
@Suppress("UNCHECKED_CAST")
internal fun <T> Any?.tryCast(): T = this as? T
    ?: throw IllegalStateException(
        """
        Error while trying to cast $this. 
        This error may have been caused because different calls have been made in the the scope for the same object,
        between two calls of the block. Calls in the scope must be consistent for the same object between two calls.
            """.trimIndent()
    )
