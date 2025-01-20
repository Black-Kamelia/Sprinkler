package com.kamelia.sprinkler.i18n.impl

import com.kamelia.sprinkler.i18n.Translator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TranslatorBuilderResourceTest {

    @Test
    fun `resource adds the resource to the translator`() {
        val t = Translator {
            translations {
                resource("/builder_test/en.yml", Charsets.UTF_8)
            }
        }
        assertEquals("foo", t.t("test"))
    }

    @Test
    fun `resource throws an IAE if the resource file does not exist`() {
        assertThrows<IllegalArgumentException> {
            Translator {
                translations {
                    resource("builder_test/does_not_exist.yml")
                }
            }
        }
    }

    @Test
    fun `resource throws an IAE if the resource directory does not exist`() {
        assertThrows<IllegalArgumentException> {
            Translator {
                translations {
                    resource("builder_test/does_not_exist/", Translator::class.java, Charsets.UTF_8)
                }
            }
        }
    }

    @Test
    fun `resource throws an IAE if the resource directory is a file and the content does not exist as a resource`() {
        assertThrows<IllegalArgumentException> {
            Translator {
                translations {
                    resource("/builder_test/resource_dir.txt", Translator::class.java)
                }
            }
        }
    }

    @Test
    fun `resource throws an IAE if the resourcePath contains a reference to a parent directory`() {
        assertThrows<IllegalArgumentException> {
            Translator {
                translations {
                    resource("../builder_test/en.yml")
                }
            }
        }
    }

    @Test
    fun `trying to add resources after the translator has been built throws an ISE`() {
        lateinit var content: TranslatorBuilder.Content
        Translator {
            translations {
                content = this
            }
        }
        assertThrows<IllegalStateException> {
            content.resource(".")
        }
    }

}
