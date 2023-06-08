package com.kamelia.sprinkler.transcoder.binary.decoder.composer

import com.zwendo.restrikt.annotation.PackagePrivate

@PackagePrivate
internal class ElementsAccumulator {

    private val list = ArrayList<Any?>()
    private val recursionElements = ArrayDeque<Any?>()
    private var currentLayer = Layer(0, null)

    val size: Int
        get() = list.size - currentLayer.start

    val isLastLayer: Boolean
        get() = currentLayer.previous == null

    fun add(element: Any?) {
        list.add(element)
    }

    fun addToRecursion(element: Any?) {
        recursionElements.add(element)
    }

    operator fun get(index: Int): Any? {
        val actualIndex = currentLayer.start + index
        require(actualIndex < list.size) {
            "Index $actualIndex ($index + ${currentLayer.start}) is out of bounds for size ${list.size}"
        }
        return list[actualIndex]
    }

    operator fun set(index: Int, element: Any?) {
        val actualIndex = currentLayer.start + index
        require(actualIndex < list.size) {
            "Index $actualIndex ($index + ${currentLayer.start}) is out of bounds for size ${list.size}"
        }
        list[actualIndex] = element
    }

    fun hasRecursionElement(): Boolean = recursionElements.isNotEmpty()

    fun getFromRecursion(): Any? = recursionElements.removeFirst()

    fun recurse() {
        currentLayer = Layer(list.size, currentLayer)
    }

    fun popRecursion() {
        val previous = currentLayer.previous
        check(previous != null) { "No element to pop" }
        val toPop = list.size - currentLayer.start
        repeat(toPop) { list.removeLast() }
        currentLayer = previous
    }

    private class Layer(
        @JvmField
        val start: Int,
        @JvmField
        val previous: Layer?,
    ) {

            override fun toString(): String = "Layer(start=$start, previous=$previous)"

    }

    override fun toString(): String = "ElementsAccumulator(list=$list, recursionElements=$recursionElements, currentLayer=$currentLayer)"

}
