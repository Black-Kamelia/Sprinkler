package com.kamelia.sprinkler.transcoder.binary.encoder.composer

import com.kamelia.sprinkler.transcoder.binary.encoder.core.Encoder
import com.kamelia.sprinkler.util.jvmlambda.KotlinDslAdapter

/**
 * Represents a scope in which an object can be encoded. This interface is used to compose encoders. It can be used as
 * an alternative to the lambda syntax of [Encoder] in the context of composition.
 *
 * &nbsp;
 *
 * In the following example, both syntax are equivalent:
 *
 * &nbsp;
 *
 * ```
 * class Person(val name: String, val age: Int)
 *
 * // the following lines
 * val strEncoder = UTF8StringEncoder()
 * val intEncoder = IntEncoder()
 * val encoder = Encoder<Person> { obj, output ->
 *     strEncoder.encode(obj.name, output)
 *     intEncoder.encode(obj.age, output)
 * }
 *
 * // are strictly equivalent to
 * val encoder2 = composedEncoder<Person> {
 *     encode(it.name)
 *     encode(it.age)
 * }
 * ```
 *
 * As shown above, the interface provides several methods to encode the most common types. It therefore allows to
 * encode an object in a more concise way than the lambda syntax of [Encoder]. Moreover, it allows to encode recursively
 * an object in an iterative way, meaning that the stack is not overloaded. This allows to encode very deep recursive
 * objects.
 *
 * &nbsp;
 *
 * ```
 * class Node(val value: Int, val next: Node?)
 *
 * // using the lambda syntax of Encoder
 * val intEncoder = IntEncoder()
 * val booleanEncoder = BooleanEncoder()
 * lateinit var encoder: Encoder<Node>
 *
 * encoder = Encoder<Node> { obj, output ->
 *     intEncoder.encode(obj.value, output) // encode the value
 *     if (obj.next == null) {
 *         booleanEncoder.encode(false, output) // encode false if there is no next node
 *         return@Encoder
 *     }
 *     booleanEncoder.encode(true, output) // encode true if there is a next node
 *     encoder.encode(obj.next, output) // encode the next node
 * }
 *
 * // can be replaced with
 * val encoder2 = composedEncoder<Node> {
 *     encode(it.value)
 *     encode(it.next)
 * }
 * ```
 *
 * The interface aims to be as flexible as possible. In this regard, it provides a method to encode an object using a
 * custom encoder, which accepts any type of object and an encoder of the same type.
 *
 * Moreover, for recursive encoding, the interface provides a [self] property that returns the encoder of the current
 * scope. This property can be used to encode recursively an object in a more flexible way, in case the other methods
 * are not sufficient.
 *
 * &nbsp;
 *
 * As stated previously, the interface, through its shorthand methods, allows to encode the most common types, by using
 * default encoders. These default encoders are not defined by the interface and therefore depend on the implementation.
 *
 * &nbsp;
 *
 * @param E the type of the object to encode
 * @see Encoder
 * @see composedEncoder
 */
@Suppress("INAPPLICABLE_JVM_NAME")
sealed interface EncodingScope<E> : KotlinDslAdapter {

    /**
     * Gets the encoder of the current scope.
     *
     * Useful to recursively encode an object.
     *
     * &nbsp;
     *
     * **NOTE**: The returned encoder should be used with caution. It should be used according to the usage indicated
     * in the documentation of the implementation of the interface. Any misuse of the encoder can lead to unexpected
     * behavior.
     */
    @get:JvmName("self")
    val self: Encoder<E>

    /**
     * Encodes the given [obj] using the given [encoder].
     *
     * @param obj the object to encode
     * @param encoder the encoder to use
     * @return the current scope
     * @param T the type of the object to encode
     */
    @JvmName("encodeWith")
    fun <T> encode(obj: T, encoder: Encoder<T>): EncodingScope<E>

    /**
     * Encodes a [Byte] using the default byte encoder of the scope.
     *
     * @param obj the byte to encode
     * @return the current scope
     */
    fun encode(obj: Byte): EncodingScope<E>

    /**
     * Encodes a [Short] using the default short encoder of the scope.
     *
     * @param obj the short to encode
     * @return the current scope
     */
    fun encode(obj: Short): EncodingScope<E>

    /**
     * Encodes an [Int] using the default int encoder of the scope.
     *
     * @param obj the int to encode
     * @return the current scope
     */
    fun encode(obj: Int): EncodingScope<E>

    /**
     * Encodes a [Long] using the default long encoder of the scope.
     *
     * @param obj the long to encode
     * @return the current scope
     */
    fun encode(obj: Long): EncodingScope<E>

    /**
     * Encodes a [Float] using the default float encoder of the scope.
     *
     * @param obj the float to encode
     * @return the current scope
     */
    fun encode(obj: Float): EncodingScope<E>

    /**
     * Encodes a [Double] using the default double encoder of the scope.
     *
     * @param obj the double to encode
     * @return the current scope
     */
    fun encode(obj: Double): EncodingScope<E>

    /**
     * Encodes a [Boolean] using the default boolean encoder of the scope.
     *
     * @param obj the boolean to encode
     * @return the current scope
     */
    fun encode(obj: Boolean): EncodingScope<E>

    /**
     * Encodes a [String] using the default string encoder of the scope.
     *
     * @param obj the string to encode
     * @return the current scope
     */
    fun encode(obj: String): EncodingScope<E>

    /**
     * Recursively encodes a nullable object of type [E]. The assumed representation of the nullability depends
     * on the implementation of the scope.
     *
     * @param obj the object to encode
     * @return the current scope
     */
    fun encode(obj: E?): EncodingScope<E>

    /**
     * Recursively encodes an object of type [E].
     *
     * @param obj the object to encode
     * @return the current scope
     */
    @JvmName("encodeSelf")
    fun encode(obj: E): EncodingScope<E> = encode(obj, self)

    /**
     * Recursively encodes a [Collection] of type [E]. The assumed representation of the collection depends on the
     * implementation of the scope.
     *
     * @param obj the collection to encode
     * @return the current scope
     */
    fun encode(obj: Collection<E>): EncodingScope<E>

    /**
     * Recursively encodes an [Array] of type [E]. The assumed representation of the array depends on the
     * implementation of the scope.
     *
     * @param obj the array to encode
     * @return the current scope
     */
    fun encode(obj: Array<E>): EncodingScope<E>

}
