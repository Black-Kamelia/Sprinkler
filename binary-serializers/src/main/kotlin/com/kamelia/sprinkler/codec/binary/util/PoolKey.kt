package com.kamelia.sprinkler.codec.binary.util

import java.util.*

class PoolKey<T> private constructor(val clazz: Class<T>, val extra: Any?) {

    val isNullable: Boolean
        get() = extra is NullableValue

    @Suppress("UNCHECKED_CAST")
    fun toNullable(): PoolKey<T?> = if (isNullable) {
        this as PoolKey<T?>
    } else {
        PoolKey(clazz, NullableValue(extra)) as PoolKey<T?>
    }

    fun toNotNull(): PoolKey<T> = if (isNullable) {
        PoolKey(clazz, (extra as NullableValue).extra)
    } else {
        this
    }

    class NullableValue @JvmOverloads constructor(val extra: Any? = null) {

        override fun equals(other: Any?): Boolean = other is NullableValue && other.extra == extra

        override fun hashCode(): Int = Objects.hash(extra)

        override fun toString(): String = "NullableValue(extra=$extra)"

    }

    companion object {

        @JvmStatic
        fun <T> of(clazz: Class<T>): PoolKey<T> = PoolKey(clazz, null)

        @JvmStatic
        fun <T> of(clazz: Class<T>, extra: Any): PoolKey<T> = PoolKey(clazz, extra)

        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun <T> ofNullable(clazz: Class<T>): PoolKey<T?> = PoolKey(clazz, NullableValue(null)) as PoolKey<T?>

        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun <T> ofNullable(clazz: Class<T>, extra: Any): PoolKey<T?> =
            PoolKey(clazz, NullableValue(extra)) as PoolKey<T?>

        inline fun <reified T> of(): PoolKey<T> = of(T::class.java)

        inline fun <reified T> of(extra: Any): PoolKey<T> = of(T::class.java, extra)

        inline fun <reified T> ofNullable(): PoolKey<T?> = ofNullable(T::class.java)

        inline fun <reified T> ofNullable(extra: Any): PoolKey<T?> = ofNullable(T::class.java, extra)

    }

    override fun equals(other: Any?): Boolean = other is PoolKey<*> && other.clazz == clazz && other.extra == extra

    override fun hashCode(): Int = Objects.hash(clazz, extra)

    override fun toString(): String = "PoolKey(class=${clazz.canonicalName}, extra=$extra)"

}
