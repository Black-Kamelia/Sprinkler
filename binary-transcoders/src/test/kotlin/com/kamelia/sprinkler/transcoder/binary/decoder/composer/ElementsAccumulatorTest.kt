package com.kamelia.sprinkler.transcoder.binary.decoder.composer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ElementsAccumulatorTest {

    @Test
    fun `add works correctly`() {
        val accumulator = ElementsAccumulator()
        accumulator.add(1)
        assertEquals(1, accumulator.size)
    }

    @Test
    fun `isLastLayer works correctly`() {
        val accumulator = ElementsAccumulator()
        assertTrue(accumulator.isLastLayer)
        accumulator.add(1)
        assertTrue(accumulator.isLastLayer)
        accumulator.recurse()
        assertFalse(accumulator.isLastLayer)
    }

    @Test
    fun `get works correctly`() {
        val accumulator = ElementsAccumulator()
        accumulator.add(1)
        assertEquals(1, accumulator[0])
        accumulator.add(true)
        assertEquals(true, accumulator[1])
    }

    @Test
    fun `get throws on index greater or equal than size`() {
        val accumulator = ElementsAccumulator()
        assertThrows<IllegalArgumentException> { accumulator[0] }
        assertThrows<IllegalArgumentException> { accumulator[1] }
    }

    @Test
    fun `get throws on negative index`() {
        val accumulator = ElementsAccumulator()
        assertThrows<IllegalArgumentException> { accumulator[-1] }
    }

    @Test
    fun `set works correctly`() {
        val accumulator = ElementsAccumulator()
        accumulator.add(1)
        accumulator[0] = 2
        assertEquals(2, accumulator[0])
    }

    @Test
    fun `set throws on index greater or equal than size`() {
        val accumulator = ElementsAccumulator()
        assertThrows<IllegalArgumentException> { accumulator[0] = 1 }
        assertThrows<IllegalArgumentException> { accumulator[1] = 1 }
    }

    @Test
    fun `set throws on negative index`() {
        val accumulator = ElementsAccumulator()
        assertThrows<IllegalArgumentException> { accumulator[-1] = 1 }
    }

    @Test
    fun `addToRecursion works correctly`() {
        val accumulator = ElementsAccumulator()
        assertFalse(accumulator.hasRecursionElement())
        accumulator.addToRecursion(1)
        assertTrue(accumulator.hasRecursionElement())
    }

    @Test
    fun `getFromRecursion works correctly`() {
        val accumulator = ElementsAccumulator()
        accumulator.addToRecursion(1)
        assertEquals(1, accumulator.getFromRecursion())
    }

    @Test
    fun `getFromRecursion throws on empty recursion`() {
        val accumulator = ElementsAccumulator()
        assertThrows<NoSuchElementException> { accumulator.getFromRecursion() }
    }

    @Test
    fun `recurse works correctly`() {
        val accumulator = ElementsAccumulator()
        accumulator.recurse()
        assertEquals(0, accumulator.size)
        accumulator.add(1)
        assertEquals(1, accumulator.size)
        accumulator.recurse()
        assertEquals(0, accumulator.size)
    }

    @Test
    fun `popRecursion works correctly`() {
        val accumulator = ElementsAccumulator()
        accumulator.add("a")
        assertEquals(1, accumulator.size)
        accumulator.recurse()
        assertEquals(0, accumulator.size)
        accumulator.popRecursion()
        assertEquals(1, accumulator.size)
        assertEquals("a", accumulator[0])
    }

    @Test
    fun `popRecursion throws on empty recursion`() {
        val accumulator = ElementsAccumulator()
        assertThrows<IllegalStateException> { accumulator.popRecursion() }
    }

}
