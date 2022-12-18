package com.kamelia.sprinkler.collection.readonly;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class JavaIterableTest {

    @Test
    public void readOnlyIteratorDoesNotAllowMutationMethods() {
        var iterable = (Iterable<Integer>) List.of(1, 2, 3);
        var iterator = ReadOnlyUtils.readOnlyIterator(iterable);

        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

}
