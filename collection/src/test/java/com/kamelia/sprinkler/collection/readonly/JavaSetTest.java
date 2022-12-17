package com.kamelia.sprinkler.collection.readonly;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class JavaSetTest {

    @Test
    public void readOnlySetDoesNotAllowMutationMethods() {
        var set = new java.util.HashSet<>(java.util.List.of(1, 2, 3));
        var readOnlySet = ReadOnlyUtils.toReadOnlySet(set);

        assertThrows(UnsupportedOperationException.class, () -> readOnlySet.add(4));
    }

    @Test
    public void readOnlyIteratorDoesNotAllowMutationMethods() {
        var set = new java.util.HashSet<>(java.util.List.of(1, 2, 3));
        var readOnlySet = ReadOnlyUtils.toReadOnlySet(set);
        var iterator = readOnlySet.iterator();

        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

}
