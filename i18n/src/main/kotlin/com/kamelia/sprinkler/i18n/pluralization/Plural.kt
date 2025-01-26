package com.kamelia.sprinkler.i18n.pluralization

/**
 * The plural value of the translation. It can be used to disambiguate translations depending on the number of
 * items.
 *
 * @see PluralRuleProvider
 */
enum class Plural {

    /**
     * The plural value usually used in case the count is 0.
     */
    ZERO,

    /**
     * The plural value usually used in case the count is 1.
     */
    ONE,

    /**
     * The plural value usually used in case the count is 2.
     */
    TWO,

    /**
     * The plural value usually used in case the count represents a few items.
     */
    FEW,

    /**
     * The plural value usually used in case the count represents many items.
     */
    MANY,

    /**
     * The plural value used as default when no other value matches.
     */
    OTHER,

    ;

}
