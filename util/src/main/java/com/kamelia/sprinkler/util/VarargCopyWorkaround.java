package com.kamelia.sprinkler.util;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Internal utility class to unnecessary array copy when calling vararg methods from kotlin.
 */
final class VarargCopyWorkaround {

    private VarargCopyWorkaround() {
        throw new AssertionError();
    }

    /**
     * Calls {@link List#of(Object[])} with the given elements.
     *
     * @param elements the elements of the list
     * @return an unmodifiable list containing the given elements
     * @param <T> the type of the elements
     */
    public static <T> @NotNull List<T> unmodifiableListOf(@NotNull T[] elements) {
        Objects.requireNonNull(elements);
        return List.of(elements);
    }

    /**
     * Calls {@link Set#of(Object[])} with the given elements.
     *
     * @param elements the elements of the set
     * @return an unmodifiable set containing the given elements
     * @param <T> the type of the elements
     */
    public static <T> @NotNull Set<T> unmodifiableSetOf(@NotNull T[] elements) {
        Objects.requireNonNull(elements);
        return Set.of(elements);
    }

    /**
     * Calls {@link Map#ofEntries(Map.Entry[])} with the given entries.
     *
     * @param entries the entries of the map
     * @return an unmodifiable map containing the given entries
     * @param <K> the type of the keys
     * @param <V> the type of the values
     */
    public static <K, V> @NotNull Map<K, V> unmodifiableMapOf(@NotNull Map.Entry<K, V>[] entries) {
        Objects.requireNonNull(entries);
        return Map.ofEntries(entries);
    }

    public static void main(String[] args) {

    }

}
