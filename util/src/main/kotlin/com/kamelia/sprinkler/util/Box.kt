package com.kamelia.sprinkler.util

import com.zwendo.restrikt.annotation.HideFromJava

/**
 * Represents a box containing a value. This interface aims at providing a way to declare a property with a value that
 * is not available at the time of declaration. Any read access to the [value] property will throw an exception if the
 * value has not been set.
 *
 * It differs from [Lazy] in that the value can be set from outside the class. A typical use case is to declare an
 * object containing a property subject to a circular dependency without having to resort to a mutable property in the
 * class.
 *
 * Example:
 * ```
 * class Foo(intBox: Box<Int>) {
 *
 *     val i by intBox
 *
 * }
 *
 * fun main() {
 *    val box = Box.SingleWriteBox<Int>()
 *    val foo = Foo(box)
 *
 *    runCatching {
 *      println(foo.i) // Throws an exception
 *    }
 *
 *    box.fill(1)
 *    println(foo.i) // Prints 1
 *
 * }
 *
 * ```
 *
 * @param T the type of the value
 * @see Box.Mutable
 * @see Box.SingleWriteBox
 * @see Box.RewritableBox
 * @see Box.PrefilledBox
 */
interface Box<T> {

    /**
     * The value contained in the box.
     * @throws IllegalStateException if the value has not been set.
     */
    val value: T

    /**
     * Whether the box has been filled.
     */
    val isFilled: Boolean

    /**
     * Delegate operator for the [value] property.
     */
    @HideFromJava
    operator fun getValue(thisRef: Any?, property: Any?): T = value

    /**
     * Represents a mutable box. It allows to fill the box with a value.
     */
    interface Mutable<T> : Box<T> {

        /**
         * Fills the box with the given value.
         * @param value the value to fill the box with
         * @return true if the box value was set, false otherwise
         */
        fun fill(value: T): Boolean

    }

    companion object {

        /**
         * Creates a new [SingleWriteBox].
         *
         * @param T the type of the value
         * @return a new [SingleWriteBox]
         */
        fun <T> singleWriteBox(): SingleWriteBox<T> = SingleWriteBox()

        /**
         * Creates a new [RewritableBox].
         *
         * @param T the type of the value
         * @return a new [RewritableBox]
         */
        fun <T> rewritableBox(): RewritableBox<T> = RewritableBox()

        /**
         * Creates a new [PrefilledBox] with the given value.
         *
         * @param T the type of the value
         * @param value the value to fill the box with
         */
        fun <T> prefilledBox(value: T): PrefilledBox<T> = PrefilledBox(value)

        /**
         * Creates a new [EmptyBox].
         *
         * @param T the type of the value
         * @return a new [EmptyBox]
         */
        fun <T> emptyBox(): EmptyBox<T> = EmptyBox()

    }

    /**
     * A box that can only be filled once. Any subsequent call to [fill] will return false.
     * @param T the type of the value
     */
    class SingleWriteBox<T> internal constructor() : Mutable<T> {

        private var valueField: T? = null

        override var isFilled: Boolean = false
            private set

        override val value: T
            get() {
                check(isFilled) { "Box is not filled." }
                @Suppress("UNCHECKED_CAST")
                return valueField as T
            }

        override fun fill(value: T): Boolean {
            if (isFilled) return false
            valueField = value
            isFilled = true
            return true
        }

    }

    /**
     * A box that can be filled multiple times. Any call to [fill] will return true and overwrite the previous value.
     * @param T the type of the value
     */
    class RewritableBox<T> internal constructor() : Mutable<T> {

        private var valueField: T? = null

        override var isFilled: Boolean = false
            private set

        override val value: T
            get() {
                check(isFilled) { "Box is not filled." }
                @Suppress("UNCHECKED_CAST")
                return valueField as T
            }

        override fun fill(value: T): Boolean {
            valueField = value
            isFilled = true
            return true
        }

    }

    /**
     * A box that is already filled with a value.
     * @param T the type of the value
     */
    class PrefilledBox<T> internal constructor(override val value: T) : Box<T> {

        override val isFilled: Boolean
            get() = true

    }

    /**
     * A box that is empty and cannot be filled.
     * @param T the type of the value
     */
    class EmptyBox<T> internal constructor() : Box<T> {

        override val value: T
            get() = throw IllegalStateException("Box is not filled.")

        override val isFilled: Boolean
            get() = false

    }

}
