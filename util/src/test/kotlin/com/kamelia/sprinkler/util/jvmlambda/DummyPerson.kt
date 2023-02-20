package com.kamelia.sprinkler.util.jvmlambda

internal class DummyPerson private constructor(builder: Builder) {

    val name: String = builder.name

    val age: Int = builder.age

    companion object {

        fun create(block: Builder.() -> Unit): DummyPerson {
            val builder = Builder()
            builder.block()
            return builder.build()
        }

    }

    class Builder internal constructor() : KotlinDslAdapter {

        var name: String = ""
            private set

        var age: Int = 0
            private set

        fun name(name: String) = apply { this.name = name }

        fun age(age: Int) = apply { this.age = age }

        internal fun build(): DummyPerson {
            return DummyPerson(this)
        }

    }

}
