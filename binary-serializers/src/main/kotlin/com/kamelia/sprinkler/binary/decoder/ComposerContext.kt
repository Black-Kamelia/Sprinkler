package com.kamelia.sprinkler.binary.decoder

interface ComposerContext<C11 : ComposerContext<C11, *>, C> {

    fun <T> addToContext(element: T): C

    companion object {

        fun <C11 : ComposerContext<C11, *>> create(
            factory: (ComposerContext10<C11, *, *, *, *, *, *, *, *, *, *>) -> C11,
        ): ComposerContext0<C11> = ComposerContext0(factory)

        fun create(): ComposerContext0<Nothing> = ComposerContext0 {
            throw IllegalStateException("ComposerContext 11 is not implemented.")
        }

    }

}

abstract class AbstractContext<C11 : ComposerContext<C11, *>> {

    protected val list: MutableList<Any>
    protected val factory: ComposerContext10<C11, *, *, *, *, *, *, *, *, *, *>.() -> C11

    constructor(previous: AbstractContext<C11>) {
        list = previous.list
        factory = previous.factory
    }

    constructor(factory: ComposerContext10<C11, *, *, *, *, *, *, *, *, *, *>.() -> C11) {
        list = ArrayList()
        this.factory = factory
    }

}

class ComposerContext0<C11 : ComposerContext<C11, *>>(
    factory: ComposerContext10<C11, *, *, *, *, *, *, *, *, *, *>.() -> C11,
) : AbstractContext<C11>(factory), ComposerContext<C11, ComposerContext1<C11, *>> {

    override fun <T> addToContext(element: T): ComposerContext1<C11, T> {
        list.add(element as Any)
        return ComposerContext1(this)
    }

}

class ComposerContext1<C11 : ComposerContext<C11, *>, E1>(
    previous: AbstractContext<C11>
) : AbstractContext<C11>(previous), ComposerContext<C11, ComposerContext2<C11, E1, *>> {


    override fun <T> addToContext(element: T): ComposerContext2<C11, E1, T> {
        list.add(element as Any)
        return ComposerContext2(this)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> create(block: (E1) -> T): T = block(list[0] as E1)

}

class ComposerContext2<C11 : ComposerContext<C11, *>, E1, E2>(
    previous: AbstractContext<C11>
) : AbstractContext<C11>(previous), ComposerContext<C11, ComposerContext3<C11, E1, E2, *>> {


    override fun <T> addToContext(element: T): ComposerContext3<C11, E1, E2, T> {
        list.add(element as Any)
        return ComposerContext3(this)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> create(block: (E1, E2) -> T): T = block(list[0] as E1, list[1] as E2)

}

class ComposerContext3<C11 : ComposerContext<C11, *>, E1, E2, E3>(
    previous: AbstractContext<C11>
) : AbstractContext<C11>(previous), ComposerContext<C11, ComposerContext4<C11, E1, E2, E3, *>> {

    override fun <T> addToContext(element: T): ComposerContext4<C11, E1, E2, E3, T> {
        list.add(element as Any)
        return ComposerContext4(this)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> create(block: (E1, E2, E3) -> T): T = block(list[0] as E1, list[1] as E2, list[2] as E3)

}

class ComposerContext4<C11 : ComposerContext<C11, *>, E1, E2, E3, E4>(
    previous: AbstractContext<C11>
) : AbstractContext<C11>(previous), ComposerContext<C11, ComposerContext5<C11, E1, E2, E3, E4, *>> {


    override fun <T> addToContext(element: T): ComposerContext5<C11, E1, E2, E3, E4, T> {
        list.add(element as Any)
        return ComposerContext5(this)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> create(block: (E1, E2, E3, E4) -> T): T = block(list[0] as E1, list[1] as E2, list[2] as E3, list[3] as E4)

}

class ComposerContext5<C11 : ComposerContext<C11, *>, E1, E2, E3, E4, E5>(
    previous: AbstractContext<C11>
) : AbstractContext<C11>(previous), ComposerContext<C11, ComposerContext6<C11, E1, E2, E3, E4, E5, *>> {


    override fun <T> addToContext(element: T): ComposerContext6<C11, E1, E2, E3, E4, E5, T> {
        list.add(element as Any)
        return ComposerContext6(this)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> create(block: (E1, E2, E3, E4, E5) -> T): T =
        block(list[0] as E1, list[1] as E2, list[2] as E3, list[3] as E4, list[4] as E5)

}

class ComposerContext6<C11 : ComposerContext<C11, *>, E1, E2, E3, E4, E5, E6>(
    previous: AbstractContext<C11>
) : AbstractContext<C11>(previous), ComposerContext<C11, ComposerContext7<C11, E1, E2, E3, E4, E5, E6, *>> {


    override fun <T> addToContext(element: T): ComposerContext7<C11, E1, E2, E3, E4, E5, E6, T> {
        list.add(element as Any)
        return ComposerContext7(this)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> create(block: (E1, E2, E3, E4, E5, E6) -> T): T =
        block(list[0] as E1, list[1] as E2, list[2] as E3, list[3] as E4, list[4] as E5, list[5] as E6)

}

class ComposerContext7<C11 : ComposerContext<C11, *>, E1, E2, E3, E4, E5, E6, E7>(
    previous: AbstractContext<C11>
) : AbstractContext<C11>(previous), ComposerContext<C11, ComposerContext8<C11, E1, E2, E3, E4, E5, E6, E7, *>> {


    override fun <T> addToContext(element: T): ComposerContext8<C11, E1, E2, E3, E4, E5, E6, E7, T> {
        list.add(element as Any)
        return ComposerContext8(this)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> create(block: (E1, E2, E3, E4, E5, E6, E7) -> T): T =
        block(list[0] as E1, list[1] as E2, list[2] as E3, list[3] as E4, list[4] as E5, list[5] as E6, list[6] as E7)

}

class ComposerContext8<C11 : ComposerContext<C11, *>, E1, E2, E3, E4, E5, E6, E7, E8>(
    previous: AbstractContext<C11>
) : AbstractContext<C11>(previous), ComposerContext<C11, ComposerContext9<C11, E1, E2, E3, E4, E5, E6, E7, E8, *>> {


    override fun <T> addToContext(element: T): ComposerContext9<C11, E1, E2, E3, E4, E5, E6, E7, E8, T> {
        list.add(element as Any)
        return ComposerContext9(this)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> create(block: (E1, E2, E3, E4, E5, E6, E7, E8) -> T): T = block(
        list[0] as E1,
        list[1] as E2,
        list[2] as E3,
        list[3] as E4,
        list[4] as E5,
        list[5] as E6,
        list[6] as E7,
        list[7] as E8
    )

}

class ComposerContext9<C11 : ComposerContext<C11, *>, E1, E2, E3, E4, E5, E6, E7, E8, E9>(
    previous: AbstractContext<C11>
) : AbstractContext<C11>(previous),
    ComposerContext<C11, ComposerContext10<C11, E1, E2, E3, E4, E5, E6, E7, E8, E9, *>> {


    override fun <T> addToContext(element: T): ComposerContext10<C11, E1, E2, E3, E4, E5, E6, E7, E8, E9, T> {
        list.add(element as Any)
        return ComposerContext10(this)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> create(block: (E1, E2, E3, E4, E5, E6, E7, E8, E9) -> T): T = block(
        list[0] as E1,
        list[1] as E2,
        list[2] as E3,
        list[3] as E4,
        list[4] as E5,
        list[5] as E6,
        list[6] as E7,
        list[7] as E8,
        list[8] as E9
    )

}

class ComposerContext10<C11 : ComposerContext<C11, *>, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10>(
    previous: AbstractContext<C11>
) : AbstractContext<C11>(previous), ComposerContext<C11, C11> {


    override fun <T> addToContext(element: T): C11 {
        list.add(element as Any)
        return factory(this)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> create(block: (E1, E2, E3, E4, E5, E6, E7, E8, E9, E10) -> T): T = block(
        list[0] as E1,
        list[1] as E2,
        list[2] as E3,
        list[3] as E4,
        list[4] as E5,
        list[5] as E6,
        list[6] as E7,
        list[7] as E8,
        list[8] as E9,
        list[9] as E10
    )

}
