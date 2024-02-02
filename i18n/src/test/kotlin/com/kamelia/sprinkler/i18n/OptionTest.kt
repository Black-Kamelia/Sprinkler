package com.kamelia.sprinkler.i18n

import org.junit.jupiter.api.Test

class OptionTest {

    @Test
    fun `coverage of const val`() {
        val fields = Options.javaClass.declaredFields
        fields.forEach {
            it.get(Options)
        }
    }

}
