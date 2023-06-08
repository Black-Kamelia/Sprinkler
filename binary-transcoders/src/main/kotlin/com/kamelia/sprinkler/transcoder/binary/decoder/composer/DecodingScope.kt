package com.kamelia.sprinkler.transcoder.binary.decoder.composer

import com.kamelia.sprinkler.transcoder.binary.decoder.core.Decoder
import com.kamelia.sprinkler.transcoder.binary.decoder.toCollection
import com.kamelia.sprinkler.util.ExtendedCollectors
import java.util.stream.Collector
import java.util.stream.Collectors

/**
 * Represents a scope in which an object can be decoded. This interface is used to compose decoders. It can be used to
 * create a decoders in a more concise way than overriding the [Decoder] interface.
 *
 *
 * In the following example, both syntaxes are almost equivalent:
 *
 * ```
 * class Person(val name: String, val age: Int)
 *
 * // the scope syntax allows to create a decoder in a more concise way
 * val personDecoder = composedDecoder<Person> {
 *     val name = string()
 *     val age = int()
 *     Person(name, age)
 * }
 *
 * // than by overriding the Decoder interface
 * class PersonDecoder : Decoder<Person> {
 *     private val strEncoder = UTF8StringDecoder()
 *     private val intEncoder = IntDecoder()
 *     private var name: String? = null
 *
 *     override fun decode(input: DecoderInput): Decoder.State<Person> {
 *         if (name == null) { // decode the name only once
 *             val result = strEncoder.decode(input)
 *             // return if the decoding is not done
 *             if (result.isNotDone()) return result.mapEmptyState()
 *             name = result.get()
 *         }
 *         val result = intEncoder.decode(input)
 *         // return if the decoding is not done
 *         if (result.isNotDone()) return result.mapEmptyState()
 *         val age = result.get()
 *         val obj = Person(name!!, age)
 *         name = null
 *         return Decoder.State.Done(obj)
 *     }
 *
 *     override fun reset() {
 *         name = null
 *         strEncoder.reset()
 *         intEncoder.reset()
 *     }
 * }
 * ```
 *
 * As shown above, the interface provides several methods to decode the most common types. It therefore allows to
 * decode an object in a more concise way than by overriding the [Decoder] interface. Moreover, it allows to recursively
 * decode an object in an iterative way, meaning that the
 *
 * The interface aims to be as flexible as possible. In this regard, it provides a method (the [decode] method) to
 * decode any type of object provided the right decoder is given.
 *
 * Moreover, for recursive decoding, the interface provides a [self] property that returns the decoder of the current
 * scope. This property can be used to recursively decode an object in a more flexible way than, in case the other
 * methods are not enough.
 *
 * &nbsp;
 *
 * As stated previously, the interface, through its shorthand methods, allows to encode the most common types by using
 * default encoders. These default encoders are not fixed by the interface and therefore depend on the implementation.
 *
 * @param E the type of the object to decode
 * @see Decoder
 * @see composedDecoder
 */
@Suppress("INAPPLICABLE_JVM_NAME")
sealed interface DecodingScope<E> {

    /**
     * Gets the decoder of the scope.
     *
     * Useful to decode recursively an object.
     *
     * &nbsp;
     *
     * **NOTE:** The returned decoder should only be used in the current scope. Any use of this decoder outside the
     * current scope may lead to unexpected results and can change the behaviour of the scope itself.
     */
    @get:JvmName("self")
    val self: Decoder<E>

    /**
     * Decodes an object using the given [decoder].
     *
     * @param decoder the decoder to use
     * @param T the type of the object to decode
     * @return the decoded object
     */
    fun <T> decode(decoder: Decoder<T>): T

    /**
     * Executes the given [block] once per object, meaning that in case of recursive decoding, the block will be
     * executed only once per object. Any subsequent call to this method will return the cached result for the same
     * object.
     *
     * @param block the block to execute
     * @param T the type of the object to decode
     * @return the result of the block
     */
    fun <T> oncePerObject(block: () -> T): T

    /**
     * Skips the given [count] of bytes.
     *
     * @param count the number of bytes to skip
     * @throws IllegalArgumentException if the given [count] is negative
     */
    fun skip(count: Long)

    /**
     * Stops the decoding process and returns the given error state [state].
     *
     * @param state the error state to return
     */
    fun errorState(state: Decoder.State.Error): Nothing

    /**
     * Decodes a byte using the default byte decoder of the scope.
     *
     * @return the decoded byte
     */
    @JvmName("decodeByte")
    fun byte(): Byte

    /**
     * Decodes a short using the default short decoder of the scope.
     *
     * @return the decoded short
     */
    @JvmName("decodeShort")
    fun short(): Short

    /**
     * Decodes an int using the default int decoder of the scope.
     *
     * @return the decoded int
     */
    @JvmName("decodeInt")
    fun int(): Int

    /**
     * Decodes a long using the default long decoder of the scope.
     *
     * @return the decoded long
     */
    @JvmName("decodeLong")
    fun long(): Long

    /**
     * Decodes a float using the default float decoder of the scope.
     *
     * @return the decoded float
     */
    @JvmName("decodeFloat")
    fun float(): Float

    /**
     * Decodes a double using the default double decoder of the scope.
     *
     * @return the decoded double
     */
    @JvmName("decodeDouble")
    fun double(): Double

    /**
     * Decodes a boolean using the default boolean decoder of the scope.
     *
     * @return the decoded boolean
     */
    @JvmName("decodeBoolean")
    fun boolean(): Boolean

    /**
     * Decodes a string using the default string decoder of the scope.
     *
     * @return the decoded string
     */
    @JvmName("decodeString")
    fun string(): String

    /**
     * Recursively decodes a nullable object of type [E] using the default nullability decoder of the scope, which
     * decodes a boolean indicating whether the object is present or not (`true` if present, `false` otherwise).
     *
     * @param nullabilityDecoder the decoder used to decode the nullability of the object
     * @return the decoded object, or `null` if the object is not present
     */
    @JvmName("decodeSelfOrNull")
    fun selfOrNull(nullabilityDecoder: Decoder<Boolean>): E? {
        val isPresent = decode(nullabilityDecoder)
        return if (isPresent) {
            decode(self)
        } else {
            null
        }
    }

    /**
     * Recursively decodes an object of type [E]. The nullability of the object is decoded using the default boolean
     * decoder of the scope.
     *
     * @return the decoded object, or `null` if the object is not present
     */
    @JvmName("decodeSelfOrNull")
    fun selfOrNull(): E?

    /**
     * Recursively decodes a collection of objects of type [E] using the given [collector] and the given [sizeDecoder].
     * The [sizeDecoder] is used to decode the size of the collection which is prepended to the collection itself.
     *
     * @param collector the collector used to collect the decoded objects
     * @param sizeDecoder the decoder used to decode the size of the collection
     * @param R the type of the collection
     * @return the decoded collection
     */
    @JvmName("decodeSelfCollection")
    fun <R> selfCollection(collector: Collector<E, *, R>, sizeDecoder: Decoder<Int>): R {
        val decoder = oncePerObject { self.toCollection(collector, sizeDecoder) }
        return decode(decoder)
    }

    /**
     * Recursively decodes a collection of objects of type [E] using the given [collector] and the default int decoder
     * of the scope. The size of the collection is prepended to the collection itself.
     *
     * @param collector the collector used to collect the decoded objects
     * @param R the type of the collection
     * @return the decoded collection
     */
    @JvmName("decodeSelfCollection")
    fun <R> selfCollection(collector: Collector<E, *, R>): R

    /**
     * Recursively decodes a list of objects of type [E] using the given [sizeDecoder]. The [sizeDecoder] is used to
     * decode the size of the list which is prepended to the list itself.
     *
     * @param sizeDecoder the decoder used to decode the size of the list
     * @return the decoded list
     */
    @JvmName("decodeSelfList")
    fun selfList(sizeDecoder: Decoder<Int>): List<E> = selfCollection(toList(), sizeDecoder)

    /**
     * Recursively decodes a list of objects of type [E] using the default int decoder of the scope. The size of the
     * list is prepended to the list itself.
     *
     * @return the decoded list
     */
    @JvmName("decodeSelfList")
    fun selfList(): List<E> = selfCollection(toList())

    /**
     * Recursively decodes a set of objects of type [E] using the given [sizeDecoder]. The [sizeDecoder] is used to
     * decode the size of the set which is prepended to the set itself.
     *
     * @param sizeDecoder the decoder used to decode the size of the set
     * @return the decoded set
     */
    @JvmName("decodeSelfSet")
    fun selfSet(sizeDecoder: Decoder<Int>): Set<E> = selfCollection(toSet(), sizeDecoder)

    /**
     * Recursively decodes a set of objects of type [E] using the default int decoder of the scope. The size of the
     * set is prepended to the set itself.
     *
     * @return the decoded set
     */
    @JvmName("decodeSelfSet")
    fun selfSet(): Set<E> = selfCollection(toSet())

    /**
     * Recursively decodes an array of objects of type [E] using the given [factory] and the given [sizeDecoder]. The
     * [sizeDecoder] is used to decode the size of the array which is prepended to the array itself. The [factory] is
     * used to create the array.
     *
     * Usually, the [::arrayOfNulls][arrayOfNulls] function is used to create the array.
     *
     * @param factory the factory used to create the array
     * @param sizeDecoder the decoder used to decode the size of the array
     * @return the decoded array
     */
    @JvmName("decodeSelfArray")
    fun selfArray(factory: (Int) -> Array<E?>, sizeDecoder: Decoder<Int>): Array<E> =
        selfCollection(ExtendedCollectors.toArray(factory), sizeDecoder)

    /**
     * Recursively decodes an array of objects of type [E] using the given [factory] and the default int decoder of the
     * scope. The size of the array is prepended to the array itself. The [factory] is used to create the array.
     *
     * Usually, the [::arrayOfNulls][arrayOfNulls] function is used to create the array.
     *
     * @param factory the factory used to create the array
     * @return the decoded array
     */
    @JvmName("decodeSelfArray")
    fun selfArray(factory: (Int) -> Array<E?>): Array<E> = selfCollection(ExtendedCollectors.toArray(factory))

    /**
     * Recursively decodes a nullable collection of objects of type [E] using the given [collector], the given
     * [sizeDecoder] and the given [nullabilityDecoder]. The [sizeDecoder] is used to decode the size of the collection
     * which is prepended to the collection itself. The [nullabilityDecoder] is used to decode the nullability of the
     * collection, which is prepended to the size of the collection.
     *
     * @param collector the collector used to collect the decoded objects
     * @param sizeDecoder the decoder used to decode the size of the collection
     * @param nullabilityDecoder the decoder used to decode the nullability of the collection
     * @param R the type of the collection
     * @return the decoded collection, or `null` if the collection is not present
     */
    @JvmName("decodeSelfCollectionOrNull")
    fun <R> selfCollectionOrNull(
        collector: Collector<E, *, R>,
        sizeDecoder: Decoder<Int>,
        nullabilityDecoder: Decoder<Boolean>,
    ): R? {
        val isPresent = decode(nullabilityDecoder)
        return if (isPresent) {
            selfCollection(collector, sizeDecoder)
        } else {
            null
        }
    }

    /**
     * Recursively decodes a nullable collection of objects of type [E] using the given [collector], the given
     * [sizeDecoder] and the default boolean decoder of the scope. The [sizeDecoder] is used to decode the size of the
     * collection which is prepended to the collection itself. The nullability of the collection is prepended to
     * the size of the collection.
     *
     * @param collector the collector used to collect the decoded objects
     * @param sizeDecoder the decoder used to decode the size of the collection
     * @param R the type of the collection
     * @return the decoded collection, or `null` if the collection is not present
     */
    @JvmName("decodeSelfCollectionOrNull")
    fun <R> selfCollectionOrNull(collector: Collector<E, *, R>, sizeDecoder: Decoder<Int>): R?

    /**
     * Recursively decodes a nullable collection of objects of type [E] using the given [collector] and the default
     * int and boolean decoders of the scope. The size of the collection is prepended to the collection itself. The
     * nullability of the collection is prepended to the size of the collection.
     *
     * @param collector the collector used to collect the decoded objects
     * @param R the type of the collection
     * @return the decoded collection, or `null` if the collection is not present
     */
    @JvmName("decodeSelfCollectionOrNull")
    fun <R> selfCollectionOrNull(collector: Collector<E, *, R>): R?

    /**
     * Recursively decodes a nullable list of objects of type [E] using the given [sizeDecoder] and the given
     * [nullabilityDecoder]. The [sizeDecoder] is used to decode the size of the list which is prepended to the list
     * itself. The [nullabilityDecoder] is used to decode the nullability of the list, which is prepended to the size
     * of the list.
     *
     * @param sizeDecoder the decoder used to decode the size of the list
     * @param nullabilityDecoder the decoder used to decode the nullability of the list
     * @return the decoded list, or `null` if the list is not present
     */
    @JvmName("decodeSelfListOrNull")
    fun selfListOrNull(sizeDecoder: Decoder<Int>, nullabilityDecoder: Decoder<Boolean>): List<E>? =
        selfCollectionOrNull(toList(), sizeDecoder, nullabilityDecoder)

    /**
     * Recursively decodes a nullable list of objects of type [E] using the given [sizeDecoder] and the default
     * boolean decoder of the scope. The size of the list is prepended to the list itself. The nullability of the list
     * is prepended to the size of the list.
     *
     * @param sizeDecoder the decoder used to decode the size of the list
     * @return the decoded list, or `null` if the list is not present
     */
    @JvmName("decodeSelfListOrNull")
    fun selfListOrNull(sizeDecoder: Decoder<Int>): List<E>? = selfCollectionOrNull(toList(), sizeDecoder)

    /**
     * Recursively decodes a nullable list of objects of type [E] using the default int and boolean decoders of the
     * scope. The size of the list is prepended to the list itself. The nullability of the list is prepended to the
     * size of the list.
     *
     * @return the decoded list, or `null` if the list is not present
     */
    @JvmName("decodeSelfListOrNull")
    fun selfListOrNull(): List<E>? = selfCollectionOrNull(toList())

    /**
     * Recursively decodes a nullable set of objects of type [E] using the given [sizeDecoder] and the given
     * [nullabilityDecoder]. The [sizeDecoder] is used to decode the size of the set which is prepended to the set
     * itself. The [nullabilityDecoder] is used to decode the nullability of the set, which is prepended to the size
     * of the set.
     *
     * @param sizeDecoder the decoder used to decode the size of the set
     * @param nullabilityDecoder the decoder used to decode the nullability of the set
     * @return the decoded set, or `null` if the set is not present
     */
    @JvmName("decodeSelfSetOrNull")
    fun selfSetOrNull(sizeDecoder: Decoder<Int>, nullabilityDecoder: Decoder<Boolean>): Set<E>? =
        selfCollectionOrNull(toSet(), sizeDecoder, nullabilityDecoder)

    /**
     * Recursively decodes a nullable set of objects of type [E] using the given [sizeDecoder] and the default
     * boolean decoder of the scope. The size of the set is prepended to the set itself. The nullability of the set is
     * prepended to the size of the set.
     *
     * @param sizeDecoder the decoder used to decode the size of the set
     * @return the decoded set, or `null` if the set is not present
     */
    @JvmName("decodeSelfSetOrNull")
    fun selfSetOrNull(sizeDecoder: Decoder<Int>): Set<E>? = selfCollectionOrNull(toSet(), sizeDecoder)

    /**
     * Recursively decodes a nullable set of objects of type [E] using the default int and boolean decoders of the
     * scope. The size of the set is prepended to the set itself. The nullability of the set is prepended to the size
     * of the set.
     *
     * @return the decoded set, or `null` if the set is not present
     */
    @JvmName("decodeSelfSetOrNull")
    fun selfSetOrNull(): Set<E>? = selfCollectionOrNull(toSet())

    /**
     * Recursively decodes a nullable array of objects of type [E] using the given [factory], the given [sizeDecoder]
     * and the given [nullabilityDecoder]. The [factory] is used to create the array. The [sizeDecoder] is used to
     * decode the size of the array which is prepended to the array itself. The [nullabilityDecoder] is used to decode
     * the nullability of the array, which is prepended to the size of the array.
     *
     * @param factory the factory used to create the array
     * @param sizeDecoder the decoder used to decode the size of the array
     * @param nullabilityDecoder the decoder used to decode the nullability of the array
     * @return the decoded array, or `null` if the array is not present
     */
    @JvmName("decodeSelfArrayOrNull")
    fun selfArrayOrNull(
        factory: (Int) -> Array<E?>,
        sizeDecoder: Decoder<Int>,
        nullabilityDecoder: Decoder<Boolean>,
    ): Array<E>? = selfCollectionOrNull(ExtendedCollectors.toArray(factory), sizeDecoder, nullabilityDecoder)

    /**
     * Recursively decodes a nullable array of objects of type [E] using the given [factory], the given [sizeDecoder]
     * and the default boolean decoder of the scope. The [factory] is used to create the array. The size of the array
     * is prepended to the array itself. The nullability of the array is prepended to the size of the array.
     *
     * @param factory the factory used to create the array
     * @param sizeDecoder the decoder used to decode the size of the array
     * @return the decoded array, or `null` if the array is not present
     */
    @JvmName("decodeSelfArrayOrNull")
    fun selfArrayOrNull(factory: (Int) -> Array<E?>, sizeDecoder: Decoder<Int>): Array<E>? =
        selfCollectionOrNull(ExtendedCollectors.toArray(factory), sizeDecoder)

    /**
     * Recursively decodes a nullable array of objects of type [E] using the given [factory], the default int and
     * boolean decoders of the scope. The [factory] is used to create the array. The size of the array is prepended to
     * the array itself. The nullability of the array is prepended to the size of the array.
     *
     * @param factory the factory used to create the array
     * @return the decoded array, or `null` if the array is not present
     */
    @JvmName("decodeSelfArrayOrNull")
    fun selfArrayOrNull(factory: (Int) -> Array<E?>): Array<E>? =
        selfCollectionOrNull(ExtendedCollectors.toArray(factory))

}

private val toList = Collectors.toList<Any>()

private val toSet = Collectors.toSet<Any>()

@Suppress("UNCHECKED_CAST")
private fun <T> toList(): Collector<T, *, List<T>> = toList as Collector<T, Any, List<T>>

@Suppress("UNCHECKED_CAST")
private fun <T> toSet(): Collector<T, *, Set<T>> = toSet as Collector<T, Any, Set<T>>
