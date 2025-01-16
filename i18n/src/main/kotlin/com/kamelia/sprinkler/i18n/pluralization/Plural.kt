package com.kamelia.sprinkler.i18n.pluralization

/**
 * The plural value of the translation. It can be used to disambiguate translations depending on the number of
 * items.
 *
 * @see PluralMapper
 */
enum class Plural {

    /**
     * Plural value usually used in case the count is 0.
     */
    ZERO,

    /**
     * Plural value usually used in case the count is 1.
     */
    ONE,

    /**
     * Plural value usually used in case the count is 2.
     */
    TWO,

    /**
     * Plural value usually used in case the count represents a few items.
     */
    FEW,

    /**
     * Plural value usually used in case the count represents many items.
     */
    MANY,

    /**
     * Plural value used as default when no other value matches.
     */
    OTHER,

    ;

    /**
     * Used to create a translation key with the plural value.
     */
    internal val representation: String
        get() = name.lowercase()

}
