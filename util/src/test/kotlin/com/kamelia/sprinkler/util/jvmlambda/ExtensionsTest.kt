package com.kamelia.sprinkler.util.jvmlambda

import java.util.concurrent.Callable
import java.util.function.BiConsumer
import java.util.function.BiFunction
import java.util.function.BiPredicate
import java.util.function.Consumer
import java.util.function.DoubleBinaryOperator
import java.util.function.DoubleConsumer
import java.util.function.DoubleFunction
import java.util.function.DoublePredicate
import java.util.function.DoubleSupplier
import java.util.function.DoubleToIntFunction
import java.util.function.DoubleToLongFunction
import java.util.function.DoubleUnaryOperator
import java.util.function.Function
import java.util.function.IntBinaryOperator
import java.util.function.IntConsumer
import java.util.function.IntFunction
import java.util.function.IntPredicate
import java.util.function.IntSupplier
import java.util.function.IntToDoubleFunction
import java.util.function.IntToLongFunction
import java.util.function.IntUnaryOperator
import java.util.function.LongBinaryOperator
import java.util.function.LongConsumer
import java.util.function.LongFunction
import java.util.function.LongPredicate
import java.util.function.LongSupplier
import java.util.function.LongToDoubleFunction
import java.util.function.LongToIntFunction
import java.util.function.LongUnaryOperator
import java.util.function.ObjDoubleConsumer
import java.util.function.ObjIntConsumer
import java.util.function.ObjLongConsumer
import java.util.function.Predicate
import java.util.function.Supplier
import java.util.function.ToDoubleBiFunction
import java.util.function.ToDoubleFunction
import java.util.function.ToIntBiFunction
import java.util.function.ToIntFunction
import java.util.function.ToLongBiFunction
import java.util.function.ToLongFunction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ExtensionsTest {

    @Test
    fun `callable invoke works`() {
        val callable = Callable { 1 }
        val result = callable()
        assertEquals(callable.call(), result)
    }

    @Test
    fun `runnable invoke works`() {
        var result = 0
        val runnable = Runnable { result = 1 }
        runnable()
        assertEquals(1, result)
    }

    @Test
    fun `consumer accept works`() {
        var result = 0
        val consumer = Consumer<Int> { result = it }
        consumer(1)
        assertEquals(1, result)
    }

    @Test
    fun `int consumer accept works`() {
        var result = 0
        val consumer = IntConsumer { result = it }
        consumer(1)
        assertEquals(1, result)
    }

    @Test
    fun `long consumer accept works`() {
        var result = 0L
        val consumer = LongConsumer { result = it }
        consumer(1L)
        assertEquals(1L, result)
    }

    @Test
    fun `double consumer accept works`() {
        var result = 0.0
        val consumer = DoubleConsumer { result = it }
        consumer(1.0)
        assertEquals(1.0, result)
    }

    @Test
    fun `bi consumer accept works`() {
        var result = 0
        val consumer = BiConsumer<Int, Int> { t, u -> result = t + u }
        consumer(1, 2)
        assertEquals(3, result)
    }

    @Test
    fun `obj int consumer accept works`() {
        var result = 0
        val consumer = ObjIntConsumer<String> { t, u -> result = t.length + u }
        consumer("aaa", 2)
        assertEquals(5, result)
    }

    @Test
    fun `obj long consumer accept works`() {
        var result = 0L
        val consumer = ObjLongConsumer<String> { t, u -> result = t.length + u }
        consumer("aaa", 2L)
        assertEquals(5L, result)
    }

    @Test
    fun `obj double consumer accept works`() {
        var result = 0.0
        val consumer = ObjDoubleConsumer<String> { t, u -> result = t.length.toDouble() + u }
        consumer("aaa", .5)
        assertEquals(3.5, result)
    }

    @Test
    fun `function apply works`() {
        val function = Function<Int, Int> { it + 1 }
        val result = function(1)
        assertEquals(2, result)
    }

    @Test
    fun `int function apply works`() {
        val function = IntFunction { it + 1 }
        val result = function(1)
        assertEquals(2, result)
    }

    @Test
    fun `long function apply works`() {
        val function = LongFunction { it + 1 }
        val result = function(1L)
        assertEquals(2L, result)
    }

    @Test
    fun `double function apply works`() {
        val function = DoubleFunction { it + 1 }
        val result = function(1.0)
        assertEquals(2.0, result)
    }

    @Test
    fun `int to long function apply works`() {
        val function = IntToLongFunction { it + 1L }
        val result = function(1)
        assertEquals(2L, result)
    }

    @Test
    fun `int to double function apply works`() {
        val function = IntToDoubleFunction { it + 1.0 }
        val result = function(1)
        assertEquals(2.0, result)
    }

    @Test
    fun `long to int function apply works`() {
        val function = LongToIntFunction { it.toInt() + 1 }
        val result = function(1L)
        assertEquals(2, result)
    }

    @Test
    fun `long to double function apply works`() {
        val function = LongToDoubleFunction { it + 1.0 }
        val result = function(1L)
        assertEquals(2.0, result)
    }

    @Test
    fun `double to int function apply works`() {
        val function = DoubleToIntFunction { it.toInt() + 1 }
        val result = function(1.0)
        assertEquals(2, result)
    }

    @Test
    fun `double to long function apply works`() {
        val function = DoubleToLongFunction { it.toLong() + 1L }
        val result = function(1.0)
        assertEquals(2L, result)
    }

    @Test
    fun `int unary operator apply works`() {
        val operator = IntUnaryOperator { it + 1 }
        val result = operator(1)
        assertEquals(2, result)
    }

    @Test
    fun `long unary operator apply works`() {
        val operator = LongUnaryOperator { it + 1 }
        val result = operator(1L)
        assertEquals(2L, result)
    }

    @Test
    fun `double unary operator apply works`() {
        val operator = DoubleUnaryOperator { it + 1 }
        val result = operator(1.0)
        assertEquals(2.0, result)
    }

    @Test
    fun `int binary operator apply works`() {
        val operator = IntBinaryOperator { t, u -> t + u }
        val result = operator(1, 2)
        assertEquals(3, result)
    }

    @Test
    fun `long binary operator apply works`() {
        val operator = LongBinaryOperator { t, u -> t + u }
        val result = operator(1L, 2L)
        assertEquals(3L, result)
    }

    @Test
    fun `double binary operator apply works`() {
        val operator = DoubleBinaryOperator { t, u -> t + u }
        val result = operator(1.0, 2.0)
        assertEquals(3.0, result)
    }

    @Test
    fun `predicate test works`() {
        val predicate = Predicate<Int> { it == 1 }
        val result = predicate(1)
        assertTrue(result)
    }

    @Test
    fun `int predicate test works`() {
        val predicate = IntPredicate { it == 1 }
        val result = predicate(1)
        assertTrue(result)
    }

    @Test
    fun `long predicate test works`() {
        val predicate = LongPredicate { it == 1L }
        val result = predicate(1L)
        assertTrue(result)
    }

    @Test
    fun `double predicate test works`() {
        val predicate = DoublePredicate { it == 1.0 }
        val result = predicate(1.0)
        assertTrue(result)
    }

    @Test
    fun `bi predicate test works`() {
        val predicate = BiPredicate<Int, Int> { t, u -> t + u == 3 }
        val result = predicate(1, 2)
        assertTrue(result)
    }

    @Test
    fun `supplier get works`() {
        val supplier = Supplier { 1 }
        val result = supplier()
        assertEquals(1, result)
    }

    @Test
    fun `int supplier get works`() {
        val supplier = IntSupplier { 1 }
        val result = supplier()
        assertEquals(1, result)
    }

    @Test
    fun `long supplier get works`() {
        val supplier = LongSupplier { 1L }
        val result = supplier()
        assertEquals(1L, result)
    }

    @Test
    fun `double supplier get works`() {
        val supplier = DoubleSupplier { 1.0 }
        val result = supplier()
        assertEquals(1.0, result)
    }

    @Test
    fun `bi function apply works`() {
        val function = BiFunction<Int, Int, Int> { t, u -> t + u }
        val result = function(1, 2)
        assertEquals(3, result)
    }

    @Test
    fun `to int bi function apply works`() {
        val function = ToIntBiFunction<Int, Int> { t, u -> t + u }
        val result = function(1, 2)
        assertEquals(3, result)
    }

    @Test
    fun `to long bi function apply works`() {
        val function = ToLongBiFunction<Int, Int> { t, u -> t.toLong() + u.toLong() }
        val result = function(1, 2)
        assertEquals(3L, result)
    }

    @Test
    fun `to double bi function apply works`() {
        val function = ToDoubleBiFunction<Int, Int> { t, u -> t.toDouble() + u.toDouble() }
        val result = function(1, 2)
        assertEquals(3.0, result)
    }

    @Test
    fun `to int function apply works`() {
        val function = ToIntFunction<Int> { it }
        val result = function(1)
        assertEquals(1, result)
    }

    @Test
    fun `to long function apply works`() {
        val function = ToLongFunction<Int> { it.toLong() }
        val result = function(1)
        assertEquals(1L, result)
    }

    @Test
    fun `to double function apply works`() {
        val function = ToDoubleFunction<Int> { it.toDouble() }
        val result = function(1)
        assertEquals(1.0, result)
    }

}
