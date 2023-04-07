package com.kamelia.sprinkler.collection.readonly;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class JavaSetTest {

    @Test
    public void readOnlySetDoesNotAllowMutationMethods() {
        var set = new HashSet<>(Set.of(1, 2, 3));
        var readOnlySet = ReadOnlyUtils.toReadOnlySet(set);

        assertThrows(UnsupportedOperationException.class, () -> readOnlySet.add(4));
        assertThrows(UnsupportedOperationException.class, () -> readOnlySet.remove(0));
        assertThrows(UnsupportedOperationException.class, () -> readOnlySet.addAll(Set.of(4, 5, 6)));
        assertThrows(UnsupportedOperationException.class, () -> readOnlySet.removeAll(Set.of(1, 2, 3)));
        assertThrows(UnsupportedOperationException.class, () -> readOnlySet.retainAll(Set.of(1, 2, 3)));
        assertThrows(UnsupportedOperationException.class, readOnlySet::clear);
    }

    @Test
    public void readOnlyIteratorDoesNotAllowMutationMethods() {
        var set = new HashSet<>(Set.of(1, 2, 3));
        var readOnlySet = ReadOnlyUtils.toReadOnlySet(set);
        var iterator = readOnlySet.iterator();

        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

}
