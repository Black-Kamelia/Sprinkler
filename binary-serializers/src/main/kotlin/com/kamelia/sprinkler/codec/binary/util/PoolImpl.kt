package com.kamelia.sprinkler.codec.binary.util

internal class PoolImpl {

    private val pool = HashMap<Any, Any>()

    operator fun get(key: PoolKey<*>): Any = internalGet(key)

    fun getNullable(key: PoolKey<*>, toNullableValue: (Any) -> Any ): Any {
        val nullable = key.toNullable()
        val result = pool[nullable]
        if (result != null) {
            return result
        }
        val notNull = get(key)
        val nullableValue = toNullableValue(notNull)
        set(nullable, nullableValue)
        return nullableValue
    }

    fun getNullable(clazz: Class<*>, toNullableValue: (Any) -> Any ): Any =
        getNullable(PoolKey.ofNullable(clazz), toNullableValue)

    operator fun get(key: Class<*>): Any = internalGet(key)

    operator fun set(key: PoolKey<*>, value: Any): Unit = internalSet(key, value)

    operator fun set(key: Class<*>, value: Any): Unit = internalSet(key, value)

    fun setNullable(key: PoolKey<*>, value: Any): Unit = internalSet(key.toNullable(), value)

    fun setNullable(key: Class<*>, value: Any): Unit = internalSet(PoolKey.ofNullable(key), value)

    private fun internalGet(key: Any): Any = pool[unwrap(key)] ?: throw NoSuchElementException("No value for $key")

    private fun internalSet(key: Any, value: Any) {
        pool.compute(unwrap(key)) { _, old ->
            if (old != null) {
                throw IllegalStateException("Value $old, already exists for $key")
            }
            value
        }
    }

    private fun unwrap(key: Any): Any = if (key is PoolKey<*> && key.extra == null) {
        key.clazz
    } else {
        key
    }

}
