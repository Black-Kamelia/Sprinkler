package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.util.castOrNull
import com.kamelia.sprinkler.util.illegalArgument

sealed interface TranslationNode {

    class Inner(private val children: Map<String, TranslationNode>) : TranslationNode {

        fun getNode(key: String): TranslationNode {
            val fastResult = children[key]
            if (fastResult != null) return fastResult

            val keyParts = key.split('.')
            var current: TranslationNode = this
            for (keyPart in keyParts) {
                current = current.castOrNull<Inner>()?.children?.get(keyPart)
                    ?: illegalArgument("No node found with key $key")
            }
            return current
        }

        fun section(key: String): Inner = getNode(key).castOrNull<Inner>()
            ?: illegalArgument("No section found with key $key")

        fun string(key: String): String = getNode(key).castOrNull<Leaf>()?.value
            ?: illegalArgument("No string found with key $key")

    }

    class Leaf(val value: String) : TranslationNode

}
