package com.kamelia.sprinkler.util

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BoxTest {

    @Test
    fun `single write box test`() {
        val box = Box.singleWrite<Int>()

        assertFalse(box.isFilled)
        assertThrows<IllegalStateException> {
            box.value
        }

        val expected = 1

        assertTrue(box.fill(expected))
        assertTrue(box.isFilled)
        assertFalse(box.fill(expected))
        assertTrue(box.isFilled)
        assertEquals(expected, box.value)
    }

    @Test
    fun `rewritable box test`() {
        val box = Box.rewritable<Int>()
        var v by box

        assertFalse(box.isFilled)
        assertThrows<IllegalStateException> {
            box.value
        }

        val expected = 1

        assertTrue(box.fill(12))
        assertTrue(box.isFilled)
        v = expected
        assertTrue(box.isFilled)
        assertEquals(expected, box.value)
    }

    @Test
    fun `rewritable box with initial value test`() {
        val expected = 1
        val box = Box.rewritable(expected)

        assertTrue(box.isFilled)
        assertEquals(expected, box.value)

        val expected2 = 5
        assertTrue(box.fill(expected2))
        assertTrue(box.isFilled)
        assertEquals(expected2, box.value)
    }

    @Test
    fun `prefilled box test`() {
        val expected = 1
        val box = Box.prefilled(expected)

        assertTrue(box.isFilled)
        assertEquals(expected, box.value)
    }

    @Test
    fun `empty box test`() {
        val box = Box.empty<Int>()
        val v by box

        assertFalse(box.isFilled)
        assertThrows<IllegalStateException> {
            box.value
        }
        assertThrows<IllegalStateException> {
            v
        }
    }


}
