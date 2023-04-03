package com.kamelia.sprinkler.codec.binary.decoder.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class DecoderStateTest {

    @Test
    fun `mapState works correctly`() {
        val value = "hello"

        val done = Decoder.State.Done(value)
        val mappedState = done.mapState { Decoder.State.Done(it.length) }
        assertInstanceOf(Decoder.State.Done::class.java, mappedState)
        assertEquals(value.length, mappedState.get())

        val error: Decoder.State<String> = Decoder.State.Error(IllegalStateException())
        val mappedError = error.mapState { Decoder.State.Done(it.length) }
        assertInstanceOf(Decoder.State.Error::class.java, mappedError)

        val mappedStateError = mappedState.mapState { Decoder.State.Error(IllegalStateException()) }
        assertInstanceOf(Decoder.State.Error::class.java, mappedStateError)
    }

    @Test
    fun `mapResult works correctly`() {
        val value = "hello"

        val done: Decoder.State<String> = Decoder.State.Done(value)
        val mappedResult = done.mapResult { it.length }
        assertInstanceOf(Decoder.State.Done::class.java, mappedResult)
        assertEquals(value.length, mappedResult.get())

        val error: Decoder.State<String> = Decoder.State.Error(IllegalStateException())
        val mappedError = error.mapResult { it.length }
        assertInstanceOf(Decoder.State.Error::class.java, mappedError)
        assertEquals(error, mappedError)
        assertThrows<IllegalStateException> {
            mappedError.get()
        }
    }

    @Test
    fun `mapEmptyState works correctly`() {
        val processing = Decoder.State.Processing()
        val state = processing.mapEmptyState<String>()
        assertInstanceOf(Decoder.State.Processing::class.java, state)

        val done = Decoder.State.Done("hello")
        assertInstanceOf(Decoder.State.Done::class.java, done)
        assertThrows<IllegalStateException> {
            done.mapEmptyState<String>()
        }
    }

    @Test
    fun `isDone and state#isNotDone work correctly`() {
        val done = Decoder.State.Done("hello")
        val error = Decoder.State.Error("an error occurred")
        val notDone = Decoder.State.Processing()

        assert(done.isDone())
        assert(!done.isNotDone())
        assert(!error.isDone())
        assert(error.isNotDone())
        assert(!notDone.isDone())
        assert(notDone.isNotDone())
    }

    @Test
    fun `get works correctly`() {
        val value = "hello"
        val done = Decoder.State.Done(value)
        val error = Decoder.State.Error(IllegalStateException())
        val processing = Decoder.State.Processing()

        assertEquals(value, done.get())
        assertThrows<IllegalStateException> {
            error.get()
        }
        assertThrows<MissingBytesException> {
            processing.get()
        }
    }

    @Test
    fun `getOrNull works correctly`() {
        val value = "hello"
        val done = Decoder.State.Done(value)
        val error = Decoder.State.Error(IllegalStateException())
        val processing = Decoder.State.Processing()

        assertEquals(value, done.getOrNull())
        assertNull(error.getOrNull())
        assertNull(processing.getOrNull())
    }

    @Test
    fun `getOrThrow works correctly`() {
        val value = "hello"
        val done = Decoder.State.Done(value)
        val error = Decoder.State.Error("aaaaa")
        val processing = Decoder.State.Processing()

        assertEquals(value, done.getOrThrow { IllegalStateException() })
        assertThrows<IllegalStateException> {
            error.getOrThrow { IllegalStateException() }
        }

        class DummyException : Exception()
        assertThrows<DummyException> {
            processing.getOrThrow { DummyException() }
        }
    }

    @Test
    fun `getOrElse(Function0 T) works correctly`() {
        val value = "hello"
        val done = Decoder.State.Done(value)
        val error: Decoder.State<String> = Decoder.State.Error(IllegalStateException())
        val processing: Decoder.State<String> = Decoder.State.Processing()

        val world = "world"
        val foo = "foo"
        assertEquals(value, done.getOrElse { world })
        assertEquals(world, error.getOrElse { world })
        assertEquals(foo, processing.getOrElse { foo })
    }

    @Test
    fun `getOrElse(T) works correctly`() {
        val value = "hello"
        val done = Decoder.State.Done(value)
        val error: Decoder.State<String> = Decoder.State.Error(IllegalStateException())
        val processing: Decoder.State<String> = Decoder.State.Processing()

        val world = "world"
        assertEquals(value, done.getOrElse(world))
        assertEquals(world, error.getOrElse(world))
        assertEquals(world, processing.getOrElse(world))
    }

    @Test
    fun `ifDone works correctly`() {
        val value = "hello"
        val done = Decoder.State.Done(value)
        val error = Decoder.State.Error(IllegalStateException())
        val processing = Decoder.State.Processing()

        val set = mutableSetOf<Int>()
        done.ifDone { set.add(1) }
        error.ifDone { set.add(2) }
        processing.ifDone { set.add(3) }

        assertEquals(setOf(1), set)
    }

    @Test
    fun `toString contains informations about state`() {
        val doneValue = 5
        val errorValue = IllegalStateException("error message")
        val processingReason = "in progress"

        val done = Decoder.State.Done(doneValue)
        val error = Decoder.State.Error(errorValue)
        val processing = Decoder.State.Processing(processingReason)

        assertTrue(doneValue.toString() in done.toString())
        assertTrue(errorValue.toString() in error.toString())
        assertTrue(processingReason in processing.toString())
    }

    @Test
    fun `Done toString doesn't initialize the value`() {
        val done = Decoder.State.Done { throw IllegalStateException("Should not be called") }
        assertDoesNotThrow {
            done.toString()
        }
    }

}
