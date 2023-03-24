package com.kamelia.sprinkler.util.closeable

import java.io.Closeable
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CloseableScopeTest {

    open class CloseableMock : Closeable {
        var isClosed = false
        override fun close() {
            isClosed = true
        }
    }

    class NestedCloseableMock : CloseableMock() {
        fun other(): CloseableMock = CloseableMock()
    }

    @Test
    fun `should close scoped closeables normally`() {
        val closeable1 = CloseableMock()
        lateinit var closeable2: CloseableMock
        lateinit var closeable3: CloseableMock
        lateinit var closeable4: CloseableMock
        lateinit var closeable5: CloseableMock

        closeableScope(closeable1) {
            closeable2 = using(CloseableMock())
            closeable3 = using(CloseableMock())

            closeable5 = NestedCloseableMock()
                .also { closeable4 = it }.usingSelf()
                .other().usingSelf()
        }

        assertTrue(closeable1.isClosed)
        assertTrue(closeable2.isClosed)
        assertTrue(closeable3.isClosed)
        assertTrue(closeable4.isClosed)
        assertTrue(closeable5.isClosed)
    }

    @Test
    fun `should close scoped closeables even if an exception is thrown`() {
        val closeable1 = CloseableMock()
        lateinit var closeable2: CloseableMock
        lateinit var closeable3: CloseableMock
        lateinit var closeable4: CloseableMock
        lateinit var closeable5: CloseableMock

        runCatching {
            closeableScope(closeable1) {
                closeable2 = using(CloseableMock())
                closeable3 = using(CloseableMock())

                closeable5 = NestedCloseableMock()
                    .also { closeable4 = it }.usingSelf()
                    .other().usingSelf()

                throw RuntimeException()
            }
        }

        assertTrue(closeable1.isClosed)
        assertTrue(closeable2.isClosed)
        assertTrue(closeable3.isClosed)
        assertTrue(closeable4.isClosed)
        assertTrue(closeable5.isClosed)
    }

}
