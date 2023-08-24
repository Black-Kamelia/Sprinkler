package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.util.castOrNull
import com.kamelia.sprinkler.util.illegalArgument

internal interface TranslationNode {

    fun section(key: String): TranslationNode?

    fun string(key: String): String

}


private class TranslationNodeImpl(private val children: Map<String, Any>) : TranslationNode {

    fun getNode(key: String): Any {
        val fastResult = children[key]
        if (fastResult != null) return fastResult

        val keyParts = key.split('.')
        var current: Any = this
        for (keyPart in keyParts) {
            current = current.castOrNull<TranslationNodeImpl>()?.children?.get(keyPart)
                ?: illegalArgument("No node found with key $key")
        }
        return current
    }

    override fun section(key: String): TranslationNode = getNode(key).castOrNull<TranslationNode>()
        ?: illegalArgument("No section found with key $key")

    override fun string(key: String): String = getNode(key).castOrNull<String>()
        ?: illegalArgument("No string found with key $key")

}