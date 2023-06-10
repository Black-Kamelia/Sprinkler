package com.kamelia.sprinkler.util

import java.util.*

class FixedArrayDeque<E>(private val capacity: Int) : Deque<E> {

    @Suppress("UNCHECKED_CAST")
    private val inner = arrayOfNulls<Any>(capacity) as Array<E?>
    private var head = 0
    private var tail = 0

    override var size: Int = 0
        private set

    override fun add(element: E): Boolean {
        TODO("Not yet implemented")
    }

    override fun addAll(elements: Collection<E>): Boolean {
        TODO("Not yet implemented")
    }

    override fun clear() {
        head = 0
        tail = 0
        size = 0
    }

    override fun iterator(): MutableIterator<E> {
        TODO("Not yet implemented")
    }

    override fun remove(): E {
        checkNotEmpty()
        val result = inner[head]!!
        inner[head] = null
        size--
        head = if (isEmpty()) {
            0
        } else {
            next(head)
        }
        return result
    }

    override fun isEmpty(): Boolean = size == 0

    override fun poll(): E? = runIfNotEmpty { inner[head] }

    override fun element(): E = checkNotEmptyAnd { inner[head]!! }

    override fun peek(): E? {
        TODO("Not yet implemented")
    }

    override fun removeFirst(): E {
        TODO("Not yet implemented")
    }

    override fun removeLast(): E {
        TODO("Not yet implemented")
    }

    override fun pollFirst(): E? {
        TODO("Not yet implemented")
    }

    override fun pollLast(): E? {
        TODO("Not yet implemented")
    }

    override fun getFirst(): E {
        TODO("Not yet implemented")
    }

    override fun getLast(): E {
        TODO("Not yet implemented")
    }

    override fun peekFirst(): E? {
        TODO("Not yet implemented")
    }

    override fun peekLast(): E? {
        TODO("Not yet implemented")
    }

    override fun removeFirstOccurrence(o: Any?): Boolean {
        TODO("Not yet implemented")
    }

    override fun removeLastOccurrence(o: Any?): Boolean {
        TODO("Not yet implemented")
    }

    override fun pop(): E {
        TODO("Not yet implemented")
    }

    override fun descendingIterator(): MutableIterator<E> {
        TODO("Not yet implemented")
    }

    override fun push(e: E) {
        TODO("Not yet implemented")
    }

    override fun offerLast(e: E): Boolean {
        TODO("Not yet implemented")
    }

    override fun offerFirst(e: E): Boolean {
        TODO("Not yet implemented")
    }

    override fun addLast(e: E) {
        TODO("Not yet implemented")
    }

    override fun addFirst(e: E) {
        TODO("Not yet implemented")
    }

    override fun offer(e: E): Boolean {
        TODO("Not yet implemented")
    }

    override fun containsAll(elements: Collection<E>): Boolean {
        TODO("Not yet implemented")
    }

    override fun contains(element: E): Boolean {
        TODO("Not yet implemented")
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        TODO("Not yet implemented")
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        TODO("Not yet implemented")
    }

    override fun remove(element: E): Boolean {
        TODO("Not yet implemented")
    }

    private inline fun <T> runIfNotEmpty(block: () -> T): T? = if (size == 0) null else block()


    private fun checkNotEmpty() {
        if (size == 0) throw NoSuchElementException("Deque is empty")
    }

    private inline fun <T> checkNotEmptyAnd(block: () -> T): T {
        checkNotEmpty()
        return block()
    }

    private fun next(index: Int): Int {
        val n = index + 1
        return if (n == capacity) {
            0
        } else {
            n
        }
    }

    private fun prev(index: Int): Int {
        val n = index - 1
        return if (n == -1) {
            capacity - 1
        } else {
            n
        }
    }
}
