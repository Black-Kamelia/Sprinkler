package com.kamelia.sprinkler.i18n.pluralization

import java.util.Locale
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BuiltinPluralPluralRuleProviderTest {

    @Test
    fun `n == part test`() {
        val mapper = BuiltinPluralRule.parseRuleContent("few:n=7")
        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(7)))
        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(7.0)))
        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(ScientificNotationNumber.from(7))))

        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(8)))
        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(8.0)))
        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(ScientificNotationNumber.from(8))))
    }

    @Test
    fun `n != part test`() {
        val mapper = BuiltinPluralRule.parseRuleContent("few:n!=7")
        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(7)))
        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(7.0)))
        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(ScientificNotationNumber.from(7))))

        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(8)))
        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(8.0)))
        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(ScientificNotationNumber.from(8))))
    }

    @Test
    fun `i == part test`() {
        val mapper = BuiltinPluralRule.parseRuleContent("few:i=7")
        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(7)))
        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(7.68)))
        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(ScientificNotationNumber.from(7.5635724))))

        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(8)))
        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(8.0)))
        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(ScientificNotationNumber.from(8))))
    }

    @Test
    fun `i != part test`() {
        val mapper = BuiltinPluralRule.parseRuleContent("few:i!=7")
        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(7)))
        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(7.0)))
        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(ScientificNotationNumber.from(7))))

        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(8)))
        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(8.0)))
        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(ScientificNotationNumber.from(8))))
    }

    @Test
    fun `f == part test`() {
        val mapper = BuiltinPluralRule.parseRuleContent("few:f=3")
        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(7.3)))
        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(ScientificNotationNumber.from(7.30))))

        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(8)))
        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(9.8)))
        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(ScientificNotationNumber.from(3.6))))
    }

    @Test
    fun `f != part test`() {
        val mapper = BuiltinPluralRule.parseRuleContent("few:f!=3")
        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(7.3)))
        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(ScientificNotationNumber.from(7.30))))

        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(8)))
        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(9.8)))
        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(ScientificNotationNumber.from(3.6))))
    }

    @Test
    fun `v == part test`() {
        val mapper = BuiltinPluralRule.parseRuleContent("few:v=3")
        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(18.463)))
        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(ScientificNotationNumber.from(7.891))))

        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(8)))
        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(1.200)))
        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(ScientificNotationNumber.from(8))))
    }

    @Test
    fun `v != part test`() {
        val mapper = BuiltinPluralRule.parseRuleContent("few:v!=3")
        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(18.463)))
        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(ScientificNotationNumber.from(7.891))))

        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(8)))
        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(1.200)))
        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(ScientificNotationNumber.from(8))))
    }

    @Test
    fun `e == part test`() {
        val mapper = BuiltinPluralRule.parseRuleContent("few:e=3")
        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(ScientificNotationNumber.from(1e3))))

        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(8)))
        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(1.001)))
        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(ScientificNotationNumber.from(8.001))))
        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(ScientificNotationNumber.from(5e2))))
    }

    @Test
    fun `e != part test`() {
        val mapper = BuiltinPluralRule.parseRuleContent("few:e!=3")
        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(ScientificNotationNumber.from(1e3))))

        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(8)))
        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(1.001)))
        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(ScientificNotationNumber.from(8.001))))
        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(ScientificNotationNumber.from(5e2))))
    }

    @Test
    fun `unknown variable throws when parsed`() {
        assertThrows<AssertionError> {
            BuiltinPluralRule.parseRuleContent("few:z=4")
        }
    }

    @Test
    fun `== toString is redefined`() {
        val mapper = BuiltinPluralRule.parseRuleContent("few:n=7")
        assertEquals("FEW: 'n == 7'", mapper.toString())
    }

    @Test
    fun `!= toString is redefined`() {
        val mapper = BuiltinPluralRule.parseRuleContent("many:n!=18")
        assertEquals("MANY: 'n != 18'", mapper.toString())
    }

    @Test
    fun `n range part test`() {
        val mapper = BuiltinPluralRule.parseRuleContent("few:n=7..9")
        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(7)))
        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(8.0)))
        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(ScientificNotationNumber.from(7.12))))
        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(ScientificNotationNumber.from(8))))

        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(2)))
        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(65)))
        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(10.1)))
        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(1.3)))
        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(ScientificNotationNumber.from(4))))
        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(ScientificNotationNumber.from(1210))))
        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(ScientificNotationNumber.from(-10.0))))
        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(ScientificNotationNumber.from(1210.6))))
    }

    @Test
    fun `n range toString is redefined`() {
        val mapper = BuiltinPluralRule.parseRuleContent("few:n=7..9")
        assertEquals("FEW: 'n == 7..9'", mapper.toString())
    }

    @Test
    fun `n enumeration part test`() {
        val mapper = BuiltinPluralRule.parseRuleContent("few:n=7,-1")
        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(7)))
        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(-1)))

        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(6)))
        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(8)))
    }

    @Test
    fun `enumeration toString is redefined`() {
        val mapper = BuiltinPluralRule.parseRuleContent("few:n=7,-1")
        assertEquals("FEW: 'n == 7,-1'", mapper.toString())
    }

    @Test
    fun `or is correctly applied`() {
        val mapper = BuiltinPluralRule.parseRuleContent("few:n=7||n=8")
        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(7)))
        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(8)))

        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(9)))
    }

    @Test
    fun `or toString is redefined`() {
        val mapper = BuiltinPluralRule.parseRuleContent("few:n=7||n=8")
        assertEquals("FEW: 'n == 7 || n == 8'", mapper.toString())
    }

    @Test
    fun `and is correctly applied`() {
        val mapper = BuiltinPluralRule.parseRuleContent("few:i=7&&f=0")
        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(7)))
        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(7.000)))

        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(7.1)))
        assertEquals(Plural.OTHER, mapper(BuiltinPluralRule.InputValue.from(8)))
    }

    @Test
    fun `and toString is redefined`() {
        val mapper = BuiltinPluralRule.parseRuleContent("few:i=7&&f=0")
        assertEquals("FEW: 'i == 7 && f == 0'", mapper.toString())
    }

    @Test
    fun `modulus is correctly applied`() {
        val mapper = BuiltinPluralRule.parseRuleContent("few:n%10=7")
        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(17)))
        assertEquals(Plural.FEW, mapper(BuiltinPluralRule.InputValue.from(ScientificNotationNumber.from(7))))

        assertThrows<UnsupportedOperationException> {
            mapper(BuiltinPluralRule.InputValue.from(8.0))
        }
        assertThrows<UnsupportedOperationException> {
            mapper(BuiltinPluralRule.InputValue.from(ScientificNotationNumber.from(4.0)))
        }
    }

    @Test
    fun `modulus toString is redefined`() {
        val mapper = BuiltinPluralRule.parseRuleContent("few:n%10=7")
        assertEquals("FEW: 'n % 10 == 7'", mapper.toString())
    }

    @Test
    fun `empty string cardinal throws an unsupported exception`() {
        val mapper = BuiltinPluralRule.loadedProvider(Locale.FRENCH, ";")
        assertThrows<UnsupportedOperationException> {
            mapper.cardinal(0L)
        }
    }

    @Test
    fun `empty string ordinal throws an unsupported exception`() {
        val mapper = BuiltinPluralRule.loadedProvider(Locale.FRENCH, ";")
        assertThrows<UnsupportedOperationException> {
            mapper.ordinal(0L)
        }
    }

    @Test
    fun `empty string toString is redefined`() {
        val mapper = BuiltinPluralRule.loadedProvider(Locale.FRENCH, ";")
        assertEquals("(cardinal=[Unsupported Cardinal], ordinal=[Unsupported Ordinal])", mapper.toString())
    }

    @Test
    fun `star always returns other`() {
        val mapper = BuiltinPluralRule.loadedProvider(Locale.ENGLISH, "*;")
        assertEquals(Plural.OTHER, mapper.cardinal(0L))
        assertEquals(Plural.OTHER, mapper.cardinal(1L))
    }

    @Test
    fun `star toString is redefined`() {
        val mapper = BuiltinPluralRule.loadedProvider(Locale.ENGLISH, "*;*")
        assertEquals("(cardinal=[*], ordinal=[*])", mapper.toString())
    }

    @Test
    fun `full mapper test`() {
        val mapper = BuiltinPluralRule.loadedProvider(Locale.ENGLISH, "zero:i=0//two:e=5//few:f=3//many:v=2;one:i=1..7//two:e=1..3//many:f=4")

        assertEquals(Plural.ZERO, mapper.cardinal(0))
        assertEquals(Plural.TWO, mapper.cardinal(ScientificNotationNumber.Companion.create(3.7, 5)))
        assertEquals(Plural.FEW, mapper.cardinal(2.3))
        assertEquals(Plural.MANY, mapper.cardinal(7.32))
        assertEquals(Plural.OTHER, mapper.cardinal(8.213456))

        assertEquals(Plural.ONE, mapper.ordinal(3))
        assertEquals(Plural.TWO, mapper.ordinal(ScientificNotationNumber.Companion.from(9e3)))
        assertEquals(Plural.MANY, mapper.ordinal(8.4))
        assertEquals(Plural.OTHER, mapper.ordinal(19))
    }

    @Test
    fun `builtinMapper factory throws if the locale is not supported`() {
        assertThrows<IllegalArgumentException> {
            PluralRuleProvider.builtins()(Locale.forLanguageTag("aaa"))
        }
    }

}
