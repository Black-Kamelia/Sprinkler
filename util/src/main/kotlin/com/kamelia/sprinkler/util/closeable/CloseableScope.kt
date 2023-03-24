@file:HideFromJava("This is a Kotlin only API")

package com.kamelia.sprinkler.util.closeable

import com.zwendo.restrikt.annotation.HideFromJava
import java.io.Closeable

/**
 * A scope for managing the lifecycle of [Closeable]s.
 *
 * @param closeables The [Closeable]s to manage.
 */
@JvmInline
value class CloseableScope @PublishedApi internal constructor(private val closeables: ArrayList<Closeable> = ArrayList()) {

    /**
     * Adds a [Closeable] to the scope. When the scope ends, this [Closeable] will be closed.
     *
     * @param closeable The [Closeable] to add.
     * @return The [Closeable] that was added.
     */
    fun <T : Closeable> using(closeable: T): T = closeable.also(closeables::add)

    /**
     * Adds a [Closeable] to the scope. When the scope ends, this [Closeable] will be closed.
     *
     * @receiver The [Closeable] to add.
     * @return The [Closeable] that was added.
     */
    fun <T : Closeable> T.usingSelf(): T = using(this)

    @PublishedApi
    internal fun closeAll() {
        for (i in closeables.lastIndex downTo 0) {
            closeables[i].close()
        }
    }

}

/**
 * Creates a [CloseableScope] and adds the given [Closeable]s to it.
 * Within the scope, one can use the [CloseableScope.using] function to add more [Closeable]s.
 * When the scope ends, all registered [Closeable]s will be closed.
 *
 * ```
 * val someCloseable = MyCloseable()
 * val ok = closeableScope(someCloseable) {
 *     val sin = using(System.`in`)
 *     val f = File("someFile")
 *         .inputStream().using()
 *         .buffered().using()
 *
 *     // Do stuff with sin and f
 *
 *     true
 * }
 * ```
 *
 * @param closeables The initial [Closeable]s to add to the scope.
 * @param block The block to execute in the scope.
 * @return The result of the block.
 */
inline fun <R> closeableScope(vararg closeables: Closeable, block: CloseableScope.() -> R): R {
    val scope = CloseableScope()
    closeables.forEach(scope::using)
    try {
        return scope.block()
    } finally {
        scope.closeAll()
    }
}
