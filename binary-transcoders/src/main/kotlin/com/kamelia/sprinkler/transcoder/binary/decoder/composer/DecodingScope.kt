package com.kamelia.sprinkler.transcoder.binary.decoder.composer

import com.kamelia.sprinkler.transcoder.binary.decoder.core.Decoder
import com.kamelia.sprinkler.transcoder.binary.decoder.toListCollector
import com.kamelia.sprinkler.transcoder.binary.decoder.toSetCollector
import com.kamelia.sprinkler.util.ExtendedCollectors
import java.util.stream.Collector

/**
 * Represents a scope in which an object can be decoded. This interface is used to compose decoders. It can be used to
 * create a decoders in a more concise way (compared to overriding the [Decoder] interface) and in a sequential manner (
 * the call order is meaningful).
 *
 *
 * In the following example, both syntax are almost equivalent:
 *
 * ```
 * class Person(val name: String, val age: Int)
 *
 * // the scope syntax allows to create a decoder in a more concise way
 * val personDecoder = composedDecoder<Person> {
 *     val name = string()
 *     val age = int() // int must be called after string to work
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
     * Useful to recursively decode an object.
     *
     * &nbsp;
     *
     * **NOTE:** The returned decoder should only be used with caution. It should be used according to the usage
     * indicated in the documentation of the implementation of the interface. Any misuse of the decoder can lead to
     * unexpected behavior.
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
     * Recursively decodes an object of type [E].
     *
     * @return the decoded object
     */
    @JvmName("decodeSelf")
    fun self(): E = decode(self)

    /**
     * Recursively decodes a nullable object of type [E]. The representation of the nullability depends on the
     * implementation of the scope.
     *
     * @return the decoded object, or `null` if the object is not present
     */
    @JvmName("decodeSelfOrNull")
    fun selfOrNull(): E? =
        if (boolean()) {
            self()
        } else {
            null
        }

    /**
     * Recursively decodes a collection of objects of type [E] using the given [collector]. The assumed representation
     * of the collection depends on the implementation of the scope.
     *
     * @param collector the collector used to collect the decoded objects
     * @param R the type of the collection
     * @return the decoded collection
     */
    @JvmName("decodeSelfCollection")
    fun <R> selfCollection(collector: Collector<E, *, R>): R

    /**
     * Recursively decodes a list of objects of type [E]. The assumed representation of the list depends on the
     * implementation of the scope.
     *
     * @return the decoded list
     */
    @JvmName("decodeSelfList")
    fun selfList(): List<E> = selfCollection(toListCollector())

    /**
     * Recursively decodes a set of objects of type [E]. The assumed representation of the set depends on the
     * implementation of the scope.
     *
     * @return the decoded set
     */
    @JvmName("decodeSelfSet")
    fun selfSet(): Set<E> = selfCollection(toSetCollector())

    /**
     * Recursively decodes an array of objects of type [E]. The assumed representation of the array depends on the
     * implementation of the scope. The [factory] is necessary to create the array. Usually, the
     * [::arrayOfNulls][arrayOfNulls] function is used to create the array.
     *
     * @param factory the factory used to create the array
     * @return the decoded array
     */
    @JvmName("decodeSelfArray")
    fun selfArray(factory: (Int) -> Array<E?>): Array<E> = selfCollection(ExtendedCollectors.toArray(factory))

    /**
     * Recursively decodes a nullable collection of objects of type [E]. The assumed representation of the collection
     * depends on the implementation of the scope.
     *
     * @param collector the collector used to collect the decoded objects
     * @param R the type of the collection
     * @return the decoded collection, or `null` if the collection is not present
     */
    @JvmName("decodeSelfCollectionOrNull")
    fun <R> selfCollectionOrNull(collector: Collector<E, *, R>): R? =
        if (boolean()) {
            selfCollection(collector)
        } else {
            null
        }

    /**
     * Recursively decodes a nullable list of objects of type [E]. The assumed representation of the list depends on
     * the implementation of the scope.
     *
     * @return the decoded list, or `null` if the list is not present
     */
    @JvmName("decodeSelfListOrNull")
    fun selfListOrNull(): List<E>? = selfCollectionOrNull(toListCollector())

    /**
     * Recursively decodes a nullable set of objects of type [E]. The assumed representation of the set depends on
     * the implementation of the scope.
     *
     * @return the decoded set, or `null` if the set is not present
     */
    @JvmName("decodeSelfSetOrNull")
    fun selfSetOrNull(): Set<E>? = selfCollectionOrNull(toSetCollector())

    /**
     * Recursively decodes a nullable array of objects of type [E]. The assumed representation of the array depends on
     * the implementation of the scope. The [factory] is necessary to create the array. Usually, the
     * [::arrayOfNulls][arrayOfNulls] function is used to create the array.
     *
     * @param factory the factory used to create the array
     * @return the decoded array, or `null` if the array is not present
     */
    @JvmName("decodeSelfArrayOrNull")
    fun selfArrayOrNull(factory: (Int) -> Array<E?>): Array<E>? =
        selfCollectionOrNull(ExtendedCollectors.toArray(factory))

}
