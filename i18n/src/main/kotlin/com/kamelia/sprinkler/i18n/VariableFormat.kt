package com.kamelia.sprinkler.i18n

import java.text.Format

interface VariableFormat : VariableFormatProvider {

    fun format(value: Any): String

    override fun getFormat(vararg args: Any?): VariableFormat = this

    companion object {

        fun fromFormat(format: Format): VariableFormat = object : VariableFormat {
            override fun format(value: Any): String = format.format(value)
        }

    }

}

interface VariableFormatProvider {

    fun getFormat(vararg args: Any?): VariableFormat

}
