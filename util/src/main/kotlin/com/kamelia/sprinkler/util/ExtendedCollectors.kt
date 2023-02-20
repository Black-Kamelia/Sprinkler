package com.kamelia.sprinkler.util

import java.util.stream.Collector
import java.util.stream.Collectors

/**
 * Class containing additional [collectors][Collectors] for [streams][java.util.stream.Stream].
 *
 * @see Collector
 * @see Collectors
 */
object ExtendedCollectors {

    /**
     * Returns a [collector][Collector] that collects elements to a [map][Map] from [pairs][Pair] of keys and values.
     *
     * @param K the type of keys
     * @param V the type of values
     * @return a [collector][Collector] that creates a [map][Map] from [pairs][Pair] of keys and values
     */
    @JvmStatic
    fun <K, V> toMap(): Collector<Pair<K, V>, *, Map<K, V>> = Collectors.toMap(Pair<K, V>::first, Pair<K, V>::second)

    /**
     * Returns a [collector][Collector] that collects elements to an [array][Array].
     *
     * @param T the type of elements
     * @param factory a function that creates an array of the desired type and the given size
     * @return a [collector][Collector] that creates an [array][Array] from elements
     */
    @JvmStatic
    fun <T> toArray(factory: (Int) -> Array<T?>): Collector<T, *, Array<T>> = Collector.of(
        { mutableListOf<T>() },
        { list, t -> list.add(t) },
        { list1, list2 -> list1.apply { addAll(list2) } },
        { list ->
            val array = @Suppress("UNCHECKED_CAST") (factory(list.size) as Array<T>)
            list.forEachIndexed { index, t -> array[index] = t }
            array
        }
    )


    /**
     * Returns a [collector][Collector] that collects elements to an [int array][IntArray].
     *
     * @return a [collector][Collector] that creates an [int array][IntArray] from elements
     */
    @JvmStatic
    fun toIntArray(): Collector<Int, *, IntArray> = Collector.of(
        { mutableListOf<Int>() },
        { list, t -> list.add(t) },
        { list1, list2 -> list1.apply { addAll(list2) } },
        { list ->
            val array = IntArray(list.size)
            list.forEachIndexed { index, t -> array[index] = t }
            array
        }
    )

    /**
     * Returns a [collector][Collector] that collects elements to a [long array][LongArray].
     *
     * @return a [collector][Collector] that creates a [long array][LongArray] from elements
     */
    @JvmStatic
    fun toLongArray(): Collector<Long, *, LongArray> = Collector.of(
        { mutableListOf<Long>() },
        { list, t -> list.add(t) },
        { list1, list2 -> list1.apply { addAll(list2) } },
        { list ->
            val array = LongArray(list.size)
            list.forEachIndexed { index, t -> array[index] = t }
            array
        }
    )

    /**
     * Returns a [collector][Collector] that collects elements to a [double array][DoubleArray].
     *
     * @return a [collector][Collector] that creates a [double array][DoubleArray] from elements
     */
    @JvmStatic
    fun toDoubleArray(): Collector<Double, *, DoubleArray> = Collector.of(
        { mutableListOf<Double>() },
        { list, t -> list.add(t) },
        { list1, list2 -> list1.apply { addAll(list2) } },
        { list ->
            val array = DoubleArray(list.size)
            list.forEachIndexed { index, t -> array[index] = t }
            array
        }
    )

    /**
     * Returns a [collector][Collector] that collects elements to a [float array][FloatArray].
     *
     * @return a [collector][Collector] that creates a [float array][FloatArray] from elements
     */
    @JvmStatic
    fun toFloatArray(): Collector<Float, *, FloatArray> = Collector.of(
        { mutableListOf<Float>() },
        { list, t -> list.add(t) },
        { list1, list2 -> list1.apply { addAll(list2) } },
        { list ->
            val array = FloatArray(list.size)
            list.forEachIndexed { index, t -> array[index] = t }
            array
        }
    )

    /**
     * Returns a [collector][Collector] that collects elements to a [short array][ShortArray].
     *
     * @return a [collector][Collector] that creates a [short array][ShortArray] from elements
     */
    @JvmStatic
    fun toShortArray(): Collector<Short, *, ShortArray> = Collector.of(
        { mutableListOf<Short>() },
        { list, t -> list.add(t) },
        { list1, list2 -> list1.apply { addAll(list2) } },
        { list ->
            val array = ShortArray(list.size)
            list.forEachIndexed { index, t -> array[index] = t }
            array
        }
    )

    /**
     * Returns a [collector][Collector] that collects elements to a [byte array][ByteArray].
     *
     * @return a [collector][Collector] that creates a [byte array][ByteArray] from elements
     */
    @JvmStatic
    fun toByteArray(): Collector<Byte, *, ByteArray> = Collector.of(
        { mutableListOf<Byte>() },
        { list, t -> list.add(t) },
        { list1, list2 -> list1.apply { addAll(list2) } },
        { list ->
            val array = ByteArray(list.size)
            list.forEachIndexed { index, t -> array[index] = t }
            array
        }
    )

}
