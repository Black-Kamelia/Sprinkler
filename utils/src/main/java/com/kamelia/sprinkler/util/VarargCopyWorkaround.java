package com.kamelia.sprinkler.util;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

final class VarargCopyWorkaround {

    private VarargCopyWorkaround() {
        throw new AssertionError();
    }

    public static <T> List<T> unmodifiableListOf(T[] elements) {
        Objects.requireNonNull(elements);
        return List.of(elements);
    }

    public static <T> Set<T> unmodifiableSetOf(T[] elements) {
        Objects.requireNonNull(elements);
        return Set.of(elements);
    }

    public static <K, V> Map<K, V> unmodifiableMapOf(Map.Entry<K, V>[] entries) {
        Objects.requireNonNull(entries);
        return Map.ofEntries(entries);
    }

}
