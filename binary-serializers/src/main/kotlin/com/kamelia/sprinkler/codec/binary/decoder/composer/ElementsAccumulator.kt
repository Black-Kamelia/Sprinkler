package com.kamelia.sprinkler.codec.binary.decoder.composer

import com.zwendo.restrikt.annotation.PackagePrivate

/**
 * Class accumulating the elements of a [DecoderComposer] to be able to use them later for object creation. This class
 * should only be used in the context the [composedDecoder] function. Most of the time the user does not need to use
 * this class directly. It will be used internally by the [composedDecoder] function.
 *
 * Example:
 * ```
 * val decoder = composedDecoder<Pair<Int, Int>> {
 *    beginWith(IntDecoder())
 *        .then(IntDecoder())
 *        .reduce(::Pair)
 * }
 * ```
 */
class ElementsAccumulator @PackagePrivate internal constructor() {

    private var elements = ArrayList<Any?>()
    private var currentLayer = Layer(0, null)
    private var index = 0

    internal val size: Int = elements.size

    /**
     * Returns the next element of the list. The returned object will be cast to the type of the parameter. The user
     * must ensure that the type is correct.
     *
     * @return the next element of the accumulator
     * @throws NoSuchElementException if there is no more element to read
     */
    fun <T> next(): T {
        if (index >= elements.size) {
            throw NoSuchElementException("No more elements to read")
        }
        val e = @Suppress("UNCHECKED_CAST") (elements[index] as T)
        index++
        updateStep()
        return e
    }

    internal fun <T> add(element: T) {
        elements.add(element)
    }

    @PublishedApi
    internal fun <T> pop(): T {
        check(index >= currentLayer.start) { "No element to pop" }
        @Suppress("UNCHECKED_CAST")
        return elements.removeLast() as T
    }

    @PublishedApi
    internal fun <T> peek(): T {
        check(index >= currentLayer.start) { "No element to peek" }
        @Suppress("UNCHECKED_CAST")
        return elements.last() as T
    }

    internal fun addStep() {
        val newIndex = elements.size
        currentLayer = Layer(newIndex, currentLayer)
        index = newIndex
    }

    private fun updateStep() {
        if (index < elements.size) return

        val previous = currentLayer.previous

        // we read the whole list, we can simply clear it
        if (previous == null) {
            elements = ArrayList()
            index = 0
            return
        }

        // otherwise we read a sublist, we must only remove the elements we read
        repeat(elements.size - currentLayer.start) {
            elements.removeLast()
        }
        currentLayer = previous
        index = currentLayer.start
    }

    private class Layer(
        @JvmField
        val start: Int,
        @JvmField
        val previous: Layer?,
    )

    override fun toString(): String = "$index $elements"

}
