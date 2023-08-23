package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.util.castOrNull
import com.kamelia.sprinkler.util.illegalArgument

sealed interface TranslationNode {

    class Inner(internal val children: Map<String, TranslationNode>) : TranslationNode {

        fun getNode(key: String): TranslationNode = getTranslationNode(key, this)
            ?: illegalArgument("No node found with key $key")

        fun section(key: String): Inner = getNode(key).castOrNull<Inner>()
            ?: illegalArgument("No section found with key $key")

        fun string(key: String): String = getNode(key).castOrNull<Leaf>()?.value
            ?: illegalArgument("No string found with key $key")

    }

    class Leaf(val value: String) : TranslationNode

}

interface TranslationTree {

    operator fun get(key: String): TranslationNode?

}

private class TranslationTreeImpl(private val root: TranslationNode.Inner) : TranslationTree {

    override fun get(key: String): TranslationNode? = getTranslationNode(key, root)

}


private fun getTranslationNode(key: String, root: TranslationNode.Inner): TranslationNode? {
    val fastResult = root.children[key]
    if (fastResult != null) return fastResult

    val keyParts = key.split('.')
    var current: TranslationNode? = root
    for (keyPart in keyParts) {
        current = current.castOrNull<TranslationNode.Inner>()?.children?.get(keyPart) ?: return null
    }
    return current
}