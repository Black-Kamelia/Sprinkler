package com.kamelia.sprinkler.collection.readonly;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class JavaCollectionTest {

    @Test
    public void readOnlyCollectionDoesNotAllowMutationMethods() {
        var collection = (Collection<Integer>) new ArrayList<>(List.of(1, 2, 3));
        var readOnlyCollection = ReadOnlyUtils.toReadOnlyCollection(collection);

        assertThrows(UnsupportedOperationException.class, () -> readOnlyCollection.add(4));
    }

    @Test
    public void readOnlyIteratorDoesNotAllowMutationMethods() {
        var collection = (Collection<Integer>) new ArrayList<>(List.of(1, 2, 3));
        var readOnlyCollection = ReadOnlyUtils.toReadOnlyCollection(collection);
        var iterator = readOnlyCollection.iterator();

        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

}
