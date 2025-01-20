@file:JvmName("Translators")
package com.kamelia.sprinkler.i18n.impl

import com.kamelia.sprinkler.i18n.Translator
import com.zwendo.restrikt2.annotation.HideFromJava
import com.zwendo.restrikt2.annotation.HideFromKotlin
import java.util.function.Consumer
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Creates a new [Translator] using a [TranslatorBuilder] configured with the provided [block]
 *
 * @param block the configuration block
 * @return the created translator
 *
 * @throws IllegalStateException if [ignoreMissingKeyOnBuild][TranslatorBuilder.checkMissingKeysOnBuild] is `false`
 * and at least two locales have different translation keys
 * @throws Exception if any method called in the [block] throws an exception
 */
@HideFromJava
@OptIn(ExperimentalContracts::class)
fun Translator(block: TranslatorBuilder.() -> Unit): Translator {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val caller = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).callerClass
    return TranslatorBuilderImpl(caller).apply(block).build()
}

/**
 * Creates a new [Translator] using a [TranslatorBuilder] configured with the provided [block].
 *
 * @param block the configuration block
 * @return the created translator
 *
 * @throws IllegalStateException if [ignoreMissingKeyOnBuild][TranslatorBuilder.checkMissingKeysOnBuild] is `false`
 * and at least two locales have different translation keys
 * @throws Exception if any method called in the [block] throws an exception
 */
@HideFromKotlin
fun Translator(block: Consumer<TranslatorBuilder>): Translator {
    val caller = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).callerClass
    return TranslatorBuilderImpl(caller).apply { block.accept(this) }.build()
}
