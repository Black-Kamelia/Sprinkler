package com.kamelia.sprinkler.binary.decoder.composer

import com.zwendo.restrikt.annotation.PackagePrivate

class ComposedDecoderElementsAccumulator @PackagePrivate internal constructor() {

    private var elements = ArrayList<Any?>()
    private var currentLayer = Layer(0, null)
    private var index = 0

    internal val size: Int = elements.size

    fun <T> next(): T {
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

    internal fun addStep() {
        val newIndex = elements.size
        currentLayer = Layer(newIndex, currentLayer)
        index = newIndex
    }

    internal fun isNotEmpty(): Boolean = elements.isNotEmpty()

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
